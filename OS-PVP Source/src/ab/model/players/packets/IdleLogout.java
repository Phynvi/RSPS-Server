package ab.model.players.packets;

import ab.model.players.Player;
import ab.model.players.PacketType;

public class IdleLogout implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		// if (!c.playerName.equalsIgnoreCase("Sanity"))
		// c.logout();
	}
}