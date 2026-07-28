# Structure System: JSON Format Reference

This reference describes the JSON format used to define multiblock structures. This mod's structures live in `config/omoshiroikamo/structures/`, one file per machine family (`ore_miner.json`, `res_miner.json`, `solar_array.json`, `quantum_beacon.json`). Missing files are regenerated on startup by `DefaultStructureGenerator`, and entries you add by hand are preserved.


## 1. File Structure
A file can contain a single object or an array of objects. A special object named `default` (or `defaults`) can be used to define shared mappings.

## 2. Main Entry Properties

### Since 1.5.1.4, "properties" has been abolished! There is no backward compatibility! 
### Instead, please write it as follows

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | String | Unique identifier (required). |
| `displayName` | String | User-friendly name (optional). |
| `recipeGroup` | String/Array | The recipe groups this structure is compatible with. |
| `mappings` | Object | Character-to-block associations. |
| `layers` | Array | Vertical slices of the structure (top to bottom). |
| `requirements` | Array | Minimum functional needs (e.g., ports). |
| `tintColor` | String | RGB hex color for structure rendering (e.g., `#FF0000`). |
| `speedMultiplier` | Float | Multiplier for processing speed (default: 1.0). |
| `energyMultiplier` | Float | Multiplier for energy consumption (default: 1.0). |
| `batchMin` | Integer | Minimum batch size for recipes (default: 1). |
| `batchMax` | Integer | Maximum batch size for recipes (default: 1). |
| `tier` | Integer | Machine tier (default: 0). |
| `tierStructures` | Array | References to sub-structures that contribute a Tier. |
| `defaultFacing` | String | Default facing is horizontal. You can modify it to vertical (`UP`, `DOWN`). |


### 2.2 Tiered Mappings
Instead of a plain block ID, a symbol can be mapped to a **component** whose Tier depends on which
block was actually placed. Give the mapping a `component` name and a `tiers` table of block ID to Tier:
```json
"mappings": {
  "F": {
    "component": "glass",
    "tiers": {
      "omoshiroikamo:basalt_structure:1": 1,
      "omoshiroikamo:basalt_structure:2": 2
    }
  }
}
```
The resulting Tier per component is exposed on the structure entry, so machine code can read it back
(for example to gate a recipe on `glass` being Tier 2 or better).

### 2.3 Tier Structures
`tierStructures` lists sub-structures that each contribute a Tier to the parent machine. Every entry
takes a `name`, a `tier`, an optional `component` (default `structure`), an optional `mode`
(`tier`, the default, or `count`), and `offsets` describing where the sub-structure sits relative to
the controller:
```json
"tierStructures": [
  { "name": "solarArrayTier2", "tier": 2, "component": "cell", "offsets": [[0, 1, 0]] }
]
```
Each offset is either a `[x, y, z]` triple or an object with `target` and `anchor` triples.

## 3. Mappings
Mappings link characters in `layers` to block IDs.

### String Format
`"F": "omoshiroikamo:basalt_structure:*"` (Wildcard `*` for meta)

### Object Format (Partial Implementation Planned)
```json
"Q": {
  "block": "omoshiroikamo:quantum_ore_extractor:0",
  "max": 1  // * Currently not implemented. Planned to limit the maximum number of installations in the future.
}
```

### Multiple Choices
```json
"A": {
  "blocks": [
    "omoshiroikamo:modifier_null:0",
    "omoshiroikamo:modifier_speed:0"
  ]
}
```

## 4. Requirements
Requirements define what internal components (Ports) the machine must have.

Available types: `itemInput`, `itemOutput`, `fluidInput`, `fluidOutput`, `energyInput`, `energyOutput`, `manaInput`, `manaOutput`, `gasInput`, `gasOutput`, `essentiaInput`, `essentiaOutput`, `visInput`, `visOutput`

### Array Format
```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

### Object Format
Since 1.5.1.4, an object format using type keys is also supported.
```json
"requirements": {
    "energyInput": { "min": 1 },
    "itemOutput": 1,
    "fluidInput": { "min": 1, "max": 4 }
}
```
* If the value is a number, it is treated as `min`.

## 5. Reserved Symbols

The following symbols have special meanings in the structure system.

### 5.1 System Reserved Symbols (Mandatory)
These symbols are used for core system functions and **cannot be overridden** in JSON `mappings`.

| Symbol | Meaning | Description |
| :--- | :--- | :--- |
| `Q` | Controller | Exactly one is required per structure. |
| `_` | Air | Treated as a forced air block. |
| (Space) | Any | Any block (ignored during validation). |

### 5.2 Conventional Reserved Symbols (Conditional)
`A`, `L`, and `G` are conventionally used by this mod's built-in machines.

| Symbol | Meaning | In Built-in Machines |
| :--- | :--- | :--- |
| `A` | Modifier | **Code Priority** |
| `L` | Lens | **Code Priority** |
| `G` | Solar Cell | **Code Priority** |

> [!IMPORTANT]
> For machines like Solar Array or Extractor, these symbols are tied to internal logic (e.g., addon connectivity). Therefore, any definitions in JSON for these symbols will be skipped/protected by the system's code.

## 6. Commands
- `/ok multiblock reload`: Reloads multiblock structure data from JSON.
- `/ok multiblock status`: Shows the current status, including structures that failed to load.
- `/ok multiblock scan <name> <x1> <y1> <z1> <x2> <y2> <z2>`: Scans the given area and writes it out as structure JSON.
- `/ok wand save [force] <name>`: Saves the current structure wand selection as structure JSON.
- `/ok wand clear`: Clears the current wand selection.

