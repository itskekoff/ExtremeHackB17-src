package ShwepSS.B17.Utils;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtremeChat
extends JFrame {
    public static final Logger LOGGER1 = LogManager.getLogger();
    private static final long serialVersionUID = 1L;
    public static String nicks = "";
    public boolean spamming;
    private int result;
    public static boolean spam = false;
    public Thread t = new Thread();
    JTextField message;
    JLabel chatVer;
    JLabel mynick;
    JLabel random;
    JLabel players;
    JLabel delay;
    TextArea namefield;
    JButton button;
    JButton sendall;
    JButton sendfor;
    JButton spammer;
    JButton offSpammer;
    JTextField delayField;
    JTextField col;
    Minecraft mc = Minecraft.getMinecraft();
    public static String bd1 = "DataBase is null\n .bd1 - create holo bd\n .bd2 - read database";

    public static Logger getLogger() {
        return LOGGER1;
    }

    public ExtremeChat() {
        this.col = new JTextField();
        this.mc.updateDisplay();
        this.message = new JTextField();
        this.chatVer = new JLabel();
        this.mynick = new JLabel();
        this.players = new JLabel();
        this.random = new JLabel();
        this.delay = new JLabel();
        this.namefield = new TextArea();
        this.delayField = new JTextField();
        this.button = new JButton();
        this.sendall = new JButton();
        this.sendfor = new JButton();
        this.spammer = new JButton();
        this.offSpammer = new JButton();
        this.delayField.setBounds(540, 400, 40, 40);
        this.delayField.setText("1");
        this.message.setBounds(5, 450, 500, 40);
        this.message.setText("\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435...");
        this.chatVer.setBounds(550, 20, 200, 20);
        this.chatVer.setText("Game chat... Operators:");
        this.players.setBounds(550, 45, 200, 20);
        this.players.setText("#p - players in tab");
        this.mynick.setBounds(550, 60, 200, 20);
        this.mynick.setText("#n - " + this.mc.getSession().getUsername());
        this.random.setBounds(550, 75, 200, 20);
        this.random.setText("#r - random symbols");
        this.delay.setBounds(540, 400, 50, 20);
        this.delay.setText("Delay");
        this.namefield.setBounds(5, 20, 530, 420);
        this.namefield.setText("");
        this.button.setBounds(510, 450, 120, 40);
        this.button.setText("Send Message");
        this.sendall.setBounds(640, 450, 120, 40);
        this.sendall.setText("Send + Rand");
        this.spammer.setBounds(640, 350, 120, 40);
        this.spammer.setText("Spammer");
        this.offSpammer.setBounds(580, 350, 50, 40);
        this.offSpammer.setText("OFF");
        this.col.setBounds(590, 400, 40, 40);
        this.col.setText("2");
        this.sendfor.setBounds(640, 400, 120, 40);
        this.sendfor.setText("Send " + this.col.getText());
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(1);
        this.setTitle("ExtremeHack \u0447\u0430\u0442");
        this.setSize(new Dimension(780, 540));
        this.setLayout(null);
        this.add(this.namefield);
        this.add(this.chatVer);
        this.add(this.button);
        this.add(this.message);
        this.add(this.sendall);
        this.add(this.sendfor);
        this.add(this.col);
        this.add(this.delayField);
        this.add(this.spammer);
        this.add(this.offSpammer);
        this.add(this.players);
        this.add(this.mynick);
        this.add(this.random);
        EventManager.register(this);
        this.spammer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                try {
                    new Thread(){

                        @Override
                        public void run() {
                            try {
                                Minecraft mc = Minecraft.getMinecraft();
                                block2: while (true) {
                                    NetHandlerPlayClient nethandlerplayclient1 = mc.player.connection;
                                    List<NetworkPlayerInfo> list1 = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(nethandlerplayclient1.getPlayerInfoMap());
                                    Iterator<NetworkPlayerInfo> iterator = list1.iterator();
                                    while (true) {
                                        if (!iterator.hasNext()) continue block2;
                                        NetworkPlayerInfo networkplayerinfo1 = iterator.next();
                                        String name = networkplayerinfo1.getGameProfile().getName().toString();
                                        String message1 = (this).ExtremeChat.this.message.getText().toString();
                                        String message2 = message1.replace("#r", RandomUtils.randomString(RandomUtils.nextInt(3, 7)));
                                        String message3 = message2.replace("#p", name);
                                        String message4 = message3.replace("#n", mc.getSession().getUsername());
                                        String message5 = message4.replace("#f", String.valueOf(list1.get(RandomUtils.nextInt(1, nethandlerplayclient1.getPlayerInfoMap().size())).getGameProfile().getName()));
                                        Thread.sleep(Integer.parseInt((this).ExtremeChat.this.delayField.getText()) * 1000);
                                        if (message3.isEmpty()) {
                                            Thread.currentThread().stop();
                                        }
                                        mc.player.sendChatMessage(message5);
                                    }
                                    break;
                                }
                            }
                            catch (InterruptedException ex2) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }.start();
                }
                catch (Exception exception5) {
                    exception5.printStackTrace();
                }
            }
        });
        this.offSpammer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                ExtremeChat.this.message.setText("");
                ExtremeChat.this.namefield.append("\n[ExtremeHack] Spammer stopped. 1sek...\n");
            }
        });
        this.sendfor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                Minecraft mc = Minecraft.getMinecraft();
                ExtremeChat.this.sendfor.setText("Send " + ExtremeChat.this.col.getText());
                for (int i2 = 0; i2 < Integer.parseInt(ExtremeChat.this.col.getText()); ++i2) {
                    NetHandlerPlayClient nethandlerplayclient1 = mc.player.connection;
                    List<NetworkPlayerInfo> list1 = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(nethandlerplayclient1.getPlayerInfoMap());
                    String message1 = ExtremeChat.this.message.getText().toString();
                    String message2 = message1.replace("#r", RandomUtils.randomString(RandomUtils.nextInt(3, 7)));
                    String message3 = message2.replace("#p", String.valueOf(list1.get(RandomUtils.nextInt(1, nethandlerplayclient1.getPlayerInfoMap().size())).getGameProfile().getName()));
                    String message4 = message3.replace("#n", mc.getSession().getUsername());
                    mc.player.sendChatMessage(message4);
                }
            }
        });
        this.button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                String nick = "";
                NetHandlerPlayClient nethandlerplayclient1 = ExtremeChat.this.mc.player.connection;
                List<NetworkPlayerInfo> list1 = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(nethandlerplayclient1.getPlayerInfoMap());
                Minecraft mc = Minecraft.getMinecraft();
                String message1 = ExtremeChat.this.message.getText().toString();
                String message2 = message1.replace("#r", RandomUtils.randomString(RandomUtils.nextInt(3, 7)));
                String message4 = message2.replace("#n", mc.getSession().getUsername());
                mc.player.sendChatMessage(message4);
            }
        });
        this.sendall.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e2) {
                NetHandlerPlayClient nethandlerplayclient1 = ExtremeChat.this.mc.player.connection;
                List<NetworkPlayerInfo> list1 = GuiPlayerTabOverlay.ENTRY_ORDERING.sortedCopy(nethandlerplayclient1.getPlayerInfoMap());
                Minecraft mc = Minecraft.getMinecraft();
                String message1 = ExtremeChat.this.message.getText().toString();
                String message2 = message1.replace("#r", RandomUtils.randomString(RandomUtils.nextInt(3, 7)));
                String message3 = message2.replace("#p", String.valueOf(list1.get(RandomUtils.nextInt(1, nethandlerplayclient1.getPlayerInfoMap().size())).getGameProfile().getName()));
                String message4 = message3.replace("#n", mc.getSession().getUsername());
                mc.player.sendChatMessage(message4);
            }
        });
    }

    @EventTarget
    public void getChat(EventPacketRecieve e2) throws IOException {
        if (e2.getPacket() instanceof SPacketChat) {
            SPacketChat packet = (SPacketChat)e2.getPacket();
            Minecraft mc = Minecraft.getMinecraft();
            StringBuilder builder = new StringBuilder();
            String message = packet.getChatComponent().getUnformattedText();
            if (message.contains("[\u0427\u0430\u0442 \u0418\u0433\u0440\u0430] \u0440\u0435\u0448\u0438\u0442\u0435: ")) {
                String[] split = message.split(" ");
                try {
                    int num1 = Integer.parseInt(split[3]);
                    int num2 = Integer.parseInt(split[5]);
                    this.result = num1 + num2;
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
                ChatUtils.message(String.valueOf(ChatUtils.ehack) + "\u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442: " + this.result);
                mc.player.sendChatMessage("!" + this.result);
            }
            builder.append(String.valueOf(message));
            builder.append("\n");
            this.namefield.appendText(String.valueOf(message) + "\n");
        }
    }
}

