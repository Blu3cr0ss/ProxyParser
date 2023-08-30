package idk.bluecross.proxyParser.args;

public abstract class AbstractArgument<T> {
    public T value;
    public String shortName;
    public String fullName;
    public String helpMessage;

    public AbstractArgument(String shortName, String fullName, T value, String helpMessage) {
        this.shortName=shortName;
        this.fullName=fullName;
        this.value=value;
        this.helpMessage=helpMessage;
    }

    public abstract void process();
}
