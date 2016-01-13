package ab.model.items;

/**
 * Gets Items From The Item List
 * 
 * @author Sanity Revised by Shawn Notes by Shawn
 */
public class ItemList {
	public int itemId;
	public String itemName;
	public String itemDescription;
	public double ShopValue;
	public double LowAlch;
	public double HighAlch;
	public int[] Bonuses = new int[100];

	/**
	 * Gets the item ID.
	 * 
	 * @param _itemId
	 */
	public ItemList(int _itemId) {
		itemId = _itemId;
	}
}
