package com.example.skriptaddon;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.util.Version;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.util.ClassLoader;

import java.lang.reflect.InvocationTargetException;

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
		} else if (Skript.getVersion().isSmallerThan(new Version("2.14.0-pre1"))) {
			getLogger().severe("You are running an unsupported version of Skript. Please update to at least Skript 2.14.0. Disabling...");
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
	public void init(@NotNull SkriptAddon addon) {
		// Register custom types here using Classes.registerClass()
		// Types must be registered BEFORE syntax that uses them
	}

	@Override
	public void load(@NotNull SkriptAddon addon) {
		// Auto-discover and register all syntax elements in the elements package
		// Every syntax class MUST have: public static void register(SyntaxRegistry registry)
		ClassLoader.builder()
				.basePackage("com.example.skriptaddon.elements")
				.deep(true) // scan sub-packages (conditions/, effects/, events/, expressions/)
				.initialize(true)
				.forEachClass(clazz -> {
					if (SyntaxElement.class.isAssignableFrom(clazz)) {
						try {
							clazz.getMethod("register", SyntaxRegistry.class).invoke(null, addon.syntaxRegistry());
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							getLogger().severe("Failed to load syntax class: " + e);
						}
					}
				})
				.build()
				.loadClasses(SkriptAddonTemplate.class, getFile());
	}

}
