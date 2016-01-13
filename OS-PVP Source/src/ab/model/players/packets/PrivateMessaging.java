package ab.model.players.packets;

import java.util.Objects;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.PlayerHandler;
import ab.model.players.PlayerSave;
import ab.util.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215,
			CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74, ADD_IGNORE = 133;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		switch (packetType) {

		case ADD_FRIEND:
			c.getFriends().add(c.getInStream().readQWord());
			break;

		case SEND_PM:
			if (System.currentTimeMillis() < c.muteEnd) {
	            c.sendMessage("You are muted for breaking a rule.");
				return;
			}
			c.muteEnd = 0;
			final long recipient = c.getInStream().readQWord();
			int pm_message_size = packetSize - 8;	
			final byte pm_chat_message[] = new byte[pm_message_size];
			c.getInStream().readBytes(pm_chat_message, pm_message_size, 0);
			c.getFriends().sendPrivateMessage(recipient, pm_chat_message);
			if (Objects.nonNull(PlayerHandler.getPlayerByLongName(recipient)) && Objects.nonNull(c)) {
				System.out.println(c.playerName + " PM: " + Misc.decodeMessage(pm_chat_message, pm_chat_message.length));
				Server.getChatLogHandler().logMessage(c, "Private Message", PlayerHandler.getPlayerByLongName(recipient).playerName, 
						Misc.decodeMessage(pm_chat_message, pm_chat_message.length));
			}
			break;

		case REMOVE_FRIEND:
			c.getFriends().remove(c.getInStream().readQWord());
			PlayerSave.saveGame(c);
			break;

		case REMOVE_IGNORE:
			c.getIgnores().remove(c.getInStream().readQWord());
			break;

		case CHANGE_PM_STATUS:
			c.getInStream().readUnsignedByte();
			c.setPrivateChat(c.getInStream().readUnsignedByte());
			c.getInStream().readUnsignedByte();
			c.getFriends().notifyFriendsOfUpdate();
			break;

		case ADD_IGNORE:
			c.getIgnores().add(c.getInStream().readQWord());
			break;

		}

	}
}
