package ShwepSS.B17.modules.hacks;

import com.mojang.authlib.GameProfile;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;

public class GuiServerFinder
extends JFrame {
    public static Socket socket;
    public static boolean tf;
    public static boolean fast;
    public static NetworkManager networkManager;
    public JButton jButton1;
    public JLabel jLabel1;
    public JLabel jLabel2;
    public JLabel jLabel3;
    public JLabel jLabel4;
    public static JLabel jLabel5;
    public JLabel jLabel6;
    public JLabel jLabel7;
    public static JTextField jTextField1;
    public static JTextField jTextField2;
    public static JTextField jTextField3;
    public static JTextField jTextField4;
    public static JTextField jTextField5;
    public static JTextField jTextField6;
    public static TextArea textArea1;
    public static JTextField textField;
    public static JTextField textField_1;
    public static List<String> listSocketPort;
    public static boolean on;
    public int c = 0;
    public static List Servers2;

    static {
        tf = false;
        fast = false;
        listSocketPort = new ArrayList<String>();
        Servers2 = new ArrayList();
    }

    public GuiServerFinder() {
        this.initComponents();
    }

    private void initComponents() {
        jTextField1 = new JTextField();
        jTextField1.setText("51.178");
        this.jLabel1 = new JLabel();
        jTextField2 = new JTextField();
        jTextField2.setText("25564");
        this.jLabel2 = new JLabel();
        this.jButton1 = new JButton();
        jTextField3 = new JTextField();
        jTextField3.setText("EHack");
        this.jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        this.jLabel7 = new JLabel();
        jTextField6 = new JTextField();
        jTextField6.setText("25566");
        jTextField4 = new JTextField();
        jTextField4.setText("1");
        jTextField5 = new JTextField();
        jTextField5.setText("255");
        this.jLabel3 = new JLabel();
        this.jLabel6 = new JLabel();
        textArea1 = new TextArea();
        textArea1.setText("===Scan Logs=== \n\u0421\u043a\u0430\u043d\u0435\u0440 \u0438\u0437 \u0447\u0438\u0442\u0430 Jessica");
        this.setTitle("Jessica SFinder | \u0420\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u044b \u0432 \u0444\u0430\u0439\u043b\u0435 .minecraft/ServerIs2.txt");
        this.jLabel1.setText("IP");
        this.jLabel2.setText("Port");
        this.jButton1.setText("Start");
        jTextField3.setToolTipText("");
        jTextField3.setName("");
        this.jLabel4.setText("Nick");
        this.jLabel7.setText("-");
        this.jButton1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                GuiServerFinder.this.jButton1ActionPerformed(evt);
            }
        });
        this.jLabel3.setText("-");
        this.jLabel6.setText(".");
        textField = new JTextField();
        textField_1 = new JTextField();
        JLabel label = new JLabel();
        label.setText(".");
        JLabel label_1 = new JLabel();
        label_1.setText("-");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(textArea1, GroupLayout.Alignment.TRAILING, -1, 387, 32767).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jTextField3, -2, 104, -2)).addGroup(layout.createSequentialGroup().addGap(52).addComponent(this.jLabel4))).addGap(4).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addComponent(jTextField2, -2, 52, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel7).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField6, -2, 52, -2)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabel2).addGroup(layout.createSequentialGroup().addComponent(textField, -2, 30, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(label_1, -2, 4, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(textField_1, -2, 30, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(label, -2, 4, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField4, -2, 30, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField5, -2, 30, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))).addGap(48)))).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel5)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jTextField1, -2, 96, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel6)).addGroup(layout.createSequentialGroup().addGap(55).addComponent(this.jLabel1)).addGroup(layout.createSequentialGroup().addGap(27).addComponent(this.jButton1, -2, 288, -2))).addGap(0, 65, 32767))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel1, -2, 14, -2).addGap(1).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jTextField1, -2, -1, -2).addComponent(this.jLabel6).addComponent(textField, -2, -1, -2).addComponent(label_1).addComponent(textField_1, -2, -1, -2).addComponent(label).addComponent(jTextField4, -2, -1, -2).addComponent(this.jLabel3).addComponent(jTextField5, -2, -1, -2)).addGap(32).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.jLabel2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jTextField3, -2, -1, -2).addComponent(jTextField2, -2, -1, -2).addComponent(this.jLabel7).addComponent(jTextField6, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(textArea1, -1, -1, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton1, -2, 34, -2).addContainerGap()));
        this.getContentPane().setLayout(layout);
        this.pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        try {
            on = true;
            try {
                for (int tt2 = Integer.parseInt(textField.getText()); tt2 <= Integer.parseInt(textField_1.getText()); ++tt2) {
                    final int t2 = tt2;
                    for (int aa2 = Integer.parseInt(jTextField4.getText()); aa2 <= Integer.parseInt(jTextField5.getText()); ++aa2) {
                        final int a2 = aa2;
                        for (int ii2 = Integer.parseInt(jTextField2.getText()); ii2 <= Integer.parseInt(jTextField6.getText()); ++ii2) {
                            System.out.println(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2);
                            try {
                                final int i2 = ii2;
                                ++this.c;
                                if (this.c > 1000) {
                                    System.out.println(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2 + ":" + i2);
                                    if (GuiServerFinder.portIsOpen(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2, i2, 150)) {
                                        if (!textArea1.getText().contains("(1.12.2) \u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0439\u0434\u0435\u043d: " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2)) {
                                            textArea1.append("(1.12.2) \u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0439\u0434\u0435\u043d: " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2 + "\n");
                                            Servers2.add("(1.12.2) " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2);
                                            GuiServerFinder.ServerIs2();
                                        }
                                        listSocketPort.add(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2 + ":" + i2);
                                    }
                                    if (this.c > 1004) {
                                        this.c = 0;
                                    }
                                }
                                new Thread(){

                                    @Override
                                    public void run() {
                                        try {
                                            System.out.println(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2 + ":" + i2);
                                            if (GuiServerFinder.portIsOpen(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2, i2, 150)) {
                                                if (!textArea1.getText().contains("(1.12.2) \u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0439\u0434\u0435\u043d: " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2)) {
                                                    textArea1.append("(1.12.2) \u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0439\u0434\u0435\u043d: " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2 + "\n");
                                                    Servers2.add("(1.12.2) " + jTextField1.getText() + "." + t2 + "." + a2 + ":" + i2);
                                                    GuiServerFinder.ServerIs2();
                                                }
                                                listSocketPort.add(String.valueOf(String.valueOf(String.valueOf(jTextField1.getText()))) + "." + t2 + "." + a2 + ":" + i2);
                                            }
                                        }
                                        catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                }.start();
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            Thread.sleep(3L);
                        }
                    }
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            Thread.sleep(10000L);
        }
        catch (InterruptedException e3) {
            e3.printStackTrace();
        }
        for (final String listSocketPort2 : listSocketPort) {
            System.out.println("#2: " + listSocketPort2);
            new Thread(){

                @Override
                public void run() {
                    String[] listSocketPort22 = listSocketPort2.split(":");
                    InetAddress var1 = null;
                    try {
                        var1 = InetAddress.getByName(listSocketPort22[0]);
                    }
                    catch (UnknownHostException e2) {
                        e2.printStackTrace();
                    }
                    networkManager = NetworkManager.createNetworkManagerAndConnect(var1, Integer.parseInt(listSocketPort22[1]), Minecraft.getMinecraft().gameSettings.isUsingNativeTransport());
                    networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, Minecraft.getMinecraft(), new GuiIngameMenu()));
                    networkManager.sendPacket(new C00Handshake(listSocketPort22[0], Integer.parseInt(listSocketPort22[1]), EnumConnectionState.LOGIN));
                    networkManager.sendPacket(new CPacketLoginStart(new GameProfile(null, jTextField3.getText())));
                    textArea1.append("(1.12.2) \u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0439\u0434\u0435\u043d #2 / \u041f\u0440\u043e\u0432\u0435\u0440\u044f\u0435\u0442\u0441\u044f: " + listSocketPort2 + "\n");
                    Servers2.add("(1.12.2) #2: " + listSocketPort2);
                    GuiServerFinder.ServerIs2();
                    try {
                        Thread.sleep(50L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }.start();
        }
        listSocketPort.clear();
    }

    public static void ServerIs2() {
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "ExtremeHack/Servers.txt");
            BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file));
            for (int i2 = 0; i2 < Servers2.size(); ++i2) {
                bufferedwriter.write(Servers2.get(i2) + "\r\n");
            }
            bufferedwriter.close();
        }
        catch (Exception exception) {
            System.err.print(exception.toString());
        }
    }

    public static boolean portIsOpen(String ip2, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip2, port), timeout);
            socket.close();
            return true;
        }
        catch (Exception ex2) {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (!"Windows".equals(info.getName())) continue;
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        catch (ClassNotFoundException ex2) {
            Logger.getLogger(GuiServerFinder.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (InstantiationException ex2) {
            Logger.getLogger(GuiServerFinder.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (IllegalAccessException ex3) {
            Logger.getLogger(GuiServerFinder.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (UnsupportedLookAndFeelException ex4) {
            Logger.getLogger(GuiServerFinder.class.getName()).log(Level.SEVERE, null, ex4);
        }
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                new GuiServerFinder().setVisible(true);
            }
        });
    }
}

