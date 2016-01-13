package ab.model.players;

import java.util.ArrayList;

import ab.model.items.GameItem;
import ab.model.items.bank.BankItem;

@SuppressWarnings("serial")
public class LostItems extends ArrayList<GameItem> {
	
	/**
	 * The player that has lost items
	 */
	private final Player player;
	
	/**
	 * Creates a new class for managing lost items by a single player
	 * @param player	the player who lost items
	 */
	public LostItems(final Player player) {
		this.player = player;
	}
	
	/**
	 * Stores the players items into a list and deletes their items
	 */
	public void store() {
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] < 1) {
				continue;
			}
			add(new GameItem(player.playerItems[i] - 1, player.playerItemsN[i]));
		}
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] < 1) {
				continue;
			}
			add(new GameItem(player.playerEquipment[i], player.playerEquipmentN[i]));
		}
		player.getItems().deleteEquipment();
		player.getItems().deleteAllItems();
	}
	
	public void retain() {
		for (GameItem item : this) {
			player.sendMessage("<col=255>Your untradeables have been placed on the ground. Pick them up!");
			player.getItems().sendItemToAnyTabOrDrop(new BankItem(item.getId(), item.getAmount()), player.getX(), player.getY());
		}
		clear();
		player.nextChat = -1;
	}

}
