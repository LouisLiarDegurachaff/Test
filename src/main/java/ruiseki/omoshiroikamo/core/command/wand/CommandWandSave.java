package ruiseki.omoshiroikamo.core.command.wand;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureConstants;
import ruiseki.omoshiroikamo.core.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.core.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandWandSave extends CommandMod {

    public static final String NAME = "save";

    /** Flag that allows overwriting an existing structure file. */
    public static final String FORCE = "force";

    public CommandWandSave(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        if (args.length < 1) {
            sendColoredMessage(player, EnumChatFormatting.RED, "command.ok.wand_usage");
            return;
        }

        // Only treat "force" as a flag when a name follows it, so a structure can still be named
        // "force".
        boolean force = args.length >= 2 && FORCE.equalsIgnoreCase(args[0]);
        String name = force ? args[1] : args[0];

        if (!StructureScanner.isValidName(name)) {
            sendColoredMessage(player, EnumChatFormatting.RED, "command.ok.wand_invalid_name", name);
            return;
        }

        WandSelectionManager.PendingScan pending = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID());

        if (pending == null) {
            sendColoredMessage(player, EnumChatFormatting.RED, "command.ok.wand_no_pending");
            return;
        }

        if (pending.dimensionId != player.worldObj.provider.dimensionId) {
            sendColoredMessage(player, EnumChatFormatting.RED, "command.ok.wand_different_dimension");
            return;
        }

        int blockCount = pending.getBlockCount();
        if (blockCount > StructureConstants.MAX_WAND_SCAN_BLOCKS) {
            sendColoredMessage(
                player,
                EnumChatFormatting.RED,
                "chat.wand.area_too_large",
                String.format("%,d", StructureConstants.MAX_WAND_SCAN_BLOCKS),
                String.format("%,d", blockCount));
            return;
        }

        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + Reference.MOD_ID);

        File outputFile = StructureScanner.getOutputFile(configDir, name);
        if (outputFile.exists()) {
            if (!force) {
                sendColoredMessage(
                    player,
                    EnumChatFormatting.RED,
                    "command.ok.wand_already_exists",
                    outputFile.getName());
                sendColoredMessage(player, EnumChatFormatting.GRAY, "command.ok.wand_already_exists_hint", name);
                return;
            }
            sendColoredMessage(player, EnumChatFormatting.YELLOW, "command.ok.wand_overwriting", outputFile.getName());
        }

        sendColoredMessage(player, EnumChatFormatting.YELLOW, "command.ok.wand_scanning", blockCount);

        StructureScanner.ScanResult result = StructureScanner.scan(
            player.worldObj,
            name,
            pending.pos1.posX,
            pending.pos1.posY,
            pending.pos1.posZ,
            pending.pos2.posX,
            pending.pos2.posY,
            pending.pos2.posZ,
            configDir);

        if (result.success) {
            sendColoredMessage(player, EnumChatFormatting.GREEN, "command.ok.scan_success", result.message);
            sendColoredMessage(player, EnumChatFormatting.GRAY, "command.ok.scan_file", name);

            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
        } else {
            sendColoredMessage(player, EnumChatFormatting.RED, "command.ok.scan_failed", result.message);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        List<String> completions = new LinkedList<>();
        if (args.length == 1 && FORCE.startsWith(args[0].toLowerCase())) {
            completions.add(FORCE);
        }
        return completions;
    }
}
