package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Opens the store page in the default web browser.
 * 
 * @author Emiel
 */
public class Store implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().sendFrame126("www.os-pvp.org/home/store.html", 12000);
	}
}
