package ab.model.players.packets.commands.admin;



import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Empty the inventory of the player.
 * 
 * @author Emiel
 */
public class Empty implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getPA().removeAllItems();
		c.sendMessage("You empty your inventory.");
	}
}
