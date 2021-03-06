package ShwepSS.B17.modules;

import java.awt.Color;

public enum Category {
    Combat(new Color(220, 20, 60).getRGB(), new Color(137, 3, 42).getRGB(), "DRAKA"),
    Movement(new Color(123, 104, 238).getRGB(), new Color(73, 63, 151).getRGB(), "MOVE"),
    Visuals(new Color(0, 206, 209).getRGB(), new Color(2, 121, 123).getRGB(), "KRASOTA"),
    Player(new Color(244, 164, 96).getRGB(), new Color(132, 68, 9).getRGB(), "PLAYER"),
    MISC(new Color(60, 179, 113).getRGB(), new Color(28, 88, 57).getRGB(), "ByShwepsik"),
    BOTS(new Color(186, 85, 211).getRGB(), new Color(91, 41, 102).getRGB(), "BOT-ATTACK"),
    Tesla(new Color(186, 85, 211).getRGB(), new Color(91, 41, 102).getRGB(), "TeslaCraft"),
    Theme(new Color(186, 85, 211).getRGB(), new Color(91, 41, 102).getRGB(), "THEMES");

    private final int color;
    private final int colord;
    public String name;

    private Category(int color, int colord, String name) {
        this.color = color;
        this.colord = colord;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public final int getColor() {
        return this.color;
    }

    public final int getColor2() {
        return this.colord;
    }
}

