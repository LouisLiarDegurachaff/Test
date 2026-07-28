# 構造体システム概要 (Structure System Overview)

OmoshiroiKamo の構造体システムは、高い柔軟性、型安全性、およびコアとなるマシンロジックからの分離を目的に設計されています。

## 1. 設計思想

拡張性を確保するため、主に2つのデザインパターンを採用しています。

- **データとロジックの分離 (Visitor パターン)**:
  構造体定義 (`IStructureEntry`) はデータ（形状、マッピング、要件）のみを保持します。バリデーション、レンダリング、ブロック位置の追跡などのロジックは `IStructureVisitor` を介して実装されます。これにより、データ構造を変更することなく、ホログラムプレビューやティアスキャナーなどの新機能を追加できます。

- **動的な拡張性 (Registry パターン)**:
  「要件（アイテムポート、燃料など）」をハードコードする代わりに、`RequirementRegistry` を使用しています。新しいタイプの要件を実行時に登録できるため、Thaumcraft や Mekanism などの他モジュールが独自の構造的ニーズをシームレスに追加できます。

## 2. 主要コンポーネント

- **`IStructureEntry`**: コアとなるデータインターフェース。単一のマルチブロック定義を表します。
- **`StructureJsonReader` / `StructureJsonWriter`**: JSON ファイルと `IStructureEntry` オブジェクトを相互変換します。リーダー側はデフォルトのマッピングや階層的な定義を処理します。
- **`StructureManager`**: 全てのロードされた構造体を保持し、検索サービスを提供する中央レジストリ。config ディレクトリの管理と、初回起動時の `DefaultStructureGenerator` 実行も担います。
- **`BlockResolver`**: 記号マッピングを StructureLib のエレメントに変換します。ブロック ID・メタデータのワイルドカード・複数候補の解決を行います。
- **`StructureRegistrationVisitor` / `StructureRegistrationUtils`**: 構造体定義から StructureLib の `IStructureDefinition` を組み立てます。コントローラー記号と各マッピングの配線を担当します。
- **`StructureScanner`**: ワールド上の既存の建造物を読み取って形状データに戻します。構造ワンドと `/ok multiblock scan` の実体です。
- **`RequirementRegistry` / `IStructureRequirement`**: 構造体の `requirements` セクションを扱い、マシンが必要な I/O タイプ（ポート）を十分に備えているかをチェックします。
- **`StructureValidationVisitor`**: 登録前に読み込んだ定義を検証し、構文・論理エラーをロード時点で表面化させます。

## 3. モジュールとの関係

- **Multiblock モジュール**: 固定された事前定義済みの構造体名（Solar Array・Quantum Extractor・Quantum Beacon）を使用します。JSON が欠落している場合のハードコードされたフォールバックを提供することが多いです。

