package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Teleport a given player to the player who issued the command.
 * 
 * @author Emiel
 */
public class Teletome implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.teleportToX = c.absX;
			c2.teleportToY = c.absY;
			c2.heightLevel = c.heightLevel;
			c.sendMessage("You have teleported " + c2.playerName + " to you.");
			c2.sendMessage("You have been teleported to " + c.playerName + "");
		} else {
			c.sendMessage(input + " is offline. You can only teleport online players.");
		}
	}
}