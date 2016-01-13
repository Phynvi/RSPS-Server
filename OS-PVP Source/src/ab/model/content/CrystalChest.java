package ab.model.content;

import ab.model.players.Player;

public class CrystalChest {
	
	public static int KEY = 989;
	public static int DRAGONSTONE = 1631;
	public static int KEY_HALVE1 = 985;
	public static int KEY_HALVE2 = 987;
	public static final int ANIMATION = 881;
	
	public static int ChestRewards[] = {1079, 1093, 526, 1319, 1163, 4131, 11840, 1127, 1149, 1969, 371, 2363, 451, 2368 };
	
	public static int randomChestRewards() {
		return ChestRewards[(int)(Math.random()*ChestRewards.length)];
	}
	
	public static void makeKey(Player c) {
		if (c.getItems().playerHasItem(KEY_HALVE1, 1) && c.getItems().playerHasItem(KEY_HALVE2, 1)) {
			c.getItems().deleteItem(KEY_HALVE1, 1);
			c.getItems().deleteItem(KEY_HALVE2, 1);
			c.getItems().addItem(KEY, 1);
		}
	}
	
	public static void searchChest(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
		c.getItems().deleteItem(KEY, 1);
		c.startAnimation(ANIMATION);
		c.getItems().addItem(DRAGONSTONE, 1);
		c.getItems().addItem(randomChestRewards(), 1);
		c.sendMessage("@blu@You stick your hand in the chest and pull an item out of the chest.");
		} else {
			c.sendMessage("@blu@The chest is locked, it won't budge!");
			return;
		}
	}

}