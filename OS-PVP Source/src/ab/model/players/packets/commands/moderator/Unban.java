package ab.model.players.packets.commands.moderator;

import ab.Connection;
import ab.database.PunishmentHandler;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Unbans a given player.
 * 
 * @author Emiel
 */
public class Unban implements Command {

	@Override
	public void execute(Player c, String input) {
		Connection.removeNameFromBanList(input);
		c.sendMessage(input + " has been unbanned.");
		new PunishmentHandler().punishOfflinePlayer(input, c, "Unban", "");
	}
}
