package ab.model.players.packets.commands.admin;



import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Toggles whether the player will lose items on death or not.
 * 
 * @author Emiel
 */
public class Safemode implements Command {

	@Override
	public void execute(Player c, String input) {
		if (c.inSafemode()) {
			c.setSafemode(false);
			c.sendMessage("You now lose items on death again.");
		} else {
			c.setSafemode(true);
			c.sendMessage("You no longer lose items on death.");
		}
	}
}
