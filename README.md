# AntiCrash

**Minecraft Fabric server mod** · by AtsukiMC · **LGPL-3.0-only**

AntiCrash creates configurable checkpoints and helps recover a server after an unexpected shutdown. A restored world can stay frozen until an administrator resumes it with `/ac start`.

AntiCrash is intended for Technical Minecraft, but it cannot guarantee exact replay of every vanilla, modded, asynchronous, or thread-scheduled state. Keep independent backups and test important machines on a copy of the world.

---

## English

### Latest maintenance update (2026-10-30)

All buildable targets include the FakePlayer checkpoint-identity repair: restoration uses the saved UUID and name, rejects collisions with online players before creation, and reports the expected and actual UUID on mismatch. Profile-cache adapters match each Minecraft version. Carpet's supported asynchronous creation paths use the saved profile only during recovery; ordinary profile lookups and logins are unchanged. Checkpoint formats and the mod version (`1.0.0`) are unchanged. These are offline-verified builds, not new live-server compatibility certifications.

### Supported versions

The 30 versions marked **Build verified** have a version-specific Mod JAR in the repository's `build/` directory. Build verification means compilation, unit tests, and artifact checks passed; it is **not** live-server compatibility qualification. **Minecraft 1.16.1 is still being ported and has no usable JAR.**

| Minecraft | Mod JAR | Fabric Loader | Java | Status |
| --- | --- | --- | --- | --- |
| 1.18 | `AntiCrash-1.0.0+mc1.18.jar` | 0.17.3+ | 17+ | Build verified |
| 1.18.1 | `AntiCrash-1.0.0+mc1.18.1.jar` | 0.17.3+ | 17+ | Build verified |
| 1.18.2 | `AntiCrash-1.0.0+mc1.18.2.jar` | 0.17.3+ | 17+ | Build verified |
| 1.19 | `AntiCrash-1.0.0+mc1.19.jar` | 0.17.3+ | 17+ | Build verified |
| 1.19.1 | `AntiCrash-1.0.0+mc1.19.1.jar` | 0.17.3+ | 17+ | Build verified |
| 1.19.2 | `AntiCrash-1.0.0+mc1.19.2.jar` | 0.17.3+ | 17+ | Build verified |
| 1.19.3 | `AntiCrash-1.0.0+mc1.19.3.jar` | 0.17.3+ | 17+ | Build verified |
| 1.19.4 | `AntiCrash-1.0.0+mc1.19.4.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20 | `AntiCrash-1.0.0+mc1.20.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20.1 | `AntiCrash-1.0.0+mc1.20.1.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20.2 | `AntiCrash-1.0.0+mc1.20.2.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20.3 | `AntiCrash-1.0.0+mc1.20.3.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20.4 | `AntiCrash-1.0.0+mc1.20.4.jar` | 0.17.3+ | 17+ | Build verified |
| 1.20.5 | `AntiCrash-1.0.0+mc1.20.5.jar` | 0.17.3+ | 21+ | Build verified |
| 1.20.6 | `AntiCrash-1.0.0+mc1.20.6.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21 | `AntiCrash-1.0.0+mc1.21.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.1 | `AntiCrash-1.0.0+mc1.21.1.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.2 | `AntiCrash-1.0.0+mc1.21.2.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.3 | `AntiCrash-1.0.0+mc1.21.3.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.4 | `AntiCrash-1.0.0+mc1.21.4.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.5 | `AntiCrash-1.0.0+mc1.21.5.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.6 | `AntiCrash-1.0.0+mc1.21.6.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.7 | `AntiCrash-1.0.0+mc1.21.7.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.8 | `AntiCrash-1.0.0+mc1.21.8.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.9 | `AntiCrash-1.0.0+mc1.21.9.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.10 | `AntiCrash-1.0.0+mc1.21.10.jar` | 0.17.3+ | 21+ | Build verified |
| 1.21.11 | `AntiCrash-1.0.0+mc1.21.11.jar` | 0.18.4+ | 21+ | Build verified |
| 26.1 | `AntiCrash-1.0.0+mc26.1.jar` | 0.18.4+ | 25+ | Build verified |
| 26.2 | `AntiCrash-1.0.0+mc26.2.jar` | 0.19.3+ | 25+ | Build verified |
| 26.3 | `AntiCrash-1.0.0+mc26.3.jar` | 0.19.5+ | 25+ | Build verified |

The Loader values are the versions declared by each build target. The matching Minecraft version and Java runtime are required. Checkpoints must not be restored across different Minecraft versions.

### Installation

1. Stop the dedicated server and make a separate backup of its world.
2. Put the matching `AntiCrash-...jar` from `build/` into the server's `mods/` directory.
3. Start the server with the listed Minecraft, Fabric Loader, and Java versions.
4. On first startup, review `config/anticrash.toml`; AntiCrash generates a complete configuration with comments.

AntiCrash is server-side. Players do not need to install it and can join with an unmodded client. Fabric API is not required for basic use. Carpet is optional; Carpet integration depends on the game version. The matching `AntiCrash-Supervisor-1.0.0+mc<VERSION>.jar` is a separate Java program, not a mod, and must not be put in `mods/`.

### Configuration

The default language is Japanese (`ja`); set `language = "en"` for English messages. The following shows common settings, not a replacement for the complete generated config:

```toml
language = "ja" # "ja" or "en"

[checkpoint]
enabled = true
interval_seconds = 30

[commands]
start_permission_level = 0
status_permission_level = 4
checkpoint_permission_level = 4
recovery_permission_level = 4

[recovery]
policy = "AUTO"
fidelity = "OPTIMIZED"
freeze_after_restore = true
require_manual_start = true
ask_timeout_seconds = 60
preload_mode = "ALL"

[recovery.chunks]
max_concurrent_loads = 4
```

Permission levels range from 0 to 4. By default, only `/ac start` is available without operator permission. The other commands require level 4. Each command's permission can be configured independently.

Recovery policies:

| Policy | After an unexpected shutdown | After a normal shutdown |
| --- | --- | --- |
| `AUTO` | Validate and restore the latest valid checkpoint automatically. | Start the current world without rollback. |
| `ASK` | Ask in the server console. `Y` restores; `N` uses the current world. No answer defaults to `Y`. | Ask in the server console. `Y` restores the shutdown checkpoint; `N` uses the current world. No answer defaults to `N`. |
| `MANUAL` | Do not restore automatically; wait for an administrator's decision. | Start the current world normally. |

`ask_timeout_seconds` sets the actual `ASK` deadline (default 60 seconds). A value of `0` disables the timeout. `fidelity = "STRICT"` prioritizes matching the checkpoint's observable tick/runtime state and blocks resume on required mismatch or unsupported state. `OPTIMIZED` is a lighter best-effort mode; **OPTIMIZED does not guarantee tick-boundary equivalence.**

When an `ASK` prompt times out, an unexpected shutdown defaults to restoring (`Y`), while a normal shutdown defaults to continuing without rollback (`N`). The older `ask_default_action` setting is retained for config compatibility and does not override this behavior.

### Commands

| Command | Purpose |
| --- | --- |
| `/ac help` | Show command help. |
| `/ac status` | Show checkpoint and recovery status. |
| `/ac start` | Resume a frozen world after recovery checks pass. |
| `/ac checkpoint now` | Request a checkpoint. |
| `/ac checkpoint list` | List available checkpoints. |
| `/ac checkpoint base` | Create an independent base checkpoint. |
| `/ac checkpoint restore <id\|latest>` | Restore a selected checkpoint. |
| `/ac recovery verify [--verbose]` | Check recovery equivalence and blockers. |
| `/ac debug equivalence [--verbose]` | Show detailed state differences. |
| `/ac recovery abort` | Abort the current recovery operation. |
| `/ac fakeplayers` | Show saved FakePlayer information. |

### Optional same-console restart supervisor

The Supervisor keeps a foreground process watching Minecraft and inherits the same console input/output. Use the matching Supervisor JAR if your host management tool monitors only one process and you need controlled restarts to remain in the same window:

```sh
java -jar AntiCrash-Supervisor-1.0.0+mc1.18.2.jar \
  --workdir /path/to/server -- \
  java -Xms1G -Xmx1G -jar fabric-server-launch.jar nogui
```

Replace the version in the JAR name with the server's Minecraft version. The command after `--` is the child server command. A direct server launch still supports the mod's checkpoint and recovery features, but it does not provide the Supervisor's process monitoring and same-console restart behavior.

### Limitations and safety

- Checkpoint time, storage, and memory requirements depend on the world and configuration. Out-of-memory termination can still lose changes since the last valid checkpoint.
- Exact recovery of every mod, TMC machine, random state, async task, and thread schedule is not guaranteed. A successful build or unit test is not proof of live-server compatibility.
- `STRICT` intentionally stays frozen when a required state is missing or mismatched. Do not bypass this for a valuable world without understanding the reported difference.
- Keep independent backups and test recovery on a copy of the world before using it on a valuable server.
- The Minecraft 1.16.1 directory is incomplete and must not be used as a released build.

The prebuilt Mod and Supervisor JARs are collected in `build/`. The project is licensed under the GNU Lesser General Public License v3.0 only (SPDX: `LGPL-3.0-only`).

### Stonecutter experiment: source layout and building

This branch uses [Stonecutter 0.9.8](https://codeberg.org/stonecutter/stonecutter) to preprocess one canonical Java source tree. Version differences use `//? if` conditions; the checkpoint's Minecraft version uses a string swap. The previous `common/`, `shared/` and source-group membership table are replaced by:

```text
src/main/java/           Canonical mod sources with version conditions
src/test/java/           Shared tests and version-specific test conditions
src/probe/java/          Runtime qualification probe sources
src/main/resources/     Resources identical across every version
versions/<minecraft>/   Native Gradle/Loom configuration and resource overrides
supervisor/             Shared process supervisor and tests
stonecutter.gradle      Read-only (detached) preprocessing controller
stonecutter-target.gradle  Per-target preprocessing configuration
```

Stonecutter requires Gradle 9. The root wrapper uses Gradle 9.4.0, while each native target keeps its existing Gradle/Loom versions. This deliberately separates source preprocessing from game compilation instead of upgrading all 30 Minecraft build toolchains at once.

Use JDK 21 for Yarn targets (1.18–1.21.11), or JDK 25 for 26.x. The native build automatically invokes the root wrapper to generate its sources:

```sh
cd versions/1.21.8
sh ./gradlew check remapJar sourcesJar :supervisor:build
```

For 26.x, replace `remapJar` with `jar`. Generated sources live in `build/stonecutter/<minecraft>/generated/stonecutter/`; compiled artifacts and reports stay under `versions/<minecraft>/build/`. Edit `src/` and version resource overrides, not generated files. Preprocessing does not switch or rewrite the checked-in source tree. The Supervisor retains per-target output isolation. Yarn's checked root JAR collection and the incomplete 1.16.1 port's publication guard remain in place.

To generate sources without compiling Minecraft, run `sh ./gradlew :1.21.8:prepareSources` from the repository root. To compare with the previous shared-source branch:

```sh
python3 -m unittest discover -s scripts -v
java scripts/SourceParity.java --self-test
python3 scripts/check_sources.py --version 1.21.8 --production-only
```

The comparison uses commit `99e41b0b3335a18a368c3495194dc92314c355f4` (override with `--baseline-ref`) and requires that commit in local Git history. Java is compared using compiler syntax trees because preprocessing adds inactive-code comments; resource bytes and membership must match exactly. The source-scanning audit parses active Java so inactive branches cannot affect its assertions. CI checks the 1.16.1 source transformation and builds/tests all 30 buildable targets, including per-target production-source/resource parity. This is offline verification, not live recovery or load qualification.

---

## 日本語

### 最新の保守更新（2026-10-30）

ビルド可能な全版へFakePlayerのCheckpoint本人識別の修正を反映しました。保存済みのUUID・名前で復元し、既存プレイヤーとの衝突は生成前に拒否します。不一致時は期待UUIDと実際のUUIDを表示します。Minecraft版ごとにプロフィールキャッシュAPIへ対応し、対応Carpetの非同期生成経路も復旧時だけ保存済みプロフィールを使用します。通常のプロフィール検索・ログインは変更しません。Checkpoint形式とModバージョン（`1.0.0`）は変更していません。今回の検証はオフラインであり、実サーバーでの動作保証が追加されたわけではありません。

### 対応バージョン

以下のうち30版は**ビルド検証済み**で、版に対応したMod JARをリポジトリの`build/`に収録しています。ビルド検証済みとは、コンパイル・ユニットテスト・成果物検査が完了したという意味で、実Minecraftサーバーでの動作確認を意味しません。**1.16.1は移植中で、利用可能なJARはありません。**

| Minecraft | Mod JAR | Fabric Loader | Java | 状態 |
| --- | --- | --- | --- | --- |
| 1.18 | `AntiCrash-1.0.0+mc1.18.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.18.1 | `AntiCrash-1.0.0+mc1.18.1.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.18.2 | `AntiCrash-1.0.0+mc1.18.2.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.19 | `AntiCrash-1.0.0+mc1.19.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.19.1 | `AntiCrash-1.0.0+mc1.19.1.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.19.2 | `AntiCrash-1.0.0+mc1.19.2.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.19.3 | `AntiCrash-1.0.0+mc1.19.3.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.19.4 | `AntiCrash-1.0.0+mc1.19.4.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20 | `AntiCrash-1.0.0+mc1.20.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20.1 | `AntiCrash-1.0.0+mc1.20.1.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20.2 | `AntiCrash-1.0.0+mc1.20.2.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20.3 | `AntiCrash-1.0.0+mc1.20.3.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20.4 | `AntiCrash-1.0.0+mc1.20.4.jar` | 0.17.3以上 | 17以上 | ビルド検証済み |
| 1.20.5 | `AntiCrash-1.0.0+mc1.20.5.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.20.6 | `AntiCrash-1.0.0+mc1.20.6.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21 | `AntiCrash-1.0.0+mc1.21.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.1 | `AntiCrash-1.0.0+mc1.21.1.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.2 | `AntiCrash-1.0.0+mc1.21.2.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.3 | `AntiCrash-1.0.0+mc1.21.3.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.4 | `AntiCrash-1.0.0+mc1.21.4.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.5 | `AntiCrash-1.0.0+mc1.21.5.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.6 | `AntiCrash-1.0.0+mc1.21.6.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.7 | `AntiCrash-1.0.0+mc1.21.7.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.8 | `AntiCrash-1.0.0+mc1.21.8.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.9 | `AntiCrash-1.0.0+mc1.21.9.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.10 | `AntiCrash-1.0.0+mc1.21.10.jar` | 0.17.3以上 | 21以上 | ビルド検証済み |
| 1.21.11 | `AntiCrash-1.0.0+mc1.21.11.jar` | 0.18.4以上 | 21以上 | ビルド検証済み |
| 26.1 | `AntiCrash-1.0.0+mc26.1.jar` | 0.18.4以上 | 25以上 | ビルド検証済み |
| 26.2 | `AntiCrash-1.0.0+mc26.2.jar` | 0.19.3以上 | 25以上 | ビルド検証済み |
| 26.3 | `AntiCrash-1.0.0+mc26.3.jar` | 0.19.5以上 | 25以上 | ビルド検証済み |

Loader欄は各版のビルド対象が宣言するバージョンです。Minecraft版・Java要件を合わせてください。異なるMinecraft版間でチェックポイントを復元することはできません。

### AntiCrashとは？

AntiCrashはチェックポイントを作成し、予期しないサーバー終了後のワールド復旧を支援するFabricサーバーModです。復元後にワールドを停止状態に保ち、管理者が`/ac start`で再開する運用にも対応しています。

Technical Minecraftでの利用を想定していますが、VanillaやすべてのModの状態、非同期処理、スレッドの実行順を完全に再現する保証はありません。通常バックアップの代わりにはならないため、大切な装置はワールド複製で先に試してください。

### 導入方法

1. 専用サーバーを停止し、ワールドの独立したバックアップを作成します。
2. `build/`からサーバーのMinecraft版に一致する`AntiCrash-...jar`を選び、サーバーの`mods/`へ入れます。
3. 表に記載したMinecraft・Fabric Loader・Javaで起動します。
4. 初回起動時に生成される`config/anticrash.toml`を確認します。全項目と説明コメントを含む設定ファイルが作成されます。

AntiCrashはサーバー側のみで動作します。プレイヤー側にModは不要で、Modなしのクライアントから参加できます。基本利用にFabric APIは不要です。Carpetも必須ではありませんが、連携できる状態はMinecraft版により異なります。対応する`AntiCrash-Supervisor-1.0.0+mc<VERSION>.jar`は別のJavaプログラムであり、Modではないため`mods/`には入れません。

### 設定と権限

既定の言語は日本語（`ja`）です。メッセージを英語にするには`language = "en"`にします。次は主な共通設定の例です。生成された完全な設定ファイル全体をこの抜粋で置き換えないでください。

```toml
language = "ja" # "ja" または "en"

[checkpoint]
enabled = true
interval_seconds = 30

[commands]
start_permission_level = 0
status_permission_level = 4
checkpoint_permission_level = 4
recovery_permission_level = 4

[recovery]
policy = "AUTO"
fidelity = "OPTIMIZED"
freeze_after_restore = true
require_manual_start = true
ask_timeout_seconds = 60
preload_mode = "ALL"

[recovery.chunks]
max_concurrent_loads = 4
```

権限レベルは0〜4です。既定では`/ac start`だけOP権限なし（レベル0）で使用でき、それ以外はレベル4が必要です。コマンドごとに権限を設定できます。

| ポリシー | 異常終了後 | 正常終了後 |
| --- | --- | --- |
| `AUTO` | 有効な最新checkpointを検証して自動復元します。 | 巻き戻さず現在のワールドで起動します。 |
| `ASK` | Server consoleで質問します。`Y`で復元、`N`で現在のワールドを使用。無回答は`Y`扱いです。 | Server consoleで質問します。`Y`で停止時checkpointへ復元、`N`で現在のワールドを使用。無回答は`N`扱いです。 |
| `MANUAL` | 自動復元せず、管理者の判断を待ちます。 | 現在のワールドで通常起動します。 |

`ask_timeout_seconds`でASKの回答期限を設定します（既定60秒、`0`は期限なし）。`fidelity = "STRICT"`はCheckpoint時に観測できたtick/runtime状態との一致を優先し、必須状態の欠落や不一致があれば再開を拒否します。`OPTIMIZED`は軽量なbest-effort方式で、**tick境界の等価性を保証しません**。

ASKで期限までに回答がない場合、異常終了後は復元（`Y`）、正常終了後は巻き戻さず続行（`N`）します。旧設定の`ask_default_action`は互換性のため残っていますが、この動作を変更しません。

### コマンド

| コマンド | 用途 |
| --- | --- |
| `/ac help` | コマンドヘルプを表示 |
| `/ac status` | Checkpoint・復旧状態を表示 |
| `/ac start` | 復旧検査を通過した凍結ワールドを再開 |
| `/ac checkpoint now` | Checkpoint作成を要求 |
| `/ac checkpoint list` | Checkpoint一覧を表示 |
| `/ac checkpoint base` | 独立したBASE checkpointを作成 |
| `/ac checkpoint restore <id\|latest>` | 指定Checkpointを復元 |
| `/ac recovery verify [--verbose]` | 復旧等価性と開始阻害要因を確認 |
| `/ac debug equivalence [--verbose]` | 状態の詳細な差分を表示 |
| `/ac recovery abort` | 進行中の復旧を中止 |
| `/ac fakeplayers` | FakePlayerの保存情報を表示 |

### 同一コンソールでの自動再起動（任意）

SupervisorはMinecraftプロセスを前面で監視し、標準入出力とコンソール入力を引き継ぎます。管理ツールが1つのPIDしか監視できず、再起動後も同じウィンドウで監視したい場合に、対応するSupervisor JARを使います。

```sh
java -jar AntiCrash-Supervisor-1.0.0+mc1.18.2.jar \
  --workdir /path/to/server -- \
  java -Xms1G -Xmx1G -jar fabric-server-launch.jar nogui
```

Supervisor JAR名のMinecraft版はサーバーに合わせます。`--`より後ろがServerの起動コマンドです。Modを直接起動してもCheckpoint・復旧機能は使えますが、Supervisorによるプロセス監視と同一コンソールでの再起動は行われません。

### 注意事項

- Checkpointに必要な時間・保存容量・メモリはワールドや設定により変わります。OOM時には最後の有効Checkpoint以降の変更が失われる可能性があります。
- あらゆるMod、TMC装置、乱数、非同期処理、スレッド実行順を完全復元できる保証はありません。ビルド・ユニットテスト成功は実サーバーでの動作確認を意味しません。
- `STRICT`は必須状態が欠落・不一致の場合、意図的にワールドを停止したままにします。差分の意味を確認せずに安全制限を回避しないでください。
- 大切なサーバーでは独立バックアップを保管し、ワールド複製で復旧テストをしてから利用してください。
- `1.16.1`は移植途中です。配布用ビルドとして使用しないでください。

配布用Mod JARとSupervisor JARはルートの`build/`にあります。ライセンスはGNU Lesser General Public License v3.0 only（SPDX: `LGPL-3.0-only`）です。

### Stonecutter実験版の構成とビルド

このブランチはStonecutter 0.9.8を使用し、共通の`src/`にJavaソースを集約しています。版による差は`//? if`条件で切り替え、チェックポイントに記録するMinecraft版には文字列の置換を使います。`common/`・`shared/`・ソース群の適用表を管理する方式との比較用です。

Stonecutterの前処理はルートのGradle 9.4.0で実行します。Minecraft本体のビルドには各版の既存Gradle・Loomを使い、前処理とコンパイルを分離しています。`cd versions/1.21.8`の後に`sh ./gradlew check remapJar sourcesJar :supervisor:build`を実行すると、必要なソース生成も自動実行されます。JDKはYarn版で21、26.xで25を使い、26.xでは`remapJar`を`jar`に置き換えてください。

編集対象は`src/`と`versions/<版>/src/`のリソースです。生成先の`build/stonecutter/<版>/generated/stonecutter/`は直接編集しません。通常の前処理ではGit管理下のソースを書き換えず、Supervisorの共通化と版別の出力先も維持しています。1.16.1の未完成・配布禁止の扱いは継続しています。

移行前の共通化ブランチとの比較では、Javaのコメントや書式を除いた構文木と、リソースの内容・配置を照合します。検証コマンドと制約は上の英語セクションに記載しています。CIでは1.16.1のソース変換とビルド可能な30版を検証します。実サーバーでの復旧確認・負荷試験は未実施です。
