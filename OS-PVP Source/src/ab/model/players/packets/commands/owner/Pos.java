package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Pos implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("loc=[absX: " + c.absX + " absY:" + c.absY + " h:" + c.height + "]");
	}
}
