package idk.bluecross.proxyParser.proxy;

public class ProxyDataWithSpeed extends ProxyData implements Comparable<ProxyDataWithSpeed> {
    public int speed;

    public ProxyDataWithSpeed(String ip, int port, int speed) {
        super(ip, port);
        this.speed = speed;
    }


    @Override
    public int compareTo(ProxyDataWithSpeed o) {
        if (speed > o.speed) return 1;
        if (speed == o.speed) return 0;
        else return -1;
    }
}
