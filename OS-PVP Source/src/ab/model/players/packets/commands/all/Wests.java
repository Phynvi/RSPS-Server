package ab.model.players.packets.commands.all;

import ab.Config;
import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleport the player to wests.
 * 
 * @author Emiel
 */
public class Wests implements Command {

	@Override
	public void execute(Player c, String input) {
		if (!Config.PLACEHOLDER_ECONOMY) {
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.inWild() || c.inCamWild()) {
				return;
			}
			c.getPA().spellTeleport(2979, 3597, 0);
		}
	}
}
