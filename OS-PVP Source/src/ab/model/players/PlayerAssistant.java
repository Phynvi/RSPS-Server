package ab.model.players;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.text.WordUtils;

import ab.Config;
import ab.Connection;
import ab.Server;
import ab.clip.Region;
import ab.event.CycleEventHandler;
import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.content.instances.InstancedArea;
import ab.model.content.instances.InstancedAreaManager;
import ab.model.content.kill_streaks.Killstreak;
import ab.model.content.zulrah.Zulrah;
import ab.model.holiday.HolidayController;
import ab.model.holiday.halloween.HalloweenDeathCycleEvent;
import ab.model.items.Item;
import ab.model.items.ItemAssistant;
import ab.model.items.bank.BankTab;
import ab.model.minigames.bounty_hunter.TargetState;
import ab.model.minigames.pest_control.PestControl;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.clan_wars.ClanWarsMap;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;
import ab.model.players.combat.Damage;
import ab.model.players.combat.Degrade;
import ab.model.players.combat.Degrade.DegradableItem;
import ab.model.players.combat.effects.DragonfireShieldEffect;
import ab.model.players.skills.CraftingData;
import ab.model.players.skills.Skill;
import ab.model.players.skills.SkillHandler;
import ab.net.outgoing.messages.ComponentVisibility;
import ab.util.Misc;
import ab.util.Stream;
import ab.world.Clan;

public class PlayerAssistant {

	private Player c;

	public PlayerAssistant(Player Client) {
		this.c = Client;
	}

	public int CraftInt, Dcolor, FletchInt;

	public void destroyItem(int itemId) {
		itemId = c.droppedItem;
		String itemName = ItemAssistant.getItemName(itemId);
		c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), c.playerItemsN[c.getItems().getItemSlot(itemId)]);
		c.sendMessage("Your " + itemName + " vanishes as you drop it on the ground.");
		removeAllWindows();
	}

	public void destroyInterface(int itemId) {// Destroy item created by Remco
		itemId = c.droppedItem;// The item u are dropping
		String itemName = ItemAssistant.getItemName(c.droppedItem);
		String[][] info = {// The info the dialogue gives
		{ "Are you sure you want to drop this item?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" },
				{ "This item is valuable, you will not", "14182" }, { "get it back once clicked yes.", "14183" }, { itemName, "14184" } };
		sendFrame34(itemId, 0, 14171, 1);
		for (int i = 0; i < info.length; i++)
			sendFrame126(info[i][0], Integer.parseInt(info[i][1]));
		sendFrame164(14170);
	}
	public void assembleSlayerHelmet() {
		if (c.slayerRecipe == false) {
			c.sendMessage("@blu@You must purchase the recipe from the slayer shop");
			c.sendMessage("@blu@before assembling a slayer helmet.");
			return;
		}
		if (c.playerLevel[c.playerCrafting] < 55) {
			c.sendMessage("@blu@You need a crafting level of 55 to assemble a slayer helmet.");
			return;
		}
		if (c.getItems().playerHasItem(4166) && c.getItems().playerHasItem(4168) && c.getItems().playerHasItem(4164) && c.getItems().playerHasItem(4551) && c.getItems().playerHasItem(8901)) {
			c.sendMessage("@blu@You assemble the pieces and create a full slayer helmet!");
			c.getItems().deleteItem(4166, 1);
			c.getItems().deleteItem(4164, 1);
			c.getItems().deleteItem(4168, 1);
			c.getItems().deleteItem(4551, 1);
			c.getItems().deleteItem(8901, 1);
			c.getItems().addItem(11864, 1);
		} else {
			c.sendMessage("You need a @blu@Facemask@bla@, @blu@Nose peg@bla@, @blu@Spiny helmet@bla@ and @blu@Earmuffs");
			c.sendMessage("@bla@in order to assemble a slayer helmet.");
		}
	}
	public long lastReward = System.currentTimeMillis();

	Properties p = new Properties();

	public void loadAnnouncements() {
		try {
			loadIni();
			if (p.getProperty("announcement1").length() > 0) {
				c.sendMessage(p.getProperty("announcement1"));
			}
			if (p.getProperty("announcement2").length() > 0) {
				c.sendMessage(p.getProperty("announcement2"));
			}
			if (p.getProperty("announcement3").length() > 0) {
				c.sendMessage(p.getProperty("announcement3"));
			}
		} catch (Exception e) {
		}
	}

	private void loadIni() {
		try {
			p.load(new FileInputStream("./Announcements.ini"));
		} catch (Exception e) {
		}
	}

	public boolean wearingCape(int cape) {
		int capes[] = { 9747, 9748, 9750, 9751, 9753, 9754, 9756, 9757, 9759, 9760, 9762, 9763, 9765, 9766, 9768, 9769, 9771, 9772, 9774, 9775, 9777,
				9778, 9780, 9781, 9783, 9784, 9786, 9787, 9789, 9790, 9792, 9793, 9795, 9796, 9798, 9799, 9801, 9802, 9804, 9805, 9807, 9808, 9810,
				9811, 10662 };
		for (int i = 0; i < capes.length; i++) {
			if (capes[i] == cape) {
				return true;
			}
		}
		return false;
	}

	public int skillcapeGfx(int cape) {
		int capeGfx[][] = { { 9747, 823 }, { 9748, 823 }, { 9750, 828 }, { 9751, 828 }, { 9753, 824 }, { 9754, 824 }, { 9756, 832 }, { 9757, 832 },
				{ 9759, 829 }, { 9760, 829 }, { 9762, 813 }, { 9763, 813 }, { 9765, 817 }, { 9766, 817 }, { 9768, 833 }, { 9769, 833 },
				{ 9771, 830 }, { 9772, 830 }, { 9774, 835 }, { 9775, 835 }, { 9777, 826 }, { 9778, 826 }, { 9780, 818 }, { 9781, 818 },
				{ 9783, 812 }, { 9784, 812 }, { 9786, 827 }, { 9787, 827 }, { 9789, 820 }, { 9790, 820 }, { 9792, 814 }, { 9793, 814 },
				{ 9795, 815 }, { 9796, 815 }, { 9798, 819 }, { 9799, 819 }, { 9801, 821 }, { 9802, 821 }, { 9804, 831 }, { 9805, 831 },
				{ 9807, 822 }, { 9808, 822 }, { 9810, 825 }, { 9811, 825 }, { 10662, 816 } };
		for (int i = 0; i < capeGfx.length; i++) {
			if (capeGfx[i][0] == cape) {
				return capeGfx[i][1];
			}
		}
		return -1;
	}

	public int skillcapeEmote(int cape) {
		int capeEmote[][] = { { 9747, 4959 }, { 9748, 4959 }, { 9750, 4981 }, { 9751, 4981 }, { 9753, 4961 }, { 9754, 4961 }, { 9756, 4973 },
				{ 9757, 4973 }, { 9759, 4979 }, { 9760, 4979 }, { 9762, 4939 }, { 9763, 4939 }, { 9765, 4947 }, { 9766, 4947 }, { 9768, 4971 },
				{ 9769, 4971 }, { 9771, 4977 }, { 9772, 4977 }, { 9774, 4969 }, { 9775, 4969 }, { 9777, 4965 }, { 9778, 4965 }, { 9780, 4949 },
				{ 9781, 4949 }, { 9783, 4937 }, { 9784, 4937 }, { 9786, 4967 }, { 9787, 4967 }, { 9789, 4953 }, { 9790, 4953 }, { 9792, 4941 },
				{ 9793, 4941 }, { 9795, 4943 }, { 9796, 4943 }, { 9798, 4951 }, { 9799, 4951 }, { 9801, 4955 }, { 9802, 4955 }, { 9804, 4975 },
				{ 9805, 4975 }, { 9807, 4957 }, { 9808, 4957 }, { 9810, 4963 }, { 9811, 4963 }, { 10662, 4945 } };
		for (int i = 0; i < capeEmote.length; i++) {
			if (capeEmote[i][0] == cape) {
				return capeEmote[i][1];
			}
		}
		return -1;
	}

	public void otherInv(Player c, Player o) {
		if (o == c || o == null || c == null)
			return;
		int[] backupItems = c.playerItems;
		int[] backupItemsN = c.playerItemsN;
		c.playerItems = o.playerItems;
		c.playerItemsN = o.playerItemsN;
		c.getItems().resetItems(3214);
		c.playerItems = backupItems;
		c.playerItemsN = backupItemsN;
	}

	public void multiWay(int i1) {
		// synchronized(c) {
		c.outStream.createFrame(61);
		c.outStream.writeByte(i1);
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);

	}
	
	public boolean salveAmulet() {
		return (c.playerEquipment[c.playerAmulet] == 4081);
	}

	public int backupItems[] = new int[Config.BANK_SIZE];
	public int backupItemsN[] = new int[Config.BANK_SIZE];

	public void otherBank(Player c, Player o) {
		if (o == c || o == null || c == null) {
			return;
		}

		for (int i = 0; i < o.bankItems.length; i++) {
			backupItems[i] = c.bankItems[i];
			backupItemsN[i] = c.bankItemsN[i];
			c.bankItemsN[i] = o.bankItemsN[i];
			c.bankItems[i] = o.bankItems[i];
		}
		openUpBank();

		for (int i = 0; i < o.bankItems.length; i++) {
			c.bankItemsN[i] = backupItemsN[i];
			c.bankItems[i] = backupItems[i];
		}
	}
	public void sendString(final String s, final int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(126);
			c.getOutStream().writeString(s);
			c.getOutStream().writeWordA(id);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}

	}
	
	/**
	 * Changes the main displaying sprite on an interface. The index represents
	 * the location of the new sprite in the index of the sprite array.
	 * 
	 * @param componentId	the interface
	 * @param index			the index in the array
	 */
	public void sendChangeSprite(int componentId, byte index) {
		if (c == null || c.getOutStream() == null) {
			return;
		}
		Stream stream = c.getOutStream();
		stream.createFrame(7);
		stream.writeDWord(componentId);
		stream.writeByte(index);
		c.flushOutStream();
	}

	/* Treasure */
			public static int lowLevelReward[] = {
				1077, 1107, 1089, 1168, 1165, 1179, 1195, 1283, 1297, 1313, 1327, 1341, 1361,
				1367, 1426, 2633, 7362, 7368, 7366, 2583, 2585, 2587, 2589, 2591, 2593,
				2595, 2597, 7332, 7338, 7350, 2635, 2637, 7388, 7386, 7392, 7390, 7396, 7394,
				2631, 7364, 7356
		};
		public static int mediemLevelReward[] = {
				2599, 2601, 2603, 2605, 2607, 2609, 2611, 2613,
				7334, 7340, 7346, 7352, 7358, 7319, 7321, 7323, 7325, 7327, 7372,
				7370, 7380, 7378, 2645, 2647, 2649, 2577, 2579, 1073, 1091, 1099,
				1111, 1135, 1124, 1145, 1161, 1169, 1183, 1199, 1211, 1245, 1271,
				1287, 1301, 1317, 1332, 1357, 1371, 1430, 6916, 6918, 6920, 6922,
				6924, 10400, 10402, 10416, 10418, 10420, 10422, 10436, 10438,
				10446, 10448, 10450, 10452, 10454, 10456, 6889
		};
		public static int highLevelReward[] = {
				1079, 1093, 1113, 1127, 1147, 1163, 1185, 1201,
				1275, 1303, 1319, 1333, 1359, 1373, 2491, 2497, 2503, 861, 859,
				2581, 2577, 2651, 1079, 1093, 1113, 1127, 1147, 1163, 1185, 1201, 1275,
				1303, 1333, 1359, 1373, 2491, 2497, 2503, 861, 859, 2581, 2577,
				2651, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629, 2639, 2641,
				2643, 2651, 2653, 2655, 2657, 2659, 2661, 2663, 2665, 2667, 2669,
				2671, 2673, 2675, 7342, 7348,  7374, 7376, 7382,
				10330, 10338, 10348, 10332, 10340, 10346, 10334, 10342, 10350,
				10336, 10344, 10352, 10368, 10376, 10384, 10370, 10378, 10386,
				10372, 10380, 10388, 10374, 10382, 10390, 10470, 10472, 10474,
				10440, 10442, 10444, 6914,
				7384, 7398, 7399, 7400, 3481, 3483, 3485, 3486, 3488, 1079, 1093,
				1113, 1127, 1148, 1164, 1185, 1201, 1213, 1247, 1275, 1289, 1303,
				1359, 1374, 1432, 2615, 2617, 2619, 2621, 2623, 1319, 1333, 1347,
		};
		
		public static int lowLevelStacks[] = {
			995, 380, 561, 886, 
		};
		public static int mediumLevelStacks[] = {
			995, 374, 561, 563, 890,
		};
		public static int highLevelStacks[] = {
			995, 386, 561, 563, 560, 892
		};
		
		public static void addClueReward(Player c, int clueLevel) {
			int chanceReward = Misc.random(2);
			if(clueLevel == 0) {
				switch (chanceReward) {
					case 0: 
					displayReward(c, lowLevelReward[Misc.random(40)], 1, lowLevelReward[Misc.random(16)], 1, lowLevelStacks[Misc.random(3)], 1 + Misc.random(150)); 
					break;
					case 1: 
					displayReward(c, lowLevelReward[Misc.random(26)], 1, lowLevelStacks[Misc.random(3)], 1 + Misc.random(150), -1, 1); 
					break;
					case 2: 
					displayReward(c, lowLevelReward[Misc.random(40)], 1, lowLevelReward[Misc.random(16)], 1, -1, 1); 
					break;
				}
			} else if(clueLevel == 1) {
				switch (chanceReward) {
					case 0: 
					displayReward(c, mediemLevelReward[Misc.random(68)], 1, mediemLevelReward[Misc.random(13)], 1, mediumLevelStacks[Misc.random(4)], 1 + Misc.random(200));
					break;
					case 1: 
					displayReward(c, mediemLevelReward[Misc.random(46)], 1, mediumLevelStacks[Misc.random(4)], 1 + Misc.random(200), -1, 1); 
					break;
					case 2: 
					displayReward(c, mediemLevelReward[Misc.random(68)], 1, mediemLevelReward[Misc.random(13)], 1, -1, 1);
					break;
				}
			} else if(clueLevel == 2) {
				switch (chanceReward) {
					case 0: 
					displayReward(c, highLevelReward[Misc.random(135)], 1, highLevelReward[Misc.random(60)], 1, highLevelStacks[Misc.random(5)], 1 + Misc.random(350)); 
					break;
					case 1: 
					displayReward(c, highLevelReward[Misc.random(52)], 1, highLevelStacks[Misc.random(5)], 1 + Misc.random(350), -1, 1); 
					break;
					case 2: 
					displayReward(c, highLevelReward[Misc.random(135)], 1, highLevelReward[Misc.random(60)], 1, -1, 1); 
					break;
				}
			}
		}
		
		public static void displayReward(Player c, int item, int amount, int item2, int amount2, int item3, int amount3) {
			int[] items = {
				item, item2, item3
			};
			int[] amounts = {
				amount, amount2, amount3
			};
			c.outStream.createFrameVarSizeWord(53);
			c.outStream.writeWord(6963);
			c.outStream.writeWord(items.length);
			for(int i = 0; i < items.length; i++) {
				if(c.playerItemsN[i] > 254) {
					c.outStream.writeByte(255);
					c.outStream.writeDWord_v2(amounts[i]);
				} else {
					c.outStream.writeByte(amounts[i]);
				}
				if(items[i] > 0) {
					c.outStream.writeWordBigEndianA(items[i] + 1);
				} else {
					c.outStream.writeWordBigEndianA(0);
				}
			}
			c.outStream.endFrameVarSizeWord();
			c.flushOutStream();
			c.getItems().addItem(item, amount);
			c.getItems().addItem(item2, amount2);
			c.getItems().addItem(item3, amount3);
			c.getPA().showInterface(6960);
		}


	public void removeAllItems() {
		for (int i = 0; i < c.playerItems.length; i++) {
			c.playerItems[i] = 0;
		}
		for (int i = 0; i < c.playerItemsN.length; i++) {
			c.playerItemsN[i] = 0;
		}
		c.getItems().resetItems(3214);
	}

	public void setConfig(int id, int state) {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(36);
			c.getOutStream().writeWordBigEndian(id);
			c.getOutStream().writeByte(state);
			c.flushOutStream();
		}
		// }
	}

	public double getAgilityRunRestore(Player c) {
		return 2260 - (c.playerLevel[16] * 10);
	}

	public Player getClient(String playerName) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				if (p.playerName.equalsIgnoreCase(playerName)) {
					return p;
				}
			}
		}
		return null;
	}

	public void normYell(String Message) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c2 = PlayerHandler.players[j];
				c2.sendClan(Misc.optimizeText(c.playerName), Message, "OS PvP", c.getRights().getValue());
			}
		}
	}

	public void admYell(String Message) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c2 = PlayerHandler.players[j];
				c2.sendClan(Misc.optimizeText(c.playerName), Message, "Developer", c.getRights().getValue());
			}
		}
	}

	public void itemOnInterface(int interfaceChild, int zoom, int itemId) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(246);
			c.getOutStream().writeWordBigEndian(interfaceChild);
			c.getOutStream().writeWord(zoom);
			c.getOutStream().writeWord(itemId);
			c.flushOutStream();
		}
	}

	public void playerWalk(int x, int y) {
		PathFinder.getPathFinder().findRoute(c, x, y, true, 1, 1);
	}

	/**
	 * If the player is using melee and is standing diagonal from the opponent,
	 * then move towards opponent.
	 */

	public void movePlayerDiagonal(int i) {
		Player c2 = PlayerHandler.players[i];
		boolean hasMoved = false;
		int c2X = c2.getX();
		int c2Y = c2.getY();
		if (c.goodDistance(c2X, c2Y, c.getX(), c.getY(), 1)) {
			if (c.getX() != c2.getX() && c.getY() != c2.getY()) {
				if (c.getX() > c2.getX() && !hasMoved) {
					if (Region.getClipping(c.getX() - 1, c.getY(), c.heightLevel, -1, 0)) {
						hasMoved = true;
						walkTo(-1, 0);
					}
				} else if (c.getX() < c2.getX() && !hasMoved) {
					if (Region.getClipping(c.getX() + 1, c.getY(), c.heightLevel, 1, 0)) {
						hasMoved = true;
						walkTo(1, 0);
					}
				}

				if (c.getY() > c2.getY() && !hasMoved) {
					if (Region.getClipping(c.getX(), c.getY() - 1, c.heightLevel, 0, -1)) {
						hasMoved = true;
						walkTo(0, -1);
					}
				} else if (c.getY() < c2.getY() && !hasMoved) {
					if (Region.getClipping(c.getX(), c.getY() + 1, c.heightLevel, 0, 1)) {
						hasMoved = true;
						walkTo(0, 1);
					}
				}
			}
		}
		hasMoved = false;
	}

	public Clan getClan() {
		if (Server.clanManager.clanExists(c.playerName)) {
			return Server.clanManager.getClan(c.playerName);
		}
		return null;
	}

	public void sendClan(String name, String message, String clan, int rights) {
		if (rights >= 3)
			rights--;
		c.outStream.createFrameVarSizeWord(217);
		c.outStream.writeString(name);
		c.outStream.writeString(Misc.formatPlayerName(message));
		c.outStream.writeString(clan);
		c.outStream.writeWord(rights);
		c.outStream.endFrameVarSize();
	}

	public void clearClanChat() {
		c.clanId = -1;
		c.getPA().sendString("Talking in: ", 18139);
		c.getPA().sendString("Owner: ", 18140);
		for (int j = 18144; j < 18244; j++) {
			c.getPA().sendString("", j);
		}
	}

	public void setClanData() {
		boolean exists = Server.clanManager.clanExists(c.playerName);
		if (!exists || c.clan == null) {
			sendString("Join chat", 18135);
			sendString("Talking in: Not in chat", 18139);
			sendString("Owner: None", 18140);
		}
		if (!exists) {
			sendString("Chat Disabled", 18306);
			String title = "";
			for (int id = 18307; id < 18317; id += 3) {
				if (id == 18307) {
					title = "Anyone";
				} else if (id == 18310) {
					title = "Anyone";
				} else if (id == 18313) {
					title = "General+";
				} else if (id == 18316) {
					title = "Only Me";
				}
				sendString(title, id + 2);
			}
			for (int index = 0; index < 100; index++) {
				sendString("", 18323 + index);
			}
			for (int index = 0; index < 100; index++) {
				sendString("", 18424 + index);
			}
			return;
		}
		Clan clan = Server.clanManager.getClan(c.playerName);
		sendString(clan.getTitle(), 18306);
		String title = "";
		for (int id = 18307; id < 18317; id += 3) {
			if (id == 18307) {
				title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 18310) {
				title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 18313) {
				title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 18316) {
				title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
			}
			sendString(title, id + 2);
		}
		if (clan.rankedMembers != null) {
			for (int index = 0; index < 100; index++) {
				if (index < clan.rankedMembers.size()) {
					sendString("<clan=" + clan.ranks.get(index) + ">" + clan.rankedMembers.get(index), 18323 + index);
				} else {
					sendString("", 18323 + index);
				}
			}
		}
		if (clan.bannedMembers != null) {
			for (int index = 0; index < 100; index++) {
				if (index < clan.bannedMembers.size()) {
					sendString(clan.bannedMembers.get(index), 18424 + index);
				} else {
					sendString("", 18424 + index);
				}
			}
		}
	}

	public void resetAutocast() {
		c.autocastId = -1;
		c.autocasting = false;
		c.setSidebarInterface(0, 328);
		c.getPA().sendFrame36(108, 0);
		c.getItems().sendWeapon(c.playerEquipment[c.playerWeapon], ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]));
	}

	public int getItemSlot(int itemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == itemID) {
				return i;
			}
		}
		return -1;
	}

	public boolean isItemInBag(int itemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == itemID) {
				return true;
			}
		}
		return false;
	}

	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public void turnTo(int pointX, int pointY) {
		c.focusPointX = 2 * pointX + 1;
		c.focusPointY = 2 * pointY + 1;
		c.updateRequired = true;
	}

	public void movePlayer(int x, int y, int h) {
		if (c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
			if (!c.getRights().isStaff()) {
				return;
			}
		}
		   if (!c.lastSpear.elapsed(4000)) {
	            c.sendMessage("You're trying to move too fast.");
				return;
			}
		c.resetWalkingQueue();
		c.teleportToX = x;
		c.teleportToY = y;
		c.heightLevel = h;
		requestUpdates();
		c.lastMove = System.currentTimeMillis();
	}

	public void movePlayerDuel(int x, int y, int h) {
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION
				&& Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			// c.getPA().removeAllWindows();
			return;
		}
		c.resetWalkingQueue();
		c.teleportToX = x;
		c.teleportToY = y;
		c.heightLevel = h;
		requestUpdates();
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	public int absX, absY;
	public int heightLevel;

	public static void QuestReward(Player c, String questName, int PointsGain, String Line1, String Line2, String Line3, String Line4, String Line5,
			String Line6, int itemID) {
		c.getPA().sendFrame126("You have completed " + questName + "!", 12144);
		sendQuest(c, "" + (c.QuestPoints), 12147);
		// c.QuestPoints += PointsGain;
		sendQuest(c, Line1, 12150);
		sendQuest(c, Line2, 12151);
		sendQuest(c, Line3, 12152);
		sendQuest(c, Line4, 12153);
		sendQuest(c, Line5, 12154);
		sendQuest(c, Line6, 12155);
		c.getPlayerAssistant().sendFrame246(12145, 250, itemID);
		c.getPA().showInterface(12140);
		Server.getStillGraphicsManager().stillGraphics(c, c.getX(), c.getY(), c.getHeightLevel(), 199, 0);
	}

	public static void sendQuest(Player client, String s, int i) {
		client.getOutStream().createFrameVarSizeWord(126);
		client.getOutStream().writeString(s);
		client.getOutStream().writeWordA(i);
		client.getOutStream().endFrameVarSizeWord();
		client.flushOutStream();
	}

	public void sendStillGraphics(int id, int heightS, int y, int x, int timeBCS) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC(y - (c.mapRegionY * 8));
		c.getOutStream().writeByteC(x - (c.mapRegionX * 8));
		c.getOutStream().createFrame(4);
		c.getOutStream().writeByte(0);// Tiles away (X >> 4 + Y & 7)
										// //Tiles away from
		// absX and absY.
		c.getOutStream().writeWord(id); // Graphic ID.
		c.getOutStream().writeByte(heightS); // Height of the graphic when
												// cast.
		c.getOutStream().writeWord(timeBCS); // Time before the graphic
												// plays.
		c.flushOutStream();
	}

	public void createArrow(int type, int id) {
		if (c != null) {
			c.getOutStream().createFrame(254); // The packet ID
			c.getOutStream().writeByte(type); // 1=NPC, 10=Player
			c.getOutStream().writeWord(id); // NPC/Player ID
			c.getOutStream().write3Byte(0); // Junk
		}
	}

	public void createArrow(int x, int y, int height, int pos) {
		if (c != null) {
			c.getOutStream().createFrame(254); // The packet ID
			c.getOutStream().writeByte(pos); // Position on Square(2 = middle, 3
												// = west, 4 = east, 5 = south,
												// 6 = north)
			c.getOutStream().writeWord(x); // X-Coord of Object
			c.getOutStream().writeWord(y); // Y-Coord of Object
			c.getOutStream().writeByte(height); // Height off Ground
		}
	}

	public void loadQuests() {
		sendFrame126("Players Online: @gre@"+ PlayerHandler.getPlayerCount() +"", 29155);
		sendFrame126("@gre@ My Statistics:", 29161);
		if (c.getRights().getValue() == 0)
		sendFrame126("@or1@Rank: Player", 29162);
		if (c.getRights().getValue() == 1)
		sendFrame126("@or1@Rank: <col=148200>Moderator @cr1@", 29162);
		if (c.getRights().getValue() == 2 || c.getRights().getValue() == 3) 
		sendFrame126("@or1@Rank: <col=A67711>Owner @cr2@", 29162);
		if (c.getRights().getValue() == 5)
		sendFrame126("@or1@Rank: @red@Donator@cr4@", 29162);
		if (c.getRights().getValue() == 6)
		sendFrame126("@or1@Rank: @blu@Sponsor@cr5@", 29162);
		if (c.getRights().getValue() == 7)
		sendFrame126("@or1@Rank: <col=FF00CD>V.I.P @cr6@", 29162);
		sendFrame126("@or1@PK Points: @gre@"+c.pkp, 29163);
		sendFrame126("@or1@Kills/Deaths: @gre@"+ c.KC +"@or1@/@gre@" + c.DC, 29164);
		sendFrame126("@or1@Current Killstreak: @gre@"+ c.killStreak, 29165);
		sendFrame126("@or1@Account Info", 29166);
		/*sendFrame126("@or1@Pk Points: @gre@" + c.pkp, 29162);
		if (c.KC == 0 && c.DC == 0) {
			sendFrame126("@or1@KDR:@red@ " + c.KC + "@or1@/@red@" + c.DC, 29163);
		} else {
			sendFrame126("@or1@KDR:@gre@ " + c.KC + "@yel@/@gre@" + c.DC, 29163);
		}
		sendFrame126("@or1@Donator Points: @gre@" + c.donPoints, 29164);
		if (c.achievementsCompleted == 0) {
		sendFrame126("@or1@Achievements Completed: @red@0", 29165);
		} else {
			sendFrame126("@or1@Achievements Completed: @gre@"+c.achievementsCompleted, 29165);
		}
		if (c.pcPoints == 0) {
			sendFrame126("@or1@Pest-Control Points: @red@" + c.pcPoints + "",
					29166);
		} else {
			sendFrame126("@or1@Pest-Control Points: @gre@" + c.pcPoints + "",
					29166);
		}*/
		sendFrame126("@gre@ Quick-spawn Sets:", 663);
		sendFrame126("@or1@Food", 29167);
		sendFrame126("@or1@Pots", 29168);
		sendFrame126("@or1@Runes", 29169);
		sendFrame126("@or1@Hybrid Set", 29170);
		sendFrame126("@or1@Melee Set", 29171);
		sendFrame126("@or1@Pure Set", 29172);
		sendFrame126("@or1@Range Set", 29173);
		/*sendFrame126("@or1@Slayer Points: @gre@" + c.slayerPoints + " ", 29167);
		if (c.slayerTask <= 0) {
			c.getPA().sendFrame126("@or1@Task: @gre@Empty ", 29168);
		} else {
			c.getPA().sendFrame126(
					"@or1@Task: @gre@" + c.taskAmount + " "
							+ Server.npcHandler.getNpcListName(c.slayerTask)
							+ " ", 29168);
		}*/
	
	}

	public void sendFrame126(String s, int id) {
		if (!c.checkPacket126Update(s, id)) {
			return;
		}
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(126);
			c.getOutStream().writeString(s);
			c.getOutStream().writeWordA(id);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}

	}

	public void sendLink(String s) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(187);
			c.getOutStream().writeString(s);
		}
	}

	public void setSkillLevel(int skillNum, int currentLevel, int XP) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(134);
			c.getOutStream().writeByte(skillNum);
			c.getOutStream().writeDWord_v1(XP);
			c.getOutStream().writeByte(currentLevel);
			c.flushOutStream();
		}
	}

	public void sendFrame106(int sideIcon) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(106);
			c.getOutStream().writeByteC(sideIcon);
			c.flushOutStream();
			requestUpdates();
		}
	}

	public void sendFrame107() {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(107);
			c.flushOutStream();
		}
	}

	public void sendFrame36(int id, int state) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(36);
			c.getOutStream().writeWordBigEndian(id);
			c.getOutStream().writeByte(state);
			c.flushOutStream();
		}

	}

	public void sendFrame185(int Frame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(185);
			c.getOutStream().writeWordBigEndianA(Frame);
		}

	}

	public void showInterface(int interfaceid) {
		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if (c.getOutStream() != null && c != null) {
			c.setInterfaceOpen(interfaceid);
			c.getOutStream().createFrame(97);
			c.getOutStream().writeWord(interfaceid);
			c.flushOutStream();
		}
	}

	public void sendFrame248(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();

		}
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(246);
			c.getOutStream().writeWordBigEndian(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.getOutStream().writeWord(SubFrame2);
			c.flushOutStream();
		}
	}

	public void sendFrame171(int state, int componentId) {
		if (c.getPacketDropper().requiresUpdate(171, new ComponentVisibility(state, componentId))) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(171);
				c.getOutStream().writeByte(state);
				c.getOutStream().writeWord(componentId);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame200(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(200);
			c.getOutStream().writeWord(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame70(int i, int o, int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(70);
			c.getOutStream().writeWord(i);
			c.getOutStream().writeWordBigEndian(o);
			c.getOutStream().writeWordBigEndian(id);
			c.flushOutStream();
		}

	}

	public void sendFrame75(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(75);
			c.getOutStream().writeWordBigEndianA(MainFrame);
			c.getOutStream().writeWordBigEndianA(SubFrame);
			c.flushOutStream();
		}

	}

	public void sendFrame164(int Frame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(164);
			c.getOutStream().writeWordBigEndian_dup(Frame);
			c.flushOutStream();
		}

	}

	public void sendFrame87(int id, int state) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(87);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.getOutStream().writeDWord_v1(state);
			c.flushOutStream();
		}

	}

	public void sendPM(long name, int rights, byte[] chatMessage) {
		c.getOutStream().createFrameVarSize(196);
		c.getOutStream().writeQWord(name);
		c.getOutStream().writeDWord(new Random().nextInt());
		c.getOutStream().writeByte(rights);
		c.getOutStream().writeBytes(chatMessage, chatMessage.length, 0);
		c.getOutStream().endFrameVarSize();

	}

	public void createPlayerHints(int type, int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(type);
			c.getOutStream().writeWord(id);
			c.getOutStream().write3Byte(0);
			c.flushOutStream();
		}

	}

	public void createObjectHints(int x, int y, int height, int pos) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(pos);
			c.getOutStream().writeWord(x);
			c.getOutStream().writeWord(y);
			c.getOutStream().writeByte(height);
			c.flushOutStream();
		}

	}

	public void sendFriend(long friend, int world) {
		c.getOutStream().createFrame(50);
		c.getOutStream().writeQWord(friend);
		c.getOutStream().writeByte(world);

	}

	public void removeAllWindows() {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getPA().resetVariables();
			c.getOutStream().createFrame(219);
			c.flushOutStream();
		}
		c.dialogue().interrupt();
		resetVariables();
	}

	public void closeAllWindows() {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(219);
			c.flushOutStream();
			c.isBanking = false;
			resetVariables();
		}
	}

	public void sendFrame34(int id, int slot, int column, int amount) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.outStream.createFrameVarSizeWord(34); // init item to smith screen
			c.outStream.writeWord(column); // Column Across Smith Screen
			c.outStream.writeByte(4); // Total Rows?
			c.outStream.writeDWord(slot); // Row Down The Smith Screen
			c.outStream.writeWord(id + 1); // item
			c.outStream.writeByte(amount); // how many there are?
			c.outStream.endFrameVarSizeWord();
		}

	}

	public void walkableInterface(int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(208);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.flushOutStream();
		}

	}
	
    public void shakeScreen(int verticleAmount, int verticleSpeed, int horizontalAmount, int horizontalSpeed) {
        if (c != null && c.getOutStream() != null) {
	    	c.outStream.createFrame(35);
	        c.outStream.writeByte(verticleAmount);
	        c.outStream.writeByte(verticleSpeed);
	        c.outStream.writeByte(horizontalAmount);
	        c.outStream.writeByte(horizontalSpeed);
        }
    }
    
    public void resetShaking() {
        shakeScreen(1, 1, 1, 1);
    }

	public int mapStatus = 0;

	public void sendFrame99(int state) { // used for disabling map
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (mapStatus != state) {
				mapStatus = state;
				c.getOutStream().createFrame(99);
				c.getOutStream().writeByte(state);
				c.flushOutStream();
			}

		}
	}

	public void sendCrashFrame() {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(123);
			c.flushOutStream();
		}
	}

	/**
	 * Reseting animations for everyone
	 **/

	public void frame1() {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Player person = PlayerHandler.players[i];
				if (person != null) {
					if (person.getOutStream() != null && !person.disconnected) {
						if (c.distanceToPoint(person.getX(), person.getY()) <= 25) {
							person.getOutStream().createFrame(1);
							person.flushOutStream();
							person.getPA().requestUpdates();
						}
					}
				}

			}
		}
	}

	/**
	 * Creating projectile
	 **/
	public void createProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon,
			int time) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(16);
			c.getOutStream().writeByte(64);
			c.flushOutStream();

		}
	}

	public void createProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon,
			int time, int slope) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(slope);
			c.getOutStream().writeByte(64);
			c.flushOutStream();
		}

	}

	public void createProjectile3(int casterY, int casterX, int offsetY, int offsetX, int gfxMoving, int StartHeight, int endHeight, int speed,
			int AtkIndex) {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Player p = PlayerHandler.players[i];
				if (p.WithinDistance(c.absX, c.absY, p.absX, p.absY, 60)) {
					if (p.heightLevel == c.heightLevel) {
						if (PlayerHandler.players[i] != null && !PlayerHandler.players[i].disconnected) {
							p.outStream.createFrame(85);
							p.outStream.writeByteC((casterY - (p.mapRegionY * 8)) - 2);
							p.outStream.writeByteC((casterX - (p.mapRegionX * 8)) - 3);
							p.outStream.createFrame(117);
							p.outStream.writeByte(50);
							p.outStream.writeByte(offsetY);
							p.outStream.writeByte(offsetX);
							p.outStream.writeWord(AtkIndex);
							p.outStream.writeWord(gfxMoving);
							p.outStream.writeByte(StartHeight);
							p.outStream.writeByte(endHeight);
							p.outStream.writeWord(51);
							p.outStream.writeWord(speed);
							p.outStream.writeByte(16);
							p.outStream.writeByte(64);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = p;
				if (person.getOutStream() != null) {
					if (person.distanceToPoint(x, y) <= 25) {
						if (p.heightLevel == c.heightLevel)
							person.getPA().createProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time);
					}
				}
			}
		}
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope) {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = p;
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25 && c.heightLevel == p.heightLevel) {
							person.getPA().createProjectile2(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time, slope);
						}
					}
			}

		}
	}

	/**
	 ** GFX
	 **/
	public void stillGfx(int id, int x, int y, int height, int time) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(y - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(x - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(4);
			c.getOutStream().writeByte(0);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeByte(height);
			c.getOutStream().writeWord(time);
			c.flushOutStream();
		}

	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = p;
				if (person.getOutStream() != null) {
					if (person.distanceToPoint(x, y) <= 25) {
						person.getPA().stillGfx(id, x, y, height, time);
					}
				}
			}
		}
	}

	/**
	 * Objects, add and remove
	 **/
	public void object(int objectId, int objectX, int objectY, int face, int objectType) {
		Region.addWorldObject(objectId, objectX, objectY, c.heightLevel, face); 
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(101);
			c.getOutStream().writeByteC((objectType << 2) + (face & 3));
			c.getOutStream().writeByte(0);

			if (objectId != -1) { // removing
				c.getOutStream().createFrame(151);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			c.flushOutStream();
		}

	}

	public void checkObjectSpawn(int objectId, int objectX, int objectY, int face, int objectType) {
		Region.addWorldObject(objectId, objectX, objectY, c.heightLevel, face); // height
																				// level
																				// should
																				// be
																				// a
																				// param
																				// :s
		if (c.distanceToPoint(objectX, objectY) > 60)
			return;
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
			c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
			c.getOutStream().createFrame(101);
			c.getOutStream().writeByteC((objectType << 2) + (face & 3));
			c.getOutStream().writeByte(0);

			if (objectId != -1) { // removing
				c.getOutStream().createFrame(151);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			c.flushOutStream();
		}

	}

	/**
	 * Show option, attack, trade, follow etc
	 **/
	public String optionType = "null";

	public void showOption(int i, int l, String s, int a) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (!optionType.equalsIgnoreCase(s)) {
				optionType = s;
				c.getOutStream().createFrameVarSize(104);
				c.getOutStream().writeByteC(i);
				c.getOutStream().writeByteA(l);
				c.getOutStream().writeString(s);
				c.getOutStream().endFrameVarSize();
				c.flushOutStream();
			}

		}
	}

	/**
	 * Open bank
	 **/
	public void sendFrame34a(int frame, int item, int slot, int amount) {
		c.outStream.createFrameVarSizeWord(34);
		c.outStream.writeWord(frame);
		c.outStream.writeByte(slot);
		c.outStream.writeWord(item + 1);
		c.outStream.writeByte(255);
		c.outStream.writeDWord(amount);
		c.outStream.endFrameVarSizeWord();
	}

	public void openUpBank() {
		resetVariables();
		if (c.getBankPin().isLocked() && c.getBankPin().getPin().trim().length() > 0) {
			c.getBankPin().open(2);
			c.isBanking = false;
			return;
		}
		if (c.takeAsNote)
			sendFrame36(115, 1);
		else
			sendFrame36(115, 0);

		if (c.inWild() && !(c.getRights().isBetween(2, 3))) {
			c.sendMessage("You can't bank in the wilderness!");
			return;
		}
		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			} else {
				c.sendMessage("You cannot bank whilst dueling.");
				return;
			}
		}
		if (c.getBank().getBankSearch().isSearching()) {
			c.getBank().getBankSearch().reset();
		}
		c.getPA().sendFrame126("Search", 58063);
		if (c.getOutStream() != null && c != null) {
			c.isBanking = true;
			c.getItems().resetItems(5064);
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(5292);// ok perfect
			c.getOutStream().writeWord(5063);
			c.flushOutStream();
		}
	}

	public boolean viewingOtherBank;
	BankTab[] backupTabs = new BankTab[9];

	public void resetOtherBank() {
		for (int i = 0; i < backupTabs.length; i++)
			c.getBank().setBankTab(i, backupTabs[i]);
		viewingOtherBank = false;
		removeAllWindows();
		c.isBanking = false;
		c.getBank().setCurrentBankTab(c.getBank().getBankTab()[0]);
		c.getItems().resetBank();
		c.sendMessage("You are no longer viewing another players bank.");
	}

	public void openOtherBank(Player otherPlayer) {
		if (otherPlayer == null)
			return;

		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
			return;
		}
		if (otherPlayer.getPA().viewingOtherBank) {
			c.sendMessage("That player is viewing another players bank, please wait.");
			return;
		}
		for (int i = 0; i < backupTabs.length; i++)
			backupTabs[i] = c.getBank().getBankTab(i);
		for (int i = 0; i < otherPlayer.getBank().getBankTab().length; i++)
			c.getBank().setBankTab(i, otherPlayer.getBank().getBankTab(i));
		c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
		viewingOtherBank = true;
		openUpBank();
	}

	public void potionPoisonHeal(int itemId, int itemSlot, int newItemId, int healType) {

		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (!Objects.isNull(session)) {
			if (session.getRules().contains(Rule.NO_DRINKS)) {
				c.sendMessage("Drinks have been disabled for this duel.");
				return;
			}
		}
		if (!c.isDead && c.foodDelay.elapsed(2000)) {
			if (c.getItems().playerHasItem(itemId, 1, itemSlot)) {
				c.sendMessage("You drink the " + ItemAssistant.getItemName(itemId).toLowerCase() + ".");
				c.foodDelay.reset();
				// Actions
				if (healType == 1) {
					// Cures The Poison
				} else if (healType == 2) {
					// Cures The Poison + protects from getting poison again
				}
				c.startAnimation(0x33D);
				c.getItems().deleteItem(itemId, itemSlot, 1);
				c.getItems().addItem(newItemId, 1);
				requestUpdates();
			}
		}
	}

	/**
	 * Magic on items
	 **/

	public void magicOnItems(int slot, int itemId, int spellId) {

		switch (spellId) {
		case 1162: // low alch
			/*if (System.currentTimeMillis() - c.alchDelay > 1000) {
				
				 if (!c.getCombat().checkMagicReqs(49)) { break; } if
				  (!c.getItems().playerHasItem(itemId, 1, slot) || itemId ==
				  995) { return; } c.getItems().deleteItem(itemId, slot, 1);
				  c.getItems().addItem(995,
				  c.getShops().getItemShopValue(itemId) / 3);
				 c.startAnimation(c.MAGIC_SPELLS[49][2]);
				  c.gfx100(c.MAGIC_SPELLS[49][3]); c.alchDelay =
				  System.currentTimeMillis(); sendFrame106(6);
				 addSkillXP(c.MAGIC_SPELLS[49][7] * Config.MAGIC_EXP_RATE, 6);
				  refreshSkill(6);
				 
			}*/
			break;

		case 1178: // high alch
		/*	if (System.currentTimeMillis() - c.alchDelay > 2000) {
				
				  if (!c.getCombat().checkMagicReqs(50)) { break; } if
				  (!c.getItems().playerHasItem(itemId, 1, slot) || itemId ==
				  995) { return; } c.getItems().deleteItem(itemId, slot, 1);
				  c.getItems().addItem(995, (int)
				  (c.getShops().getItemShopValue(itemId) * .75));
				  c.startAnimation(c.MAGIC_SPELLS[50][2]);
				  c.gfx100(c.MAGIC_SPELLS[50][3]); c.alchDelay =
				  System.currentTimeMillis(); sendFrame106(6);
				  addSkillXP(c.MAGIC_SPELLS[50][7] * Config.MAGIC_EXP_RATE, 6);
				  refreshSkill(6);
				 
			}*/
			break;
			
		case 1155: //Lvl-1 enchant sapphire
		case 1165: //Lvl-2 enchant emerald
		case 1176: //Lvl-3 enchant ruby
		case 1180: //Lvl-4 enchant diamond
		case 1187: //Lvl-5 enchant dragonstone
		case 6003: //Lvl-6 enchant onyx
			enchantBolt(spellId, itemId, 28);
		break;
		}
	}
	public int[][] boltData = {
			{1155, 879, 9, 9236}, {1155, 9337, 17, 9240}, {1165, 9335, 19, 9237}, {1165, 880, 29, 9238},
			{1165, 9338, 37, 9241}, {1176, 9336, 39, 9239}, {1176, 9339, 59, 9242}, {1180, 9340, 67, 9243},
			{1187, 9341, 78, 9244}, {6003, 9342, 97, 9245}
		};
		
		public int[][] runeData = {
				{1155, 555, 1, -1, -1}, {1165, 556, 3, -1, -1}, {1176, 554, 5, -1, -1}, 
				{1180, 557, 10, -1, -1}, {1187, 555, 15, 557, 15}, {6003, 554, 20, 557, 20}
		};

		public void enchantBolt(int spell, int bolt, int amount) {
			for(int i = 0; i < boltData.length; i++) {
				if(spell == boltData[i][0]) {
					if(bolt == boltData[i][1]) {
						for(int a = 0; a < runeData.length; a++) {
							if(spell == runeData[a][0]) {
								if(!c.getItems().playerHasItem(564, 1) || !c.getItems().playerHasItem(runeData[a][1], runeData[a][2]) || (!c.getItems().playerHasItem(runeData[a][3], runeData[a][4]) && runeData[a][3] > 0)){
									c.sendMessage("You do not have the required runes to cast this spell!");
									return;
								}
								c.getItems().deleteItem(564, c.getItems().getItemSlot(564), 1);
								c.getItems().deleteItem(runeData[a][1], c.getItems().getItemSlot(runeData[a][1]), runeData[a][2]);
								if(runeData[a][3] > 0)
									c.getItems().deleteItem(runeData[a][3], c.getItems().getItemSlot(runeData[a][3]), runeData[a][4]);
							}
						}
						if(!c.getItems().playerHasItem(boltData[i][1], amount))
							amount = c.getItems().getItemAmount(bolt);
						c.getItems().deleteItem(boltData[i][1], c.getItems().getItemSlot(boltData[i][1]), amount);
						c.getPA().addSkillXP(boltData[i][2] * amount, 6);
						c.getItems().addItem(boltData[i][3], amount);
						c.getPA().sendFrame106(6);
						return;
					}
				}
			}
		}
	/**
	 * Dieing
	 **/
	public void yell(String msg) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c2 = PlayerHandler.players[j];
				c2.sendMessage(msg);
			}
		}
	}

	public void applyDead() {
		if (c.getItems().playerHasItem(12926) || c.getItems().isWearingItem(12926)) {
			c.setToxicBlowpipeCharge(0);
			c.setToxicBlowpipeAmmo(0);
			c.setToxicBlowpipeAmmoAmount(0);
			c.sendMessage("<col=255>You have lost your blow pipes charges & ammo!");
		}		
		if (c.getItems().playerHasItem(12931) || c.getItems().isWearingItem(12931)) {
			c.setSerpentineHelmCharge(0);
			c.sendMessage("<col=255>You have lost your helms charges!");
		}
		if (c.getItems().playerHasItem(12904) || c.getItems().isWearingItem(12904)) {
			c.setToxicStaffOfDeadCharge(0);
			c.sendMessage("<col=255>You have lost your staffs charges!");
		}
		c.isFullHelm = Item.isFullHelm(c.playerEquipment[c.playerHat]);
		c.isFullMask = Item.isFullMask(c.playerEquipment[c.playerHat]);
		c.isFullBody = Item.isFullBody(c.playerEquipment[c.playerChest]);
		c.getPA().requestUpdates();
		c.respawnTimer = 15;
		c.isDead = false;
		c.freezeTimer = 1;
		c.recoilHits = 0;
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			duelSession = null;
		}
		if (Objects.isNull(duelSession)) {
			c.setKiller(c.getPlayerKiller());
			Player o = PlayerHandler.getPlayer(c.getKiller());
			if (c.getKiller() != null && o != null && !c.isKilledByZombie()) {
				c.killerId = o.playerId;
				if (c.killerId != c.playerId)
					if (c.inWild() || c.inCamWild()) {
						if (HolidayController.HALLOWEEN.isActive()) {
							CycleEventHandler.getSingleton().addEvent(c, new HalloweenDeathCycleEvent(c, o), 6);
						}
						if (!o.getPlayerKills().killedRecently(c.connectedFrom) && !o.getMacAddress().equals(c.getMacAddress())) {
							o.getPlayerKills().add(c.connectedFrom);
							Achievements.increase(o, AchievementType.KILL_PLAYER, 1);
							c.DC++;
							o.KC++;
							c.getLogs().playerKills();
							if (Config.BOUNTY_HUNTER_ACTIVE) {
								c.getBH().dropPlayerEmblem(o);
								if (c.getBH().hasTarget() && c.getBH().getTarget().getName().equalsIgnoreCase(o.playerName) && o.getBH().hasTarget()
										&& o.getBH().getTarget().getName().equalsIgnoreCase(c.playerName)) {
									o.getBH().setCurrentHunterKills(o.getBH().getCurrentHunterKills() + 1);
									if (o.getBH().getCurrentHunterKills() > o.getBH().getRecordHunterKills()) {
										o.getBH().setRecordHunterKills(o.getBH().getCurrentHunterKills());
									}
									o.getKillstreak().increase(Killstreak.Type.HUNTER);
									o.getBH().upgradePlayerEmblem();
									o.getBH().setTotalHunterKills(o.getBH().getTotalHunterKills() + 1);
									o.getBH().removeTarget();
									c.getBH().removeTarget();
									o.getBH().setTargetState(TargetState.RECENT_TARGET_KILL);
									o.sendMessage("<col=255>You have killed your target: " + c.playerName + ".");
								} else {
									o.getKillstreak().increase(Killstreak.Type.ROGUE);
									o.getBH().setCurrentRogueKills(o.getBH().getCurrentRogueKills() + 1);
									o.getBH().setTotalRogueKills(o.getBH().getTotalRogueKills() + 1);
									if (o.getBH().getCurrentRogueKills() > o.getBH().getRecordRogueKills()) {
										o.getBH().setRecordRogueKills(o.getBH().getCurrentRogueKills());
									}
								}
								o.getBH().updateStatisticsUI();
								o.getBH().updateTargetUI();
							}
							int opponentKillstreak = c.getKillstreak().getAmount(Killstreak.Type.ROGUE);
							if (opponentKillstreak > 1) {
								o.sendMessage("You receive an additional 5 PK tickets, your opponent had a killstreak of " + opponentKillstreak + ".");
								PlayerHandler.executeGlobalMessage("<col=CC0000>" + WordUtils.capitalize(o.playerName) + "</col><col=255>"
										+ " has ended </col><col=CC0000>" + WordUtils.capitalize(c.playerName) + "</col><col=255>'s"
										+ " rogue killstreak of " + opponentKillstreak + ".");
								PlayerHandler.executeGlobalMessage("<col=255>They have been awarded " + opponentKillstreak + " pk tickets.");
								o.getItems().addItemUnderAnyCircumstance(2996, opponentKillstreak);
							}
							opponentKillstreak = c.getKillstreak().getAmount(Killstreak.Type.HUNTER);
							if (opponentKillstreak > 1) {
								o.sendMessage("You receive an additional 5 PK tickets, your opponent had a killstreak of " + opponentKillstreak + ".");
								PlayerHandler.executeGlobalMessage("<col=CC0000>" + WordUtils.capitalize(o.playerName) + "</col><col=255>"
										+ " has ended </col><col=CC0000>" + WordUtils.capitalize(c.playerName) + "</col><col=255>'s"
										+ " hunter killstreak of " + opponentKillstreak + ".");
								o.getItems().addItemUnderAnyCircumstance(2996, 5);
							}
							if (o.getRights().getValue() == 5) {
								o.pkp += 1;
							}
							if (o.getRights().getValue() == 6) {
								o.pkp += 1;
							}
							if (o.getRights().getValue() == 7) {
								o.pkp += 2;
							}
							if (o.getRights().getValue() == 8) {
								o.pkp += 2;
							}
							if (o.getRights().getValue() == 9) {
								o.pkp += 3;
							}
							o.pkp += Config.BONUS_WEEKEND ? 5 : 3;
							c.getKillstreak().resetAll();
							c.getItems().dropPVP();
							c.getPA().loadQuests();
							o.getPA().loadQuests();
						} else {
							o.sendMessage("You do not get any PK Points as you have recently defeated @blu@" + Misc.optimizeText(c.playerName)
									+ "@bla@.");
							// return;
						}
					}
				c.playerKilled = c.playerId;
				PlayerSave.saveGame(c);
				PlayerSave.saveGame(o);
			}
			c.sendMessage("Oh dear you are dead!");
			c.setPoisonDamage((byte) 0);
		}
		if (Config.BOUNTY_HUNTER_ACTIVE) {
			c.getBH().setCurrentHunterKills(0);
			c.getBH().setCurrentRogueKills(0);
			c.getBH().updateStatisticsUI();
			c.getBH().updateTargetUI();
		}
		c.faceUpdate(0);
		c.stopMovement();
		if (duelSession != null && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION) {
			if (!duelSession.getWinner().isPresent()) {
				c.sendMessage("You have lost the duel!");
				Player opponent = duelSession.getOther(c);
				opponent.logoutDelay.reset();
				if (!duelSession.getWinner().isPresent()) {
					duelSession.setWinner(opponent);
				}
				PlayerSave.saveGame(opponent);
			} else {
				c.sendMessage("Congratulations, you have won the duel.");
			}
			c.logoutDelay.reset();
		}
		PlayerSave.saveGame(c);
		c.specAmount = 10;
		c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
		c.lastVeng.reset();
		c.startAnimation(2304);
		c.vengOn = false;
		resetFollowers();
		c.attackTimer = 10;
		c.tradeResetNeeded = true;
		c.doubleHit = false;
		removeAllWindows();
		closeAllWindows();
	}

	public void resetTb() {
		c.teleBlockLength = 0;
		c.teleBlockDelay = 0;
	}

	public void resetFollowers() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].followId == c.playerId) {
					Player c = PlayerHandler.players[j];
					c.getPA().resetFollow();
				}
			}
		}
	}

	public void giveLife() {
		c.isFullHelm = Item.isFullHelm(c.playerEquipment[c.playerHat]);
		c.isFullMask = Item.isFullMask(c.playerEquipment[c.playerHat]);
		c.isFullBody = Item.isFullBody(c.playerEquipment[c.playerChest]);
		c.isDead = false;
		c.faceUpdate(-1);
		c.freezeTimer = 1;
		c.getPA().sendFrame126("freezetimer:-2", 1810);
		if (!c.inDuelArena() && !c.inClanWars() && !Boundary.isIn(c, Boundary.DUEL_ARENAS) && !Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			if (!Boundary.isIn(c, PestControl.GAME_BOUNDARY) && !c.inSafemode() && !c.isKilledByZombie() && !Boundary.isIn(c, Zulrah.BOUNDARY)) {
				for (int itemId : Config.DROP_AND_DELETE_ON_DEATH) {
					if (c.getItems().isWearingItem(itemId)) {
						int slot = c.getItems().getItemSlot(itemId);
						if (slot != -1) {
							c.getItems().removeItem(itemId, slot);
						}
					}
					if (c.getItems().playerHasItem(itemId)) {
						c.getItems().deleteItem2(itemId, c.getItems().getItemAmount(itemId));
					}
				}
				c.getItems().resetKeepItems();
				if (!c.isSkulled) { // what items to keep
					c.getItems().keepItem(0, true);
					c.getItems().keepItem(1, true);
					c.getItems().keepItem(2, true);
				}
				if (c.prayerActive[10] && c.lastProtItem.elapsed(700)) {
					c.getItems().keepItem(3, true);
				}
				for (int item = 0; item < Config.ITEMS_KEPT_ON_DEATH.length; item++) {
					int itemId = Config.ITEMS_KEPT_ON_DEATH[item];
					int itemAmount = c.getItems().getItemAmount(itemId) + c.getItems().getWornItemAmount(itemId);
					if (c.getItems().playerHasItem(itemId) || c.getItems().isWearingItem(itemId)) {
						c.getItems().sendItemToAnyTab(itemId, itemAmount);
					}
				}
				c.getItems().dropAllItems(); // drop all items
				c.getItems().deleteAllItems(); // delete all items

				if (!c.isSkulled) { // add the kept items once we finish
									// deleting and dropping them
					for (int i1 = 0; i1 < 3; i1++) {
						if (c.itemKeptId[i1] > 0) {
							c.getItems().addItem(c.itemKeptId[i1], 1);
						}
					}
				}
				if (c.prayerActive[10]) {
					if (c.itemKeptId[3] > 0) {
						c.getItems().addItem(c.itemKeptId[3], 1);
					}
				}
				c.getItems().resetKeepItems();
				/*Player killer = PlayerHandler.getPlayer(c.getKiller());
				if (c.getItems().isWearingItem(12931) && c.getItems().getWornItemSlot(12931) == c.playerHat
						|| c.getItems().playerHasItem(12931)) {
					if (c.getSerpentineHelmCharge() > 0) {
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12934, c.getX(), c.getY(), c.heightLevel,
								c.getSerpentineHelmCharge(), killer == null ? c.playerId : killer.playerId);
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12929, c.getX(), c.getY(), c.heightLevel,
								1, killer == null ? c.playerId : killer.playerId);
						if (c.getItems().isWearingItem(12931) && c.getItems().getWornItemSlot(12931) == c.playerHat) {
							c.getItems().wearItem(-1, 0, c.playerHat);
						} else {
							c.getItems().deleteItem2(12931, 1);
						}
						c.sendMessage("The serpentine helm has been dropped on the floor. You lost the helm and it's charge.");
						c.setSerpentineHelmCharge(0);
					}
				}
				if (c.getItems().isWearingItem(12904) && c.getItems().getWornItemSlot(12904) == c.playerWeapon
						|| c.getItems().playerHasItem(12904)) {
					if (c.getToxicStaffOfDeadCharge() > 0) {
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12934, c.getX(), c.getY(), c.heightLevel,
								c.getToxicStaffOfDeadCharge(), killer == null ? c.playerId : killer.playerId);
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12904, c.getX(), c.getY(), c.heightLevel,
								1, killer == null ? c.playerId : killer.playerId);
						if (c.getItems().isWearingItem(12904) && c.getItems().getWornItemSlot(12904) == c.playerWeapon) {
							c.getItems().wearItem(-1, 0, c.playerWeapon);
						} else {
							c.getItems().deleteItem2(12904, 1);
						}
						c.sendMessage("The toxic staff of the dead has been dropped on the floor. You lost the staff and its charge.");
						c.setToxicStaffOfDeadCharge(0);
					}
				}
				if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == c.playerWeapon
						|| c.getItems().playerHasItem(12926)) {
					if (c.getToxicBlowpipeAmmo() > 0 && c.getToxicBlowpipeAmmoAmount() > 0 && c.getToxicBlowpipeCharge() > 0) {
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12924, c.getX(), c.getY(), c.heightLevel,
								1, killer == null ? c.playerId : killer.playerId);
						Server.itemHandler.createGroundItem(killer == null ? c : killer, 12934, c.getX(), c.getY(), c.heightLevel,
								c.getToxicBlowpipeCharge(), killer == null ? c.playerId : killer.playerId);
						Server.itemHandler.createGroundItem(killer == null ? c : killer, c.getToxicBlowpipeAmmo(), c.getX(), c.getY(), 
								c.heightLevel, c.getToxicBlowpipeAmmoAmount(), killer == null ? c.playerId : killer.playerId);
						if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == c.playerWeapon) {
							c.getItems().wearItem(-1, 0, c.playerWeapon);
						} else {
							c.getItems().deleteItem2(12926, 1);
						}
						c.setToxicBlowpipeAmmo(0);
						c.setToxicBlowpipeAmmoAmount(0);
						c.setToxicBlowpipeCharge(0);
						c.sendMessage("Your blowpipe has been dropped on the floor. You lost the ammo, pipe, and charge.");
					}
				}*/
				/*
				 * } else if (c.inPits) {
				 * Server.fightPits.removePlayerFromPits(c.playerId);
				 * c.pitsStatus = 1;
				 */
			} else if (Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)) {
				c.getPA().movePlayer(2657, 2639, 0);
			}
		}
		c.getCombat().resetPrayers();
		for (int i = 0; i < 20; i++) {
			c.playerLevel[i] = getLevelForXP(c.playerXP[i]);
			c.getPA().refreshSkill(i);
		}
		if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
			c.getPA().movePlayer(2656 + Misc.random(2), 2614 - Misc.random(3), 0);
		} else if (Boundary.isIn(c, Zulrah.BOUNDARY)) {
			c.getPA().movePlayer(3092, 3494, 0);
			InstancedArea instance = c.getZulrahEvent().getInstancedZulrah();
			if (instance != null) {
				InstancedAreaManager.getSingleton().disposeOf(instance);
			}
			c.getLostItems().store();
			c.talkingNpc = 2040;
			c.getDH().sendNpcChat("You have lost!", "I'll give you your items back for 5 PKP.");
		} else if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION) {
				Player opponent = duelSession.getWinner().get();
				if (opponent != null) {
					opponent.getPA().createPlayerHints(10, -1);
					duelSession.finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
				}
			}
		} else if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			c.getFightCave().handleDeath();
		} else {
			movePlayer(Config.RESPAWN_X, Config.RESPAWN_Y, 0);
			c.isSkulled = false;
			c.skullTimer = 0;
			c.attackedPlayers.clear();
			removeAllWindows();
			closeAllWindows();
		}
		PlayerSave.saveGame(c);
		c.resetDamageReceived();
		c.getCombat().resetPlayerAttack();
		resetAnimation();
		c.startAnimation(65535);
		frame1();
		resetTb();
		c.isSkulled = false;
		c.attackedPlayers.clear();
		c.headIconPk = -1;
		c.skullTimer = -1;
		requestUpdates();
		c.tradeResetNeeded = true;
		c.setKiller(null);
		c.setKilledByZombie(false);
	}

	/**
	 * Location change for digging, levers etc
	 **/

	public void changeLocation() {
		switch (c.newLocation) {
		case 1:
			sendFrame99(2);
			movePlayer(3578, 9706, -1);
			break;
		case 2:
			sendFrame99(2);
			movePlayer(3568, 9683, -1);
			break;
		case 3:
			sendFrame99(2);
			movePlayer(3557, 9703, -1);
			break;
		case 4:
			sendFrame99(2);
			movePlayer(3556, 9718, -1);
			break;
		case 5:
			sendFrame99(2);
			movePlayer(3534, 9704, -1);
			break;
		case 6:
			sendFrame99(2);
			movePlayer(3546, 9684, -1);
			break;
		}
		c.newLocation = 0;
	}

	/**
	 * Teleporting
	 **/
	public void spellTeleport(int x, int y, int height) {
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		c.getPA().startTeleport(x, y, height, c.playerMagicBook == 1 ? "ancient" : "modern");
	}

	public void startLeverTeleport(int x, int y, int height, String teleportType) {
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}

		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			if (teleportType.equalsIgnoreCase("lever")) {
				c.startAnimation(2140);
				c.teleTimer = 8;
				c.sendMessage("You pull the lever..");
			}
		}
		c.getSkilling().stop();
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
	}

	public boolean canTeleport(String type) {
		if (!c.lastSpear.elapsed(4000)) {
			c.sendMessage("You are stunned and can not teleport!");
			return false;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				c.sendMessage("You cannot teleport whilst in a duel.");
				return false;
			}
		}
		if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			c.sendMessage("You cannot teleport out of fight caves.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You must finish what you're doing before you can teleport.");
			return false;
		}
		if (c.isInJail() && !(c.getRights().isBetween(1, 3))) {
			c.sendMessage("You cannot teleport out of jail.");
			return false;
		}
		if (c.inWild()) {
			if (!type.equals("glory")) {
				if (c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
					c.sendMessage("You can't teleport above level " + Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
					c.getPA().closeAllWindows();
					return false;
				}
			} else {
				if (c.wildLevel > 30) {
					c.sendMessage("You can't teleport above level 30 in the wilderness.");
					c.getPA().closeAllWindows();
					return false;
				}
			}
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return false;
		}
		return true;
	}

	public void startTeleport(int x, int y, int height, String teleportType) {
		c.isWc = false;
		if (c.stopPlayerSkill) {
			SkillHandler.resetPlayerSkillVariables(c);
			c.stopPlayerSkill = false;
		}
		resetVariables();
		SkillHandler.isSkilling[12] = false;
		if (!c.lastSpear.elapsed(4000)) {
			c.sendMessage("You are stunned and can not teleport!");
			return;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You must finish what you're doing first.");
			return;
		}
		if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			c.sendMessage("You cannot teleport out of fight caves.");
			return;
		}
		if (c.isInJail() && !(c.getRights().isBetween(1, 3))) {
			c.sendMessage("You cannot teleport out of jail.");
			return;
		}
		if (c.inWild() && !(c.getRights().isBetween(1, 3))) {
			if (!teleportType.equals("glory") && !teleportType.equals("obelisk")) {
				if (c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
					c.sendMessage("You can't teleport above level " + Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
					c.getPA().closeAllWindows();
					return;
				}
			} else if (!teleportType.equals("obelisk")) {
				if (c.wildLevel > 30) {
					c.sendMessage("You can't teleport above level 30 in the wilderness.");
					c.getPA().closeAllWindows();
					return;
				}
			}
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			if (teleportType.equalsIgnoreCase("modern") || teleportType.equals("glory")) {
				c.startAnimation(714);
				c.teleTimer = 11;
				c.teleGfx = 308;
				c.teleEndAnimation = 715;
			} else if (teleportType.equalsIgnoreCase("ancient")) {
				c.startAnimation(1979);
				c.teleGfx = 0;
				c.teleTimer = 9;
				c.teleEndAnimation = 0;
				c.gfx0(392);
			} else if (teleportType.equals("obelisk")) {
				c.startAnimation(1816);
				c.teleTimer = 11;
				c.teleGfx = 661;
				c.teleEndAnimation = 65535;
			}
			c.getSkilling().stop();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
		}
	}

	public void teleTabTeleport(int x, int y, int height, String teleportType) {
		/*
		 * if (c.inPits) {
		 * c.sendMessage("You can't teleport during Fight Pits."); return; }
		 */
		/*
		 * if (!c.startPack) { c.sendMessage(
		 * "You must select your starter package before doing any action.");
		 * return; }
		 */
		/*
		 * if (c.getPA().inPitsWait()) {
		 * c.sendMessage("You can't teleport during Fight Pits."); return; }
		 */
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You cannot teleport until you finish what you're doing.");
			return;
		}
		if (c.inWild() && c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
			c.sendMessage("You can't teleport above level " + Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			c.sendMessage("You cannot teleport out of fight caves.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.getSkilling().stop();
			if (teleportType.equalsIgnoreCase("teleTab")) {
				c.startAnimation(4731);
				c.teleEndAnimation = 0;
				c.teleTimer = 8;
				c.gfx0(678);
			}
		}
	}

	public void startTeleport2(int x, int y, int height) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You cannot teleport until you finish what you're doing.");
			return;
		}
		if (!c.lastSpear.elapsed(4000)) {
			c.sendMessage("You are stunned and can not teleport!");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
			c.sendMessage("You cannot teleport out of fight caves.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0) {
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.startAnimation(714);
			c.teleTimer = 11;
			c.teleGfx = 308;
			c.teleEndAnimation = 715;
			c.getSkilling().stop();
		}
	}

	public void processTeleport() {
		c.teleportToX = c.teleX;
		c.teleportToY = c.teleY;
		c.heightLevel = c.teleHeight;
		if (c.teleEndAnimation > 0) {
			c.startAnimation(c.teleEndAnimation);
		}
	}

	public void followNpc() {
		if (NPCHandler.npcs[c.followId] == null || NPCHandler.npcs[c.followId].isDead) {
			c.followId = 0;
			return;
		}
		if (c.freezeTimer > 0) {
			return;
		}
		if (c.isDead || c.playerLevel[3] <= 0)
			return;
		int otherX = NPCHandler.npcs[c.followId2].getX();
		int otherY = NPCHandler.npcs[c.followId2].getY();
		boolean withinDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
//		boolean goodDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1);
		boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
		boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 8);
		boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 4);
		boolean sameSpot = c.absX == otherX && c.absY == otherY;
		if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
			c.followId2 = 0;
			return;
		}
		if (c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
			if (otherX != c.getX() && otherY != c.getY()) {
				stopDiagonal(otherX, otherY);
				return;
			}
		}
		if ((c.usingBow || c.mageFollow || (c.npcIndex > 0 && c.autocastId > 0)) && bowDistance && !sameSpot/* && !Region.pathBlocked(c, NPCHandler.npcs[c.followId])*/) {
			return;
		}
		if (c.getCombat().usingHally() && hallyDistance && !sameSpot) {
			return;
		}

		if (c.usingRangeWeapon && rangeWeaponDistance && !sameSpot) {
			return;
		}

		c.faceUpdate(c.followId);
		if (otherX == c.absX && otherY == c.absY) {
			int r = Misc.random(3);
			switch (r) {
			case 0:
				walkTo(0, -1);
				break;
			case 1:
				walkTo(0, 1);
				break;
			case 2:
				walkTo(1, 0);
				break;
			case 3:
				walkTo(-1, 0);
				break;
			}
		} else if (c.isRunning2 && !withinDistance) {
			if (otherY > c.getY() && otherX == c.getX()) {
				// walkTo(0, getMove(c.getY(), otherY - 1) + getMove(c.getY(),
				// otherY - 1));
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				// walkTo(0, getMove(c.getY(), otherY + 1) + getMove(c.getY(),
				// otherY + 1));
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				// walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
				// otherX - 1), 0);
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
				// otherX + 1), 0);
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
				// otherX + 1), getMove(c.getY(), otherY + 1) +
				// getMove(c.getY(), otherY + 1));
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				// walkTo(getMove(c.getX(), otherX - 1) + getMove(c.getX(),
				// otherX - 1), getMove(c.getY(), otherY - 1) +
				// getMove(c.getY(), otherY - 1));
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
				// otherX + 1), getMove(c.getY(), otherY - 1) +
				// getMove(c.getY(), otherY - 1));
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1) + getMove(c.getX(),
				// otherX + 1), getMove(c.getY(), otherY - 1) +
				// getMove(c.getY(), otherY - 1));
				playerWalk(otherX + 1, otherY - 1);
			}
		} else {
			if (otherY > c.getY() && otherX == c.getX()) {
				// walkTo(0, getMove(c.getY(), otherY - 1));
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				// walkTo(0, getMove(c.getY(), otherY + 1));
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				// walkTo(getMove(c.getX(), otherX - 1), 0);
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1), 0);
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
				// otherY + 1));
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				// walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(),
				// otherY - 1));
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				// walkTo(getMove(c.getX(), otherX + 1), getMove(c.getY(),
				// otherY - 1));
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				// walkTo(getMove(c.getX(), otherX - 1), getMove(c.getY(),
				// otherY + 1));
				playerWalk(otherX - 1, otherY + 1);
			}
		}
		c.faceUpdate(c.followId);
	}

	/**
	 * Following
	 **/

	public void followPlayer() {
		if (PlayerHandler.players[c.followId] == null || PlayerHandler.players[c.followId].isDead) {
			c.followId = 0;
			return;
		}
		if (c.freezeTimer > 0) {
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (!Objects.isNull(session)) {
				if (session.getRules().contains(Rule.NO_MOVEMENT)) {
					c.followId = 0;
					return;
				}
			}
		}
		if (inPitsWait()) {
			c.followId = 0;
		}

		if (c.isDead || c.playerLevel[3] <= 0)
			return;
		if (!c.lastSpear.elapsed(4000)) {
			c.sendMessage("You are stunned, you cannot move.");
			c.followId = 0;
			return;
		}
		int otherX = PlayerHandler.players[c.followId].getX();
		int otherY = PlayerHandler.players[c.followId].getY();

		boolean sameSpot = (c.absX == otherX && c.absY == otherY);

		boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
		boolean withinDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 2);
		boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 4);
		boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(), c.getY(), 6);

		if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
			c.followId = 0;
			return;
		}
		if (c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
			if (otherX != c.getX() && otherY != c.getY()) {
				stopDiagonal(otherX, otherY);
				return;
			}
		}

		if ((c.usingBow || c.mageFollow || (c.playerIndex > 0 && c.autocastId > 0)) && bowDistance && !sameSpot) {
			return;
		}

		if (c.getCombat().usingHally() && hallyDistance && !sameSpot) {
			return;
		}

		if (c.usingRangeWeapon && rangeWeaponDistance && !sameSpot) {
			return;
		}

		c.faceUpdate(c.followId + 32768);
		if (otherX == c.absX && otherY == c.absY) {
			int r = Misc.random(3);
			switch (r) {
			case 0:
				if (Region.canMove(c.absX, c.absY, c.absX, c.absY - 1, c.heightLevel, 1, 1))
					walkTo(0, -1);
				break;
			case 1:
				if (Region.canMove(c.absX, c.absY, c.absX, c.absY + 1, c.heightLevel, 1, 1))
					walkTo(0, 1);
				break;
			case 2:
				if (Region.canMove(c.absX, c.absY, c.absX + 1, c.absY, c.heightLevel, 1, 1))
					walkTo(1, 0);
				break;
			case 3:
				if (Region.canMove(c.absX, c.absY, c.absX - 1, c.absY, c.heightLevel, 1, 1))
					walkTo(-1, 0);
				break;
			}
		} else if (c.isRunning2 && !withinDistance) {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			}
		} else {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX - 1, otherY + 1);
			}
		}
		c.faceUpdate(c.followId + 32768);
	}

	public int getRunningMove(int i, int j) {
		if (j - i > 2)
			return 2;
		else if (j - i < -2)
			return -2;
		else
			return j - i;
	}

	public void sendStatement(String s) {
		sendFrame126(s, 357);
		sendFrame126("Click here to continue", 358);
		sendFrame164(356);
	}

	public void resetFollow() {
		c.followId = 0;
		c.followId2 = 0;
		c.mageFollow = false;
		if (c.outStream != null) {
			c.outStream.createFrame(174);
			c.outStream.writeWord(0);
			c.outStream.writeByte(0);
			c.outStream.writeWord(1);
		}
	}
	
	public void stepAway(NPC npc) {
		if (npc == null) {
			return;
		}
		int size = npc.getSize();
		int xOffset = 0;
		int yOffset = 0;
		if (npc.getX() > c.getX()) {
			if (npc.getY() > c.getY()) {
				
			} else if (npc.getY() < c.getY()) {
				
			}
		}
	}

	public void walkTo3(int i, int j) {
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.absX + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = tmpNWCX[0] = tmpNWCY[0] = 0;
		int l = c.absY + j;
		l -= c.mapRegionY * 8;
		c.isRunning2 = false;
		c.isRunning = false;
		c.getNewWalkCmdX()[0] += k;
		c.getNewWalkCmdY()[0] += l;
		c.poimiY = l;
		c.poimiX = k;
	}

	int tmpNWCX[] = new int[50];
	int tmpNWCY[] = new int[50];

	public void walkTo(int i, int j) {
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public void walkTo2(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public void stopDiagonal(int otherX, int otherY) {
		if (c.freezeDelay > 0)
			return;
		if (c.freezeTimer > 0) // player can't move
			return;
		c.newWalkCmdSteps = 1;
		int xMove = otherX - c.getX();
		int yMove = 0;
		if (xMove == 0)
			yMove = otherY - c.getY();
		/*
		 * if (!clipHor) { yMove = 0; } else if (!clipVer) { xMove = 0; }
		 */

		int k = c.getX() + xMove;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + yMove;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}

	}

	public void walkToCheck(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public int getMove(int place1, int place2) {
		if (!c.lastSpear.elapsed(4000))
			return 0;
		if ((place1 - place2) == 0) {
			return 0;
		} else if ((place1 - place2) < 0) {
			return 1;
		} else if ((place1 - place2) > 0) {
			return -1;
		}
		return 0;
	}

	public boolean fullVeracs() {
		return c.playerEquipment[c.playerHat] == 4753 && c.playerEquipment[c.playerChest] == 4757 && c.playerEquipment[c.playerLegs] == 4759
				&& c.playerEquipment[c.playerWeapon] == 4755;
	}

	public boolean fullGuthans() {
		return c.playerEquipment[c.playerHat] == 4724 && c.playerEquipment[c.playerChest] == 4728 && c.playerEquipment[c.playerLegs] == 4730
				&& c.playerEquipment[c.playerWeapon] == 4726;
	}

	/**
	 * reseting animation
	 **/
	public void resetAnimation() {
		c.getCombat().getPlayerAnimIndex(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
		c.startAnimation(c.playerStandIndex);
		requestUpdates();
	}

	public void requestUpdates() {
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	/*
	 * public void Obelisks(int id) { if (!c.getItems().playerHasItem(id)) {
	 * c.getItems().addItem(id, 1); } }
	 */

	public void levelUp(int skill) {
		int totalLevel = (getLevelForXP(c.playerXP[0]) + getLevelForXP(c.playerXP[1]) + getLevelForXP(c.playerXP[2]) + getLevelForXP(c.playerXP[3])
				+ getLevelForXP(c.playerXP[4]) + getLevelForXP(c.playerXP[5]) + getLevelForXP(c.playerXP[6]) + getLevelForXP(c.playerXP[7])
				+ getLevelForXP(c.playerXP[8]) + getLevelForXP(c.playerXP[9]) + getLevelForXP(c.playerXP[10]) + getLevelForXP(c.playerXP[11])
				+ getLevelForXP(c.playerXP[12]) + getLevelForXP(c.playerXP[13]) + getLevelForXP(c.playerXP[14]) + getLevelForXP(c.playerXP[15])
				+ getLevelForXP(c.playerXP[16]) + getLevelForXP(c.playerXP[17]) + getLevelForXP(c.playerXP[18]) + getLevelForXP(c.playerXP[19]) + getLevelForXP(c.playerXP[20]));
		sendFrame126("Total Lvl: " + totalLevel, 3984);
		switch (skill) {
		case 0:
			sendFrame126("Congratulations, you just advanced an attack level!", 6248);
			sendFrame126("Your attack level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6249);
			c.sendMessage("Congratulations, you just advanced an attack level.");
			sendFrame164(6247);
			break;

		case 1:
			sendFrame126("Congratulations, you just advanced a defence level!", 6254);
			sendFrame126("Your defence level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6255);
			c.sendMessage("Congratulations, you just advanced a defence level.");
			sendFrame164(6253);
			break;

		case 2:
			sendFrame126("Congratulations, you just advanced a strength level!", 6207);
			sendFrame126("Your strength level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6208);
			c.sendMessage("Congratulations, you just advanced a strength level.");
			sendFrame164(6206);
			break;

		case 3:
			sendFrame126("Congratulations, you just advanced a hitpoints level!", 6217);
			sendFrame126("Your hitpoints level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6218);
			c.sendMessage("Congratulations, you just advanced a hitpoints level.");
			sendFrame164(6216);
			break;

		case 4:
			c.sendMessage("Congratulations, you just advanced a ranging level.");
			break;

		case 5:
			sendFrame126("Congratulations, you just advanced a prayer level!", 6243);
			sendFrame126("Your prayer level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6244);
			c.sendMessage("Congratulations, you just advanced a prayer level.");
			sendFrame164(6242);
			break;

		case 6:
			sendFrame126("Congratulations, you just advanced a magic level!", 6212);
			sendFrame126("Your magic level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6213);
			c.sendMessage("Congratulations, you just advanced a magic level.");
			sendFrame164(6211);
			break;

		case 7:
			sendFrame126("Congratulations, you just advanced a cooking level!", 6227);
			sendFrame126("Your cooking level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6228);
			c.sendMessage("Congratulations, you just advanced a cooking level.");
			sendFrame164(6226);
			break;

		case 8:
			sendFrame126("Congratulations, you just advanced a woodcutting level!", 4273);
			sendFrame126("Your woodcutting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4274);
			c.sendMessage("Congratulations, you just advanced a woodcutting level.");
			sendFrame164(4272);
			break;

		case 9:
			sendFrame126("Congratulations, you just advanced a fletching level!", 6232);
			sendFrame126("Your fletching level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6233);
			c.sendMessage("Congratulations, you just advanced a fletching level.");
			sendFrame164(6231);
			break;

		case 10:
			sendFrame126("Congratulations, you just advanced a fishing level!", 6259);
			sendFrame126("Your fishing level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6260);
			c.sendMessage("Congratulations, you just advanced a fishing level.");
			sendFrame164(6258);
			break;

		case 11:
			sendFrame126("Congratulations, you just advanced a fire making level!", 4283);
			sendFrame126("Your firemaking level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4284);
			c.sendMessage("Congratulations, you just advanced a fire making level.");
			sendFrame164(4282);
			break;

		case 12:
			sendFrame126("Congratulations, you just advanced a crafting level!", 6264);
			sendFrame126("Your crafting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6265);
			c.sendMessage("Congratulations, you just advanced a crafting level.");
			sendFrame164(6263);
			break;

		case 13:
			sendFrame126("Congratulations, you just advanced a smithing level!", 6222);
			sendFrame126("Your smithing level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6223);
			c.sendMessage("Congratulations, you just advanced a smithing level.");
			sendFrame164(6221);
			break;

		case 14:
			sendFrame126("Congratulations, you just advanced a mining level!", 4417);
			sendFrame126("Your mining level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4438);
			c.sendMessage("Congratulations, you just advanced a mining level.");
			sendFrame164(4416);
			break;

		case 15:
			sendFrame126("Congratulations, you just advanced a herblore level!", 6238);
			sendFrame126("Your herblore level is now " + getLevelForXP(c.playerXP[skill]) + ".", 6239);
			c.sendMessage("Congratulations, you just advanced a herblore level.");
			sendFrame164(6237);
			break;

		case 16:
			sendFrame126("Congratulations, you just advanced a agility level!", 4278);
			sendFrame126("Your agility level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4279);
			c.sendMessage("Congratulations, you just advanced an agility level.");
			sendFrame164(4277);
			break;

		case 17:
			sendFrame126("Congratulations, you just advanced a thieving level!", 4263);
			sendFrame126("Your theiving level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4264);
			c.sendMessage("Congratulations, you just advanced a thieving level.");
			sendFrame164(4261);
			break;

		case 18:
			sendFrame126("Congratulations, you just advanced a slayer level!", 12123);
			sendFrame126("Your slayer level is now " + getLevelForXP(c.playerXP[skill]) + ".", 12124);
			c.sendMessage("Congratulations, you just advanced a slayer level.");
			sendFrame164(12122);
			break;
			
		case 19:
	        c.sendMessage("Congratulations! You've just advanced a Farming level.");
			break;

		case 20:
			sendFrame126("Congratulations, you just advanced a runecrafting level!", 4268);
			sendFrame126("Your runecrafting level is now " + getLevelForXP(c.playerXP[skill]) + ".", 4269);
			c.sendMessage("Congratulations, you just advanced a runecrafting level.");
			sendFrame164(4267);
			break;
		}
		if (getLevelForXP(c.playerXP[skill]) == 99) {
			Skill s = Skill.forId(skill);
			PlayerHandler.executeGlobalMessage("<img=10></img>[<col=255>News</col>] <col=CC0000>" + Misc.capitalize(c.playerName) + 
					"</col> has reached level 99 <col=CC0000>" + s.toString() + "</col>, congratulations.");
		}
		c.dialogueAction = 0;
		c.nextChat = 0;
	}

	public void refreshSkill(int i) {
		c.combatLevel = c.calculateCombatLevel();
		switch (i) {
		case 0:
			sendFrame126("" + c.playerLevel[0] + "", 4004);
			sendFrame126("" + getLevelForXP(c.playerXP[0]) + "", 4005);
			sendFrame126("" + c.playerXP[0] + "", 4044);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[0]) + 1) + "", 4045);
			requestUpdates();
			break;

		case 1:
			sendFrame126("" + c.playerLevel[1] + "", 4008);
			sendFrame126("" + getLevelForXP(c.playerXP[1]) + "", 4009);
			sendFrame126("" + c.playerXP[1] + "", 4056);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[1]) + 1) + "", 4057);
			break;

		case 2:
			sendFrame126("" + c.playerLevel[2] + "", 4006);
			sendFrame126("" + getLevelForXP(c.playerXP[2]) + "", 4007);
			sendFrame126("" + c.playerXP[2] + "", 4050);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[2]) + 1) + "", 4051);
			break;

		case 3:
			sendFrame126("" + c.playerLevel[3] + "", 4016);
			sendFrame126("" + getLevelForXP(c.playerXP[3]) + "", 4017);
			sendFrame126("" + c.playerXP[3] + "", 4080);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[3]) + 1) + "", 4081);
			break;

		case 4:
			sendFrame126("" + c.playerLevel[4] + "", 4010);
			sendFrame126("" + getLevelForXP(c.playerXP[4]) + "", 4011);
			sendFrame126("" + c.playerXP[4] + "", 4062);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[4]) + 1) + "", 4063);
			break;

		case 5:
			sendFrame126("" + c.playerLevel[5] + "", 4012);
			sendFrame126("" + getLevelForXP(c.playerXP[5]) + "", 4013);
			sendFrame126("" + c.playerXP[5] + "", 4068);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[5]) + 1) + "", 4069);
			sendFrame126("" + c.playerLevel[5] + "/" + getLevelForXP(c.playerXP[5]) + "", 687);// Prayer
																								// frame
			break;

		case 6:
			sendFrame126("" + c.playerLevel[6] + "", 4014);
			sendFrame126("" + getLevelForXP(c.playerXP[6]) + "", 4015);
			sendFrame126("" + c.playerXP[6] + "", 4074);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[6]) + 1) + "", 4075);
			break;

		case 7:
			sendFrame126("" + c.playerLevel[7] + "", 4034);
			sendFrame126("" + getLevelForXP(c.playerXP[7]) + "", 4035);
			sendFrame126("" + c.playerXP[7] + "", 4134);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[7]) + 1) + "", 4135);
			break;

		case 8:
			sendFrame126("" + c.playerLevel[8] + "", 4038);
			sendFrame126("" + getLevelForXP(c.playerXP[8]) + "", 4039);
			sendFrame126("" + c.playerXP[8] + "", 4146);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[8]) + 1) + "", 4147);
			break;

		case 9:
			sendFrame126("" + c.playerLevel[9] + "", 4026);
			sendFrame126("" + getLevelForXP(c.playerXP[9]) + "", 4027);
			sendFrame126("" + c.playerXP[9] + "", 4110);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[9]) + 1) + "", 4111);
			break;

		case 10:
			sendFrame126("" + c.playerLevel[10] + "", 4032);
			sendFrame126("" + getLevelForXP(c.playerXP[10]) + "", 4033);
			sendFrame126("" + c.playerXP[10] + "", 4128);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[10]) + 1) + "", 4129);
			break;

		case 11:
			sendFrame126("" + c.playerLevel[11] + "", 4036);
			sendFrame126("" + getLevelForXP(c.playerXP[11]) + "", 4037);
			sendFrame126("" + c.playerXP[11] + "", 4140);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[11]) + 1) + "", 4141);
			break;

		case 12:
			sendFrame126("" + c.playerLevel[12] + "", 4024);
			sendFrame126("" + getLevelForXP(c.playerXP[12]) + "", 4025);
			sendFrame126("" + c.playerXP[12] + "", 4104);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[12]) + 1) + "", 4105);
			break;

		case 13:
			sendFrame126("" + c.playerLevel[13] + "", 4030);
			sendFrame126("" + getLevelForXP(c.playerXP[13]) + "", 4031);
			sendFrame126("" + c.playerXP[13] + "", 4122);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[13]) + 1) + "", 4123);
			break;

		case 14:
			sendFrame126("" + c.playerLevel[14] + "", 4028);
			sendFrame126("" + getLevelForXP(c.playerXP[14]) + "", 4029);
			sendFrame126("" + c.playerXP[14] + "", 4116);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[14]) + 1) + "", 4117);
			break;

		case 15:
			sendFrame126("" + c.playerLevel[15] + "", 4020);
			sendFrame126("" + getLevelForXP(c.playerXP[15]) + "", 4021);
			sendFrame126("" + c.playerXP[15] + "", 4092);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[15]) + 1) + "", 4093);
			break;

		case 16:
			sendFrame126("" + c.playerLevel[16] + "", 4018);
			sendFrame126("" + getLevelForXP(c.playerXP[16]) + "", 4019);
			sendFrame126("" + c.playerXP[16] + "", 4086);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[16]) + 1) + "", 4087);
			break;

		case 17:
			sendFrame126("" + c.playerLevel[17] + "", 4022);
			sendFrame126("" + getLevelForXP(c.playerXP[17]) + "", 4023);
			sendFrame126("" + c.playerXP[17] + "", 4098);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[17]) + 1) + "", 4099);
			break;

		case 18:
			sendFrame126("" + c.playerLevel[18] + "", 12166);
			sendFrame126("" + getLevelForXP(c.playerXP[18]) + "", 12167);
			sendFrame126("" + c.playerXP[18] + "", 12171);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[18]) + 1) + "", 12172);
			break;

		case 19:
			sendFrame126("" + c.playerLevel[19] + "", 13926);
			sendFrame126("" + getLevelForXP(c.playerXP[19]) + "", 13927);
			sendFrame126("" + c.playerXP[19] + "", 13921);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[19]) + 1) + "", 13922);
			break;

		case 20:
			sendFrame126("" + c.playerLevel[20] + "", 4152);
			sendFrame126("" + getLevelForXP(c.playerXP[20]) + "", 4153);
			sendFrame126("" + c.playerXP[20] + "", 4157);
			sendFrame126("" + getXPForLevel(getLevelForXP(c.playerXP[20]) + 1) + "", 4159);
			break;
		}
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
		if (exp > 13034430)
			return 99;
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp) {
				return lvl;
			}
		}
		return 0;
	}

	public boolean addSkillXP(double amount, int skill) {
		if (amount + c.playerXP[skill] < 0) {
			return false;
		}
		if (Config.BONUS_WEEKEND && c.bonusXpTime == 0) {
			amount *= Config.SERVER_EXP_BONUS_WEEKEND;
		} else if (Config.BONUS_WEEKEND && c.bonusXpTime > 0) {
			amount *= Config.SERVER_EXP_BONUS_WEEKEND_BOOSTED;
		} else if (!Config.BONUS_WEEKEND && c.bonusXpTime > 0) {
			amount *= Config.SERVER_EXP_BONUS_BOOSTED;
		} else {
			amount *= Config.SERVER_EXP_BONUS;
		}
		if (Boundary.isIn(c, Boundary.RESOURCE_AREA) && skill > 6) {
			amount *= 1.10;
		}
		int oldLevel = getLevelForXP(c.playerXP[skill]);
		if (c.playerXP[skill] + amount > 200000000) {
			c.playerXP[skill] = 200000000;
		} else {
			c.playerXP[skill] += amount;
		}
		if (oldLevel < getLevelForXP(c.playerXP[skill])) {
			if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill]) && skill != 3 && skill != 5)
				c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
			c.combatLevel = c.calculateCombatLevel();
			levelUp(skill);
			c.gfx100(199);
			requestUpdates();
		}
		setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
		refreshSkill(skill);
		return true;
	}

	public boolean addSkillXP(int amount, int skill) {
		if (c.expLock == true) {
			return false;
		}
		if (amount + c.playerXP[skill] < 0) {
			return false;
		}
		if (Config.BONUS_WEEKEND && c.bonusXpTime == 0) {
			amount *= Config.SERVER_EXP_BONUS_WEEKEND;
		} else if (Config.BONUS_WEEKEND && c.bonusXpTime > 0) {
			amount *= Config.SERVER_EXP_BONUS_WEEKEND_BOOSTED;
		} else if (!Config.BONUS_WEEKEND && c.bonusXpTime > 0) {
			amount *= Config.SERVER_EXP_BONUS_BOOSTED;
		} else {
			amount *= Config.SERVER_EXP_BONUS;
		}
		int oldLevel = getLevelForXP(c.playerXP[skill]);
		if (c.playerXP[skill] + amount > 200000000) {
			c.playerXP[skill] = 200000000;
		} else {
			c.playerXP[skill] += amount;
		}
		if (oldLevel < getLevelForXP(c.playerXP[skill])) {
			if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill]) && skill != 3 && skill != 5)
				c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
			c.combatLevel = c.calculateCombatLevel();
			levelUp(skill);
			c.gfx100(199);
			requestUpdates();
		}
		setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
		refreshSkill(skill);
		return true;
	}

	public void resetBarrows() {
		c.barrowsNpcs[0][1] = 0;
		c.barrowsNpcs[1][1] = 0;
		c.barrowsNpcs[2][1] = 0;
		c.barrowsNpcs[3][1] = 0;
		c.barrowsNpcs[4][1] = 0;
		c.barrowsNpcs[5][1] = 0;
		c.barrowsKillCount = 0;
		c.randomCoffin = Misc.random(3) + 1;
	}

	// public static int Barrows[] = {4708, 4710, 4712, 4714, 4716, 4718, 4720,
	// 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749,
	// 4751, 4753, 4755, 4757, 4759};
	public static int Runes[] = { 4740, 558, 560, 565 };
	public static int Pots[] = {};

	/*
	 * public int randomBarrows() { return
	 * Barrows[(int)(Math.random()*Barrows.length)]; }
	 */

	public int randomRunes() {
		return Runes[(int) (Math.random() * Runes.length)];
	}

	public int randomPots() {
		return Pots[(int) (Math.random() * Pots.length)];
	}

	/**
	 * Show an arrow icon on the selected player.
	 * 
	 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
	 * @Param j - The player/Npc that the arrow will be displayed above.
	 * @Param k - Keep this set as 0
	 * @Param l - Keep this set as 0
	 */
	public void drawHeadicon(int i, int j, int k, int l) {
		// synchronized(c) {
		c.outStream.createFrame(254);
		c.outStream.writeByte(i);

		if (i == 1 || i == 10) {
			c.outStream.writeWord(j);
			c.outStream.writeWord(k);
			c.outStream.writeByte(l);
		} else {
			c.outStream.writeWord(k);
			c.outStream.writeWord(l);
			c.outStream.writeByte(j);
		}

	}

	public int getNpcId(int id) {
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] != null) {
				if (NPCHandler.npcs[i].npcId == id) {
					return i;
				}
			}
		}
		return -1;
	}

	public void removeObject(int x, int y) {
		object(-1, x, y, 10, 10);
	}

	private void objectToRemove(int X, int Y) {
		object(-1, X, Y, 10, 10);
	}

	private void objectToRemove2(int X, int Y) {
		object(-1, X, Y, -1, 0);
	}

	public void removeObjects() {
		objectToRemove(2638, 4688);
		objectToRemove2(2635, 4693);
		objectToRemove2(2634, 4693);
	}

	public void handleGlory(int gloryId) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You cannot do that right now.");
			return;
		}
		c.getDH().sendOption4("Edgeville", "Al Kharid", "Karamja", "Mage Bank");
		c.sendMessage("You rub the amulet...");
		c.usingGlory = true;
	}
	
	public void handleDueling(int duelingId) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You cannot do that right now.");
			return;
		}
		c.getDH().sendOption3("Duel Arena", "Castle Wars", "Clan Wars");
		c.sendMessage("You rub the ring...");
		c.usingDueling = true;
	}
	
	public void handleROW(int rowId) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.sendMessage("You cannot do that right now.");
			return;
		}
		c.getDH().sendOption4("Miscellania", "Grand Exchange", "Falador Park", "Zul-Andra");
		c.sendMessage("You rub the ring...");
		c.usingROW = true;
	}

	public void resetVariables() {
		if (c.playerIsCrafting) {
			CraftingData.resetCrafting(c);
		}
		if (c.playerSkilling[9]) {
			c.playerSkilling[9] = false;
		}
		if (c.isBanking) {
			c.isBanking = false;
		}
		c.usingGlory = false;
		c.smeltInterface = false;
		// c.smeltAmount = 0;
		if (c.dialogueAction > -1) {
			c.dialogueAction = -1;
		}
		if (c.teleAction > -1) {
			c.teleAction = -1;
		}
		if (c.craftDialogue) {
			c.craftDialogue = false;
		}
		c.setInterfaceOpen(-1);
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.BONE_ON_ALTAR);
	}

	public boolean inPitsWait() {
		return c.getX() <= 2404 && c.getX() >= 2394 && c.getY() <= 5175 && c.getY() >= 5169;
	}

	public void castleWarsObjects() {
		object(-1, 2373, 3119, -3, 10);
		object(-1, 2372, 3119, -3, 10);
	}
	public double getAgilityRunRestore() {
		return 2260 - (c.playerLevel[16] * 10);
	}

	public boolean checkForFlags() {
		int[][] itemsToCheck = { { 995, 100000000 }, { 35, 5 }, { 667, 5 }, { 2402, 5 }, { 746, 5 }, { 4151, 150 }, { 565, 100000 }, { 560, 100000 },
				{ 555, 300000 }, { 11235, 10 } };
		for (int j = 0; j < itemsToCheck.length; j++) {
			if (itemsToCheck[j][1] < c.getItems().getTotalCount(itemsToCheck[j][0]))
				return true;
		}
		return false;
	}

	/*
	 * Vengeance
	 */
	public void castVeng() {
		if (c.playerLevel[6] < 94) {
			c.sendMessage("You need a magic level of 94 to cast this spell.");
			return;
		}
		if (c.playerLevel[1] < 40) {
			c.sendMessage("You need a defence level of 40 to cast this spell.");
			return;
		}
		if (!c.getItems().playerHasItem(9075, 4) || !c.getItems().playerHasItem(557, 10) || !c.getItems().playerHasItem(560, 2)) {
			c.sendMessage("You don't have the required runes to cast this spell.");
			return;
		}
		if (System.currentTimeMillis() - c.lastCast < 30000) {
			c.sendMessage("You can only cast vengeance every 30 seconds.");
			return;
		}
		if (c.vengOn) {
			c.sendMessage("You already have vengeance casted.");
			return;
		}
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (!Objects.isNull(session)) {
			if (session.getRules().contains(Rule.NO_MAGE)) {
				c.sendMessage("You can't cast this spell because magic has been disabled.");
				return;
			}
		}
		c.startAnimation(4410);
		c.gfx100(726);
		c.getItems().deleteItem2(9075, 4);
		c.getItems().deleteItem2(557, 10);
		c.getItems().deleteItem2(560, 2);
		addSkillXP(10000, 6);
		refreshSkill(6);
		c.vengOn = true;
        c.getPA().sendFrame126("vengtimer:50", 1811);
		c.lastCast = System.currentTimeMillis();
	}

	public void vengMe() {
		if (c.lastVeng.elapsed(30000)) {
			if (c.getItems().playerHasItem(557, 10) && c.getItems().playerHasItem(9075, 4) && c.getItems().playerHasItem(560, 2)) {
				c.vengOn = true;
	            c.lastCast = System.currentTimeMillis();
	            c.getPA().sendFrame126("vengtimer:50", 1811);
				c.lastVeng.reset();
				c.startAnimation(4410);
				c.gfx100(726);
				c.getItems().deleteItem(557, c.getItems().getItemSlot(557), 10);
				c.getItems().deleteItem(560, c.getItems().getItemSlot(560), 2);
				c.getItems().deleteItem(9075, c.getItems().getItemSlot(9075), 4);
			} else {
				c.sendMessage("You do not have the required runes to cast this spell. (9075 for astrals)");
			}
		} else {
			c.sendMessage("You must wait 30 seconds before casting this again.");
		}
	}

	public int totalLevel() {
		int total = 0;
		for (int i = 0; i <= 20; i++) {
			total += getLevelForXP(c.playerXP[i]);
		}
		return total;
	}

	public int xpTotal() {
		int xp = 0;
		for (int i = 0; i <= 20; i++) {
			xp += c.playerXP[i];
		}
		return xp;
	}

	public void addStarter() {
		if (!Connection.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
                                    for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = (Player) PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] "
							+ Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}

				c.getDH().sendDialogues(2299, 0);
			c.getItems().addItem(995, 2000000);
			c.getItems().addItem(386, 1000);
			c.getItems().addItem(6686, 1000);
			c.getItems().addItem(3025, 1000);
			c.getItems().addItem(2437, 1000);
			c.getItems().addItem(2441, 1000);
			c.getItems().addItem(3041, 1000);
			c.getItems().addItem(2445, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(557, 1000);
			c.getItems().addItem(9075, 1000);
			c.getItems().addItem(555, 1000);
			c.getItems().addItem(565, 1000);
			Connection.addIpToStarterList1(PlayerHandler.players[c.playerId].connectedFrom);
			Connection.addIpToStarter1(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)
				&& !Connection
						.hasRecieved2ndStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
                                    for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = (Player) PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] "
							+ Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}
								c.getDH().sendDialogues(2299, 0);
							c.getItems().addItem(995, 2000000);
			c.getItems().addItem(386, 1000);
			c.getItems().addItem(6686, 1000);
			c.getItems().addItem(3025, 1000);
			c.getItems().addItem(2437, 1000);
			c.getItems().addItem(2441, 1000);
			c.getItems().addItem(3041, 1000);
			c.getItems().addItem(2445, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(557, 1000);
			c.getItems().addItem(9075, 1000);
			c.getItems().addItem(555, 1000);
			c.getItems().addItem(565, 1000);
			c.sendMessage("You have recieved 2 out of 2 starter packages.");
			Connection
					.addIpToStarterList2(PlayerHandler.players[c.playerId].connectedFrom);
			Connection
					.addIpToStarter2(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection
				.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)
				&& Connection
						.hasRecieved2ndStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
                                    for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = (Player) PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] "
							+ Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}
			c.sendMessage("You have already recieved 2 starters!");
		}
	}

	public void addStarterEco() {
		if (!Connection.hasRecieved1stStarterEco(PlayerHandler.players[c.playerId].connectedFrom)) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] " + Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}
			c.sendMessage("<img=11> Did you know? You can type @blu@::help@bla@ to request help from our staff members!");
			c.getDH().sendDialogues(2299, 0);
			c.getItems().addItem(995, 1000000);
			c.getItems().addItem(386, 1000);
			c.getItems().addItem(6686, 1000);
			c.getItems().addItem(3025, 1000);
			c.getItems().addItem(2437, 1000);
			c.getItems().addItem(2441, 1000);
			c.getItems().addItem(3041, 1000);
			c.getItems().addItem(2445, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(557, 1000);
			c.getItems().addItem(9075, 1000);
			c.getItems().addItem(555, 1000);
			c.getItems().addItem(565, 1000);
			Connection.addIpToStarterListEco1(PlayerHandler.players[c.playerId].connectedFrom);
			Connection.addIpToStarterEco1(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection.hasRecieved1stStarterEco(PlayerHandler.players[c.playerId].connectedFrom)
				&& !Connection.hasRecieved2ndStarterEco(PlayerHandler.players[c.playerId].connectedFrom)) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] " + Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}
			c.getDH().sendDialogues(2299, 0);
			c.getItems().addItem(995, 2000000);
			c.getItems().addItem(386, 1000);
			c.getItems().addItem(6686, 1000);
			c.getItems().addItem(3025, 1000);
			c.getItems().addItem(2437, 1000);
			c.getItems().addItem(2441, 1000);
			c.getItems().addItem(3041, 1000);
			c.getItems().addItem(2445, 1000);
			c.getItems().addItem(560, 1000);
			c.getItems().addItem(557, 1000);
			c.getItems().addItem(9075, 1000);
			c.getItems().addItem(555, 1000);
			c.getItems().addItem(565, 1000);
			c.sendMessage("You have recieved 2 out of 2 starter packages.");
			c.sendMessage("<img=11> Did you know? You can type @blu@::help@bla@ to request help from our staff members!");
			Connection.addIpToStarterListEco2(PlayerHandler.players[c.playerId].connectedFrom);
			Connection.addIpToStarterEco2(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection.hasRecieved1stStarterEco(PlayerHandler.players[c.playerId].connectedFrom)
				&& Connection.hasRecieved2ndStarterEco(PlayerHandler.players[c.playerId].connectedFrom)) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Player c2 = PlayerHandler.players[j];
					c2.sendMessage("[@blu@<img=10>New Player@bla@] " + Misc.ucFirst(c.playerName) + " @bla@has logged in! Welcome!");
				}
			}
			c.sendMessage("You have already recieved 2 starters!");
			c.sendMessage("<img=11> Did you know? You can type @blu@::help@bla@ to request help from our staff members!");
		}
	}

	public void sendFrame34P2(int item, int slot, int frame, int amount) {
		c.outStream.createFrameVarSizeWord(34);
		c.outStream.writeWord(frame);
		c.outStream.writeByte(slot);
		c.outStream.writeWord(item + 1);
		c.outStream.writeByte(255);
		c.outStream.writeDWord(amount);
		c.outStream.endFrameVarSizeWord();
	}

	public int getWearingAmount() {
		int count = 0;
		for (int j = 0; j < c.playerEquipment.length; j++) {
			if (c.playerEquipment[j] > 0)
				count++;
		}
		return count;
	}
	public void startSlayerTeleport() {
		if (c.inWild()) {
			c.sendMessage("You can not teleport to your task whilst in the wilderness.");
				return;
			}
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("Finish what you are doing before teleporting to your task.");
				return;
			}
			switch(c.slayerTask){
						case 273:
						c.getPA().startTeleport(2706, 9450, 0, "modern");
						break;
						case 270:
						c.getPA().startTeleport(2731, 9484, 0, "modern");
						break;
						case 85:
						c.getPA().startTeleport(2903, 9849, 0, "modern");
						break;
						case 448:
						c.getPA().startTeleport(3416,3536,0,"modern");
						break;
						case 2834:
						c.getPA().startTeleport(2913, 9832, 0, "modern");
						break;
						case 437:
						c.getPA().startTeleport(2705, 10028, 0, "modern");
						break;
						case 419:
						c.getPA().startTeleport(2802, 10034, 0, "modern");
						break;
						case 417:
						c.getPA().startTeleport(2745, 10005, 0, "modern");
						break;
						case 435:
						c.getPA().startTeleport(2761, 10011, 0, "modern");
						break;
						case 2840:
						c.getPA().startTeleport(3121, 9970, 0, "modern");
						break;
						case 2025:
						c.getPA().startTeleport(2635, 9505, 2, "modern");
						break;
						case 2098:
						c.getPA().startTeleport(3118, 9838, 0, "modern");
						break;
						case 2084:
						c.getPA().startTeleport(2645, 9491, 0, "modern");
						break;
						case 411:
						c.getPA().startTeleport(2701, 9997, 0, "modern");
						break;
						case 119:
						c.getPA().startTeleport(2895, 9769, 0, "modern");
						break;
						case 18:
						c.getPA().startTeleport(3293, 3171, 0, "modern");
						break;
						case 181:
						c.getPA().startTeleport(2932, 9847, 0, "modern");
						break;
						case 446:
						c.getPA().startTeleport(3436, 3560, 1, "modern");
						break;
						case 484:
						c.getPA().startTeleport(3418, 3563, 1, "modern");
						break;
						case 264:
						c.getPA().startTeleport(2988, 3597, 0, "modern");
						break;
						case 2005:
						c.getPA().startTeleport(2932, 9800, 0, "modern");
						break;
						case 52:
						c.getPA().startTeleport(2916, 9801, 0, "modern");
						break;
						case 1612:
						c.getPA().startTeleport(3438, 3560, 0, "modern");
						break;
						case 891:
						c.getPA().startTeleport(2675, 9549, 0, "modern");
						break;
						case 125:
						c.getPA().startTeleport(2954, 3892, 0, "modern");
						break;
						case 1341:
						c.getPA().startTeleport(2452, 10152, 0, "modern");
						break;
						case 424:
						c.getPA().startTeleport(3422, 3541, 1, "modern");
						break;
						case 1543:
						c.getPA().startTeleport(3442, 3554, 2, "modern");
						break;
						case 11:
						c.getPA().startTeleport(3440, 3566, 2, "modern");
						break;
						case 2919:
						c.getPA().startTeleport(1748, 5328, 0, "modern");
						break;
						case 415:
						c.getPA().startTeleport(3419, 3568, 2, "modern");
						break;
						case 268:
						c.getPA().startTeleport(2897, 9800, 0, "modern");
						break;
						case 259:
						c.getPA().startTeleport(2833, 9824, 0, "modern");
						break;
						case 1432:
						c.getPA().startTeleport(2864, 9775, 0, "modern");
						break;
						case 135:
						c.getPA().startTeleport(2857, 9840, 0, "modern");
						break;
						case 4005:
						c.getPA().startTeleport(2907, 9692, 0, "modern");
						break;
						case 247:
						c.getPA().startTeleport(2698, 9512, 0, "modern");
						break;
						case 924:
						c.getPA().startTeleport(3105, 9949, 0, "modern");
						break;
						case 274:
						c.getPA().startTeleport(2712, 9432, 0, "modern");
						break;
						case 50:
						case 3200:
						c.sendMessage("Use the boss teleports for this task.");
						break;
						case 2745:
						case 2167:
						c.sendMessage("Use the minigames teleports for this task.");
						break;
						case 9467:
						c.getPA().startTeleport(2521, 4646, 0, "modern");
						break;
						case 9465:
						c.getPA().startTeleport(2521, 4646, 0, "modern");
						break;
						case 9463:
						c.getPA().startTeleport(2521, 4646, 0, "modern");
						break;
						default:
						c.sendMessage("Could not find a teleportation location.");
						break;
					}
			
	}
	public void useOperate(int itemId) {
		Optional<DegradableItem> d = DegradableItem.forId(itemId);
		if (d.isPresent()) {
			Degrade.checkPercentage(c, itemId);
			return;
		}
		switch (itemId) {
		case 4202:
			startSlayerTeleport();
			break;
		case 11283:
			DragonfireShieldEffect dfsEffect = new DragonfireShieldEffect();
			if (c.npcIndex <= 0 && c.playerIndex <= 0) {
				return;
			}
			if (dfsEffect.isExecutable(c)) {
				Damage damage = new Damage(Misc.random(25));
				if (c.playerIndex > 0) {
					Player target = PlayerHandler.players[c.playerIndex];
					if (Objects.isNull(target)) {
						return;
					}
					c.attackTimer = 7;
					dfsEffect.execute(c, target, damage);
					c.setLastDragonfireShieldAttack(System.currentTimeMillis());
				} else if (c.npcIndex > 0) {
					NPC target = NPCHandler.npcs[c.npcIndex];
					if (Objects.isNull(target)) {
						return;
					}
					c.attackTimer = 7;
					dfsEffect.execute(c, target, damage);
					c.setLastDragonfireShieldAttack(System.currentTimeMillis());
				}
			}
			break;

		case 1712:
		case 1710:
		case 1708:
		case 1706:
			c.getPA().handleGlory(itemId);
			c.itemUsing = itemId;
			c.isOperate = true;
			break;
		case 2552:
		case 2554:
		case 2556:
		case 2558:
		case 2560:
		case 2562:
		case 2564:
		case 2566:
			c.getPA().startTeleport(3362, 3263, 0, "modern");
			break;
		}
	}

	public void getSpeared(int otherX, int otherY, int distance) {
		int x = c.absX - otherX;
		int y = c.absY - otherY;
		int xOffset = 0;
		int yOffset = 0;
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERACTION) {
			c.sendMessage("You cannot use this special whilst in the duel arena.");
			return;
		}
		if (x > 0) {
			if (Region.getClipping(c.getX() + distance, c.getY(), c.heightLevel, 1, 0)) {
				xOffset = distance;
			}
		} else if (x < 0) {
			if (Region.getClipping(c.getX() - distance, c.getY(), c.heightLevel, -1, 0)) {
				xOffset = -distance;
			}
		}
		if (y > 0) {
			if (Region.getClipping(c.getX(), c.getY() + distance, c.heightLevel, 0, 1)) {
				yOffset = distance;
			}
		} else if (y < 0) {
			if (Region.getClipping(c.getX(), c.getY() - distance, c.heightLevel, 0, -1)) {
				yOffset = -distance;
			}
		}
		moveCheck(xOffset, yOffset, distance);
		c.lastSpear.reset();
	}

	public void moveCheck(int x, int y, int distance) {
		PathFinder.getPathFinder().findRoute(c, c.getX() + x, c.getY() + y, true, 1, 1);
	}

	public void resetTzhaar() {
		c.waveId = -1;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
	}

	public boolean checkForPlayer(int x, int y) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				if (p.getX() == x && p.getY() == y)
					return true;
			}
		}
		return false;
	}

	public void checkPouch(int i) {
		if (i < 0)
			return;
		c.sendMessage("This pouch has " + c.pouches[i] + " rune ess in it.");
	}

	public void fillPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > c.getItems().getItemAmount(1436)) {
			toAdd = c.getItems().getItemAmount(1436);
		}
		if (toAdd > c.POUCH_SIZE[i] - c.pouches[i])
			toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > 0) {
			c.getItems().deleteItem(1436, toAdd);
			c.pouches[i] += toAdd;
		}
	}

	public void emptyPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.pouches[i];
		if (toAdd > c.getItems().freeSlots()) {
			toAdd = c.getItems().freeSlots();
		}
		if (toAdd > 0) {
			c.getItems().addItem(1436, toAdd);
			c.pouches[i] -= toAdd;
		}
	}

	/*
	 * public void fixAllBarrows() { int totalCost = 0; int cashAmount =
	 * c.getItems().getItemAmount(995); for (int j = 0; j <
	 * c.playerItems.length; j++) { boolean breakOut = false; for (int i = 0; i
	 * < c.getItems().brokenBarrows.length; i++) { if (c.playerItems[j]-1 ==
	 * c.getItems().brokenBarrows[i][1]) { if (totalCost + 80000 > cashAmount) {
	 * breakOut = true; c.sendMessage("You have run out of money."); break; }
	 * else { totalCost += 80000; } c.playerItems[j] =
	 * c.getItems().brokenBarrows[i][0]+1; } } if (breakOut) break; } if
	 * (totalCost > 0) c.getItems().deleteItem(995,
	 * c.getItems().getItemSlot(995), totalCost); }
	 */

	public void handleLoginText() {
		c.getPA().sendFrame126("PK Hotspots", 1300);
		c.getPA().sendFrame126("Teleport to a PK hotspot", 1301);
		c.getPA().sendFrame126("Minigame Teleports", 1325);
		c.getPA().sendFrame126("Teleport to a minigame", 1326);
		c.getPA().sendFrame126("Boss Teleports", 1350);
		c.getPA().sendFrame126("Teleport to a boss", 1351);
		c.getPA().sendFrame126("Skilling Area", 1382);
		c.getPA().sendFrame126("Teleport to skilling areas", 1383);
		c.getPA().sendFrame126("Monster Teleports", 1415);
		c.getPA().sendFrame126("Teleports you to various monsters", 1416);
		c.getPA().sendFrame126("Training Teleport", 1454);
		c.getPA().sendFrame126("Teleports you to Rock Crabs", 1455);
		c.getPA().sendFrame126("PK Hotspots", 13037);
		c.getPA().sendFrame126("Teleport to a PK hotspot", 13038);
		c.getPA().sendFrame126("Minigame Teleports", 13047);
		c.getPA().sendFrame126("Teleport to a minigame", 13048);
		c.getPA().sendFrame126("Boss Teleports", 13055);
		c.getPA().sendFrame126("Teleport to a boss", 13056);
		c.getPA().sendFrame126("Skilling Area", 13063);
		c.getPA().sendFrame126("Teleport to skilling areas", 13064);
		c.getPA().sendFrame126("Monster Teleports", 13071);
		c.getPA().sendFrame126("Teleports you to various monsters", 13072);
		c.getPA().sendFrame126("Training Teleport", 13081);
		c.getPA().sendFrame126("Teleports you to Rock Crabs", 13082);


		/*
		 * c.getPA().sendFrame126("Ardougne Teleport", 1415);
		 * c.getPA().sendFrame126("", 1416);
		 * c.getPA().sendFrame126("Watchtower Teleport", 1454);
		 * c.getPA().sendFrame126("", 1455);
		 * c.getPA().sendFrame126("Dareevak Teleport", 13071);
		 * c.getPA().sendFrame126("", 13072);
		 * c.getPA().sendFrame126("Trollheim Teleport", 7457);
		 * c.getPA().sendFrame126("", 7458);
		 * c.getPA().sendFrame126("Ghorrock Teleport", 13097);
		 * c.getPA().sendFrame126("", 13098);
		 * c.getPA().sendFrame126("Annakarl Teleport", 13089);
		 * c.getPA().sendFrame126("", 13090);
		 * c.getPA().sendFrame126("Carrallanger Teleport", 13081);
		 * c.getPA().sendFrame126("", 13082); * //modern
		 * c.getPA().sendFrame126("Teleport name", 1300); //varrock
		 * c.getPA().sendFrame126("Description", 1301); //varrock description
		 * c.getPA().sendFrame126("Teleport name", 1325); //lumbridge
		 * c.getPA().sendFrame126("Description", 1326); //lumbridge description
		 * c.getPA().sendFrame126("Teleport name", 1350); //falador
		 * c.getPA().sendFrame126("Description", 1351); //falador description
		 * c.getPA().sendFrame126("Teleport name", 1382); //camelot
		 * c.getPA().sendFrame126("Description", 1383); //camelot description
		 * c.getPA().sendFrame126("Teleport name", 1415); //ardougne
		 * c.getPA().sendFrame126("Description", 1416); //ardougne description
		 * c.getPA().sendFrame126("Teleport name", 1454); //watchtower
		 * c.getPA().sendFrame126("Description", 1455); //watchtower description
		 * c.getPA().sendFrame126("Teleport name", 7457); //trollheim
		 * c.getPA().sendFrame126("Description", 7458); //trollheim description
		 * c.getPA().sendFrame126("Teleport name", 18472); //ape atoll
		 * c.getPA().sendFrame126("Description", 18473); //ape atoll description
		 * 
		 * //ancients c.getPA().sendFrame126("Teleport name", 13037); //paddewwa
		 * c.getPA().sendFrame126("Monster Teleport", 13038); //paddewwa
		 * description c.getPA().sendFrame126("Teleport name", 13047);
		 * //senntisten c.getPA().sendFrame126("Description", 13048);
		 * //senntisten description c.getPA().sendFrame126("Teleport name",
		 * 13055); //kharyll c.getPA().sendFrame126("Description", 13056);
		 * //kharyll description c.getPA().sendFrame126("Teleport name", 13063);
		 * //lassar c.getPA().sendFrame126("Description", 13064); //lassar
		 * description c.getPA().sendFrame126("Teleport name", 13071);
		 * //dareeyak c.getPA().sendFrame126("Description", 13072); //dareeyak
		 * description c.getPA().sendFrame126("Teleport name", 13081);
		 * //carrallanger c.getPA().sendFrame126("Description", 13082);
		 * //carralanger description c.getPA().sendFrame126("Teleport name",
		 * 13089); //annakarl c.getPA().sendFrame126("Description", 13090);
		 * //annakarl description c.getPA().sendFrame126("Teleport name",
		 * 13097); //ghorrock c.getPA().sendFrame126("Description", 13098);
		 * //ghorrock description
		 */
	}

	/*
	 * public void handleLoginText() {
	 * c.getPA().sendFrame126("Training Teleport", 13037);
	 * c.getPA().sendFrame126("Duel Arena", 13047);
	 * c.getPA().sendFrame126("Boss Teleport", 13055);
	 * c.getPA().sendFrame126("Wilderness Teleport", 13063);
	 * c.getPA().sendFrame126("Ardougne Teleport", 13071);
	 * c.getPA().sendFrame126("Training Teleport", 1300);
	 * c.getPA().sendFrame126("Duel Arena", 1325);
	 * c.getPA().sendFrame126("Boss Teleport", 1350);
	 * c.getPA().sendFrame126("Wilderness Teleport", 1382);
	 * c.getPA().sendFrame126("Cities Teleport", 1415); }
	 */

	public void handleWeaponStyle() {
		if (c.fightMode == 0) {
			c.getPA().sendFrame36(43, c.fightMode);
		} else if (c.fightMode == 1) {
			c.getPA().sendFrame36(43, 3);
		} else if (c.fightMode == 2) {
			c.getPA().sendFrame36(43, 1);
		} else if (c.fightMode == 3) {
			c.getPA().sendFrame36(43, 2);
		}
	}

	/**
	 * 
	 * @author Jason MacKeigan (http://www.rune-server.org/members/jason)
	 * @date Sep 26, 2014, 12:57:42 PM
	 */
	public enum PointExchange {
		PK_POINTS, VOTE_POINTS;
	}

	/**
	 * Exchanges all items in the player owners inventory to a specific to
	 * whatever the exchange specifies. Its up to the switch statement to make
	 * the conversion.
	 * 
	 * @param pointVar
	 *            the point exchange we're trying to make
	 * @param itemId
	 *            the item id being exchanged
	 * @param exchangeRate
	 *            the exchange rate for each item
	 */
	public void exchangeItems(PointExchange pointVar, int itemId, int exchangeRate) {
		try {
			int amount = c.getItems().getItemAmount(itemId);
			String pointAlias = Misc.ucFirst(pointVar.name().toLowerCase().replaceAll("_", " "));
			if (exchangeRate <= 0 || itemId < 0) {
				throw new IllegalStateException();
			}
			if (amount <= 0) {
				c.getDH().sendStatement("You do not have the items required to exchange", "for " + pointAlias + ".");
				c.nextChat = -1;
				return;
			}
			int exchange = amount * exchangeRate;
			c.getItems().deleteItem2(itemId, amount);
			switch (pointVar) {
			case PK_POINTS:
				c.pkp += exchange;
				break;

			case VOTE_POINTS:
				c.votePoints += exchange;
				break;
			}
			c.getDH().sendStatement("You exchange " + amount + " tickets for " + exchange + " " + pointAlias + ".");
			c.nextChat = -1;
		} catch (IllegalStateException exception) {
			Misc.println("WARNING: Illegal state has been reached.");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Sends some information to the client about screen fading. 
	 * @param text		the text that will be displayed in the center of the screen
	 * @param state		the state should be either 0, -1, or 1. 
	 * @param seconds	the amount of time in seconds it takes for the fade
	 * to transition.
	 * <p>
	 * If the state is -1 then the screen fades from black to transparent.
	 * When the state is +1 the screen fades from transparent to black. If 
	 * the state is 0 all drawing is stopped.
	 */
	public void sendScreenFade(String text, int state, int seconds) {
		if (c == null || c.getOutStream() == null) {
			return;
		}
		if (seconds < 1 && state != 0) {
			throw new IllegalArgumentException("The amount of seconds cannot be less than one.");
		}
		c.getOutStream().createFrameVarSize(9);
		c.getOutStream().writeString(text);
		c.getOutStream().writeByte(state);
		c.getOutStream().writeByte(seconds);
		c.getOutStream().endFrameVarSize();
	}
	
	public void stillCamera(int x, int y, int height, int speed, int angle) {
		c.outStream.createFrame(177);
		c.outStream.writeByte(x / 64);
		c.outStream.writeByte(y / 64);
		c.outStream.writeWord(height);
		c.outStream.writeByte(speed);
		c.outStream.writeByte(angle);
	}

	public void spinCamera(int i1, int i2, int i3, int i4, int i5) {
		c.outStream.createFrame(166);
		c.outStream.writeByte(i1);
		c.outStream.writeByte(i2);
		c.outStream.writeWord(i3);
		c.outStream.writeByte(i4);
		c.outStream.writeByte(i5);
	}

	public void resetCamera() {
		c.outStream.createFrame(107);
		c.updateRequired = true;
		c.appearanceUpdateRequired = true;
	}
}
