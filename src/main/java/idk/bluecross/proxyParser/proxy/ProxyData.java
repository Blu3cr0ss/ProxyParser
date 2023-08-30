package idk.bluecross.proxyParser.proxy;

public class ProxyData extends AbstractProxyData {
    public ProxyData(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public class ProxyDataWithSpeed extends ProxyData {
        public int speed;

        public ProxyDataWithSpeed(String ip, int port, int speed) {
            super(ip, port);
            this.speed = speed;
        }
    }
}
