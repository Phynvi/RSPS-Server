package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Give the player runes for the Vengeance spell.
 * 
 * @author Emiel
 *
 */
public class Veng implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("This command is not available at this time.");
	}
}
