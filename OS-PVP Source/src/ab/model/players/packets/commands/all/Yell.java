package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Tells the player they need to be a donator to use this feature.
 * 
 * @author Emiel
 */
public class Yell implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("You need to donate to use this command. ::donate");
	}
}
