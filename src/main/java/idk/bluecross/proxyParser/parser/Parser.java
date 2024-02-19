package idk.bluecross.proxyParser.parser;

import idk.bluecross.proxyParser.util.Logger;
import idk.bluecross.proxyParser.settings.Settings;
import idk.bluecross.proxyParser.proxy.ProxyData;
import idk.bluecross.proxyParser.proxy.ProxyDataWithSpeed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Parser extends AbstractParser {
    LinkedList<String> ipList = new LinkedList();

    @Override
    public String getWebsite() {
        return "https://free-proxy-list.net";
    }

    @Override
    public String process() {
        Logger.info("Getting proxy list...");
        List<ProxyData> proxies = getProxyData();
        Logger.verbose(proxies.stream().map(it -> {
            return it.ip + ":" + it.port + "\n";
        }).collect(Collectors.toList()));
        Logger.info("Done getting list, pinging now...");
        List<ProxyDataWithSpeed> proxyDataWithSpeeds = pingProxies(proxies);
        if (Settings.deepPinging) {
            Logger.verbose("before: " + proxyDataWithSpeeds.size());
            proxyDataWithSpeeds = deepPingProxies(proxyDataWithSpeeds);
            Logger.verbose("after: " + proxyDataWithSpeeds.size());
        }
        Logger.info("Done pinging, sorting now...");
        LinkedList<ProxyDataWithSpeed> sorted = proxyDataWithSpeeds.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        StringBuilder sb = new StringBuilder();
        sorted.subList(0, Math.min(Settings.count, sorted.size())).forEach(proxy -> {
            sb.append(proxy.ip);
            sb.append(":");
            sb.append(proxy.port);
            if (Settings.outputLatency)
                sb.append(" -> " + proxy.speed+" ms");
            sb.append(System.lineSeparator());
        });
        if (sb.toString().isEmpty()){
            sb.append("Proxies not found :( Try changing latency");
        }
        return sb.toString();
    }

    private List<ProxyData> getProxyData() {
        List<ProxyData> output = new LinkedList<>();
        Document doc;
        try {
            doc = Jsoup.connect("https://free-proxy-list.net/").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element tbody = doc.selectFirst("tbody");
        Elements tr_s = tbody.select("tr");
        for (Element el : tr_s) {
            Elements td_s = el.select("td");
            String type = td_s.get(4).text();
            String country = td_s.get(2).text();
            boolean supportsHttps = td_s.get(6).text().equals("yes");

            if ((!Settings.https || supportsHttps) && ((Settings.allowTransparent || !type.equals("transparent")) && !Settings.disallowedCountries.contains(country))) {
                String ip = td_s.get(0).text();
                int port = Integer.parseInt(td_s.get(1).text());
                output.add(new ProxyData(ip, port));
            }
        }
        return output;
    }

    private List<ProxyDataWithSpeed> pingProxies(List<ProxyData> proxies) {
        List<ProxyDataWithSpeed> output = new ArrayList<>();
        proxies.forEach(proxyData -> {
            // PINGING LOGIC COPIED FROM V1 BC IM LAZY :)
            boolean reached = false;
            int[] latencies = new int[Settings.pingRetries];
            int latency = -1;
            for (int i = 0; i < Settings.pingRetries; i++) {
                long before = System.currentTimeMillis();
                try {
                    reached = InetAddress.getByName(proxyData.ip).isReachable(Settings.latency);
                } catch (IOException e) {
                    reached = false;
                }
                long after = System.currentTimeMillis();
                latencies[i] = (int) (after - before);
                if (!reached) {
                    break;
                }
            }
            for (int i : latencies) latency += i;
            latency /= Settings.pingRetries;

            if (!reached || latency > Settings.latency) {
                Logger.verbose(proxyData.ip + " not reached");
            } else {
                Logger.verbose(proxyData.ip + " latency = " + Arrays.toString(latencies));
                Logger.verbose(proxyData.ip + " avg latency = " + latency);
                output.add(new ProxyDataWithSpeed(proxyData.ip, proxyData.port, latency));
            }
        });
        return output;
    }

    private List<ProxyDataWithSpeed> deepPingProxies(List<ProxyDataWithSpeed> proxies) {
        List<ProxyDataWithSpeed> output = new ArrayList<>();
        URL testUrl = null;
        try {
            testUrl = new URL("h ");
        } catch (MalformedURLException e) { // im pretty sure this isn't possible but i have to catch it
            System.out.println(e);
        }
        URL finalTestUrl = testUrl;
        proxies.forEach(proxyData -> {
            // DEEP PINGING LOGIC COPIED FROM V1 TOO BC IM LAZY :)
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyData.ip, proxyData.port));
            HttpURLConnection con;

            try {
                con = (HttpURLConnection) finalTestUrl.openConnection(proxy);
                con.setConnectTimeout(Settings.latency * 3);
                con.setReadTimeout(Settings.latency * 3);
                con.connect();
                if (!String.valueOf(con.getResponseCode()).startsWith("2")) {
                    throw new RuntimeException(String.valueOf(con.getResponseCode())); // we wont see this bc of catch
                }
                output.add(proxyData);
                Logger.verbose(proxyData.ip + " passed deep pinging");
            } catch (Exception e) {
                Logger.verbose(proxyData.ip + " failed deeep pinging - " + e.getMessage());
            }
        });
        return output;
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
