# skript-addon-template

A minimal template for building [Skript](https://github.com/SkriptLang/Skript) addons with the Skript 2.14+ addon API.

## Requirements

| Dependency | Version |
|---|---|
| Java | 21+ |
| Skript | 2.14.3+ |
| Paper | 1.19.4+ |
| Gradle | 8.x |

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
| **Functions** (callable with parameters) | `FuncLocationBetween.java` | `location_between(loc1, loc2)` |
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
    │   ├── ExprCustomColor.java          # "point at 10, 20"
    │   └── ExprConfigValue.java          # "config value 'key' from 'name'"
    ├── functions/
    │   └── FuncLocationBetween.java      # "location_between(loc1, loc2)"
    ├── sections/
    │   └── SecCooldown.java              # "with cooldown 5 seconds for player:"
    ├── structures/
    │   └── StructCustomConfig.java       # "custom config 'name':"
    └── types/
        └── TypeCustomColor.java          # "point2d" type (2D point)


src/test/scripts/
└── *.sk                                  # Tests for the implementation
```

## Registration Rules

- Register custom types in `init()`.
- Register all syntax (expressions, conditions, effects, events, sections, structures, functions) in `load()`.

## Build and Test

```bash
# Standard build
./gradlew build

# Nightly build (adds git hash)
./gradlew nightlyBuild
```

Build output: `build/libs/`.

## Example Script
Use `example.sk` as a reference. Place it in `plugins/Skript/scripts/` on your test server.

## Useful Links

- [Skript GitHub](https://github.com/SkriptLang/Skript)
- [Skript JavaDocs](https://docs.skriptlang.org/javadocs/)
- [skript-worldguard](https://github.com/SkriptLang/skript-worldguard) — Real-world addon this template is based on
- [SkBee](https://github.com/ShaneBeee/SkBee) — Large Skript addon for reference
- [Paper API JavaDocs](https://jd.papermc.io/paper/1.21.11/)
- [Skript Pattern Calculator](https://bi0qaw.github.io/skript-pattern-calculator/) — Combinatoric calculator to see how many different ways your pattern can be matched

## License

[MIT License](LICENSE)
