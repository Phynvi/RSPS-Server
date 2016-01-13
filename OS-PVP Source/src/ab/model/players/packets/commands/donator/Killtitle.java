package ab.model.players.packets.commands.donator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Changes the title of the player to their default kill title.
 * 
 * @author Emiel
 */
public class Killtitle implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("You will now be using your kill title instead. Relog for changes to take effect.");
		c.keepTitle = false;
		c.killTitle = true;
	}
}
