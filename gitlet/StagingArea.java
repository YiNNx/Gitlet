package gitlet;

import java.io.Serializable;
import java.util.Map;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public interface StagingArea extends Map<String,String>,Serializable,Dumpable {
    void writeToLocal();

    default void printStagedFiles(){
        for(String key:keySet()){
            System.out.println(key);
        }
    }

    default void dump() {
        for (Map.Entry<String, String> entry : entrySet()) {
            System.out.printf("[%s:%s]\n", entry.getKey(), entry.getValue().substring(0,12));
        }
    }
}
