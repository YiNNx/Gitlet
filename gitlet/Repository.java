package gitlet;

import java.io.File;
import java.io.IOException;
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
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

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
    public static final File BRANCHES_DIR = join(GITLET_DIR, "refs","heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    private Commit HEAD;
    private Commit[] branches;

    private Map<String,String> addStaging;
    private Map<String,String> removeStaging;

    private void loadHEAD() {
        HEAD=Commit.read(readContentsAsString(HEAD_FILE));
    }

    public void load() {
        if (!GITLET_DIR.exists()) {
            return;
        }

        loadHEAD();
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdir();

        newBranch("master");
        writeHEADFile("master");

        Commit initialCommit = new Commit(0, "initial commit");
        initialCommit.write();
        updateBranchRef("master",initialCommit.hash());
    }

    static File getObjFile(String hash){
        return join(OBJECTS_DIR,hash.substring(0,2),hash.substring(2)) ;
    }

    static void updateBranchRef(String branch, String headRef){
        writeContents(getBranchFile(branch),headRef);
    }

    static void writeHEADFile(String branch){
        writeContents(HEAD_FILE,CWD.toURI().relativize(getBranchFile(branch).toURI()).toString());
    }

    static void newBranch(String branchName){
        File fileMasterBr=getBranchFile(branchName);
        try {
            fileMasterBr.getParentFile().mkdirs();
            fileMasterBr.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static File getBranchFile(String branchName){
        return join(BRANCHES_DIR,branchName);
    }
}
