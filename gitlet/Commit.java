package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     * <p>
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
    }

    public Commit(String message, Commit parent, Tree fileMapping) {
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        this.fileMapping = fileMapping;
    }

    public String hash() {
        return Utils.sha1(
                TYPE,
                this.fileMapping==null?"":this.fileMapping.toString(),
                this.parent==null?"":this.parent.hash(),
                this.secondParent==null?"":this.secondParent.hash(),
                this.date.toString(),
                this.message
        );
    }

    public static Commit read(String hash) {
        return Utils.readObject(Repository.getObjFile(hash), Commit.class);
    }

    public void write() {
        File f=Repository.getObjFile(this.hash());
        f.getParentFile().mkdirs();
        Utils.writeObject(Repository.getObjFile(this.hash()), this);
    }

    @Override
    public void dump() {

    }

    private class Tree {
        private Map<String, String> tree;

        public Tree() {
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
