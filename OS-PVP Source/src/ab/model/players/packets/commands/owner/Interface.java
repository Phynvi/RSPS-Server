package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Open a specific interface.
 * 
 * @author Emiel
 *
 */
public class Interface implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			int a = Integer.parseInt(input);
			c.getPA().showInterface(a);
		} catch (Exception e) {
			c.sendMessage("::interface ####");
		}
	}
}
