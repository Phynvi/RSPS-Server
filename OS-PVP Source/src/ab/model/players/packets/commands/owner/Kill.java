package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.combat.Hitmark;
import ab.model.players.packets.commands.Command;

/**
 * Kill a player.
 * 
 * @author Emiel
 */
public class Kill implements Command {

	@Override
	public void execute(Player c, String input) {
		Player player = PlayerHandler.getPlayer(input);
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		player.appendDamage(player.playerLevel[3], Hitmark.HIT);
		player.getPA().refreshSkill(3);
		player.sendMessage("You have been merked by " + c.playerName + ".");
	}
}
