package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.settings.Settings;

public class ArgumentSilent extends AbstractArgument<Boolean> {
    public ArgumentSilent() {
        super("s", "silent", false,
                "Output only proxy list. Useful for example when you want `echo command >> file`");
    }

    @Override
    public void process() {
        Settings.silent = value;
    }
}
