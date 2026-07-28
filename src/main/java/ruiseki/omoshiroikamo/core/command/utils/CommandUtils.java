package ruiseki.omoshiroikamo.core.command.utils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Utility subcommand handler.
 * Handles: /ok utils &lt;dump&gt;
 */
public class CommandUtils extends CommandMod {

    public static final String NAME = "utils";

    public CommandUtils(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandDump.NAME, new CommandDump(mod));
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        printUsageTitle(sender, "command.ok.usage_title");
        printSubcommandUsage(sender, "/ok utils", "command.ok.help.utils.");
    }
}
