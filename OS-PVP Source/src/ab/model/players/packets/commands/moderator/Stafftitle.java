package ab.model.players.packets.commands.moderator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Changes the title of the player to their default staff title.
 * 
 * @author Emiel
 */
public class Stafftitle implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("You will now get your staff title instead. Relog for changes to take effect.");
		c.keepTitle = false;
		c.killTitle = false;
	}
}