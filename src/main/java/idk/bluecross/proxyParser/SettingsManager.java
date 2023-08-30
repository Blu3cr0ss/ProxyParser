package idk.bluecross.proxyParser;

import idk.bluecross.proxyParser.args.*;

import static idk.bluecross.proxyParser.Settings.*;

import java.util.*;
import java.util.stream.Collectors;

public class SettingsManager {
    List<AbstractArgument> existingArguments = new ArrayList<>();

    private void addArguments() {
        existingArguments.add(new ArgumentSilent());
        existingArguments.add(new ArgumentVerbose());
        existingArguments.add(new ArgumentCount());
        existingArguments.add(new ArgumentLatency());
        existingArguments.add(new ArgumentDisallowedCountries());
        existingArguments.add(new ArgumentPingRetries());
        existingArguments.add(new ArgumentDeepPinging());
        existingArguments.add(new ArgumentAllowTransparent());
        existingArguments.add(new ArgumentOutputLatency());
    }

    private String getHelpMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello! This app is gets proxies from the site `https://free-proxy-list.net/`, gets rid of non-working proxies and sorts working proxies by speed");
        sb.append(System.lineSeparator());
        sb.append("Invoke this message: -h or --help");
        sb.append(System.lineSeparator());
        sb.append("Syntax of parameters: ");
        sb.append(System.lineSeparator());
        sb.append("Boolean: -param *val*");
        sb.append(System.lineSeparator());
        sb.append("Number: -param *val*");
        sb.append(System.lineSeparator());
        sb.append("Array: -param *val*,*val*,*val* !!! IMPORTANT - WITHOUT SPACES BETWEEN `,` !!!");
        sb.append(System.lineSeparator());
        existingArguments.forEach(it -> {
            sb.append("( -" + it.shortName + " or --" + it.fullName + " ): " + it.helpMessage + ". Default value: " + it.value);
            sb.append(System.lineSeparator());
        });
        return sb.toString().trim();
    }

    private boolean isHelpArgument(List<String> args) {
        return args.get(0).equals("-h") || args.get(0).equals("--help");
    }

    private void setDefaultSettings() {
        latency = 100;
        count = 3;
        pingRetries = 2;
        deepPinging = true;
        allowTransparent = true;
        outputLatency = false;
        disallowedCountries = new ArrayList<String>(Arrays.asList("RU"));
        silent = false;
        verbose = false;
    }

    private List<AbstractArgument> mapStringArgumentsToArguments(List<String> args) {
        return Utils.chunkList(args, 2).stream().map(list -> {
            String setting = list.get(0).replaceFirst("-", "").replaceFirst("-", "");
            String value = list.get(1);
            AbstractArgument arg = null;
            for (AbstractArgument a : existingArguments) {
                if (Objects.equals(a.shortName, setting) || Objects.equals(a.fullName, setting)) {
                    arg = a;
                    break;
                }
            }
            if (arg == null) throw new RuntimeException("Argument not found");
            arg.value = Utils.toObject(arg.value.getClass(), value);
            return arg;
        }).collect(Collectors.toList());
    }

    public void process(List<String> args) {
        setDefaultSettings();
        if (args.isEmpty()) return;
        if (isHelpArgument(args)) {
            Logger.info(getHelpMessage(), true);
            System.exit(0);
        }
        if (args.size() % 2 != 0)
            throw new RuntimeException("Some argument haven't value OR you have argument with 2 values OR you have spaces in Array argument");
        List<AbstractArgument> mappedArgs = mapStringArgumentsToArguments(args);
        Logger.info("Ok, settings: ");
        mappedArgs.forEach(arg -> {
            System.out.println(" + " + arg.fullName + " -> " + arg.value);
        });
        mappedArgs.forEach(AbstractArgument::process);
    }

    public SettingsManager() {
        addArguments();
    }
}
