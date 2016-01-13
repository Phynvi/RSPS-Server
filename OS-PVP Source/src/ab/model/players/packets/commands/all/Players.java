package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Tells the player how many players are online.
 * 
 * @author Emiel
 */
public class Players implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("There are currently @blu@" + PlayerHandler.getPlayerCount() + "@bla@ players online on @blu@OS PvP@bla@.");
	}
}
