package wiki.justreddy.ga.reddyutils.exceptions;

public class MoreThenOneDatabaseException extends RuntimeException {

    public MoreThenOneDatabaseException(int amount){
        super("There were " + amount + " connections to a database found, when there can only be one!");
    }

}
