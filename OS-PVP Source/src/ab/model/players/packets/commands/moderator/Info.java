package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Shows the IP and Mac address of a given player.
 * 
 * @author Emiel
 */
public class Info implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c.sendMessage("IP of " + c2.playerName + " : " + c2.connectedFrom);
			c.sendMessage("Mac Address of " + c2.playerName + " : " + c2.getMacAddress());
		} else {
			c.sendMessage(input + " is not line. You can request the info of online players.");
		}
	}
}
