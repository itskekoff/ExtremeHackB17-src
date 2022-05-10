package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class CommandTime
extends CommandBase {
    @Override
    public String getCommandName() {
        return "time";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.time.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            if ("set".equals(args[0])) {
                int i1 = "day".equals(args[1]) ? 1000 : ("night".equals(args[1]) ? 13000 : CommandTime.parseInt(args[1], 0));
                this.setAllWorldTimes(server, i1);
                CommandTime.notifyCommandListener(sender, (ICommand)this, "commands.time.set", i1);
                return;
            }
            if ("add".equals(args[0])) {
                int l2 = CommandTime.parseInt(args[1], 0);
                this.incrementAllWorldTimes(server, l2);
                CommandTime.notifyCommandListener(sender, (ICommand)this, "commands.time.added", l2);
                return;
            }
            if ("query".equals(args[0])) {
                if ("daytime".equals(args[1])) {
                    int k2 = (int)(sender.getEntityWorld().getWorldTime() % 24000L);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, k2);
                    CommandTime.notifyCommandListener(sender, (ICommand)this, "commands.time.query", k2);
                    return;
                }
                if ("day".equals(args[1])) {
                    int j2 = (int)(sender.getEntityWorld().getWorldTime() / 24000L % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, j2);
                    CommandTime.notifyCommandListener(sender, (ICommand)this, "commands.time.query", j2);
                    return;
                }
                if ("gametime".equals(args[1])) {
                    int i2 = (int)(sender.getEntityWorld().getTotalWorldTime() % Integer.MAX_VALUE);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, i2);
                    CommandTime.notifyCommandListener(sender, (ICommand)this, "commands.time.query", i2);
                    return;
                }
            }
        }
        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return CommandTime.getListOfStringsMatchingLastWord(args, "set", "add", "query");
        }
        if (args.length == 2 && "set".equals(args[0])) {
            return CommandTime.getListOfStringsMatchingLastWord(args, "day", "night");
        }
        return args.length == 2 && "query".equals(args[0]) ? CommandTime.getListOfStringsMatchingLastWord(args, "daytime", "gametime", "day") : Collections.emptyList();
    }

    protected void setAllWorldTimes(MinecraftServer server, int time) {
        for (int i2 = 0; i2 < server.worldServers.length; ++i2) {
            server.worldServers[i2].setWorldTime(time);
        }
    }

    protected void incrementAllWorldTimes(MinecraftServer server, int amount) {
        for (int i2 = 0; i2 < server.worldServers.length; ++i2) {
            WorldServer worldserver = server.worldServers[i2];
            worldserver.setWorldTime(worldserver.getWorldTime() + (long)amount);
        }
    }
}

