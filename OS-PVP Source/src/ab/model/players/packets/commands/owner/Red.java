package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Show a red skull above the player's head.
 * 
 * @author Emiel
 *
 */
public class Red implements Command {

	@Override
	public void execute(Player c, String input) {
		c.headIconPk = (1);
		c.getPA().requestUpdates();
	}
}
