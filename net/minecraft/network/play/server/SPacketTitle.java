package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketTitle
implements Packet<INetHandlerPlayClient> {
    private Type type;
    private ITextComponent message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;

    public SPacketTitle() {
    }

    public SPacketTitle(Type typeIn, ITextComponent messageIn) {
        this(typeIn, messageIn, -1, -1, -1);
    }

    public SPacketTitle(int fadeInTimeIn, int displayTimeIn, int fadeOutTimeIn) {
        this(Type.TIMES, null, fadeInTimeIn, displayTimeIn, fadeOutTimeIn);
    }

    public SPacketTitle(Type typeIn, @Nullable ITextComponent messageIn, int fadeInTimeIn, int displayTimeIn, int fadeOutTimeIn) {
        this.type = typeIn;
        this.message = messageIn;
        this.fadeInTime = fadeInTimeIn;
        this.displayTime = displayTimeIn;
        this.fadeOutTime = fadeOutTimeIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.type = buf2.readEnumValue(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            this.message = buf2.readTextComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = buf2.readInt();
            this.displayTime = buf2.readInt();
            this.fadeOutTime = buf2.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            buf2.writeTextComponent(this.message);
        }
        if (this.type == Type.TIMES) {
            buf2.writeInt(this.fadeInTime);
            buf2.writeInt(this.displayTime);
            buf2.writeInt(this.fadeOutTime);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTitle(this);
    }

    public Type getType() {
        return this.type;
    }

    public ITextComponent getMessage() {
        return this.message;
    }

    public int getFadeInTime() {
        return this.fadeInTime;
    }

    public int getDisplayTime() {
        return this.displayTime;
    }

    public int getFadeOutTime() {
        return this.fadeOutTime;
    }

    public static enum Type {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;


        public static Type byName(String name) {
            for (Type spackettitle$type : Type.values()) {
                if (!spackettitle$type.name().equalsIgnoreCase(name)) continue;
                return spackettitle$type;
            }
            return TITLE;
        }

        public static String[] getNames() {
            String[] astring = new String[Type.values().length];
            int i2 = 0;
            for (Type spackettitle$type : Type.values()) {
                astring[i2++] = spackettitle$type.name().toLowerCase(Locale.ROOT);
            }
            return astring;
        }
    }
}

