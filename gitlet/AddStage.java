package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.*;

public class AddStage extends HashMap<String,String> implements StagingArea {
    public static final File STAGE_FILE = join(Repository.INDEX_DIR, "addition");

    static public AddStage readFromLocal(){
        return STAGE_FILE.exists()?readObject(STAGE_FILE, AddStage.class): new AddStage();
    }

    @Override
    public void writeToLocal(){
        writeObject(STAGE_FILE,this);
    }
}
