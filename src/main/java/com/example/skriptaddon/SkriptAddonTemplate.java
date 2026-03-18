package com.example.skriptaddon;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.example.skriptaddon.elements.conditions.CondExampleCondition;
import com.example.skriptaddon.elements.effects.EffExampleEffect;
import com.example.skriptaddon.elements.events.EvtExampleEvent;
import com.example.skriptaddon.elements.expressions.ExprConfigValue;
import com.example.skriptaddon.elements.expressions.ExprPlayerGreeting;
import com.example.skriptaddon.elements.expressions.ExprPointAt;
import com.example.skriptaddon.elements.functions.FuncLocationBetween;
import com.example.skriptaddon.elements.sections.SecCooldown;
import com.example.skriptaddon.elements.structures.StructCustomConfig;
import com.example.skriptaddon.elements.types.TypeCustomColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Main plugin class — serves as both the Bukkit entry point and Skript addon module.
 * <p>
 * AddonModule lifecycle (called in order):
 * 1. {@link #init(SkriptAddon)} — register custom types (ClassInfo)
 * 2. {@link #load(SkriptAddon)} — register syntax elements (expressions, conditions, effects, events)
 */
public class SkriptAddonTemplate extends JavaPlugin implements AddonModule {

	private static @Nullable SkriptAddonTemplate instance;

	public static @Nullable SkriptAddonTemplate getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		// Verify Skript is present and enabled
		Plugin skript = getServer().getPluginManager().getPlugin("Skript");
		if (skript == null || !skript.isEnabled()) {
			getLogger().severe("Could not find Skript! Make sure you have it installed and that it properly loaded. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else if (Skript.getVersion().isSmallerThan(new Version("2.14.3"))) {
			getLogger().severe("You are running an unsupported version of Skript. Please update to at least Skript 2.14.3. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		// Register with Skript — "skript-addon-template" is the addon name shown in docs/logs
		SkriptAddon addon = Skript.instance().registerAddon(SkriptAddonTemplate.class, "skript-addon-template");
		// "lang" = look in src/main/resources/lang/ for language files
		addon.localizer().setSourceDirectories("lang", null);
		// Triggers init() then load() on this module
		addon.loadModules(this);
	}

	@Override
	public @NotNull String name() {
		return "skript-addon-template";
	}

	@Override
	public void init(@NotNull SkriptAddon addon) {
		// Register custom types here using Classes.registerClass()
		// Types must be registered BEFORE syntax that uses them
		TypeCustomColor.register();
	}

	@Override
	public void load(@NotNull SkriptAddon addon) {
		SyntaxRegistry registry = addon.syntaxRegistry();

		// Register expressions
		ExprPlayerGreeting.register(registry);
		ExprPointAt.register(registry);
		ExprConfigValue.register(registry);

		// Register conditions
		CondExampleCondition.register(registry);

		// Register effects
		EffExampleEffect.register(registry);

		// Register events
		EvtExampleEvent.register(registry);

		// Register sections
		SecCooldown.register(registry);

		// Register structures
		StructCustomConfig.register(registry);

		// Register functions
		FuncLocationBetween.register(addon);
	}

}
