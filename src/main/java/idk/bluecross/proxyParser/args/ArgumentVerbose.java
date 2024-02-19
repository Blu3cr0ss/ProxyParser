package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.settings.Settings;

public class ArgumentVerbose extends AbstractArgument<Boolean> {
    public ArgumentVerbose() {
        super("v", "verbose", false,
                "Output many things");
    }

    @Override
    public void process() {
        Settings.verbose = value;
    }
}
