package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author yinn
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithMessage("Please enter a command.");
        }

        String firstArg = args[0];
        Repository repo = new Repository();
        switch (firstArg) {
            case "init":
                validateNumArgs(args, 1);
                repo.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                repo.add(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    Utils.exitWithMessage("Please enter a commit message.");
                }
                repo.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                repo.rm(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                repo.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                repo.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                repo.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                repo.status();
                break;
            case "checkout":
                repo.checkout(args);
                break;
            case "branch":
                validateNumArgs(args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                repo.rmBranch(args[1]);
                break;
            default:
                Utils.exitWithMessage("No command with that name exists.");
        }
    }


    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Utils.exitWithMessage("Invalid number of arguments.");
        }
    }
}
