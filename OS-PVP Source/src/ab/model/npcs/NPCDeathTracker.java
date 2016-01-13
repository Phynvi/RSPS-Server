package ab.model.npcs;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ab.model.players.Player;
import ab.util.Misc;

/**
 * 
 * @author Jason MacKeigan
 * @date Jul 25, 2014, 10:43:59 PM
 */
public class NPCDeathTracker {

	/**
	 * The player this is relative to
	 */
	private Player player;
	
	/**
	 * A mapping of npcs names with their corresponding kill count
	 */
	private Map<NPCName, Integer> tracker = new HashMap<>();

	/**
	 * Creates a new {@link NPCDeathTracker} object for a singular player
	 * @param player	the player
	 */
	public NPCDeathTracker(Player player) {
		this.player = player;
	}

	/**
	 * Attempts to add a kill to the total amount of kill for a single npc
	 * @param name	the name of the npc
	 */
	public void add(String name) {
		if (name == null) {
			return;
		}
		for (NPCName boss : NPCName.NAMES) {
			if (boss.toString().equalsIgnoreCase(name)) {
				int kills = (tracker.get(boss) == null ? 0 : tracker.get(boss)) + 1;
				tracker.put(boss, kills);
				player.sendMessage("<col=255>You have killed</col> <col=FF0000>" + kills + "</col> <col=255>" + boss.toString() + ".</col>");
				break;
			}
		}
	}
	
	/**
	 * Determines the total amount of kills
	 * @return	the total kill count
	 */
	public long getTotal() {
		return tracker.values().stream().mapToLong(Integer::intValue).sum();
	}
	/**
	 * A mapping of npcs with their corresponding kill count 
	 * @return	the map
	 */
	public Map<NPCName, Integer> getTracker() {
		return tracker;
	}

	public enum NPCName {
		ZULRAH, KRAKEN, GIANT_MOLE, CHAOS_ELEMENTAL, KALPHITE_QUEEN, CALLISTO, VENENATIS, VETION,
		KING_BLACK_DRAGON, GENERAL_GRAARDOR, COMMANDER_ZILYANA, KREE_ARRA, KRIL_TSUTSAROTH,
		DAGANNOTH_REX, DAGANNOTH_SUPREME, DAGANNOTH_PRIME, BARRELCHEST, GREEN_DRAGON, BLUE_DRAGON,
		RED_DRAGON, BLACK_DRAGON, BLACK_DEMON, ABYSSAL_DEMON, CHAOS_DRUID, BLACK_KNIGHT,
		MAGIC_AXE, BANSHEE, BASILISK, COCKATRICE, KURASK, PYREFIEND, GIANT_BAT, BLOODVELD, BRONZE_DRAGON, CRAWLING_HAND,
		DARK_BEAST, DUST_DEVIL, EARTH_WARRIOR, FIRE_GIANT, GARGOYLE, GHOST, CAVE_HORROR, GREATER_DEMON, HELLHOUND, HILL_GIANT, INFERNAL_MAGE,
		IRON_DRAGON, LESSER_DEMON, MOSS_GIANT, NECHRYAEL, SKELETON, STEEL_DRAGON, TZHAAR_XIL, MITHRIL_DRAGON;

		@Override
		public String toString() {
			String name = this.name().toLowerCase();
			name = name.replaceAll("_", " ");
			name = Misc.capitalize(name);
			return name;
		}

		static final Set<NPCName> NAMES = EnumSet.allOf(NPCName.class);

		public static NPCName get(String name) {
			for (NPCName boss : NAMES) {
				if (boss.toString().equalsIgnoreCase(name))
					return boss;
			}
			return null;
		}
	}

}
