package ruiseki.omoshiroikamo.integration.nei;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry of client-side NEI reload callbacks.
 * Dependent mods (e.g. OK Modular) register a callback here so that
 * {@link ruiseki.omoshiroikamo.core.network.packet.PacketReloadNEI} can
 * trigger their NEI re-registration without this mod knowing about them.
 */
public final class NEIReloadHandlers {

    private static final List<Runnable> HANDLERS = new CopyOnWriteArrayList<>();

    private NEIReloadHandlers() {}

    public static void register(Runnable handler) {
        HANDLERS.add(handler);
    }

    public static void runAll() {
        for (Runnable handler : HANDLERS) {
            handler.run();
        }
    }
}
