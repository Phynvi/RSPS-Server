package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Changepassword implements Command {

	@Override
	public void execute(Player c, String input) {
		if (input.length() > 20) {
			c.sendMessage("Passwords cannot contain more than 20 characters.");
			c.sendMessage("The password you tried had " + input.length() + " characters.");
			return;
		}
		if (input.contains("character-rights") || input.contains("[CHARACTER]")) {
			c.sendMessage("Your password contains illegal characters.");
			return;
		}
		c.playerPass = input;
		c.sendMessage("Your password is now: @red@" + c.playerPass);
	}
}
