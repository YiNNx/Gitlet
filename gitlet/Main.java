package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author yinn
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithMessage("Must have at least one argument");
        }

        String firstArg = args[0];
        Repository repo=new Repository();
        switch(firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
