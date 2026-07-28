package ruiseki.omoshiroikamo.module.dml.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * DML subcommand handler.
 * Handles: /ok dml <reload>
 */
public class CommandDML extends CommandMod {

    public static final String NAME = "dml";

    public CommandDML(ModBase mod) {
        super(mod);
        addSubcommands(CommandDMLReload.NAME, new CommandDMLReload(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        printUsageTitle(sender, "command.ok.usage_title");
        printSubcommandUsage(sender, "/ok dml", "command.ok.help.dml.");
    }
}
