package ab.model.players.packets;

import java.util.Objects;
import java.util.Optional;

import ab.Server;
import ab.model.items.GameItem;
import ab.model.multiplayer_session.MultiplayerSession;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.trade.TradeSession;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.PlayerHandler;
import ab.model.players.skills.Skill;
import ab.model.players.skills.prayer.Bone;
import ab.model.players.skills.prayer.Prayer;

/**
 * Bank X Items
 **/
public class BankX2 implements PacketType {
	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int Xamount = c.getInStream().readDWord();
		if (Xamount < 0)// this should work fine
		{
			Xamount = c.getItems().getItemAmount(c.xRemoveId);
		}
		if (Xamount == 0) {
			Xamount = 1;
		}
		switch (c.xInterfaceId) {

		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
        		c.sendMessage("You cannot bank items whilst trading.");
        		return;
        	}
			if (!c.getItems().playerHasItem(c.xRemoveId, Xamount))
				return;
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST &&
					duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			c.getItems().addToBank(c.playerItems[c.xRemoveSlot] - 1, Xamount, true);
			break;
		case 5382:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
        		c.sendMessage("You cannot remove items from the bank whilst trading.");
        		return;
        	}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().removeItem(c.getBank().getCurrentBankTab().getItem(c.xRemoveSlot).getId() - 1, Xamount);
				return;
			}
			c.getItems().removeFromBank(c.getBank().getCurrentBankTab().getItem(c.xRemoveSlot).getId() - 1, Xamount, true);
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession) {
				session.addItem(c, new GameItem(c.xRemoveId, Xamount));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession) {
				session.removeItem(c, c.xRemoveSlot, new GameItem(c.xRemoveId, Xamount));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, c.xRemoveSlot, new GameItem(c.xRemoveId, Xamount));
			}
			break;
		}
		if (c.settingMin) {
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			c.diceMin = Xamount;
			c.settingMin = false;
			c.settingMax = true;
			c.getDH().sendDialogues(9998, -1);
			return;
		} else if (c.settingMax) {
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			c.diceMax = Xamount;
			c.settingMax = false;
			c.settingMin = false;
			c.getDH().sendDialogues(9999, -1);
			return;
		} else if (c.settingBet) {
			Player o = c.diceHost;
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			if (!c.getItems().playerHasItem(2996, Xamount)) {
				c.sendMessage("You need more tickets!");
				o.sendMessage("The other player needs more tickets.");
				c.getPA().removeAllWindows();
				o.getPA().removeAllWindows();
				return;
			}
			if (!o.getItems().playerHasItem(2996, Xamount)) {
				c.sendMessage("The other player needs more tickets.");
				o.sendMessage("You need more tickets!");
				c.getPA().removeAllWindows();
				o.getPA().removeAllWindows();
				return;
			}
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			if (Xamount > o.diceMax || Xamount < o.diceMin) {
				c.sendMessage("That bet is too big or too small.");
				c.sendMessage("Please bet between " + o.diceMin + " and " + o.diceMax);
				c.getPA().removeAllWindows();
				o.getPA().removeAllWindows();
				return;
			}
			c.betAmount = Xamount;
			c.getItems().deleteItem(2996, Xamount);
			o.getItems().deleteItem(2996, Xamount);
			PlayerHandler.players[c.otherDiceId].betAmount = Xamount;
			c.settingBet = false;
			c.settingMax = false;
			c.settingMin = false;
			c.getDH().sendDialogues(11002, -1);
			return;
		}
		if (c.attackSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 0;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.attackSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.defenceSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 1;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getCombat().resetPrayers();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.defenceSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.strengthSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 2;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.strengthSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.healthSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 3;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.healthSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.rangeSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 4;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.rangeSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.prayerSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				c.getCombat().resetPrayers();
				int skill = 5;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.prayerSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.mageSkill) {
			if (c.inWild() || c.inCamWild() || c.inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 6;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.mageSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.getPrayer().getAltarBone().isPresent()) {
			c.getPrayer().alter(Xamount);
			return;
		}
	}
}