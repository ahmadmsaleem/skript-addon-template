package com.example.skriptaddon.elements.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Example section — runs its body only if the player's cooldown has expired.
 * Demonstrates loadCode() to compile indented code and walk() to execute it conditionally.
 *
 * Skript usage:
 *   with cooldown 5 seconds for player:
 *       send "Action performed!"
 */
@Name("Cooldown Section")
@Description("Runs the code inside only if the player's cooldown has expired. Skips silently if still on cooldown.")
@Example("""
		command /ability:
			trigger:
				with cooldown 5 seconds for player:
					send "Ability used!"
		""")
@Since("1.0.0")
public class SecCooldown extends Section {

	private static final Map<UUID, Long> cooldowns = new HashMap<>();

	public static void register(@NotNull SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecCooldown.class)
				.supplier(SecCooldown::new)
				.addPatterns("with cooldown %timespan% for %player%")
				.build());
	}

	private Expression<Timespan> timespanExpr;
	private Expression<Player> playerExpr;
	private Trigger trigger;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed,
						@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode,
						@NotNull List<TriggerItem> triggerItems) {
		timespanExpr = (Expression<Timespan>) expressions[0];
		playerExpr = (Expression<Player>) expressions[1];
		// Compile the indented code block into a Trigger we can run later
		trigger = loadCode(sectionNode, "cooldown section", getParser().getCurrentEvents());
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event event) {
		Player player = playerExpr.getSingle(event);
		Timespan timespan = timespanExpr.getSingle(event);
		if (player == null || timespan == null)
			return walk(event, false);

		UUID uuid = player.getUniqueId();
		long now = System.currentTimeMillis();
		long cooldownMs = timespan.getAs(Timespan.TimePeriod.MILLISECOND);

		Long lastUsed = cooldowns.get(uuid);
		if (lastUsed != null && (now - lastUsed) < cooldownMs) {
			// Still on cooldown — skip the section body
			return walk(event, false);
		}

		// Cooldown expired or first use — run the body and set new cooldown
		cooldowns.put(uuid, now);
		Trigger.walk(trigger, event);
		return walk(event, false);
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "with cooldown " + timespanExpr.toString(event, debug) + " for " + playerExpr.toString(event, debug);
	}

}
