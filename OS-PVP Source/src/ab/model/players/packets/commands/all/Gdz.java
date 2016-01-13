package ab.model.players.packets.commands.all;

import ab.Config;
import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleport the player to the Greater Demon Zone.
 * 
 * @author Emiel
 */
public class Gdz implements Command {

	@Override
	public void execute(Player c, String input) {
		if (!Config.PLACEHOLDER_ECONOMY) {
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (c.inWild() || c.inCamWild()) {
				return;
			}
			c.getPA().spellTeleport(3280, 3878, 0);
		}
	}
}
