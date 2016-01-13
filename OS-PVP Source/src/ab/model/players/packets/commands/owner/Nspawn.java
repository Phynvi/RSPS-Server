package ab.model.players.packets.commands.owner;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * Reload all NPCs.
 * 
 * @author Emiel
 *
 */
public class Nspawn implements Command {

	@Override
	public void execute(Player c, String input) {
		Server.npcHandler = null;
		Server.npcHandler = new ab.model.npcs.NPCHandler();
		PlayerHandler.executeGlobalMessage("[@red@" + c.playerName + "@bla@] " + "NPC Spawns have been reloaded.");
	}
}
