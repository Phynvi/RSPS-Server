package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Opens the help interface.
 * 
 * @author Emiel
 */
public class Help implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().closeAllWindows();
		c.getPA().showInterface(59525);
	}
}
