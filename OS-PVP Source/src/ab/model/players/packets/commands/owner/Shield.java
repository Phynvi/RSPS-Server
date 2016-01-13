package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * LOOK MOM! I'M A SHIELD!
 * 
 * @author Emiel
 */
public class Shield implements Command {

	@Override
	public void execute(Player c, String input) {
		if (c.isNpc && c.npcId2 == 336) {
			c.isNpc = false;
		} else {
			c.npcId2 = 336;
			c.isNpc = true;
		}
		c.updateRequired = true;
		c.appearanceUpdateRequired = true;
	}
}
