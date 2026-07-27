package ruiseki.omoshiroikamo.core.energy.capability;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Delegate interface for mod-specific energy integrations.
 * Allows extending ExternalEnergyProxy with custom energy system support
 * without coupling the core proxy logic to specific mod APIs.
 *
 * <p>
 * Each delegate is only loaded when its corresponding mod is present.
 * Delegates are registered during mod integration initialization and are
 * checked in priority order.
 *
 * <p>
 * <b>Usage Pattern:</b>
 * <ol>
 * <li>Mod integration checks if mod is loaded ({@code LibMods.ModName.isLoaded()})</li>
 * <li>If loaded, registers delegate via
 * {@link EnergyIntegrationRegistry#registerDelegate(IEnergyIntegrationDelegate)}</li>
 * <li>ExternalEnergyProxy iterates through delegates in priority order</li>
 * <li>First delegate that returns non-null handles the operation</li>
 * </ol>
 *
 * <p>
 * <b>Example:</b>
 * 
 * <pre>
 * {@code
 * // In a module's integration hook
 * if (LibMods.EnderIO.isLoaded()) {
 *     EnergyIntegrationRegistry.registerDelegate(new IEnergyIntegrationDelegate() {
 *         public Integer tryExtract(Object te, ForgeDirection side, int amount, boolean simulate) {
 *             return EnderIOIntegration.tryExtract(te, side, amount, simulate);
 *         }
 *         // ... other methods
 *         public int getPriority() { return 10; }
 *     });
 * }
 * }
 * </pre>
 */
public interface IEnergyIntegrationDelegate {

    /**
     * Try to extract energy from a tile entity using this integration.
     *
     * @param te       The tile entity (Object to avoid ClassNotFoundError)
     * @param side     The side to extract from
     * @param amount   Amount to extract
     * @param simulate Whether to simulate
     * @return Amount extracted, or null if this delegate doesn't handle this tile
     */
    Integer tryExtract(Object te, ForgeDirection side, int amount, boolean simulate);

    /**
     * Try to insert energy into a tile entity using this integration.
     *
     * @param te       The tile entity (Object to avoid ClassNotFoundError)
     * @param side     The side to insert to
     * @param amount   Amount to insert
     * @param simulate Whether to simulate
     * @return Amount inserted, or null if this delegate doesn't handle this tile
     */
    Integer tryReceive(Object te, ForgeDirection side, int amount, boolean simulate);

    /**
     * Get stored energy from a tile entity using this integration.
     *
     * @param te The tile entity
     * @return Stored energy, or null if this delegate doesn't handle this tile
     */
    Integer getEnergyStored(Object te);

    /**
     * Get max stored energy from a tile entity using this integration.
     *
     * @param te The tile entity
     * @return Max stored energy, or null if this delegate doesn't handle this tile
     */
    Integer getMaxEnergyStored(Object te);

    /**
     * Priority for this delegate. Higher priority delegates are checked first.
     * Used to establish fallback order (IOKEnergy > EnderIO > CoFH RF).
     *
     * <p>
     * <b>Recommended Priority Values:</b>
     * <ul>
     * <li>100+: Internal systems (IOKEnergy - implicit, not delegated)</li>
     * <li>10-99: Advanced mod APIs (EnderIO, IC2, GregTech)</li>
     * <li>0-9: Standard APIs (CoFH RF - default)</li>
     * </ul>
     *
     * @return Priority value (higher = checked earlier)
     */
    default int getPriority() {
        return 0;
    }
}
