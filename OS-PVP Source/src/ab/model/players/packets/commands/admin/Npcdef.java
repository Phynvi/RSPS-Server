package ab.model.players.packets.commands.admin;

/**
 * 
 */


import ab.model.npcs.NPCDefinitions;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * @author Chris
 * @date Aug 19, 2015 5:16:46 PM
 *
 */
public class Npcdef implements Command {
	
	@Override
	public void execute (Player player, String input) {
		String[] args = input.split(" ");
		final NPCDefinitions npc = NPCDefinitions.get(Integer.parseInt(args[0]));
		/*player.sendMessage("npcName: [" + npc.getNpcName() + "]");
		player.sendMessage("npcExamine: " + npc.getExamine());
		player.sendMessage("npcStandIndex: " + npc.getStandIndex());
		player.sendMessage("npcWalkIndex: " + npc.getWalkIndex());
		player.sendMessage("npcType: " + npc.type);*/
	}

}
