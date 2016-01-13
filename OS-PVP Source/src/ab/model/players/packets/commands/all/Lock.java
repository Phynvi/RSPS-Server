package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Prevents the player from gaining any experience.
 * 
 * @author Emiel
 */
public class Lock implements Command {

	@Override
	public void execute(Player c, String input) {
		if (c.expLock == false) {
			c.expLock = true;
			c.sendMessage("Your XP is now: @red@locked@bla@.");
		} else {
			c.expLock = false;
			c.sendMessage("Your XP is now: @gre@unlocked@bla@.");
		}
	}
}
