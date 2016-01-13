package ab.model.players.packets.commands.all;

import ab.Config;
import ab.model.items.ItemAssistant;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;
import ab.util.Misc;

/**
 * Puts a given amount of the item in the player's inventory.
 * 
 * @author Emiel
 */
public class Item implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split(" ");		
			int newItemID = Integer.parseInt(args[0]);
			int newItemAmount = Misc.stringToInt(args[1]);
			if (args.length == 2) {
				if ((newItemAmount > 2000000000)) {
					c.sendMessage("You are not allowed to spawn that great of a value at once.");
					return;
				}
			
					c.getItems().addItem(newItemID, newItemAmount);
			}
			} catch (Exception e) {
			}
		}
	}