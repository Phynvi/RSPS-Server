package ab.model.players.packets;

import ab.model.players.Player;
import ab.model.players.PacketType;

/**
 * Dialogue
 **/
public class Dialogue implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {

		if (c.nextChat > 0) {
			c.getDH().sendDialogues(c.nextChat, c.talkingNpc);
		} else {
			c.getDH().sendDialogues(0, -1);
		}
		
		if (c.dialogue().isActive()) {
			c.dialogue().next();
		}

	}

}
