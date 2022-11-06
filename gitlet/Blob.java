package gitlet;

import java.io.Serializable;

public class Blob implements Serializable,Dumpable {
    private static final String TYPE ="blob";

    private String id;

    private String fileName;
    private String fileContent;

    public String hash(){
        return Utils.sha1(
                TYPE,
                this.fileName,
                this.fileContent
        );
    }

    @Override
    public void dump() {

    }
}
