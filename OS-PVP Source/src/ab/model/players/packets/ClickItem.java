package ab.model.players.packets;

import java.util.Optional;

import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.holiday.HolidayTool;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.DiceHandler;
import ab.model.players.PacketType;
import ab.model.players.PlayerAssistant;
import ab.model.players.TeleportTablets;
import ab.util.Misc;
import ab.Server;
import ab.model.players.combat.Hitmark;
import ab.model.players.skills.prayer.Bone;
import ab.model.players.skills.prayer.Prayer;

/**
 * Clicking an item, bury bone, eat food etc
 **/
public class ClickItem implements PacketType {
	
	public Player c;

	public ClickItem(Player c) {
		this.c = c;
	}

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int frame = c.getInStream().readSignedShortLittleEndianA(); // use to be
																	// readSignedWordBigEndianA();
		int itemSlot = c.getInStream().readSignedWordA(); // use to be
														  // readUnsignedWordA();
		int itemId = c.getInStream().readSignedWordBigEndian(); // us to be
																// unsigned.
		if (itemSlot >= c.playerItems.length || itemSlot < 0) {
			return;
		}
		if (itemId != c.playerItems[itemSlot] - 1) {
			return;
		}
		if (c.isDead || c.playerLevel[3] <= 0) {
			return;
		}
		if (Server.getHolidayController().clickItem(c, itemId)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		c.lastClickedItem = itemId;
		c.getHerblore().clean(itemId);
		if (c.getFood().isFood(itemId)) {
			c.getFood().eat(itemId, itemSlot);
		} else if (c.getPotions().isPotion(itemId)) {
			c.getPotions().handlePotion(itemId, itemSlot);
		}
		Optional<Bone> bone = Prayer.isOperableBone(itemId);
		if (bone.isPresent()) {
			c.getPrayer().bury(bone.get());
			return;
		}
		TeleportTablets.operate(c, itemId);
		if (itemId == 2379) {
			if (c.getPoisonDamage() > 0 || c.getVenomDamage() > 0) {
				c.sendMessage("You are poisoned or effected by venom, you should heal this first.");
				return;
			}
			if (c.playerLevel[c.playerHitpoints] == 1) {
				c.sendMessage("I better not do that.");
				return;
			}
			c.sendMessage("Wow, the rock exploded in your mouth. That looks like it hurt.");
			c.playerLevel[c.playerHitpoints] = 1;
			c.getPA().refreshSkill(c.playerHitpoints);
			c.getItems().deleteItem2(itemId, 1);
			return;
		}
		if (itemId == 9553) {
			c.getPotions().eatChoc(9553, -1, itemSlot, 1, true);
		}
		if (itemId == 12846) {
			c.getDH().sendDialogues(578, -1);
		}
		if (itemId == 12938) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			c.getPA().spellTeleport(2205, 3055, 0);
			c.sendMessage("<col=2600FF>You use your teleport scroll and arrive at Zul-Andra.");
			c.getItems().deleteItem(12938, 1);
		/*	if (c.getZulrahEvent().getInstancedZulrah() != null) {
				c.sendMessage("You are already in Zul-Andra! Please relog if this is incorrect.");
				return;
			}
			c.getDH().sendDialogues(625, -1);*/
			return;
		}
		if (itemId == 4155) {
			c.sendMessage("I currently have @blu@" + c.taskAmount + " " + Server.npcHandler.getNpcListName(c.slayerTask) + "@bla@ to kill.");
			c.getPA().closeAllWindows();
		}
		if (itemId == 2839) {
			if (c.slayerRecipe == true) {
				c.sendMessage("@blu@You have already learnt this recipe. You have no more use for this scroll.");
				return;
			}
			if (c.getItems().playerHasItem(2839)) {
			c.slayerRecipe = true;
			c.sendMessage("You have learnt the slayer helmet recipe. You can now assemble it");
			c.sendMessage("using a @blu@Black Mask@bla@, @blu@Facemask@bla@, @blu@Nose peg@bla@, @blu@Spiny helmet@bla@ and @blu@Earmuffs@bla@.");
		 	c.getItems().deleteItem(2839, 1);
			}
		}
		if (itemId == 11996) {
			HolidayTool.spawnRare(c);
		}
		if (itemId == DiceHandler.DICE_BAG) {
			DiceHandler.selectDice(c, itemId);
		}
		if (itemId > DiceHandler.DICE_BAG && itemId <= 15100) {
			if (System.currentTimeMillis() - c.diceDelay >= 5000) {
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("Nothing channel mate "
								+ Misc.ucFirst(c.playerName) + " rolled @red@"
								+ Misc.random(100)
								+ "@bla@ on the percentile dice.");
						c.diceDelay = System.currentTimeMillis();
					}
				}
			} else {
				c.sendMessage("You must wait 10 seconds to roll dice again.");
			}
		}
		if (itemId == 5733) {
			c.dialogue().start("rotten_potato_peel", c);
		}
		if (itemId == 2697) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2697, 1)) {
				c.getDH().sendDialogues(4000, -1);
			}
		}
		if (itemId == 2698) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() ||Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2698, 1)) {
				c.getDH().sendDialogues(4001, -1);
			}
		}
		if (itemId == 2699) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2699, 1)) {
				c.getDH().sendDialogues(4002, -1);
			}
		}
		if (itemId == 2700) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2700, 1)) {
				c.getDH().sendDialogues(4003, -1);
			}
		}
		if (itemId == 2701) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.getItems().playerHasItem(2701, 1)) {
				c.getDH().sendDialogues(4004, -1);
			}
		}
		if (itemId == 7509) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
				return;
			}
			if (c.getItems().playerHasItem(7509, 1)) {
				int damage = (c.playerLevel[3] - 10);
				c.startAnimation(829);
				c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
				c.getItems().deleteItem(7509, 1);
				c.forcedChat("Ouch! I nearly broke a tooth!");
				c.getPA().refreshSkill(3);
			}
		}
		if (itemId == 10269) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10269, 1)) {
				c.getItems().addItem(995, 30000);
				c.getItems().deleteItem(10269, 1);
			}
		}
		if (itemId == 952) {
			c.getBarrows().spadeDigging();
			return;
		}
		if (itemId == 10271) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10271, 1)) {
				c.getItems().addItem(995, 10000);
				c.getItems().deleteItem(10271, 1);
			}
		}
		if (itemId == 10273) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10273, 1)) {
				c.getItems().addItem(995, 14000);
				c.getItems().deleteItem(10273, 1);
			}
		}
		if (itemId == 10275) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10275, 1)) {
				c.getItems().addItem(995, 18000);
				c.getItems().deleteItem(10275, 1);
			}
		}
		if (itemId == 10277) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10277, 1)) {
				c.getItems().addItem(995, 22000);
				c.getItems().deleteItem(10277, 1);
			}
		}
		if (itemId == 10279) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				return;
			}
			if (c.getItems().playerHasItem(10279, 1)) {
				c.getItems().addItem(995, 26000);
				c.getItems().deleteItem(10279, 1);
			}
		}
		/* Mystery box */
		if (itemId == 6199)
			if (c.getItems().playerHasItem(6199)) {
				c.getMysteryBox().open();
				return;
			}
		if (itemId == 2714) { // Easy Clue Scroll Casket
			c.getItems().deleteItem(itemId, 1);
			PlayerAssistant.addClueReward(c, 0);
		}
		if (itemId == 2802) { // Medium Clue Scroll Casket
			c.getItems().deleteItem(itemId, 1);
			PlayerAssistant.addClueReward(c, 1);
		}
		if (itemId == 2775) { // Hard Clue Scroll Casket
			c.getItems().deleteItem(itemId, 1);
			PlayerAssistant.addClueReward(c, 2);
		}
		if (itemId == 2677) {
			int randomClue = Misc.random(11);
                        Achievements.increase(c, AchievementType.CASKET, 1);
			if (randomClue == 0) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2714, 1);
				c.sendMessage("You've recieved a easy clue scroll casket.");
			}
			if (randomClue == 0) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2714, 1);
				c.sendMessage("You've recieved a easy clue scroll casket.");
			}
			if (randomClue == 1) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2714, 1);
				c.sendMessage("You've recieved a easy clue scroll casket.");
			}
			if (randomClue == 2) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2714, 1);
				c.sendMessage("You've recieved a easy clue scroll casket.");
			}
			if (randomClue == 3) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2714, 1);
				c.sendMessage("You've recieved a easy clue scroll casket.");
			}
			if (randomClue == 4) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2802, 1);
				c.sendMessage("You've recieved a medium clue scroll casket.");
			}
			if (randomClue == 5) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2802, 1);
				c.sendMessage("You've recieved a medium clue scroll casket.");
			}
			if (randomClue == 6) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2802, 1);
				c.sendMessage("You've recieved a medium clue scroll casket.");
			}
			if (randomClue == 7) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2802, 1);
				c.sendMessage("You've recieved a medium clue scroll casket.");
			}
			if (randomClue == 8) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2802, 1);
				c.sendMessage("You've recieved a medium clue scroll casket.");
			}
			if (randomClue == 9) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2775, 1);
				c.sendMessage("You've recieved a hard clue scroll casket.");
			}
			if (randomClue == 10) {
				c.getItems().deleteItem(itemId, 1);
				c.getItems().addItem(2775, 1);
				c.sendMessage("You've recieved a hard clue scroll casket.");
			}
		}
		if (itemId == 2528) { c.usingLamp
			 = true; c.normalLamp = true; c.antiqueLamp = false;
			  c.sendMessage("You rub the lamp...");
			  c.getPA().showInterface(2808); 
			  }
		/*
		 * if (itemId == 4447) { c.usingLamp = true; c.antiqueLamp = true;
		 * c.normalLamp = false;
		 * c.sendMessage("You rub the antique lamp of 13 million experience..."
		 * ); c.getPA().showInterface(2808); } if (itemId == 2528) { c.usingLamp
		 * = true; c.normalLamp = true; c.antiqueLamp = false;
		 * c.sendMessage("You rub the lamp of 1 million experience...");
		 * c.getPA().showInterface(2808); }
		 */

		/*
		 * if (itemId >= 5509 && itemId <= 5514) { int pouch = -1; int a =
		 * itemId; if (a == 5509) pouch = 0; if (a == 5510) pouch = 1; if (a ==
		 * 5512) pouch = 2; if (a == 5514) pouch = 3;
		 * c.getPA().fillPouch(pouch); return; }
		 */
	}

}