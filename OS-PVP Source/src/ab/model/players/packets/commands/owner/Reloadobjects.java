package ab.model.players.packets.commands.owner;

import java.io.IOException;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

public class Reloadobjects implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			Server.getGlobalObjects().reloadObjectFile(c);
			c.sendMessage("The object file has been reloaded.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
