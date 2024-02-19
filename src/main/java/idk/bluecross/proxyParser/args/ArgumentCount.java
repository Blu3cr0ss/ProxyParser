package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.settings.Settings;

public class ArgumentCount extends AbstractArgument<Integer> {
    public ArgumentCount() {
        super("c", "count", 3,
                "Count of proxy to output");
    }

    @Override
    public void process() {
        Settings.count = value;
    }
}
