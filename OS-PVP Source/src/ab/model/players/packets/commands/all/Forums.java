package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Forums implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-pvp.org/forums", 12000);
	}
}
