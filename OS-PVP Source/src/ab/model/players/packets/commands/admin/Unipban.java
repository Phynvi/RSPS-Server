package ab.model.players.packets.commands.admin;



import java.io.IOException;

import ab.Connection;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

public class Unipban implements Command {

	@Override
	public void execute(Player c, String input) {
		if (input.isEmpty()) {
			c.sendMessage("You must enter a valid IP address.");
			return;
		}
		if (!Connection.isIpBanned(input)) {
			c.sendMessage("This IP address is not listed as IP banned");
			return;
		}
		try {
			Connection.removeIpBan(input);
		} catch (IOException e) {
			c.sendMessage("The IP could not be successfully removed from the file.");
			return;
		}
		Connection.removeIpFromBanList(input);
		c.sendMessage("The IP '"+input+"' has been removed from the IP ban list.");
	}

}
