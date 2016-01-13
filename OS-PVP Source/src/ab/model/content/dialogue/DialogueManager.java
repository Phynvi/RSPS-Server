package ab.model.content.dialogue;

import ab.model.players.Player;

/**
 * A class that manages dialogues.
 * @author Erik Eide
 * @date Aug 19, 2015 11:07:16 AM
 *
 */
public class DialogueManager {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The current dialogue.
	 */
	private Dialogue dialogue = null;

	public DialogueManager(final Player player) {
		this.player = player;
	}

	public boolean input(final int value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public boolean input(final String value) {
		if (dialogue != null) {
			dialogue.input(value);
			return true;
		}

		return false;
	}

	public void interrupt() {
		if (dialogue != null) {
			dialogue.finish();
			dialogue = null;
			// player.getmessage("Dialogue stopped.");
		}
	}

	public boolean isActive() {
		return dialogue != null;
	}

	public boolean next() {
		if (dialogue != null) {
			dialogue.next();
			return true;
		}

		return false;
	}

	public boolean select(final int index) {
		if (dialogue != null) {
			dialogue.select(index);
			return true;
		}

		return false;
	}

	/**
	 * Starts a dialogue with a new dialogue block instead of repository.
	 *
	 * @param dialogue The dialogue to start for the player
	 * @param parameters Parameters to pass on to the dialogue
	 */
	public void start(Dialogue dialogue, Object... parameters) {
		this.dialogue = dialogue;
		if (dialogue != null) {
			dialogue.player = player;
			dialogue.start(parameters);
		} else {
			player.sendMessage("Invalid dialogue");
		}
	}

	/**
	 * Starts a dialogue from the repository.
	 * @param name	the name of the dialogue, as defined as the key of the dialogue map.
	 * @param parameters	the parameters of the dialogue
	 */
	public void start(String name, Object... parameters) {
		dialogue = DialogueRepository.getDialogue(name);
		if (dialogue == null) {
			player.sendDebugMessage("Invalid dialogue! Params: " + name);
			return;
		}
		dialogue.player = player;
		dialogue.start(parameters);
	}

}