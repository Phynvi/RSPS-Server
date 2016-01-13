package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.Server;
import ab.database.PunishmentHandler;
import ab.event.CycleEventHandler;
import ab.model.players.Player;
import ab.model.players.ConnectedFrom;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class Kick implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			c2.outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(c2);
			c2.properLogout = true;
			ConnectedFrom.addConnectedFrom(c2, c2.connectedFrom);
			c.sendMessage("Kicked " + c2.playerName);
			new PunishmentHandler().punishOnlinePlayer(c2, c, "Kick", "");			
		} else {
			c.sendMessage(input + " is not online. You can only kick online players.");
		}
	}
}
