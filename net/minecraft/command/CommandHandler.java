package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CommandHandler
implements ICommandManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, ICommand> commandMap = Maps.newHashMap();
    private final Set<ICommand> commandSet = Sets.newHashSet();

    @Override
    public int executeCommand(ICommandSender sender, String rawCommand) {
        if ((rawCommand = rawCommand.trim()).startsWith("/")) {
            rawCommand = rawCommand.substring(1);
        }
        String[] astring = rawCommand.split(" ");
        String s2 = astring[0];
        astring = CommandHandler.dropFirstString(astring);
        ICommand icommand = this.commandMap.get(s2);
        int i2 = 0;
        try {
            int j2 = this.getUsernameIndex(icommand, astring);
            if (icommand == null) {
                TextComponentTranslation textcomponenttranslation1 = new TextComponentTranslation("commands.generic.notFound", new Object[0]);
                textcomponenttranslation1.getStyle().setColor(TextFormatting.RED);
                sender.addChatMessage(textcomponenttranslation1);
            } else if (icommand.checkPermission(this.getServer(), sender)) {
                if (j2 > -1) {
                    List<Entity> list = EntitySelector.matchEntities(sender, astring[j2], Entity.class);
                    String s1 = astring[j2];
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
                    if (list.isEmpty()) {
                        throw new PlayerNotFoundException("commands.generic.selector.notFound", astring[j2]);
                    }
                    for (Entity entity : list) {
                        astring[j2] = entity.getCachedUniqueIdString();
                        if (!this.tryExecute(sender, astring, icommand, rawCommand)) continue;
                        ++i2;
                    }
                    astring[j2] = s1;
                } else {
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
                    if (this.tryExecute(sender, astring, icommand, rawCommand)) {
                        ++i2;
                    }
                }
            } else {
                TextComponentTranslation textcomponenttranslation2 = new TextComponentTranslation("commands.generic.permission", new Object[0]);
                textcomponenttranslation2.getStyle().setColor(TextFormatting.RED);
                sender.addChatMessage(textcomponenttranslation2);
            }
        }
        catch (CommandException commandexception) {
            TextComponentTranslation textcomponenttranslation = new TextComponentTranslation(commandexception.getMessage(), commandexception.getErrorObjects());
            textcomponenttranslation.getStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(textcomponenttranslation);
        }
        sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, i2);
        return i2;
    }

    protected boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input) {
        try {
            command.execute(this.getServer(), sender, args);
            return true;
        }
        catch (WrongUsageException wrongusageexception) {
            TextComponentTranslation textcomponenttranslation2 = new TextComponentTranslation("commands.generic.usage", new TextComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorObjects()));
            textcomponenttranslation2.getStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(textcomponenttranslation2);
        }
        catch (CommandException commandexception) {
            TextComponentTranslation textcomponenttranslation1 = new TextComponentTranslation(commandexception.getMessage(), commandexception.getErrorObjects());
            textcomponenttranslation1.getStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(textcomponenttranslation1);
        }
        catch (Throwable throwable) {
            TextComponentTranslation textcomponenttranslation = new TextComponentTranslation("commands.generic.exception", new Object[0]);
            textcomponenttranslation.getStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(textcomponenttranslation);
            LOGGER.warn("Couldn't process command: " + input, throwable);
        }
        return false;
    }

    protected abstract MinecraftServer getServer();

    public ICommand registerCommand(ICommand command) {
        this.commandMap.put(command.getCommandName(), command);
        this.commandSet.add(command);
        for (String s2 : command.getCommandAliases()) {
            ICommand icommand = this.commandMap.get(s2);
            if (icommand != null && icommand.getCommandName().equals(s2)) continue;
            this.commandMap.put(s2, command);
        }
        return command;
    }

    private static String[] dropFirstString(String[] input) {
        String[] astring = new String[input.length - 1];
        System.arraycopy(input, 1, astring, 0, input.length - 1);
        return astring;
    }

    @Override
    public List<String> getTabCompletionOptions(ICommandSender sender, String input, @Nullable BlockPos pos) {
        ICommand icommand;
        String[] astring = input.split(" ", -1);
        String s2 = astring[0];
        if (astring.length == 1) {
            ArrayList<String> list = Lists.newArrayList();
            for (Map.Entry<String, ICommand> entry : this.commandMap.entrySet()) {
                if (!CommandBase.doesStringStartWith(s2, entry.getKey()) || !entry.getValue().checkPermission(this.getServer(), sender)) continue;
                list.add(entry.getKey());
            }
            return list;
        }
        if (astring.length > 1 && (icommand = this.commandMap.get(s2)) != null && icommand.checkPermission(this.getServer(), sender)) {
            return icommand.getTabCompletionOptions(this.getServer(), sender, CommandHandler.dropFirstString(astring), pos);
        }
        return Collections.emptyList();
    }

    @Override
    public List<ICommand> getPossibleCommands(ICommandSender sender) {
        ArrayList<ICommand> list = Lists.newArrayList();
        for (ICommand icommand : this.commandSet) {
            if (!icommand.checkPermission(this.getServer(), sender)) continue;
            list.add(icommand);
        }
        return list;
    }

    @Override
    public Map<String, ICommand> getCommands() {
        return this.commandMap;
    }

    private int getUsernameIndex(ICommand command, String[] args) throws CommandException {
        if (command == null) {
            return -1;
        }
        for (int i2 = 0; i2 < args.length; ++i2) {
            if (!command.isUsernameIndex(args, i2) || !EntitySelector.matchesMultiplePlayers(args[i2])) continue;
            return i2;
        }
        return -1;
    }
}

