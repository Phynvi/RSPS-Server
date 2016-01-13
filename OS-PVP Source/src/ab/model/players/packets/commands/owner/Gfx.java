package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Open a specific interface.
 * 
 * @author Emiel
 *
 */
public class Gfx implements Command {

	@Override
	public void execute(Player c, String input) {
		c.gfx0(Integer.parseInt(input));
	}
}
