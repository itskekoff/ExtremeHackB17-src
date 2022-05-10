package ShwepSS.B17;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils {
    public static String Dblue = "\u00a71";
    public static String blue = "\u00a79";
    public static String cyan = "\u00a7b";
    public static String Dgreen = "\u00a72";
    public static String green = "\u00a7a";
    public static String red = "\u00a7c";
    public static String Dred = "\u00a74";
    public static String pink = "\u00a7d";
    public static String gold = "\u00a76";
    public static String purple = "\u00a75";
    public static String gray = "\u00a77";
    public static String dred = "\u00a74";
    public static String white = "\u00a7f";
    public static String ss = "\u00a7";
    public static String l = "\u00a7l";
    public static String o = "\u00a7o";
    public static String n = "\u00a7n";
    public static String k = "\u00a7k";
    public static String m = "\u00a7m";
    public static String YT = "\u00a74\u00a7nYou\u00a7f\u00a7nTube\u00a7f\u00a7n";
    public static String hacker = "\u00a7";
    public static String ehack = "\u00a77\u00a7l[\u00a7b\u00a7lExtreme\u00a72\u00a7lHack\u00a77\u00a7l] \u00a7f\u00a7l";
    public static String ebot = "\u00a7f\u00a7l[\u00a7b\u00a7lExtreme\u00a7a\u00a7lBot\u00a7f\u00a7l] ";
    public static String dev = "\u00a7b\u00a7lCREATOR";
    public static String helper = "\u00a79Helper";
    public static String friend = "\u00a7aFriend";
    private static boolean enabled = true;

    public static void setEnabled(boolean enabled) {
        ChatUtils.enabled = enabled;
    }

    public static void component(ITextComponent component) {
        if (enabled) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("").appendSibling(component));
        }
    }

    public static void message(String message) {
        ChatUtils.component(new TextComponentString(message));
    }

    public static void emessage(String message) {
        ChatUtils.component(new TextComponentString(String.valueOf(white) + "[" + cyan + "EHack" + white + "] " + message));
    }

    public static void warning(String message) {
        ChatUtils.message("\ufffdc[\ufffd6\ufffdlWARNING\ufffdc]\ufffdf " + message);
    }

    public static void error(String message) {
        ChatUtils.message("\ufffdc[\ufffd4\ufffdlERROR\ufffdc]\ufffdf " + message);
    }

    public static void success(String message) {
        ChatUtils.message("\ufffda[\ufffd2\ufffdlSUCCESS\ufffda]\ufffdf " + message);
    }

    public static void failure(String message) {
        ChatUtils.message("\ufffdc[\ufffd4\ufffdlFAILURE\ufffdc]\ufffdf " + message);
    }

    public static void cmd(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\ufffdbExtremeHack\ufffdf \ufffd0\ufffdl<\ufffdaCMD\ufffd0\ufffdl>\ufffdf " + message));
    }

    public static void clear() {
    }

    public static String clearFormat(String s2) {
        ArrayList<String> formats = new ArrayList<String>();
        for (int i2 = 0; i2 < s2.length(); ++i2) {
            char c2 = s2.charAt(i2);
            if (c2 != '\u00a7') continue;
            formats.add(s2.substring(i2, Math.min(i2 + 2, s2.length())));
        }
        for (String st2 : formats) {
            s2 = s2.replace(st2, "");
        }
        return s2;
    }
}

