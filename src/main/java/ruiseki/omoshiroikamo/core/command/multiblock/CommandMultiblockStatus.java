package ruiseki.omoshiroikamo.core.command.multiblock;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureErrorCollector;
import ruiseki.omoshiroikamo.core.common.structure.StructureException;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandMultiblockStatus extends CommandMod {

    public static final String NAME = "status";

    /** Errors listed in chat before falling back to the errors file. */
    private static final int MAX_LISTED_ERRORS = 5;

    public CommandMultiblockStatus(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        StructureManager manager = StructureManager.getInstance();
        StructureErrorCollector errorCollector = manager.getErrorCollector();

        sendColoredMessage(sender, EnumChatFormatting.AQUA, "command.ok.status_header");

        String initialized = manager.isInitialized()
            ? EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.status_yes")
            : EnumChatFormatting.RED + LangHelpers.localize("command.ok.status_no");
        sendColoredMessage(sender, EnumChatFormatting.WHITE, "command.ok.status_initialized", initialized);

        sendColoredMessage(
            sender,
            EnumChatFormatting.WHITE,
            "command.ok.status_structures",
            manager.getStructureNames()
                .size(),
            manager.getCustomStructureNames()
                .size());

        if (!errorCollector.hasErrors()) {
            sendColoredMessage(
                sender,
                EnumChatFormatting.WHITE,
                "command.ok.status_errors",
                EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.status_none"));
            return;
        }

        sendColoredMessage(
            sender,
            EnumChatFormatting.WHITE,
            "command.ok.status_errors",
            EnumChatFormatting.RED + errorCollector.getSummary());

        List<StructureException> errors = errorCollector.getErrors();
        int listed = Math.min(errors.size(), MAX_LISTED_ERRORS);
        for (int i = 0; i < listed; i++) {
            printLineToChat(
                sender,
                EnumChatFormatting.RED + "  "
                    + (i + 1)
                    + ". "
                    + errors.get(i)
                        .getFormattedMessage());
        }
        if (errors.size() > listed) {
            sendColoredMessage(
                sender,
                EnumChatFormatting.GRAY,
                "command.ok.status_errors_more",
                errors.size() - listed);
        }
        sendColoredMessage(sender, EnumChatFormatting.GRAY, "command.ok.status_errors_file");
    }
}
