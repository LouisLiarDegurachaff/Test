package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Multiblock management subcommand handler.
 * Handles: /ok multiblock <reload|status|scan>
 * This class is called by CommandOK with args already shifted.
 */
public class CommandMultiblock extends CommandMod {

    public static final String NAME = "multiblock";

    public CommandMultiblock(ModBase mod) {
        super(mod, NAME);

        addSubcommands(CommandMultiblockReload.NAME, new CommandMultiblockReload(mod));
        addSubcommands("status", new CommandMultiblockStatus(mod));
        addSubcommands("scan", new CommandMultiblockScan(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        printUsageTitle(sender, "command.ok.usage_title");
        printSubcommandUsage(sender, "/ok multiblock", "command.ok.help.multiblock.");
    }
}
