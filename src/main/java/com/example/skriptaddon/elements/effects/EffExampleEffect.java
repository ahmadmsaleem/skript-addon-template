package com.example.skriptaddon.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Example effect — performs an action, returns nothing.
 * Uses SyntaxInfo.builder() for registration (no return type needed).
 */
@Name("Example Announce")
@Description("Sends an example announcement message to a player. From skript-addon-template.")
@Example("""
	on join:
		example announce player
	""")
@Since("1.0.0")
public class EffExampleEffect extends Effect {

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffExampleEffect.class)
				.supplier(EffExampleEffect::new)
				.addPatterns("example announce %player%")
				.build());
	}

	private Expression<Player> playerExpression;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		playerExpression = (Expression<Player>) expressions[0];
		return true;
	}

	// getSingle() is @Nullable — always null-check before using
	@Override
	protected void execute(@NotNull Event event) {
		Player player = playerExpression.getSingle(event);
		if (player != null) {
			player.sendMessage("Welcome to the server, " + player.getName() + "!");
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "example announce " + playerExpression.toString(event, debug);
	}

}
