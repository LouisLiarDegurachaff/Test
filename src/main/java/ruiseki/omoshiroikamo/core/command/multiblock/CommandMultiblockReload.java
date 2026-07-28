package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockModule;

public class CommandMultiblockReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandMultiblockReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sendColoredMessage(sender, EnumChatFormatting.YELLOW, "command.ok.multiblock_reloading");

        MultiBlockModule multiblockModule = getMod().getModuleManager()
            .getModuleByType(MultiBlockModule.class);
        if (multiblockModule == null || !multiblockModule.isEnable()) {
            sendColoredMessage(sender, EnumChatFormatting.RED, "command.ok.multiblock_disabled");
            return;
        }

        try {
            getMod().getModuleManager()
                .getModuleByType(ruiseki.omoshiroikamo.core.CoreModule.class)
                .reload(sender);
            multiblockModule.reload(sender);
        } catch (Exception e) {
            sendColoredMessage(sender, EnumChatFormatting.RED, "command.ok.multiblock_reload_failed", e.getMessage());
            return;
        }

        if (!JsonErrorCollector.getInstance()
            .hasErrors()) {
            sendColoredMessage(sender, EnumChatFormatting.GREEN, "command.ok.multiblock_reload_success");
        }
    }
}
