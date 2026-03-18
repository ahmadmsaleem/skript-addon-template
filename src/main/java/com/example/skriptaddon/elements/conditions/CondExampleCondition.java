package com.example.skriptaddon.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Example condition using PropertyCondition — a helper for simple "is/has" checks.
 * Auto-generates both positive and negative patterns:
 *   %players% (is|are) example dummy   /   %players% (isn't|is not) example dummy
 *
 * PropertyType options: BE ("is"), CAN ("can"), HAVE ("has"), WILL ("will")
 * For complex conditions, extend ch.njol.skript.lang.Condition directly.
 * Uses infoBuilder() for registration.
 */
@Name("Is Example Dummy")
@Description("Example condition from skript-addon-template. Checks if a player's name starts with 'A'.")
@Example("""
	if player is example dummy:
		send "Your name starts with A!" to player
	""")
@Since("1.0.0")
public class CondExampleCondition extends PropertyCondition<Player> {

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.CONDITION, infoBuilder(CondExampleCondition.class, PropertyType.BE,
				"example dummy", // property name — appears after "is" in syntax
				"players")       // type name — must match a registered Skript type (plural)
						.supplier(CondExampleCondition::new)
						.build());
	}

	// Only write the positive check — PropertyCondition handles negation automatically
	// Player is guaranteed non-null by PropertyCondition
	@Override
	public boolean check(@NotNull Player player) {
		return player.getName().startsWith("A");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "example dummy";
	}

}
