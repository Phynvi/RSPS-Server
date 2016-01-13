package ab.model.players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ab.model.items.GameItem;
import ab.model.items.ItemAssistant;
import ab.model.players.Player;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.util.Misc;
/**
 * Revamped a simple means of receiving a random item based on chance.
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class MysteryBox extends CycleEvent {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int MYSTERY_BOX = 6199;
	
	/**
	 * A map containing a List of {@link GameItem}'s that contain
	 * items relevant to their rarity.
	 */
	private static Map<Rarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity
	 * to the list
	 */
	static {
		items.put(Rarity.COMMON, Arrays.asList(
    			new GameItem(9005, 1),
    			new GameItem(12430, 1),
    			new GameItem(12319, 1),
    			new GameItem(12335, 1),
    			new GameItem(12430, 1),
    			new GameItem(12249, 1),
    			new GameItem(12749, 1),
    			new GameItem(6585, 1),
    			new GameItem(6737, 1),
    			new GameItem(6735, 1),
    			new GameItem(6731, 1),
    			new GameItem(6733, 1)
			)
		);
		items.put(Rarity.UNCOMMON, Arrays.asList(
				new GameItem(4566, 1),
				new GameItem(12428, 1),
				new GameItem(12439, 1),
				new GameItem(12397, 1),
				new GameItem(12412, 1),
				new GameItem(12357, 1),
				new GameItem(12351, 1),
				new GameItem(11834, 1),
				new GameItem(11832, 1),
				new GameItem(1419, 1),
				new GameItem(4084, 1),
				new GameItem(11235, 1),
				new GameItem(12696, 50),
				new GameItem(2572, 1),
				new GameItem(12752, 1)
			)
		);
		items.put(Rarity.RARE, Arrays.asList(
				new GameItem(11802, 1),
				new GameItem(11804, 1),
				new GameItem(11806, 1),
				new GameItem(11808, 1),
				new GameItem(11283, 1),
				new GameItem(1038, 1),
				new GameItem(1040, 1),
				new GameItem(1042, 1),
				new GameItem(1044, 1),
				new GameItem(1046, 1),
				new GameItem(1048, 1),
				new GameItem(1050, 1),
				new GameItem(1037, 1),
				new GameItem(12437, 1),
				new GameItem(12424, 1),
				new GameItem(12426, 1),
				new GameItem(12756, 1),
				new GameItem(12422, 1)
			)
		);
	}
	
	/**
	 * The player object that will be triggering this event
	 */
	private Player player;
	
	/**
	 * Constructs a new myster box to handle item receiving for this player
	 * and this player alone
	 * @param player the player
	 */
	public MysteryBox(Player player) {
		this.player = player;
	}
	
	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 * @param player the player triggering the evnet
	 */
	public void open() {
		if (System.currentTimeMillis() - player.lastMysteryBox < 600 * 4) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need atleast two free slots to open a mystery box.");
			return;
		}
		if (!player.getItems().playerHasItem(MYSTERY_BOX)) {
			player.sendMessage("You need a mystery box to do this.");
			return;
		}
		player.getItems().deleteItem(MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		CycleEventHandler.getSingleton().stopEvents(this);
		CycleEventHandler.getSingleton().addEvent(this, this, 2);
	}

	/**
	 * Executes the event for receiving the mystery box
	 */
	@Override
	public void execute(CycleEventContainer container) {
		if (player.disconnected || Objects.isNull(player)) {
			container.stop();
			return;
		}
		int pkp = 5 + Misc.random(50);
		int random = Misc.random(100);
		List<GameItem> itemList = random < 55 ? items.get(Rarity.COMMON)
				: random >= 55 && random <= 86 ? items.get(Rarity.UNCOMMON)
					: items.get(Rarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		player.getItems().addItem(2996, pkp);
		player.getItems().addItem(item.getId(), item.getAmount());
		player.sendMessage("You receive "+ item.getAmount()+ " "+ ItemAssistant.getItemName(item.getId())+ ", and " + Misc.insertCommas(Integer.toString(pkp)) + "PKP.");
		container.stop();
	}
	
	/**
	 * Represents the rarity of a certain list of items
	 * @author Jason MacKeigan
	 * @date Oct 29, 2014, 1:53:29 PM
	 */
	enum Rarity {
		UNCOMMON, COMMON, RARE
	}
	
}