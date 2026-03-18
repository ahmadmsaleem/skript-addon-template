package com.example.skriptaddon.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.example.skriptaddon.elements.types.TypeCustomColor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Expression to create a 2D point.
 *
 * Skript usage:
 *   set {_point} to point at 10, 20
 */
@Name("Point At")
@Description("Creates a 2D point from x and y coordinates.")
@Example("""
		set {_point} to point at 10, 20
		send "%{_point}%" to player
		""")
@Since("1.0.0")
public class ExprPointAt extends SimpleExpression<TypeCustomColor> {

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprPointAt.class, TypeCustomColor.class)
				.supplier(ExprPointAt::new)
				.addPatterns("point at %integer%, %integer%")
				.build());
	}

	private Expression<Integer> xExpr, yExpr;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		xExpr = (Expression<Integer>) expressions[0];
		yExpr = (Expression<Integer>) expressions[1];
		return true;
	}

	@Override
	protected TypeCustomColor @Nullable [] get(@NotNull Event event) {
		Integer x = xExpr.getSingle(event);
		Integer y = yExpr.getSingle(event);
		if (x == null || y == null)
			return null;
		return new TypeCustomColor[]{new TypeCustomColor(x, y)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends TypeCustomColor> getReturnType() {
		return TypeCustomColor.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "point at " + xExpr.toString(event, debug) + ", " + yExpr.toString(event, debug);
	}

}
