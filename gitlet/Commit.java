package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

    private Tree fileMapping;
    private String parent;
    private String secondParent;

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

    public Commit(String message, Commit parent, StagingArea additon, StagingArea removal) {
        this.message = message;
        this.date = new Date();
        this.parent = parent.id();
        this.fileMapping = addStaged(parent.fileMapping, additon, removal);
    }

    public String getParent() {
        return parent;
    }

    public String getMsg() {
        return message;
    }

    private static Tree addStaged(Tree parent, StagingArea additon, StagingArea removal) {
        Tree res = (Tree) parent.clone();
        res.putAll(additon);
        for (String key : removal.keySet()) {
            res.remove(key);
        }
        return res;
    }

    public void log() {
        String formatted= String.format("%ta %tb %td %tT %tY %tz", date, date,date, date, date,date);
        System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n", this.id(),formatted, message);
    }

    public String id() {
        return Utils.sha1(
                TYPE,
                this.fileMapping == null ? "" : this.fileMapping.toString(),
                this.parent == null ? "" : parent,
                this.secondParent == null ? "" : secondParent,
                this.date.toString(),
                this.message
        );
    }

    public Map<String, String> getTree() {
        return fileMapping;
    }

    public static Commit loadFromLocalById(String id) {
        if (id == null || id.equals("")) {return null;}
        return Utils.readObject(Repository.getObjFile(id), Commit.class);
    }

    public void writeToLocal() {
        File f = Repository.getObjFile(this.id());
        Utils.writeObject(f, this);
    }

    @Override
    public void dump() {
        System.out.printf("COMMIT %s\nparent: %s\ntime: %s\nmsg: %s\n%s", id().substring(0, 12), this.parent == null ? "" : this.parent.substring(0, 12), date.toString(), message, this.fileMapping == null ? "" : this.fileMapping.toString());
    }

    private static class Tree extends HashMap<String, String> implements Serializable {

        @Override
        public String toString() {
            StringBuilder entries = new StringBuilder();
            for (Map.Entry<String, String> entry : entrySet()) {
                entries.append(String.format("[%s:%s]", entry.getKey(), entry.getValue()));
            }
            return entries.toString();
        }
    }

}
