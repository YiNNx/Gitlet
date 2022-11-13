package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Branch {
    public static final File BRANCHES_DIR = join(Repository.GITLET_DIR, "refs", "heads");

    String name;
    String refCommitId;
    File branchFile;

    public Branch(String name, String commitId) {
        this.name = name;
        this.refCommitId = commitId;
        this.branchFile = join(BRANCHES_DIR, name);
    }

    public static Branch loadFromLocalByName(String name) {
        String refCommitId = readContentsAsString(getFile(name));
        return new Branch(name, refCommitId);
    }

    public boolean exist() {
        return branchFile.exists();
    }

    public void createLocally() {
//        System.out.println(refCommitId);
        writeContents(branchFile, refCommitId);
    }

    public void deleteLocally() {
        branchFile.delete();
    }

    public void updateRefCommitId(String commitId) {
        refCommitId = commitId;
        writeContents(branchFile, commitId);
    }

    public static File getFile(String branchName) {
        return join(BRANCHES_DIR, branchName);
    }

    public Commit getRefCommit() {
        return Commit.loadFromLocalById(refCommitId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return this.name.equals(((Branch) obj).name) &&
                this.refCommitId.equals(((Branch) obj).refCommitId) &&
                this.branchFile.equals(((Branch) obj).branchFile);
    }
}
