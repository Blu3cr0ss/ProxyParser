package idk.bluecross.proxyParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {       // SHITCODE ATTENTION
    public Parser() {
    }

//    public static Parser INSTANCE = new Parser();

    LinkedList<String> ipList = new LinkedList();

    public void start() throws IOException {
        try {
            Logger.info("Getting proxy list...");
            getList();
            Logger.info("Done getting list, pinging now...");
            LinkedHashMap pingList = new LinkedHashMap(testPing());
            Logger.info("Deep pinging...");
            if (Settings.deepPinging) doDeepPinging(pingList);
            output(pingList);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Something went wrong :/");
        }
    }

    private void getList() throws IOException {
        Document doc = Jsoup.connect("https://free-proxy-list.net/").get();
        Element tbody = doc.selectFirst("tbody");
        Elements tr_s = tbody.select("tr");
        for (Element el : tr_s) {
            Elements td_s = el.select("td");
            String type = td_s.get(4).text();
            String country = td_s.get(2).text();
            boolean supportsHttps = td_s.get(6).text().equals("yes");
            if (supportsHttps && ((Settings.allowTransparent || !type.equals("transparent")) && !Settings.disallowedCountries.contains(country))) {
                String ip = td_s.get(0).text() + ":" + td_s.get(1).text();
                ipList.add(ip);
            }
        }
    }

    private HashMap<String, Integer> testPing() throws IOException {
        ArrayList<String> forRemoval = new ArrayList();
        HashMap<String, Integer> latencyList = new HashMap();
        for (String ip : ipList) {
            boolean reached = false;
            int[] latencies = new int[Settings.pingRetries];
            int latency = -1;
            for (int i = 0; i < Settings.pingRetries; i++) {
                long before = System.currentTimeMillis();
                reached = InetAddress.getByName(ip.split(":")[0]).isReachable(Settings.latency);
                long after = System.currentTimeMillis();
                latencies[i] = (int) (after - before);
                if (!reached) {
                    break;
                }
            }

            for (int i : latencies) latency += i;
            latency /= Settings.pingRetries;


            if (!reached || latency > Settings.latency) {
                Logger.verbose(ip + " not reached");
                forRemoval.add(ip);
            } else {
                Logger.verbose(ip + " latency = " + Arrays.toString(latencies));
                Logger.verbose(ip + " avg latency = " + latency);
                latencyList.put(ip, (int) latency);
            }
        }
        ipList.removeAll(forRemoval);
        Logger.info("Done pinging, sorting now...");
        return latencyList;
    }

    private void doDeepPinging(LinkedHashMap<String, Integer> list) throws MalformedURLException {
        URL testUrl = new URL("http://checkip.amazonaws.com/");
        ArrayList<String> forRemoval = new ArrayList();
        list.forEach((addr, ping) -> {
            Proxy proxy;
            HttpURLConnection con;

            String ip = addr.split(":")[0];
            int port = Integer.parseInt(addr.split(":")[1]);

            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));

            try {
                con = (HttpURLConnection) testUrl.openConnection(proxy);
                con.setConnectTimeout(Settings.latency * 3);
                con.setReadTimeout(Settings.latency * 3);
                con.connect();
                if (!String.valueOf(con.getResponseCode()).startsWith("2")) {
                    throw new RuntimeException(String.valueOf(con.getResponseCode())); // we wont see this bc of catch
                }
                Logger.verbose(addr + " passed deep pinging");
            } catch (Exception e) {
                Logger.verbose(addr + " failed deeep pinging - " + e.getMessage());
                forRemoval.add(addr);
            }
        });
        for (String s : forRemoval) {
            list.remove(s);
        }
    }

    private LinkedHashMap<String, Integer> sort(LinkedHashMap<String, Integer> list) {
        LinkedHashMap<String, Integer> sorted = (LinkedHashMap) sortByValue(list);
        if (Settings.count != 0) {
            if (sorted.size() > Settings.count) {  // returning only N proxies (-c arg)
                Map temp = sorted.entrySet().stream().collect(Collectors.toList()).subList(0, Settings.count).stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                sorted.clear();
                sorted.putAll(temp);
            }
        }
        return sorted;
    }

    void output(LinkedHashMap list) {
        Logger.info("-----------------------------------------------");
        if (list.isEmpty()) {
            Logger.info("Proxies not found :( \nTry changing max latency or allow transparen or something idk");
            return;
        }
        LinkedHashMap _list = sort(list);
        if (Settings.outputLatency) {
            _list.forEach((k, v) -> {
                Logger.info(k + " ; latency = " + v, true);
            });
        } else {
            _list.forEach((k, v) -> {
                Logger.info(k, true);
            });
        }
    }

    private LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> map) {  // why tf this shit isnt works :(((
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}
