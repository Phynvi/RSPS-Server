package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Teleport to a given player.
 * 
 * @author Emiel
 */
public class Xteleto implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c.getPA().movePlayer(c2.getX(), c2.getY(), c2.heightLevel);			
		} else {
			c.sendMessage(input + " is not line. You can only teleport to online players.");
		}
	}
}
