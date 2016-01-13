package ab.model.players.packets;

import java.util.Objects;

import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.players.Player;
import ab.model.players.DiceHandler;
import ab.model.players.PacketType;
import ab.model.players.PlayerHandler;
import ab.util.Misc;
import ab.Server;

/**
 * Wear Item
 **/
public class WearItem implements PacketType {

	@SuppressWarnings("static-access")
	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.wearId = c.getInStream().readUnsignedWord();
		c.wearSlot = c.getInStream().readUnsignedWordA();
		c.interfaceId = c.getInStream().readUnsignedWordA();
		c.alchDelay = System.currentTimeMillis();
		if (!c.getItems().playerHasItem(c.wearId, 1)) {
			return;
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			c.sendMessage("You cannot remove items from your equipment whilst trading, trade declined.");
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		int oldCombatTimer = c.attackTimer;
		if ((c.playerIndex > 0 || c.npcIndex > 0) && c.wearId != 4153 && c.wearId != 12848 && !c.usingMagic && !c.usingBow
				&& !c.usingOtherRangeWeapons && !c.usingCross)
			c.getCombat().resetPlayerAttack();
		if (c.canChangeAppearance) {
			c.sendMessage("You can't wear an item while changing appearence.");
			return;
		}
		for (int arrows : c.ARROWS) {
			if (c.wearId == arrows) {
				if (c.rangeDelay > 0) {
					return;
				}
			}
		}
		for (int bowId : c.BOWS) {
			if (c.wearId == bowId) {
				c.switchDelay.reset();
			}
		}
		if (c.wearId > DiceHandler.DICE_BAG && c.wearId <= 15100) {
			if (System.currentTimeMillis() - c.diceDelay >= 5000) {
				int random;
					random = Misc.random(100);
			for (int j = 0; j < Server.playerHandler.players.length; j++) {
				if (Server.playerHandler.players[j] != null) {
					Player c2 = (Player) Server.playerHandler.players[j];
									if (c2.inDice()) {
							//c.forcedChat("[DICE] "+ Misc.ucFirst(c.playerName) + " rolled "+random+" on the percentile dice.");
							c2.sendMessage("[@red@DICE@bla@] @blu@"+ Misc.ucFirst(c.playerName) + " @bla@rolled @red@"+random+"@bla@ on the percentile dice.");	
							if (random < 55) {
							c2.sendMessage("[@red@DICE@bla@] The gambler @blu@"+ Misc.ucFirst(c.playerName) + " @bla@wins.");
							} else if (random == 55) {
							c2.sendMessage("[@red@DICE@bla@] It's a tie for gambler @blu@"+ Misc.ucFirst(c.playerName) +"@bla@. Please re-roll!");
							} else {
								c2.sendMessage("[@red@DICE@bla@] The gambler @blu@"+ Misc.ucFirst(c.playerName) + " @bla@loses. Please pay out the winnings!");
									}
									c.diceDelay = System.currentTimeMillis();
								}
				}
			}
							} else {
								if (c.inDice()) {
									c.sendMessage("Please wait 10 seconds to re-roll.");
								}
								return;
							}
							return;
						}
	/*	if (c.inDice() && c.wearId > DiceHandler.DICE_BAG && c.wearId <= 15100) {
			if (System.currentTimeMillis() - c.diceDelay >= 5000) {
				int roll = Misc.random(100);
				Server.getChatLogHandler().logMessage(c, "Dice roll", "", c.playerName + " rolled " + roll + " on the percentile dice.");
				for (Player player : PlayerHandler.players) {
					if (player.inDice()) {
						player.sendMessage("[@red@DICE@bla@] @blu@" + Misc.ucFirst(c.playerName) + " @bla@rolled @red@" + roll
								+ "@bla@ on the percentile dice.");
						if (roll < 55) {
							player.sendMessage("[@red@DICE@bla@] The gambler @blu@" + Misc.ucFirst(c.playerName) + " @bla@wins.");
						} else if (roll == 55) {
							player.sendMessage("[@red@DICE@bla@] It's a tie for gambler @blu@" + Misc.ucFirst(c.playerName)
									+ "@bla@. Please re-roll!");
						} else {
							player.sendMessage("[@red@DICE@bla@] The gambler @blu@" + Misc.ucFirst(c.playerName)
									+ " @bla@loses. Please pay out the winnings!");
						}
					}
					c.diceDelay = System.currentTimeMillis();
				}
			} else {
				if (c.inDice()) {
					c.sendMessage("Please wait 10 seconds to re-roll.");
				}
				return;
			}
			return;
		}*/

		if (c.wearId >= 5509 && c.wearId <= 5515) {
			int pouch = -1;
			int a = c.wearId;
			if (a == 5509)
				pouch = 0;
			if (a == 5510)
				pouch = 1;
			if (a == 5512)
				pouch = 2;
			if (a == 5514)
				pouch = 3;
			c.getPA().emptyPouch(pouch);
			return;
		}
		if (!Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.getItems().wearItem(c.wearId, c.wearSlot);
		}
	}

}
