package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Opens the highscores in the default web browser.
 * 
 * @author Emiel
 */
public class Highscores implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-pvp.org/highscores", 12000);
	}
}
