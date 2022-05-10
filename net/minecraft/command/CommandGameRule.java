package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;

public class CommandGameRule
extends CommandBase {
    @Override
    public String getCommandName() {
        return "gamerule";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.gamerule.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GameRules gamerules = this.getOverWorldGameRules(server);
        String s2 = args.length > 0 ? args[0] : "";
        String s1 = args.length > 1 ? CommandGameRule.buildString(args, 1) : "";
        switch (args.length) {
            case 0: {
                sender.addChatMessage(new TextComponentString(CommandGameRule.joinNiceString(gamerules.getRules())));
                break;
            }
            case 1: {
                if (!gamerules.hasRule(s2)) {
                    throw new CommandException("commands.gamerule.norule", s2);
                }
                String s22 = gamerules.getString(s2);
                sender.addChatMessage(new TextComponentString(s2).appendText(" = ").appendText(s22));
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gamerules.getInt(s2));
                break;
            }
            default: {
                if (gamerules.areSameType(s2, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1)) {
                    throw new CommandException("commands.generic.boolean.invalid", s1);
                }
                gamerules.setOrCreateGameRule(s2, s1);
                CommandGameRule.notifyGameRuleChange(gamerules, s2, server);
                CommandGameRule.notifyCommandListener(sender, (ICommand)this, "commands.gamerule.success", s2, s1);
            }
        }
    }

    public static void notifyGameRuleChange(GameRules rules, String p_184898_1_, MinecraftServer server) {
        if ("reducedDebugInfo".equals(p_184898_1_)) {
            byte b0 = (byte)(rules.getBoolean(p_184898_1_) ? 22 : 23);
            for (EntityPlayerMP entityplayermp : server.getPlayerList().getPlayerList()) {
                entityplayermp.connection.sendPacket(new SPacketEntityStatus(entityplayermp, b0));
            }
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return CommandGameRule.getListOfStringsMatchingLastWord(args, this.getOverWorldGameRules(server).getRules());
        }
        if (args.length == 2) {
            GameRules gamerules = this.getOverWorldGameRules(server);
            if (gamerules.areSameType(args[0], GameRules.ValueType.BOOLEAN_VALUE)) {
                return CommandGameRule.getListOfStringsMatchingLastWord(args, "true", "false");
            }
            if (gamerules.areSameType(args[0], GameRules.ValueType.FUNCTION)) {
                return CommandGameRule.getListOfStringsMatchingLastWord(args, server.func_193030_aL().func_193066_d().keySet());
            }
        }
        return Collections.emptyList();
    }

    private GameRules getOverWorldGameRules(MinecraftServer server) {
        return server.worldServerForDimension(0).getGameRules();
    }
}

