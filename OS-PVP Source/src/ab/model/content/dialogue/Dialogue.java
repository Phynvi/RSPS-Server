package ab.model.content.dialogue;

import ab.model.content.dialogue.DialogueAssistant.Expression;
import ab.model.content.dialogue.DialogueAssistant.Type;
import ab.model.npcs.NPCDefinitions;
import ab.model.npcs.NPCHandler;
import ab.model.players.Player;

/**
 * 
 * @author Erik Eide
 * @modified Chris
 * @version 1.0
 * @date Aug 19, 2015 11:31:52 AM
 * 
 * <p>A {@link Dialogue} is a set of conversation between an NPC and the 
 * player. When committing new dialogues to the repository, the user must note
 * that the {@link Dialogue} object is case insensitive, therefore naming conventions
 * are laxed, however the string literal should be a lowercase representation of the
 * name of the <code>NPC</code> separated by underscores and must include the suffix '_dialogue'.</p>
 *
 */
public abstract class Dialogue {

	protected static final String DEFAULT_OPTION_TITLE = "Select an Option";

	protected Player player;
	protected short phase = 0;

	public void finish() {
	}

	protected void input(int value) {
	}

	protected void input(String value) {
	}

	protected void next() {
		stop();
	}

	protected void select(int index) {
	}

	/**
	 * Sends the dialogue.
	 * @param type	the type of dialogue to send
	 * @param parameters	several parameters
	 */
	protected void send(Type type, Object... parameters) {
		if (type == Type.CHOICE) {
			if (parameters.length == 3) {
				player.getPA().sendFrame126(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2460);
				player.getPA().sendFrame126((String) parameters[1], 2461);
				player.getPA().sendFrame126((String) parameters[2], 2462);
				player.getPA().sendFrame164(2459);
			} else if (parameters.length == 4) {
				player.getPA().sendFrame126(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2470);
				player.getPA().sendFrame126((String) parameters[1], 2471);
				player.getPA().sendFrame126((String) parameters[2], 2472);
				player.getPA().sendFrame126((String) parameters[3], 2473);
				player.getPA().sendFrame164(2469);
			} else if (parameters.length == 5) {
				player.getPA().sendFrame126(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2481);
				player.getPA().sendFrame126((String) parameters[1], 2482);
				player.getPA().sendFrame126((String) parameters[2], 2483);
				player.getPA().sendFrame126((String) parameters[3], 2484);
				player.getPA().sendFrame126((String) parameters[4], 2485);
				player.getPA().sendFrame164(2480);
			} else if (parameters.length == 6) {
				player.getPA().sendFrame126(parameters[0] != null ? (String) parameters[0] : DEFAULT_OPTION_TITLE, 2493);
				player.getPA().sendFrame126((String) parameters[1], 2494);
				player.getPA().sendFrame126((String) parameters[2], 2495);
				player.getPA().sendFrame126((String) parameters[3], 2496);
				player.getPA().sendFrame126((String) parameters[4], 2497);
				player.getPA().sendFrame126((String) parameters[5], 2498);
				player.getPA().sendFrame164(2492);
			} else {
				throw new IllegalArgumentException("Invalid Arguments");
			}
		} else if (type == Type.ITEM) {
			if (parameters.length == 3) {
				player.getPA().sendFrame126((String) parameters[1], 4884);
				player.getPA().sendFrame126((String) parameters[2], 4885);
				player.getPA().sendFrame246(4883, 250, (Integer) parameters[0]);
				player.getPA().sendFrame164(4882);
			} else if (parameters.length == 4) {
				player.getPA().sendFrame126((String) parameters[1], 4889);
				player.getPA().sendFrame126((String) parameters[2], 4890);
				player.getPA().sendFrame126((String) parameters[3], 4891);
				player.getPA().sendFrame246(4888, 250, (Integer) parameters[0]);
				player.getPA().sendFrame164(4887);
			} else if (parameters.length == 5) {
				player.getPA().sendFrame126((String) parameters[1], 4895);
				player.getPA().sendFrame126((String) parameters[2], 4896);
				player.getPA().sendFrame126((String) parameters[3], 4897);
				player.getPA().sendFrame126((String) parameters[4], 4898);
				player.getPA().sendFrame246(4894, 250, (Integer) parameters[0]);
				player.getPA().sendFrame164(4893);
			} else if (parameters.length == 6) {
				player.getPA().sendFrame126((String) parameters[1], 4902);
				player.getPA().sendFrame126((String) parameters[2], 4903);
				player.getPA().sendFrame126((String) parameters[3], 4904);
				player.getPA().sendFrame126((String) parameters[4], 4905);
				player.getPA().sendFrame126((String) parameters[5], 4906);
				player.getPA().sendFrame246(4901, 250, (Integer) parameters[0]);
				player.getPA().sendFrame164(4900);
			}
		} else if (type == Type.NPC) {
			if (parameters.length == 3) {
				player.getPA().sendFrame126(NPCDefinitions.get((Integer) parameters[0]).getNpcName(), 4884);
				
				player.getPA().sendFrame126((String) parameters[2], 4885);
				player.getPA().sendFrame200(4883, ((Expression) parameters[1]).getEmoteId());
				player.getPA().sendFrame75((Integer) parameters[0], 4883);
				player.getPA().sendFrame164(4882);
			} else if (parameters.length == 4) {
				player.getPA().sendFrame126(NPCDefinitions.get((Integer) parameters[0]).getNpcName(), 4889);
				player.getPA().sendFrame126((String) parameters[2], 4890);
				player.getPA().sendFrame126((String) parameters[3], 4891);
				player.getPA().sendFrame200(4888, ((Expression) parameters[1]).getEmoteId());
				player.getPA().sendFrame75((Integer) parameters[0], 4888);
				player.getPA().sendFrame164(4887);
			} else if (parameters.length == 5) {
				player.getPA().sendFrame126(NPCDefinitions.get((Integer) parameters[0]).getNpcName(), 4895);
				player.getPA().sendFrame126((String) parameters[2], 4896);
				player.getPA().sendFrame126((String) parameters[3], 4897);
				player.getPA().sendFrame126((String) parameters[4], 4898);
				player.getPA().sendFrame200(4894, ((Expression) parameters[1]).getEmoteId());
				player.getPA().sendFrame75((Integer) parameters[0], 4894);
				player.getPA().sendFrame164(4893);
			} else if (parameters.length == 6) {
				player.getPA().sendFrame126(NPCDefinitions.get((Integer) parameters[0]).getNpcName(), 4902);
				player.getPA().sendFrame126((String) parameters[2], 4903);
				player.getPA().sendFrame126((String) parameters[3], 4904);
				player.getPA().sendFrame126((String) parameters[4], 4905);
				player.getPA().sendFrame126((String) parameters[5], 4906);
				player.getPA().sendFrame200(4901, ((Expression) parameters[1]).getEmoteId());
				player.getPA().sendFrame75((Integer) parameters[0], 4901);
				player.getPA().sendFrame164(4900);
			} else {
				throw new InternalError();
			}
		} else if (type == Type.PLAYER) {
			if (parameters.length == 2) {
				player.getPA().sendFrame126(player.playerName, 970);
				player.getPA().sendFrame126((String) parameters[1], 971);
				player.getPA().sendFrame200(969, ((Expression) parameters[0]).getEmoteId());
				player.getPA().sendFrame185(969);
				player.getPA().sendFrame164(968);
			} else if (parameters.length == 3) {
				player.getPA().sendFrame126(player.playerName, 975);
				player.getPA().sendFrame126((String) parameters[1], 976);
				player.getPA().sendFrame126((String) parameters[2], 977);
				player.getPA().sendFrame200(974, ((Expression) parameters[0]).getEmoteId());
				player.getPA().sendFrame185(974);
				player.getPA().sendFrame164(973);
			} else if (parameters.length == 4) {
				player.getPA().sendFrame126(player.playerName, 981);
				player.getPA().sendFrame126((String) parameters[1], 982);
				player.getPA().sendFrame126((String) parameters[2], 983);
				player.getPA().sendFrame126((String) parameters[3], 984);
				player.getPA().sendFrame200(980, ((Expression) parameters[0]).getEmoteId());
				player.getPA().sendFrame185(980);
				player.getPA().sendFrame164(979);
			} else if (parameters.length == 5) {
				player.getPA().sendFrame126(player.playerName, 988);
				player.getPA().sendFrame126((String) parameters[1], 989);
				player.getPA().sendFrame126((String) parameters[2], 990);
				player.getPA().sendFrame126((String) parameters[3], 991);
				player.getPA().sendFrame126((String) parameters[4], 992);
				player.getPA().sendFrame200(987, ((Expression) parameters[0]).getEmoteId());
				player.getPA().sendFrame185(987);
				player.getPA().sendFrame164(986);
			} else {
				throw new InternalError();
			}
		} else if (type == Type.STATEMENT) {
			if (parameters.length == 1) {
				player.getPA().sendFrame126("Click here to continue", 358);
				player.getPA().sendFrame126((String) parameters[0], 357);
				player.getPA().sendFrame164(356);
			} else if (parameters.length == 2) {
				player.getPA().sendFrame126("Click here to continue", 362);
				player.getPA().sendFrame126((String) parameters[0], 360);
				player.getPA().sendFrame126((String) parameters[1], 361);
				player.getPA().sendFrame164(359);
			} else if (parameters.length == 5) {
				player.getPA().sendFrame126((String) parameters[0], 6180);
				player.getPA().sendFrame126((String) parameters[1], 6181);
				player.getPA().sendFrame126((String) parameters[2], 6182);
				player.getPA().sendFrame126((String) parameters[3], 6183);
				player.getPA().sendFrame126((String) parameters[4], 6184);
				player.getPA().sendFrame164(6179);
			} else {
				throw new InternalError();
			}
		} else if (type == Type.STRING_INT) {
			player.getOutStream().createFrame(219);
			player.getOutStream().createFrame(27);
		} else if (type == Type.STRING_TEXT) {
			player.getOutStream().createFrame(219);
			player.getOutStream().createFrame(187);
		} else {
			throw new InternalError();
		}
	}

	protected abstract void start(Object... parameters);

	protected final void stop() {
		player.getPA().removeAllWindows();
	}

}