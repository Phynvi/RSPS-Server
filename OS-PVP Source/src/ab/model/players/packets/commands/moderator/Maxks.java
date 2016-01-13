package ab.model.players.packets.commands.moderator;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Shows the player who has the highest killstreak.
 * 
 * @author Emiel
 */
public class Maxks implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> op = PlayerHandler.getPlayers().stream().filter(Objects::nonNull)
				.max(Comparator.comparing(client -> client.getKillstreak().getTotalKillstreak()));
		if (op.isPresent()) {
			c.sendMessage("Highest killstreak: " + op.get().playerName + " - " + op.get().getKillstreak().getTotalKillstreak());
		}
	}
}
