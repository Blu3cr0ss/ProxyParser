package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.Settings;

public class ArgumentOutputLatency extends AbstractArgument<Boolean> {
    public ArgumentOutputLatency() {
        super("ol", "out-latency", false,
                "Also output latency of every proxy");
    }

    @Override
    public void process() {
        Settings.outputLatency = value;
    }
}
