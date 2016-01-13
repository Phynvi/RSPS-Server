package ab.model.players.packets.commands.moderator;

import java.util.List;
import java.util.stream.Collectors;

import ab.Connection;
import ab.Server;
import ab.database.PunishmentHandler;
import ab.model.multiplayer_session.MultiplayerSession;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Ban a given IP.
 * 
 * @author Emiel
 */
public class Banip implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split("-");
			if (args.length != 2) {
				throw new IllegalArgumentException();
			}
			String ipToBan = args[0];
			String reason = args[1];
			Connection.addIpToBanList(ipToBan);
			Connection.addIpToFile(ipToBan);

			List<Player> clientList = PlayerHandler.getPlayers().stream().filter(player -> player.connectedFrom.equals(ipToBan))
					.collect(Collectors.toList());
			for (Player c2 : clientList) {
				if (!Connection.isIpBanned(c2.connectedFrom)) {
					Connection.addNameToBanList(ipToBan, Long.MAX_VALUE);
					Connection.addNameToFile(ipToBan, Long.MAX_VALUE);
					if (Server.getMultiplayerSessionListener().inAnySession(c2)) {
						MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c2);
						session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					}
					c2.disconnected = true;
					c.sendMessage("You have IP banned the user: " + c2.playerName + " with the host: " + c2.connectedFrom);
				}

			}
			c.sendMessage("You have successfully banned the IP: " + ipToBan);
		new PunishmentHandler().punishOfflinePlayer(ipToBan, c, "Ban IP", reason);
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::banip-ip-reason");
		}
	}
}
