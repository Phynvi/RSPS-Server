package ab.model.players.packets.commands.donator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleports the player to the donator zone.
 * 
 * @author Emiel
 */
public class Donatorzone implements Command {

	@Override
	public void execute(Player c, String input) {
		if (c.inTrade || c.inDuel || c.inWild()) {
			return;
		}
		c.getPA().startTeleport(3366, 9640, 0, "modern");
	}
}
