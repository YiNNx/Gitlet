package gitlet;

import java.io.File;
import java.util.List;

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
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
    public static final File INDEX_DIR = join(GITLET_DIR, "index");
    public static final File STAGE_ADDING = join(INDEX_DIR, "addition");
    public static final File STAGE_REMOVAL = join(GITLET_DIR, "removal");
    public static final File BRANCHES_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    private static Commit loadHEAD() {
        File headRefFile = join(GITLET_DIR, readContentsAsString(HEAD_FILE));
        return Commit.read(readContentsAsString(headRefFile));
    }

    public void init() {
        if (!GITLET_DIR.mkdir()) {
            exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }

        String defaultBranch = "master";
        String initCommitMsg = "initial commit";

        newBranchFile(defaultBranch);
        headFileRefTo(defaultBranch);

        Commit initialCommit = new Commit(0, initCommitMsg);
        initialCommit.write();

        branchRefTo(defaultBranch, initialCommit);
    }

    public void add(String filename) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        File file = join(CWD, filename);
        if (!file.exists()) {
            exitWithMessage("File does not exist.");
        }

        Blob blob = new Blob(file);

        AddStage addStage = AddStage.readFromLocal();
        if (!blob.hash().equals(loadHEAD().getTree().get(filename))) {
            blob.write();
            addStage.put(filename, blob.hash());
        } else if (AddStage.readFromLocal().containsKey(filename)) {
            addStage.remove(filename);
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

        Commit HEAD = loadHEAD();
        Commit newCommit = new Commit(commitMsg, HEAD, addStage, rmStage);
        newCommit.write();
        STAGE_ADDING.delete();

        addStage.clear();
        addStage.writeToLocal();
        rmStage.clear();
        rmStage.writeToLocal();

        headBranchRefTo(newCommit);
    }

    public void rm(String rmFilename) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        AddStage addStage = AddStage.readFromLocal();
        RmStage rmStage = RmStage.readFromLocal();
        File rmFile = join(CWD, rmFilename);
        Commit HEAD = loadHEAD();

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

        Commit c = loadHEAD();
        while (c != null) {
            c.log();
            c = Commit.read(c.getParent());
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
                if (commit != null && commit.getMsg().equals(msg)) System.out.println(commit.hash());
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

    private void printBranches() {
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (join(BRANCHES_DIR, branch).equals(join(GITLET_DIR, readContentsAsString(HEAD_FILE))))
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

        System.out.println();
    }

    private void printUntracked() {
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }

    static File getObjFile(String hash) {
        return join(OBJECTS_DIR, hash.substring(0, 2), hash.substring(2));
    }

    static void branchRefTo(String branch, Commit commit) {
        writeContents(getBranchFile(branch), commit.hash());
    }

    static void headBranchRefTo(Commit commit) {
        File headRefFile = join(GITLET_DIR, readContentsAsString(HEAD_FILE));
        writeContents(headRefFile, commit.hash());
    }

    // make HEAD file reference to branch's ref
    static void headFileRefTo(String branch) {
        writeContents(HEAD_FILE, GITLET_DIR.toURI().relativize(getBranchFile(branch).toURI()).toString());
    }

    // creates empty refs/heads/<branch> file
    static void newBranchFile(String branch) {
        newFile(getBranchFile(branch));
    }

    static File getBranchFile(String branchName) {
        return join(BRANCHES_DIR, branchName);
    }
}
