package ab.model.content.dialogue.impl;

import ab.Config;
import ab.model.content.dialogue.Dialogue;
import ab.model.content.dialogue.DialogueAssistant.Expression;
import ab.model.content.dialogue.DialogueAssistant.Type;
import ab.model.minigames.bounty_hunter.BountyHunterEmblem;
import ab.model.npcs.NPCHandler;
import ab.util.Misc;

/**
 * A dialogue for the Emblem Trader NPC.
 * @author Chris
 * @date Aug 19, 2015 7:48:20 PM
 *
 */
public class EmblemTraderDialogue extends Dialogue {
	
	private static final int NPC_ID = 315;

	@Override
	protected void start(Object... parameters) {
		send(Type.PLAYER, Expression.CALM_TALK2, "Hey..?");
		phase = 0;
	}
	
	@Override
	public void next() {
		if (phase == 0) {
			send(Type.NPC, Expression.HAPPY_JOYFUL, "Hello, wanderer.");
			for (BountyHunterEmblem e : BountyHunterEmblem.EMBLEMS) {
				if (player.getItems().playerHasItem(e.getItemId())) {
					phase = 12;
					break;
				}
			}
			phase = 1;
		} else if (phase == 1) {
			send(Type.NPC, NPC_ID, Expression.ANGRY1, "Don't supposed you've come across any strange...",
					  "emblems along your journey?");
			phase = 2;
		} else if (phase == 2) {
			send(Type.PLAYER, Expression.CALM_TALK2, "Not that I've seen.");
			phase = 3;
		} else if (phase == 3) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "If you do, please do let me know. I'll reward you", 
					   "handsomely.");
			phase = 4;
		} else if (phase == 4) {
			send(Type.CHOICE, null, "What rewards have you got?", "Can I have a PK skull please?", "That's nice.");
		} else if (phase == 12) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "I see you have something valuable on your person.", 
													   "Certain.... ancient emblems, you see.");
			phase = 13;
		} else if (phase == 13) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "I'll happily take those off of your hands for a handsome",
													   "fee.");
			phase = 14;
		} else if (phase == 14) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "All of your emblems are worth a total of "+Misc.insertCommas(Integer.toString(player.getBH().getNetworthForEmblems())), "Bounty points.");
			phase = 15;
		} else if (phase == 15) {
			send(Type.CHOICE, "Sell all Emblems?", "Yes", "No");
		} else if (phase == 16) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "Thank you. My master in the north will be very", "pleased.");
			phase = 17;
		} else if (phase == 17) {
			stop();
		}
	}
	
	@Override
	public void select(int index) {
		if (phase == 15) {
			if (index == 1) {
				int worth = player.getBH().getNetworthForEmblems();
				long total = (long) worth + player.getBH().getBounties();
				if (total > Integer.MAX_VALUE) {
					player.sendMessage("You have to spend some bounties before obtaining any more.");
					stop();
				}
				if (worth > 0) {
					BountyHunterEmblem.EMBLEMS.forEach(emblem -> player.getItems().deleteItem2(emblem.getItemId(),
							player.getItems().getItemAmount(emblem.getItemId())));
					player.getBH().setBounties(player.getBH().getBounties() + worth);
					player.sendMessage("You sold all of the emblems in your inventory for "+Misc.insertCommas(Integer.toString(worth)) +" bounties.");
					phase = 16;
				} else {
					stop();
				}
			} else {
				stop();
			}
		} else if (phase == 4) {
			
		}
	}

}
