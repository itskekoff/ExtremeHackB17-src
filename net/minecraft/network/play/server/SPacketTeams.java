package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class SPacketTeams
implements Packet<INetHandlerPlayClient> {
    private String name = "";
    private String displayName = "";
    private String prefix = "";
    private String suffix = "";
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private final Collection<String> players;
    private int action;
    private int friendlyFlags;

    public SPacketTeams() {
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = Lists.newArrayList();
    }

    public SPacketTeams(ScorePlayerTeam teamIn, int actionIn) {
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = Lists.newArrayList();
        this.name = teamIn.getRegisteredName();
        this.action = actionIn;
        if (actionIn == 0 || actionIn == 2) {
            this.displayName = teamIn.getTeamName();
            this.prefix = teamIn.getColorPrefix();
            this.suffix = teamIn.getColorSuffix();
            this.friendlyFlags = teamIn.getFriendlyFlags();
            this.nameTagVisibility = teamIn.getNameTagVisibility().internalName;
            this.collisionRule = teamIn.getCollisionRule().name;
            this.color = teamIn.getChatFormat().getColorIndex();
        }
        if (actionIn == 0) {
            this.players.addAll(teamIn.getMembershipCollection());
        }
    }

    public SPacketTeams(ScorePlayerTeam teamIn, Collection<String> playersIn, int actionIn) {
        this.nameTagVisibility = Team.EnumVisible.ALWAYS.internalName;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = -1;
        this.players = Lists.newArrayList();
        if (actionIn != 3 && actionIn != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (playersIn == null || playersIn.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.action = actionIn;
        this.name = teamIn.getRegisteredName();
        this.players.addAll(playersIn);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.name = buf2.readStringFromBuffer(16);
        this.action = buf2.readByte();
        if (this.action == 0 || this.action == 2) {
            this.displayName = buf2.readStringFromBuffer(32);
            this.prefix = buf2.readStringFromBuffer(16);
            this.suffix = buf2.readStringFromBuffer(16);
            this.friendlyFlags = buf2.readByte();
            this.nameTagVisibility = buf2.readStringFromBuffer(32);
            this.collisionRule = buf2.readStringFromBuffer(32);
            this.color = buf2.readByte();
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            int i2 = buf2.readVarIntFromBuffer();
            for (int j2 = 0; j2 < i2; ++j2) {
                this.players.add(buf2.readStringFromBuffer(40));
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(this.name);
        buf2.writeByte(this.action);
        if (this.action == 0 || this.action == 2) {
            buf2.writeString(this.displayName);
            buf2.writeString(this.prefix);
            buf2.writeString(this.suffix);
            buf2.writeByte(this.friendlyFlags);
            buf2.writeString(this.nameTagVisibility);
            buf2.writeString(this.collisionRule);
            buf2.writeByte(this.color);
        }
        if (this.action == 0 || this.action == 3 || this.action == 4) {
            buf2.writeVarIntToBuffer(this.players.size());
            for (String s2 : this.players) {
                buf2.writeString(s2);
            }
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTeams(this);
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public int getAction() {
        return this.action;
    }

    public int getFriendlyFlags() {
        return this.friendlyFlags;
    }

    public int getColor() {
        return this.color;
    }

    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }
}

