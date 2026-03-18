package com.example.skriptaddon.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Example expression — returns a value.
 * Extends SimpleExpression<String> — the generic type is what this expression returns.
 * Uses DefaultSyntaxInfos.Expression.builder() for registration.
 *
 * NOTE: SimpleExpression is from ch.njol.skript.lang.util (NOT ch.njol.skript.lang)
 */
@Name("Example Greeting")
@Description("Returns a custom greeting for a player. This is an example expression from skript-addon-template.")
@Example("""
	command /greet <player>:
		trigger:
			send example greeting of arg-1 to sender
	""")
@Since("1.0.0")
public class ExprPlayerGreeting extends SimpleExpression<String> {

	// Must be public static void register(SyntaxRegistry) — called via reflection by main class
	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprPlayerGreeting.class, String.class)
				.supplier(ExprPlayerGreeting::new)
				.addPatterns("[example] greeting of %player%") // [example] is optional in syntax
				.build());
	}

	// Expression<Player>, not Player — actual value is only known at runtime
	private Expression<Player> playerExpression;

	// expressions[] matches %type% placeholders in order. matchedPattern = which pattern (0-indexed)
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		playerExpression = (Expression<Player>) expressions[0];
		return true; // true = parse succeeded
	}

	// Core method — produces the value. Returns null if expression can't produce a value.
	// @Nullable on the array means the entire result can be null (no value available)
	@Override
	protected String @Nullable [] get(@NotNull Event event) {
		Player player = playerExpression.getSingle(event); // getSingle() is @Nullable — always check!
		if (player == null)
			return null;
		return new String[]{"Hello, " + player.getName() + "!"};
	}

	@Override
	public boolean isSingle() {
		return true; // returns one value, not a list
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	// Used in debug output and error messages. Event is @Nullable (null during parsing)
	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "greeting of " + playerExpression.toString(event, debug);
	}

}
