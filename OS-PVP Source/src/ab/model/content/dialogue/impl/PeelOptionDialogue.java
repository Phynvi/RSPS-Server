package ab.model.content.dialogue.impl;

import ab.model.content.dialogue.Dialogue;
import ab.model.content.dialogue.DialogueAssistant.Type;
import ab.model.players.Player;

/**
 * A dialogue for the Rotten potato administrative tool.
 * @author Chris
 * @date Aug 19, 2015 6:47:57 PM
 *
 */
public class PeelOptionDialogue extends Dialogue {
	
	private static final String[] OP1 = { "Wipe my inventory", 
										  "Wipe my bank", 
										  "Teleport to player", 
										  "Teleport player to me",
										  "Next ->" };
	
	private static final String[] OP2 = { "Start an instance of Zulrah",
										  "Change my spellbook",
										  "Display commands",
										  "Take me to the moderator room",
										  "<- Back" };
	
	@Override
	protected void start(Object... parameters) {
		send(Type.CHOICE, "Op2", OP1[0], OP1[1], OP1[2], OP1[3], OP1[4]);
		phase = 0;
	}
	
	@Override
	public void select(int index) {
		System.out.println("Phase: " + phase + " index : " + index);
		if (phase == 0) { 
			if (index == 1) { 
				for (int i = 0; i < player.playerItems.length; i++) {
					if (player.playerItems[i] - 1 != 5733)
						player.getItems().deleteItem(player.playerItems[i] - 1, player.getItems().getItemSlot(player.playerItems[i] - 1), player.playerItemsN[i]);
				}
				player.sendMessage("Inventory items all gone!");
				player.getPA().closeAllWindows();
			} else if (index == 2) { 
				player.getBank().deleteAll();
				player.sendMessage("Bank wiped!");
				player.getPA().closeAllWindows();
			} else if (index == 3) { 
				player.getPA().closeAllWindows();
				player.getOutStream().createFrame(27);
			} else if (index == 4) { 
				player.getPA().closeAllWindows();
				player.getOutStream().createFrame(27);
			} else if (index == 5) { 
				send(Type.CHOICE, "Op2", OP2[0], OP2[1], OP2[2], OP2[3], OP2[4]);
				phase = 1;
			}
			if (index >= 1 && index < 3) {
				player.getPA().closeAllWindows();
			}
		} else if (phase == 1) {
			if (index == 1) { 
				player.sendMessage("Temporarily unavailable.");
				player.getPA().closeAllWindows();
			} else if (index == 2) { 
				player.sendMessage("Temporarily unavailable.");
				player.getPA().closeAllWindows();
			} else if (index == 3) { 
				send(Type.STATEMENT, "Available: setvar, god kick, ban, mute");
			} else if (index == 4) {
				player.getPA().spellTeleport(1866, 5348, 0);
				player.getPA().closeAllWindows();
			} else if (index == 5) { 
				send(Type.CHOICE, "Op2", OP1[0], OP1[1], OP1[2], OP1[3], OP1[4]);
				phase = 0;
			}
		} 
   }

}
