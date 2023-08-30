package idk.bluecross.proxyParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        new SettingsManager().process(Arrays.stream(args).collect(Collectors.toList()));
        new Parser().start();
    }
}