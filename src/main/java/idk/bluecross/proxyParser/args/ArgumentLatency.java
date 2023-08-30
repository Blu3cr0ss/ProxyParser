package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.Settings;

public class ArgumentLatency extends AbstractArgument<Integer> {
    public ArgumentLatency() {
        super("l", "latency", 100,
                "Max latency to proxy");
    }

    @Override
    public void process() {
        Settings.latency = value;
    }
}
