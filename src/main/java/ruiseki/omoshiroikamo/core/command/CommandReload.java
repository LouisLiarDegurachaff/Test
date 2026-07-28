package ruiseki.omoshiroikamo.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;

/**
 * /ok reload — reloads all enabled modules at once.
 */
public class CommandReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        JsonErrorCollector.getInstance()
            .clear();
        sendColoredMessage(sender, EnumChatFormatting.YELLOW, "command.ok.reload_all");

        getMod().getModuleManager()
            .reloadAll(sender);

        if (JsonErrorCollector.getInstance()
            .hasErrors()) {
            JsonErrorCollector.getInstance()
                .writeToFile();
            JsonErrorCollector.getInstance()
                .reportToChat(sender);
        } else {
            sendColoredMessage(sender, EnumChatFormatting.GREEN, "command.ok.reload_all_success");
        }
    }
}
