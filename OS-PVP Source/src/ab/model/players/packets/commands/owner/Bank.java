package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Open the banking interface.
 * 
 * @author Emiel
 */
public class Bank implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().openUpBank();
	}
}
