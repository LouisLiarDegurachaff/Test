# 構造体システム: JSON フォーマットリファレンス

このリファレンスでは、マルチブロック構造体を定義するための JSON 形式について説明します。本 Mod の構造体は `config/omoshiroikamo/structures/` に、マシン系統ごと 1 ファイル（`ore_miner.json` / `res_miner.json` / `solar_array.json` / `quantum_beacon.json`）で配置されます。欠けているファイルは起動時に `DefaultStructureGenerator` が再生成し、手書きで追加したエントリはそのまま保持されます。


## 1. ファイル構成
ファイルには単一のオブジェクト、またはオブジェクトの配列を含めることができます。`default`（または `defaults`）という名前の特殊なオブジェクトを使用して、共通のマッピングを定義できます。

## 2. 主要なプロパティ

### ※1.5.1.4以降、"properties"を廃止しました！後方互換性はありません！
### 代わりに、以下の様に入れ子にせずに書いてください

| プロパティ | 型 | 説明 |
| :--- | :--- | :--- |
| `name` | 文字列 | 一意識別子（必須）。 |
| `displayName` | 文字列 | ユーザーフレンドリーな表示名（任意）。 |
| `recipeGroup` | 文字列/配列 | この構造体が対応するレシピグループ。 |
| `mappings` | オブジェクト | 文字記号とブロックの対応。 |
| `layers` | 配列 | 構造体の垂直方向のスライス（上から下へ）。 |
| `requirements` | 配列 | 最小限必要な機能（ポートなど）。 |
| `tintColor` | 文字列 | 構造体のレンダリング色（例: `#FF0000`）。 |
| `speedMultiplier` | Float | 処理速度の乗数（デフォルト: 1.0）。 |
| `energyMultiplier` | Float | エネルギー消費の乗数（デフォルト: 1.0）。 |
| `batchMin` | Integer | レシピの最小バッチサイズ（デフォルト: 1）。 |
| `batchMax` | Integer | レシピの最大バッチサイズ（デフォルト: 1）。 |
| `tier` | Integer | マシンのティア（デフォルト: 0）。 |
| `tierStructures` | 配列 | Tier を供給するサブ構造体への参照。 |
| `defaultFacing` | 文字列 | 構造体のデフォルトの向き（`UP`, `DOWN`）。指定がない場合は横向きになります。 |

### 2.2 Tier 付きマッピング
記号には単なるブロック ID の代わりに「**コンポーネント**」を割り当てることができ、その Tier は実際に
設置されたブロックによって決まります。マッピングに `component` 名と、ブロック ID → Tier の `tiers`
テーブルを与えます。
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
コンポーネントごとに決まった Tier は構造体定義から取得できるため、マシン側のコードで読み戻せます
（例: `glass` が Tier 2 以上であることをレシピの条件にする）。

### 2.3 Tier 構造体 (Tier Structures)
`tierStructures` には、親マシンに Tier を供給するサブ構造体を列挙します。各エントリは `name`、`tier`、
任意の `component`（既定値 `structure`）、任意の `mode`（既定値 `tier`、または `count`）、および
コントローラーからの相対位置を表す `offsets` を取ります。
```json
"tierStructures": [
  { "name": "solarArrayTier2", "tier": 2, "component": "cell", "offsets": [[0, 1, 0]] }
]
```
各 offset は `[x, y, z]` の 3 要素配列、または `target` と `anchor` の 3 要素配列を持つオブジェクトです。

## 3. マッピング (Mappings)
マッピングは、`layers` 内の文字をブロック ID にリンクします。

### 文字列形式
`"F": "omoshiroikamo:basalt_structure:*"` (メタデータにワイルドカード `*` が使用可能)

### オブジェクト形式 (一部実装予定)
```json
"Q": {
  "block": "omoshiroikamo:quantum_ore_extractor:0",
  "max": 1
}
```

### 複数候補の指定
```json
"A": {
  "blocks": [
    "omoshiroikamo:modifier_null:0",
    "omoshiroikamo:modifier_speed:0"
  ]
}
```

## 4. 要件 (Requirements)
要件は、マシンが備えていなければならない内部コンポーネント（ポート）を定義します。

利用可能なタイプ: `itemInput`, `itemOutput`, `fluidInput`, `fluidOutput`, `energyInput`, `energyOutput`, `manaInput`, `manaOutput`, `gasInput`, `gasOutput`, `essentiaInput`, `essentiaOutput`, `visInput`, `visOutput`

### 配列形式
```json
"requirements": [
    { "type": "energyInput", "min": 1 },
    { "type": "itemOutput", "min": 2 }
]
```

### オブジェクト形式
1.5.1.4以降、各タイプをキーとしたオブジェクト形式もサポートされています。
```json
"requirements": {
    "energyInput": { "min": 1 },
    "itemOutput": 1,
    "fluidInput": { "min": 1, "max": 4 }
}
```
※ 値が数値の場合は、`min` として扱われます。

## 5. 予約記号 (Reserved Symbols)

構造体システムでは、以下の記号が特殊な意味を持ちます。

### 5.1 システム予約記号 (必須)
これらの記号はシステムの中核機能で使用され、JSON の `mappings` で**上書きすることはできません**。

| 記号 | 意味 | 説明 |
| :--- | :--- | :--- |
| `Q` | コントローラー | 構造体に必ず1つ必要です。 |
| `_` | 空気 (Air) | 強制的な空気ブロックとして扱われます。 |
| (スペース) | 任意 (Any) | バリデーション対象外の空間です。 |

### 5.2 慣習的予約記号 (条件付き)
`A`, `L`, `G` は本 Mod の組み込みマシンで慣習的に使用されています。

| 記号 | 意味 | 組み込みマシンでの扱い |
| :--- | :--- | :--- |
| `A` | Modifier | **コード定義が優先** |
| `L` | Lens | **コード定義が優先** |
| `G` | Solar Cell | **コード定義が優先** |

> [!IMPORTANT]
> Solar Array や Extractor 等の組み込みマシンでは、これらの記号は内部ロジック（アドオン接続等）と密接に紐付いています。そのため、JSON で定義を書いてもシステム（コード）側の定義によってスキップ/保護されます。

## 6. コマンド
- `/ok multiblock reload`: マルチブロックの構造体データを JSON から再読み込みします。
- `/ok multiblock status`: 現在の状態（読み込みに失敗した構造体を含む）を表示します。
- `/ok multiblock scan <name> <x1> <y1> <z1> <x2> <y2> <z2>`: 指定範囲をスキャンして構造体 JSON として書き出します。
- `/ok wand save [force] <name>`: 構造ワンドの現在の選択範囲を構造体 JSON として保存します。
- `/ok wand clear`: 構造ワンドの選択範囲をクリアします。

