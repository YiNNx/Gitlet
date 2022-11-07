package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public class Staging extends HashMap<String,String> implements Serializable ,Dumpable{

    public Set<Map.Entry<String, String>> getEntries(){
        return entrySet();
    }

    static void addStage(String filename,String blobHash){
        Staging addArea=Repository.STAGE_ADDING.exists()?
                readObject(Repository.STAGE_ADDING,Staging.class):
                new Staging();
        addArea.put(filename,blobHash);
        writeObject(Repository.STAGE_ADDING,addArea);
    }

    static void removeAddition(String key){
        Staging addArea=readObject(Repository.STAGE_ADDING,Staging.class);
        addArea.remove(key);
        writeObject(Repository.STAGE_ADDING,addArea);
    }

    static Staging readAddition(){
        return Repository.STAGE_ADDING.exists()?readObject(Repository.STAGE_ADDING,Staging.class):null;
    }

    @Override
    public void dump() {
        for (Map.Entry<String, String> entry : entrySet()) {
            System.out.printf("[%s:%s]\n", entry.getKey(), entry.getValue().substring(0,12));
        }
    }
}
