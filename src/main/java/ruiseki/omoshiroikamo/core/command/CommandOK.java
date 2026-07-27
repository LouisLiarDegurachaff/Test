package ruiseki.omoshiroikamo.core.command;

import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.utils.CommandUtils;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Main command handler for /ok
 * Delegates to subcommand handlers for extensibility.
 */
public class CommandOK extends CommandMod {

    public CommandOK(ModBase mod, Map<String, ICommand> subCommands) {
        super(mod, subCommands);
        addSubcommands(CommandUtils.NAME, new CommandUtils(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP required
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sendLocalizedMessage(sender, "command.ok.main_usage_title", EnumChatFormatting.YELLOW);
        sendLocalizedMessage(sender, "command.ok.main_usage_multiblock", EnumChatFormatting.WHITE.toString() + "  ");
        sendLocalizedMessage(
            sender,
            "command.ok.main_usage_multiblock_reload",
            EnumChatFormatting.WHITE.toString() + "  ");
    }
}
