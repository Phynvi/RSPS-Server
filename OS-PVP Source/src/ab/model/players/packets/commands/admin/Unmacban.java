package ab.model.players.packets.commands.admin;



import ab.Connection;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Remove the ban on a specific Mac address.
 * 
 * @author Emiel
 */
public class Unmacban implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			c.sendMessage("address: " + input);
			if (!Connection.isMacBanned(input)) {
				c.sendMessage("The address does not exist in the list, make sure it matches perfectly.");
				return;
			}
			Connection.removeMacBan(input);
			c.sendMessage("The mac ban on the address; " + input + " has been lifted.");
		} catch (IndexOutOfBoundsException exception) {
			c.sendMessage("Error. Correct syntax: ::unmacban address.");
		}
	}
}
