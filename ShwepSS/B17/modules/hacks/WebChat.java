package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import net.minecraft.network.play.server.SPacketChat;

public class WebChat
extends Module {
    public static StringBuilder sb = new StringBuilder();
    public static Module instance;
    public static String message;
    public static ArrayList<String> msg;
    public static ArrayList<OutputStream> streams;

    static {
        message = "\u0427\u0430\u0442 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442!";
        msg = new ArrayList();
        streams = new ArrayList();
    }

    public WebChat() {
        super("\u0412\u0435\u0431-\u0447\u0430\u0442", "\u0437\u0430\u043f\u0443\u0441\u043a\u0430\u0435\u0442 \u0432\u0435\u0431 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0443 \u0441 \u0447\u0430\u0442\u043e\u043c", 0, Category.MISC, false);
        instance = this;
    }

    public static void starter() throws Throwable {
        try {
            new Thread(){

                @Override
                public void run() {
                    try {
                        ServerSocket ss2 = new ServerSocket(8080);
                        while (instance.isEnabled()) {
                            Socket s2 = ss2.accept();
                            System.err.println("Client accepted");
                            try {
                                new Thread(new SocketProcessor(s2)).start();
                            }
                            catch (Throwable e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }.start();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        try {
            WebChat.starter();
        }
        catch (Throwable e2) {
            e2.printStackTrace();
        }
        ChatUtils.emessage("\u0412\u0435\u0431 \u0441\u0435\u0440\u0432\u0435\u0440 \u0437\u0430\u043f\u0443\u0449\u0435\u043d \u043d\u0430 \u043f\u043e\u0440\u0442\u0443 8080");
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        File file = new File("webpanel.txt");
        if (file.delete()) {
            System.out.println(String.valueOf(file.getName()) + " deleted");
        } else {
            System.out.println(String.valueOf(file.getName()) + " not deleted");
        }
    }

    @EventTarget
    public void processChat(EventPacketRecieve ev2) {
        if (ev2.getPacket() instanceof SPacketChat) {
            String message;
            SPacketChat packet = (SPacketChat)ev2.getPacket();
            WebChat.message = message = packet.getChatComponent().getUnformattedText().toString();
            try {
                File file = new File("webpanel.txt");
                Scanner sc2 = new Scanner(new FileInputStream(file));
                int count = 0;
                while (sc2.hasNext()) {
                    sc2.next();
                    ++count;
                }
                System.out.println("Number of words: " + count + " ");
            }
            catch (Exception file) {
                // empty catch block
            }
            try {
                FileWriter writer = new FileWriter("webpanel.txt", true);
                BufferedWriter bufferWriter = new BufferedWriter(writer);
                bufferWriter.write(String.valueOf(message) + " <br>");
                bufferWriter.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static class SocketProcessor
    implements Runnable {
        private Socket s;
        private InputStream is;
        public OutputStream os;

        private SocketProcessor(Socket s2) throws Throwable {
            this.s = s2;
            this.is = s2.getInputStream();
            this.os = s2.getOutputStream();
        }

        @Override
        public void run() {
            block12: {
                try {
                    try {
                        this.readInputHeaders();
                        this.os.write("HTTP/1.0 200 OK\r\n".getBytes());
                        this.os.write("Content-Type: text/html\r\n".getBytes());
                        this.os.write("\r\n".getBytes());
                        this.os.write("<TITLE>ExtremeHack</TITLE>".getBytes());
                        this.os.write("<br>".getBytes());
                        this.os.write("<meta charset=\"UTF-8\" />".getBytes());
                        File file = new File("webpanel.txt");
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String readString = "";
                        while ((readString = bufferedReader.readLine()) != null) {
                            this.os.write(readString.getBytes());
                        }
                    }
                    catch (Throwable throwable) {
                        try {
                            this.s.close();
                        }
                        catch (Throwable throwable2) {}
                        break block12;
                    }
                }
                catch (Throwable throwable) {
                    try {
                        this.s.close();
                    }
                    catch (Throwable throwable3) {
                        // empty catch block
                    }
                    throw throwable;
                }
                try {
                    this.s.close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            System.err.println("Client processing finished");
        }

        private void readInputHeaders() throws Throwable {
            String s2;
            BufferedReader br2 = new BufferedReader(new InputStreamReader(this.is));
            while ((s2 = br2.readLine()) != null && s2.trim().length() != 0) {
            }
        }
    }
}

