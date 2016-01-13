package ab.model.players.packets.commands.owner;

import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.Rights;
import ab.model.players.packets.commands.Command;

/**
 * Promote a given player to a Moderator.
 * 
 * @author Emiel
 *
 */
public class Givemod implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.setRights(Rights.MODERATOR);
			c.sendMessage("You've promoted the user:  " + c2.playerName + " IP: " + c2.connectedFrom);
			c2.disconnected = true;
		} else {
			c.sendMessage(input + " is not online. You can only promote online players.");
		}
	}
}
