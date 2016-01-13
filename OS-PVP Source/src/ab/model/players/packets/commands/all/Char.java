package ab.model.players.packets.commands.all;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleport the player to the Make-over Mage.
 * 
 * @author Emiel
 */
public class Char implements Command {

	@Override
	public void execute(Player c, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.inWild() || c.inCamWild()) {
			return;
		}
		c.getPA().startTeleport(1, 2, 0, "modern");
	}
}
