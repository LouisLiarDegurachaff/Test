package ruiseki.omoshiroikamo.core.command.wand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Structure wand subcommand handler.
 * Handles: /ok wand &lt;save|clear&gt;
 */
public class CommandWand extends CommandMod {

    public static final String NAME = "wand";

    public CommandWand(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandWandClear.NAME, new CommandWandClear(mod));
        addSubcommands(CommandWandSave.NAME, new CommandWandSave(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sendColoredMessage(sender, EnumChatFormatting.RED, "command.ok.wand_players_only");
            return;
        }
        super.processCommand(sender, args);
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        printUsageTitle(sender, "command.ok.usage_title");
        printSubcommandUsage(sender, "/ok wand", "command.ok.help.wand.");
    }
}
