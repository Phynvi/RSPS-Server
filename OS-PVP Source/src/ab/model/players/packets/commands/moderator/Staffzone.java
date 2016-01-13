package ab.model.players.packets.commands.moderator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Teleport the player to the staffzone.
 * 
 * @author Emiel
 */
public class Staffzone implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().startTeleport(2912, 5475, 0, "modern");
	}
}
