package gitlet;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.Map;

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
        File headRefFile=join(GITLET_DIR,readContentsAsString(HEAD_FILE));
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

        File file=join(CWD,filename);
        if(!file.exists()){
            exitWithMessage("File does not exist.");
        }

        Blob blob=new Blob(file);
        blob.write();

        Staging.addStage(filename,blob.hash());
    }

    public void commit(String commitMsg) {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }

        Commit HEAD=loadHEAD();
        Commit newCommit=new Commit(commitMsg,HEAD,Staging.readAddition(),null);
        newCommit.write();

        headBranchRefTo(newCommit);
    }

    static File getObjFile(String hash) {
        return join(OBJECTS_DIR, hash.substring(0, 2), hash.substring(2));
    }

    static void branchRefTo(String branch, Commit commit) {
        writeContents(getBranchFile(branch), commit.hash());
    }

    static void headBranchRefTo(Commit commit) {
        File headRefFile=join(GITLET_DIR,readContentsAsString(HEAD_FILE));
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
