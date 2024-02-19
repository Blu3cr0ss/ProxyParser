package idk.bluecross.proxyParser;

import idk.bluecross.proxyParser.parser.Parser;
import idk.bluecross.proxyParser.settings.SettingsManager;
import idk.bluecross.proxyParser.util.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        new SettingsManager().process(Arrays.stream(args).collect(Collectors.toList()));
        Logger.info(new Parser().process(), true);
    }
}