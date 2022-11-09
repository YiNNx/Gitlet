package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Head {
    public static final File HEAD_FILE = join(Repository.GITLET_DIR, "HEAD");

    public static void create() {
        newFile(HEAD_FILE);
    }

    public static String getRefBranchName() {
        return join(Repository.GITLET_DIR, readContentsAsString(HEAD_FILE)).getName();
    }

    public static void changeRefToBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, Repository.GITLET_DIR.toURI().relativize(Branch.getFile(branchName).toURI()).toString());
    }

    public static Commit loadRefCommit() {
        String branchName = join(Repository.GITLET_DIR, readContentsAsString(HEAD_FILE)).getName();
        Branch ref = Branch.loadFromLocalByName(branchName);
        return ref.getRefCommit();
    }

    public static Branch loadRefBranch() {
        String branchName = getRefBranchName();
        return Branch.loadFromLocalByName(branchName);
    }
}
