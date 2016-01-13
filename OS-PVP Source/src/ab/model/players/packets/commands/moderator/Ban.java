package ab.model.players.packets.commands.moderator;

import java.util.Optional;
import ab.Connection;
import ab.Server;
import ab.database.PunishmentHandler;
import ab.model.multiplayer_session.MultiplayerSession;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Ban a given player.
 * 
 * @author Emiel
 */
public class Ban implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split("-");
			if (args.length != 3) {
				throw new IllegalArgumentException();
			}
			String name = args[0];
			int duration = Integer.parseInt(args[1]);
			long banEnd;
			if (duration == 0) {
				banEnd = Long.MAX_VALUE;
			} else {
				banEnd = System.currentTimeMillis() + duration * 1000 * 60;
			}
			String reason = args[2];
			Connection.addNameToBanList(name, banEnd);
			Connection.addNameToFile(name, banEnd);

			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(name);
			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();
				if (c2.getRights().isBetween(2,  3)) {
					c.sendMessage("You cannot ban this player.");
					return;
				}
				if (Server.getMultiplayerSessionListener().inAnySession(c2)) {
					MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c2);
					session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				}
				c2.disconnected = true;
				if (duration == 0) {
					c.sendMessage(name + " has been permanently banned.");
					new PunishmentHandler().punishOnlinePlayer(c2, c, "Ban (Permanent)", reason);
				} else {
					c.sendMessage(name + " has been banned for " + duration + " minute(s).");
					new PunishmentHandler().punishOnlinePlayer(c2, c, "Ban (" + duration + ")", reason);
				}
				return;
			}
			if (duration == 0) {
				c.sendMessage(name + " has been permanently banned.");
				new PunishmentHandler().punishOfflinePlayer(name, c, "Ban (Permanent)", reason);
			} else {
				c.sendMessage(name + " has been banned for " + duration + " minute(s).");
			new PunishmentHandler().punishOfflinePlayer(name, c, "Ban (" + duration + ")", reason);
			}
		} catch (Exception e) {
			c.sendMessage("Correct usage: ::ban-player-duration-reason (0 as duration for permanent)");
		}
	}
}
