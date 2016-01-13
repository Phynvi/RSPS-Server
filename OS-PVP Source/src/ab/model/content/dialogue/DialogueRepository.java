package ab.model.content.dialogue;

import java.util.HashMap;
import java.util.Map;

import ab.model.content.dialogue.impl.*;
import ab.model.players.packets.commands.Command;

/**
 * A repository containing server dialogues.
 * @author Chris
 * @date Aug 19, 2015 10:39:08 AM
 * @see {@link Dialogue}
 *
 */
public class DialogueRepository {

	/**
	 * A <code>Map</code> of all included dialogues.
	 */
	private static final Map<String, Dialogue> dialogues;
	
	/**
	 * Populates the {@link DialogueRepository} map with {@link Dialogue}s.
	 * Syntax: dialogues.put("man_dialogue", new ManDialogue());
	 */
	static {
		dialogues = new HashMap<>();
		dialogues.put("rick_dialogue", new RickDialogue());
		dialogues.put("emblem_trader_dialogue", new EmblemTraderDialogue());
		dialogues.put("rotten_potato_peel", new PeelOptionDialogue());
	}

	/**
	 * Gets the <code>Map</code> of all dialogues.
	 * @return	dialogues	the dialogue map
	 */
	public static Map<String, Dialogue> getDialogues() {
		return dialogues;
	}

	/**
	 * Gets the dialogue for the specified string literal key.
	 * @param name	the name of the dialogue
	 * @return	the dialogue value associated with the specified key.
	 */
	static Dialogue getDialogue(final String name) {
		return dialogues.get(name);
	}

}