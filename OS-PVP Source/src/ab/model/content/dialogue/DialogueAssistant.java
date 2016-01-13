package ab.model.content.dialogue;

/**
 * An assistant class containing {@link Expression} and {@link Dialogue} types.
 * @author Chris
 * @date Aug 19, 2015 10:39:49 AM
 *
 */
public class DialogueAssistant {
	
	public enum Expression {
		
		HAPPY_JOYFUL(588),
		CALM_TALK1(589),
		CALM_TALK2(590),
		DEFAULT(591),
		EVIL1(592),
		EVIL2(593),
		EVIL3(594),
		ANNOYED(595),
		DISTRESSED(596),
		DISTRESSED2(597),
		ALMOST_CRYING(598),
		BOWS_HEAD_SAD(598),
		DRUNK_LEFT(600),
		DRUNK_RIGHT(601),
		NOT_INTERESTED(602),
		SLEEPY(603),
		PLAIN_EVIL(604),
		LAUGH1(605),
		LAUGH2(606),
		LAUGH3(607),
		LAUGH4(608),
		EVIL_LAUGH(609),
		SAD(610),
		MORE_SAD(611),
		ON_ONE_HAND(612),
		NEARLY_CRYING(613),
		ANGRY1(614),
		ANGRY2(615),
		ANGRY3(616),
		ANGRY4(617);
		;
		
		/**
		 * The emote ID of the expression.
		 */
		int emoteId;
		
		/**
		 * Constructs a new <code>Expression</code>.
		 * @param emoteId
		 */
		Expression(int emoteId) {
			this.emoteId = emoteId;
		}
		
		/**
		 * Gets the emote ID of the {@link Expression}.
		 * @return	emoteId	the emote id of the <code>Expression</code>
		 */
		public int getEmoteId() {
			return emoteId;
		}
		
	}
	
	public enum Type {
		
		CHOICE, ITEM, NPC, PLAYER, STATEMENT, STRING_INT, STRING_TEXT;
		
	}

}
