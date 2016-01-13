package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.database.PunishmentHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Unmute a given player.
 * 
 * @author Emiel
 */
public class Umute implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.muteEnd = 0;
			c.sendMessage(c2.playerName + " has been unmuted.");
			c2.sendMessage("@red@You have been unmuted by " + c.playerName + ".");
			new PunishmentHandler().punishOnlinePlayer(c2, c, "Unmute", "");
		}
	}
}
