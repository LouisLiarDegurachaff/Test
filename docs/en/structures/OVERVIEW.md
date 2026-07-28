# Structure System: Overview

The Structure JSON System in OmoshiroiKamo is designed to be highly flexible, type-safe, and decoupled from the core machine logic.

## 1. Design Philosophy

The system follows two primary design patterns to ensure scalability:

- **Separation of Data and Logic (Visitor Pattern)**:
  Structure definitions (`IStructureEntry`) only hold the data (shape, mappings, requirements). The logic for validation, rendering, and block position tracking is implemented via `IStructureVisitor`. This allows adding new functionality (like a hologram previewer or a tier scanner) without changing the data structure.
  
- **Dynamic Extensibility (Registry Pattern)**:
  Instead of hardcoding what a "requirement" is (e.g., item ports, fuel), we use the `RequirementRegistry`. New types of requirements can be registered at runtime, allowing modules (like Thaumcraft or Mekanism) to add their own structural needs seamlessly.

## 2. Core Components

- **`IStructureEntry`**: The core data interface. It represents a single multiblock definition.
- **`StructureJsonReader` / `StructureJsonWriter`**: Convert between JSON files and `IStructureEntry` objects. The reader handles default mappings and hierarchical definitions.
- **`StructureManager`**: The central registry that stores all loaded structures and provides lookup services. It also owns the config directory and triggers `DefaultStructureGenerator` on first run.
- **`BlockResolver`**: Turns a symbol mapping into a StructureLib element, resolving block IDs, metadata wildcards and multi-choice mappings.
- **`StructureRegistrationVisitor` / `StructureRegistrationUtils`**: Build a StructureLib `IStructureDefinition` out of an entry, wiring the controller symbol and every mapped element.
- **`StructureScanner`**: Reads an existing build in the world back into a shape. This is what backs the structure wand and `/ok multiblock scan`.
- **`RequirementRegistry` / `IStructureRequirement`**: Handle the `requirements` section of a structure, checking whether the machine has enough of the required I/O types.
- **`StructureValidationVisitor`**: Validates a loaded entry before it is registered, so syntax and logic errors surface at load time.

## 3. Module Relationship

- **Multiblock Module**: Uses fixed, predefined structure names (Solar Array, Quantum Extractor, Quantum Beacon). It often provides a hardcoded fallback if the JSON is missing.

