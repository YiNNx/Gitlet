package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.*;

public class RmStage extends HashMap<String,String> implements StagingArea{
    public static final File STAGE_FILE = join(Repository.INDEX_DIR, "removal");

    static public RmStage readFromLocal(){
        return STAGE_FILE.exists()?readObject(STAGE_FILE, RmStage.class): new RmStage();
    }

    @Override
    public void writeToLocal(){
        writeObject(STAGE_FILE,this);
    }
}

