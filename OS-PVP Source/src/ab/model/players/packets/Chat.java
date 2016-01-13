package ab.model.players.packets;

import ab.Connection;
import ab.Server;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.util.Misc;

/**
 * Chat
 **/
public class Chat implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.setChatTextEffects(c.getInStream().readUnsignedByteS());
		c.setChatTextColor(c.getInStream().readUnsignedByteS());
		c.setChatTextSize((byte) (c.packetSize - 2));
		c.inStream.readBytes_reverseA(c.getChatText(), c.getChatTextSize(), 0);
		if (System.currentTimeMillis() < c.muteEnd) {
            c.sendMessage("You are muted for breaking a rule.");
			return;
		}
		c.muteEnd = 0;
		Server.getChatLogHandler().logMessage(c, "Chat", "", Misc.decodeMessage(c.getChatText(), c.getChatTextSize()));
		System.out.println(c.playerName + " chat: " + Misc.decodeMessage(c.getChatText(), c.getChatTextSize()));
		ReportHandler.addText(c.playerName, c.getChatText(), packetSize - 2);
        if (!Connection.isMuted(c)) {
            c.setChatTextUpdateRequired(true);
        } else {
            c.sendMessage("You are muted for breaking a rule.");
            return;
        }
	}
}
