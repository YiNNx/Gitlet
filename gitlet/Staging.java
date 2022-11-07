package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public class Staging implements Serializable {
    private Map<String, String> mapping;

    public Staging(){
        mapping=new HashMap<>();
    }

    public Set<Map.Entry<String, String>> getEntries(){
        return mapping.entrySet();
    }

    static void addStage(String filename,String blobHash){
        Staging addArea=Repository.STAGE_ADDING.exists()?
                readObject(Repository.STAGE_ADDING,Staging.class):
                new Staging();
        addArea.mapping.put(filename,blobHash);
        writeObject(Repository.STAGE_ADDING,addArea);
    }

    static Staging readAddition(){
        return Repository.STAGE_ADDING.exists()?readObject(Repository.STAGE_ADDING,Staging.class):null;
    }
}
