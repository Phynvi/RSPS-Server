package ab.model.players.packets.commands.admin;



import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ab.Connection;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Mac ban a player.
 * 
 * @author Emiel
 */
public class Macban implements Command {

	@Override
	public void execute(Player c, String input) {
		if (isMacAddress(input)) {
			banAddress(c, input);
		} else {
			banPlayer(c, input);
		}
	}
	
	public void banPlayer(Player c, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (c2.getMacAddress().isEmpty()) {
				c.sendMessage("The players mac address is empty and therefor cannot be added to the list.");
				c.sendMessage("This happens when the client cannot determine the player address during login.");
				c.sendMessage("You are going to have to consider another possible means of action.");
				return;
			}
			if (Connection.isMacBanned(c2.getMacAddress())) {
				c.sendMessage("This player is already mac banned, they shouldn't be online.");
				c.sendMessage("Consider another possible means of action.");
				return;
			}
			Connection.addNameToBanList(c2.playerName, Long.MAX_VALUE);
			Connection.addMacBan(c2.getMacAddress());
			c.sendMessage(c2.playerName + " has been mac banned with the address: " + c2.getMacAddress() + ".");
			c2.disconnected = true;		
			c2.properLogout = true;
		} else {
			c.sendMessage(input + " is offline. Try '::macban macaddress' instead to ban offline players.");			
		}
	}
	
	public void banAddress(Player c, String input) {
		Connection.addMacBan(input);
		c.sendMessage("Mac address: " + input + " has been banned.");
	}
	
	public boolean isMacAddress(String input) {
		return input.length() == 17 && StringUtils.countMatches(input, "-") == 5;
	}
}
