package ab.model.npcs.drops;

/**
 * Class representing a possible item drop from an NPC.
 * 
 * @author Snappie
 *
 */
public class Drop {

	private int itemId;
	private int minAmount;
	private int maxAmount;
	private int rarity;

	/**
	 * @param itemId
	 *            The ItemID of the item that can be dropped.
	 * @param minAmount
	 *            The minimum amount of the item that can be dropped.
	 * @param maxAmount
	 *            The maximum amount of the item that can be dropped.
	 * @param rarity
	 *            The chance the item will be dropped. Rarity 50 would mean a 1
	 *            in 50 (or 0.2) drop chance.
	 */
	public Drop(int itemId, int minAmount, int maxAmount, int rarity) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.rarity = rarity;
	}

	public int getItemId() {
		return itemId;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getRarity() {
		return rarity;
	}

}
