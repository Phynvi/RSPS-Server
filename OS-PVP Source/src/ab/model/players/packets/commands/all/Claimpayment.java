package ab.model.players.packets.commands.all;

import ab.database.DonationQuery;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;
import ab.util.MadTurnipConnection;

/**
 * Checks if the player has unclaimed donations.
 * 
 * @author Emiel
 *
 */
public class Claimpayment implements Command {

	@Override
	public void execute(Player player, String input) {
		player.sendMessage("Checking the database for unclaimed donations...");
			MadTurnipConnection.addDonateItems(player, player.playerName);
	}
}
