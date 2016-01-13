package ab.model.players.packets.commands.moderator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Toggles whether the player will be visible or not.
 * 
 * @author Emiel
 */
public class Visibility implements Command {

	@Override
	public void execute(Player c, String input) {
		if (c.isInvisible()) {
			c.setInvisible(false);
			c.sendMessage("You are no longer invisible.");
		} else {
			c.setInvisible(true);
			c.sendMessage("You are now invisible.");
		}
		c.getPA().requestUpdates();
	}
}
