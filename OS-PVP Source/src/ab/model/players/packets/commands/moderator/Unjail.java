package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.Server;
import ab.database.PunishmentHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Unjails a given player.
 * 
 * @author Emiel
 */
public class Unjail implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);

		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			c2.teleportToX = 3093;
			c2.teleportToY = 3493;
			c2.jailEnd = 0;
			c2.sendMessage("You have been unjailed by " + c.playerName + ". Don't get jailed again!");
			c.sendMessage("Successfully unjailed " + c2.playerName + ".");
			new PunishmentHandler().punishOnlinePlayer(c2, c, "Unjail", "");
		} else {
			c.sendMessage(input + " is not online. Only online players can be unjailed.");
		}
	}
}
