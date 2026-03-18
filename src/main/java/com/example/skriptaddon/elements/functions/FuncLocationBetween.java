package com.example.skriptaddon.elements.functions;

import ch.njol.skript.lang.function.Functions;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.common.function.DefaultFunction;

/**
 * Example function — returns the midpoint between two locations.
 * Uses DefaultFunction.builder() for registration with the new Skript function API.
 *
 * Skript usage:
 *   set {_mid} to location_between(player's location, target entity's location)
 */
public class FuncLocationBetween {

	public static void register(@NotNull SkriptAddon addon) {
		DefaultFunction<Location> function = DefaultFunction.builder(addon, "location_between", Location.class)
				.description("Returns the midpoint between two locations. Both must be in the same world.")
				.examples("""
						set {_mid} to location_between(player's location, target entity's location)
						spawn particle using dustOption(red, 1) at {_mid}""")
				.since("1.0.0")
				.parameter("loc1", Location.class)
				.parameter("loc2", Location.class)
				.build(args -> {
					Location loc1 = args.get("loc1");
					Location loc2 = args.get("loc2");
					if (loc1 == null || loc2 == null)
						return null;
					if (!loc1.getWorld().equals(loc2.getWorld()))
						return null;
					return loc1.clone().add(loc2).multiply(0.5);
				});

		Functions.register(function);
	}

}
