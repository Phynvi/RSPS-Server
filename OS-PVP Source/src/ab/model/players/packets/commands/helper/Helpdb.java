package ab.model.players.packets.commands.helper;

import ab.model.content.help.HelpDatabase;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb implements Command {

	@Override
	public void execute(Player c, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}
}
