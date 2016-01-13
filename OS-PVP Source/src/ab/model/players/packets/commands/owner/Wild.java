package ab.model.players.packets.commands.owner;

import ab.Config;
import ab.model.content.WildernessDitch;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Debug the wilderness boolean.
 * @author Chris
 * @date Aug 12, 2015 1:23:48 PM
 *
 */
public class Wild implements Command {

	@Override
	public void execute(Player c, String input) {
		c.sendMessage("inWilderness: " + (c.getY() == 3523 ? true : false));
		c.sendMessage("loc=[absX: " + c.getX() + " absY:" + c.getY() + "]");
		c.sendMessage("bh_active: " + Config.BOUNTY_HUNTER_ACTIVE);
	}
}
