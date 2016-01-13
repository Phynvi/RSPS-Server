package ab.model.players.skills.thieving;
import ab.Config;
import ab.Server;
import ab.model.items.GameItem;
import ab.model.items.ItemDefinition;
import ab.model.npcs.NPC;
import ab.model.players.Player;
import ab.model.players.skills.Skill;
import ab.util.Location3D;
import ab.util.Misc;
import ab.world.objects.GlobalObject;

/**
 * A representation of the thieving skill. Support for both object and npc actions
 * will be supported.
 * 
 * @author Jason MacKeigan
 * @date Feb 15, 2015, 7:12:14 PM
 */
public class Thieving {
	
	/**
	 * The managing player of this class
	 */
	private Player player;
	
	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;
	
	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 1_500L;
	
	/**
	 * The stealing animation
	 */
	private static final int ANIMATION = 881;
	
	/**
	 * Constructs a new {@link Thieving} object that manages interactions 
	 * between players and stalls, as well as players and non playable characters.
	 * 
	 * @param player	the visible player of this class
	 */
	public Thieving(final Player player) {
		this.player = player;
	}
	
	/**
	 * A method for stealing from a stall
	 * @param stall		the stall being stolen from
	 * @param objectId	the object id value of the stall
	 * @param location	the location of the stall
	 */
	public void steal(Stall stall, int objectId, Location3D location) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this.");
			return;
		}
		if (!Server.getGlobalObjects().exists(objectId, location.getX(), location.getY())) {
			player.sendMessage("The stall has been depleted.");
			return;
		}
		if (player.playerLevel[Skill.THIEVING.getId()] < stall.level) {
			player.sendMessage("You need a thieving level of " + stall.level + " to steal from this.");
			return;
		}
		if (Misc.random(100) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		player.turnPlayerTo(location.getX(), location.getY());
		if (Misc.random(stall.depletionProbability) == 0) {
			GlobalObject stallObj = Server.getGlobalObjects().get(objectId, location.getX(), location.getY(),
					location.getZ());
			if (stallObj != null) {
				Server.getGlobalObjects().add(new GlobalObject(634, location.getX(), location.getY(),
						location.getZ(), stallObj.getFace(), 10, 8, stallObj.getObjectId()));
			}
		}
		GameItem item = stall.item;
		ItemDefinition definition = ItemDefinition.forId(item.getId());
		int experience = stall.experience;
		player.startAnimation(ANIMATION);
		player.getItems().addItem(item.getId(), item.getAmount());
		player.getPA().addSkillXP(experience * Config.THIEVING_EXPERIENCE, Skill.THIEVING.getId());
		player.sendMessage("You steal a " + definition.getName() + " from the stall.");
		lastInteraction = System.currentTimeMillis();
	}
	
	/**
	 * A method for pick pocketing npc's
	 * @param pickpocket	the pickpocket type
	 * @param npc			the npc being pick pocketed
	 */
	public void steal(Pickpocket pickpocket, NPC npc) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this npc.");
			return;
		}
		if (player.playerLevel[Skill.THIEVING.getId()] < pickpocket.level) {
			player.sendMessage("You need a thieving level of " + pickpocket.level + " to steal from this npc.");
			return;
		}
		if (Misc.random(100) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		/**
		 * Incorporate chance for failure
		 */
		player.turnPlayerTo(npc.getX(), npc.getY());
		player.startAnimation(ANIMATION);
		GameItem item = pickpocket.items[Misc.random(pickpocket.items.length - 1)];
		player.getItems().addItem(item.getId(), item.getAmount());
		player.getPA().addSkillXP(pickpocket.experience * Config.THIEVING_EXPERIENCE, Skill.THIEVING.getId());
		lastInteraction = System.currentTimeMillis();
	}
	
	public enum Pickpocket {
		MAN(1, 8, new GameItem(995, 1000), new GameItem(995, 1250), new GameItem(995, 750)),
		FARMER(60, 65, new GameItem(5291), new GameItem(5292), new GameItem(5293), new GameItem(5294),
				new GameItem(5291), new GameItem(5292), new GameItem(5293), new GameItem(5294), new GameItem(5295));
		
		/**
		 * The level required to pickpocket
		 */
		private final int level;
		
		/**
		 * The experience gained from the pick pocket
		 */
		private final int experience;
		
		/**
		 * The list of possible items received from the pick pocket
		 */
		private final GameItem[] items;
		
		/**
		 * Creates a new pick-pocketable npc with level requirement and experience gained
		 * @param npcId			the id of the npc
		 * @param level			the level required to steal from
		 * @param experience	the experience gained from stealing
		 * @param item			the item obtained from stealing, if any
		 */
		private Pickpocket(int level, int experience, GameItem... items) {
			this.level = level;
			this.experience = experience;
			this.items = items;
		}
	}
	
	public enum Stall {
		CAKE(new GameItem(1893), 5, 16, 20),
		FUR(new GameItem(6814), 35, 36, 15),
		SILVER(new GameItem(1806), 50, 54, 10),
		GEM(new GameItem(1613), 75, 80, 10),
		WINE(new GameItem(1993), 90, 90, 10);
		
		/**
		 * The item received from the stall
		 */
		private final GameItem item;
		
		/**
		 * The experience gained in thieving from a single stall thieve
		 */
		private final int experience;
		
		/**
		 * The probability that the stall will deplete
		 */
		private final int depletionProbability;
		
		/**
		 * The level required to steal from the stall
		 */
		private final int level;
		
		/**
		 * Constructs a new {@link Stall} object with a single parameter, 
		 * {@link GameItem} which is the item received when interacted with.
		 * @param item	the item received upon interaction
		 */
		private Stall(GameItem item, int level, int experience, int depletionProbability) {
			this.item = item;
			this.level = level;
			this.experience = experience;
			this.depletionProbability = depletionProbability;
		}
	}

}
