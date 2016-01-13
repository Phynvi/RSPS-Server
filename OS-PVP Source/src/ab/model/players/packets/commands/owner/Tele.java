package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleport the player to the given coordinates.
 * 
 * @author Emiel
 *
 */
public class Tele implements Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		if (args.length == 3) {
			c.getPA().movePlayer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (args.length == 2) {
			c.getPA().movePlayer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), c.heightLevel);
		}
	}
}
