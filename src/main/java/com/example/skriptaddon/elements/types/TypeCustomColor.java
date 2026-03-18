package com.example.skriptaddon.elements.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

/**
 * Example custom type — a simple 2D point (x, y).
 * Registered as ClassInfo so Skript knows how to parse, display, and save it.
 *
 * Skript usage:
 *   set {_point} to point at 10, 20
 *   send "%{_point}%" to player  → "Point(10, 20)"
 */
public class TypeCustomColor {

	private final int x, y;

	public TypeCustomColor(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { return x; }
	public int getY() { return y; }

	@Override
	public String toString() {
		return "Point(" + x + ", " + y + ")";
	}

	/**
	 * Registers this type with Skript. Called from init() in the main class
	 * because types MUST be registered before any syntax that uses them.
	 */
	public static void register() {
		Classes.registerClass(new ClassInfo<>(TypeCustomColor.class, "point2d")
				.user("2d ?points?")
				.name("2D Point")
				.description("Represents a simple 2D point with x and y coordinates. Example type from skript-addon-template.")
				.since("1.0.0")

				// Parser — how Skript reads/writes this type as text
				.parser(new Parser<>() {
					@Override
					public boolean canParse(@NotNull ParseContext context) {
						return false;
					}

					@Override
					public @NotNull String toString(TypeCustomColor point, int flags) {
						return point.toString();
					}

					@Override
					public @NotNull String toVariableNameString(TypeCustomColor point) {
						return point.x + "," + point.y;
					}
				})

				// Serializer — how it's saved/loaded from variable storage
				.serializer(new Serializer<>() {
					@Override
					public @NotNull Fields serialize(TypeCustomColor point) {
						Fields fields = new Fields();
						fields.putPrimitive("x", point.x);
						fields.putPrimitive("y", point.y);
						return fields;
					}

					@Override
					protected TypeCustomColor deserialize(@NotNull Fields fields) throws StreamCorruptedException {
						int x = fields.getPrimitive("x", int.class);
						int y = fields.getPrimitive("y", int.class);
						return new TypeCustomColor(x, y);
					}

					@Override
					public void deserialize(TypeCustomColor o, @NotNull Fields f) { }

					@Override
					public boolean mustSyncDeserialization() {
						return false;
					}

					@Override
					protected boolean canBeInstantiated() {
						return false;
					}
				}));
	}

}
