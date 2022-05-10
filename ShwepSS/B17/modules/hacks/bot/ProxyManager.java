package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ChatUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class ProxyManager {
    private static int proxyIterator;
    public static List<Proxy> proxies;
    public static ArrayList<String> stringProxy;

    static {
        stringProxy = new ArrayList();
        proxyIterator = 0;
        proxies = new ArrayList<Proxy>();
    }

    public static void loadProxies(String www) {
        try {
            String inputLine;
            URL website = new URL(www);
            URLConnection connection = website.openConnection();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in2.readLine()) != null) {
                stringProxy.add(inputLine);
                System.out.println("[ExtremeHack] \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u044b \u043f\u0440\u043e\u043a\u0441\u0438 \u0432 \u0442\u0435\u043a\u0441\u0442\u043e\u0432\u044b\u0439 \u0441\u043f\u0438\u0441\u043e\u043a");
                if (inputLine.contains(":")) {
                    String ip2 = inputLine.split(":")[0];
                    int port = Integer.valueOf(inputLine.split(":")[1]);
                    Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip2, port));
                    proxies.add(proxy);
                    continue;
                }
                System.out.println(String.valueOf(String.valueOf(inputLine)) + " error");
            }
        }
        catch (MalformedURLException e2) {
            System.err.println("Page does not exist!");
        }
        catch (IOException e2) {
            System.err.println("No internet!");
        }
        System.out.println(String.valueOf(String.valueOf(proxies.size())) + " Proxies loaded.");
    }

    public static void downloadFile() {
        try {
            URL website = new URL("https://api.proxyscrape.com/?request=getproxies&proxytype=socks5&timeout=500000&country=all&anonymity=elite&ssl=yes");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("proxy.txt");
            fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            ChatUtils.message(String.valueOf(ChatUtils.ehack) + "Downloaded proxy!");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void loadProxiesFromFile() {
        try {
            File file = new File("proxy.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readString = "";
            while ((readString = bufferedReader.readLine()) != null) {
                stringProxy.add(readString);
                if (!readString.contains(":")) continue;
                proxies.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(readString.split(":")[0], Integer.parseInt(readString.split(":")[1]))));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        System.out.println(proxies.size() + " Proxies loaded.");
    }

    public static Proxy getRandomProxy() {
        if (proxyIterator > proxies.size() - 1) {
            proxyIterator = 0;
        }
        Proxy proxy = proxies.get(proxyIterator);
        ++proxyIterator;
        return proxy;
    }
}

