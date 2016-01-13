package ab.model.players.packets;

import java.util.Objects;

import ab.Config;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.PlayerHandler;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int tradeId = c.getInStream().readSignedWordBigEndian();
		Player requested = PlayerHandler.players[tradeId];
		c.getPA().resetFollow();
		if (!Config.ADMIN_ATTACKABLE && requested.getRights().isBetween(1, 4)) {
			c.sendMessage("You cannot trade with " + Config.SERVER_NAME + " administrators!");
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			c.sendMessage("You cannot trade whilst inside the duel arena.");
			return;
		}
		if (Objects.equals(requested, c)) {
			c.sendMessage("You cannot trade yourself.");
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (requested.getInterfaceEvent().isActive()) {
			c.sendMessage("That player needs to finish what they're doing.");
			return;
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}
	//	if (Server.serverlistenerPort != 5555) {
		if (c.getTrade().requestable(requested)) {
			c.getTrade().request(requested);
			return;
		}
		if(tradeId < 1)
            return;
		/*if (c.duelStatus >= 1 && c.duelStatus <= 4) {
			Client o = (Client) PlayerHandler.players[c.duelingWith];
			c.duelStatus = 0;
			o.duelStatus = 0;
			// c.sendMessage("@red@The challange has been declined.");
			// o.sendMessage("@red@Other player has declined the challange.");
			// Misc.println("trade reset");
			o.getTradeAndDuel().declineDuel();
			c.getTradeAndDuel().declineDuel();
			 return;
		}*/
	}

}
