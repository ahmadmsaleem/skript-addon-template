package com.example.skriptaddon.elements.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Example event — maps a Bukkit event to a Skript event trigger.
 * Uses BukkitSyntaxInfos.Event.builder() for registration.
 * Documentation uses builder methods (addDescription, addExample) instead of annotations — both work.
 * Init takes Literal[] (parse-time constants) instead of Expression[] (runtime values).
 */
public class EvtExampleEvent extends SkriptEvent {

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(BukkitSyntaxInfos.Event.KEY, BukkitSyntaxInfos.Event.builder(EvtExampleEvent.class, "Example Sneak Toggle")
				.supplier(EvtExampleEvent::new)
				.addEvent(PlayerToggleSneakEvent.class) // the Bukkit event to listen for
				.addPatterns(
						"example sneak toggle",      // pattern 0: any toggle
						"example start sneak[ing]",  // pattern 1: start only
						"example stop sneak[ing]")   // pattern 2: stop only
				.addDescription("Example event from skript-addon-template. Called when a player toggles sneaking.")
				.addExample("""
						on example sneak toggle:
							send "You toggled sneak!" to player
						""")
				.addSince("1.0.0")
				.build());
	}

	private int matchedPattern;

	// Events use Literal[] (not Expression[]) — literals are known at parse time
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, @NotNull ParseResult parseResult) {
		this.matchedPattern = matchedPattern;
		return true;
	}

	// Called every time the Bukkit event fires — return true to run the Skript trigger
	@Override
	public boolean check(@NotNull Event event) {
		if (!(event instanceof PlayerToggleSneakEvent sneakEvent))
			return false;
		return switch (matchedPattern) {
			case 0 -> true;                     // any toggle
			case 1 -> sneakEvent.isSneaking();   // start sneaking
			case 2 -> !sneakEvent.isSneaking();  // stop sneaking
			default -> false;
		};
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return switch (matchedPattern) {
			case 1 -> "example start sneaking";
			case 2 -> "example stop sneaking";
			default -> "example sneak toggle";
		};
	}

}
