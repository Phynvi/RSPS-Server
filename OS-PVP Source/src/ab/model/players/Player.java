package ab.model.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import ab.Config;
import ab.Connection;
import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.content.PunishmentPanel;
import ab.model.content.RandomEventInterface;
import ab.model.content.achievement.AchievementHandler;
import ab.model.content.achievement.Achievements;
import ab.model.content.dialogue.DialogueManager;
import ab.model.content.instances.InstancedAreaManager;
import ab.model.content.kill_streaks.Killstreak;
import ab.model.content.presets.Presets;
import ab.model.content.titles.Titles;
import ab.model.content.zulrah.Zulrah;
import ab.model.holiday.HolidayStages;
import ab.model.items.EquipmentSet;
import ab.model.items.Item;
import ab.model.items.ItemAssistant;
import ab.model.items.ItemCombination;
import ab.model.items.bank.Bank;
import ab.model.items.bank.BankPin;
import ab.model.minigames.Barrows;
import ab.model.minigames.bounty_hunter.BountyHunter;
import ab.model.minigames.fight_cave.FightCave;
import ab.model.minigames.pest_control.PestControl;
import ab.model.minigames.pest_control.PestControlRewards;
import ab.model.minigames.warriors_guild.WarriorsGuild;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCDeathTracker;
import ab.model.npcs.NPCHandler;
import ab.model.npcs.PetHandler;
import ab.model.players.combat.CombatAssistant;
import ab.model.players.combat.Damage;
import ab.model.players.combat.DamageQueueEvent;
import ab.model.players.combat.Degrade;
import ab.model.players.combat.Hitmark;
import ab.model.players.skills.Agility;
import ab.model.players.skills.Cooking;
import ab.model.players.skills.Crafting;
import ab.model.players.skills.Runecrafting;
import ab.model.players.skills.SkillInterfaces;
import ab.model.players.skills.Skilling;
import ab.model.players.skills.Slayer;
import ab.model.players.skills.Smelting;
import ab.model.players.skills.Smithing;
import ab.model.players.skills.SmithingInterface;
import ab.model.players.skills.agility.AgilityHandler;
import ab.model.players.skills.agility.impl.GnomeAgility;
import ab.model.players.skills.agility.impl.WildernessAgility;
import ab.model.players.skills.farming.Farming;
import ab.model.players.skills.herblore.Herblore;
import ab.model.players.skills.mining.Mining;
import ab.model.players.skills.prayer.Prayer;
import ab.model.players.skills.thieving.Thieving;
import ab.model.shops.ShopAssistant;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.clan_wars.ClanWarsMap;
import ab.model.multiplayer_session.duel.Duel;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.trade.*;
import ab.net.Packet;
import ab.net.Packet.Type;
import ab.net.outgoing.UnnecessaryPacketDropper;
import ab.util.ISAACCipher;
import ab.util.Misc;
import ab.util.Stopwatch;
import ab.util.Stream;
import ab.world.Clan;
import ab.util.Throttler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
	private RandomEventInterface randomEventInterface = new RandomEventInterface(this);
	private Mining mining = new Mining(this);
	private PestControlRewards pestControlRewards = new PestControlRewards(this);
	private WarriorsGuild warriorsGuild = new WarriorsGuild(this);
	private Zulrah zulrah = new Zulrah(this);
	private NPCDeathTracker npcDeathTracker = new NPCDeathTracker(this);
	private UnnecessaryPacketDropper packetDropper = new UnnecessaryPacketDropper();
	private DamageQueueEvent damageQueue = new DamageQueueEvent(this);
	private HashMap<String, ArrayList<Damage>> damageReceived = new HashMap<>();
	private BountyHunter bountyHunter = new BountyHunter(this);
	private MysteryBox mysteryBox = new MysteryBox(this);
	private long lastContainerSearch;
	private HolidayStages holidayStages;
	private AchievementHandler achievementHandler;
	private PlayerKill playerKills;
	private String macAddress;
	private Duel duelSession = new Duel(this);
	private Player itemOnPlayer;
	private Presets presets = null;
	private Killstreak killstreaks;
	private PunishmentPanel punishmentPanel = new PunishmentPanel(this);

	@Override
	public String toString() {
		return "player[" + playerName + "]";
	}

	public byte buffer[] = null;
	public Stream inStream = null, outStream = null;
	private Channel session;
	private Trade trade = new Trade(this);
	private ItemAssistant itemAssistant = new ItemAssistant(this);
	private ShopAssistant shopAssistant = new ShopAssistant(this);
	private PlayerAssistant playerAssistant = new PlayerAssistant(this);
	private CombatAssistant combat = new CombatAssistant(this);
	private ActionHandler actionHandler = new ActionHandler(this);
	private DialogueHandler dialogueHandler = new DialogueHandler(this);
	private DialogueManager dialogue = new DialogueManager(this);
	private Friends friend = new Friends(this);
	private Ignores ignores = new Ignores(this);
	private Queue<Packet> queuedPackets = new ConcurrentLinkedQueue<>();
	private Potions potions = new Potions(this);
	private PotionMixing potionMixing = new PotionMixing(this);
	private Food food = new Food(this);
	private Logs logs = new Logs(this);
	private Killstreak killingStreak = new Killstreak(this);
	private SkillInterfaces skillInterfaces = new SkillInterfaces(this);

	/**
	 * Skill instances
	 */
	private PlayerAction playerAction = new PlayerAction(this);
	private Slayer slayer = new Slayer(this);
	private Runecrafting runecrafting = new Runecrafting();
	private AgilityHandler agilityHandler = new AgilityHandler();
	private PointItems pointItems = new PointItems(this);
	private GnomeAgility gnomeAgility = new GnomeAgility();
	private WildernessAgility wildernessAgility = new WildernessAgility();
	private Agility agility = new Agility(this);
	private Cooking cooking = new Cooking();
	private Crafting crafting = new Crafting(this);
	private Prayer prayer = new Prayer(this);
	private Smithing smith = new Smithing(this);
	private FightCave fightcave = null;
	private SmithingInterface smithInt = new SmithingInterface(this);
	private Herblore herblore = new Herblore(this);
	private Thieving thieving = new Thieving(this);
	private Barrows barrows = new Barrows(this);
	public Stopwatch potionTimer = new Stopwatch();
	public int[] degradableItem = new int[Degrade.MAXIMUM_ITEMS];
	public boolean[] claimDegradableItem = new boolean[Degrade.MAXIMUM_ITEMS];
	private Optional<ItemCombination> currentCombination = Optional.empty();
	public static PlayerSave save;
	public static Player cliento2;
	public int lowMemoryVersion = 0;
	public int timeOutCounter = 0;
	public int returnCode = 2;
	private Future<?> currentTask;
	public int currentRegion = 0;
	public long lastRoll;
	public int diceItem;
	public int page;
	public int specRestore = 0;
	private int privateChat;
	public boolean slayerHelmetEffect;
	public boolean inArdiCC;
	public boolean attackSkill = false;
	public boolean strengthSkill = false;
	public boolean defenceSkill = false;
	public boolean mageSkill = false;
	public boolean rangeSkill = false;
	public boolean prayerSkill = false;
	public boolean healthSkill = false;
	private Skilling skilling = new Skilling(this);
	public int lastClickedItem;
	private int[] farmingSeedId = new int[Farming.MAX_PATCHES];
	private int[] farmingTime = new int[Farming.MAX_PATCHES];
	private int[] farmingState = new int[Farming.MAX_PATCHES];
	private int[] farmingHarvest = new int[Farming.MAX_PATCHES];
	private Farming farming = new Farming(this);
	public int pestControlDamage;
	private long bestZulrahTime;
	private LostItems lostItems;
	private int toxicBlowpipeCharge;
	private int toxicBlowpipeAmmo;
	private int toxicBlowpipeAmmoAmount;
	private int serpentineHelmCharge;
	private int toxicStaffOfDeadCharge;
	private int tridentCharge;
	private int toxicTridentCharge;
	
	public Player(Channel s, int _playerId) {
		playerId = _playerId;
		rights = Rights.PLAYER;

		for (int i = 0; i < playerItems.length; i++) {
			playerItems[i] = 0;
		}
		for (int i = 0; i < playerItemsN.length; i++) {
			playerItemsN[i] = 0;
		}

		for (int i = 0; i < playerLevel.length; i++) {
			if (i == 3) {
				playerLevel[i] = 10;
			} else {
				playerLevel[i] = 1;
			}
		}

		for (int i = 0; i < playerXP.length; i++) {
			if (i == 3) {
				playerXP[i] = 1300;
			} else {
				playerXP[i] = 0;
			}
		}
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			bankItems[i] = 0;
		}

		for (int i = 0; i < Config.BANK_SIZE; i++) {
			bankItemsN[i] = 0;
		}

		playerAppearance[0] = 0; // gender
		playerAppearance[1] = 0; // head
		playerAppearance[2] = 18;// Torso
		playerAppearance[3] = 26; // arms
		playerAppearance[4] = 33; // hands
		playerAppearance[5] = 36; // legs
		playerAppearance[6] = 42; // feet
		playerAppearance[7] = 10; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 0; // torso colour
		playerAppearance[10] = 0; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0; // skin colour

		apset = 0;
		actionID = 0;

		playerEquipment[playerHat] = -1;
		playerEquipment[playerCape] = -1;
		playerEquipment[playerAmulet] = -1;
		playerEquipment[playerChest] = -1;
		playerEquipment[playerShield] = -1;
		playerEquipment[playerLegs] = -1;
		playerEquipment[playerHands] = -1;
		playerEquipment[playerFeet] = -1;
		playerEquipment[playerRing] = -1;
		playerEquipment[playerArrows] = -1;
		playerEquipment[playerWeapon] = -1;

		heightLevel = 0;

		teleportToX = Config.START_LOCATION_X;
		teleportToY = Config.START_LOCATION_Y;

		absX = absY = -1;
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		resetWalkingQueue();
		this.session = s;
		// synchronized(this) {
		outStream = new Stream(new byte[Config.BUFFER_SIZE]);
		outStream.currentOffset = 0;

		inStream = new Stream(new byte[Config.BUFFER_SIZE]);
		inStream.currentOffset = 0;
		buffer = new byte[Config.BUFFER_SIZE];
		// }
	}

	public Player getClient(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (validClient(i)) {
				Player client = getClient(i);
				if (client.playerName.toLowerCase().equalsIgnoreCase(name)) {
					return client;
				}
			}
		}
		return null;
	}

	private Bank bank;

	public Bank getBank() {
		if (bank == null)
			bank = new Bank(this);
		return bank;
	}

	private BankPin pin;

	public BankPin getBankPin() {
		if (pin == null)
			pin = new BankPin(this);
		return pin;
	}

	public void sendMessage(String s, int color) {
		// synchronized (this) {
		if (getOutStream() != null) {
			s = "<col=" + color + ">" + s + "</col>";
			outStream.createFrameVarSize(253);
			outStream.writeString(s);
			outStream.endFrameVarSize();
		}

	}
	
	public void sendDebugMessage(String s) {
		if (getRights().isOwner() && isDebug()) {
			if (getOutStream() != null) {
				outStream.createFrameVarSize(253);
				outStream.writeString(s);
				outStream.endFrameVarSize();
			}
		}
	}

	public Player getClient(int id) {
		return PlayerHandler.players[id];
	}

	public boolean validClient(int id) {
		if (id < 0 || id > Config.MAX_PLAYERS) {
			return false;
		}
		return validClient(getClient(id));
	}

	public boolean validClient(String name) {
		return validClient(getClient(name));
	}

	public boolean validClient(Player client) {
		return (client != null && !client.disconnected);
	}

	public void flushOutStream() {
		if (!session.isConnected() || disconnected
				|| outStream.currentOffset == 0)
			return;

		byte[] temp = new byte[outStream.currentOffset];
		System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
		Packet packet = new Packet(-1, Type.FIXED,
				ChannelBuffers.wrappedBuffer(temp));
		session.write(packet);
		outStream.currentOffset = 0;

	}

	private Map<Integer, TinterfaceText> interfaceText = new HashMap<>();

	public class TinterfaceText {
		public int id;
		public String currentState;

		public TinterfaceText(String s, int id) {
			this.currentState = s;
			this.id = id;
		}

	}

	public boolean checkPacket126Update(String text, int id) {
		if (interfaceText.containsKey(id)) {
			TinterfaceText t = interfaceText.get(id);
			if (text.equals(t.currentState)) {
				return false;
			}
		}
		interfaceText.put(id, new TinterfaceText(text, id));
		return true;
	}

	public void sendClan(String name, String message, String clan, int rights) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		message = message.substring(0, 1).toUpperCase() + message.substring(1);
		clan = clan.substring(0, 1).toUpperCase() + clan.substring(1);
		outStream.createFrameVarSizeWord(217);
		outStream.writeString(name);
		outStream.writeString(message);
		outStream.writeString(clan);
		outStream.writeWord(rights);
		outStream.endFrameVarSize();
	}

	public static final int PACKET_SIZES[] = {
			0, 0, 0, 1, -1, 0, 0, 0, 4, 0, // 0
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 16, 0, -1, -1, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			6, 10, -1, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, /* 0 */4, 7, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};

	public void resetRanks() {
		for (int i = 0; i < 10; i++) {
			ranks[i] = 0;
			rankPpl[i] = "";
		}
	}

	public void highscores() {
		getPA().sendFrame126("OS Perfection - Top PKers Online", 6399); // Title
		for (int i = 0; i < 10; i++) {
			if (ranks[i] > 0) {
				getPA().sendFrame126(
						"Rank " + (i + 1) + ": " + rankPpl[i] + " - Kills: "
								+ ranks[i] + "", 6402 + i);
			}
		}
		getPA().showInterface(6308);
		flushOutStream();
		resetRanks();
	}

	public int playerRank = 0;
	public static int[] ranks = new int[11];
	public static String[] rankPpl = new String[11];

	public void homeTeleport(int x, int y, int h) {
		if (homeTele == 9) {
			startAnimation(4850);
		} else if (homeTele == 7) {
			startAnimation(4853);
			gfx0(802);
		} else if (homeTele == 5) {
			startAnimation(4855);
			gfx0(803);
		} else if (homeTele == 3) {
			startAnimation(4857);
			gfx0(804);
		} else if (homeTele == 1) {
			homeTeleDelay = 0;
			homeTele = 0;
			teleportToX = x;
			teleportToY = y;
			heightLevel = h;
		}
	}
	
	public void destruct() {
		if (session == null)
			return;
		if (zulrah.getInstancedZulrah() != null) {
			InstancedAreaManager.getSingleton().disposeOf(zulrah.getInstancedZulrah());
		}
		if (getPA().viewingOtherBank) {
			getPA().resetOtherBank();
		}
		if (Boundary.isIn(this, PestControl.GAME_BOUNDARY)) {
			PestControl.removeGameMember(this);
		}
		if (Boundary.isIn(this, PestControl.LOBBY_BOUNDARY)) {
			PestControl.removeFromLobby(this);
		}
		if (underAttackBy > 0 || underAttackBy2 > 0)
			return;
		//new HighscoresHandler(this).start();
		/*if (!rights.isAdministrator() && !rights.isOwner()) {
			new HighscoresHandler(this).start();
		}*/
		if (disconnected == true) {
			saveCharacter = true;
		}
		Server.getMultiplayerSessionListener().removeOldRequests(this);
		if (clan != null) {
			clan.removeMember(this);
		}
		getFriends().notifyFriendsOfUpdate();
		Misc.println("[Logged out]: " + playerName + "");
		CycleEventHandler.getSingleton().stopEvents(this);
		disconnected = true;
		session.close();
		session = null;
		inStream = null;
		outStream = null;
		isActive = false;
		buffer = null;
		playerListSize = 0;
		for (int i = 0; i < maxPlayerListSize; i++)
			playerList[i] = null;
		absX = absY = -1;
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		resetWalkingQueue();
	}

	public void sendMessage(String s) {
		// synchronized (this) {
		if (getOutStream() != null) {
			outStream.createFrameVarSize(253);
			outStream.writeString(s);
			outStream.endFrameVarSize();
		}

	}

	public void setSidebarInterface(int menuId, int form) {
		// synchronized (this) {
		if (getOutStream() != null) {
			outStream.createFrame(71);
			outStream.writeWord(form);
			outStream.writeByteA(menuId);
		}

	}

	public void joinHelpClan() {
		if (clan == null) {
			Clan localClan = Server.clanManager.getClan("alex");
			if (localClan != null)
				localClan.addMember(this);
			else if ("help".equalsIgnoreCase(this.playerName))
				Server.clanManager.create(this);
			else {
				sendMessage(Misc.formatPlayerName("Alex")
						+ " has temporarily disabled the help chat.");
			}
			getPA().refreshSkill(21);
			getPA().refreshSkill(22);
			getPA().refreshSkill(23);
			// inArdiCC = true;
		}
	}

	public void loadHighscore() {
		getPA().sendFrame126("OS Perfection - Top PKers Online", 6399);
		getPA().sendFrame126("Close Window", 6401);
		getPA().sendFrame126(" ", 6402);
		getPA().sendFrame126(" ", 6403);
		getPA().sendFrame126(" ", 6404);
		getPA().sendFrame126(" ", 6405);
		getPA().sendFrame126("OS Perfection", 640);
		getPA().sendFrame126(" ", 6406);
		getPA().sendFrame126(" ", 6407);
		getPA().sendFrame126(" ", 6408);
		getPA().sendFrame126(" ", 6409);
		getPA().sendFrame126(" ", 6410);
		getPA().sendFrame126(" ", 6411);
		getPA().sendFrame126(" ", 8578);
		getPA().sendFrame126(" ", 8579);
		getPA().sendFrame126(" ", 8580);
		getPA().sendFrame126(" ", 8581);
		getPA().sendFrame126(" ", 8582);
		getPA().sendFrame126(" ", 8583);
		getPA().sendFrame126(" ", 8584);
		getPA().sendFrame126(" ", 8585);
		getPA().sendFrame126(" ", 8586);
		getPA().sendFrame126(" ", 8587);
		getPA().sendFrame126(" ", 8588);
		getPA().sendFrame126(" ", 8589);
		getPA().sendFrame126(" ", 8590);
		getPA().sendFrame126(" ", 8591);
		getPA().sendFrame126(" ", 8592);
		getPA().sendFrame126(" ", 8593);
		getPA().sendFrame126(" ", 8594);
		getPA().sendFrame126(" ", 8595);
		getPA().sendFrame126(" ", 8596);
		getPA().sendFrame126(" ", 8597);
		getPA().sendFrame126(" ", 8598);
		getPA().sendFrame126(" ", 8599);
		getPA().sendFrame126(" ", 8600);
		getPA().sendFrame126(" ", 8601);
		getPA().sendFrame126(" ", 8602);
		getPA().sendFrame126(" ", 8603);
		getPA().sendFrame126(" ", 8604);
		getPA().sendFrame126(" ", 8605);
		getPA().sendFrame126(" ", 8606);
		getPA().sendFrame126(" ", 8607);
		getPA().sendFrame126(" ", 8608);
		getPA().sendFrame126(" ", 8609);
		getPA().sendFrame126(" ", 8610);
		getPA().sendFrame126(" ", 8611);
		getPA().sendFrame126(" ", 8612);
		getPA().sendFrame126(" ", 8613);
		getPA().sendFrame126(" ", 8614);
		getPA().sendFrame126(" ", 8615);
		getPA().sendFrame126(" ", 8616);
		getPA().sendFrame126(" ", 8617);
	}

	public void updateHighscores() {
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				int totalz = KC;
				for (int d = 0; d <= 10; d++) {
					if (totalz >= ranks[d]) {
						if (d == 0) {
							playerRank = d + 1;
							ranks[d] = totalz;
							rankPpl[d] = playerName;
						} else if (d < 10) {
							if (totalz < ranks[d - 1]) {
								playerRank = d + 1;
								ranks[d] = totalz;
								rankPpl[d] = playerName;
							}
						} else {
							if (totalz < ranks[d - 1]) {
								playerRank = 0;
							}
						}
					}
				}
				// System.out.println("Highscores updated succesfully!");
			}

			@Override
			public void stop() {

			}
		}, 50);
	}

	public String randomMessage() {
		int randomMessage = Misc.random(4);
		switch(randomMessage) {
			case 0: return "<img=10><col=255> Did you know? You can help the server grow by voting every 12 hours! ::vote <img=10>";
			case 1: return "<img=10><col=255> Did you know? You can type ::help to submit a ticket to all staff members! <img=10>";
			case 2: return "<img=10><col=255> Did you know? You can upgrade your account and get great benefits! ::store <img=10>";
			case 3: return "<img=10><col=255> Did you know? OS Perfection is the #1 PvP Server! Tell all of your friends! <img=10>";	
			}
		return "<img=10><col=255> Did you know? We have a great set of community forums! Join now! ::forums <img=10>";
	}

	public void serverMessages() {
   		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
					sendMessage(randomMessage());		
					}						
					@Override
					public void stop() {
				}
			}, 1500);
		}
	
	public void initialize() {
		try {
			Achievements.checkIfFinished(this);
			getPA().loadQuests();
			Connection.appendStarters();
			Connection.appendStarters2();
			setStopPlayer(false);
			getPlayerAction().setAction(false);
			getPlayerAction().canWalk(true);
			getPA().sendFrame126(runEnergy + "%", 149);
			isFullHelm = Item.isFullHelm(playerEquipment[playerHat]);
			isFullMask = Item.isFullMask(playerEquipment[playerHat]);
			isFullBody = Item.isFullBody(playerEquipment[playerChest]);
			getPA().sendFrame36(173, isRunning2 ? 1 : 0);
			serverMessages();
			sendMessage("<img=10>Welcome to <col=255>OS Perfection@bla@!");
			//sendMessage("<img=10>Use @blu@::commands@bla@ to see your available commands!");
			//sendMessage("<img=10>Remember to @blu@::vote @bla@and @red@::store @bla@to support the server!");
			//sendMessage("<img=10>Keep your account safe - change your password! @red@::changepassword newpass");
			//sendMessage("<img=10>Please take a moment to register at @blu@::forums@bla@!");
			//sendMessage("<img=10>Latest updates: @red@" + Config.MOTD);
			if (Config.BONUS_WEEKEND)
				sendMessage("<img=10>Bonus Weekend is @red@LIVE@bla@! Bonus voting, PKP and XP!");
			if (Config.CYBER_MONDAY)
				sendMessage("@cr8@Christmas sale is @red@LIVE@bla@! 20% extra points on all purchases! @red@::store");
			combatLevel = calculateCombatLevel();
			/*
			 * if (playerRights != 3 || playerRights != 2) { Highscores.process(); }
			 */
			outStream.createFrame(249);
			outStream.writeByteA(1); // 1 for members, zero for free
			outStream.writeWordBigEndianA(playerId);
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (j == playerId)
					continue;
				if (PlayerHandler.players[j] != null) {
					if (PlayerHandler.players[j].playerName
							.equalsIgnoreCase(playerName))
						disconnected = true;
				}
			}
			for (int p = 0; p < PRAYER.length; p++) { // reset prayer glows
				prayerActive[p] = false;
				getPA().sendFrame36(PRAYER_GLOW[p], 0);
			}
			getPA().handleWeaponStyle();
			getPA().handleLoginText();
			accountFlagged = getPA().checkForFlags();
			getPA().sendFrame36(108, 0);// resets autocast button
			getPA().sendFrame36(172, 1);
			getPA().sendFrame107(); // reset screen
			setSidebarInterface(1, 3917);
			setSidebarInterface(2, 638);
			setSidebarInterface(3, 3213);
			setSidebarInterface(4, 1644);
			//getPA().sendFrame171(0, 27670);
			setSidebarInterface(5, 5608);
			if (playerMagicBook == 0) {
				setSidebarInterface(6, 28060); // modern
			}
			if (playerMagicBook == 1) {
				setSidebarInterface(6, 28062); // ancient
			}
			if (playerMagicBook == 2) {
				setSidebarInterface(6, 28064); // ancient
			}
			if (hasNpc) {
				if (summonId > 0) {
					PetHandler.spawnPet(this, summonId, -1, true);
				}
			}
			if (splitChat) {
				getPA().sendFrame36(502, 1);
				getPA().sendFrame36(287, 1);			
			}
			setSidebarInterface(7, -1);
			setSidebarInterface(8, 5065);
			setSidebarInterface(9, 5715);
			setSidebarInterface(10, 18128);
			setSidebarInterface(11, 904); // wrench tab
			setSidebarInterface(12, 147); // run tab
			setSidebarInterface(13, -1);
			setSidebarInterface(0, 2423);
			getPA().showOption(4, 0, "Follow", 3);
			getPA().showOption(5, 0, "Trade with", 4);
			getPA().showOption(6, 0, "Moderate", 5);
			getItems().resetItems(3214);
			getItems().sendWeapon(playerEquipment[playerWeapon],
					getItems().getItemName(playerEquipment[playerWeapon]));
			getItems().resetBonus();
			getItems().getBonus();
			getItems().writeBonus();
			getItems().setEquipment(playerEquipment[playerHat], 1, playerHat);
			getItems().setEquipment(playerEquipment[playerCape], 1, playerCape);
			getItems().setEquipment(playerEquipment[playerAmulet], 1, playerAmulet);
			getItems().setEquipment(playerEquipment[playerArrows],
					playerEquipmentN[playerArrows], playerArrows);
			getItems().setEquipment(playerEquipment[playerChest], 1, playerChest);
			getItems().setEquipment(playerEquipment[playerShield], 1, playerShield);
			getItems().setEquipment(playerEquipment[playerLegs], 1, playerLegs);
			getItems().setEquipment(playerEquipment[playerHands], 1, playerHands);
			getItems().setEquipment(playerEquipment[playerFeet], 1, playerFeet);
			getItems().setEquipment(playerEquipment[playerRing], 1, playerRing);
			getItems().setEquipment(playerEquipment[playerWeapon],
					playerEquipmentN[playerWeapon], playerWeapon);
			getCombat().getPlayerAnimIndex(ItemAssistant.getItemName(playerEquipment[playerWeapon]).toLowerCase());
			if (getPrivateChat() > 2) {
				setPrivateChat(0);
			}
			if (Boundary.isIn(this, PestControl.GAME_BOUNDARY)) {
				getPA().movePlayer(2662, 2652, 0);
			}
			/* Login Friend List */
			outStream.createFrame(221);
			outStream.writeByte(2);
	
			outStream.createFrame(206);
			outStream.writeByte(0);
			outStream.writeByte(getPrivateChat());
			outStream.writeByte(0);
			getFriends().sendList();
			getIgnores().sendList();
	
			getItems().addSpecialBar(playerEquipment[playerWeapon]);
			saveTimer = Config.SAVE_TIMER;
			saveCharacter = true;
			// HighscoresConfig.updateHighscores(this);
			Misc.println("[Logged in]: " + playerName + "");
			handler.updatePlayer(this, outStream);
			handler.updateNPC(this, outStream);
			flushOutStream();
			totalLevel = getPA().totalLevel();
			xpTotal = getPA().xpTotal();
			getPA().sendFrame126("Combat Level: " + combatLevel + "", 3983);
			getPA().sendFrame126("Level: " + totalLevel + "", 3984);
			getPA().resetFollow();
			getPA().clearClanChat();
			getPA().resetFollow();
			getPA().setClanData();
			if (startPack == false) {
				getPA().addStarter();
				startPack = true;
				Server.clanManager.getHelpClan().addMember(this);
			}
			if (autoRet == 1)
				getPA().sendFrame36(172, 1);
			else
				getPA().sendFrame36(172, 0);
			addEvents();
			if (Config.BOUNTY_HUNTER_ACTIVE) {
				bountyHunter.updateTargetUI();		
			}
			for (int i = 0; i < playerLevel.length; i++) {
				/**
				 * WARNING; Do not tamper with the uncommented code.
				 */
				//if (i == 3 || i == 5) {
				//	continue;
				//}
	 			//playerLevel[i] = getPA().getLevelForXP(playerXP[i]);
				for(int level = 7; level < 25; level++)
					getPA().setSkillLevel(level, 99, 13_100_100);
				getPA().refreshSkill(i);
			}
			correctCoordinates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addEvents() {
		if (Config.BOUNTY_HUNTER_ACTIVE) {
			CycleEventHandler.getSingleton().addEvent(this, bountyHunter, 1);
		}
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.PLAYER_COMBAT_DAMAGE, this, damageQueue, 1, true);
	}
	
	public void update() {
		handler.updatePlayer(this, outStream);
		handler.updateNPC(this, outStream);
		flushOutStream();

	}

	public void wildyWarning() {
		getPA().showInterface(1908);
	}

	public boolean inProcess = false;

	public void logout() {
		if (this.clan != null) {
			this.clan.removeMember(this);
		}
		if (getPA().viewingOtherBank) {
			getPA().resetOtherBank();
		}
		/*
		 * if (playerRights != 3 || playerRights != 2) { Highscores.save(this);
		 * }
		 */
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(this, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() >= MultiplayerSessionStage.FURTHER_INTERACTION) {
				sendMessage("You are not permitted to logout during a duel. If you forcefully logout you will");
				sendMessage("lose all of your staked items, if any, to your opponent.");
			}
		}
		if (underAttackBy > 0 || underAttackBy2 > 0)
			return;
		// synchronized (this) {
		if (logoutDelay.elapsed(10000)) {
			outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(this);
			properLogout = true;
			ConnectedFrom.addConnectedFrom(this, connectedFrom);
		} else {
			sendMessage("You must wait a few seconds from being out of combat to logout.");
		}

	}

	public int packetSize = 0, packetType = -1;
	private long lastOverloadBoost;
	
	public long lastDamageCalculation;
	public long lastUpdate = System.currentTimeMillis();

	public void process() {
		farming.farmingProcess();
		if (updateItems) {
			itemAssistant.updateItems();
		}
		if (isDead && respawnTimer == -6) {
			getPA().applyDead();
		}
		if (bonusXpTime > 0) {
			bonusXpTime --;
		}
		getPA().sendFrame126((int) (specAmount * 10)+"", 155);
		if (bonusXpTime == 1) {
			sendMessage("@blu@Your time is up. Your XP is no longer boosted by the voting reward.");
		}
		if (respawnTimer == 7) {
			respawnTimer = -6;
			getPA().giveLife();
		} else if (respawnTimer == 12) {
			respawnTimer--;
			startAnimation(0x900);
		}
		if (Boundary.isIn(this, Zulrah.BOUNDARY) && getZulrahEvent().isInToxicLocation()) {
			appendDamage(1 + Misc.random(3), Hitmark.VENOM);
		}
		if (getPoisonDamage() > 0 && System.currentTimeMillis() - getLastPoisonHit()
				> TimeUnit.MINUTES.toMillis(1)) {
			appendPoisonDamage();
			setLastPoisonHit(System.currentTimeMillis());
			sendMessage("You have been poisoned!");
		}
		if (getVenomDamage() > 0 && System.currentTimeMillis() - getLastVenomHit()
				> 15_0000) {
			appendVenomDamage();
			setLastVenomHit(System.currentTimeMillis());
			sendMessage("You have been hit by the venom infection.");
		}
		//if (lastUpdate > 100000) {
	//		Test.run();
	//	}
		
		if (respawnTimer > -6) {
			respawnTimer--;
		}
		
		if (freezeTimer > -6) {
            freezeTimer--;
            if (freezeTimer == 0) {
                getPA().sendFrame126("freezetimer:-2", 1810);
            }
            if (frozenBy > 0) {
                if (PlayerHandler.players[frozenBy] == null) {
                    freezeTimer = -1;
                    frozenBy = -1;
                    getPA().sendFrame126("freezetimer:-2", 1810);
                } else if (!Misc.goodDistance(absX, absY, PlayerHandler.players[frozenBy].absX, PlayerHandler.players[frozenBy].absY, 12)) {
                    freezeTimer = -1;
                    frozenBy = -1;
                    getPA().sendFrame126("freezetimer:-2", 1810);
                }
            }
        }
		
		if (hitDelay > 0) {
			hitDelay--;
		}

		getAgilityHandler().agilityProcess(this);
		if (specRestore > 0) {
			specRestore--;
		}

		if (rangeDelay > 0) {
			rangeDelay--;
		}
		if (pTime != 2147000000) {
			pTime++;
		}
		getPA().sendFrame126(
				"Players Online: @gre@" + PlayerHandler.getPlayerCount() + "",
				29155);
		// getPA().sendFrame126("@gre@ My Statistics:", 29161);
		if (rights.isPlayer())
			getPA().sendFrame126("@or1@Rank: Player", 29162);
		if (rights.isModerator())
			getPA().sendFrame126("@or1@Rank: <col=148200>Moderator @cr1@",
					29162);
		if (rights.isAdministrator() || rights.isOwner())
			getPA().sendFrame126("@or1@Rank: <col=A67711>Owner @cr2@", 29162);
		if (rights.isContributor())
			getPA().sendFrame126("@or1@Rank: @red@Donator@cr4@", 29162);
		if (rights.isSponsor())
			getPA().sendFrame126("@or1@Rank: @blu@Sponsor@cr5@", 29162);
		if (rights.isSupporter())
			getPA().sendFrame126("@or1@Rank: @gre@Supporter @cr6@", 29162);
		if (rights.isVIP())
			getPA().sendFrame126("@or1@Rank: <col=FF00CD>V.I.P @cr7@", 29162);
		if (rights.isSuperVIP())
			getPA().sendFrame126("@or1@Rank: @yel@Super V.I.P@cr8@", 29162);
		if (rights.isRespectedMember())
			getPA().sendFrame126("@or1@Rank: <col=FF00CD>Respected @cr9@",
					29162);
		if (rights.isHelper())
			getPA().sendFrame126("@or1@Rank: @blu@Helper @cr10@", 29162);
		getPA().sendFrame126("@or1@PK Points: @gre@" + pkp, 29163);
		getPA().sendFrame126(
				"@or1@Kills/Deaths: @gre@" + KC + "@or1@/@gre@" + DC, 29164);
		getPA().sendFrame126("@or1@Hunter KS: @gre@" + getKillstreak().getAmount(Killstreak.Type.HUNTER) + "@or1@, "
				 + "Rogue KS: @gre@" + getKillstreak().getAmount(Killstreak.Type.ROGUE), 29165);
		if (specDelay.elapsed(Config.INCREASE_SPECIAL_AMOUNT * 20)) {
			specDelay.reset();
			if (specAmount < 10) {
				specAmount += 10;
				if (specAmount > 10)
					specAmount = 10;
				getItems().addSpecialBar(playerEquipment[playerWeapon]);
			}
		}

		getCombat().handlePrayerDrain();
		if (singleCombatDelay.elapsed(6000)) {
			underAttackBy = 0;
		}
		if (singleCombatDelay2.elapsed(6000)) {
			underAttackBy2 = 0;
		}
        if (hasOverloadBoost) {
        	if(System.currentTimeMillis() - lastOverloadBoost > 15000) {
        		getPotions().doOverloadBoost();
        		lastOverloadBoost = System.currentTimeMillis();
        	}
        }
		if (System.currentTimeMillis() - restoreStatsDelay > 60000) {
			restoreStatsDelay = System.currentTimeMillis();
			for (int level = 0; level < playerLevel.length; level++) {
				if (playerLevel[level] < getLevelForXP(playerXP[level])) {
					if (level < 7 && level != 5 && level != 3) {
		        		if(hasOverloadBoost)
		        			continue;
					}
					if (level != 5) { // prayer doesn't restore
						playerLevel[level] += 1;
						getPA().setSkillLevel(level, playerLevel[level],
								playerXP[level]);
						getPA().refreshSkill(level);
					}
				} else if (playerLevel[level] > getLevelForXP(playerXP[level])) {
					playerLevel[level] -= 1;
					getPA().setSkillLevel(level, playerLevel[level],
							playerXP[level]);
					getPA().refreshSkill(level);
				}
			}
		}

		if (inWild()) {
			int modY = absY > 6400 ? absY - 6400 : absY;
			wildLevel = (((modY - 3520) / 8) + 1);
			if (Config.SINGLE_AND_MULTI_ZONES) {
				getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
			} else {
				getPA().multiWay(-1);
				getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
			}
			getPA().showOption(3, 0, "Attack", 1);
			if (Config.BOUNTY_HUNTER_ACTIVE) {
				getPA().walkableInterface(28000);
				getPA().sendFrame171(1, 28070);
				getPA().sendFrame171(0, 196);
			} else {
				getPA().walkableInterface(197);
			}
		} else if (inClanWars()) {
			getPA().showOption(3, 0, "Attack", 1);
			if (Boundary.isIn(this, ClanWarsMap.FREE_FOR_ALL.getBoundary())) {
				getPA().walkableInterface(197);
			}
		} else if (inEdgeville()) {
			if (Config.BOUNTY_HUNTER_ACTIVE) {
				if (bountyHunter.hasTarget()) {
					getPA().walkableInterface(28000);
					getPA().sendFrame171(0, 28070);
					getPA().sendFrame171(1, 196);
					bountyHunter.updateOutsideTimerUI();
				} else {
					getPA().walkableInterface(-1);
				}
			} else {
				getPA().sendFrame99(0);
				getPA().walkableInterface(-1);
				getPA().showOption(3, 0, "Null", 1);
			}
			getPA().showOption(3, 0, "null", 1);
		/*} else if (Dicing.inDiceArea(this)) {
			if (!this.getItems().playerHasItem(Dicing.DICE)) {
				getPA().showOption(3, 1, "Dice With", 1);
			} else if (this.getItems().playerHasItem(Dicing.DICE)) {
				getPA().showOption(3, 1, "null", 1);
			}*/
		} else if (Boundary.isIn(this, PestControl.LOBBY_BOUNDARY)) {
			getPA().walkableInterface(21119);
			PestControl.drawInterface(this, "lobby");
		} else if (Boundary.isIn(this, PestControl.GAME_BOUNDARY)) {
			getPA().walkableInterface(21100);
			PestControl.drawInterface(this, "game");
		} else if ((inDuelArena() || Boundary.isIn(this, Boundary.DUEL_ARENAS)) && !inDice()) {
			getPA().walkableInterface(201);
			if (Boundary.isIn(this, Boundary.DUEL_ARENAS)) {
				getPA().showOption(3, 0, "Attack", 1);
			} else {
				getPA().showOption(3, 0, "Challenge", 1);
			}
			wildLevel = 126;
		} else if (isInBarrows() || isInBarrows2()) {
			// getPA().sendFrame99(0);
			getPA().sendFrame126("Kill Count: " + barrowsKillCount, 4536);
			getPA().walkableInterface(4535);
		} else if (inCwGame || inPits) {
			getPA().showOption(3, 0, "Attack", 1);
		} else if (getPA().inPitsWait()) {
			getPA().showOption(3, 0, "Null", 1);
		} else if (!inCwWait) {
			getPA().sendFrame99(0);
			getPA().walkableInterface(-1);
			getPA().showOption(3, 0, "Null", 1);
		}

		if (!inWild()) {
			wildLevel = 0;
		}
		if (!hasMultiSign && inMulti()) {
			hasMultiSign = true;
			getPA().multiWay(1);
		}

		if (hasMultiSign && !inMulti()) {
			hasMultiSign = false;
			getPA().multiWay(-1);
		}
		if (!inMulti() && inWild())
			getPA().sendFrame70(30, 0, 196);
		else if (inMulti() && inWild())
			getPA().sendFrame70(0, 0, 196);
		if (this.skullTimer > 0) {
			--skullTimer;
			if (skullTimer == 1) {
				isSkulled = false;
				attackedPlayers.clear();
				headIconPk = -1;
				skullTimer = -1;
				getPA().requestUpdates();
			}
		}

		if (freezeTimer > -6) {
			freezeTimer--;
			if (frozenBy > 0) {
				if (PlayerHandler.players[frozenBy] == null) {
					freezeTimer = -1;
					frozenBy = -1;
				} else if (!goodDistance(absX, absY,
						PlayerHandler.players[frozenBy].absX,
						PlayerHandler.players[frozenBy].absY, 20)) {
					freezeTimer = -1;
					frozenBy = -1;
				}
			}
		}

		if (teleTimer > 0) {
			teleTimer--;
			if (!isDead) {
				if (teleTimer == 1 && newLocation > 0) {
					teleTimer = 0;
					getPA().changeLocation();
				}
				if (teleTimer == 5) {
					teleTimer--;
					getPA().processTeleport();
				}
				if (teleTimer == 9 && teleGfx > 0) {
					teleTimer--;
					gfx100(teleGfx);
				}
			} else {
				teleTimer = 0;
			}
		}

		if (attackTimer > 0) {
			attackTimer--;
		}

		if (followId > 0) {
			getPA().followPlayer();
		} else if (followId2 > 0) {
			getPA().followNpc();
		}
		
		if (hitDelay == 1) {
			if (oldNpcIndex > 0) {
				getCombat().delayedHit(this, oldNpcIndex);
			}
		}
		if (attackTimer <= 1) {
			if (npcIndex > 0 && clickNpcType == 0) {
				getCombat().attackNpc(npcIndex);
			}
			if (playerIndex > 0) {
				getCombat().attackPlayer(playerIndex);
			}
		}

	}

	public void setCurrentTask(Future<?> task) {
		currentTask = task;
	}

	public Future<?> getCurrentTask() {
		return currentTask;
	}

	public Stream getInStream() {
		return inStream;
	}

	public int getPacketType() {
		return packetType;
	}

	public int getPacketSize() {
		return packetSize;
	}

	public Stream getOutStream() {
		return outStream;
	}

	public ItemAssistant getItems() {
		return itemAssistant;
	}

	public PlayerAssistant getPA() {
		return playerAssistant;
	}

	public DialogueHandler getDH() {
		return dialogueHandler;
	}
	
	public DialogueManager dialogue() {
		return dialogue;
	}

	public ShopAssistant getShops() {
		return shopAssistant;
	}

	public CombatAssistant getCombat() {
		return combat;
	}

	public ActionHandler getActions() {
		return actionHandler;
	}

	public Killstreak getStreak() {
		return killingStreak;
	}

	public Channel getSession() {
		return session;
	}

	public Potions getPotions() {
		return potions;
	}

	public PotionMixing getPotMixing() {
		return potionMixing;
	}

	public Food getFood() {
		return food;
	}

	public boolean ardiRizal() {
		if ((playerEquipment[playerHat] == -1)
				&& (playerEquipment[playerCape] == -1)
				&& (playerEquipment[playerAmulet] == -1)
				&& (playerEquipment[playerChest] == -1)
				&& (playerEquipment[playerShield] == -1)
				&& (playerEquipment[playerLegs] == -1)
				&& (playerEquipment[playerHands] == -1)
				&& (playerEquipment[playerFeet] == -1)
				&& (playerEquipment[playerWeapon] == -1)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isBusy = false;
	private boolean isBusyHP = false;
	public boolean isBusyFollow = false;

	public boolean checkBusy() {
		/*
		 * if (getCombat().isFighting()) { return true; }
		 */
		if (isBusy) {
			// actionAssistant.sendMessage("You are too busy to do that.");
		}
		return isBusy;
	}

	public boolean checkBusyHP() {
		return isBusyHP;
	}

	public boolean checkBusyFollow() {
		return isBusyFollow;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusyFollow(boolean isBusyFollow) {
		this.isBusyFollow = isBusyFollow;
	}

	public void setBusyHP(boolean isBusyHP) {
		this.isBusyHP = isBusyHP;
	}

	public boolean isBusyHP() {
		return isBusyHP;
	}

	public boolean isBusyFollow() {
		return isBusyFollow;
	}

	public int makeTimes;
	public int event;
	public long lastBankDeposit;
	public boolean hasOverloadBoost;

	public boolean canWalk() {
		return canWalk;
	}

	public void setCanWalk(boolean canWalk) {
		this.canWalk = canWalk;
	}

	public PlayerAssistant getPlayerAssistant() {
		return playerAssistant;
	}

	public SkillInterfaces getSI() {
		return skillInterfaces;
	}

	/**
	 * Skill Constructors
	 */
	public Slayer getSlayer() {
		return slayer;
	}

	public Runecrafting getRunecrafting() {
		return runecrafting;
	}

	public Cooking getCooking() {
		return cooking;
	}

	public Agility getAgility() {
		return agility;
	}

	public Crafting getCrafting() {
		return crafting;
	}

	public Barrows getBarrows() {
		return barrows;
	}

	public Thieving getThieving() {
		return thieving;
	}

	public Herblore getHerblore() {
		return herblore;
	}

	public GnomeAgility getGnomeAgility() {
		return gnomeAgility;
	}
	
	public PointItems getPoints() {
		return pointItems;
	}
	public PlayerAction getPlayerAction() {
		return playerAction;
	}

	public WildernessAgility getWildernessAgility() {
		return wildernessAgility;
	}

	public AgilityHandler getAgilityHandler() {
		return agilityHandler;
	}
	public Smithing getSmithing() {
		return smith;
	}

	public Logs getLogs() {
		return logs;
	}

	public FightCave getFightCave() {
		if (fightcave == null)
			fightcave = new FightCave(this);
		return fightcave;
	}

	public SmithingInterface getSmithingInt() {
		return smithInt;
	}

	/*
	 * public Fletching getFletching() { return fletching; }
	 */

	public Prayer getPrayer() {
		return prayer;
	}

	/**
	 * End of Skill Constructors
	 */

	public void queueMessage(Packet arg1) {
		packetsReceived.incrementAndGet();
		queuedPackets.add(arg1);
	}

	public boolean processQueuedPackets() {
		Packet p = null;
		int processed = 0;
		packetsReceived.set(0);
		while ((p = queuedPackets.poll()) != null) {
			if (processed > Config.MAX_INCOMONG_PACKETS_PER_CYCLE) {
				break;
			}
			inStream.currentOffset = 0;
			packetType = p.getOpcode();
			packetSize = p.getLength();
			inStream.buffer = p.getPayload().array();
			if (packetType > 0) {
				PacketHandler.processPacket(this, packetType, packetSize);
				processed++;
			}
		}
		return true;
	}

	public void correctCoordinates() {
		final Boundary pc = PestControl.GAME_BOUNDARY;
		final Boundary fc = Boundary.FIGHT_CAVE;
		int x = teleportToX;
		int y = teleportToY;
		if (x > pc.getMinimumX() && x < pc.getMaximumX() && y > pc.getMinimumY() && y < pc.getMaximumY()) {
			getPA().movePlayer(2657, 2639, 0);
		} else if (x > fc.getMinimumX() && x < fc.getMaximumX() && y > fc.getMinimumY() && y < fc.getMaximumY()) {
			getPA().movePlayer(absX, absY, playerId * 4);
			sendMessage("Wave " + (this.waveId + 1)
					+ " will start in approximately 5-10 seconds. ");
			getFightCave().spawn();
		}
	}

	public int getPrivateChat() {
		return privateChat;
	}

	public Friends getFriends() {
		return friend;
	}

	public Ignores getIgnores() {
		return ignores;
	}

	public void setPrivateChat(int option) {
		this.privateChat = option;
	}

	public Trade getTrade() {
		return trade;
	}

	public int localX() {
		return this.getX() - this.getMapRegionX() * 8;
	}

	public int localY() {
		return this.getY() - this.getMapRegionY() * 8;
	}

	public AchievementHandler getAchievements() {
		if (achievementHandler == null)
			achievementHandler = new AchievementHandler(this);
		return achievementHandler;
	}

	public HolidayStages getHolidayStages() {
		if (holidayStages == null) {
			holidayStages = new HolidayStages();
		}
		return holidayStages;
	}

	public long getLastContainerSearch() {
		return lastContainerSearch;
	}

	public void setLastContainerSearch(long lastContainerSearch) {
		this.lastContainerSearch = lastContainerSearch;
	}

	public MysteryBox getMysteryBox() {
		return mysteryBox;
	}
	
	public DamageQueueEvent getDamageQueue() {
		return damageQueue;
	}
	
	public void addDamageReceived(String player, int damage) {
		if (damage <= 0) {
			return;
		}
		Damage combatDamage = new Damage(damage);
		if (damageReceived.containsKey(player)) {
			damageReceived.get(player).add(new Damage(damage));
		} else {
			damageReceived.put(player, new ArrayList<>(Arrays.asList(combatDamage)));
		}
	}
	
	public void resetDamageReceived() {
		damageReceived.clear();
	}
	
	private String killer;
	public boolean craftDialogue;
	
	public String getPlayerKiller() {
		String killer = null;
		int totalDamage = 0;
		for (Entry<String, ArrayList<Damage>> entry : damageReceived.entrySet()) {
			String player = entry.getKey();
			ArrayList<Damage> damageList = entry.getValue();
			int damage = 0;
			for (Damage d : damageList) {
				if (System.currentTimeMillis() - d.getTimestamp() < 90000) {
					damage += d.getAmount();
				}
			}
			if (totalDamage == 0 || damage > totalDamage || killer == null) {
				totalDamage = damage;
				killer = player;
			}
		}
		return killer;
	}

	public String getKiller() {
		return killer;
	}

	public void setKiller(String killer) {
		this.killer = killer;
	}
	
	private boolean killedByZombie = false;
	public boolean isAnimatedArmourSpawned;
	private Hitmark hitmark = null;
	private Hitmark secondHitmark = null;
	protected Rights rights = Rights.PLAYER;
	private Titles titles = new Titles(this);
	private boolean invisible;
	private boolean godmode;
	private boolean safemode;
	private int debug = 0;
	private boolean trading;
	public boolean playerIsCrafting;
	public boolean hasNpc = false;
	public boolean updateItems = false;
	public int ratsCaught;
	public int summonId;
	public int bossKills;
	public int droppedItem = -1;
	public int kbdCount;
	public int dagannothCount;
	public int krakenCount;
	public int chaosCount;
	public int armaCount;
	public int bandosCount;
	public int saraCount;
	public int zammyCount;
	public int barrelCount;
	public int moleCount;
	public int callistoCount;
	public int venenatisCount;
	public int vetionCount;
	public int rememberNpcIndex;
	public boolean slayerRecipe;
	public boolean claimedReward;
	public long lastMove;
	public long bonusXpTime;
	public long craftingDelay;
	public boolean settingMin;
	public boolean settingMax;
	public boolean settingBet;
	public int diceMin;
	public int diceMax;
	public int otherDiceId;
	public int betAmount;
	public int totalProfit;
	public int betsWon;
	public int betsLost;
	public Player diceHost;
	public int removedTasks[] = { -1, -1, -1, -1 };
	public long buySlayerTimer;
	public long lastFire;
	public boolean needsNewTask = false;
	public boolean keepTitle = false;
	public boolean killTitle = false;
	public int slayerPoints = 0;
	public int pTime;
	public int killStreak;
	public boolean hide = false;
	public ArrayList<String> killedPlayers = new ArrayList<String>();
	public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();
	public ArrayList<String> lastConnectedFrom = new ArrayList<String>();
	private boolean stopPlayer;
	/**
	 * Clan Chat Variables
	 */
	public Clan clan;
	public String clanName;
	public String properName;
	public String lastClanChat = "";
	public long lastMysteryBox;
	public int waveType;
	public int[] waveInfo = new int[3];
	public String date;
	public String currentTime;
	public int day;
	public int month;
	public int YEAR;
	public int totalLevel;
	public int xpTotal;
	public int smeltAmount = 0;
	public int smeltEventId = 5567;
	public String barType = "";
	public Smelting.Bars bar = null;
	public boolean isSmelting = false;
	public long lastSmelt = 0;
	/**
	 * Title Variables
	 */
	public String playerTitle = "";
	/**
	 * Event Variables
	 */
	public boolean hasEvent;
	/**
	 * Achievement Variables
	 */
	public int achievementsCompleted;
	public int achievementPoints;
	public int fireslit;
	public int crabsKilled;
	public int treesCut;
	public boolean expLock = false;
	public boolean buyingX;
	public boolean leverClicked = false;
	public double crossbowDamage;
	public int pkp;
	public int KC;
	public int DC;
	public int votePoints;
	public int amDonated;
	public boolean[] clanWarRule = new boolean[10];
	public int follow2 = 0;
	public int antiqueSelect = 0;
	public boolean usingLamp = false;
	public boolean normalLamp = false;
	public boolean antiqueLamp = false;
	public int[][] playerSkillProp = new int[20][15];
	public boolean[] playerSkilling = new boolean[20];
	public boolean setPin = false;
	public String bankPin = "";
	public boolean teleporting;
	public long jailEnd;
	public long muteEnd;
	public long marketMuteEnd;
	public long banEnd;
	public long lastReport = 0;
	public int level1 = 0;
	public int level2 = 0;
	public int level3 = 0;
	public String lastReported = "";
	public long lastButton;
	public int leatherType = -1;
	public boolean isWc;
	public int homeTele = 0;
	public int homeTeleDelay = 0;
	public boolean wcing;
	public int treeX;
	public int treeY;
	public long miscTimer;
	public boolean canWalk = true;
	public long lastFlower;
	public long waitTime;
	public boolean usingROD = false;
	public int DELAY = 1250;
	public long saveButton = 0;
	public int attempts = 3;
	public boolean isOperate;
	public int itemUsing;
	public boolean isFullBody = false;
	public boolean isFullHelm = false;
	public boolean isFullMask = false;
	public long lastCast;
	public boolean hasBankPin;
	public boolean enterdBankpin;
	public boolean firstPinEnter;
	public boolean requestPinDelete;
	public boolean secondPinEnter;
	public boolean thirdPinEnter;
	public boolean fourthPinEnter;
	public boolean hasBankpin;
	public int lastLoginDate;
	public int playerBankPin;
	public int recoveryDelay = 3;
	public int attemptsRemaining = 3;
	public int lastPinSettings = -1;
	public int setPinDate = -1;
	public int changePinDate = -1;
	public int deletePinDate = -1;
	public int firstPin;
	public int secondPin;
	public int thirdPin;
	public int fourthPin;
	public int bankPin1;
	public int bankPin2;
	public int bankPin3;
	public int bankPin4;
	public int pinDeleteDateRequested;
	public boolean isBanking = true;
	public boolean isCooking = false;
	public boolean initialized = false;
	public boolean disconnected = false;
	public boolean ruleAgreeButton = false;
	public boolean rebuildNPCList = false;
	public boolean isActive = false;
	public boolean isKicked = false;
	public boolean isSkulled = false;
	public boolean friendUpdate = false;
	public boolean newPlayer = false;
	public boolean hasMultiSign = false;
	public boolean saveCharacter = false;
	public boolean mouseButton = false;
	public boolean splitChat = false;
	public boolean chatEffects = true;
	public boolean acceptAid = false;
	public boolean nextDialogue = false;
	public boolean autocasting = false;
	public boolean usedSpecial = false;
	public boolean mageFollow = false;
	public boolean dbowSpec = false;
	public boolean craftingLeather = false;
	public boolean properLogout = false;
	public boolean secDbow = false;
	public boolean maxNextHit = false;
	public boolean ssSpec = false;
	public boolean vengOn = false;
	public boolean addStarter = false;
	public boolean startPack = false;
	public boolean accountFlagged = false;
	public boolean msbSpec = false;
	public boolean dtOption = false;
	public boolean dtOption2 = false;
	public boolean doricOption = false;
	public boolean doricOption2 = false;
	public boolean caOption2 = false;
	public boolean caOption2a = false;
	public boolean caOption4a = false;
	public boolean caOption4b = false;
	public boolean caOption4c = false;
	public boolean caPlayerTalk1 = false;
	public boolean horrorOption = false;
	public boolean rfdOption = false;
	public boolean inDt = false;
	public boolean inHfd = false;
	public boolean disableAttEvt = false;
	public boolean AttackEventRunning = false;
	public boolean npcindex;
	public boolean spawned = false;
	public int saveDelay;
	public int height = 0;
	public int playerKilled;
	public int totalPlayerDamageDealt;
	public int killedBy;
	public int lastChatId = 1;
	public int friendSlot = 0;
	public int dialogueId;
	public int randomCoffin;
	public int newLocation;
	public int specEffect;
	public int specBarId;
	public int attackLevelReq;
	public int defenceLevelReq;
	public int strengthLevelReq;
	public int rangeLevelReq;
	public int magicLevelReq;
	public int followId;
	public int skullTimer;
	public int votingPoints;
	public int nextChat = 0;
	public int talkingNpc = -1;
	public int dialogueAction = 0;
	public int autocastId;
	public int followDistance;
	public int followId2;
	public int barrageCount = 0;
	public int delayedDamage = 0;
	public int delayedDamage2 = 0;
	public int pcPoints = 0;
	public int donatorPoints = 0;
	public int magePoints = 0;
	public int lastArrowUsed = -1;
	public int clanId = -1;
	public int autoRet = 0;
	public int pcDamage = 0;
	public int xInterfaceId = 0;
	public int xRemoveId = 0;
	public int xRemoveSlot = 0;
	public int tzhaarToKill = 0;
	public int tzhaarKilled = 0;
	public int waveId;
	public int frozenBy = 0;
	public int teleAction = 0;
	public int newPlayerAct = 0;
	public int bonusAttack = 0;
	public int lastNpcAttacked = 0;
	public int killCount = 0;
	public int actionTimer;
	public int rfdRound = 0;
	public int roundNpc = 0;
	public int desertTreasure = 0;
	public int horrorFromDeep = 0;
	public int QuestPoints = 0;
	public int doricQuest = 0;
	public int[] voidStatus = new int[5];
	public int[] itemKeptId = new int[4];
	public int[] pouches = new int[4];
	public final int[] POUCH_SIZE = { 3, 6, 9, 12 };
	public boolean[] invSlot = new boolean[28];
	public boolean[] equipSlot = new boolean[14];
	public long friends[] = new long[200];
	public double specAmount = 0;
	public double specAccuracy = 1;
	public double specDamage = 1;
	public double prayerPoint = 1.0;
	public boolean storing = false;
	public int teleGrabItem;
	public int teleGrabX;
	public int teleGrabY;
	public int duelCount;
	public int underAttackBy;
	public int underAttackBy2;
	public int wildLevel;
	public int teleTimer;
	public int respawnTimer;
	public int saveTimer = 0;
	public int teleBlockLength;
	public long lastPlayerMove = System.currentTimeMillis();
	public Stopwatch lastSpear = new Stopwatch();
	public Stopwatch lastProtItem = new Stopwatch();
	public long dfsDelay = System.currentTimeMillis();
	public Stopwatch lastVeng = new Stopwatch();
	public Stopwatch lastYell = new Stopwatch();
	public long teleGrabDelay = System.currentTimeMillis();
	public long protMageDelay = System.currentTimeMillis();
	public long protMeleeDelay = System.currentTimeMillis();
	public long protRangeDelay = System.currentTimeMillis();
	public long lastAction = System.currentTimeMillis();
	public long lastThieve = System.currentTimeMillis();
	public long lastLockPick = System.currentTimeMillis();
	public long alchDelay = System.currentTimeMillis();
	public long specCom = System.currentTimeMillis();
	public Stopwatch specDelay = new Stopwatch();
	public long duelDelay = System.currentTimeMillis();
	public long teleBlockDelay = System.currentTimeMillis();
	public long godSpellDelay = System.currentTimeMillis();
	public Stopwatch singleCombatDelay = new Stopwatch();
	public Stopwatch singleCombatDelay2 = new Stopwatch();
	public long reduceStat = System.currentTimeMillis();
	public long restoreStatsDelay = System.currentTimeMillis();
	public Stopwatch logoutDelay = new Stopwatch();
	public long buryDelay = System.currentTimeMillis();
	public long cleanDelay = System.currentTimeMillis();
	public Stopwatch lastRare = new Stopwatch();
	public long diceDelay;
	public Stopwatch foodDelay = new Stopwatch();
	public Stopwatch switchDelay = new Stopwatch();
	public Stopwatch potDelay = new Stopwatch();
	public boolean canChangeAppearance = false;
	public boolean mageAllowed;
	public int focusPointX = -1;
	public int focusPointY = -1;
	public int questPoints = 0;
	public int cooksA;
	public int lastDtKill = 0;
	public int dtHp = 0;
	public int dtMax = 0;
	public int dtAtk = 0;
	public int dtDef = 0;
	public int desertT;
	public long lastChat;
	public long lastRandom;
	public long lastCaught = 0;
	public long lastAttacked;
	public long homeTeleTime;
	public long lastDagChange = -1;
	public long reportDelay;
	public long lastPlant;
	public long objectTimer;
	public long npcTimer;
	public long lastEss;
	public long lastClanMessage;
	public int DirectionCount = 0;
	public boolean appearanceUpdateRequired = true;
	public int hitDiff2;
	public int hitDiff = 0;
	public boolean hitUpdateRequired2;
	public boolean hitUpdateRequired = false;
	public boolean isDead = false;
	public boolean randomEvent = false;
	public boolean FirstClickRunning = false;
	public boolean WildernessWarning = false;
	protected boolean faceNPCupdate = false;
	public int faceNPC = -1;
	public int[] keepItems = new int[4];
	public int[] keepItemsN = new int[4];
	public int WillKeepAmt1;
	public int WillKeepAmt2;
	public int WillKeepAmt3;
	public int WillKeepAmt4;
	public int WillKeepItem1;
	public int WillKeepItem2;
	public int WillKeepItem3;
	public int WillKeepItem4;
	public int WillKeepItem1Slot;
	public int WillKeepItem2Slot;
	public int WillKeepItem3Slot;
	public int WillKeepItem4Slot;
	public int EquipStatus;
	public final int[] BOWS = { 12788, 9185, 11785, 839, 845, 847, 851, 855, 859, 841, 843, 849, 853, 857, 12424, 861, 4212, 4214, 4215, 12765, 12766,
				12767, 12768, 11235, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 6724, 4734, 4934, 4935, 4936, 4937 };
	public final int[] ARROWS = { 882, 884, 886, 888, 890, 892, 4740, 11212, 9140, 9141, 4142, 9143, 9144, 9240, 9241, 9242, 9243, 9244, 9245 };
	public final int[] NO_ARROW_DROP = { 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 4734, 4934, 4935, 4936, 4937 };
	public final int[] OTHER_RANGE_WEAPONS = { 863, 864, 865, 866, 867, 868, 869, 806, 807, 808, 809, 810, 811, 825, 826, 827, 828, 829, 830, 800,
				801, 802, 803, 804, 805, 6522 };
	public final int[][] MAGIC_SPELLS = {
				// example {magicId, level req, animation, startGFX, projectile Id,
				// endGFX, maxhit, exp gained, rune 1, rune 1 amount, rune 2, rune 2
				// amount, rune 3, rune 3 amount, rune 4, rune 4 amount}
	
				// Modern Spells
				{ 1152, 1, 711, 90, 91, 92, 2, 5, 556, 1, 558, 1, 0, 0, 0, 0 }, // wind
																				// strike
				{ 1154, 5, 711, 93, 94, 95, 4, 7, 555, 1, 556, 1, 558, 1, 0, 0 }, // water
																					// strike
				{ 1156, 9, 711, 96, 97, 98, 6, 9, 557, 2, 556, 1, 558, 1, 0, 0 },// earth
																					// strike
				{ 1158, 13, 711, 99, 100, 101, 8, 11, 554, 3, 556, 2, 558, 1, 0, 0 }, // fire
																						// strike
				{ 1160, 17, 711, 117, 118, 119, 9, 13, 556, 2, 562, 1, 0, 0, 0, 0 }, // wind
																						// bolt
				{ 1163, 23, 711, 120, 121, 122, 10, 16, 556, 2, 555, 2, 562, 1, 0, 0 }, // water
																						// bolt
				{ 1166, 29, 711, 123, 124, 125, 11, 20, 556, 2, 557, 3, 562, 1, 0, 0 }, // earth
																						// bolt
				{ 1169, 35, 711, 126, 127, 128, 12, 22, 556, 3, 554, 4, 562, 1, 0, 0 }, // fire
																						// bolt
				{ 1172, 41, 711, 132, 133, 134, 13, 25, 556, 3, 560, 1, 0, 0, 0, 0 }, // wind
																						// blast
				{ 1175, 47, 711, 135, 136, 137, 14, 28, 556, 3, 555, 3, 560, 1, 0, 0 }, // water
																						// blast
				{ 1177, 53, 711, 138, 139, 140, 15, 31, 556, 3, 557, 4, 560, 1, 0, 0 }, // earth
																						// blast
				{ 1181, 59, 711, 129, 130, 131, 16, 35, 556, 4, 554, 5, 560, 1, 0, 0 }, // fire
																						// blast
				{ 1183, 62, 711, 158, 159, 160, 17, 36, 556, 5, 565, 1, 0, 0, 0, 0 }, // wind
																						// wave
				{ 1185, 65, 711, 161, 162, 163, 18, 37, 556, 5, 555, 7, 565, 1, 0, 0 }, // water
																						// wave
				{ 1188, 70, 711, 164, 165, 166, 19, 40, 556, 5, 557, 7, 565, 1, 0, 0 }, // earth
																						// wave
				{ 1189, 75, 711, 155, 156, 157, 20, 42, 556, 5, 554, 7, 565, 1, 0, 0 }, // fire
																						// wave
				{ 1153, 3, 716, 102, 103, 104, 0, 13, 555, 3, 557, 2, 559, 1, 0, 0 }, // confuse
				{ 1157, 11, 716, 105, 106, 107, 0, 20, 555, 3, 557, 2, 559, 1, 0, 0 }, // weaken
				{ 1161, 19, 716, 108, 109, 110, 0, 29, 555, 2, 557, 3, 559, 1, 0, 0 }, // curse
				{ 1542, 66, 729, 167, 168, 169, 0, 76, 557, 5, 555, 5, 566, 1, 0, 0 }, // vulnerability
				{ 1543, 73, 729, 170, 171, 172, 0, 83, 557, 8, 555, 8, 566, 1, 0, 0 }, // enfeeble
				{ 1562, 80, 729, 173, 174, 107, 0, 90, 557, 12, 555, 12, 556, 1, 0, 0 }, // stun
				{ 1572, 20, 711, 177, 178, 181, 0, 30, 557, 3, 555, 3, 561, 2, 0, 0 }, // bind
				{ 1582, 50, 711, 177, 178, 180, 2, 60, 557, 4, 555, 4, 561, 3, 0, 0 }, // snare
				{ 1592, 79, 711, 177, 178, 179, 4, 90, 557, 5, 555, 5, 561, 4, 0, 0 }, // entangle
				{ 1171, 39, 724, 145, 146, 147, 15, 25, 556, 2, 557, 2, 562, 1, 0, 0 }, // crumble
																						// undead
				{ 1539, 50, 708, 87, 88, 89, 25, 42, 554, 5, 560, 1, 0, 0, 0, 0 }, // iban
																					// blast
				{ 12037, 50, 1576, 327, 328, 329, 19, 30, 560, 1, 558, 4, 0, 0, 0, 0 }, // magic
																						// dart
				{ 1190, 60, 811, 0, 0, 76, 20, 60, 554, 2, 565, 2, 556, 4, 0, 0 }, // sara
																					// strike
				{ 1191, 60, 811, 0, 0, 77, 20, 60, 554, 1, 565, 2, 556, 4, 0, 0 }, // cause
																					// of
																					// guthix
				{ 1192, 60, 811, 0, 0, 78, 20, 60, 554, 4, 565, 2, 556, 1, 0, 0 }, // flames
																					// of
																					// zammy
				{ 12445, 85, 1819, 0, 344, 345, 0, 65, 563, 1, 562, 1, 560, 1, 0, 0 }, // teleblock
	
				// Ancient Spells
				{ 12939, 50, 1978, 0, 384, 385, 13, 30, 560, 2, 562, 2, 554, 1, 556, 1 }, // smoke
																							// rush
				{ 12987, 52, 1978, 0, 378, 379, 14, 31, 560, 2, 562, 2, 566, 1, 556, 1 }, // shadow
																							// rush
				{ 12901, 56, 1978, 0, 0, 373, 15, 33, 560, 2, 562, 2, 565, 1, 0, 0 }, // blood
																						// rush
				{ 12861, 58, 1978, 0, 360, 361, 16, 34, 560, 2, 562, 2, 555, 2, 0, 0 }, // ice
																						// rush
				{ 12963, 62, 1979, 0, 0, 389, 19, 36, 560, 2, 562, 4, 556, 2, 554, 2 }, // smoke
																						// burst
				{ 13011, 64, 1979, 0, 0, 382, 20, 37, 560, 2, 562, 4, 556, 2, 566, 2 }, // shadow
																						// burst
				{ 12919, 68, 1979, 0, 0, 376, 21, 39, 560, 2, 562, 4, 565, 2, 0, 0 }, // blood
																						// burst
				{ 12881, 70, 1979, 0, 0, 363, 22, 40, 560, 2, 562, 4, 555, 4, 0, 0 }, // ice
																						// burst
				{ 12951, 74, 1978, 0, 386, 387, 23, 42, 560, 2, 554, 2, 565, 2, 556, 2 }, // smoke
																							// blitz
				{ 12999, 76, 1978, 0, 380, 381, 24, 43, 560, 2, 565, 2, 556, 2, 566, 2 }, // shadow
																							// blitz
				{ 12911, 80, 1978, 0, 374, 375, 25, 45, 560, 2, 565, 4, 0, 0, 0, 0 }, // blood
																						// blitz
				{ 12871, 82, 1978, 366, 0, 367, 26, 46, 560, 2, 565, 2, 555, 3, 0, 0 }, // ice
																						// blitz
				{ 12975, 86, 1979, 0, 0, 391, 27, 48, 560, 4, 565, 2, 556, 4, 554, 4 }, // smoke
																						// barrage
				{ 13023, 88, 1979, 0, 0, 383, 28, 49, 560, 4, 565, 2, 556, 4, 566, 3 }, // shadow
																						// barrage
				{ 12929, 92, 1979, 0, 0, 377, 29, 51, 560, 4, 565, 4, 566, 1, 0, 0 }, // blood
																						// barrage
				{ 12891, 94, 1979, 0, 0, 369, 30, 52, 560, 4, 565, 2, 555, 6, 0, 0 }, // ice
																						// barrage
	
				{ -1, 80, 811, 301, 0, 0, 0, 0, 554, 3, 565, 3, 556, 3, 0, 0 }, // charge
				{ -1, 21, 712, 112, 0, 0, 0, 10, 554, 3, 561, 1, 0, 0, 0, 0 }, // low
																				// alch
				{ -1, 55, 713, 113, 0, 0, 0, 20, 554, 5, 561, 1, 0, 0, 0, 0 }, // high
																				// alch
				{ -1, 33, 728, 142, 143, 144, 0, 35, 556, 1, 563, 1, 0, 0, 0, 0 }, // telegrab
				
				{ -1, 75, 719, 1251, 1252, 1253, 29, 35, 0, 0, 0, 0, 0, 0, 0, 0 }, //trident of the seas
				
				{ -1, 75, 719, 665, 1040, 1042, 32, 35, 0, 0, 0, 0, 0, 0, 0, 0 } //trident of the swamp
	
		};
	public boolean multiAttacking;
	public boolean rangeEndGFXHeight;
	public boolean playerFletch;
	public boolean playerIsFletching;
	public boolean playerIsMining;
	public boolean playerIsFiremaking;
	public boolean playerIsFishing;
	public boolean playerIsCooking;
	public boolean below459 = true;
	public boolean defaultWealthTransfer;
	public boolean updateInventory;
	public boolean oldSpec;
	public boolean stopPlayerSkill;
	public boolean playerStun;
	public boolean stopPlayerPacket;
	public boolean usingClaws;
	public boolean playerBFishing;
	public boolean finishedBarbarianTraining;
	public boolean ignoreDefence;
	public boolean secondFormAutocast;
	public boolean usingArrows;
	public boolean usingOtherRangeWeapons;
	public boolean usingCross;
	public boolean magicDef;
	public boolean spellSwap;
	public boolean recoverysSet;
	public int rangeEndGFX;
	public int boltDamage;
	public int teleotherType;
	public int playerTradeWealth;
	public int doAmount;
	public int woodcuttingTree;
	public int stageT;
	public int dfsCount;
	public int recoilHits;
	public int playerDialogue;
	public int clawDelay;
	public int previousDamage;
	public boolean protectItem = false;
	public int[] autocastIds = { 51133, 32, 51185, 33, 51091, 34, 24018, 35, 51159, 36, 51211, 37, 51111, 38, 51069, 39, 51146, 40, 51198, 41, 51102,
				42, 51058, 43, 51172, 44, 51224, 45, 51122, 46, 51080, 47, 7038, 0, 7039, 1, 7040, 2, 7041, 3, 7042, 4, 7043, 5, 7044, 6, 7045, 7, 7046,
				8, 7047, 9, 7048, 10, 7049, 11, 7050, 12, 7051, 13, 7052, 14, 7053, 15, 47019, 27, 47020, 25, 47021, 12, 47022, 13, 47023, 14, 47024, 15 };
	public int[][] barrowsNpcs = { { 1677, 0 }, // verac
				{ 1676, 0 }, // toarg
				{ 1675, 0 }, // karil
				{ 1674, 0 }, // guthan
				{ 1673, 0 }, // dharok
				{ 1672, 0 } // ahrim
		};
	public int barrowsKillCount;
	public int reduceSpellId;
	public final int[] REDUCE_SPELL_TIME = { 250000, 250000, 250000, 500000, 500000, 500000 };
	public long[] reduceSpellDelay = new long[6];
	public final int[] REDUCE_SPELLS = { 1153, 1157, 1161, 1542, 1543, 1562 };
	public boolean[] canUseReducingSpell = { true, true, true, true, true, true };
	public int slayerTask;
	public int taskAmount;
	public int prayerId = -1;
	public int headIcon = -1;
	public int bountyIcon = 0;
	public long stopPrayerDelay;
	public long prayerDelay;
	public boolean usingPrayer;
	public final int[] PRAYER_DRAIN_RATE = { 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500,
				500, 500, 500, 500, 500 };
	public final int[] PRAYER_LEVEL_REQUIRED = { 1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52, 60, 70 };
	public final int[] PRAYER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
	public final String[] PRAYER_NAME = { "Thick Skin", "Burst of Strength", "Clarity of Thought", "Sharp Eye", "Mystic Will", "Rock Skin",
				"Superhuman Strength", "Improved Reflexes", "Rapid Restore", "Rapid Heal", "Protect Item", "Hawk Eye", "Mystic Lore", "Steel Skin",
				"Ultimate Strength", "Incredible Reflexes", "Protect from Magic", "Protect from Missiles", "Protect from Melee", "Eagle Eye",
				"Mystic Might", "Retribution", "Redemption", "Smite", "Chivalry", "Piety" };
	public final int[] PRAYER_GLOW = { 83, 84, 85, 601, 602, 86, 87, 88, 89, 90, 91, 603, 604, 92, 93, 94, 95, 96, 97, 605, 606, 98, 99, 100, 607,
				608 };
	public final int[] PRAYER_HEAD_ICONS = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0, -1, -1, 3, 5, 4, -1, -1 };
	public boolean[] prayerActive = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
				false, false, false, false, false, false, false, false, false, false };
	public int headIconPk = -1;
	public int headIconHints;
	public boolean[] duelRule = new boolean[22];
	public boolean doubleHit;
	public boolean usingSpecial;
	public boolean npcDroppingItems;
	public boolean usingRangeWeapon;
	public boolean usingBow;
	public boolean usingMagic;
	public boolean castingMagic;
	public boolean usingMelee;
	public int specMaxHitIncrease;
	public int freezeDelay;
	public int freezeTimer = -6;
	public int killerId;
	public int playerIndex;
	public int oldPlayerIndex;
	public int lastWeaponUsed;
	public int projectileStage;
	public int crystalBowArrowCount;
	public int playerMagicBook;
	public int teleGfx;
	public int teleEndAnimation;
	public int teleHeight;
	public int teleX;
	public int teleY;
	public int rangeItemUsed;
	public int killingNpcIndex;
	public int totalDamageDealt;
	public int oldNpcIndex;
	public int fightMode;
	public int attackTimer;
	public int npcIndex;
	public int npcClickIndex;
	public int npcType;
	public int castingSpellId;
	public int oldSpellId;
	public int spellId;
	public int hitDelay;
	public boolean magicFailed;
	public boolean oldMagicFailed;
	public int bowSpecShot;
	public int clickNpcType;
	public int clickObjectType;
	public int objectId;
	public int objectX;
	public int objectY;
	public int objectXOffset;
	public int objectYOffset;
	public int objectDistance;
	public int pItemX;
	public int pItemY;
	public int pItemId;
	public boolean isMoving;
	public boolean walkingToItem;
	public boolean isShopping;
	public boolean updateShop;
	public int myShopId;
	public int tradeStatus;
	public int tradeWith;
	public int amountGifted;
	public boolean forcedChatUpdateRequired;
	public boolean inDuel;
	public boolean tradeAccepted;
	public boolean goodTrade;
	public boolean inTrade;
	public boolean tradeRequested;
	public boolean tradeResetNeeded;
	public boolean tradeConfirmed;
	public boolean tradeConfirmed2;
	public boolean canOffer;
	public boolean acceptTrade;
	public boolean acceptedTrade;
	public int attackAnim;
	public int animationRequest = -1;
	public int animationWaitCycles;
	public int[] playerBonus = new int[12];
	public boolean isRunning2 = true;
	public boolean takeAsNote;
	public int combatLevel;
	public boolean saveFile = false;
	public int playerAppearance[] = new int[13];
	public int apset;
	public int actionID;
	public int wearItemTimer;
	public int wearId;
	public int wearSlot;
	public int interfaceId;
	public int XremoveSlot;
	public int XinterfaceID;
	public int XremoveID;
	public int Xamount;
	public int tutorial = 15;
	public boolean usingGlory = false;
	public boolean usingDueling = false;
	public boolean usingROW = false;
	public int[] woodcut = new int[7];
	public int wcTimer = 0;
	public int miningTimer = 0;
	public boolean fishing = false;
	public int fishTimer = 0;
	public int smeltType;
	public int smeltTimer = 0;
	public boolean smeltInterface;
	public boolean patchCleared;
	public int[] farm = new int[2];
	public long lastAntifirePotion;
	public long antifireDelay;
	/**
	 * Castle Wars
	 */
	public int castleWarsTeam;
	public boolean inCwGame;
	public boolean inCwWait;
	public int npcId2 = 0;
	public boolean isNpc;
	/**
	 * Fight Pits
	 */
	public boolean inPits = false;
	public int pitsStatus = 0;
	public String connectedFrom = "";
	public String globalMessage = "";
	public int playerId = -1;
	public int safeTimer = 0;
	public String playerName = null;
	public String playerName2 = null;
	public String playerPass = null;
	private long nameAsLong;
	public PlayerHandler handler = null;
	public int playerItems[] = new int[28];
	public int playerItemsN[] = new int[28];
	public int bankItems[] = new int[Config.BANK_SIZE];
	public int bankItemsN[] = new int[Config.BANK_SIZE];
	public boolean bankNotes = false;
	private int dragonfireShieldCharge;
	private long lastDragonfireShieldAttack;
	private boolean dragonfireShieldActive;
	public int playerStandIndex = 0x328;
	public int playerTurnIndex = 0x337;
	public int playerWalkIndex = 0x333;
	public int playerTurn180Index = 0x334;
	public int playerTurn90CWIndex = 0x335;
	public int playerTurn90CCWIndex = 0x336;
	public int playerRunIndex = 0x338;
	public int playerHat = 0;
	public int playerCape = 1;
	public int playerAmulet = 2;
	public int playerWeapon = 3;
	public int playerChest = 4;
	public int playerShield = 5;
	public int playerLegs = 7;
	public int playerHands = 9;
	public int playerFeet = 10;
	public int playerRing = 12;
	public int playerArrows = 13;
	public int playerAttack = 0;
	public int playerDefence = 1;
	public int playerStrength = 2;
	public int playerHitpoints = 3;
	public int playerRanged = 4;
	public int playerPrayer = 5;
	public int playerMagic = 6;
	public int playerCooking = 7;
	public int playerWoodcutting = 8;
	public int playerFletching = 9;
	public int playerFishing = 10;
	public int playerFiremaking = 11;
	public static int playerCrafting = 12;
	public static int playerSmithing = 13;
	public int playerMining = 14;
	public int playerHerblore = 15;
	public int playerAgility = 16;
	public int playerThieving = 17;
	public int playerSlayer = 18;
	public int playerFarming = 19;
	public int playerRunecrafting = 20;
	public int fletchingType;
	public int[] playerEquipment = new int[14];
	public int[] playerEquipmentN = new int[14];
	public int[] playerLevel = new int[25];
	public int[] playerXP = new int[25];
	public boolean seedPlanted = false;
	public boolean seedWatered = false;
	public boolean patchCleaned = false;
	public boolean patchRaked = false;
	public int[][] barrowCrypt = { { 4921, 0 }, { 2035, 0 } };
	public Player playerList[] = new Player[maxPlayerListSize];
	public int playerListSize = 0;
	public byte playerInListBitmap[] = new byte[(Config.MAX_PLAYERS + 7) >> 3];
	public NPC npcList[] = new NPC[maxNPCListSize];
	public int npcListSize = 0;
	public byte npcInListBitmap[] = new byte[(NPCHandler.maxNPCs + 7) >> 3];
	public int getHeightLevel;
	public int mapRegionX;
	public int mapRegionY;
	public int absX;
	public int absY;
	public int currentX;
	public int currentY;
	public int heightLevel;
	public int playerSE = 0x328;
	public int playerSEW = 0x333;
	public int playerSER = 0x334;
	public boolean updateRequired = true;
	public final int walkingQueueSize = 50;
	public int walkingQueueX[] = new int[walkingQueueSize];
	public int walkingQueueY[] = new int[walkingQueueSize];
	public int wQueueReadPtr = 0;
	public int wQueueWritePtr = 0;
	public boolean isRunning = true;
	public int teleportToX = -1;
	public int teleportToY = -1;
	public int runEnergy = 100;
	public long lastRunRecovery;
	public long rangeDelay;
	public boolean inSpecMode = false;
	public boolean didTeleport = false;
	public boolean mapRegionDidChange = false;
	public int dir1 = -1;
	public int dir2 = -1;
	public boolean createItems = false;
	public int poimiX = 0;
	public int poimiY = 0;
	public byte cachedPropertiesBitmap[] = new byte[(Config.MAX_PLAYERS + 7) >> 3];
	private boolean chatTextUpdateRequired = false;
	private byte chatText[] = new byte[4096];
	private byte chatTextSize = 0;
	private int chatTextColor = 0;
	private int chatTextEffects = 0;
	public String forcedText = "null";
	/**
	 * Graphics
	 **/
	public int mask100var1 = 0;
	public int mask100var2 = 0;
	protected boolean mask100update = false;
	/**
	 * Face Update
	 **/
	protected boolean faceUpdateRequired = false;
	public int face = -1;
	public int FocusPointX = -1;
	public int FocusPointY = -1;
	private int newWalkCmdX[] = new int[walkingQueueSize];
	private int newWalkCmdY[] = new int[walkingQueueSize];
	public int newWalkCmdSteps = 0;
	private boolean newWalkCmdIsRunning = false;
	protected int travelBackX[] = new int[walkingQueueSize];
	protected int travelBackY[] = new int[walkingQueueSize];
	protected int numTravelBackSteps = 0;
	protected AtomicInteger packetsReceived = new AtomicInteger();
	private ISAACCipher inStreamDecryption = null;
	private ISAACCipher outStreamDecryption = null;
	public int[] damageTaken = new int[Config.MAX_PLAYERS];
	private long lastVenomHit;
	private long lastVenomCure;
	private long venomImmunity;
	private byte venomDamage;
	private long lastPoisonHit;
	private long lastPoisonCure;
	private long poisonImmunity;
	private byte poisonDamage;
	private List<Byte> poisonDamageHistory = new ArrayList<>(4);
	
	public boolean isKilledByZombie() {
		return killedByZombie;
	}
	
	public void setKilledByZombie(boolean state) {
		killedByZombie = state;
	}
	
	public int getFarmingSeedId(int index) {
		return farmingSeedId[index];
	}

	public void setFarmingSeedId(int index, int farmingSeedId) {
		this.farmingSeedId[index] = farmingSeedId;
	}

	public int getFarmingTime(int index) {
		return this.farmingTime[index];
	}

	public void setFarmingTime(int index, int farmingTime) {
		this.farmingTime[index] = farmingTime;
	}

	public int getFarmingState(int index) {
		return farmingState[index];
	}

	public void setFarmingState(int index, int farmingState) {
		this.farmingState[index] = farmingState;
	}

	public int getFarmingHarvest(int index) {
		return farmingHarvest[index];
	}

	public void setFarmingHarvest(int index, int farmingHarvest) {
		this.farmingHarvest[index] = farmingHarvest;
	}
	
	public Farming getFarming() {
		if (farming == null) {
			farming = new Farming(this);
		}
		return farming;
	}
	
	/**
	 * Retrieves the bounty hunter instance for this client object. We use 
	 * lazy initialization because we store values from the player save file
	 * in the bountyHunter object upon login. Without lazy initialization
	 * the value would be overwritten.
	 * @return	the bounty hunter object
	 */
	public BountyHunter getBH() {
		if (Objects.isNull(bountyHunter)) {
			bountyHunter = new BountyHunter(this);
		}
		return bountyHunter;
	}
	
	public UnnecessaryPacketDropper getPacketDropper() {
		return packetDropper;
	}
	
	public Optional<ItemCombination> getCurrentCombination() {
		return currentCombination;
	}
	
	public void setCurrentCombination(Optional<ItemCombination> combination) {
		this.currentCombination = combination;
	}
	
	public PlayerKill getPlayerKills() {
		if (Objects.isNull(playerKills)) {
			playerKills = new PlayerKill();
		}
		return playerKills;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public int getMaximumHealth() {
		int base = getLevelForXP(playerXP[3]);
		if (EquipmentSet.GUTHAN.isWearingBarrows(this) && getItems().isWearingItem(12853)) {
			base += 10;
		}
		return base;
	}
	
	public Duel getDuel() {
		return duelSession;
	}
	
	public void setItemOnPlayer(Player player) {
		this.itemOnPlayer = player;
	}
	
	public Player getItemOnPlayer() {
		return itemOnPlayer;
	}
	
	public Skilling getSkilling() {
		return skilling;
	}
	
	public Presets getPresets() {
		if (presets == null) {
			presets = new Presets(this);
		}
		return presets;
	}
	
	public Killstreak getKillstreak() {
		if (killstreaks == null) {
			killstreaks = new Killstreak(this);
		}
		return killstreaks;
	}
	
	/**
	 * Returns the single instance of the {@link NPCDeathTracker} class for this player.
	 * @return	the tracker clas 
	 */
	public NPCDeathTracker getNpcDeathTracker() {
		return npcDeathTracker;
	}
	
	/**
	 * The zulrah event 
	 * @return	event
	 */
	public Zulrah getZulrahEvent() {
		return zulrah;
	}
	
	/**
	 * The single {@link WarriorsGuild} instance for this player
	 * @return	warriors guild
	 */
	public WarriorsGuild getWarriorsGuild() {
		return warriorsGuild;
	}
	
	/**
	 * The single instance of the {@link PestControlRewards} class for this player
	 * @return	the reward class
	 */
	public PestControlRewards getPestControlRewards() {
		return pestControlRewards;
	}
	
	public Mining getMining() {
		return mining;
	}
	
	public PunishmentPanel getPunishmentPanel() {
		return punishmentPanel;
	}

	public void faceNPC(int index) {
		faceNPC = index;
		faceNPCupdate = true;
		updateRequired = true;
	}

	public void appendFaceNPCUpdate(Stream str) {
		str.writeWordBigEndian(faceNPC);
	}

	public void ResetKeepItems() {
		WillKeepAmt1 = -1;
		WillKeepItem1 = -1;
		WillKeepAmt2 = -1;
		WillKeepItem2 = -1;
		WillKeepAmt3 = -1;
		WillKeepItem3 = -1;
		WillKeepAmt4 = -1;
		WillKeepItem4 = -1;
	}

	public void StartBestItemScan(Player c) {
		if (c.isSkulled && !c.prayerActive[10]) {
			ItemKeptInfo(c, 0);
			return;
		}
		FindItemKeptInfo(c);
		ResetKeepItems();
		BestItem1(c);
	}

	public void FindItemKeptInfo(Player c) {
		if (isSkulled && c.prayerActive[10])
			ItemKeptInfo(c, 1);
		else if (!isSkulled && !c.prayerActive[10])
			ItemKeptInfo(c, 3);
		else if (!isSkulled && c.prayerActive[10])
			ItemKeptInfo(c, 4);
	}

	public void ItemKeptInfo(Player c, int Lose) {
		for (int i = 17109; i < 17131; i++) {
			c.getPA().sendFrame126("", i);
		}
		c.getPA().sendFrame126("Items you will keep on death:", 17104);
		c.getPA().sendFrame126("Items you will lose on death:", 17105);
		c.getPA().sendFrame126("Player Information", 17106);
		c.getPA().sendFrame126("Max items kept on death:", 17107);
		c.getPA().sendFrame126("~ " + Lose + " ~", 17108);
		c.getPA().sendFrame126("The normal amount of", 17111);
		c.getPA().sendFrame126("items kept is three.", 17112);
		switch (Lose) {
		case 0:
		default:
			c.getPA().sendFrame126("Items you will keep on death:", 17104);
			c.getPA().sendFrame126("Items you will lose on death:", 17105);
			c.getPA().sendFrame126("You're marked with a", 17111);
			c.getPA().sendFrame126("@red@skull. @lre@This reduces the", 17112);
			c.getPA().sendFrame126("items you keep from", 17113);
			c.getPA().sendFrame126("three to zero!", 17114);
			break;
		case 1:
			c.getPA().sendFrame126("Items you will keep on death:", 17104);
			c.getPA().sendFrame126("Items you will lose on death:", 17105);
			c.getPA().sendFrame126("You're marked with a", 17111);
			c.getPA().sendFrame126("@red@skull. @lre@This reduces the", 17112);
			c.getPA().sendFrame126("items you keep from", 17113);
			c.getPA().sendFrame126("three to zero!", 17114);
			c.getPA().sendFrame126("However, you also have", 17115);
			c.getPA().sendFrame126("the @red@Protect @lre@Items prayer", 17116);
			c.getPA().sendFrame126("active, which saves you", 17117);
			c.getPA().sendFrame126("one extra item!", 17118);
			break;
		case 3:
			c.getPA().sendFrame126("Items you will keep on death(if not skulled):", 17104);
			c.getPA().sendFrame126("Items you will lose on death(if not skulled):", 17105);
			c.getPA().sendFrame126("You have no factors", 17111);
			c.getPA().sendFrame126("affecting the items you", 17112);
			c.getPA().sendFrame126("keep.", 17113);
			break;
		case 4:
			c.getPA().sendFrame126("Items you will keep on death(if not skulled):", 17104);
			c.getPA().sendFrame126("Items you will lose on death(if not skulled):", 17105);
			c.getPA().sendFrame126("You have the @red@Protect", 17111);
			c.getPA().sendFrame126("@red@Item @lre@prayer active,", 17112);
			c.getPA().sendFrame126("which saves you one", 17113);
			c.getPA().sendFrame126("extra item!", 17114);
			break;
		}
	}

	public void BestItem1(Player c) {
		int BestValue = 0;
		int NextValue = 0;
		int ItemsContained = 0;
		WillKeepItem1 = 0;
		WillKeepItem1Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (playerItems[ITEM] > 0) {
				ItemsContained += 1;
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerItems[ITEM] - 1));
				if (NextValue > BestValue) {
					BestValue = NextValue;
					WillKeepItem1 = playerItems[ITEM] - 1;
					WillKeepItem1Slot = ITEM;
					if (playerItemsN[ITEM] > 2 && !c.prayerActive[10]) {
						WillKeepAmt1 = 3;
					} else if (playerItemsN[ITEM] > 3 && c.prayerActive[10]) {
						WillKeepAmt1 = 4;
					} else {
						WillKeepAmt1 = playerItemsN[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (playerEquipment[EQUIP] > 0) {
				ItemsContained += 1;
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerEquipment[EQUIP]));
				if (NextValue > BestValue) {
					BestValue = NextValue;
					WillKeepItem1 = playerEquipment[EQUIP];
					WillKeepItem1Slot = EQUIP + 28;
					if (playerEquipmentN[EQUIP] > 2 && !c.prayerActive[10]) {
						WillKeepAmt1 = 3;
					} else if (playerEquipmentN[EQUIP] > 3 && c.prayerActive[10]) {
						WillKeepAmt1 = 4;
					} else {
						WillKeepAmt1 = playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!isSkulled && ItemsContained > 1 && (WillKeepAmt1 < 3 || (c.prayerActive[10] && WillKeepAmt1 < 4))) {
			BestItem2(c, ItemsContained);
		}
	}

	public void BestItem2(Player c, int ItemsContained) {
		int BestValue = 0;
		int NextValue = 0;
		WillKeepItem2 = 0;
		WillKeepItem2Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (playerItems[ITEM] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerItems[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == WillKeepItem1Slot && playerItems[ITEM] - 1 == WillKeepItem1)) {
					BestValue = NextValue;
					WillKeepItem2 = playerItems[ITEM] - 1;
					WillKeepItem2Slot = ITEM;
					if (playerItemsN[ITEM] > 2 - WillKeepAmt1 && !c.prayerActive[10]) {
						WillKeepAmt2 = 3 - WillKeepAmt1;
					} else if (playerItemsN[ITEM] > 3 - WillKeepAmt1 && c.prayerActive[10]) {
						WillKeepAmt2 = 4 - WillKeepAmt1;
					} else {
						WillKeepAmt2 = playerItemsN[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == WillKeepItem1Slot && playerEquipment[EQUIP] == WillKeepItem1)) {
					BestValue = NextValue;
					WillKeepItem2 = playerEquipment[EQUIP];
					WillKeepItem2Slot = EQUIP + 28;
					if (playerEquipmentN[EQUIP] > 2 - WillKeepAmt1 && !c.prayerActive[10]) {
						WillKeepAmt2 = 3 - WillKeepAmt1;
					} else if (playerEquipmentN[EQUIP] > 3 - WillKeepAmt1 && c.prayerActive[10]) {
						WillKeepAmt2 = 4 - WillKeepAmt1;
					} else {
						WillKeepAmt2 = playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!isSkulled && ItemsContained > 2 && (WillKeepAmt1 + WillKeepAmt2 < 3 || (c.prayerActive[10] && WillKeepAmt1 + WillKeepAmt2 < 4))) {
			BestItem3(c, ItemsContained);
		}
	}

	public void BestItem3(Player c, int ItemsContained) {
		int BestValue = 0;
		int NextValue = 0;
		WillKeepItem3 = 0;
		WillKeepItem3Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (playerItems[ITEM] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerItems[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == WillKeepItem1Slot && playerItems[ITEM] - 1 == WillKeepItem1)
						&& !(ITEM == WillKeepItem2Slot && playerItems[ITEM] - 1 == WillKeepItem2)) {
					BestValue = NextValue;
					WillKeepItem3 = playerItems[ITEM] - 1;
					WillKeepItem3Slot = ITEM;
					if (playerItemsN[ITEM] > 2 - (WillKeepAmt1 + WillKeepAmt2) && !c.prayerActive[10]) {
						WillKeepAmt3 = 3 - (WillKeepAmt1 + WillKeepAmt2);
					} else if (playerItemsN[ITEM] > 3 - (WillKeepAmt1 + WillKeepAmt2) && c.prayerActive[10]) {
						WillKeepAmt3 = 4 - (WillKeepAmt1 + WillKeepAmt2);
					} else {
						WillKeepAmt3 = playerItemsN[ITEM];
					}
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == WillKeepItem1Slot && playerEquipment[EQUIP] == WillKeepItem1)
						&& !(EQUIP + 28 == WillKeepItem2Slot && playerEquipment[EQUIP] == WillKeepItem2)) {
					BestValue = NextValue;
					WillKeepItem3 = playerEquipment[EQUIP];
					WillKeepItem3Slot = EQUIP + 28;
					if (playerEquipmentN[EQUIP] > 2 - (WillKeepAmt1 + WillKeepAmt2) && !c.prayerActive[10]) {
						WillKeepAmt3 = 3 - (WillKeepAmt1 + WillKeepAmt2);
					} else if (playerEquipmentN[EQUIP] > 3 - WillKeepAmt1 && c.prayerActive[10]) {
						WillKeepAmt3 = 4 - (WillKeepAmt1 + WillKeepAmt2);
					} else {
						WillKeepAmt3 = playerEquipmentN[EQUIP];
					}
				}
			}
		}
		if (!isSkulled && ItemsContained > 3 && c.prayerActive[10] && ((WillKeepAmt1 + WillKeepAmt2 + WillKeepAmt3) < 4)) {
			BestItem4(c);
		}
	}

	public void BestItem4(Player c) {
		int BestValue = 0;
		int NextValue = 0;
		WillKeepItem4 = 0;
		WillKeepItem4Slot = 0;
		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (playerItems[ITEM] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerItems[ITEM] - 1));
				if (NextValue > BestValue && !(ITEM == WillKeepItem1Slot && playerItems[ITEM] - 1 == WillKeepItem1)
						&& !(ITEM == WillKeepItem2Slot && playerItems[ITEM] - 1 == WillKeepItem2)
						&& !(ITEM == WillKeepItem3Slot && playerItems[ITEM] - 1 == WillKeepItem3)) {
					BestValue = NextValue;
					WillKeepItem4 = playerItems[ITEM] - 1;
					WillKeepItem4Slot = ITEM;
				}
			}
		}
		for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
			if (playerEquipment[EQUIP] > 0) {
				NextValue = (int) Math.floor(c.getShops().getItemShopValue(playerEquipment[EQUIP]));
				if (NextValue > BestValue && !(EQUIP + 28 == WillKeepItem1Slot && playerEquipment[EQUIP] == WillKeepItem1)
						&& !(EQUIP + 28 == WillKeepItem2Slot && playerEquipment[EQUIP] == WillKeepItem2)
						&& !(EQUIP + 28 == WillKeepItem3Slot && playerEquipment[EQUIP] == WillKeepItem3)) {
					BestValue = NextValue;
					WillKeepItem4 = playerEquipment[EQUIP];
					WillKeepItem4Slot = EQUIP + 28;
				}
			}
		}
	}

	public boolean isAutoButton(int button) {
		for (int j = 0; j < autocastIds.length; j += 2) {
			if (autocastIds[j] == button)
				return true;
		}
		return false;
	}

	public void assignAutocast(int button) {
		for (int j = 0; j < autocastIds.length; j++) {
			if (autocastIds[j] == button) {
				Player c = PlayerHandler.players[this.playerId];
				autocasting = true;
				autocastId = autocastIds[j + 1];
				c.getPA().sendFrame36(108, 1);
				c.setSidebarInterface(0, 328);
				// spellName = getSpellName(autocastId);
				// spellName = spellName;
				// c.getPA().sendFrame126(spellName, 354);
				c = null;
				break;
			}
		}
	}

	public int getLocalX() {
		return getX() - 8 * getMapRegionX();
	}

	public int getLocalY() {
		return getY() - 8 * getMapRegionY();
	}

	public String getSpellName(int id) {
		switch (id) {
		case 0:
			return "Air Strike";
		case 1:
			return "Water Strike";
		case 2:
			return "Earth Strike";
		case 3:
			return "Fire Strike";
		case 4:
			return "Air Bolt";
		case 5:
			return "Water Bolt";
		case 6:
			return "Earth Bolt";
		case 7:
			return "Fire Bolt";
		case 8:
			return "Air Blast";
		case 9:
			return "Water Blast";
		case 10:
			return "Earth Blast";
		case 11:
			return "Fire Blast";
		case 12:
			return "Air Wave";
		case 13:
			return "Water Wave";
		case 14:
			return "Earth Wave";
		case 15:
			return "Fire Wave";
		case 32:
			return "Shadow Rush";
		case 33:
			return "Smoke Rush";
		case 34:
			return "Blood Rush";
		case 35:
			return "Ice Rush";
		case 36:
			return "Shadow Burst";
		case 37:
			return "Smoke Burst";
		case 38:
			return "Blood Burst";
		case 39:
			return "Ice Burst";
		case 40:
			return "Shadow Blitz";
		case 41:
			return "Smoke Blitz";
		case 42:
			return "Blood Blitz";
		case 43:
			return "Ice Blitz";
		case 44:
			return "Shadow Barrage";
		case 45:
			return "Smoke Barrage";
		case 46:
			return "Blood Barrage";
		case 47:
			return "Ice Barrage";
		default:
			return "Select Spell";
		}
	}

	public boolean fullVoidRange() {
		return playerEquipment[playerHat] == 11664 && playerEquipment[playerLegs] == 8840 && playerEquipment[playerChest] == 8839
				&& playerEquipment[playerHands] == 8842;
	}

	public boolean fullVoidMage() {
		return playerEquipment[playerHat] == 11663 && playerEquipment[playerLegs] == 8840 && playerEquipment[playerChest] == 8839
				&& playerEquipment[playerHands] == 8842;
	}

	public boolean fullVoidMelee() {
		return playerEquipment[playerHat] == 11665 && playerEquipment[playerLegs] == 8840 && playerEquipment[playerChest] == 8839
				&& playerEquipment[playerHands] == 8842;
	}

	/**
	 * SouthWest, NorthEast, SouthWest, NorthEast
	 */
	public boolean isInTut() {
		if (absX >= 2625 && absX <= 2687 && absY >= 4670 && absY <= 4735) {
			return true;
		}
		return false;
	}

	public boolean isInBarrows() {
		if (absX > 3543 && absX < 3584 && absY > 3265 && absY < 3311) {
			return true;
		}
		return false;
	}

	public boolean isInBarrows2() {
		if (absX > 3529 && absX < 3581 && absY > 9673 && absY < 9722) {
			return true;
		}
		return false;
	}

	public boolean inBarrows() {
		if (absX > 3520 && absX < 3598 && absY > 9653 && absY < 9750) {
			return true;
		}
		return false;
	}

	public boolean inArea(int x, int y, int x1, int y1) {
		if (absX > x && absX < x1 && absY < y && absY > y1) {
			return true;
		}
		return false;
	}

	public boolean Area(final int x1, final int x2, final int y1, final int y2) {
		return (absX >= x1 && absX <= x2 && absY >= y1 && absY <= y2);
	}

	public boolean inBank() {
		return Area(3090, 3099, 3487, 3500) || Area(3089, 3090, 3492, 3498) || Area(3248, 3258, 3413, 3428) || Area(3179, 3191, 3432, 3448)
				|| Area(2944, 2948, 3365, 3374) || Area(2942, 2948, 3367, 3374) || Area(2944, 2950, 3365, 3370) || Area(3008, 3019, 3352, 3359)
				|| Area(3017, 3022, 3352, 3357) || Area(3203, 3213, 3200, 3237) || Area(3212, 3215, 3200, 3235) || Area(3215, 3220, 3202, 3235)
				|| Area(3220, 3227, 3202, 3229) || Area(3227, 3230, 3208, 3226) || Area(3226, 3228, 3230, 3211) || Area(3227, 3229, 3208, 3226);
	}

	public boolean isInJail() {
		if (absX >= 2065 && absX <= 2111 && absY >= 4415 && absY <= 4455) {
			return true;
		}
		return false;
	}

	public boolean inCamWild() {
		if (absX > 3231 && absX < 3300 && absY > 3180 && absY < 3300 || absX > 3120 && absX < 3300 && absY > 3236 && absY < 3300 || absX > 3227
				&& absX < 3300 && absY > 3225 && absY < 3300) {
			return true;
		}
		return false;
	}

	public boolean inMining() {
		if (absY > 9700 && absY < 10366) {
			return true;
		}
		return false;
	}

	public boolean inWc() {
		if (absY > 3346 && absY < 3530 && absX > 2600 && absX < 2730) {
			return true;
		}
		return false;
	}

	public boolean inFarm() {
		if (absY > 3446 && absY < 3480 && absX > 2802 && absX < 2820) {
			return true;
		}
		return false;
	}

	public boolean inDz() {
		if (absY > 9620 && absY < 9657 && absX > 3344 && absX < 3381) {
			return true;
		}
		return false;
	}

	public boolean inFm() {
		if (absX > 2333 && absX < 2355 && absY > 3663 && absY < 3702) {
			return true;
		}
		return false;
	}

	public boolean inWild() {
		if (absX > 2941 && absX < 3392 && absY > 3524 && absY < 3968 || absX > 2941 && absX < 3392 && absY > 9918 && absY < 10366) {
			return true;
		}
		return false;
	}
	
	public boolean inClanWars() {
		if (Boundary.isIn(this, ClanWarsMap.getBoundaries())) {
			return true;
		}
		return false;
	}

	public boolean inEdgeville() {
		return (absX > 3040 && absX < 3200 && absY > 3460 && absY < 3519);
	}

	public boolean inDice() {
		if (absX > 3089 && absX < 3098 && absY > 3473 && absY < 3484) {
			return true;
		}
		return false;
	}

	public boolean arenas() {
		if (absX > 3331 && absX < 3391 && absY > 3242 && absY < 3260) {
			return true;
		}
		return false;
	}

	public boolean inDuelArena() {
		if ((absX > 3322 && absX < 3394 && absY > 3195 && absY < 3291) || (absX > 3311 && absX < 3323 && absY > 3223 && absY < 3248)) {
			return true;
		}
		return false;
	}

	public boolean inMulti() {
		if (Boundary.isIn(this, Zulrah.BOUNDARY)) {
			return true;
		}
		if ((absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607) || (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
				|| (absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967) || (absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967)
				|| (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831) || (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
				|| (absX >= 2824 && absX <= 2944 && absY >= 5258 && absY <= 5369) || (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
				|| (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647) || (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
				|| (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117) || (absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630)
				|| (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464) || (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
				|| (absX >= 2962 && absX <= 3006 && absY >= 3621 && absY <= 3659)
				|| (absX >= 3155 && absX <= 3214 && absY >= 3755 && absY <= 3803)) {
			return true;
		}
		return false;
	}

	public boolean inFightCaves() {
		return absX >= 2360 && absX <= 2445 && absY >= 5045 && absY <= 5125;
	}

	public boolean inPirateHouse() {
		return absX >= 3038 && absX <= 3044 && absY >= 3949 && absY <= 3959;
	}

	public boolean checkFullGear(Player c) {
		if (c.playerEquipment[0] >= 0 && c.playerEquipment[1] >= 0 && c.playerEquipment[2] >= 0 && c.playerEquipment[3] >= 0
				&& c.playerEquipment[4] >= 0 && c.playerEquipment[5] >= 0 && c.playerEquipment[7] >= 0 && c.playerEquipment[9] >= 0
				&& c.playerEquipment[10] >= 0 && c.playerEquipment[12] >= 0) {
			return true;
		}
		return false;
	}

	public void updateshop(int i) {
		Player p = PlayerHandler.players[playerId];
		p.getShops().resetShop(i);
	}

	public void println_debug(String str) {
		System.out.println("[player-" + playerId + "]: " + str);
	}

	public void println(String str) {
		System.out.println("[player-" + playerId + "]: " + str);
	}

	public boolean WithinDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean withinDistance(Player otherPlr) {
		if (heightLevel != otherPlr.heightLevel)
			return false;
		int deltaX = otherPlr.absX - absX, deltaY = otherPlr.absY - absY;
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(NPC npc) {
		if (heightLevel != npc.heightLevel)
			return false;
		if (npc.needRespawn == true)
			return false;
		int deltaX = npc.absX - absX, deltaY = npc.absY - absY;
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public boolean withinDistance(int absX, int getY, int getHeightLevel) {
		if (this.getHeightLevel() != getHeightLevel)
			return false;
		int deltaX = this.getX() - absX, deltaY = this.getY() - getY;
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public int getHeightLevel() {
		return getHeightLevel;
	}

	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
	}

	public void resetWalkingQueue() {
		wQueueReadPtr = wQueueWritePtr = 0;
	
		for (int i = 0; i < walkingQueueSize; i++) {
			walkingQueueX[i] = currentX;
			walkingQueueY[i] = currentY;
		}
	}

	public void addToWalkingQueue(int x, int y) {
		// if (VirtualWorld.I(heightLevel, absX, absY, x, y, 0)) {
		int next = (wQueueWritePtr + 1) % walkingQueueSize;
		if (next == wQueueWritePtr)
			return;
		walkingQueueX[wQueueWritePtr] = x;
		walkingQueueY[wQueueWritePtr] = y;
		wQueueWritePtr = next;
		// }
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks the combat distance to see if the player is in an appropriate
	 * location based on the attack style.
	 * 
	 * @param attacker
	 * @param target
	 * @return
	 */
	public boolean checkCombatDistance(Player attacker, Player target) {
		int distance = Misc.distanceBetween(attacker, target);
		int required_distance = this.getDistanceRequired();
		return (this.usingMagic || this.usingRangeWeapon || this.usingBow || this.autocasting) && distance <= required_distance ? true
				: (this.usingMelee && this.isMoving && distance <= required_distance ? true : distance == 1
						&& (this.freezeTimer <= 0 || this.getX() == target.getX() || this.getY() == target.getY()));
	}

	public int getDistanceRequired() {
		return !this.usingMagic && !this.usingRangeWeapon && !usingBow && !this.autocasting ? (this.isMoving ? 3 : 1) : 9;
	}

	public int getNextWalkingDirection() {
			if (wQueueReadPtr == wQueueWritePtr)
				return -1;
			int dir;
			do {
				dir = Misc.direction(currentX, currentY, walkingQueueX[wQueueReadPtr], walkingQueueY[wQueueReadPtr]);
				if (dir == -1) {
					wQueueReadPtr = (wQueueReadPtr + 1) % walkingQueueSize;
				} else if ((dir & 1) != 0) {
					println_debug("Invalid waypoint in walking queue!");
					resetWalkingQueue();
					return -1;
				}
			} while ((dir == -1) && (wQueueReadPtr != wQueueWritePtr));
			if (dir == -1)
				return -1;
			dir >>= 1;
			currentX += Misc.directionDeltaX[dir];
			currentY += Misc.directionDeltaY[dir];
			absX += Misc.directionDeltaX[dir];
			absY += Misc.directionDeltaY[dir];
	/*
			 * if (isRunning()) { Client c = (Client) this; if (runEnergy > 0) {
			 * runEnergy--; c.getPA().sendFrame126(runEnergy + "%", 149); } else {
			 * isRunning2 = false; c.getPA().setConfig(173, 0); } }
			 */
			return dir;
		}

	public boolean isRunning() {
		return isNewWalkCmdIsRunning() || (isRunning2 && isMoving);
	}

	public void getNextPlayerMovement() {
		mapRegionDidChange = false;
		didTeleport = false;
		dir1 = dir2 = -1;
		if (teleportToX != -1 && teleportToY != -1) {
			mapRegionDidChange = true;
			if (mapRegionX != -1 && mapRegionY != -1) {
				int relX = teleportToX - mapRegionX * 8, relY = teleportToY - mapRegionY * 8;
				if (relX >= 2 * 8 && relX < 11 * 8 && relY >= 2 * 8 && relY < 11 * 8)
					mapRegionDidChange = false;
			}
			if (mapRegionDidChange) {
				mapRegionX = (teleportToX >> 3) - 6;
				mapRegionY = (teleportToY >> 3) - 6;
			}
			currentX = teleportToX - 8 * mapRegionX;
			currentY = teleportToY - 8 * mapRegionY;
			absX = teleportToX;
			absY = teleportToY;
			resetWalkingQueue();
	
			teleportToX = teleportToY = -1;
			didTeleport = true;
		} else {
			dir1 = getNextWalkingDirection();
			if (dir1 == -1)
				return;
			if (isRunning) {
				dir2 = getNextWalkingDirection();
			}
			int deltaX = 0, deltaY = 0;
			if (currentX < 2 * 8) {
				deltaX = 4 * 8;
				mapRegionX -= 4;
				mapRegionDidChange = true;
			} else if (currentX >= 11 * 8) {
				deltaX = -4 * 8;
				mapRegionX += 4;
				mapRegionDidChange = true;
			}
			if (currentY < 2 * 8) {
				deltaY = 4 * 8;
				mapRegionY -= 4;
				mapRegionDidChange = true;
			} else if (currentY >= 11 * 8) {
				deltaY = -4 * 8;
				mapRegionY += 4;
				mapRegionDidChange = true;
			}
	
			if (mapRegionDidChange) {
				currentX += deltaX;
				currentY += deltaY;
				for (int i = 0; i < walkingQueueSize; i++) {
					walkingQueueX[i] += deltaX;
					walkingQueueY[i] += deltaY;
				}
			}
	
		}
	}

	public void updateThisPlayerMovement(Stream str) {
		// synchronized(this) {
		if (mapRegionDidChange) {
			str.createFrame(73);
			str.writeWordA(mapRegionX + 6);
			str.writeWord(mapRegionY + 6);
		}
	
		if (didTeleport) {
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			str.writeBits(1, 1);
			str.writeBits(2, 3);
			str.writeBits(2, heightLevel);
			str.writeBits(1, 1);
			str.writeBits(1, (updateRequired) ? 1 : 0);
			str.writeBits(7, currentY);
			str.writeBits(7, currentX);
			return;
		}
	
		if (dir1 == -1) {
			// don't have to update the character position, because we're just
			// standing
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			isMoving = false;
			if (updateRequired) {
				// tell client there's an update block appended at the end
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
			if (DirectionCount < 50) {
				DirectionCount++;
			}
		} else {
			DirectionCount = 0;
			str.createFrameVarSizeWord(81);
			str.initBitAccess();
			str.writeBits(1, 1);
	
			if (dir2 == -1) {
				isMoving = true;
				str.writeBits(2, 1);
				str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
				if (updateRequired)
					str.writeBits(1, 1);
				else
					str.writeBits(1, 0);
			} else {
				isMoving = true;
				str.writeBits(2, 2);
				str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
				str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
				if (updateRequired)
					str.writeBits(1, 1);
				else
					str.writeBits(1, 0);
			}
		}
	
	}

	public void updatePlayerMovement(Stream str) {
		// synchronized(this) {
		if (dir1 == -1) {
			if (updateRequired || isChatTextUpdateRequired()) {
	
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else
				str.writeBits(1, 0);
		} else if (dir2 == -1) {
	
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
			str.writeBits(1, (updateRequired || isChatTextUpdateRequired()) ? 1 : 0);
		} else {
	
			str.writeBits(1, 1);
			str.writeBits(2, 2);
			str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
			str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
			str.writeBits(1, (updateRequired || isChatTextUpdateRequired()) ? 1 : 0);
		}
	
	}

	public void addNewNPC(NPC npc, Stream str, Stream updateBlock) {
		// synchronized(this) {
		int id = npc.npcId;
		npcInListBitmap[id >> 3] |= 1 << (id & 7);
		npcList[npcListSize++] = npc;
	
		str.writeBits(14, id);
	
		int z = npc.absY - absY;
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		z = npc.absX - absX;
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
	
		str.writeBits(1, 0);
		str.writeBits(14, npc.npcType);
	
		boolean savedUpdateRequired = npc.updateRequired;
		npc.updateRequired = true;
		npc.appendNPCUpdateBlock(updateBlock);
		npc.updateRequired = savedUpdateRequired;
		str.writeBits(1, 1);
	}

	public void addNewPlayer(Player plr, Stream str, Stream updateBlock) {
		// synchronized(this) {
		if (playerListSize >= 255) {
			return;
		}
		int id = plr.playerId;
		playerInListBitmap[id >> 3] |= 1 << (id & 7);
		playerList[playerListSize++] = plr;
		str.writeBits(11, id);
		str.writeBits(1, 1);
		boolean savedFlag = plr.isAppearanceUpdateRequired();
		boolean savedUpdateRequired = plr.updateRequired;
		plr.setAppearanceUpdateRequired(true);
		plr.updateRequired = true;
		plr.appendPlayerUpdateBlock(updateBlock);
		plr.setAppearanceUpdateRequired(savedFlag);
		plr.updateRequired = savedUpdateRequired;
		str.writeBits(1, 1);
		int z = plr.absY - absY;
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		z = plr.absX - absX;
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
	}

	protected void appendPlayerAppearance(Stream str) {
		playerProps.currentOffset = 0;
		playerProps.writeByte(playerAppearance[0]);
		StringBuilder sb = new StringBuilder(titles.getCurrentTitle());
		if (titles.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		playerProps.writeString(sb.toString());
		sb = new StringBuilder(titles.getCurrentTitleColor());
		if (titles.getCurrentTitle().equalsIgnoreCase("None")) {
			sb.delete(0, sb.length());
		}
		playerProps.writeString(sb.toString());
		if (venomDamage > 0) {
			playerProps.writeByte(2);
		} else if (poisonDamage > 0) {
			playerProps.writeByte(1);
		} else {
			playerProps.writeByte(0);
		}
		playerProps.writeByte(headIcon);
		playerProps.writeByte(headIconPk);
		if (isNpc == false) {
			if (playerEquipment[playerHat] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerHat]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerCape] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerCape]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerAmulet] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerAmulet]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerWeapon] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerWeapon]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerChest] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerChest]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[2]);
			}
	
			if (playerEquipment[playerShield] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerShield]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (!Item.isFullBody(playerEquipment[playerChest])) {
				playerProps.writeWord(0x100 + playerAppearance[3]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerLegs] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerLegs]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[5]);
			}
	
			if (!Item.isFullHelm(playerEquipment[playerHat]) && !Item.isFullMask(playerEquipment[playerHat])) {
				playerProps.writeWord(0x100 + playerAppearance[1]);
			} else {
				playerProps.writeByte(0);
			}
	
			if (playerEquipment[playerHands] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerHands]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[4]);
			}
	
			if (playerEquipment[playerFeet] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerFeet]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[6]);
			}
	
			if (playerAppearance[0] != 1 && !Item.isFullMask(playerEquipment[playerHat])) {
				playerProps.writeWord(0x100 + playerAppearance[7]);
			} else {
				playerProps.writeByte(0);
			}
		} else {
			playerProps.writeWord(-1);
			playerProps.writeWord(npcId2);
		}
		playerProps.writeByte(playerAppearance[8]);
		playerProps.writeByte(playerAppearance[9]);
		playerProps.writeByte(playerAppearance[10]);
		playerProps.writeByte(playerAppearance[11]);
		playerProps.writeByte(playerAppearance[12]);
		playerProps.writeWord(playerStandIndex); // standAnimIndex
		playerProps.writeWord(playerTurnIndex); // standTurnAnimIndex
		playerProps.writeWord(playerWalkIndex); // walkAnimIndex
		playerProps.writeWord(playerTurn180Index); // turn180AnimIndex
		playerProps.writeWord(playerTurn90CWIndex); // turn90CWAnimIndex
		playerProps.writeWord(playerTurn90CCWIndex); // turn90CCWAnimIndex
		playerProps.writeWord(playerRunIndex); // runAnimIndex
		playerProps.writeQWord(Misc.playerNameToInt64(playerName));
		playerProps.writeByte(invisible ? 1 : 0);
		combatLevel = calculateCombatLevel();
		playerProps.writeByte(combatLevel); // combat level
		playerProps.writeByte(rights.getValue());
		playerProps.writeWord(0);
		str.writeByteC(playerProps.currentOffset);
		str.writeBytes(playerProps.buffer, playerProps.currentOffset, 0);
	}

	public int calculateCombatLevel() {
		int j = getLevelForXP(playerXP[playerAttack]);
		int k = getLevelForXP(playerXP[playerDefence]);
		int l = getLevelForXP(playerXP[playerStrength]);
		int i1 = getLevelForXP(playerXP[playerHitpoints]);
		int j1 = getLevelForXP(playerXP[playerPrayer]);
		int k1 = getLevelForXP(playerXP[playerRanged]);
		int l1 = getLevelForXP(playerXP[playerMagic]);
		int combatLevel = (int) (((k + i1) + Math.floor(j1 / 2)) * 0.25D) + 1;
		double d = (j + l) * 0.32500000000000001D;
		double d1 = Math.floor(k1 * 1.5D) * 0.32500000000000001D;
		double d2 = Math.floor(l1 * 1.5D) * 0.32500000000000001D;
		if (d >= d1 && d >= d2) {
			combatLevel += d;
		} else if (d1 >= d && d1 >= d2) {
			combatLevel += d1;
		} else if (d2 >= d && d2 >= d1) {
			combatLevel += d2;
		}
		return combatLevel;
	}

	public int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
	
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp)
				return lvl;
		}
		return 99;
	}

	protected void appendPlayerChatText(Stream str) {
		// synchronized(this) {
		str.writeWordBigEndian(((getChatTextColor() & 0xFF) << 8) + (getChatTextEffects() & 0xFF));
		str.writeByte(rights.getValue());
		str.writeByteC(getChatTextSize());
		str.writeBytes_reverse(getChatText(), getChatTextSize(), 0);
	
	}

	public void forcedChat(String text) {
		forcedText = text;
		forcedChatUpdateRequired = true;
		updateRequired = true;
		setAppearanceUpdateRequired(true);
	}

	public void appendForcedChat(Stream str) {
		// synchronized(this) {
		str.writeString(forcedText);
	}

	public void appendMask100Update(Stream str) {
		// synchronized(this) {
		str.writeWordBigEndian(mask100var1);
		str.writeDWord(mask100var2);
	
	}

	public void gfx100(int gfx) {
		mask100var1 = gfx;
		mask100var2 = 6553600;
		mask100update = true;
		updateRequired = true;
	}

	public void gfx0(int gfx) {
		mask100var1 = gfx;
		mask100var2 = 65536;
		mask100update = true;
		updateRequired = true;
	}

	public boolean wearing2h() {
		Player c = this;
		String s = c.getItems().getItemName(c.playerEquipment[c.playerWeapon]);
		if (s.contains("2h"))
			return true;
		else if (s.contains("godsword"))
			return true;
		return false;
	}

	/**
	 * Animations
	 **/
	public void startAnimation(int animId) {
		if (wearing2h() && animId == 829)
			return;
		animationRequest = animId;
		animationWaitCycles = 0;
		updateRequired = true;
	}

	public void startAnimation(int animId, int time) {
		animationRequest = animId;
		animationWaitCycles = time;
		updateRequired = true;
	}

	public void stopAnimation() {
		animationRequest = 65535;
		animationWaitCycles = 0;
		updateRequired = true;
	}

	public void appendAnimationRequest(Stream str) {
		// synchronized(this) {
		str.writeWordBigEndian((animationRequest == -1) ? 65535 : animationRequest);
		str.writeByteC(animationWaitCycles);
	
	}

	public void faceUpdate(int index) {
		face = index;
		faceUpdateRequired = true;
		updateRequired = true;
	}

	public void appendFaceUpdate(Stream str) {
		// synchronized(this) {
		str.writeWordBigEndian(face);
	
	}

	public void turnPlayerTo(int pointX, int pointY) {
		FocusPointX = 2 * pointX + 1;
		FocusPointY = 2 * pointY + 1;
		updateRequired = true;
	}

	private void appendSetFocusDestination(Stream str) {
		// synchronized(this) {
		str.writeWordBigEndianA(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	
	}

	/**
	 * Hit Update
	 **/
	protected void appendHitUpdate(Stream str) {
		str.writeByte(hitDiff);
		if (hitmark == null) {
			str.writeByteA(0);
		} else {
			str.writeByteA(hitmark.getId());
		}
		if (playerLevel[3] <= 0) {
			playerLevel[3] = 0;
			isDead = true;
		}
		str.writeByteC(playerLevel[3]);
		str.writeByte(getLevelForXP(playerXP[3])); 
	}

	protected void appendHitUpdate2(Stream str) {
		str.writeByte(hitDiff2);
		if (secondHitmark == null) {
			str.writeByteS(0);
		} else {
			str.writeByteS(secondHitmark.getId());
		}
		if (playerLevel[3] <= 0) {
			playerLevel[3] = 0;
			isDead = true;
		}
		str.writeByte(playerLevel[3]);
		str.writeByteC(getLevelForXP(playerXP[3]));
	
	}

	public void appendPlayerUpdateBlock(Stream str) {
		// synchronized(this) {
		if (!updateRequired && !isChatTextUpdateRequired())
			return; // nothing required
		int updateMask = 0;
		if (mask100update) {
			updateMask |= 0x100;
		}
		if (animationRequest != -1) {
			updateMask |= 8;
		}
		if (forcedChatUpdateRequired) {
			updateMask |= 4;
		}
		if (isChatTextUpdateRequired()) {
			updateMask |= 0x80;
		}
		if (isAppearanceUpdateRequired()) {
			updateMask |= 0x10;
		}
		if (faceUpdateRequired) {
			updateMask |= 1;
		}
		if (FocusPointX != -1) {
			updateMask |= 2;
		}
		if (hitUpdateRequired) {
			updateMask |= 0x20;
		}
	
		if (hitUpdateRequired2) {
			updateMask |= 0x200;
		}
	
		if (updateMask >= 0x100) {
			updateMask |= 0x40;
			str.writeByte(updateMask & 0xFF);
			str.writeByte(updateMask >> 8);
		} else {
			str.writeByte(updateMask);
		}
		if (mask100update) {
			appendMask100Update(str);
		}
		if (animationRequest != -1) {
			appendAnimationRequest(str);
		}
		if (forcedChatUpdateRequired) {
			appendForcedChat(str);
		}
		if (isChatTextUpdateRequired()) {
			appendPlayerChatText(str);
		}
		if (faceUpdateRequired) {
			appendFaceUpdate(str);
		}
		if (isAppearanceUpdateRequired()) {
			appendPlayerAppearance(str);
		}
		if (FocusPointX != -1) {
			appendSetFocusDestination(str);
		}
		if (hitUpdateRequired) {
			appendHitUpdate(str);
		}
		if (hitUpdateRequired2) {
			appendHitUpdate2(str);
		}
	
	}

	public void clearUpdateFlags() {
		updateRequired = false;
		setChatTextUpdateRequired(false);
		setAppearanceUpdateRequired(false);
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		forcedChatUpdateRequired = false;
		mask100update = false;
		animationRequest = -1;
		FocusPointX = -1;
		FocusPointY = -1;
		faceUpdateRequired = false;
		face = 65535;
	}

	public void stopMovement() {
		if (teleportToX <= 0 && teleportToY <= 0) {
			teleportToX = absX;
			teleportToY = absY;
		}
		newWalkCmdSteps = 0;
		getNewWalkCmdX()[0] = getNewWalkCmdY()[0] = travelBackX[0] = travelBackY[0] = 0;
		getNextPlayerMovement();
	}

	public void preProcessing() {
		newWalkCmdSteps = 0;
	}

	public int setPacketsReceived(int packetsReceived) {
		return packetsReceived;
	}

	public AtomicInteger getPacketsReceived() {
		return packetsReceived;
	}

	public void postProcessing() {
		if (newWalkCmdSteps > 0) {
			int firstX = getNewWalkCmdX()[0], firstY = getNewWalkCmdY()[0];
	
			int lastDir = 0;
			boolean found = false;
			numTravelBackSteps = 0;
			int ptr = wQueueReadPtr;
			int dir = Misc.direction(currentX, currentY, firstX, firstY);
			if (dir != -1 && (dir & 1) != 0) {
				do {
					lastDir = dir;
					if (--ptr < 0)
						ptr = walkingQueueSize - 1;
	
					travelBackX[numTravelBackSteps] = walkingQueueX[ptr];
					travelBackY[numTravelBackSteps++] = walkingQueueY[ptr];
					dir = Misc.direction(walkingQueueX[ptr], walkingQueueY[ptr], firstX, firstY);
					if (lastDir != dir) {
						found = true;
						break;
					}
	
				} while (ptr != wQueueWritePtr);
			} else
				found = true;
	
			if (!found)
				println_debug("Fatal: couldn't find connection vertex! Dropping packet.");
			else {
				wQueueWritePtr = wQueueReadPtr;
	
				addToWalkingQueue(currentX, currentY);
	
				if (dir != -1 && (dir & 1) != 0) {
	
					for (int i = 0; i < numTravelBackSteps - 1; i++) {
						addToWalkingQueue(travelBackX[i], travelBackY[i]);
					}
					int wayPointX2 = travelBackX[numTravelBackSteps - 1], wayPointY2 = travelBackY[numTravelBackSteps - 1];
					int wayPointX1, wayPointY1;
					if (numTravelBackSteps == 1) {
						wayPointX1 = currentX;
						wayPointY1 = currentY;
					} else {
						wayPointX1 = travelBackX[numTravelBackSteps - 2];
						wayPointY1 = travelBackY[numTravelBackSteps - 2];
					}
	
					dir = Misc.direction(wayPointX1, wayPointY1, wayPointX2, wayPointY2);
					if (dir == -1 || (dir & 1) != 0) {
						println_debug("Fatal: The walking queue is corrupt! wp1=(" + wayPointX1 + ", " + wayPointY1 + "), " + "wp2=(" + wayPointX2
								+ ", " + wayPointY2 + ")");
					} else {
						dir >>= 1;
						found = false;
						int x = wayPointX1, y = wayPointY1;
						while (x != wayPointX2 || y != wayPointY2) {
							x += Misc.directionDeltaX[dir];
							y += Misc.directionDeltaY[dir];
							if ((Misc.direction(x, y, firstX, firstY) & 1) == 0) {
								found = true;
								break;
							}
						}
						if (!found) {
							println_debug("Fatal: Internal error: unable to determine connection vertex!" + "  wp1=(" + wayPointX1 + ", "
									+ wayPointY1 + "), wp2=(" + wayPointX2 + ", " + wayPointY2 + "), " + "first=(" + firstX + ", " + firstY + ")");
						} else
							addToWalkingQueue(wayPointX1, wayPointY1);
					}
				} else {
					for (int i = 0; i < numTravelBackSteps; i++) {
						addToWalkingQueue(travelBackX[i], travelBackY[i]);
					}
				}
	
				for (int i = 0; i < newWalkCmdSteps; i++) {
					addToWalkingQueue(getNewWalkCmdX()[i], getNewWalkCmdY()[i]);
				}
	
			}
	
			isRunning = isNewWalkCmdIsRunning() || isRunning2;
		}
	}

	public int getMapRegionX() {
		return mapRegionX;
	}

	public int getMapRegionY() {
		return mapRegionY;
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	public int getId() {
		return playerId;
	}

	public boolean inCook() {
		return absX >= 3029 && absX <= 3032 && absY >= 3382 && absY <= 3384;
	}

	public boolean inPcBoat() {
		return absX >= 2660 && absX <= 2663 && absY >= 2638 && absY <= 2643;
	}

	public boolean inPcGame() {
		return absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619;
	}

	public void setHitDiff(int hitDiff) {
		this.hitDiff = hitDiff;
	}

	public void setHitDiff2(int hitDiff2) {
		this.hitDiff2 = hitDiff2;
	}

	public int getHitDiff() {
		return hitDiff;
	}

	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}

	public void setChatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
	}

	public int getChatTextEffects() {
		return chatTextEffects;
	}

	public void setChatTextSize(byte chatTextSize) {
		this.chatTextSize = chatTextSize;
	}

	public byte getChatTextSize() {
		return chatTextSize;
	}

	public void setChatTextUpdateRequired(boolean chatTextUpdateRequired) {
		this.chatTextUpdateRequired = chatTextUpdateRequired;
	}

	public boolean isChatTextUpdateRequired() {
		return chatTextUpdateRequired;
	}

	public void setChatText(byte chatText[]) {
		this.chatText = chatText;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public void setChatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
	}

	public int getChatTextColor() {
		return chatTextColor;
	}

	public void setNewWalkCmdX(int newWalkCmdX[]) {
		this.newWalkCmdX = newWalkCmdX;
	}

	public int[] getNewWalkCmdX() {
		return newWalkCmdX;
	}

	public void setNewWalkCmdY(int newWalkCmdY[]) {
		this.newWalkCmdY = newWalkCmdY;
	}

	public int[] getNewWalkCmdY() {
		return newWalkCmdY;
	}

	public void setNewWalkCmdIsRunning(boolean newWalkCmdIsRunning) {
		this.newWalkCmdIsRunning = newWalkCmdIsRunning;
	}

	public boolean isNewWalkCmdIsRunning() {
		return newWalkCmdIsRunning;
	}

	public void setInStreamDecryption(ISAACCipher inStreamDecryption) {
		this.inStreamDecryption = inStreamDecryption;
	}

	public void setOutStreamDecryption(ISAACCipher outStreamDecryption) {
		this.outStreamDecryption = outStreamDecryption;
	}

	public boolean samePlayer() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (j == playerId)
				continue;
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].playerName.equalsIgnoreCase(playerName)) {
					disconnected = true;
					return true;
				}
			}
		}
		return false;
	}

	public void putInCombat(int attacker) {
		underAttackBy = attacker;
		logoutDelay.reset();
		singleCombatDelay.reset();
	}

	public int appendDamage(int damage, Hitmark h) {
		if (damage < 0) {
			damage = 0;
			h = Hitmark.MISS;
		}
		if (playerLevel[playerHitpoints] - damage < 0) {
			damage = playerLevel[playerHitpoints];
		}
		if (teleTimer <= 0) {
			playerLevel[playerHitpoints] -= damage;
			if (!hitUpdateRequired) {
				hitUpdateRequired = true;
				hitDiff = damage;
				hitmark = h;
			} else if (!hitUpdateRequired2) {
				hitUpdateRequired2 = true;
				hitDiff2 = damage;
				secondHitmark = h;
			}
			this.getPA().refreshSkill(3);
		} else {
			if (hitUpdateRequired) {
				hitUpdateRequired = false;
			}
			if (hitUpdateRequired2) {
				hitUpdateRequired2 = false;
			}
		}
		updateRequired = true;
		return damage;
	}

	public void handleHitMask(int damage) {
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
		}
		updateRequired = true;
	}

	public String getLastClanChat() {
		return lastClanChat;
	}

	public long getNameAsLong() {
		return nameAsLong;
	}

	public void setNameAsLong(long hash) {
		this.nameAsLong = hash;
	}

	public boolean isStopPlayer() {
		return stopPlayer;
	}

	public void setStopPlayer(boolean stopPlayer) {
		this.stopPlayer = stopPlayer;
	}

	public int getPlayerIdentifier() {
		return this.playerId;
	}

	public int getFace() {
		return this.playerId + '\u8000';
	}

	public int getLockIndex() {
		return -this.playerId - 1;
	}

	public int getHeight() {
		return this.heightLevel;
	}

	public boolean isDead() {
		return this.playerLevel[3] <= 0 || this.isDead;
	}

	public void healPlayer(int heal) {
		this.playerLevel[3] += heal;
		if (this.playerLevel[3] > this.getLevelForXP(this.playerXP[3])) {
			this.playerLevel[3] = this.getLevelForXP(this.playerXP[3]);
		}
	
	}

	int maxLevel() {
		return 99;
	}

	public void sendGraphic(int id, int height) {
		if (height == 0) {
			this.gfx0(id);
		}
	
		if (height == 100) {
			this.gfx100(id);
		}
	
	}

	public boolean protectingRange() {
		return this.prayerActive[17];
	}

	public boolean protectingMagic() {
		return this.prayerActive[16];
	}

	public boolean protectingMelee() {
		return this.prayerActive[18];
	}

	public void setTrading(boolean trading) {
		this.trading = trading;
	}

	public boolean isTrading() {
		return this.trading;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public boolean inGodmode() {
		return godmode;
	}

	public void setGodmode(boolean godmode) {
		this.godmode = godmode;
	}
	
	public boolean isDebug() {
		return debug == 1;
	}
	
	public void setDebug(int debug) {
		this.debug = debug;
	}

	public boolean inSafemode() {
		return safemode;
	}

	public void setSafemode(boolean safemode) {
		this.safemode = safemode;
	}

	public void setDragonfireShieldCharge(int charge) {
		this.dragonfireShieldCharge = charge;
	}

	public int getDragonfireShieldCharge() {
		return dragonfireShieldCharge;
	}

	public void setLastDragonfireShieldAttack(long lastAttack) {
		this.lastDragonfireShieldAttack = lastAttack;
	}

	public long getLastDragonfireShieldAttack() {
		return lastDragonfireShieldAttack;
	}

	public boolean isDragonfireShieldActive() {
		return dragonfireShieldActive;
	}

	public void setDragonfireShieldActive(boolean dragonfireShieldActive) {
		this.dragonfireShieldActive = dragonfireShieldActive;
	}

	/**
	 * Retrieves the rights for this player.
	 * @return	the rights
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Updates the rights for this player by comparing the players
	 * current rights to that of the available rights and assigning
	 * the first rank found. 
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	/**
	 * Returns a single instance of the Titles class for this player
	 * @return	the titles class
	 */
	public Titles getTitles() {
		if (titles == null) {
			titles = new Titles(this);
		}
		return titles;
	}

	/**
	 * Mutates the current hitmark to be that of the new hitmark. This is commonly done in battle.
	 * @param hitmark	the hitmark
	 */
	public void setHitmark(Hitmark hitmark) {
		this.hitmark = hitmark;
	}

	/**
	 * Mutates the first and second hitmark. This is commonly done in battle.
	 * @param hitmark			the first hitmark
	 * @param secondHitmark		the second hitmark
	 */
	public void setHitmarks(Hitmark hitmark, Hitmark secondHitmark) {
		this.hitmark = hitmark;
		this.secondHitmark = secondHitmark;
	}

	public void appendPoisonDamage() {
		if (poisonDamage <= 0) {
			return;
		}
		Player client = this;
		if (poisonDamageHistory.size() >= 4) {
			poisonDamageHistory.clear();
			poisonDamage--;
		}
		if (poisonDamage <= 0) {
			client.sendMessage("The poison has subsided.");
			client.getPA().requestUpdates();
			return;
		}
		poisonDamageHistory.add(poisonDamage);
		appendDamage(poisonDamage, Hitmark.POISON);
		client.getPA().requestUpdates();
	}

	public void appendVenomDamage() {
		if (venomDamage <= 0) {
			return;
		}
		venomDamage += 2;
		if (venomDamage >= 20) {
			venomDamage = 20;
		}
		appendDamage(venomDamage, Hitmark.VENOM);
		getPA().requestUpdates();
	}

	/**
	 * Determines if the player is susceptible to poison but comparing
	 * the duration of their immunity to the time of the last cure.
	 * @return	true of they can be poisoned.
	 */
	public boolean isSusceptibleToPoison() {
		return System.currentTimeMillis() - this.lastPoisonCure > this.poisonImmunity;
	}

	/**
	 * The duration of time in milliseconds the player is immune to poison for
	 * @return	the duration of time the player is immune to poison for
	 */
	public long getPoisonImmunity() {
		return poisonImmunity;
	}

	/**
	 * Modifies the current duration of poison immunity
	 * @param duration	the new duration
	 */
	public void setPoisonImmunity(long duration) {
		this.poisonImmunity = duration;
	}

	/**
	 * The amount of damage received when hit by toxic
	 * @return	the toxic damage
	 */
	public byte getPoisonDamage() {
		return poisonDamage;
	}

	/**
	 * Sets the current amount of damage received when hit by toxic
	 * @param toxicDamage	the new amount of damage received
	 */
	public void setPoisonDamage(byte toxicDamage) {
		this.poisonDamage = toxicDamage;
	}

	/**
	 * The time in milliseconds of the last toxic damage the player received
	 * @return	the time in milliseconds of the last toxic damage hit
	 */
	public long getLastPoisonHit() {
		return lastPoisonHit;
	}

	/**
	 * Sets the last time, in milliseconds, the toxic damaged the player
	 * @param toxic	the time in milliseconds
	 */
	public void setLastPoisonHit(long toxic) {
		lastPoisonHit = toxic;
	}

	/**
	 * Sets the current amount of damage received when hit by venom
	 * @param venomDamage	the new amount of damage received
	 */
	public void setVenomDamage(byte venomDamage) {
		this.venomDamage = venomDamage;
	}

	/**
	 * The time in milliseconds of the last venom damage the player received
	 * @return	the time in milliseconds of the last venom damage hit
	 */
	public long getLastVenomHit() {
		return lastVenomHit;
	}

	/**
	 * Sets the last time, in milliseconds, the venom damaged the player
	 * @param venom	the time in milliseconds
	 */
	public void setLastVenomHit(long toxic) {
		lastVenomHit = toxic;
	}

	/**
	 * Determines if the player is susceptible to venom by comparing
	 * the duration of their immunity to the time of the last cure.
	 * @return	true of they can be infected by venom.
	 */
	public boolean isSusceptibleToVenom() {
		return System.currentTimeMillis() - lastVenomCure > venomImmunity && !getItems().isWearingItem(12931);
	}

	/**
	 * The duration of time in milliseconds the player is immune to venom for
	 * @return	the duration of time the player is immune to poison for
	 */
	public long getVenomImmunity() {
		return venomImmunity;
	}

	/**
	 * Modifies the current duration of venom immunity
	 * @param duration	the new duration
	 */
	public void setVenomImmunity(long duration) {
		this.venomImmunity = duration;
	}

	/**
	 * The amount of damage received when hit by venom
	 * @return	the venom damage
	 */
	public byte getVenomDamage() {
		return venomDamage;
	}

	/**
	 * The time in milliseconds that the player healed themselves of venom
	 * @return	the last time the player cured themself of poison
	 */
	public long getLastVenomCure() {
		return lastVenomCure;
	}

	/**
	 * Sets the time in milliseconds that the player cured themself of poison
	 * @param lastPoisonCure	the last time the player cured themselves
	 */
	public void setLastVenomCure(long lastVenomCure) {
		this.lastVenomCure = lastVenomCure;
	}

	/**
	 * Mutates the current hitmark to be that of the new hitmark. 
	 * This is commonly done in battle.
	 * @param hitmark	the hitmark
	 */
	public void setSecondHitmark(Hitmark secondHitmark) {
		this.secondHitmark = secondHitmark;
	}

	/**
	 * The time in milliseconds that the player healed themselves of poison
	 * @return	the last time the player cured themself of poison
	 */
	public long getLastPoisonCure() {
		return lastPoisonCure;
	}

	/**
	 * Sets the time in milliseconds that the player cured themself of poison
	 * @param lastPoisonCure	the last time the player cured themselves
	 */
	public void setLastPoisonCure(long lastPoisonCure) {
		this.lastPoisonCure = lastPoisonCure;
	}

	/**
	 * Retrieves the current hitmark
	 * @return the hitmark
	 */
	public Hitmark getHitmark() {
		return hitmark;
	}

	/**
	 * Retrieves the second hitmark
	 * @return the second hitmark
	 */
	public Hitmark getSecondHitmark() {
		return secondHitmark;
	}
	
	public RandomEventInterface getInterfaceEvent() {
		return randomEventInterface; 
	}
	
	/**
	 * The interface that is currently open
	 */
	private int interfaceOpen;
	
	/**
	 * Modifies the current interface open
	 * @param interfaceOpen	the interface id
	 */
	public void setInterfaceOpen(int interfaceOpen) {
		this.interfaceOpen = interfaceOpen;
	}
	
	/**
	 * The interface that is opened
	 * @return	the interface id
	 */
	public int getInterfaceOpen() {
		return interfaceOpen;
	}
	
	public long setBestZulrahTime(long bestZulrahTime) {
		return this.bestZulrahTime = bestZulrahTime;
	}
	
	public long getBestZulrahTime() {
		return bestZulrahTime;
	}
	
	public LostItems getLostItems() {
		if (lostItems == null) {
			lostItems = new LostItems(this);
		}
		return lostItems;
	}
	
	public int getToxicBlowpipeCharge() {
		return toxicBlowpipeCharge;
	}
	
	public void setToxicBlowpipeCharge(int charge) {
		this.toxicBlowpipeCharge = charge;
	}
	
	public int getToxicBlowpipeAmmo() {
		return toxicBlowpipeAmmo;
	}
	
	public int getToxicBlowpipeAmmoAmount() {
		return toxicBlowpipeAmmoAmount;
	}
	
	public void setToxicBlowpipeAmmoAmount(int amount) {
		this.toxicBlowpipeAmmoAmount = amount;
	}
	
	public void setToxicBlowpipeAmmo(int ammo) {
		this.toxicBlowpipeAmmo = ammo;
	}
	
	public int getSerpentineHelmCharge() {
		return this.serpentineHelmCharge;
	}
	public int getToxicStaffOfDeadCharge() {
		return this.toxicStaffOfDeadCharge;
	}
	
	public void setSerpentineHelmCharge(int charge) {
		this.serpentineHelmCharge = charge;
	}
	public void setToxicStaffOfDeadCharge(int charge) {
		this.toxicStaffOfDeadCharge = charge;
	}
	public int getTridentCharge() {
		return tridentCharge;
	}

	public void setTridentCharge(int tridentCharge) {
		this.tridentCharge = tridentCharge;
	}

	public int getToxicTridentCharge() {
		return toxicTridentCharge;
	}

	public void setToxicTridentCharge(int toxicTridentCharge) {
		this.toxicTridentCharge = toxicTridentCharge;
	}

	public static final int maxPlayerListSize = Config.MAX_PLAYERS;
	public static final int maxNPCListSize = NPCHandler.maxNPCs;
	protected static Stream playerProps;
	
	static {
		playerProps = new Stream(new byte[100]);
	}

	public void updateRank() {
		if (amDonated >= 10 && amDonated < 30 && getRights().getValue() == 0) {
			setRights(Rights.CONTRIBUTOR);
		}
		if (amDonated >= 30 && amDonated < 75 && getRights().getValue() <= 5 && !getRights().isStaff()) {
			setRights(Rights.SPONSOR);
		}
		if (amDonated >= 75 && amDonated < 150 && getRights().getValue() <= 6 && !getRights().isStaff()) {
			setRights(Rights.SUPPORTER);
		}
		if (amDonated >= 150 && amDonated < 300 && getRights().getValue() <= 7 && !getRights().isStaff()) {
			setRights(Rights.V_I_P);
		}
		if (amDonated >= 300 && !getRights().isStaff()) {
			setRights(Rights.SUPER_V_I_P);
		}
	}

}