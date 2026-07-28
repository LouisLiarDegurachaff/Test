package ruiseki.omoshiroikamo.core.command.utils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.module.dml.common.command.CommandMobDump;

/**
 * Data dump subcommand handler.
 * Handles: /ok utils dump &lt;mobs&gt;
 */
public class CommandDump extends CommandMod {

    public static final String NAME = "dump";

    public CommandDump(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandMobDump.NAME, new CommandMobDump(mod));
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        printUsageTitle(sender, "command.ok.usage_title");
        printSubcommandUsage(sender, "/ok utils dump", "command.ok.help.utils.dump.");
    }
}
