package idk.bluecross.proxyParser.args;

import java.util.function.Function;

public class CustomArgument<T> extends AbstractArgument<T> {
    public interface Lambda {
        public void run();
    }

    private Lambda lambda;

    public CustomArgument(String shortName, String fullName, T value, String helpMessage, Lambda lambda) {
        super(shortName, fullName, value, helpMessage);
        this.lambda = lambda;
    }

    @Override
    public void process() {
        lambda.run();
    }
}
