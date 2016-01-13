package ab.model.players.packets;

import java.util.Objects;

import ab.Server;
import ab.model.items.ItemDefinition;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.TeleportTablets;
import ab.util.Misc;

/**
 * Item Click 2 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemClick2 implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();

		if (!c.getItems().playerHasItem(itemId, 1))
			return;
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
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
		TeleportTablets.operate(c, itemId);
		ItemDefinition def = ItemDefinition.forId(itemId);
		switch (itemId) {
		case 11907:
		case 12899:
			int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
			c.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
			break;
		case 12926:
			def = ItemDefinition.forId(c.getToxicBlowpipeAmmo());
			c.sendMessage("The blowpipe has "+c.getToxicBlowpipeAmmoAmount()+" " + def.getName() + " and " + c.getToxicBlowpipeCharge() + " charge remaining.");
			break;
		case 12931:
			def = ItemDefinition.forId(c.getToxicBlowpipeAmmo());
			c.sendMessage("The serpentine helm has "+c.getSerpentineHelmCharge()+" charge remaining.");
			break;
		case 12904:
			def = ItemDefinition.forId(c.getToxicBlowpipeAmmo());
			c.sendMessage("The serpentine helm has "+c.getToxicStaffOfDeadCharge()+" charge remaining.");
			break;
		case 8901:
			c.getPA().assembleSlayerHelmet();
		break;
		case 11283:
		case 11285:
		case 11284:
			c.sendMessage("Your dragonfire shield currently has "+c.getDragonfireShieldCharge()+" charges.");
			break;
		case 4155:
			c.sendMessage("I currently have@blu@ "+c.slayerPoints+"@bla@ slayer points.");
			break;
	/*	case 15098:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(15088, 1);
			break;
		case 15088:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(15098, 1);
			break;*/
		case 11802:
			c.sendMessage("Dismantle has been disabled.");
			break;
		case 11804:
			c.sendMessage("Dismantle has been disabled.");
			break;
		case 11806:
			c.sendMessage("Dismantle has been disabled.");
			break;
		case 11808:
			c.sendMessage("Dismantle has been disabled.");
			break;
		default:
			if (c.getRights().isOwner())
				Misc.println(c.playerName + " - Item3rdOption: " + itemId);
			break;
		}

	}

}
