package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File INDEX_DIR = join(GITLET_DIR, "index");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "refs", "heads");

    public void init() {
        if (!GITLET_DIR.mkdir()) {
            exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }
        String initialBranchName = "master";
        String initialCommitMsg = "initial commit";

        Commit initialCommit = new Commit(0, initialCommitMsg);
        initialCommit.writeToLocal();

        Branch newBranch = new Branch(initialBranchName, initialCommit.id());
        newBranch.createLocally();

        Head.create();
        Head.changeRefToBranch(initialBranchName);
    }

    public void add(String filenameAdd) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        File fileAdd = join(CWD, filenameAdd);
        if (!fileAdd.exists()) {
            exitWithMessage("File does not exist.");
        }

        Blob blob = new Blob(fileAdd);

        AddStage addStage = AddStage.readFromLocal();
        if (!blob.id().equals(Head.loadRefCommit().getTree().get(filenameAdd))) {
            blob.writeToRepo();
            addStage.put(filenameAdd, blob.id());
        } else if (AddStage.readFromLocal().containsKey(filenameAdd)) {
            addStage.remove(filenameAdd);
        }
        addStage.writeToLocal();
    }

    public void commit(String commitMsg) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();
        if (addStage.isEmpty() && rmStage.isEmpty()) {
            exitWithMessage("No changes added to the commit.");
        }

        Commit HEADRefCommit = Head.loadRefCommit();
        Commit newCommit = new Commit(commitMsg, HEADRefCommit, addStage, rmStage);
        newCommit.writeToLocal();

        addStage.clear();
        rmStage.clear();
        addStage.writeToLocal();
        rmStage.writeToLocal();

        Head.loadRefBranch().updateRefCommitId(newCommit.id());
    }

    public void rm(String rmFilename) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();
        File rmFile = join(CWD, rmFilename);
        Commit HEAD = Head.loadRefCommit();

        if (addStage != null && addStage.containsKey(rmFilename)) {
            addStage.remove(rmFilename);
            addStage.writeToLocal();
        } else if (rmFile.exists() && HEAD.getTree().containsKey(rmFilename)) {
            rmStage.put(rmFilename, HEAD.getTree().get(rmFilename));
            Utils.restrictedDelete(rmFile);
            rmStage.writeToLocal();
        } else {
            exitWithMessage("No reason to remove the file.");
        }
    }

    public void log() {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        Commit c = Head.loadRefCommit();
        while (c != null) {
            c.log();
            c = Commit.loadFromLocalById(c.getParent());
        }
    }

    public void globalLog() {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        String[] dirs = OBJECTS_DIR.list();
        if (dirs == null) return;
        for (String dir : dirs) {
            String[] objs = join(OBJECTS_DIR, dir).list();
            for (String obj : objs) {
                File objFile = join(OBJECTS_DIR, dir, obj);
                Commit commit = Utils.tryReadObject(objFile, Commit.class);
                if (commit != null) commit.log();
            }
        }
    }

    public void find(String msg) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        String[] dirs = OBJECTS_DIR.list();
        if (dirs == null) return;
        for (String dir : dirs) {
            String[] objs = join(OBJECTS_DIR, dir).list();
            for (String obj : objs) {
                File objFile = join(OBJECTS_DIR, dir, obj);
                Commit commit = Utils.tryReadObject(objFile, Commit.class);
                if (commit != null && commit.getMsg().equals(msg)) System.out.println(commit.id());
            }
        }
    }

    public void status() {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        printBranches();
        printStaged();
        printModifications();
        printUntracked();
    }

    public void checkout(String[] args) {
        int bar = Arrays.asList(args).indexOf("--");
        if (bar == -1) { // checkout [branch name]
            String branchName = args[1];
            if (!GITLET_DIR.exists()) {
                exitWithMessage("Not in an initialized Gitlet directory.");
            }
            if (!Branch.getFile(branchName).exists())
                exitWithMessage("No such branch exists.");

            Branch b = Branch.loadFromLocalByName(branchName);

            if (Head.loadRefBranch().equals(b))
                exitWithMessage("No need to checkout the current branch.");

            List<String> allFiles = Utils.plainFilenamesIn(CWD);
            if (allFiles != null) {
                for (String file : allFiles) {
                    AddStage addStage = AddStage.readFromLocal();
                    if (!Head.loadRefCommit().getTree().containsKey(file)&& !addStage.containsKey(file) && b.getRefCommit().getTree().containsKey(file))
                        exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }

            for (Map.Entry<String, String> blob : Head.loadRefCommit().getTree().entrySet()) {
                String filename = blob.getKey();
                Utils.join(CWD, filename).delete();
            }

            for (Map.Entry<String, String> blob : b.getRefCommit().getTree().entrySet()) {
                Blob.loadFromLocalById(blob.getValue()).overwriteWorkingDir();
            }

            clearStage();
            Head.changeRefToBranch(b.name);
        } else {
            String filename = args[bar + 1];
            Commit checkoutCommit;

            if (args.length == 4) {
                if (!getObjFile(args[1]).exists()) exitWithMessage("No commit with that id exists.");
                checkoutCommit = Commit.loadFromLocalById(args[1]);
            } else {
                checkoutCommit = Head.loadRefCommit();
            }
            String checkoutBlobId = checkoutCommit.getTree().get(filename);
            if (checkoutBlobId == null) exitWithMessage("File does not exist in that commit.");
            Blob.loadFromLocalById(checkoutBlobId).overwriteWorkingDir();
        }
    }

    public void branch(String branchName) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        Commit HEAD = Head.loadRefCommit();

        Branch newBranch = new Branch(branchName, HEAD.id());
        if (newBranch.exist()) exitWithMessage("A branch with that name already exists.");
        newBranch.createLocally();
    }

    public void rmBranch(String branchName) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        if (!Branch.getFile(branchName).exists())
            exitWithMessage("A branch with that name does not exist.");

        Branch branchRm = Branch.loadFromLocalByName(branchName);

        if (branchRm.name.equals(Head.getRefBranchName()))
            exitWithMessage("Cannot remove the current branch.");

        branchRm.deleteLocally();
    }

    private void printBranches() {
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (join(BRANCHES_DIR, branch).equals(join(GITLET_DIR, readContentsAsString(Head.HEAD_FILE))))
                System.out.printf("*%s\n", branch);
            else System.out.printf("%s\n", branch);
        }
        System.out.println();
    }

    private void printStaged() {
        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();

        System.out.println("=== Staged Files ===");
        addStage.printStagedFiles();
        System.out.println();

        System.out.println("=== Removed Files ===");
        rmStage.printStagedFiles();
        System.out.println();
    }

    private void printModifications() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> allFiles = Utils.plainFilenamesIn(CWD);
        if (allFiles != null) {
            for (String file : allFiles) {
                String fileId = new Blob(join(CWD, file)).id();
                if (Head.loadRefCommit().getTree().containsKey(file) && !getObjFile(fileId).exists())
                    System.out.println(file);
            }
        }

        System.out.println();
    }

    private void printUntracked() {
        System.out.println("=== Untracked Files ===");

        List<String> allFiles = Utils.plainFilenamesIn(CWD);
        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();
        if (allFiles != null) {
            for (String file : allFiles) {
                if ((!Head.loadRefCommit().getTree().containsKey(file) && !addStage.containsKey(file)) || rmStage.containsKey(file))
                    System.out.println(file);
            }
        }
        System.out.println();
    }

    static File getObjFile(String hash) {
        return join(OBJECTS_DIR, hash.substring(0, 2), hash.substring(2));
    }

    static void clearStage() {
        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();
        addStage.clear();
        rmStage.clear();
        addStage.writeToLocal();
        rmStage.writeToLocal();

    }
//
//    static void branchRefTo(String branch, Commit commit) {
//        writeContents(Branch.getFile(branch), commit.id());
//    }
//

}
