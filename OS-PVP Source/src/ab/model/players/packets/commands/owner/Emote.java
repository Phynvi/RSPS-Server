package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Force the player to perform a given emote.
 * 
 * @author Emiel
 *
 */
public class Emote implements Command {

	@Override
	public void execute(Player c, String input) {
		c.startAnimation(Integer.parseInt(input));
		c.getPA().requestUpdates();
	}
}
