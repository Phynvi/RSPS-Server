package ab.model.players.packets.commands.owner;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Reload all shops.
 * 
 * @author Emiel
 *
 */
public class Reloadshops implements Command {

	@Override
	public void execute(Player c, String input) {
		Server.shopHandler = new ab.world.ShopHandler();
		c.sendMessage("@don2@[Load] Reloading @blu@Shop Config.cfg");
	}
}
