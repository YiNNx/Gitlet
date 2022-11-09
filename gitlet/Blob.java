package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable,Dumpable {
    private static final String TYPE ="blob";

    private String fileName;
    private String fileContent;

    public Blob(File file){
        this.fileName=file.getName();
        this.fileContent=Utils.readContentsAsString(file);
    }

    public String id(){
        return Utils.sha1(
                TYPE,
                this.fileName,
                this.fileContent
        );
    }

    public void write(){
        File f=Repository.getObjFile(this.id());
        Utils.writeObject(f, this);
    }

    @Override
    public void dump() {
        System.out.printf("BLOB %s [ %s ]\n%s", id().substring(0,12),fileName,fileContent);
    }
}
