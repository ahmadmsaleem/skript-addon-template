package com.example.skriptaddon.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.example.skriptaddon.elements.structures.StructCustomConfig;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Expression to read values from a custom config structure.
 *
 * Skript usage:
 *   send config value "greeting" from "my-settings"
 */
@Name("Config Value")
@Description("Gets a value from a custom config structure by key and config name.")
@Example("""
		send config value "greeting" from "my-settings"
		""")
@Since("1.0.0")
public class ExprConfigValue extends SimpleExpression<String> {

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprConfigValue.class, String.class)
				.supplier(ExprConfigValue::new)
				.addPatterns("config value %string% from %string%")
				.build());
	}

	private Expression<String> keyExpr, configNameExpr;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		keyExpr = (Expression<String>) expressions[0];
		configNameExpr = (Expression<String>) expressions[1];
		return true;
	}

	@Override
	protected String @Nullable [] get(@NotNull Event event) {
		String key = keyExpr.getSingle(event);
		String configName = configNameExpr.getSingle(event);
		if (key == null || configName == null)
			return null;
		String value = StructCustomConfig.getValue(configName, key);
		if (value == null)
			return null;

		return new String[]{ChatColor.translateAlternateColorCodes('&', value)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "config value " + keyExpr.toString(event, debug) + " from " + configNameExpr.toString(event, debug);
	}

}
