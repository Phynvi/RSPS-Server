package ab.model.players.packets.commands.admin;



import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * A command to change the player's visibility.
 * @author Chris
 *
 */
public class Setvis implements Command {
	
	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		if (args.length != 1 || Integer.parseInt(args[0]) > 1) {
			c.sendMessage("Fool! You can't set your visibility higher than 2!");
			return;
		}
		c.setInvisible((Integer.parseInt(args[0]) == 1 && !c.isInvisible() ? true : false));
		c.getPA().requestUpdates();
		c.sendMessage("vis: " + args[0]);
	}

}
