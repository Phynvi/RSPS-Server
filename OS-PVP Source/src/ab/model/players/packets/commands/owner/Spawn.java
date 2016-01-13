package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;
import ab.util.Misc;

/**
 * Puts a given amount of the item in the player's inventory.
 * 
 * @author Emiel
 */
public class Spawn implements Command {

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		try {
			int itemId = Integer.parseInt(args[0]);
			int amount = Misc.stringToInt(args[1]);
			c.getItems().addItem(itemId, amount);
		} catch (NumberFormatException nfe) {
			c.sendMessage("Improper use of the command; '::spawn itemid amount'.");
		}
	}
}
