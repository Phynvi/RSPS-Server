package ab;

import ab.model.content.zulrah.Zulrah;

public class Config {

	public static final int MAX_INCOMONG_PACKETS_PER_CYCLE = 100;

	public static boolean BOUNTY_HUNTER_ACTIVE = true;

	public static boolean NEW_DUEL_ARENA_ACTIVE = true;

	public static boolean PLACEHOLDER_ECONOMY = false; // this is just a
														// placeholder variable
														// for if the server is
														// in economy mode or
														// not, will be changed
														// later
	/**
	 * DO NOT REMOVE OR CHANGE THIS UNLESS YOU WANT THE NEW COMBAT SYSTEM.
	 */
	public static boolean NEW_COMBAT_TEST = false;

	/**
	 * Enable or disable server debugging.
	 */
	public static final boolean SERVER_DEBUG = false;

	/**
	 * Your server name.
	 */
	public static final String SERVER_NAME = "OS Perfection";

	/**
	 * The welcome message displayed once logged in the server.
	 */
	public static final String WELCOME_MESSAGE = "OS Perfection";
	
	/**
	 * The current message of the day, displayed once a player has logged in.
	 */
	public static final String MOTD = "Zulrah is working flawlessly!";

	/**
	 * A URL to your server forums. Not necessary needed.
	 */
	public static final String FORUMS = "";

	/**
	 * The client version you are using. Not necessary needed.
	 */
	public static final int CLIENT_VERSION = 317;

	/**
	 * The delay it takes to type and or send a message.
	 */
	public static int MESSAGE_DELAY = 6000;

	public static boolean sendServerPackets = false;

	public static final int[] CHEATPACKETS =
	/** P1****P2***P3***P4****P5***P6**P7**P8 ***/
	{ 7376, 7575, 7227, 8904, 5096, 9002, 2330, 7826 };
	/**
	 * The highest amount ID. Change is not needed here unless loading items
	 * higher than the 667 revision.
	 */
	public static final int ITEM_LIMIT = 19000;

	/**
	 * An integer needed for the above code.
	 */
	public static final int MAXITEM_AMOUNT = Integer.MAX_VALUE;

	/**
	 * The size of a players bank.
	 */
	public static final int BANK_SIZE = 350;

	/**
	 * The max amount of players until your server is full.
	 */
	public static final int MAX_PLAYERS = 1024;

	/**
	 * The delay of logging in from connections. Do not change this.
	 */
	public static final int CONNECTION_DELAY = 100;

	/**
	 * How many IP addresses can connect from the same network until the server
	 * rejects another.
	 */
	public static final int IPS_ALLOWED = 4;

	/**
	 * Change to true if you want to stop the world --8. Can cause screen
	 * freezes on SilabSoft Clients. Change is not needed.
	 */
	public static final boolean WORLD_LIST_FIX = false;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting",
			"Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming",
			"Runecrafting" };

	/**
	 * Items that cannot be sold in any stores.
	 */
	public static final int[] ITEM_SELLABLE = {11907, 6686, 6685, 3025, 3024, 2437, 2436, 2441, 2440, 3041, 3040, 2445, 2444, 4091, 4090, 4092, 4093, 4094, 3840, 3839, 3841, 2411, 2412, 2413, 4096, 4097, 4098, 7460, 7461, 7462, 10827, 10828, 10829, 1711, 1712, 1713, 2549, 2550, 2551, 4674, 4675, 4676, 1200, 1201, 1202, 1126, 1127, 1128, 4150, 4151, 4152, 5697, 5698, 5699, 2413, 2414, 2415, 11839, 11840, 11841, 2449, 2550, 2551, 1078, 1079, 1080, 2502, 2503, 2504, 2496, 2497, 2498,
			12899, 12926, 12931, 12954, 12006, 11864, 15573, 8135, 7460, 7461, 7462, 1200, 1201, 1202, 1126, 1127, 1128, 2413, 2414, 2415, 3104, 3105, 3106, 6106, 6107, 6108, 6109, 4501, 4502, 4503, 6567, 6568, 6569, 3841, 3842, 3843, 2496, 2497, 2498, 7457, 7458, 7459, 9184, 9185, 9186, 891, 892, 893, 3748, 3749, 3750, 2502, 2503, 2504, 1186, 1187, 1188, 
			12853, 611, 1959, 1960, 9920, 9921, 9922, 9923, 9924, 9925, 1050, 1051, 1044, 1045, 1046, 1047, 1048,
			1049, 1052, 3842, 3844, 3840, 8844, 8845, 8846, 8847, 8848, 8849, 8850, 10551, 10548, 6570, 7462, 7461, 7460, 7459, 7458, 7457, 7456,
			7455, 7454, 9748, 9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808, 9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772, 9778, 9787, 9811, 9766,
			9749, 9755, 9752, 9770, 9758, 9761, 9764, 9803, 9809, 9785, 9800, 9806, 9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 9767, 9747, 9753,
			9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 8839, 8840, 8842,
			11663, 11664, 11665, 10499, 995, 12650, 12649, 7582, 12651, 12652, 15567, 12644, 12645, 12643, 15568, 12653, 12655, 15571, 15572 };

	/**
	 * Items that cannot be traded or staked.
	 */
	public static final int[] ITEM_TRADEABLE = {12921, 12939, 12940, 12785, 12954, 15573, 8135, 11864, 12432, 12389, 12373, 12379, 12369, 12367, 12365, 12363, 10507, 6822, 6824, 6826, 6828, 6830, 6832,
			6834, 6836, 6838, 6840, 6842, 6844, 6846, 6848, 6850, 12853, 1959, 1960, 611, 1959, 1960, 9920, 9921, 9922, 9923, 9924, 9925, 12845,
			3842, 3844, 3840, 8844, 8845, 8846, 8847, 8848, 8849, 8850, 10551, 6570, 7462, 7461, 7460, 7459, 7458, 7457, 7456, 7455, 7454, 9748,
			9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808, 9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772, 9778, 9787, 9811, 9766, 9749, 9755, 9752,
			9770, 9758, 9761, 9764, 9803, 9809, 9785, 9800, 9806, 9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 9767, 9747, 9753, 9750, 9768, 9756,
			9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 8839, 8840, 8842, 10271, 10273, 10275,
			10277, 10279, 10269, 11663, 11664, 15572, 11665, 10499, 10548, 15098, 12650, 12649, 12651, 12652, 15567, 12644, 12645, 12643, 15568,
			12653, 12655, 15571, 7582, 12848, 12855, 12856, 12808, 12809, 12806, 12807, 7449, 12796, 12748, 12749, 12750, 12751, 12752, 12753, 12754,
			12755, 12756, 12211, 12205, 12207, 12213, 12241, 12235, 12237, 12243, 12283, 12277, 12279, 12281, 12309, 12311, 12313, 12514, 12299, 12301, 12303, 12305, 12307, 12321, 12323, 12325, 12516 };

	public static final int[] ITEMS_KEPT_ON_DEATH = { 12785, 12954, 15573, 8135, 11864, 12432, 12389, 12373, 12379, 12369, 12367, 12365, 12363, 7449, 611, 8840, 8839, 8842, 11664,
			15098, 12650, 12649, 12651, 12652, 15567, 12644, 12645, 12643, 15568, 12653, 12655, 15571, 11663, 11665, 6570, 8845, 8846, 8847, 8848,
			8849, 8850, 10551, 10548, 7462, 7461, 7460, 7459, 7458, 7457, 7456, 7455, 7582, 15572, 12855, 12856 };
	public static final int[] DROP_AND_DELETE_ON_DEATH = { 6822, 6824, 6826, 6828, 6830, 6832, 6834, 6836, 6838, 6840, 6842, 6844, 6846, 6848, 6850,
			10507 };

	/**
	 * Items that cannot be dropped.
	 */
	public static final int[] UNDROPPABLE_ITEMS = {12899, 11907, 12432, 12389, 12373, 12379, 12369, 12367, 12365, 12363, 6822, 6824, 6826, 6828, 6830, 6832, 6834,
			6836, 6838, 6840, 6842, 6844, 6846, 6848, 6850 };

	/**
	 * Items that are listed as fun weapons for duelling.
	 */
	public static final int[] FUN_WEAPONS = { 4151, 5698, 1231, 1215, 5680 };

	public static final String[] UNSPAWNABLE = {
			"ahrim", "reindeer", "token", "lamp", "warrior guild", "manta", "sea turtle", "tuna potato", "star bauble", "bauble", "tokkul", "grimy", "herb", "torag", "dharok", "overload", "tenderiser", "woolly",
			"bobble", "jester", "gilded", "legend", "hell", "dragon spear", "odium", "malediction", "callisto", "gods", "yrannical", "treasonous",
			"granite maul", "ancient mace", "super combat", "dragon halberd", "torstol", "d hally", "dragon hally", "karil", "defender icon",
			"attacker icon", "picture", "collector icon", "collecter icon", "healer icon", "crystal key", "essence", "3rd", "third", "bomb",
			"karamb", "guthan", "verac", "dark crab", "void", "uncut", "Restrict", "onyx amulet", "onyx ring", "spirit", "chisel", "statius", "vesta", "morrigan",
			"zuriel", "occult", "trident", "mystic smoke", "mystic steam", "tentacle", "dark bow", "ranger boots", "robin hood hat", "attack cape",
			"defence cape", "strength cape", "prayer cape", "constitution", "range cape", "ranged cape", "ranging cape", "magic cape", "herblore",
			"agility", "fletching", "crafting", "runecrafting", "runecraft", "farming", "hunter", "slayer", "summoning", "construction",
			"woodcutting", "firemaking", "fishing", "cooking", "smithing", "mining", "thieving", "arcane", "divine", "spectral", "wealth", "elysian",
			"spirit", "status", "hand cannon", "visage", "raw", "logs", "bar", "ore", "uncut", "dragon leather", "scroll", "hatchet", "iron axe",
			"steel axe", "bronze axe", "rune axe", "adamant axe", "mithril axe", "black axe", "dragon axe", "vesta", "pumpkin", "statius's",
			"zuriel", "morrigan", "dwarven", "statius", "fancy", "rubber", "sled", "flippers", "camo", "lederhosen", "mime", "lantern", "santa",
			"scythe", "bunny", "h'ween", "hween", "clue", "casket", "cash", "box", "cracker", "zuriel's", "Statius's", "torso", "fighter", "Statius",
			"skeleton", "chicken", "zamorak platebody", "guthix platebody", "saradomin plate", "grim reaper hood", "armadyl", "bandos",
			"armadyl cross", "graardor", "zilyana", "kree", "tsut", "mole", "kraken", "dagannoth", "king black dragon", "chaos ele",
			"staff of the dead", "staff of dead", "(i)", "ticket", "PK Point", "jester", "dragon defender", "fury", "mithril defender", "adamant defender", "rune defender", "elysian",
			"mystery box", "arcane", "chaotic", "Chaotic", "stream", "broken", "deg", "corrupt", "fire cape", "sigil", "godsword", "void seal",
			"lunar", "hilt", "(g)", "mage's book", "berserker ring", "warriors ring", "warrior ring", "warrior's ring", "archer", "archer's ring",
			"archer ring", "archers' ring", "seers' ring", "seer's ring", "seers ring", "mages' book", "wand", "(t)", "guthix", "zamorak",
			"saradomin", "scythe", "bunny ears", "zaryte bow", "armadyl battlestaff", "(i)", "infinity", "slayer", "korasi",
			"staff of light", "dice", "ardougne", "unarmed", "gloves", "dragon claws", "party", "santa", "completionist", "null", "coins",
			"tokhaar-kal", "tokhaar", "sanfew", "dragon defender", "zaryte", "coupon", "flippers", "dragonfire shield", "sled", "tzrek", "holiday tool",
			"ironman"};

	/**
	 * If administrators can trade or not.
	 */
	public static final boolean ADMIN_CAN_TRADE = true;

	/**
	 * If administrators can sell items or not.
	 */
	public static final boolean ADMIN_CAN_SELL_ITEMS = true;

	/**
	 * If administrators can drop items or not.
	 */
	public static final boolean ADMIN_DROP_ITEMS = true;
	
	/**
	 * Represents whether administrators are interactable.
	 */
	public static boolean ADMIN_ATTACKABLE = true;

	/**
	 * The starting location of your server.
	 */
	public static final int START_LOCATION_X = 3094;
	public static final int START_LOCATION_Y = 3480;

	/**
	 * The re-spawn point of when someone dies.
	 */
	public static final int RESPAWN_X = 3103;
	public static final int RESPAWN_Y = 3491;

	/**
	 * The re-spawn point of when a duel ends.
	 */
	public static final int DUELING_RESPAWN_X = 3362;
	public static final int DUELING_RESPAWN_Y = 3263;

	/**
	 * The point in where you spawn in a duel. Do not change this.
	 */
	public static final int RANDOM_DUELING_RESPAWN = 0;

	/**
	 * The level in which you can not teleport in the wild, and higher.
	 */
	public static final int NO_TELEPORT_WILD_LEVEL = 20;

	/**
	 * The time, in game cycles that the skull above a player should exist for.
	 * Every game cycle is 600ms, every minute has 60 seconds. Therefor there
	 * are 100 cycles in 1 minute. .600 * 100 = 60.
	 */
	public static final int SKULL_TIMER = 500;

	/**
	 * The maximum time for a player skull with an extension in the length.
	 */
	public static final int EXTENDED_SKULL_TIMER = 2000;

	/**
	 * How long the teleport block effect takes.
	 */
	public static final int TELEBLOCK_DELAY = 20000;

	/**
	 * Single and multi player killing zones.
	 */
	public static final boolean SINGLE_AND_MULTI_ZONES = true;

	/**
	 * Wilderness levels and combat level differences. Used when attacking
	 * players.
	 */
	public static final boolean COMBAT_LEVEL_DIFFERENCE = true;

	/**
	 * Combat level requirements needed to wield items.
	 */
	public static final boolean itemRequirements = true;

	public static final int ATTACK = 0;
	public static final int DEFENCE = 1;
	public static final int STRENGTH = 2;
	public static final int HITPOINTS = 3;
	public static final int RANGED = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 16;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	/**
	 * Combat experience rates.
	 */
	public static final int MELEE_EXP_RATE = 25;
	public static final int RANGE_EXP_RATE = 25;
	public static final int MAGIC_EXP_RATE = 25;

	/**
	 * Special server experience bonus rates. (Double experience weekend etc)
	 */

	public static final double SERVER_EXP_BONUS = 1;

	/**
	 * XP given when XP is boosted by a voting reward only
	 */
	public static final double SERVER_EXP_BONUS_BOOSTED = 1.15;
	/**
	 * XP given when XP is boosted by a voting reward and bonus mode
	 */
	public static final double SERVER_EXP_BONUS_WEEKEND_BOOSTED = 1.35;
	
	/**
	 * XP given when XP is boosted by bonus mode only
	 */
	public static final double SERVER_EXP_BONUS_WEEKEND = 1.2;
	
	public static boolean BONUS_WEEKEND = false;

	public static boolean CYBER_MONDAY = false;

	public static boolean mySql = false;

	/**
	 * How fast the special attack bar refills.
	 */
	public static final int INCREASE_SPECIAL_AMOUNT = 17500;

	/**
	 * If you need more than one prayer point to use prayer.
	 */
	public static final boolean PRAYER_POINTS_REQUIRED = true;

	/**
	 * If you need a certain prayer level to use a certain prayer.
	 */
	public static final boolean PRAYER_LEVEL_REQUIRED = true;

	/**
	 * If you need a certain magic level to use a certain spell.
	 */
	public static final boolean MAGIC_LEVEL_REQUIRED = true;

	/**
	 * How long the god charge spell lasts.
	 */
	public static final int GOD_SPELL_CHARGE = 300000;

	/**
	 * If you need runes to use magic spells.
	 */
	public static final boolean RUNES_REQUIRED = true;

	/**
	 * If you need correct arrows to use with bows.
	 */
	public static final boolean CORRECT_ARROWS = true;

	/**
	 * If the crystal bow degrades.
	 */
	public static final boolean CRYSTAL_BOW_DEGRADES = true;

	/**
	 * How often the server saves data.
	 */
	public static final int SAVE_TIMER = 60; // Saves every one minute.

	/**
	 * How far NPCs can walk.
	 */
	public static final int NPC_RANDOM_WALK_DISTANCE = 5; // 5x5 square, NPCs
															// would be able to
															// walk 25 squares
															// around.

	/**
	 * How far NPCs can follow you when attacked.
	 */
	public static final int NPC_FOLLOW_DISTANCE = 10; // 10 squares

	/**
	 * NPCs that act as if they are dead. (For salve amulet, etc)
	 */
	public static final int[] UNDEAD_NPCS = { 90, 91, 92, 93, 94, 103, 104, 73, 74, 75, 76, 77 };
	public static final String[] UNDEAD = {
		"armoured zombie", "ankou", "banshee", "crawling hand", "dried zombie", "ghost", "ghostly warrior", "ghast",
		"mummy", "mighty banshee", "revenant imp", "revenant goblin",  "revenant icefiend",  "revenant pyrefiend",
		"revenant hobgoblin",  "revenant vampyre",  "revenant werewolf", "revenant cyclops", "revenant darkbeast",
		"revenant demon", "revenant ork",  "revenant hellhound", "revenant knight", "revenant dragon",
		"shade", "skeleton", "skeleton brute", "skeleton thug", "skeleton warlord", "summoned zombie",
		"skorge", "tortured soul", "undead chicken", "undead cow", "undead one", "undead troll",
		"zombie", "zombie rat", "zogre"
	};
	/**
	 * Glory locations.
	 */
	public static final int EDGEVILLE_X = 3087;
	public static final int EDGEVILLE_Y = 3500;
	public static final String EDGEVILLE = "";
	public static final int AL_KHARID_X = 3293;
	public static final int AL_KHARID_Y = 3174;
	public static final String AL_KHARID = "";
	public static final int KARAMJA_X = 3087;
	public static final int KARAMJA_Y = 3500;
	public static final String KARAMJA = "";
	public static final int MAGEBANK_X = 2538;
	public static final int MAGEBANK_Y = 4716;
	public static final String MAGEBANK = "";
	
	/**
	 * Ring of Wealth locations.
	 */
	public static final int MISC_X = 2512;
	public static final int MISC_Y = 3860;
	public static final String MISC = "";
	public static final int GE_X = 3165;
	public static final int GE_Y = 3464;
	public static final String GE = "";
	public static final int FALADOR_PARK_X = 2995;
	public static final int FALADOR_PARK_Y = 3371;
	public static final String FALADOR_PARK = "";
	public static final int ZULANDRA_X = 2205;
	public static final int ZULANDRA_Y = 3055;
	public static final String ZULANDRA = "";

	/**
	 * Ring of Dueling locations.
	 */
	
	public static final int DUEL_ARENA_X = 3364;
	public static final int DUEL_ARENA_Y = 3265;
	public static final String DUEL_ARENA = "";
	public static final int CASTLE_WARS_X = 2441;
	public static final int CASTLE_WARS_Y = 3088;
	public static final String CASTLE_WARS = "";
	public static final int CLAN_WARS_X = 3387;
	public static final int CLAN_WARS_Y = 3160;
	public static final String CLAN_WARS = "";	
	
	/**
	 * Teleport spells.
	 **/
	/*
	 * Modern spells
	 */
	public static final int VARROCK_X = 3210;
	public static final int VARROCK_Y = 3424;
	public static final String VARROCK = "";

	public static final int LUMBY_X = 3222;
	public static final int LUMBY_Y = 3218;
	public static final String LUMBY = "";

	public static final int FALADOR_X = 2964;
	public static final int FALADOR_Y = 3378;
	public static final String FALADOR = "";

	public static final int CAMELOT_X = 2757;
	public static final int CAMELOT_Y = 3477;
	public static final String CAMELOT = "";

	public static final int ARDOUGNE_X = 2662;
	public static final int ARDOUGNE_Y = 3305;
	public static final String ARDOUGNE = "";

	public static final int WATCHTOWER_X = 3087;
	public static final int WATCHTOWER_Y = 3500;
	public static final String WATCHTOWER = "";

	public static final int TROLLHEIM_X = 3243;
	public static final int TROLLHEIM_Y = 3513;
	public static final String TROLLHEIM = "";

	/*
	 * Ancient spells
	 */
	public static final int PADDEWWA_X = 3098;
	public static final int PADDEWWA_Y = 9884;

	public static final int SENNTISTEN_X = 3322;
	public static final int SENNTISTEN_Y = 3336;

	public static final int KHARYRLL_X = 3492;
	public static final int KHARYRLL_Y = 3471;

	public static final int LASSAR_X = 3006;
	public static final int LASSAR_Y = 3471;

	public static final int DAREEYAK_X = 3161;
	public static final int DAREEYAK_Y = 3671;

	public static final int CARRALLANGAR_X = 3156;
	public static final int CARRALLANGAR_Y = 3666;

	public static final int ANNAKARL_X = 3288;
	public static final int ANNAKARL_Y = 3886;

	public static final int GHORROCK_X = 2977;
	public static final int GHORROCK_Y = 3873;
	//

	/**
	 * Timeout time.
	 */
	public static final int TIMEOUT = 20;

	/**
	 * Cycle time.
	 */
	public static final int CYCLE_TIME = 600;

	/**
	 * Buffer size.
	 */
	public static final int BUFFER_SIZE = 512;

	/**
	 * Slayer Variables.
	 */
	public static final int[][] SLAYER_TASKS = { { 1, 87, 90, 4, 5 }, // Low
																		// tasks
			{ 6, 7, 8, 9, 10 }, // Medium tasks
			{ 11, 12, 13, 14, 15 }, // High tasks
			{ 1, 1, 15, 20, 25 }, // Low requirements
			{ 30, 35, 40, 45, 50 }, // Medium requirements
			{ 60, 75, 80, 85, 90 } }; // High requirements

	/**
	 * Skill experience multipliers.
	 */
	public static final int WOODCUTTING_EXPERIENCE = 13;
	public static final int MINING_EXPERIENCE = 14;
	public static final int SMITHING_EXPERIENCE = 22;
	public static final int FARMING_EXPERIENCE = 9;
	public static final int FIREMAKING_EXPERIENCE = 12;
	public static final int HERBLORE_EXPERIENCE = 9;
	public static final int FISHING_EXPERIENCE = 10;
	public static final int AGILITY_EXPERIENCE = 38;
	public static final int PRAYER_EXPERIENCE = 38;
	public static final int RUNECRAFTING_EXPERIENCE = 4;
	public static final int CRAFTING_EXPERIENCE = 7;
	public static final int THIEVING_EXPERIENCE = 18;
	public static final int SLAYER_EXPERIENCE = 18;
	public static final int COOKING_EXPERIENCE = 7;
	public static final int FLETCHING_EXPERIENCE = 18;

}