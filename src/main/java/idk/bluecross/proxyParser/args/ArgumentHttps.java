package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.settings.Settings;

public class ArgumentHttps extends AbstractArgument<Boolean> {
    public ArgumentHttps() {
        super("hs", "https", true,
                "Allow only proxy with https");
    }

    @Override
    public void process() {
        Settings.https = value;
    }
}
