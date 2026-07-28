# 構造体システム: 開発者ガイド

このガイドは、コードを介して構造体 JSON システムを拡張しようとする開発者を対象としています。

## 1. 新しい要件 (Requirement) の登録

要件が答えるのは「この座標のブロックはこのタイプとして数えられるか」という 1 点だけです。
`IStructureRequirement` を実装し、JSON のタイプキーに対応するパーサーを登録します。

1. **`IStructureRequirement` を実装する**:
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
   入出力の両方向を 1 クラスで賄います。`manaInput` と `manaOutput` を区別しているのは、
   コンストラクタに渡された `type` 文字列です。

2. **パーサーを登録する**:
   組み込みのタイプは `RequirementRegistry` の static イニシャライザで自己登録されています。
   独自に追加するものは JSON の読み込みより前に登録する必要があり、
   `FMLPreInitializationEvent` / `FMLInitializationEvent` はどちらも十分に早い段階です。
   ```java
   RequirementRegistry.register("manaInput", ManaRequirement::fromJson);
   RequirementRegistry.register("manaOutput", ManaRequirement::fromJson);
   ```

## 2. Visitor の使用

`IStructureVisitor` には 2 つの入口があります。構造体定義そのもの用と、そこに付いた各要件用です。

```java
public interface IStructureVisitor {
    void visit(IStructureEntry entry);
    void visit(IStructureRequirement requirement);
}
```

`entry.accept(visitor)` はまず `visit(entry)` を呼び、続いて要件ごとに `visit(requirement)` を呼びます。
バリデーション（`StructureValidationVisitor`）と、定義から StructureLib の形状を組み立てる処理
（`StructureRegistrationVisitor`）はどちらもこの仕組みに乗っています。

### 例: 要件の個数を集計する

```java
public class RequirementSummaryVisitor implements IStructureVisitor {

    private final Map<String, Integer> minByType = new HashMap<>();

    @Override
    public void visit(IStructureEntry entry) {
        // 要件より前に 1 度だけ呼ばれる。エントリ単位の状態はここでリセットする
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

## 3. ベストプラクティス

- **登録順序**: JSON の読み込みが発生する前に、すべての要件が登録されていることを確認してください（通常は `postInit` より前）。
- **カプセル化**: `StructureEntry` インスタンスを直接作成するのではなく、`StructureEntryBuilder` を使用してください。
- **早期失敗 (Fail Fast)**: JSON をロードした直後にバリデーション Visitor を使用して、構文や論理的なエラーを早期にキャッチしてください。

## 4. JSON Reader & Writer

システムには、JSON と `StructureEntry` オブジェクトを相互変換するための統一された仕組みが用意されています。

### StructureJsonReader
JSON 要素（ファイル全体または個別の構造体オブジェクト）を読み込み、内部オブジェクトに変換します。
- `readFile(JsonElement)`: ファイル全体を読み込み、`FileData`（構造体リストとデフォルトマッピングを含む）を返します。
- `readStructure(JsonObject, Map<String, String>)`: 個別の構造体オブジェクトをパースします。

### StructureJsonWriter
`StructureEntry` オブジェクトを `JsonObject` に書き戻します。
- `writeStructure(IStructureEntry)`: 構造体定義を JSON 形式に変換します。

これらのクラスを使用することで、手動での JSON パースを避け、システムの整合性を保つことができます。

## 5. テスト

長期的な安定性を維持するため、厳格なテスト戦略を採用しています。詳細なテストフェーズおよび実装ガイドラインについては、[テスト計画](./TEST_PLAN.md) を参照してください。
