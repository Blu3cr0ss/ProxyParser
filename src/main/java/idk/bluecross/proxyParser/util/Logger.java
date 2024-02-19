package idk.bluecross.proxyParser.util;

import idk.bluecross.proxyParser.settings.Settings;

public class Logger {

    public static void info(Object str, boolean important) {
        try {
            if (important || !Settings.silent) {
                System.out.println(str);
            }
        } catch (Exception e) {
        }
    }

    public static void info(Object str) {
        info(str, false);
    }

    public static void verbose(Object str) {
        if (!Settings.silent && Settings.verbose) System.out.println(str);
    }
}
