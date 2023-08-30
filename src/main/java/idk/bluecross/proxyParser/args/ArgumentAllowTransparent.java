package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.Settings;

public class ArgumentAllowTransparent extends AbstractArgument<Boolean> {
    public ArgumentAllowTransparent() {
        super("at", "allow-tp", true,
                "Allow proxy type transparent");
    }

    @Override
    public void process() {
        Settings.allowTransparent = value;
    }
}
