# skript-addon-template

A template for creating [Skript](https://github.com/SkriptLang/Skript) addons using the new Skript 2.14.0+ addon API.


## Requirements

| Dependency | Version | Why |
|---|---|---|
| Java | 21+ | Required by Skript 2.14.0 |
| Skript | 2.14.0+ | Uses the new addon API (SkriptAddon, AddonModule, SyntaxRegistry) |
| Spigot/Paper | 1.19.4+ | Bukkit API |
| Gradle | 8.x | Build tool |

## Getting Started

1. Clone or fork this repository
2. Rename the package from `com.example.skriptaddon` to your own (e.g., `com.yourname.youraddon`)
3. Rename `SkriptAddonTemplate.java` to match your addon name
4. Update these files with your addon's info:
   - `build.gradle` — group, version, dependencies
   - `settings.gradle` — project name
   - `src/main/resources/plugin.yml` — main class, name, authors, description
5. Delete the example syntax files and start writing your own
6. Build with `./gradlew build`

## Project Structure

```
src/main/java/com/example/skriptaddon/
├── SkriptAddonTemplate.java              # Main plugin class (JavaPlugin + AddonModule)
└── elements/                             # All syntax elements go here
    ├── conditions/                       # Conditions (true/false checks)
    │   └── CondExampleCondition.java     # "player is example dummy"
    ├── effects/                          # Effects (actions, no return value)
    │   └── EffExampleEffect.java         # "example announce player"
    ├── events/                           # Events (Bukkit event → Skript trigger)
    │   └── EvtExampleEvent.java          # "on example sneak toggle"
    └── expressions/                      # Expressions (returns a value)
        └── ExprExampleExpression.java    # "greeting of player"

src/main/resources/
├── plugin.yml                            # Bukkit plugin descriptor
└── lang/
    └── default.lang                      # Custom type names and enum values
```

## How the Addon System Works

### Plugin Lifecycle

```
Server starts
  → Bukkit calls onEnable()
    → Check Skript is installed and version >= 2.14.0
    → Register with Skript via registerAddon()
    → Skript calls init() — register custom types (ClassInfo)
    → Skript calls load() — auto-discover and register all syntax elements
```

### Auto-Discovery

The main class scans `com.example.skriptaddon.elements` and all sub-packages for classes that implement `SyntaxElement`. For each one, it calls the static `register(SyntaxRegistry)` method via reflection.

**Every syntax class MUST have this method:**
```java
public static void register(SyntaxRegistry registry) {
    // registration code here
}
```

### The 4 Syntax Element Types

| Type | Superclass | Registration | Purpose | Example Syntax |
|---|---|---|---|---|
| **Expression** | `SimpleExpression<T>` | `DefaultSyntaxInfos.Expression.builder()` | Returns a value | `greeting of player` |
| **Condition** | `PropertyCondition<T>` | `infoBuilder()` | Returns true/false | `player is example dummy` |
| **Effect** | `Effect` | `SyntaxInfo.builder()` | Performs an action | `example announce player` |
| **Event** | `SkriptEvent` | `BukkitSyntaxInfos.Event.builder()` | Maps Bukkit events | `on example sneak toggle:` |

### Registration Cheat Sheet

**Expression** (returns a value):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(SyntaxRegistry.EXPRESSION,
        DefaultSyntaxInfos.Expression.builder(MyExpr.class, String.class) // return type
            .supplier(MyExpr::new)
            .addPatterns("my expression of %player%")
            .build());
}
```

**Condition** (PropertyCondition — auto-generates negation):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(SyntaxRegistry.CONDITION,
        infoBuilder(MyCond.class, PropertyType.BE, "my property", "players")
            .supplier(MyCond::new)
            .build());
}
// Generates: %players% (is|are) my property / %players% (isn't|is not) my property
```

**Effect** (performs an action):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(SyntaxRegistry.EFFECT,
        SyntaxInfo.builder(MyEffect.class)
            .supplier(MyEffect::new)
            .addPatterns("my effect %player%")
            .build());
}
```

**Event** (listens for Bukkit events):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(BukkitSyntaxInfos.Event.KEY,
        BukkitSyntaxInfos.Event.builder(MyEvent.class, "My Event Name")
            .supplier(MyEvent::new)
            .addEvent(PlayerJoinEvent.class) // Bukkit event class
            .addPatterns("my custom event")
            .addDescription("When something happens.")
            .addExample("on my custom event:\n\tsend \"hello\" to player")
            .addSince("1.0.0")
            .build());
}
```

## Pattern Syntax

Patterns define how users write your syntax in Skript scripts.

| Syntax | Meaning | Example |
|---|---|---|
| `%player%` | Required input of type player | `greeting of %player%` |
| `%string%` | Required string input | `send %string% to %player%` |
| `%integer%` | Required integer input | `set health of %player% to %integer%` |
| `[optional]` | Optional section | `[the] greeting` matches both "greeting" and "the greeting" |
| `(a\|b\|c)` | Choose one option | `(exit\|leave)` matches "exit" or "leave" |
| `text[ing]` | Optional suffix | `sneak[ing]` matches "sneak" or "sneaking" |
| `%objects%` | Multiple values | Used when the expression returns a list |
| `[:tag]` | Named tag (checked via `parseResult.hasTag("tag")`) | `[:silent] send %string%` |

## @NotNull and @Nullable

These annotations from `org.jetbrains.annotations` are **essential** for writing safe addon code.

### Why They Matter

Skript expressions frequently return `null` — a player went offline, a variable is empty, an expression failed to parse. Without null annotations, you won't know which values can be null until your addon crashes on a live server.

```java
// BAD — no annotations, no null check, will crash when player is null
@Override
protected void execute(Event event) {
    Player player = playerExpression.getSingle(event);
    player.sendMessage("Hello!");  // NullPointerException!
}

// GOOD — @Nullable awareness forces you to check
@Override
protected void execute(@NotNull Event event) {
    @Nullable Player player = playerExpression.getSingle(event);  // getSingle is @Nullable
    if (player != null) {
        player.sendMessage("Hello!");
    }
}
```

### Where to Use Them

| Annotation | Where | Why |
|---|---|---|
| `@NotNull` | Method params that are never null | `execute(@NotNull Event event)` — Bukkit guarantees this |
| `@NotNull` | Return types that never return null | `@NotNull String getPropertyName()` |
| `@Nullable` | Values that CAN be null | `playerExpression.getSingle(event)` returns `@Nullable` |
| `@Nullable` | Event param in `toString()` | Null during parsing (no event exists yet) |
| `@Nullable` | The `get()` return array | `String @Nullable []` — no value available |

### The IDE Advantage

With these annotations, your IDE will:
- Warn you if you pass `null` where `@NotNull` is expected
- Warn you if you use a `@Nullable` value without checking for null
- Catch potential `NullPointerException` at **compile time** instead of runtime

This is why `org.jetbrains:annotations` is included as a dependency in `build.gradle`. It has **zero runtime overhead** — the annotations are only used by the compiler and IDE.

## Documentation Annotations

Skript uses annotations to generate documentation for your syntax:

```java
@Name("My Expression")           // Display name in docs
@Description("What it does.")    // Description paragraph
@Example("""
    command /test:
        trigger:
            send my expression
    """)                          // Skript code example
@Since("1.0.0")                  // Version this was added
```

For events, you can also use builder methods:
```java
.addDescription("What it does.")
.addExample("on my event:\n\tsend \"hello\"")
.addSince("1.0.0")
```

## Naming Conventions

| Type | Prefix | Example |
|---|---|---|
| Expression | `Expr` | `ExprPlayerHealth.java` |
| Condition | `Cond` | `CondIsFlying.java` |
| Effect | `Eff` | `EffTeleport.java` |
| Event | `Evt` | `EvtBlockBreak.java` |

## Building

```bash
# Standard build
./gradlew build

# Nightly build (appends git hash to version)
./gradlew nightlyBuild
```

Output jar is in `build/libs/`.

## Adding Dependencies

To depend on other plugins (WorldGuard, Vault, etc.):

1. Add the repository and dependency in `build.gradle`:
```groovy
repositories {
    maven { url "https://maven.enginehub.org/repo/" }  // WorldGuard
}
dependencies {
    implementation 'com.sk89q.worldguard:worldguard-bukkit:7.0.8'
}
```

2. Add the plugin to `plugin.yml`:
```yaml
softdepend: [Skript, WorldGuard]  # softdepend = optional
depend: [Skript, WorldGuard]      # depend = required (server won't start without it)
```

3. Check for it in `onEnable()`:
```java
Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
if (worldGuard == null || !worldGuard.isEnabled()) {
    getLogger().severe("Could not find WorldGuard! Disabling...");
    getServer().getPluginManager().disablePlugin(this);
    return;
}
```

## Useful Links

- [Skript GitHub](https://github.com/SkriptLang/Skript)
- [Skript JavaDocs](https://docs.skriptlang.org/javadocs/)
- [skript-worldguard](https://github.com/SkriptLang/skript-worldguard) — Real-world addon this template is based on
- [SkBee](https://github.com/ShaneBeestudios/SkBee) — Large Skript addon for reference
- [Spigot API JavaDocs](https://hub.spigotmc.org/javadocs/spigot/)
- [Paper API JavaDocs](https://jd.papermc.io/paper/1.21.10/)

## License

[MIT License](LICENSE)
