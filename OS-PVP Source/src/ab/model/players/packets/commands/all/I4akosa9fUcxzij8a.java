package ab.model.players.packets.commands.all;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Really Abnant? Come on...
 * 
 * @author Emiel
 */
public class I4akosa9fUcxzij8a implements Command {

	@Override
	public void execute(Player c, String input) {
		c.getItems().addItem(4012, 1);
		c.sendMessage("@red@DAMN! You just got trolled hard by @blu@Ab@bla@!");
	}
}
