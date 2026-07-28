# Structure System: Developer Guide

This guide is for developers looking to extend the Structure JSON System via code.

## 1. Registering New Requirements

A requirement answers one question: does the block at these coordinates count towards this type?
Implement `IStructureRequirement`, then register a parser for the JSON type key.

1. **Implement `IStructureRequirement`**:
   ```java
   public class ManaRequirement implements IStructureRequirement {

       private final String type;
       private final int min;
       private final int max;

       public ManaRequirement(String type, int min, int max) {
           this.type = type;
           this.min = min;
           this.max = max;
       }

       @Override
       public String getType() { return type; }

       @Override
       public int getMinCount() { return min; }

       @Override
       public int getMaxCount() { return max; }

       @Override
       public boolean matches(World world, int x, int y, int z) {
           TileEntity te = world.getTileEntity(x, y, z);
           if (!(te instanceof IModularPort)) return false;
           return ((IModularPort) te).getPortType() == IPortType.Type.MANA;
       }

       @Override
       public JsonObject serialize() {
           JsonObject json = new JsonObject();
           json.addProperty("type", type);
           if (min != 0) json.addProperty("min", min);
           if (max != Integer.MAX_VALUE) json.addProperty("max", max);
           return json;
       }

       public static IStructureRequirement fromJson(String type, JsonObject json) {
           int min = json.has("min") ? json.get("min").getAsInt() : 0;
           int max = json.has("max") ? json.get("max").getAsInt() : Integer.MAX_VALUE;
           return new ManaRequirement(type, min, max);
       }
   }
   ```
   One class covers both directions: the `type` string it was constructed with is what distinguishes
   `manaInput` from `manaOutput`.

2. **Register the Parser**:
   The built-in types register themselves in `RequirementRegistry`'s static initializer. Anything you
   add has to be registered before JSON loading happens — `FMLPreInitializationEvent` or
   `FMLInitializationEvent` are both early enough.
   ```java
   RequirementRegistry.register("manaInput", ManaRequirement::fromJson);
   RequirementRegistry.register("manaOutput", ManaRequirement::fromJson);
   ```

## 2. Using Visitors

`IStructureVisitor` has two entry points — one for the definition itself, one for each requirement on it:

```java
public interface IStructureVisitor {
    void visit(IStructureEntry entry);
    void visit(IStructureRequirement requirement);
}
```

`entry.accept(visitor)` calls `visit(entry)` first, then `visit(requirement)` once per requirement.
This is how validation is wired (`StructureValidationVisitor`) and how a definition is turned into a
StructureLib shape (`StructureRegistrationVisitor`).

### Example: Summing Requirement Counts

```java
public class RequirementSummaryVisitor implements IStructureVisitor {

    private final Map<String, Integer> minByType = new HashMap<>();

    @Override
    public void visit(IStructureEntry entry) {
        // Called once, before any requirement. Reset per-entry state here.
        minByType.clear();
    }

    @Override
    public void visit(IStructureRequirement requirement) {
        minByType.merge(requirement.getType(), requirement.getMinCount(), Integer::sum);
    }

    public Map<String, Integer> getMinByType() {
        return minByType;
    }
}
```

## 3. Best Practices

- **Registry Order**: Ensure all requirements are registered before any JSON loading occurs (usually before `postInit`).
- **Encapsulation**: Use `StructureEntryBuilder` instead of creating `StructureEntry` instances directly.
- **Fail Fast**: Use the validation visitor after loading a JSON to catch syntax and logic errors early.

## 4. JSON Reader & Writer

The system provides a unified mechanism for converting between JSON and `StructureEntry` objects.

### StructureJsonReader
Reads JSON elements (entire files or individual structure objects) and converts them into internal objects.
- `readFile(JsonElement)`: Reads an entire file and returns `FileData` containing a list of structures and default mappings.
- `readStructure(JsonObject, Map<String, String>)`: Parses an individual structure object.

### StructureJsonWriter
Serializes `StructureEntry` objects back into `JsonObject`.
- `writeStructure(IStructureEntry)`: Converts a structure definition into JSON format.

Using these classes ensures consistency and avoids manual JSON parsing bugs.

## 5. Testing

To maintain long-term stability, we follow a rigorous testing strategy. See the [Test Plan](./TEST_PLAN.md) for detailed test phases and implementation guidelines.
