package ab.model.players.packets.commands.admin;



import ab.Connection;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Updates the list of all banned accounts.
 * 
 * @author Emiel
 */
public class Updatebans implements Command {

	@Override
	public void execute(Player c, String input) {
		Connection.resetIpBans();
	}
}
