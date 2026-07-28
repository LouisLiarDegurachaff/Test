package ruiseki.omoshiroikamo.core.command.wand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandWandClear extends CommandMod {

    public static final String NAME = "clear";

    public CommandWandClear(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        if (WandSelectionManager.getInstance()
            .hasPendingScan(player.getUniqueID())) {
            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
            sendColoredMessage(player, EnumChatFormatting.GREEN, "command.ok.wand_cleared");
        } else {
            sendColoredMessage(player, EnumChatFormatting.GRAY, "command.ok.wand_no_selection");
        }
    }
}
