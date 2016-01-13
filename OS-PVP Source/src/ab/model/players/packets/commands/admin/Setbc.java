package ab.model.players.packets.commands.admin;



import java.util.Arrays;
import java.util.Objects;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Broadcasts a new message to all online players.
 * @author Chris
 *
 */
public class Setbc implements Command {

	@Override
	public void execute(Player c, String input) {
		if (Server.UpdateServer) {
			c.sendMessage("You cannot execute a broadcast whilst an update is in progress!");
			return;
		}
		PlayerHandler.executeBroadcast(input);
	}
}
