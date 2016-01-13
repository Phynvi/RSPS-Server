package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Opens the game rule page in the default web browser.
 * 
 * @author Emiel
 */
public class Rules implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("http://os-pvp.org/forums/showthread.php?165", 12000);
	}
}
