package ab.model.players.packets.commands.owner;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Reload the item and price configs.
 * 
 * @author Emiel
 *
 */
public class Reloaditems implements Command {

	@Override
	public void execute(Player c, String input) {
		Server.itemHandler.loadItemList("item.cfg");
		Server.itemHandler.loadItemPrices("prices.txt");
		c.sendMessage("@don2@[Load] Reloading @blu@item.cfg@bla@ and @blu@prices.txt");
	}
}
