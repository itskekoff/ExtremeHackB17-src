package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class GuiUtilRenderComponents {
    public static String removeTextColorsIfConfigured(String text, boolean forceColor) {
        return !forceColor && !Minecraft.getMinecraft().gameSettings.chatColours ? TextFormatting.getTextWithoutFormattingCodes(text) : text;
    }

    public static List<ITextComponent> splitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean p_178908_3_, boolean forceTextColor) {
        int i2 = 0;
        TextComponentString itextcomponent = new TextComponentString("");
        ArrayList<ITextComponent> list = Lists.newArrayList();
        ArrayList<ITextComponent> list1 = Lists.newArrayList(textComponent);
        for (int j2 = 0; j2 < list1.size(); ++j2) {
            String s4;
            ITextComponent itextcomponent1 = (ITextComponent)list1.get(j2);
            String s2 = itextcomponent1.getUnformattedComponentText();
            boolean flag = false;
            if (s2.contains("\n")) {
                int k2 = s2.indexOf(10);
                String s1 = s2.substring(k2 + 1);
                s2 = s2.substring(0, k2 + 1);
                TextComponentString itextcomponent2 = new TextComponentString(s1);
                itextcomponent2.setStyle(itextcomponent1.getStyle().createShallowCopy());
                list1.add(j2 + 1, itextcomponent2);
                flag = true;
            }
            String s5 = (s4 = GuiUtilRenderComponents.removeTextColorsIfConfigured(String.valueOf(itextcomponent1.getStyle().getFormattingCode()) + s2, forceTextColor)).endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRendererIn.getStringWidth(s5);
            TextComponentString textcomponentstring = new TextComponentString(s5);
            textcomponentstring.setStyle(itextcomponent1.getStyle().createShallowCopy());
            if (i2 + i1 > maxTextLenght) {
                String s3;
                String s22 = fontRendererIn.trimStringToWidth(s4, maxTextLenght - i2, false);
                String string = s3 = s22.length() < s4.length() ? s4.substring(s22.length()) : null;
                if (s3 != null && !s3.isEmpty()) {
                    int l2 = s22.lastIndexOf(32);
                    if (l2 >= 0 && fontRendererIn.getStringWidth(s4.substring(0, l2)) > 0) {
                        s22 = s4.substring(0, l2);
                        if (p_178908_3_) {
                            ++l2;
                        }
                        s3 = s4.substring(l2);
                    } else if (i2 > 0 && !s4.contains(" ")) {
                        s22 = "";
                        s3 = s4;
                    }
                    TextComponentString textcomponentstring1 = new TextComponentString(s3);
                    textcomponentstring1.setStyle(itextcomponent1.getStyle().createShallowCopy());
                    list1.add(j2 + 1, textcomponentstring1);
                }
                i1 = fontRendererIn.getStringWidth(s22);
                textcomponentstring = new TextComponentString(s22);
                textcomponentstring.setStyle(itextcomponent1.getStyle().createShallowCopy());
                flag = true;
            }
            if (i2 + i1 <= maxTextLenght) {
                i2 += i1;
                itextcomponent.appendSibling(textcomponentstring);
            } else {
                flag = true;
            }
            if (!flag) continue;
            list.add(itextcomponent);
            i2 = 0;
            itextcomponent = new TextComponentString("");
        }
        list.add(itextcomponent);
        return list;
    }
}

