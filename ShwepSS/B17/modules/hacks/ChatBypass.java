package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventChatSend;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;

public class ChatBypass
extends Module {
    Setting bypass = new Setting("\u041c\u0430\u0442-\u043e\u0431\u0445\u043e\u0434", this, true);
    Setting enText = new Setting("EN - \u0447\u0430\u0442", this, false);
    Setting ruText = new Setting("\u041a\u0440\u0430\u0441\u0438\u0432\u044b\u0439", this, false);
    Setting bypass2 = new Setting("\u041e\u0431\u0445\u043e\u0434 2", this, false);

    public ChatBypass() {
        super("ChatBypass", "\u041e\u0431\u0445\u043e\u0434 \u0430\u043d\u0442\u0438\u043c\u0430\u0442\u0430 \u0438 \u043a\u0440\u0430\u0441\u0438\u0432\u044b\u0439 \u0447\u0430\u0442", 0, Category.MISC, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.bypass);
        ExtremeHack.instance.getSetmgr().rSetting(this.enText);
        ExtremeHack.instance.getSetmgr().rSetting(this.ruText);
        ExtremeHack.instance.getSetmgr().rSetting(this.bypass2);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @EventTarget
    public void onev(EventChatSend e2) {
        String blacklist = "(){}[]|";
        if (e2.streng.startsWith("/") || e2.streng.startsWith(".")) {
            return;
        }
        String out = "";
        String message = e2.streng;
        if (this.bypass2.getValue()) {
            message = message.replaceAll("\u0430", "\u0430\u061c");
            message = message.replaceAll("\u0431", "\u0431\u061c");
            message = message.replaceAll("\u0432", "\u0432\u061c");
            message = message.replaceAll("\u0433", "\u0433\u061c");
            message = message.replaceAll("\u0434", "\u0434\u061c");
            message = message.replace("\u0435", "\u0435\u061c");
            message = message.replace("\u0451", "\u0451\u061c");
            message = message.replace("\u0436", "\u0436\u061c");
            message = message.replace("\u0437", "\u0437\u061c");
            message = message.replace("\u0438", "\u0438\u061c");
            message = message.replace("\u043a", "\u043a\u061c");
            message = message.replace("\u043b", "\u043b\u061c");
            message = message.replace("\u043c", "\u043c\u061c");
            message = message.replaceAll("\u043d", "\u043d\u061c");
            message = message.replace("\u043e", "\u043e\u061c");
            message = message.replace("\u043f", "\u043f\u061c");
            message = message.replace("\u0440", "\u0440\u061c");
            message = message.replace("\u0441", "\u0441\u061c");
            message = message.replace("\u0442", "\u0442\u061c");
            message = message.replace("\u044b", "\u044b\u061c");
            message = message.replace("\u044e", "\u044e\u061c");
            message = message.replace("\u044f", "\u044f\u061c");
            message = message.replace("\u044d", "\u044d\u061c");
            message = message.replace("\u0447", "\u0447\u061c");
            message = message.replace("\u0445", "\u0445\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c\u061c");
            message = message.replace("\u0444", "\u0444");
            e2.streng = message = message.toUpperCase();
        }
        if (this.ruText.getValue()) {
            message = message.replaceAll("\u0430", "\uff21");
            message = message.replaceAll("\u0431", "6");
            message = message.replaceAll("\u0432", "\uff22");
            message = message.replaceAll("\u0433", "\u0413");
            message = message.replaceAll("\u0441", "\uff23");
            message = message.replace("\u0435", "\uff25");
            message = message.replace("\u043c", "\uff2d");
            message = message.replace("\u0447", "4");
            message = message.replace("\u0440", "\uff30");
            message = message.replace("\u0442", "\uff34");
            message = message.replace("\u0443", "\uff39");
            message = message.replace("\u043d", "\uff28");
            message = message.replace("\u0434", "\uff24");
            message = message.replaceAll("\u0437", "3");
            message = message.replace("\u043e", "\uff2f");
            e2.streng = message = message.toUpperCase();
        }
        if (this.enText.getValue()) {
            for (char chr2 : e2.streng.toCharArray()) {
                out = chr2 >= '!' && chr2 <= '\u0080' && !"(){}[]|".contains(Character.toString(chr2)) ? String.valueOf(String.valueOf(out)) + new String(Character.toChars(chr2 + 65248)) : String.valueOf(String.valueOf(out)) + chr2;
            }
            e2.streng = out;
        }
        if (this.bypass.getValue()) {
            message = message.replaceAll("\u0430", "\u1d00");
            message = message.replaceAll("\u0435", "\u1d07");
            message = message.replaceAll("\u0431", "6");
            message = message.replaceAll("\u0430", "\u1d00");
            message = message.replaceAll("\u0433", "\u1d26");
            message = message.replaceAll("\u0445", "\u2179");
            message = message.replaceAll("\u0438", "\u1d0e");
            message = message.replaceAll("\u043d", "\u157c");
            message = message.replaceAll("\u043e", "\u1d0f");
            message = message.replaceAll("\u043b", "Jl");
            message = message.replaceAll("\u0440", "\u1d29");
            message = message.replaceAll("\u0441", "\u1455");
            e2.streng = message = message.replaceAll("\u0443", "y");
        }
    }
}

