package ab.model.players.packets.commands.moderator;

import ab.database.Query;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Make a note for a given player.
 * 
 * @author Emiel
 */
public class Note implements Command {

	@Override
	public void execute(Player c, String input) {
		String command = input.replaceAll("'", "\\\\'");
		String[] args = command.split("-");
		if (args.length != 2) {
			throw new IllegalArgumentException();
		}
		String query = "INSERT INTO NOTES (PLAYER, MESSAGE) VALUES ('" + args[1] + "', '" + args[2] + "')";
		c.sendMessage("Successfully added a note for " + args[1]);
		new Query(query).execute();
	}
}
