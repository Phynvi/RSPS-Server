package ab.model.content.dialogue.impl;

import ab.Config;
import ab.model.content.dialogue.Dialogue;
import ab.model.content.dialogue.DialogueAssistant.Expression;
import ab.model.content.dialogue.DialogueAssistant.Type;
import ab.model.npcs.NPCHandler;
import ab.util.Misc;

/**
 * A dialogue for a Grand Exchange NPC.
 * @author Chris
 * @date Aug 19, 2015 11:51:04 AM
 *
 */
public class RickDialogue extends Dialogue {
	
	private static final int NPC_ID = 221;

	@Override
	protected void start(Object... parameters) {
		send(Type.NPC, NPC_ID, Expression.HAPPY_JOYFUL, "Greetings, " + Misc.formatPlayerName(player.playerName) + ", and might I be", 
													  "the first to welcome you to the Grand Exchange!");
		phase = 0;
	}
	
	@Override
	public void next() {
		if (phase == 0) {
			send(Type.PLAYER, Expression.HAPPY_JOYFUL, "Err.. thanks? What exactly is this 'Grand Exchange'?");
			phase = 1;
		} else if (phase == 1) {
			send(Type.STATEMENT, "Rick glares at you and gasps.");
			phase = 2;
		} else if (phase == 2) {
			send(Type.NPC, NPC_ID, Expression.ANGRY1, "Why, what is the Grand Exchange? It is only",
											  "soon to be the largest trading center in the land of", 
											   Config.SERVER_NAME + "! At the moment, we've got a few", 
											  "details to work out, but eventually, in a few months' time");
			phase = 3;
		} else if (phase == 3) {
			send(Type.NPC, NPC_ID, Expression.DEFAULT, "the interface- er, doors, to the Grand Exchange", 
													   "will be opened for good!");
			phase = 4;
			
		} else if (phase == 4) {
			send(Type.PLAYER, Expression.NEARLY_CRYING, "How will this 'Grand Exchange' work? And how do you",
													 "suppose my items will fit into that tiny booth over",
													 "there!?");
			phase = 5;
		} else if (phase == 5) {
			send(Type.NPC, NPC_ID, Expression.LAUGH2, "Ah, you outerlanders are quite comical. You won't be",
													  "putting any items into the booth! Simply hand over the",
													  "item to an Exchange clerk, and your offer will be sent!");
			phase = 6;
		} else if (phase == 6) {
			send(Type.PLAYER, Expression.BOWS_HEAD_SAD, "Oh... I suppose that makes enough sense.");
			phase = 7;
		} else if (phase == 7) {
			send(Type.NPC, NPC_ID, Expression.LAUGH4, "Indeed. For now, I presume dicers will lurk these streets",
													  "until the exchange has opened. Time will tell...");
			phase = 8;
		} else if (phase == 8) {
			send(Type.STATEMENT, "Rick rambles on, and you begin to move away casually.");
			phase = 9;
		} else if (phase == 9) {
			stop();
		}
	}
	
	@Override
	public void select(int index) {
		if (phase == 1) {
			if (index == 1) {
				player.getPA().openUpBank();
			} else {
				stop();
			}
		}
	}

}
