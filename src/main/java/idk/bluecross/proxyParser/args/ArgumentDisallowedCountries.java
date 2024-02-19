package idk.bluecross.proxyParser.args;

import idk.bluecross.proxyParser.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentDisallowedCountries extends AbstractArgument<List<String>> {
    public ArgumentDisallowedCountries() {
        super("dc", "disallowed-c", arrayOf("RU"),
                "Disallowed countries of proxies");
    }

    private static ArrayList arrayOf(Object... els) {
        return (ArrayList) Arrays.stream(els).collect(Collectors.toList());
    }

    @Override
    public void process() {
        Settings.disallowedCountries = (ArrayList<String>) value;
    }
}
