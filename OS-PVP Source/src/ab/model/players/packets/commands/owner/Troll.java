package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * This one should be pretty self-explanatory >_<.
 * 
 * @author Emiel
 *
 */
public class Troll implements Command {

	@Override
	public void execute(Player c, String input) {
		PlayerHandler.executeGlobalMessage("Quick! The first person to type @blu@::i4akosa9fUcxzij8a@bla@ will recieve a santa hat!!");
	}
}
