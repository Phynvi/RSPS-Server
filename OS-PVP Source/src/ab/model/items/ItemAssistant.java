package ab.model.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ab.Config;
import ab.Server;
import ab.model.items.bank.BankItem;
import ab.model.items.bank.BankTab;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.npcs.NPCHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.PlayerSave;
import ab.model.players.combat.Degrade.DegradableItem;
import ab.model.players.combat.range.RangeData;
import ab.model.shops.ShopAssistant;
import ab.util.Misc;

/**
 * Indicates Several Usage Of Items
 * 
 * @author Sanity Revised by Shawn Notes by Shawn
 */
public class ItemAssistant {

	private Player c;

	public ItemAssistant(Player client) {
		this.c = client;
	}

	public boolean updateInventory = false;

	public void updateInventory() {
		updateInventory = false;
		resetItems(3214);
	}

	public int[] Nests = { 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304 };

	public int getWornItemSlot(int itemId) {
		for (int i = 0; i < c.playerEquipment.length; i++)
			if (c.playerEquipment[i] == itemId)
				return i;
		return -1;
	}

	public void handleNests(int itemId) {
		int reward = Nests[Misc.random(14)];
		addItem(reward, 3 + Misc.random(5));
		deleteItem(itemId, 1);
		c.sendMessage("You search the nest");
	}

	/**
	 * Sends an item to the bank in any tab possible.
	 * 
	 * @param itemId
	 *            the item id
	 * @param amount
	 *            the item amount
	 */
	public void sendItemToAnyTab(int itemId, int amount) {
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.freeSlots() > 0 || tab.contains(item)) {
				c.getBank().setCurrentBankTab(tab);
				addItemToBank(itemId, amount);
				return;
			}
		}
		addItemToBank(itemId, amount);
	}
	
	/**
	 * Adds an item to the players inventory, bank, or drops it.
	 * It will do this under any circumstance so if it cannot be added to the inventory
	 * it will next try to send it to the bank and if it cannot, it will drop it.
	 * 
	 * @param itemId	the item
	 * @param amount	the amount of said item
	 */
	public void addItemUnderAnyCircumstance(int itemId, int amount) {
		if (!addItem(itemId, amount)) {
			sendItemToAnyTabOrDrop(new BankItem(itemId, amount), c.getX(), c.getY());
		}
	}

	/**
	 * The x and y represents the possible x and y location of the dropped item
	 * if in fact it cannot be added to the bank.
	 * 
	 * @param item
	 * @param x
	 * @param y
	 */
	public void sendItemToAnyTabOrDrop(BankItem item, int x, int y) {
		item = new BankItem(item.getId() + 1, item.getAmount());
		if (bankContains(item.getId() - 2)) {
			if (isBankSpaceAvailable(item)) {
				sendItemToAnyTab(item.getId() - 1, item.getAmount());
			} else {
				Server.itemHandler.createGroundItem(c, item.getId() - 1, x, y, c.heightLevel, item.getAmount(), c.playerId);
			}
		} else {
			sendItemToAnyTab(item.getId() - 1, item.getAmount());
		}
	}

	/**
	 * PVP Drop handling
	 */
	public void dropPVP() {
		Player pl = Server.playerHandler.players[c.killerId];
		int random = Misc.random(750);
		if (random == 0) {
			Server.itemHandler.createGroundItem(pl, PVPItems(), c.getX(), c.getY(), c.heightLevel, 1, c.killerId);
			pl.sendMessage("@blu@You recieve a rare PVP drop!");
		}
		if (random == 1) {
			Server.itemHandler.createGroundItem(pl, PVPWeapons(), c.getX(), c.getY(), c.heightLevel, 1, c.killerId);
			pl.sendMessage("@blu@You recieve a rare PVP drop!");
		}
	}

	int[] PvpItems = { 13887, 13893, 13896, 13884, 13890, 13864, 13861, 13858, 13864, 13861, 13858 };
	int[] PvpWeapons = { 13902, 13902, 13905, 13905, 13899 };

	public int PVPWeapons() {
		return PvpWeapons[(int) (Math.random() * PvpWeapons.length)];
	}

	public int PVPItems() {
		return PvpItems[(int) (Math.random() * PvpItems.length)];
	}

	/**
	 * Check all slots and determine whether or not a slot is accompanied by
	 * that item
	 */
	public boolean isWearingItem(int itemID) {
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check all slots and determine the amount of said item worn in that slot
	 */
	public int getWornItemAmount(int itemID) {
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] == itemID) {
				return c.playerEquipmentN[i];
			}
		}
		return 0;
	}

	/**
	 * Trimmed and untrimmed skillcapes.
	 */
	public int[][] skillcapes = { { 9747, 9748 }, // Attack
			{ 9753, 9754 }, // Defence
			{ 9750, 9751 }, // Strength
			{ 9768, 9769 }, // Hitpoints
			{ 9756, 9757 }, // Range
			{ 9759, 9760 }, // Prayer
			{ 9762, 9763 }, // Magic
			{ 9801, 9802 }, // Cooking
			{ 9807, 9808 }, // Woodcutting
			{ 9783, 9784 }, // Fletching
			{ 9798, 9799 }, // Fishing
			{ 9804, 9805 }, // Firemaking
			{ 9780, 9781 }, // Crafting
			{ 9795, 9796 }, // Smithing
			{ 9792, 9793 }, // Mining
			{ 9774, 9775 }, // Herblore
			{ 9771, 9772 }, // Agility
			{ 9777, 9778 }, // Thieving
			{ 9786, 9787 }, // Slayer
			{ 9810, 9811 }, // Farming
			{ 9765, 9766 } // Runecraft
	};

	/**
	 * Broken barrows items.
	 */
	public int[][] brokenBarrows = { { 4708, 4860 }, { 4710, 4866 }, { 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 }, { 4718, 4890 },
			{ 4720, 4896 }, { 4722, 4902 }, { 4732, 4932 }, { 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 }, { 4726, 4914 },
			{ 4728, 4920 }, { 4730, 4926 }, { 4745, 4956 }, { 4747, 4926 }, { 4749, 4968 }, { 4751, 4794 }, { 4753, 4980 }, { 4755, 4986 },
			{ 4757, 4992 }, { 4759, 4998 } };

	/**
	 * Empties all of (a) player's items.
	 */
	public void resetItems(int WriteFrame) {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(WriteFrame);
			c.getOutStream().writeWord(c.playerItems.length);
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItemsN[i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
				} else {
					c.getOutStream().writeByte(c.playerItemsN[i]);
				}
				c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
		// }
	}

	public boolean isNotable(int itemId) {
		for (ItemList list : Server.itemHandler.ItemList)
			if (list != null)
				if (list.itemId == itemId)
					if (list.itemDescription.startsWith("Swap this note at any bank"))
						return true;
		return false;
	}

	public void addItemToBank(int itemId, int amount) {
		BankTab tab = c.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(c.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemId)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
						}
					} else {
						if (isNotable(itemId) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemId, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
							}
						}
					}
				}
			}
		}
		if (isNotable(itemId))
			item = new BankItem(itemId, amount);
		if (tab.freeSlots() == 0) {
			c.sendMessage("The item has been dropped on the floor.");
			Server.itemHandler.createGroundItem(c, itemId, c.absX, c.absY, c.heightLevel, amount, c.playerId);
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			c.sendMessage("The item has been dropped on the floor.");
			Server.itemHandler.createGroundItem(c, itemId, c.absX, c.absY, c.heightLevel, amount, c.playerId);
			return;
		}
		tab.add(item);
		resetTempItems();
		if (c.isBanking)
			resetBank();
		c.sendMessage(getItemName(itemId) + " x" + item.getAmount() + " has been added to your bank.");
	}

	/**
	 * Counts (a) player's items.
	 * 
	 * @param itemID
	 * @return count start
	 */
	public int getItemCount(int itemID) {
		int count = 0;
		for (int j = 0; j < c.playerItems.length; j++) {
			if (c.playerItems[j] == itemID + 1) {
				count += c.playerItemsN[j];
			}
		}
		return count;
	}

	public void replaceItem(Player c, int i, int l) {
		for (int k = 0; k < c.playerItems.length; k++) {
			if (playerHasItem(i, 1)) {
				deleteItem(i, getItemSlot(i), 1);
				addItem(l, 1);
			}
		}
	}

	/**
	 * Gets the bonus' of an item.
	 */
	public void writeBonus() {
		int offset = 0;
		String send = "";
		for (int i = 0; i < c.playerBonus.length; i++) {
			if (c.playerBonus[i] >= 0) {
				send = BONUS_NAMES[i] + ": +" + c.playerBonus[i];
			} else {
				send = BONUS_NAMES[i] + ": -" + java.lang.Math.abs(c.playerBonus[i]);
			}

			if (i == 10) {
				offset = 1;
			}
			c.getPA().sendFrame126(send, (1675 + i + offset));
		}

	}

	/**
	 * Gets the total count of (a) player's items.
	 * 
	 * @param itemID
	 * @return
	 */
	public int getTotalCount(int itemID) {
		int count = 0;
		for (int j = 0; j < c.playerItems.length; j++) {
			if (Item.itemIsNote[itemID + 1]) {
				if (itemID + 2 == c.playerItems[j])
					count += c.playerItemsN[j];
			}
			if (!Item.itemIsNote[itemID + 1]) {
				if (itemID + 1 == c.playerItems[j]) {
					count += c.playerItemsN[j];
				}
			}
		}
		for (int j = 0; j < c.bankItems.length; j++) {
			if (c.bankItems[j] == itemID + 1) {
				count += c.bankItemsN[j];
			}
		}
		return count;
	}

	/**
	 * Send the items kept on death.
	 */
	public void sendItemsKept() {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(6963);
			c.getOutStream().writeWord(c.itemKeptId.length);
			for (int i = 0; i < c.itemKeptId.length; i++) {
				if (c.playerItemsN[i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(1);
				} else {
					c.getOutStream().writeByte(1);
				}
				if (c.itemKeptId[i] > 0) {
					c.getOutStream().writeWordBigEndianA(c.itemKeptId[i] + 1);
				} else {
					c.getOutStream().writeWordBigEndianA(0);
				}
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
		// }
	}

	/**
	 * Item kept on death
	 **/
	public void keepItem(int keepItem, boolean deleteItem) {
		int value = 0;
		int item = 0;
		int slotId = 0;
		boolean itemInInventory = false;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] - 1 > 0) {
				int inventoryItemValue = c.getShops().getItemShopValue(c.playerItems[i] - 1);
				if (inventoryItemValue > value && (!c.invSlot[i])) {
					value = inventoryItemValue;
					item = c.playerItems[i] - 1;
					slotId = i;
					itemInInventory = true;
				}
			}
		}
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			if (c.playerEquipment[i1] > 0) {
				int equipmentItemValue = c.getShops().getItemShopValue(c.playerEquipment[i1]);
				if (equipmentItemValue > value && (!c.equipSlot[i1])) {
					value = equipmentItemValue;
					item = c.playerEquipment[i1];
					slotId = i1;
					itemInInventory = false;
				}
			}
		}
		if (itemInInventory) {
			c.invSlot[slotId] = true;
			if (deleteItem) {
				deleteItem(c.playerItems[slotId] - 1, getItemSlot(c.playerItems[slotId] - 1), 1);
			}
		} else {
			c.equipSlot[slotId] = true;
			if (deleteItem) {
				deleteEquipment(item, slotId);
			}
		}
		c.itemKeptId[keepItem] = item;
	}

	/**
	 * Reset items kept on death.
	 **/
	public void resetKeepItems() {
		for (int i = 0; i < c.itemKeptId.length; i++) {
			c.itemKeptId[i] = -1;
		}
		for (int i1 = 0; i1 < c.invSlot.length; i1++) {
			c.invSlot[i1] = false;
		}
		for (int i2 = 0; i2 < c.equipSlot.length; i2++) {
			c.equipSlot[i2] = false;
		}
	}

	/**
	 * Deletes all of a player's items.
	 **/
	public void deleteAllItems() {
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			deleteEquipment(c.playerEquipment[i1], i1);
		}
		for (int i = 0; i < c.playerItems.length; i++) {
			deleteItem(c.playerItems[i] - 1, getItemSlot(c.playerItems[i] - 1), c.playerItemsN[i]);
		}
	}

	/**
	 * Drops all items for a killer.
	 **/
	public void dropAllItems() {
		Player o = PlayerHandler.getPlayer(c.getKiller());
		List<GameItem> droppedItems = new ArrayList<>();
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == 0) {
				continue;
			}
			boolean keepItem = false;
			for (int item = 0; item < Config.ITEMS_KEPT_ON_DEATH.length; item++) {
				int itemId = Config.ITEMS_KEPT_ON_DEATH[item];
				if ((c.playerItems[i] - 1) == itemId) {
					keepItem = true;
					break;
				}
			}
			if (o != null) {
				if (isTradeable(c.playerItems[i] - 1)) {
					Server.itemHandler.createGroundItem(o, c.playerItems[i] - 1, c.getX(), c.getY(), c.heightLevel, c.playerItemsN[i], c.killerId);
					addToDroppedItems(droppedItems, new GameItem(c.playerItems[i] - 1, c.playerItemsN[i]));
				}
			} else {
				if (!keepItem) {
					Server.itemHandler.createGroundItem(c, c.playerItems[i] - 1, c.getX(), c.getY(), c.heightLevel, c.playerItemsN[i], c.playerId);
				}
			}
		}
		for (int e = 0; e < c.playerEquipment.length; e++) {
			if (c.playerEquipment[e] < 0) {
				continue;
			}
			boolean keepItem = false;
			for (int item = 0; item < Config.ITEMS_KEPT_ON_DEATH.length; item++) {
				int itemId = Config.ITEMS_KEPT_ON_DEATH[item];
				if (c.playerEquipment[e] == itemId) {
					keepItem = true;
					break;
				}
			}
			if (o != null) {
				if (isTradeable(c.playerEquipment[e])) {
					Server.itemHandler.createGroundItem(o, c.playerEquipment[e], c.getX(), c.getY(), c.heightLevel, c.playerEquipmentN[e], c.killerId);
					addToDroppedItems(droppedItems, new GameItem(c.playerEquipment[e], c.playerEquipmentN[e]));
				}
			} else {
				if (!keepItem) {
					Server.itemHandler.createGroundItem(c, c.playerEquipment[e], c.getX(), c.getY(), c.heightLevel, c.playerEquipmentN[e], c.playerId);
				}
			}
		}
		Server.itemHandler.createGroundItem(o == null ? c : o, 526, c.getX(), c.getY(), c.heightLevel, 1, o == null ? c.playerId : c.killerId);
		if (o != null) {
			long start = System.currentTimeMillis();
		//	Server.getKillLogHandler().logKill(o, c, droppedItems);
		}
	}

	/**
	 * Adds an untradeable item to a list if it's not already on the list. If it
	 * is, it increases the amount of the item.
	 * 
	 * @param droppedItems
	 *            A list of {@link GameItem}s.
	 * @param item
	 *            An item which is to be added to the list.
	 */
	private void addToDroppedItems(List<GameItem> droppedItems, GameItem item) {
//		if (Server.itemHandler.isSpawnable(item.getId())) {
//			return;
//		}
		for (GameItem listElement : droppedItems) {
			if (listElement.getId() == item.getId()) {
				int newAmount = listElement.getAmount() + item.getAmount();
				listElement.setAmount(newAmount);
				return;
			}
		}
		droppedItems.add(item);
	}	

	/**
	 * Untradable items with a special currency. (Tokkel, etc)
	 * 
	 * @param item
	 * @return amount
	 */
	public int getUntradePrice(int item) {
		switch (item) {
		case 2518:
		case 2524:
		case 2526:
			return 100000;
		case 2520:
		case 2522:
			return 150000;
		}
		return 0;
	}

	/**
	 * Voided items. (Not void knight items..)
	 * 
	 * @param itemId
	 */
	public void addToVoidList(int itemId) {
		switch (itemId) {
		case 2518:
			c.voidStatus[0]++;
			break;
		case 2520:
			c.voidStatus[1]++;
			break;
		case 2522:
			c.voidStatus[2]++;
			break;
		case 2524:
			c.voidStatus[3]++;
			break;
		case 2526:
			c.voidStatus[4]++;
			break;
		}
	}

	/**
	 * Handles tradable items.
	 */
	public boolean isTradeable(int itemId) {
		if (itemId == 12899 && c.getToxicTridentCharge() > 0 || itemId == 11907 && c.getTridentCharge() > 0) {
			c.sendMessage("You cannot trade your trident whilst it has a charge.");
			return false;
		}
		for (int j = 0; j < Config.ITEM_TRADEABLE.length; j++) {
			if (itemId == Config.ITEM_TRADEABLE[j])
				return false;
		}
		return true;
	}

	/**
	 * Adds an item to a player's inventory.
	 **/

	public boolean addItem(int item, int amount) {
		// synchronized(c) {
		if (amount < 1) {
			amount = 1;
		}
		if (item <= 0) {
			return false;
		}
		if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && Item.itemStackable[item]) || ((freeSlots() > 0) && !Item.itemStackable[item])) {
			for (int i = 0; i < c.playerItems.length; i++) {
				if ((c.playerItems[i] == (item + 1)) && Item.itemStackable[item] && (c.playerItems[i] > 0)) {
					c.playerItems[i] = (item + 1);
					if (((c.playerItemsN[i] + amount) < Config.MAXITEM_AMOUNT) && ((c.playerItemsN[i] + amount) > -1)) {
						c.playerItemsN[i] += amount;
					} else {
						c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
					}
					if (c.getOutStream() != null && c != null) {
						c.getOutStream().createFrameVarSizeWord(34);
						c.getOutStream().writeWord(3214);
						c.getOutStream().writeByte(i);
						c.getOutStream().writeWord(c.playerItems[i]);
						if (c.playerItemsN[i] > 254) {
							c.getOutStream().writeByte(255);
							c.getOutStream().writeDWord(c.playerItemsN[i]);
						} else {
							c.getOutStream().writeByte(c.playerItemsN[i]);
						}
						c.getOutStream().endFrameVarSizeWord();
						c.flushOutStream();
					}
					i = 30;
					return true;
				}
			}
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] <= 0) {
					c.playerItems[i] = item + 1;
					if ((amount < Config.MAXITEM_AMOUNT) && (amount > -1)) {
						c.playerItemsN[i] = 1;
						if (amount > 1) {
							c.getItems().addItem(item, amount - 1);
							return true;
						}
					} else {
						c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
					}
					resetItems(3214);
					i = 30;
					return true;
				}
			}
			return false;
		} else {
			resetItems(3214);
			c.sendMessage("Not enough space in your inventory.");
			return false;
		}
		// }
	}

	/**
	 * Gets the item type.
	 */
	public String itemType(int item) {
		for (int i = 0; i < Item.capes.length; i++) {
			if (item == Item.capes[i])
				return "cape";
		}
		for (int i = 0; i < Item.hats.length; i++) {
			if (item == Item.hats[i])
				return "hat";
		}
		for (int i = 0; i < Item.boots.length; i++) {
			if (item == Item.boots[i])
				return "boots";
		}
		for (int i = 0; i < Item.gloves.length; i++) {
			if (item == Item.gloves[i])
				return "gloves";
		}
		for (int i = 0; i < Item.shields.length; i++) {
			if (item == Item.shields[i])
				return "shield";
		}
		for (int i = 0; i < Item.amulets.length; i++) {
			if (item == Item.amulets[i])
				return "amulet";
		}
		for (int i = 0; i < Item.arrows.length; i++) {
			if (item == Item.arrows[i])
				return "arrows";
		}
		for (int i = 0; i < Item.rings.length; i++) {
			if (item == Item.rings[i])
				return "ring";
		}
		for (int i = 0; i < Item.body.length; i++) {
			if (item == Item.body[i])
				return "body";
		}
		for (int i = 0; i < Item.legs.length; i++) {
			if (item == Item.legs[i])
				return "legs";
		}
		return "weapon";
	}

	/**
	 * Item bonuses.
	 **/
	public final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range", "Strength",
			"Prayer" };

	/**
	 * Resets item bonuses.
	 */
	public void resetBonus() {
		for (int i = 0; i < c.playerBonus.length; i++) {
			c.playerBonus[i] = 0;
		}
	}

	/**
	 * Gets the item bonus from the item.cfg.
	 */
	public void getBonus() {
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] > -1) {
				for (int j = 0; j < Config.ITEM_LIMIT; j++) {
					if (Server.itemHandler.ItemList[j] != null) {
						if (Server.itemHandler.ItemList[j].itemId == c.playerEquipment[i]) {
							for (int k = 0; k < c.playerBonus.length; k++) {
								c.playerBonus[k] += Server.itemHandler.ItemList[j].Bonuses[k];
							}
							break;
						}
					}
				}
			}
		}
		if (c.getItems().isWearingItem(12926) && c.getToxicBlowpipeAmmoAmount() > 0 && c.getToxicBlowpipeCharge() > 0) {
			int dartStrength = RangeData.getRangeStr(c.getToxicBlowpipeAmmo());
			if (dartStrength > 18) {
				dartStrength = 18;
			}
			c.playerBonus[4] += RangeData.getRangeStr(c.getToxicBlowpipeAmmo());
		}
		if (EquipmentSet.VERAC.isWearingBarrows(c) && isWearingItem(12853)) {
			c.playerBonus[11] += 4;
		}
	}

	/**
	 * Weapon type.
	 **/
	public void sendWeapon(int Weapon, String WeaponName) {
		String WeaponName2 = WeaponName.replaceAll("Bronze", "");
		WeaponName2 = WeaponName2.replaceAll("Iron", "");
		WeaponName2 = WeaponName2.replaceAll("Steel", "");
		WeaponName2 = WeaponName2.replaceAll("Black", "");
		WeaponName2 = WeaponName2.replaceAll("Mithril", "");
		WeaponName2 = WeaponName2.replaceAll("Adamant", "");
		WeaponName2 = WeaponName2.replaceAll("Rune", "");
		WeaponName2 = WeaponName2.replaceAll("Granite", "");
		WeaponName2 = WeaponName2.replaceAll("Dragon", "");
		WeaponName2 = WeaponName2.replaceAll("Drag", "");
		WeaponName2 = WeaponName2.replaceAll("Crystal", "");
		WeaponName2 = WeaponName2.trim();
		/**
		 * Attack styles.
		 */
		if (WeaponName.equals("Unarmed")) {
			c.setSidebarInterface(0, 5855); // punch, kick, block
			c.getPA().sendFrame126(WeaponName, 5857);
		} else if (WeaponName.endsWith("whip") || WeaponName.contains("tentacle")) {
			c.setSidebarInterface(0, 12290); // flick, lash, deflect
			c.getPA().sendFrame246(12291, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 12293);
		} else if (WeaponName.endsWith("bow") || WeaponName.contains("shortbow (i)") || WeaponName.endsWith("10") || WeaponName.endsWith("full")
				|| WeaponName.startsWith("seercull") || WeaponName.startsWith("Toxic")) {
			c.setSidebarInterface(0, 1764); // accurate, rapid, longrange
			c.getPA().sendFrame246(1765, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1767);
		} else if (WeaponName.startsWith("Staff") || WeaponName.endsWith("staff") || WeaponName.endsWith("of the seas")
				|| WeaponName.endsWith("wand")) {
			c.setSidebarInterface(0, 328); // spike, impale, smash, block
			c.getPA().sendFrame246(329, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 331);
		} else if (WeaponName2.startsWith("dart") || WeaponName2.startsWith("knife") || WeaponName2.startsWith("javelin")
				|| WeaponName.equalsIgnoreCase("toktz-xil-ul")) {
			c.setSidebarInterface(0, 4446); // accurate, rapid, longrange
			c.getPA().sendFrame246(4447, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4449);
		} else if (WeaponName2.startsWith("dagger") || WeaponName2.contains("anchor") || WeaponName2.contains("sword")) {
			c.setSidebarInterface(0, 2276); // stab, lunge, slash, block
			c.getPA().sendFrame246(2277, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2279);
		} else if (WeaponName2.startsWith("pickaxe")) {
			c.setSidebarInterface(0, 5570); // spike, impale, smash, block
			c.getPA().sendFrame246(5571, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 5573);
		} else if (WeaponName2.startsWith("axe") || WeaponName2.startsWith("battleaxe")) {
			c.setSidebarInterface(0, 1698); // chop, hack, smash, block
			c.getPA().sendFrame246(1699, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1701);
		} else if (WeaponName2.startsWith("halberd")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName2.startsWith("Scythe")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName2.startsWith("spear") || c.playerEquipment[c.playerWeapon] == 13905) {
			c.setSidebarInterface(0, 4679); // lunge, swipe, pound, block
			c.getPA().sendFrame246(4680, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4682);
		} else if (WeaponName2.toLowerCase().contains("mace")) {
			c.setSidebarInterface(0, 3796);
			c.getPA().sendFrame246(3797, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 3799);
		} else if (c.playerEquipment[c.playerWeapon] == 4153 || c.playerEquipment[c.playerWeapon] == 12848
				|| c.playerEquipment[c.playerWeapon] == 13902) {
			c.setSidebarInterface(0, 425); // war hammer equip.
			c.getPA().sendFrame246(426, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 428);
		} else if (WeaponName2.equals("claws")) {
			c.setSidebarInterface(0, 7762);
			c.getPA().sendFrame246(7763, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 7765);
		} else {
			c.setSidebarInterface(0, 2423); // chop, slash, lunge, block
			c.getPA().sendFrame246(2424, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2426);
		}
	}

	/**
	 * Two handed weapon check.
	 **/
	public boolean is2handed(String itemName, int itemId) {
		if (itemName.contains("ahrim") || itemName.contains("karil") || itemName.contains("verac") || itemName.contains("guthan")
				|| itemName.contains("dharok") || itemName.contains("torag")) {
			return true;
		}
		if (itemName.contains("longbow") || itemName.contains("shortbow") || itemName.contains("ark bow")) {
			return true;
		}
		if (itemName.contains("crystal")) {
			return true;
		}
		if (itemName.contains("godsword") || itemName.contains("aradomin sword") || itemName.contains("2h") || itemName.contains("spear")) {
			return true;
		}
		switch (itemId) {
		case 12926:
		case 6724:
		case 11838:
		case 12809:
		case 14484:
		case 4153:
		case 12848:
		case 6528:
		case 10887:
		case 12424:
			return true;
		}
		return false;
	}

	/**
	 * Adds special attack bar to special attack weapons. Removes special attack
	 * bar to weapons that do not have special attacks.
	 **/
	public void addSpecialBar(int weapon) {
		switch (weapon) {
		case 14484:
			c.getPA().sendFrame171(0, 7800);
			specialAmount(weapon, c.specAmount, 7812);
			break;
		case 4151: // whip
		case 12006:
			c.getPA().sendFrame171(0, 12323);
			specialAmount(weapon, c.specAmount, 12335);
			break;

		case 859: // magic bows
		case 861:
		case 12424:
		case 11235:
		case 12765:
		case 12766:
		case 12767:
		case 12768:
		case 11785:
		case 12788:
		case 12926:
			c.getPA().sendFrame171(0, 7549);
			specialAmount(weapon, c.specAmount, 7561);
			break;
			
		case 4587: // dscimmy
			c.getPA().sendFrame171(0, 7599);
			specialAmount(weapon, c.specAmount, 7611);
			break;

		case 3204: // d hally
			c.getPA().sendFrame171(0, 8493);
			specialAmount(weapon, c.specAmount, 8505);
			break;

		case 1377: // d battleaxe
			c.getPA().sendFrame171(0, 7499);
			specialAmount(weapon, c.specAmount, 7511);
			break;
		case 12848:
		case 4153: // gmaul
			c.getPA().sendFrame171(0, 7474);
			specialAmount(weapon, c.specAmount, 7486);
			break;

		case 1249: // dspear
		case 13905:
			c.getPA().sendFrame171(0, 7674);
			specialAmount(weapon, c.specAmount, 7686);
			break;

		case 1215:// dragon dagger
		case 1231:
		case 5680:
		case 5698:
		case 1305: // dragon long
		case 11802:
		case 11806:
		case 11808:
		case 11838:
		case 12809:
		case 11804:
		case 10887:
		case 13899:
			c.getPA().sendFrame171(0, 7574);
			specialAmount(weapon, c.specAmount, 7586);
			break;
		case 13902: // Statius War
			c.getPA().sendFrame171(0, 7474);
			specialAmount(weapon, c.specAmount, 7486);
			break;
		case 1434: // dragon mace
		case 11061:
			c.getPA().sendFrame171(0, 7624);
			specialAmount(weapon, c.specAmount, 7636);
			break;

		default:
			c.getPA().sendFrame171(1, 7624); // mace interface
			c.getPA().sendFrame171(1, 7474); // hammer, gmaul
			c.getPA().sendFrame171(1, 7499); // axe
			c.getPA().sendFrame171(1, 7549); // bow interface
			c.getPA().sendFrame171(1, 7574); // sword interface
			c.getPA().sendFrame171(1, 7599); // scimmy sword interface, for most
			c.getPA().sendFrame171(1, 8493);
			c.getPA().sendFrame171(1, 12323); // whip interface
			break;
		}
	}

	/**
	 * Special attack bar filling amount.
	 **/
	public void specialAmount(int weapon, double specAmount, int barId) {
		c.specBarId = barId;
		c.getPA().sendFrame70(specAmount >= 10 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 9 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 8 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 7 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 6 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 5 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 4 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 3 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 2 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 1 ? 500 : 0, 0, (--barId));
		updateSpecialBar();
		sendWeapon(weapon, getItemName(weapon));
	}

	/**
	 * Special attack text.
	 **/
	public void updateSpecialBar() {
		String percent = Double.toString(c.specAmount);
		if (percent.contains(".")) {
			percent = percent.replace(".", "");
		}
		if (percent.startsWith("0") && !percent.equals("00")) {
			percent = percent.replace("0", "");
		}
		if (percent.startsWith("0") && percent.equals("00")) {
			percent = percent.replace("00", "0");
		}
		c.getPA().sendFrame126(c.usingSpecial ? "@yel@Special Attack (" + percent + "%)" : "@bla@Special Attack (" + percent + "%)", c.specBarId);
	}

	/**
	 * Wielding items.
	 **/
	public boolean wearItem(int wearID, int slot) {
		// synchronized (c) {
		int targetSlot = 0;
		boolean canWearItem = true;
		if (c.playerItems[slot] == (wearID + 1)) {
			ItemDefinition item = ItemDefinition.forId(wearID);
			if (item == null) {
				if (wearID == 15098) {
					c.sendMessage("Please navigate to the designated dicing area in order to roll (South of edge bank)");
					return false;
				}
				c.sendMessage("This item is currently unwearable.");
				return false;
			}
            for (int i = 0; i < item.getRequirements().length; i++) {
                if (c.getLevelForXP(c.playerXP[i]) < item.getRequirements()[i]) {
                	c.sendMessage("You need an " + Config.SKILL_NAME[i] + " level of " + item.getRequirements()[i] + " to wear this item.");
                    return false;
                }
            }
            Optional<DegradableItem> degradable = DegradableItem.forId(wearID);
            if (degradable.isPresent()) {
            	if (c.claimDegradableItem[degradable.get().ordinal()]) {
            		c.sendMessage("A previous item simialr to this has degraded. You must go to the old man");
            		c.sendMessage("in edgeville to claim this item.");
            		return false;
            	}
            }
            targetSlot = item.getSlot();
            
			if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
				DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
				if (!Objects.isNull(session)) {
					if (targetSlot == c.playerHat && session.getRules().contains(Rule.NO_HELM)) {
						c.sendMessage("Wearing helmets has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerAmulet && session.getRules().contains(Rule.NO_AMULET)) {
						c.sendMessage("Wearing amulets has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerArrows && session.getRules().contains(Rule.NO_ARROWS)) {
						c.sendMessage("Wearing arrows has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerChest && session.getRules().contains(Rule.NO_BODY)) {
						c.sendMessage("Wearing platebodies has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerFeet && session.getRules().contains(Rule.NO_BOOTS)) {
						c.sendMessage("Wearing boots has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerHands && session.getRules().contains(Rule.NO_GLOVES)) {
						c.sendMessage("Wearing gloves has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerCape && session.getRules().contains(Rule.NO_CAPE)) {
						c.sendMessage("Wearing capes has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerLegs && session.getRules().contains(Rule.NO_LEGS)) {
						c.sendMessage("Wearing platelegs has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerRing && session.getRules().contains(Rule.NO_RINGS)) {
						c.sendMessage("Wearing a ring has been disabled for this duel.");
						return false;
					}
					if (targetSlot == c.playerWeapon && session.getRules().contains(Rule.NO_WEAPON)) {
						c.sendMessage("Wearing weapons has been disabled for this duel.");
						return false;
					}
					if (session.getRules().contains(Rule.NO_SHIELD)) {
						if (targetSlot == c.playerShield || targetSlot == c.playerWeapon && is2handed(getItemName(wearID).toLowerCase(), wearID)) {
							c.sendMessage("Wearing shields and 2handed weapons has been disabled for this duel.");
							return false;
						}
					}
				}
			}
			if (targetSlot == 3) {
				c.autocasting = false;
				c.autocastId = 0;
				c.getPA().sendFrame36(108, 0);
				c.usingSpecial = false;
				addSpecialBar(wearID);
				if (wearID != 4153 && wearID != 12848) {
					c.getCombat().resetPlayerAttack();
				}
			}
			if (targetSlot == -1 || !item.isWearable()) {
				c.sendMessage("This item cannot be worn.");
				return false;
			}
			if (!canWearItem) {
				return false;
			}

			int wearAmount = c.playerItemsN[slot];
			if (wearAmount < 1) {
				return false;
			}
			if (slot >= 0 && wearID >= 0) {
				int toEquip = c.playerItems[slot];
				int toEquipN = c.playerItemsN[slot];
				int toRemove = c.playerEquipment[targetSlot];
				int toRemoveN = c.playerEquipmentN[targetSlot];
				boolean stackable = false;
				if (getItemName(toRemove).contains("javelin") || getItemName(toRemove).contains("dart") || getItemName(toRemove).contains("knife")
						|| getItemName(toRemove).contains("bolt") || getItemName(toRemove).contains("arrow")
						|| getItemName(toRemove).contains("Bolt") || getItemName(toRemove).contains("bolts")
						|| getItemName(toRemove).contains("thrownaxe") || getItemName(toRemove).contains("throwing"))
					stackable = true;
				else
					stackable = false;
				if (toEquip == toRemove + 1 && Item.itemStackable[toRemove]) {
					deleteItem(toRemove, getItemSlot(toRemove), toEquipN);
					c.playerEquipmentN[targetSlot] += toEquipN;
				} else if (targetSlot != 5 && targetSlot != 3) {
					if (playerHasItem(toRemove, 1) && stackable == true) {
						c.playerItems[slot] = 0;// c.playerItems[slot] =
												// toRemove + 1;
						c.playerItemsN[slot] = 0;// c.playerItemsN[slot] =
													// toRemoveN;
						if (toRemove > 0 && toRemoveN > 0)// c.playerEquipment[targetSlot]
															// = toEquip - 1;
							addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
															// = toEquipN;
					} else {
						c.playerItems[slot] = toRemove + 1;
						c.playerItemsN[slot] = toRemoveN;
					}
					c.playerEquipment[targetSlot] = toEquip - 1;
					c.playerEquipmentN[targetSlot] = toEquipN;
				} else if (targetSlot == 5) {
					boolean wearing2h = is2handed(getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase(), c.playerEquipment[c.playerWeapon]);
					boolean wearingShield = c.playerEquipment[c.playerShield] > 0;
					if (wearing2h) {
						toRemove = c.playerEquipment[c.playerWeapon];
						toRemoveN = c.playerEquipmentN[c.playerWeapon];
						c.playerEquipment[c.playerWeapon] = -1;
						c.playerEquipmentN[c.playerWeapon] = 0;
						updateSlot(c.playerWeapon);
					}
					c.playerItems[slot] = toRemove + 1;
					c.playerItemsN[slot] = toRemoveN;
					c.playerEquipment[targetSlot] = toEquip - 1;
					c.playerEquipmentN[targetSlot] = toEquipN;
				} else if (targetSlot == 3) {
					boolean is2h = is2handed(getItemName(wearID).toLowerCase(), wearID);
					boolean wearingShield = c.playerEquipment[c.playerShield] > 0;
					boolean wearingWeapon = c.playerEquipment[c.playerWeapon] > 0;
					if (is2h) {
						if (wearingShield && wearingWeapon) {
							if (freeSlots() > 0) {
								if (playerHasItem(toRemove, 1) && stackable == true) {
									c.playerItems[slot] = 0;// c.playerItems[slot]
															// = toRemove + 1;
									c.playerItemsN[slot] = 0;// c.playerItemsN[slot]
																// = toRemoveN;
									if (toRemove > 0 && toRemoveN > 0)// c.playerEquipment[targetSlot]
																		// =
																		// toEquip
																		// - 1;
										addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																		// =
																		// toEquipN;
								} else {
									c.playerItems[slot] = toRemove + 1;
									c.playerItemsN[slot] = toRemoveN;
								}
								c.playerEquipment[targetSlot] = toEquip - 1;
								c.playerEquipmentN[targetSlot] = toEquipN;
								removeItem(c.playerEquipment[c.playerShield], c.playerShield);
							} else {
								c.sendMessage("You do not have enough inventory space to do this.");
								return false;
							}
						} else if (wearingShield && !wearingWeapon) {
							c.playerItems[slot] = c.playerEquipment[c.playerShield] + 1;
							c.playerItemsN[slot] = c.playerEquipmentN[c.playerShield];
							c.playerEquipment[targetSlot] = toEquip - 1;
							c.playerEquipmentN[targetSlot] = toEquipN;
							c.playerEquipment[c.playerShield] = -1;
							c.playerEquipmentN[c.playerShield] = 0;
							updateSlot(c.playerShield);
						} else {
							if (playerHasItem(toRemove, 1) && stackable == true) {
								c.playerItems[slot] = 0;// c.playerItems[slot] =
														// toRemove + 1;
								c.playerItemsN[slot] = 0;// c.playerItemsN[slot]
															// = toRemoveN;
								if (toRemove > 0 && toRemoveN > 0)// c.playerEquipment[targetSlot]
																	// = toEquip
																	// - 1;
									addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																	// =
																	// toEquipN;
							} else {
								c.playerItems[slot] = toRemove + 1;
								c.playerItemsN[slot] = toRemoveN;
							}
							c.playerEquipment[targetSlot] = toEquip - 1;
							c.playerEquipmentN[targetSlot] = toEquipN;
						}
					} else {
						if (playerHasItem(toRemove, 1) && stackable == true) {
							c.playerItems[slot] = 0;// c.playerItems[slot] =
													// toRemove + 1;
							c.playerItemsN[slot] = 0;// c.playerItemsN[slot] =
														// toRemoveN;
							if (toRemove > 0 && toRemoveN > 0)// c.playerEquipment[targetSlot]
																// = toEquip -
																// 1;
								addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																// = toEquipN;
						} else {
							c.playerItems[slot] = toRemove + 1;
							c.playerItemsN[slot] = toRemoveN;
						}
						c.playerEquipment[targetSlot] = toEquip - 1;
						c.playerEquipmentN[targetSlot] = toEquipN;
					}
				}
				resetItems(3214);
			}
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(targetSlot);
				c.getOutStream().writeWord(wearID + 1);

				if (c.playerEquipmentN[targetSlot] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(c.playerEquipmentN[targetSlot]);
				} else {
					c.getOutStream().writeByte(c.playerEquipmentN[targetSlot]);
				}

				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
			sendWeapon(c.playerEquipment[c.playerWeapon], getItemName(c.playerEquipment[c.playerWeapon]));
			resetBonus();
			getBonus();
			writeBonus();
			c.getCombat().getPlayerAnimIndex(c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
			c.getPA().requestUpdates();
			return true;
		} else {
			return false;
		}
		// }
	}

	/**
	 * Indicates the action to wear an item.
	 * 
	 * @param wearID
	 * @param wearAmount
	 * @param targetSlot
	 */
	public void updateItems() {
		resetItems(3214);
		c.updateItems = false;
	}

	public void wearItem(int wearID, int wearAmount, int targetSlot) {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(targetSlot);
			c.getOutStream().writeWord(wearID + 1);
			if (wearAmount > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(wearAmount);
			} else {
				c.getOutStream().writeByte(wearAmount);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipment[targetSlot] = wearID;
			c.playerEquipmentN[targetSlot] = wearAmount;
			c.getItems().sendWeapon(c.playerEquipment[c.playerWeapon], c.getItems().getItemName(c.playerEquipment[c.playerWeapon]));
			c.getItems().resetBonus();
			c.updateItems = true;
			c.getItems().getBonus();
			c.getItems().writeBonus();
			c.getCombat().getPlayerAnimIndex(c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
		// }
	}

	/**
	 * Updates the slot when wielding an item.
	 * 
	 * @param slot
	 */
	public void updateSlot(int slot) {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(slot);
			c.getOutStream().writeWord(c.playerEquipment[slot] + 1);
			if (c.playerEquipmentN[slot] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(c.playerEquipmentN[slot]);
			} else {
				c.getOutStream().writeByte(c.playerEquipmentN[slot]);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
		// }

	}

	/**
	 * Removes a wielded item.
	 **/
	public void removeItem(int wearID, int slot) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (c.playerEquipment[slot] > -1) {
				if (addItem(c.playerEquipment[slot], c.playerEquipmentN[slot])) {
					c.playerEquipment[slot] = -1;
					c.playerEquipmentN[slot] = 0;
					sendWeapon(c.playerEquipment[c.playerWeapon], getItemName(c.playerEquipment[c.playerWeapon]));
					resetBonus();
					getBonus();
					writeBonus();
					c.getCombat().getPlayerAnimIndex(c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.getOutStream().createFrame(34);
					c.getOutStream().writeWord(6);
					c.getOutStream().writeWord(1688);
					c.getOutStream().writeByte(slot);
					c.getOutStream().writeWord(0);
					c.getOutStream().writeByte(0);
					c.flushOutStream();
					c.updateRequired = true;
					c.setAppearanceUpdateRequired(true);
					c.isFullHelm = Item.isFullHelm(c.playerEquipment[c.playerHat]);
					c.isFullMask = Item.isFullMask(c.playerEquipment[c.playerHat]);
					c.isFullBody = Item.isFullBody(c.playerEquipment[c.playerChest]);
				}
			}
		}
		// }
	}

	/**
	 * Items in your bank.
	 */
	public void rearrangeBank() {
		int totalItems = 0;
		int highestSlot = 0;
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (c.bankItems[i] != 0) {
				totalItems++;
				if (highestSlot <= i) {
					highestSlot = i;
				}
			}
		}

		for (int i = 0; i <= highestSlot; i++) {
			if (c.bankItems[i] == 0) {
				boolean stop = false;

				for (int k = i; k <= highestSlot; k++) {
					if (c.bankItems[k] != 0 && !stop) {
						int spots = k - i;
						for (int j = k; j <= highestSlot; j++) {
							c.bankItems[j - spots] = c.bankItems[j];
							c.bankItemsN[j - spots] = c.bankItemsN[j];
							stop = true;
							c.bankItems[j] = 0;
							c.bankItemsN[j] = 0;
						}
					}
				}
			}
		}

		int totalItemsAfter = 0;
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (c.bankItems[i] != 0) {
				totalItemsAfter++;
			}
		}

		if (totalItems != totalItemsAfter)
			c.disconnected = true;
	}

	/**
	 * Items displayed on the armor interface.
	 * 
	 * @param id
	 * @param amount
	 */
	public void itemOnInterface(int id, int amount) {
		// synchronized (c) {
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(2274);
		c.getOutStream().writeWord(1);
		if (amount > 254) {
			c.getOutStream().writeByte(255);
			c.getOutStream().writeDWord_v2(amount);
		} else {
			c.getOutStream().writeByte(amount);
		}
		c.getOutStream().writeWordBigEndianA(id);
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}

	/**
	 * Reseting your bank.
	 */
	public void resetBank() {
		int tabId = c.getBank().getCurrentBankTab().getTabId();
		for (int i = 0; i < c.getBank().getBankTab().length; i++) {
			if (i == 0)
				continue;
			if (i != c.getBank().getBankTab().length - 1 && c.getBank().getBankTab()[i].size() == 0 && c.getBank().getBankTab()[i + 1].size() > 0) {
				for (BankItem item : c.getBank().getBankTab()[i + 1].getItems()) {
					c.getBank().getBankTab()[i].add(item);
				}
				c.getBank().getBankTab()[i + 1].getItems().clear();
			}
		}
		c.getPA().sendFrame36(700, 0);
		c.getPA().sendFrame34a(58040, -1, 0, 0);
		int newSlot = -1;
		for (int i = 0; i < c.getBank().getBankTab().length; i++) {
			BankTab tab = c.getBank().getBankTab()[i];
			if (i == tabId) {
				c.getPA().sendFrame36(700 + i, 1);
			} else {
				c.getPA().sendFrame36(700 + i, 0);
			}
			if (tab.getTabId() != 0 && tab.size() > 0 && tab.getItem(0) != null) {
				c.getPA().sendFrame171(0, 58050 + i);
				c.getPA().sendFrame34a(58040 + i, c.getBank().getBankTab()[i].getItem(0).getId() - 1, 0,
						c.getBank().getBankTab()[i].getItem(0).getAmount());
			} else if (i != 0) {
				if (newSlot == -1) {
					newSlot = i;
					c.getPA().sendFrame34a(58040 + i, -1, 0, 0);
					c.getPA().sendFrame171(0, 58050 + i);
					continue;
				}
				c.getPA().sendFrame34a(58040 + i, -1, 0, 0);
				c.getPA().sendFrame171(1, 58050 + i);
			}
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(5382); // bank
		c.getOutStream().writeWord(Config.BANK_SIZE);
		BankTab tab = c.getBank().getCurrentBankTab();
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (i > tab.size() - 1) {
				c.getOutStream().writeByte(0);
				c.getOutStream().writeWordBigEndianA(0);
				continue;
			} else {
				BankItem item = tab.getItem(i);
				if (item == null)
					item = new BankItem(-1, 0);
				if (item.getAmount() > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					c.getOutStream().writeByte(item.getAmount());
				}
				if (item.getAmount() < 1)
					item.setAmount(0);
				if (item.getId() > Config.ITEM_LIMIT || item.getId() < 0)
					item.setId(-1);
				c.getOutStream().writeWordBigEndianA(item.getId());
			}
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		c.getPA().sendFrame126("" + c.getBank().getCurrentBankTab().size(), 58061);
		c.getPA().sendFrame126(Integer.toString(tabId), 5292);
		c.getPA().sendFrame126(Misc.capitalize(c.playerName.toLowerCase()) + "'s Bank.", 58064);
	}

	/**
	 * Resets temporary worn items. Used in minigames, etc
	 */
	public void resetTempItems() {
		// synchronized (c) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] > -1) {
				itemCount = i;
			}
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(5064);
		c.getOutStream().writeWord(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {
			if (c.playerItemsN[i] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
			} else {
				c.getOutStream().writeByte(c.playerItemsN[i]);
			}
			if (c.playerItems[i] > Config.ITEM_LIMIT || c.playerItems[i] < 0) {
				c.playerItems[i] = Config.ITEM_LIMIT;
			}
			c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}

	/**
	 * Banking your item.
	 * 
	 * @param itemID
	 * @param fromSlot
	 * @param amount
	 * @return
	 */

	public boolean addToBank(int itemID, int amount, boolean updateView) {
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
			return false;
		}
		if (!c.isBanking)
			return false;
		if (!c.getItems().playerHasItem(itemID))
			return false;
		if (c.getBank().getBankSearch().isSearching()) {
			c.getBank().getBankSearch().reset();
			return false;
		}
		if (c.getBankPin().requiresUnlock()) {
			resetBank();
			c.getBankPin().open(2);
			return false;
		}
		BankTab tab = c.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemID + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(c.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemID)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							//c.getBank().setCurrentBankTab(tab);
							//resetBank();
						}
					} else {
						if (isNotable(itemID) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemID, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								//c.getBank().setCurrentBankTab(tab);
								//resetBank();
							}
						}
					}
				}
			}
		}
		if (isNotable(itemID)) {
			item = new BankItem(itemID, amount);
		}
		if (item.getAmount() > getItemAmount(itemID))
			item.setAmount(getItemAmount(itemID));
		if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
			c.sendMessage("Your bank is already holding the maximum amount of " + getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots() == 0 && !tab.contains(item)) {
			c.sendMessage("Your current bank tab is full.");
			return false;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			int difference = Integer.MAX_VALUE - tab.getItemAmount(item);
			item.setAmount(difference);
			deleteItem2(itemID, difference);
		} else {
			deleteItem2(itemID, item.getAmount());
		}
		tab.add(item);
		if (updateView) {
			resetTempItems();
			resetBank();
		}
		return true;
	}

	public boolean bankContains(int itemId) {
		for (BankTab tab : c.getBank().getBankTab())
			if (tab.contains(new BankItem(itemId + 1)))
				return true;
		return false;
	}
	
	public boolean bankContains(int itemId, int itemAmount) {
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.containsAmount(new BankItem(itemId + 1, itemAmount))) {
				return true;
			}
		}
		return false;
	}

	public boolean isBankSpaceAvailable(BankItem item) {
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.contains(item)) {
				return tab.spaceAvailable(item);
			}
		}
		return false;
	}

	public boolean removeFromAnyTabWithoutAdding(int itemId, int itemAmount, boolean updateView) {
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}
		BankTab tab = null;
		BankItem item = new BankItem(itemId + 1, itemAmount);
		for (BankTab searchedTab : c.getBank().getBankTab()) {
			if (searchedTab.contains(item)) {
				tab = searchedTab;
				break;
			}
		}
		if (tab == null) {
			return false;
		}
		if (itemAmount <= 0)
			return false;
		if (!tab.contains(item))
			return false;
		if (tab.getItemAmount(item) < itemAmount) {
			item.setAmount(tab.getItemAmount(item));
		}
		if (item.getAmount() < 0)
			item.setAmount(0);
		tab.remove(item);
		if (tab.size() == 0) {
			c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
		}
		if (updateView) {
			resetBank();
		}
		c.getItems().resetItems(5064);
		return true;
	}

	public void removeFromBank(int itemId, int itemAmount, boolean updateView) {
		BankTab tab = c.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, itemAmount);
		boolean noted = false;
		if (!c.isBanking)
			return;
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
			return;
		}
		if (itemAmount <= 0)
			return;
		if (c.getBankPin().requiresUnlock()) {
			resetBank();
			c.getBankPin().open(2);
			return;
		}
		if (System.currentTimeMillis() - c.lastBankDeposit < 3000)
			return;
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			c.getPA().closeAllWindows();
			return;
		}
		if (!tab.contains(item))
			return;
		if (c.takeAsNote) {
			if (getItemName(itemId).trim().equalsIgnoreCase(getItemName(itemId + 1).trim()) && isNotable(itemId + 1)) {
				noted = true;
			} else
				c.sendMessage("This item cannot be taken out as noted.");
		}
		/**
		 * Update and remove the proceeding snippet
		 */
		int[] buggedItems = {

		};
		if (freeSlots() == 0 && !playerHasItem(itemId)) {
			c.sendMessage("There is not enough space in your inventory.");
			return;
		}
		if (getItemAmount(itemId) == Integer.MAX_VALUE) {
			c.sendMessage("Your inventory is already holding the maximum amount of " + getItemName(itemId).toLowerCase() + " possible.");
			return;
		}
		if (isStackable(item.getId() - 1) || noted) {
			long totalAmount = (long) getItemAmount(itemId) + (long) itemAmount;
			if (totalAmount > Integer.MAX_VALUE)
				item.setAmount(tab.getItemAmount(item) - getItemAmount(itemId));
		}
		if (tab.getItemAmount(item) < itemAmount) {
			item.setAmount(tab.getItemAmount(item));
		}
		if (!isStackable(item.getId() - 1) && !noted) {
			if (freeSlots() < item.getAmount())
				item.setAmount(freeSlots());
		}
		if (item.getAmount() < 0)
			item.setAmount(0);
		if (!noted)
			addItem(item.getId() - 1, item.getAmount());
		else
			addItem(item.getId(), item.getAmount());
		tab.remove(item);
		if (tab.size() == 0) {
			c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
		}
		if (updateView) {
			resetBank();
		}
		c.getItems().resetItems(5064);
	}

	public boolean addEquipmentToBank(int itemID, int slot, int amount, boolean updateView) {
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
			return false;
		}
		if (!c.isBanking)
			return false;
		if (c.playerEquipment[slot] != itemID || c.playerEquipmentN[slot] <= 0)
			return false;
		BankTab tab = c.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemID + 1, amount);
		Iterator<BankTab> iterator = Arrays.asList(c.getBank().getBankTab()).iterator();
		while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				Iterator<BankItem> iterator2 = t.getItems().iterator();
				while (iterator2.hasNext()) {
					BankItem i = iterator2.next();
					if (i.getId() == item.getId() && !isNotable(itemID)) {
						if (t.getTabId() != tab.getTabId()) {
							tab = t;
							c.getBank().setCurrentBankTab(tab);
							resetBank();
						}
					} else {
						if (isNotable(itemID) && i.getId() == item.getId() - 1) {
							item = new BankItem(itemID, amount);
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								c.getBank().setCurrentBankTab(tab);
								resetBank();
							}
						}
					}
				}
			}
		}
		if (isNotable(itemID))
			item = new BankItem(itemID, amount);
		if (item.getAmount() > c.playerEquipmentN[slot])
			item.setAmount(c.playerEquipmentN[slot]);
		if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
			c.sendMessage("Your bank is already holding the maximum amount of " + getItemName(itemID).toLowerCase() + " possible.");
			return false;
		}
		if (tab.freeSlots() == 0 && !tab.contains(item)) {
			c.sendMessage("Your current bank tab is full.");
			return false;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			c.sendMessage("Your bank is already holding the maximum amount of this item.");
			return false;
		} else
			c.playerEquipmentN[slot] -= item.getAmount();
		if (c.playerEquipmentN[slot] <= 0) {
			c.playerEquipmentN[slot] = -1;
			c.playerEquipment[slot] = -1;
		}
		tab.add(item);
		if (updateView) {
			resetTempItems();
			resetBank();
			updateSlot(slot);
		}
		return true;
	}

	/**
	 * Checking item amounts.
	 * 
	 * @param itemID
	 * @return
	 */
	public int itemAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				tempAmount += c.playerItemsN[i];
			}
		}
		return tempAmount;
	}

	/**
	 * Checks if the item is stackable.
	 * 
	 * @param itemID
	 * @return
	 */
	public boolean isStackable(int itemID) {
		return Item.itemStackable[itemID];
	}

	/**
	 * Updates the equipment tab.
	 **/
	public void setEquipment(int wearID, int amount, int targetSlot) {
		c.isFullHelm = Item.isFullHelm(c.playerEquipment[c.playerHat]);
		c.isFullMask = Item.isFullMask(c.playerEquipment[c.playerHat]);
		c.isFullBody = Item.isFullBody(c.playerEquipment[c.playerChest]);
		// synchronized (c) {
		c.getOutStream().createFrameVarSizeWord(34);
		c.getOutStream().writeWord(1688);
		c.getOutStream().writeByte(targetSlot);
		c.getOutStream().writeWord(wearID + 1);
		if (amount > 254) {
			c.getOutStream().writeByte(255);
			c.getOutStream().writeDWord(amount);
		} else {
			c.getOutStream().writeByte(amount);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		c.playerEquipment[targetSlot] = wearID;
		c.playerEquipmentN[targetSlot] = amount;
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
		// }
	}

	public void swapBankItem(int from, int to) {
		BankItem item = c.getBank().getCurrentBankTab().getItem(from);
		c.getBank().getCurrentBankTab().setItem(from, c.getBank().getCurrentBankTab().getItem(to));
		c.getBank().getCurrentBankTab().setItem(to, item);
	}

	public void moveItems(int from, int to, int moveWindow, boolean insertMode) {
		if (moveWindow == 3214) {
			int tempI;
			int tempN;
			tempI = c.playerItems[from];
			tempN = c.playerItemsN[from];
			c.playerItems[from] = c.playerItems[to];
			c.playerItemsN[from] = c.playerItemsN[to];
			c.playerItems[to] = tempI;
			c.playerItemsN[to] = tempN;
		}
		if (moveWindow == 5382) {
			if (!c.isBanking) {
				c.getPA().removeAllWindows();
				resetBank();
				return;
			}
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				resetBank();
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			if (to > 999) {
				int tabId = to - 1000;
				if (tabId < 0)
					tabId = 0;
				if (tabId == c.getBank().getCurrentBankTab().getTabId()) {
					c.sendMessage("You cannot add an item from it's tab to the same tab.");
					resetBank();
					return;
				}
				if (from > c.getBank().getCurrentBankTab().size()) {
					resetBank();
					return;
				}
				BankItem item = c.getBank().getCurrentBankTab().getItem(from);
				if (item == null) {
					resetBank();
					return;
				}
				c.getBank().getCurrentBankTab().remove(item);
				c.getBank().getBankTab()[tabId].add(item);
			} else {
				if (from > c.getBank().getCurrentBankTab().size() - 1 || to > c.getBank().getCurrentBankTab().size() - 1) {
					resetBank();
					return;
				}
				if (!insertMode) {
					BankItem item = c.getBank().getCurrentBankTab().getItem(from);
					c.getBank().getCurrentBankTab().setItem(from, c.getBank().getCurrentBankTab().getItem(to));
					c.getBank().getCurrentBankTab().setItem(to, item);
				} else {
					int tempFrom = from;
					for (int tempTo = to; tempFrom != tempTo;)
						if (tempFrom > tempTo) {
							swapBankItem(tempFrom, tempFrom - 1);
							tempFrom--;
						} else if (tempFrom < tempTo) {
							swapBankItem(tempFrom, tempFrom + 1);
							tempFrom++;
						}
				}
			}
		}
		if (moveWindow == 5382) {
			resetBank();
		}
		if (moveWindow == 5064) {
			int tempI;
			int tempN;
			tempI = c.playerItems[from];
			tempN = c.playerItemsN[from];

			c.playerItems[from] = c.playerItems[to];
			c.playerItemsN[from] = c.playerItemsN[to];
			c.playerItems[to] = tempI;
			c.playerItemsN[to] = tempN;
			resetItems(3214);
		}
		resetTempItems();
		if (moveWindow == 3214) {
			resetItems(3214);
		}

	}

	/**
	 * Delete item equipment.
	 **/
	public void deleteEquipment(int i, int j) {
		// synchronized (c) {
		if (PlayerHandler.players[c.playerId] == null) {
			return;
		}
		if (i < 0) {
			return;
		}

		c.playerEquipment[j] = -1;
		c.playerEquipmentN[j] = c.playerEquipmentN[j] - 1;
		c.getOutStream().createFrame(34);
		c.getOutStream().writeWord(6);
		c.getOutStream().writeWord(1688);
		c.getOutStream().writeByte(j);
		c.getOutStream().writeWord(0);
		c.getOutStream().writeByte(0);
		getBonus();
		if (j == c.playerWeapon) {
			sendWeapon(-1, "Unarmed");
		}
		resetBonus();
		getBonus();
		writeBonus();
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
		// }
	}

	/**
	 * Delete items.
	 * 
	 * @param id
	 * @param amount
	 */
	public void deleteItem(int id, int amount) {
		deleteItem(id, getItemSlot(id), amount);
	}

	public void deleteItem(int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (c.playerItems[slot] == (id + 1)) {
			if (c.playerItemsN[slot] > amount) {
				c.playerItemsN[slot] -= amount;
			} else {
				c.playerItemsN[slot] = 0;
				c.playerItems[slot] = 0;
			}
			PlayerSave.saveGame(c);
			resetItems(3214);
		}
	}

	public void deleteItem2(int id, int amount) {
		int am = amount;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (am == 0) {
				break;
			}
			if (c.playerItems[i] == (id + 1)) {
				if (c.playerItemsN[i] > amount) {
					c.playerItemsN[i] -= amount;
					break;
				} else {
					c.playerItems[i] = 0;
					c.playerItemsN[i] = 0;
					am--;
				}
			}
		}
		resetItems(3214);
	}

	/**
	 * Delete arrows.
	 **/
	public void deleteArrow() {
		// synchronized (c) {
		if (c.playerEquipment[c.playerCape] == 10499 && Misc.random(5) != 1 && c.playerEquipment[c.playerArrows] != 4740)
			return;
		if (c.playerEquipmentN[c.playerArrows] == 1) {
			c.getItems().deleteEquipment(c.playerEquipment[c.playerArrows], c.playerArrows);
		}
		if (c.playerEquipmentN[c.playerArrows] != 0) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(c.playerArrows);
			c.getOutStream().writeWord(c.playerEquipment[c.playerArrows] + 1);
			if (c.playerEquipmentN[c.playerArrows] - 1 > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(c.playerEquipmentN[c.playerArrows] - 1);
			} else {
				c.getOutStream().writeByte(c.playerEquipmentN[c.playerArrows] - 1);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipmentN[c.playerArrows] -= 1;
		}
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
		// }
	}

	public void deleteEquipment() {
		// synchronized (c) {
		if (c.playerEquipmentN[c.playerWeapon] == 1) {
			c.getItems().deleteEquipment(c.playerEquipment[c.playerWeapon], c.playerWeapon);
		}
		if (c.playerEquipmentN[c.playerWeapon] != 0) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(c.playerWeapon);
			c.getOutStream().writeWord(c.playerEquipment[c.playerWeapon] + 1);
			if (c.playerEquipmentN[c.playerWeapon] - 1 > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(c.playerEquipmentN[c.playerWeapon] - 1);
			} else {
				c.getOutStream().writeByte(c.playerEquipmentN[c.playerWeapon] - 1);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipmentN[c.playerWeapon] -= 1;
		}
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
		// }
	}

	/**
	 * Dropping arrows
	 **/
	public void dropArrowNpc() {
		if (c.playerEquipment[c.playerCape] == 10499)
			return;
		int enemyX = NPCHandler.npcs[c.oldNpcIndex].getX();
		int enemyY = NPCHandler.npcs[c.oldNpcIndex].getY();
		int height = NPCHandler.npcs[c.oldNpcIndex].heightLevel;
		if (Misc.random(10) >= 4) {
			if (Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height) == 0) {
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, 1, c.getId());
			} else if (Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height) != 0) {
				int amount = Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height);
				Server.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, false);
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, amount + 1, c.getId());
			}
		}
	}

	/**
	 * Ranging arrows.
	 */
	public void dropArrowPlayer() {
		int enemyX = PlayerHandler.players[c.oldPlayerIndex].getX();
		int enemyY = PlayerHandler.players[c.oldPlayerIndex].getY();
		int height = PlayerHandler.players[c.oldPlayerIndex].heightLevel;
		if (c.playerEquipment[c.playerCape] == 10499)
			return;
		if (Misc.random(10) >= 4) {
			if (Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height) == 0) {
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, 1, c.getId());
			} else if (Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height) != 0) {
				int amount = Server.itemHandler.itemAmount(c.playerName, c.rangeItemUsed, enemyX, enemyY, height);
				Server.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, false);
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX, enemyY, height, amount + 1, c.getId());
			}
		}
	}

	/**
	 * Removes all items from player's equipment.
	 */
	public void removeAllItems() {
		for (int i = 0; i < c.playerItems.length; i++) {
			c.playerItems[i] = 0;
		}
		for (int i = 0; i < c.playerItemsN.length; i++) {
			c.playerItemsN[i] = 0;
		}
		resetItems(3214);
	}

	/**
	 * Checks if you have a free slot.
	 * 
	 * @return
	 */
	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}
	
	public int freeEquipmentSlots() {
		int slots = 0;
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] <= 0) {
				slots++;
			}
		}
		return slots;
	}
	
	public boolean isWearingItems() {
		return freeEquipmentSlots() < 14;
	}

	/**
	 * Finds the item.
	 * 
	 * @param id
	 * @param items
	 * @param amounts
	 * @return
	 */
	public int findItem(int id, int[] items, int[] amounts) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if (((items[i] - 1) == id) && (amounts[i] > 0)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the item name from the item.cfg
	 * 
	 * @param ItemID
	 * @return
	 */
	public static String getItemName(int ItemID) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					return Server.itemHandler.ItemList[i].itemName;
				}
			}
		}
		return "Unarmed";
	}

	/**
	 * Gets the item ID from the item.cfg
	 * 
	 * @param itemName
	 * @return
	 */
	public int getItemId(String itemName) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemName.equalsIgnoreCase(itemName)) {
					return Server.itemHandler.ItemList[i].itemId;
				}
			}
		}
		return -1;
	}

	/**
	 * Gets the item slot.
	 * 
	 * @param ItemID
	 * @return
	 */
	public int getItemSlot(int ItemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the item amount.
	 * 
	 * @param ItemID
	 * @return
	 */
	public int getItemAmount(int ItemID) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				itemCount += c.playerItemsN[i];
			}
		}
		return itemCount;
	}

	/**
	 * Checks if the player has the item.
	 * 
	 * @param itemID
	 * @param amt
	 * @param slot
	 * @return
	 */
	public boolean playerHasItem(int itemID, int amt, int slot) {
		itemID++;
		int found = 0;
		if (c.playerItems[slot] == (itemID)) {
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] == itemID) {
					if (c.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			if (found >= amt) {
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean playerHasItem(int itemID) {
		itemID++;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID)
				return true;
		}
		return false;
	}

	public boolean playerHasAllItems(int[] items) {
		for (int i = 0; i < items.length; i++) {
			if (!playerHasItem(items[i]))
				return false;
		}
		return true;
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				if (c.playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		if (found >= amt) {
			return true;
		}
		return false;
	}

	/**
	 * Getting un-noted items.
	 * 
	 * @param ItemID
	 * @return
	 */
	public int getUnnotedItem(int ItemID) {
		int NewID = ItemID - 1;
		String NotedName = "";
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					NotedName = Server.itemHandler.ItemList[i].itemName;
				}
			}
		}
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemName == NotedName) {
					if (Server.itemHandler.ItemList[i].itemDescription.startsWith("Swap this note at any bank for a") == false) {
						NewID = Server.itemHandler.ItemList[i].itemId;
						break;
					}
				}
			}
		}
		return NewID;
	}

	/**
	 * Dropping items
	 **/
	public void createGroundItem(int itemID, int itemX, int itemY, int itemAmount) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
		c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
		c.getOutStream().createFrame(44);
		c.getOutStream().writeWordBigEndianA(itemID);
		c.getOutStream().writeWord(itemAmount);
		c.getOutStream().writeByte(0);
		c.flushOutStream();
	}

	/**
	 * Pickup items from the ground.
	 **/
	public void removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		// synchronized (c) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
		c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
		c.getOutStream().createFrame(156);
		c.getOutStream().writeByteS(0);
		c.getOutStream().writeWord(itemID);
		c.flushOutStream();
		// }
	}

	public void createGroundItem(GroundItem item) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC((item.getY() - 8 * c.mapRegionY));
		c.getOutStream().writeByteC((item.getX() - 8 * c.mapRegionX));
		c.getOutStream().createFrame(44);
		c.getOutStream().writeWordBigEndianA(item.getId());
		c.getOutStream().writeWord(item.getAmount());
		c.getOutStream().writeByte(0);
		c.flushOutStream();
	}

	public void removeGroundItem(GroundItem item) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC((item.getY() - 8 * c.mapRegionY));
		c.getOutStream().writeByteC((item.getX() - 8 * c.mapRegionX));
		c.getOutStream().createFrame(156);
		c.getOutStream().writeByteS(0);
		c.getOutStream().writeWord(item.getId());
		c.flushOutStream();
	}

	/**
	 * Checks if a player owns a cape.
	 * 
	 * @return
	 */
	public boolean ownsCape() {
		if (c.getItems().playerHasItem(2412, 1) || c.getItems().playerHasItem(2413, 1) || c.getItems().playerHasItem(2414, 1))
			return true;
		for (int j = 0; j < Config.BANK_SIZE; j++) {
			if (c.bankItems[j] == 2412 || c.bankItems[j] == 2413 || c.bankItems[j] == 2414)
				return true;
		}
		if (c.playerEquipment[c.playerCape] == 2413 || c.playerEquipment[c.playerCape] == 2414 || c.playerEquipment[c.playerCape] == 2415)
			return true;
		return false;
	}

	/**
	 * Checks if the player has all the shards.
	 * 
	 * @return
	 */
	public boolean hasAllShards() {
		return playerHasItem(11818, 1) && playerHasItem(11820, 1) && playerHasItem(11822, 1);
	}

	/**
	 * Makes the godsword blade.
	 */
	public void makeBlade() {
		deleteItem(11818, 1);
		deleteItem(11820, 1);
		deleteItem(11822, 1);
		addItem(11798, 1);
		c.getDH().sendStatement("You combine the shards to make a godsword blade.");
	}

	/**
	 * Makes the godsword.
	 * 
	 * @param i
	 */
	public void makeGodsword(int i) {
		int godsword = i - 8;
		if (playerHasItem(11798) && playerHasItem(i)) {
			deleteItem(11798, 1);
			deleteItem(i, 1);
			addItem(i - 8, 1);
			c.getDH().sendStatement("You combine the hilt and the blade to make a full godsword.");
		}
	}

	public int getTotalRiskedWorth() {
		int worth = getTotalWorth();
		resetKeepItems();
		if (!c.isSkulled) {
			for (int i = 0; i < 3; i++) {
				keepItem(i, false);
			}
		}
		if (c.prayerActive[10] && c.lastProtItem.elapsed(700)) {
			keepItem(3, false);
		}
		for (int i = 0; i < c.itemKeptId.length; i++) {
			worth -= ShopAssistant.getItemShopValue(c.itemKeptId[i]);
		}
		return worth;
	}

	public int getTotalWorth() {
		int worth = 0;
		for (int inventorySlot = 0; inventorySlot < c.playerItems.length; inventorySlot++) {
			int inventoryId = c.playerItems[inventorySlot] - 1;
			int inventoryAmount = c.playerItemsN[inventorySlot];
			int price = ShopAssistant.getItemShopValue(inventoryId);
			if (inventoryId == 996)
				price = 1;
			if (inventoryId > 0 && inventoryAmount > 0) {
				worth += (price * inventoryAmount);
			}
		}
		for (int equipmentSlot = 0; equipmentSlot < c.playerEquipment.length; equipmentSlot++) {
			int equipmentId = c.playerEquipment[equipmentSlot];
			int equipmentAmount = c.playerEquipmentN[equipmentSlot];
			int price = ShopAssistant.getItemShopValue(equipmentId);
			if (equipmentId > 0 && equipmentAmount > 0) {
				worth += (price * equipmentAmount);
			}
		}
		return worth;
	}

	/**
	 * Checks if the item is a godsword hilt.
	 * 
	 * @param i
	 * @return
	 */
	public boolean isHilt(int i) {
		return i >= 11810 && i <= 11816 && i % 2 == 0;
	}

}