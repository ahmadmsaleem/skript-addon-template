package com.example.skriptaddon.elements.structures;

import ch.njol.skript.config.EntryNode;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Example structure — a top-level block that reads key = value entries from a script.
 * Structures live outside events (like command:, function:, on event:).
 *
 * Skript usage:
 *   custom config "my-settings":
 *       greeting = Hello, world!
 *       max-lives = 3
 *
 * Values are accessible at runtime via StructCustomConfig.getValue("my-settings", "greeting")
 */
@Name("Custom Config")
@Description("Defines key-value configuration entries in a script file. Values can be accessed by other syntax elements.")
@Example("""
		custom config "my-settings":
			greeting = Hello, world!
			max-lives = 3
		""")
@Since("1.0.0")
public class StructCustomConfig extends Structure {

	// Store all configs: configName → (key → value)
	private static final Map<String, Map<String, String>> configs = new HashMap<>();

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.STRUCTURE, SyntaxInfo.Structure.builder(StructCustomConfig.class)
				.supplier(StructCustomConfig::new)
				.addPatterns("custom config %string%")
				.nodeType(SyntaxInfo.Structure.NodeType.SECTION)
				.build());
	}

	/**
	 * Get a config value at runtime.
	 */
	public static @Nullable String getValue(String configName, String key) {
		Map<String, String> entries = configs.get(configName);
		if (entries == null) return null;
		return entries.get(key);
	}

	private String configName;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Literal<?>[] args, int matchedPattern, @NotNull ParseResult parseResult,
						@Nullable EntryContainer entryContainer) {
		configName = ((Literal<String>) args[0]).getSingle();
		if (entryContainer == null) return false;

		// Parse key = value entries from the section
		SectionNode source = entryContainer.getSource();
		source.convertToEntries(0, "=");

		Map<String, String> entries = new HashMap<>();
		for (Node node : source) {
			if (node instanceof EntryNode entryNode) {
				entries.put(entryNode.getKey(), entryNode.getValue());
			}
		}

		configs.put(configName, entries);
		return true;
	}

	@Override
	public boolean load() {
		return true; // work was done in init()
	}

	@Override
	public @NotNull String toString(@Nullable org.bukkit.event.Event event, boolean debug) {
		return "custom config \"" + configName + "\"";
	}

}
