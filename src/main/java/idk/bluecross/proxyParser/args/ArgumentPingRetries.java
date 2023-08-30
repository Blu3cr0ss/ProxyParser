package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.Settings;

public class ArgumentPingRetries extends AbstractArgument<Integer> {

    public ArgumentPingRetries() {
        super("pr", "ping-retr", 1,
                "Resend ping N times for better accuracy of latency");
    }

    @Override
    public void process() {
        Settings.pingRetries = value;
    }
}
