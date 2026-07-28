package ruiseki.omoshiroikamo.core.command;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * The mod base command.
 *
 * @author rubensworks
 *
 */
public class CommandMod implements ICommand {

    private final ModBase mod;
    private final Map<String, ICommand> subCommands;
    private final List<String> aliases = Lists.newLinkedList();

    public CommandMod(ModBase mod, Map<String, ICommand> subCommands) {
        this.mod = mod;
        this.subCommands = subCommands;
        this.subCommands.put(CommandVersion.NAME, new CommandVersion(mod));
        addAlias(mod.getModId());
    }

    public CommandMod(ModBase mod) {
        this.mod = mod;
        this.subCommands = Maps.newHashMap();
    }

    public CommandMod(ModBase mod, String name) {
        this(mod);
        addAlias(name);
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
    }

    protected ModBase getMod() {
        return this.mod;
    }

    protected List<String> getAliases() {
        return aliases;
    }

    public Map<String, ICommand> getSubcommands() {
        return subCommands;
    }

    public void addSubcommands(String name, ICommand command) {
        subCommands.put(name, command);
    }

    /**
     * @return The names of all registered subcommands, alphabetically sorted so that help output is stable.
     */
    protected List<String> getSortedSubcommandNames() {
        List<String> names = Lists.newArrayList(getSubcommands().keySet());
        Collections.sort(names);
        return names;
    }

    private List<String> getSubCommands(String cmd) {
        List<String> completions = new LinkedList<>();
        for (String full : getSubcommands().keySet()) {
            if (full.startsWith(cmd)) {
                completions.add(full);
            }
        }
        return completions;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return mod.getModId();
    }

    /**
     * @return Recursively returns the whole command string up to the current subcommand.
     */
    public String getFullCommand() {
        return getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        String command = "/" + getFullCommand();
        Iterator<String> it = getSubcommands().keySet()
            .iterator();
        return it.hasNext() ? joinStrings(command + " <", it, " | ", ">") : command;
    }

    @Override
    public List<String> getCommandAliases() {
        return this.getAliases();
    }

    protected String[] shortenArgumentList(String[] astring) {
        String[] asubstring = new String[astring.length - 1];
        System.arraycopy(astring, 1, asubstring, 0, astring.length - 1);
        return asubstring;
    }

    /**
     * This method is called when the user uses the command in an incorrect way. This command
     * should either print out a friendly message explaining how to use the given (sub)command,
     * or throw a CommandException with extra helpful information.
     *
     * @param sender Use this commandsender to print chat message.
     * @param args   The list of strings that were entered as subcommands to the current command by the user.
     * @throws CommandException Thrown when the user entered something wrong, should contain some helpful information as
     *                          to what wrong.
     */
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            processCommandHelp(sender, args);
        } else {
            ICommand subcommand = getSubcommands().get(args[0]);
            if (subcommand != null) {
                String[] asubstring = shortenArgumentList(args);
                subcommand.processCommand(sender, asubstring);
            } else {
                throw new WrongUsageException(LangHelpers.localize("command.ok.invalidSubcommand"));
            }
        }
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length != 0) {
            ICommand subcommand = getSubcommands().get(args[0]);
            if (subcommand != null) {
                String[] asubstring = shortenArgumentList(args);
                return subcommand.addTabCompletionOptions(sender, asubstring);
            } else {
                return getSubCommands(args[0]);
            }
        } else {
            return getSubCommands("");
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return false;
    }

    // == Helper functions ==//

    protected String joinStrings(Iterator<String> it, String delim) {
        return joinStrings("", it, delim, "");
    }

    protected String joinStrings(String prefix, Iterator<String> it, String delim, String suffix) {
        StringBuilder builder = new StringBuilder(prefix);

        if (it.hasNext()) {
            builder.append(it.next());
            while (it.hasNext()) {
                builder.append(delim);
                builder.append(it.next());
            }
        }

        builder.append(suffix);

        return builder.toString();
    }

    protected void printLineToChat(ICommandSender sender, String line) {
        sender.addChatMessage(new ChatComponentText(line));
    }

    protected void sendLocalizedMessage(ICommandSender sender, String key, Object... params) {
        printLineToChat(sender, LangHelpers.localize(key, params));
    }

    /**
     * Send a localized message in the given color.
     * A color must never be passed as a format parameter instead: keys without a placeholder drop it
     * silently, keys with a typed placeholder such as {@code %d} turn the whole line into a format
     * error, and keys with a single {@code %s} show the color code where the value belongs.
     *
     * @param sender Use this commandsender to print chat message.
     * @param color  The color of the whole line.
     * @param key    The l10n key of the message.
     * @param params The parameters of the formatting.
     */
    protected void sendColoredMessage(ICommandSender sender, EnumChatFormatting color, String key, Object... params) {
        printLineToChat(sender, color + LangHelpers.localize(key, params));
    }

    /**
     * Print the header line of a help listing.
     *
     * @param sender Use this commandsender to print chat message.
     * @param key    The l10n key of the title.
     */
    protected void printUsageTitle(ICommandSender sender, String key) {
        sendColoredMessage(sender, EnumChatFormatting.YELLOW, key);
    }

    /**
     * Print one help line per registered subcommand, so that the listing can never drift away from
     * what is actually registered. Subcommands that have subcommands of their own get those listed
     * as {@code <a|b>}; for the others the optional l10n key {@code <helpKeyPrefix><name>.args}
     * supplies the argument list. The optional key {@code <helpKeyPrefix><name>.desc} supplies the
     * description. A subcommand without any of those entries is still listed, just bare.
     *
     * @param sender        Use this commandsender to print chat message.
     * @param commandPath   The command path up to this command, for example {@code "/ok multiblock"}.
     * @param helpKeyPrefix The l10n key prefix of this command's subcommand entries.
     */
    protected void printSubcommandUsage(ICommandSender sender, String commandPath, String helpKeyPrefix) {
        for (String name : getSortedSubcommandNames()) {
            StringBuilder line = new StringBuilder("  ").append(commandPath)
                .append(' ')
                .append(name);

            ICommand subcommand = getSubcommands().get(name);
            List<String> nested = subcommand instanceof CommandMod
                ? ((CommandMod) subcommand).getSortedSubcommandNames()
                : Collections.emptyList();
            if (!nested.isEmpty()) {
                line.append(" <")
                    .append(joinStrings(nested.iterator(), "|"))
                    .append('>');
            } else if (LangHelpers.canLocalize(helpKeyPrefix + name + ".args")) {
                line.append(' ')
                    .append(LangHelpers.localize(helpKeyPrefix + name + ".args"));
            }

            if (LangHelpers.canLocalize(helpKeyPrefix + name + ".desc")) {
                line.append(" - ")
                    .append(LangHelpers.localize(helpKeyPrefix + name + ".desc"));
            }

            printLineToChat(sender, EnumChatFormatting.WHITE + line.toString());
        }
    }
}
