package ab.model.players.packets.commands.owner;

import ab.Config;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Toggle the Cyber Monday sale on or off.
 * 
 * @author Emiel
 *
 */
public class Cyber implements Command {

	@Override
	public void execute(Player c, String input) {
		if (Config.CYBER_MONDAY) {
			Config.CYBER_MONDAY = false;
			c.sendMessage("CYBER MONDAY TOGGLED OFF");
		} else {
			Config.CYBER_MONDAY = true;
			c.sendMessage("CYBER MONDAY TOGGLED ON");
		}
	}
}
