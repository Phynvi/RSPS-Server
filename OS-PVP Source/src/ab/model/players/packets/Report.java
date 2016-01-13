package ab.model.players.packets;

import ab.model.players.Player;
import ab.model.players.PacketType;

public class Report implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		try {
			ReportHandler.handleReport(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}