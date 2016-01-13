package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Spawn a specific Object.
 * 
 * @author Emiel
 *
 */
public class Object implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().object(Integer.parseInt(input), c.absX, c.absY, 0, 10);
	}
}
