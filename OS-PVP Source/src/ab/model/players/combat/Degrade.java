package ab.model.players.combat;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import ab.model.items.ItemAssistant;
import ab.model.players.Player;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Nov 7, 2013
 */
public class Degrade {

	public static final int MAXIMUM_ITEMS = DegradableItem.values().length;
	
	public static enum DegradableItem {
		SARADOMIN_SWORD_BLESSED(12809, 12804, -1, 10_000),
		AMULET_OF_THE_DAMNED_FULL(12851, 12853, -1, 1),
		AMULET_OF_THE_DAMNED(12853, -1, -1, 7_500),
		VESTAS_CHAINBODY(13887, -1, 500_000, 1_000),
		VESTAS_PLATESKIRT(13893, -1, 450_000, 1_000),
		ZURIELS_ROBETOP(13858, -1, 450_000, 1_000),
		ZURIELS_ROBE_BOTTOM(13861, -1, 450_000, 1_000),
		ZURIELS_STAFF(13867, -1, 550_000, 1_000),
		ZURIELS_HOOD(13864, -1, 400_000, 1_000),
		MORRIGANS_LEATHER_BODY(13870, -1, 500_000, 1_000),
		MORRIGANS_LEATHER_CHAPS(13873, -1, 450_000, 1_000),
		MORRIGANS_LEATHER_COIF(13876, -1, 400_000, 1_000),
		STATIUS_PLATEBODY(13884, -1, 500_00, 1_000),
		STATIUS_PLATELEGS(13890, -1, 450_000, 1_000),
		STATIUS_FULL_HELM(13896, -1, 400_000, 1_000),
		STATIUS_WARHAMMER(13902, -1, 550_000, 1_000),
		VESTAS_LONGSWORD(13899, -1, 600_000, 1_000),
		VESTAS_SPEAR(13905, -1, 500_000, 1_000),
		TENTACLE_WHIP(15000, 15001, -1, 10_000);
		
		
		private int itemId, brokenId, cost, hits;
		
		DegradableItem(int itemId, int brokenId, int cost, int hits) {
			this.itemId = itemId;
			this.brokenId = brokenId;
			this.cost = cost;
			this.hits = hits;
		}
		
		public int getItemId() {
			return this.itemId;
		}
		
		public int getBrokenId() {
			return this.brokenId;
		}
		
		public int getCost() {
			return this.cost;
		}
		
		public int getHits() {
			return this.hits;
		}
		
		static Set<DegradableItem> DEGRADABLES = Collections.unmodifiableSet(EnumSet.allOf(DegradableItem.class));
		
		public static Optional<DegradableItem> forId(int itemId) {
			return DEGRADABLES.stream().filter(d -> d.itemId == itemId).findFirst();
		}
	}
	
	public static void degrade(Player player) {
		for(DegradableItem degradable : DegradableItem.DEGRADABLES) {
			if(player.getItems().isWearingItem(degradable.getItemId())) {
				String name = degradable.name().toLowerCase().replaceAll("_", " ");
				player.degradableItem[degradable.ordinal()]++;
				if(player.degradableItem[degradable.ordinal()] >= degradable.getHits()) {
					int slot = player.getItems().getWornItemSlot(degradable.getItemId());
					player.getItems().wearItem(degradable.getBrokenId(), 1, slot);
					player.sendMessage("Your "+name+" has degraded due to combat.", 0xFFCC00);
					if (degradable.cost > -1) {
						player.claimDegradableItem[degradable.ordinal()] = true;
						player.sendMessage("Talk to the wise old man in edgeville to get it back for a price.", 0xFFCC00);
					}
					player.degradableItem[degradable.ordinal()] = 0;
				}
			}
		}
	}
	
	public static void checkRemaining(Player player, int itemId) {
		for(DegradableItem degradable : DegradableItem.values()) {
			if(degradable.getItemId() == itemId) {
				player.sendMessage("Your "+ItemAssistant.getItemName(itemId)+" has "+
							(degradable.hits - player.degradableItem[degradable.ordinal()])+" hits remaining.", 255);
				break;
			}
		}
	}

	public static void checkPercentage(Player player, int clickedItem) {
		for(DegradableItem degradable : DegradableItem.values()) {
			if(degradable.getItemId() == clickedItem) {
				int percent = 100 - (player.degradableItem[degradable.ordinal()] / (degradable.getHits() / 100));
				player.sendMessage("Your "+ItemAssistant.getItemName(clickedItem)+" has "+percent+"% remaining.", 255);
				break;
			}
		}
	}
	
	/**
	 * Determines if an item is repairable under some standards. 
	 * @param player	the player repairing the item
	 * @param item		the item to be repaired
	 * @return			will return a DegradableItem object if the item is repairable
	 */
	public static boolean repair(Player player, int item) {
		Optional<DegradableItem> degradable = DegradableItem.forId(item);
		if (!degradable.isPresent()) {
			return false;
		}
		DegradableItem degraded = degradable.get();
		if (player.degradableItem[degraded.ordinal()] <= 0) {
			player.sendMessage("This item has not degraded at all, therefor it does not need to be fixed.");
			return false;
		}
		if (player.claimDegradableItem[degraded.ordinal()]) {
			player.sendMessage("This item has degrdaded and needs to be claimed. Talk to the old wise man.");
			return false;
		}
		double percent = (100 - (player.degradableItem[degraded.ordinal()] / (degraded.hits / 100))) / 100.0D;
		int cost = (int) (degraded.cost * percent);
		if (player.getItems().getItemAmount(995) < cost) {
			player.sendMessage("You do not have the coins to repair this item.");
			return false;
		}
		player.getItems().deleteItem2(995, cost);
		player.degradableItem[degraded.ordinal()] = 0;
		player.sendMessage("The item has been repaired for "+cost+" coins.");
		return true;
	}
	
	public static boolean claim(Player player, int item) {
		Optional<DegradableItem> degradable = DegradableItem.forId(item);
		if (!degradable.isPresent()) {
			return false;
		}
		DegradableItem degraded = degradable.get();
		if (!player.claimDegradableItem[degraded.ordinal()] || degraded.cost < 0) {
			player.sendMessage("This item does not need to be claimed.");
			return false;
		}
		int cost = degraded.cost * 2;
		if (player.getItems().getItemAmount(995) < cost) {
			player.sendMessage("You do not have the coins to claim this item.");
			return false;
		}
		if (player.getItems().freeSlots() < 1) {
			player.sendMessage("You need at least one free slot to do this.");
			return false;
		}
		player.getItems().deleteItem2(995, cost);
		player.degradableItem[degraded.ordinal()] = 0;
		player.claimDegradableItem[degraded.ordinal()] = false;
		player.getItems().addItem(degraded.getItemId(), 1);
		player.sendMessage("You have claimed the "+ItemAssistant.getItemName(item)+" for "+cost+" coins.");
		return true;
	}
	
	public static DegradableItem[] getClaimedItems(Player player) {
		DegradableItem[] options = new DegradableItem[4];
		int i = 0;
		for(int j = 0; j < player.claimDegradableItem.length; j++) {
			if (player.claimDegradableItem[j]) {
				options[i] = DegradableItem.values()[j];
				i++;
			}
			if (i == options.length) {
				break;
			}
		}
		DegradableItem[] backingArray = new DegradableItem[i];
		System.arraycopy(options, 0, backingArray, 0, i);
		return backingArray;
	}

}
