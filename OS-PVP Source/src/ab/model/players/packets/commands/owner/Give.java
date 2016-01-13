package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Gives the player the item specified in the command arguments.
 * @author Chris
 *
 */
public class Give implements Command {
	
	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split(" ");
			if (args.length == 2) {
				int newItemID = (args[0].equalsIgnoreCase("coins") ? 995 : c.getItems().getItemId(args[0]));
				int newItemAmount = Integer.parseInt(args[1]);
				if ((newItemID <= 20500) && (newItemID >= 0)) {
					c.getItems().addItem(newItemID, newItemAmount);
				}
			} else if (args.length == 3) {
				String itemName = args[0] + " " + args[1];
				int newItemID = c.getItems().getItemId(itemName);
				int newItemAmount = Integer.parseInt(args[2]);
				if ((newItemID <= 20500) && (newItemID >= 0)) {
					c.getItems().addItem(newItemID, newItemAmount);
				}
			} else if (args.length == 5) {
				String itemName = args[0] + " " + args[1] + " " + args[2];
				int newItemID = c.getItems().getItemId(itemName);
				int newItemAmount = Integer.parseInt(args[3]);
				if ((newItemID <= 20500) && (newItemID >= 0)) {
					c.getItems().addItem(newItemID, newItemAmount);
				}
			} else if (args.length == 6) {
				String itemName = args[0] + " " + args[1] + " " + args[2] + " " + args[3];
				int newItemID = c.getItems().getItemId(itemName);
				int newItemAmount = Integer.parseInt(args[4]);
				if ((newItemID <= 20500) && (newItemID >= 0)) {
					c.getItems().addItem(newItemID, newItemAmount);
				}
			}
		} catch (Exception e) {
			c.sendMessage("Invalid item name or syntax!");
			c.sendMessage("Try this - ::give itemname amount");
		}
	}

}
