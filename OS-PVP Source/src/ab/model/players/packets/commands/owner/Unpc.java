package ab.model.players.packets.commands.owner;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Reverts the player to their original model & animation indices.
 * @author Chris
 * @date Aug 19, 2015 6:01:21 PM
 *
 */
public class Unpc implements Command {

	@Override
	public void execute(Player c, String input) {
		c.isNpc = false;
		c.playerStandIndex = 0x328;
		c.playerTurnIndex = 0x337;
		c.playerWalkIndex = 0x333;
		c.playerTurn180Index = 0x334;
		c.playerTurn90CWIndex = 0x335;
		c.playerTurn90CCWIndex = 0x336;
		c.playerRunIndex = 0x338;
		c.updateRequired = true;
		c.appearanceUpdateRequired = true;
	}
}
