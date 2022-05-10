package ShwepSS.B17.Utils;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WurstServerPinger {
    private static final AtomicInteger threadNumber = new AtomicInteger(0);
    public static final Logger logger = LogManager.getLogger();
    public ServerData server;
    private boolean done = false;
    private boolean failed = false;

    public void ping(String ip2) {
        this.ping(ip2, 25565);
    }

    public void ping(final String ip2, final int port) {
        this.server = new ServerData("", String.valueOf(String.valueOf(ip2)) + ":" + port, false);
        new Thread("Wurst Server Connector #" + threadNumber.incrementAndGet()){

            @Override
            public void run() {
                ServerPinger pinger = new ServerPinger();
                try {
                    logger.info("Pinging " + ip2 + ":" + port + "...");
                    pinger.ping(WurstServerPinger.this.server);
                    logger.info("Ping successful: " + ip2 + ":" + port);
                }
                catch (UnknownHostException e2) {
                    logger.info("Unknown host: " + ip2 + ":" + port);
                    WurstServerPinger.access$0(WurstServerPinger.this, true);
                }
                catch (Exception e2) {
                    logger.info("Ping failed: " + ip2 + ":" + port);
                    WurstServerPinger.access$0(WurstServerPinger.this, true);
                }
                pinger.clearPendingNetworks();
                WurstServerPinger.access$1(WurstServerPinger.this, true);
            }
        }.start();
    }

    public boolean isStillPinging() {
        return !this.done;
    }

    public boolean isWorking() {
        return !this.failed;
    }

    public boolean isOtherVersion() {
        return this.server.version != 47;
    }

    static void access$0(WurstServerPinger wurstServerPinger, boolean failed) {
        wurstServerPinger.failed = failed;
    }

    static void access$1(WurstServerPinger wurstServerPinger, boolean done) {
        wurstServerPinger.done = done;
    }
}

