# skript-addon-template

A template for creating [Skript](https://github.com/SkriptLang/Skript) addons using the new Skript 2.14.0+ addon API.


## Requirements

| Dependency | Version | Why |
|---|---|---|
| Java | 21+ | Required by Skript 2.14.0 |
| Skript | 2.14.3+ | Uses the new addon API (SkriptAddon, AddonModule, SyntaxRegistry) |
| Paper | 1.19.4+ | Paper API (Skript is Paper-only) |
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

## Learning Guide

| I want to learn... | Read these files | Skript syntax |
|---|---|---|
| **How the addon starts up** | `SkriptAddonTemplate.java` | — |
| **Expressions** (return a value) | `ExprExampleExpression.java` | `greeting of player` |
| **Conditions** (true/false checks) | `CondExampleCondition.java` | `player is example dummy` |
| **Effects** (perform an action) | `EffExampleEffect.java` | `example announce player` |
| **Events** (listen to Bukkit events) | `EvtExampleEvent.java` | `on example sneak toggle:` |
| **Functions** (callable with params) | `FuncLocationBetween.java` | `location_between(loc1, loc2)` |
| **Sections** (code blocks with `:`) | `SecCooldown.java` | `with cooldown 5 seconds for player:` |
| **Structures** (top-level config blocks) | `StructCustomConfig.java` + `ExprConfigValue.java` | `custom config "name":` |
| **Custom types** (ClassInfo, Parser, Serializer) | `TypeCustomColor.java` + `ExprCustomColor.java` | `point at 10, 20` |
| **Testing** | `src/test/scripts/*.sk` | `assert {_var} is set with "msg"` |
| **Build & CI** | `build.gradle` + `.github/workflows/gradle.yml` | — |

All Java files are in `src/main/java/com/example/skriptaddon/elements/`.

## Project Structure

```
src/main/java/com/example/skriptaddon/
├── SkriptAddonTemplate.java              # Main plugin class (JavaPlugin + AddonModule)
└── elements/                             # All syntax elements go here
    ├── conditions/
    │   └── CondExampleCondition.java     # "player is example dummy"
    ├── effects/
    │   └── EffExampleEffect.java         # "example announce player"
    ├── events/
    │   └── EvtExampleEvent.java          # "on example sneak toggle"
    ├── expressions/
    │   ├── ExprExampleExpression.java    # "greeting of player"
    │   ├── ExprCustomColor.java         # "point at 10, 20"
    │   └── ExprConfigValue.java         # "config value "key" from "name""
    ├── functions/
    │   └── FuncLocationBetween.java      # "location_between(loc1, loc2)"
    ├── sections/
    │   └── SecCooldown.java             # "with cooldown 5 seconds for player:"
    ├── structures/
    │   └── StructCustomConfig.java      # "custom config "name":"
    └── types/
        └── TypeCustomColor.java         # "point2d" type (2D point)

src/main/resources/
├── plugin.yml                            # Bukkit plugin descriptor
└── lang/
    └── default.lang                      # Custom type names and enum values

src/test/scripts/                         # Skript-based tests (run via skript-test-action)
├── condition.sk
├── cooldown_section.sk
├── custom_color.sk
├── custom_config.sk
├── effect.sk
├── event.sk
├── expression.sk
└── location_between.sk
```

## How the Addon System Works

### Plugin Lifecycle

```
Server starts
  → Bukkit calls onEnable()
    → Check Skript is installed and version >= 2.14.3
    → Register with Skript via registerAddon()
    → Skript calls init() — register custom types (ClassInfo)
    → Skript calls load() — register all syntax elements
```

### Registration

Types are registered in `init()` (before syntax). Everything else is registered in `load()`:

```java
@Override
public void init(@NotNull SkriptAddon addon) {
    TypeCustomColor.register(); // types MUST be registered before syntax that uses them
}

@Override
public void load(@NotNull SkriptAddon addon) {
    SyntaxRegistry registry = addon.syntaxRegistry();

    ExprExampleExpression.register(registry);
    ExprCustomColor.register(registry);
    ExprConfigValue.register(registry);
    CondExampleCondition.register(registry);
    EffExampleEffect.register(registry);
    EvtExampleEvent.register(registry);
    SecCooldown.register(registry);
    StructCustomConfig.register(registry);
    FuncLocationBetween.register(addon);
}
```

### The 7 Syntax Element Types

| Type | Superclass | Registration | Purpose | Example Syntax |
|---|---|---|---|---|
| **Expression** | `SimpleExpression<T>` | `DefaultSyntaxInfos.Expression.builder()` | Returns a value | `greeting of player` |
| **Condition** | `PropertyCondition<T>` | `infoBuilder()` | Returns true/false | `player is example dummy` |
| **Effect** | `Effect` | `SyntaxInfo.builder()` | Performs an action | `example announce player` |
| **Event** | `SkriptEvent` | `BukkitSyntaxInfos.Event.builder()` | Maps Bukkit events | `on example sneak toggle:` |
| **Function** | `DefaultFunction<T>` | `DefaultFunction.builder()` | Callable function | `location_between(loc1, loc2)` |
| **Section** | `Section` | `SyntaxInfo.builder()` | Code block with `:` | `with cooldown 5 seconds for player:` |
| **Structure** | `Structure` | `SyntaxInfo.Structure.builder()` | Top-level block | `custom config "name":` |
| **Type** | `ClassInfo<T>` | `Classes.registerClass()` | Custom Skript type | `point2d` |

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
            .addExample("""
                    on my custom event:
                        send "hello" to player
                    """)
            .addSince("1.0.0")
            .build());
}
```

**Function** (callable from Skript):
```java
public static void register(SkriptAddon addon) {
    DefaultFunction<Location> function = DefaultFunction.builder(addon, "my_function", Location.class)
            .description("What it does.")
            .examples("set {_x} to my_function(arg1, arg2)")
            .since("1.0.0")
            .parameter("param1", Location.class)
            .parameter("param2", Location.class)
            .build(args -> {
                Location loc = args.get("param1");
                return loc; // return value or null
            });
    Functions.register(function);
}
```

**Section** (code block with `:`):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(SyntaxRegistry.SECTION,
        SyntaxInfo.builder(MySection.class)
            .supplier(MySection::new)
            .addPatterns("my section %timespan% for %player%")
            .build());
}
// Override init() with SectionNode param, call loadCode() to compile the body
// Override walk() to run the body conditionally with Trigger.walk(trigger, event)
```

**Structure** (top-level block):
```java
public static void register(SyntaxRegistry registry) {
    registry.register(SyntaxRegistry.STRUCTURE,
        SyntaxInfo.Structure.builder(MyStructure.class)
            .supplier(MyStructure::new)
            .addPatterns("my structure %string%")
            .nodeType(SyntaxInfo.Structure.NodeType.SECTION)
            .build());
}
// Override init() with EntryContainer param, read entries from SectionNode
// Override load() to finalize — return true on success
```

**Type** (custom ClassInfo — registered in `init()`, not `load()`):
```java
public static void register() {
    Classes.registerClass(new ClassInfo<>(MyType.class, "mytype")
            .user("my ?types?")
            .name("My Type")
            .description("What this type represents.")
            .since("1.0.0")
            .parser(new Parser<>() {
                public boolean canParse(ParseContext context) { return false; }
                public String toString(MyType o, int flags) { return o.toString(); }
                public String toVariableNameString(MyType o) { return o.toString(); }
            })
            .serializer(new Serializer<>() { /* serialize/deserialize */ }));
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
| Function | `Func` | `FuncLocationBetween.java` |
| Section | `Sec` | `SecCooldown.java` |
| Structure | `Struct` | `StructCustomConfig.java` |
| Type | `Type` | `TypeCustomColor.java` |

## Example Script

See [`example.sk`](example.sk) for a ready-to-use Skript file that demonstrates all the example syntax elements. Place it in `plugins/Skript/scripts/` to test.

## Testing

Tests are Skript-based `.sk` files in `src/test/scripts/`, run on a real Paper server via [skript-test-action](https://github.com/SkriptLang/skript-test-action) in CI.

### Test Files

| File | What it tests |
|---|---|
| `expression.sk` | `example greeting of %player%` returns correct greeting |
| `condition.sk` | `is example dummy` checks player name starts with "A" |
| `effect.sk` | `example announce %player%` runs without error |
| `event.sk` | All 3 sneak toggle event patterns parse correctly |
| `location_between.sk` | `location_between()` function returns correct midpoint |
| `custom_color.sk` | `point at x, y` type creation and negative coords |
| `cooldown_section.sk` | `with cooldown` section runs body on first use |
| `custom_config.sk` | `custom config` structure parses key=value entries |

### Writing Tests

```sk
test "skript-addon-template - my feature":
    # Setup
    set {_var} to something

    # Assert
    assert {_var} is set with "Variable should exist"
    assert {_var} = expected_value with "Should equal expected"
```

### Running Tests in CI

Add this to your GitHub Actions workflow:

```yaml
- uses: SkriptLang/skript-test-action@v1.2
  with:
    test-script-directory: src/test/scripts
    extra-plugins-directory: build/libs
```

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
- [SkBee](https://github.com/ShaneBeee/SkBee) — Large Skript addon for reference
- [Paper API JavaDocs](https://jd.papermc.io/paper/1.21.11/)
- [Skript Pattern Calculator](https://bi0qaw.github.io/skript-pattern-calculator/) — Combinatoric calculator to see how many different ways your pattern can be matched

## License

[MIT License](LICENSE)
