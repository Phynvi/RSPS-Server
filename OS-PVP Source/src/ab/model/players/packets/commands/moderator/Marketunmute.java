package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.database.PunishmentHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Market Unmute a given player.
 * 
 * @author Emiel
 */
public class Marketunmute implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			c2.marketMuteEnd = 0;
			c.sendMessage(c2.playerName + " has been unmuted on the market channel.");
			c2.sendMessage("@red@You have been unmuted by " + c.playerName + " on the market channel.");
			new PunishmentHandler().punishOnlinePlayer(c2, c, "Market Unmute", "");
		}
	}
}
