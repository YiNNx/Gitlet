package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author yinn
 */
public class Commit implements Serializable, Dumpable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private static final String TYPE = "commit";

    private String id;

    private Tree fileMapping;
    private Commit parent;
    private Commit secondParent;

    private Date date;
    /**
     * The message of this Commit.
     */
    private String message;

    public Commit(long time, String message) {
        this.date = new Date(time);
        this.message = message;
        this.fileMapping = new Tree();
    }

    public Commit(String message, Commit parent, Staging additon, Staging removal) {
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        this.fileMapping = addStaged(parent.fileMapping, additon, removal);
    }

    private static Tree addStaged(Tree parent, Staging additon, Staging removal) {
        if (additon != null) {
            for (Map.Entry<String, String> entry : additon.getEntries()) {
                parent.tree.put(entry.getKey(), entry.getValue());
            }
        }
        // TODO: removal
        return parent;
    }

    public String hash() {
        return Utils.sha1(
                TYPE,
                this.fileMapping == null ? "" : this.fileMapping.toString(),
                this.parent == null ? "" : this.parent.hash(),
                this.secondParent == null ? "" : this.secondParent.hash(),
                this.date.toString(),
                this.message
        );
    }

    public static Commit read(String hash) {
        return Utils.readObject(Repository.getObjFile(hash), Commit.class);
    }

    public void write() {
        File f = Repository.getObjFile(this.hash());
        Utils.writeObject(f, this);
    }

    @Override
    public void dump() {
        System.out.printf("COMMIT %s\nparent: %s\ntime: %s\nmsg: %s\n%s",hash().substring(0,12),this.parent == null ? "" : this.parent.hash().substring(0,12),date.toString(),message,this.fileMapping == null ? "" : this.fileMapping.toString());
    }

    private class Tree implements Serializable {
        private Map<String, String> tree;

        public Tree() {
            tree = new HashMap<>();
        }

        @Override
        public String toString() {
            StringBuilder entries = new StringBuilder();
            for (Map.Entry<String, String> entry : tree.entrySet()) {
                entries.append(String.format("[%s:%s]", entry.getKey(), entry.getValue()));
            }
            return entries.toString();
        }
    }

}
