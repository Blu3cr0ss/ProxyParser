package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.Settings;

public class ArgumentDeepPinging extends AbstractArgument<Boolean> {

    public ArgumentDeepPinging() {
        super("dp", "deep-ping", true,
                "Not only ping proxy itself, but also ping another website using proxy");
    }

    @Override
    public void process() {
        Settings.deepPinging = value;
    }
}
