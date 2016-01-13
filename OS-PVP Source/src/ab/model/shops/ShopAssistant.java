package ab.model.shops;

import ab.Config;
import ab.Server;
import ab.model.holiday.HolidayController;
import ab.model.items.Item;
import ab.model.items.ItemAssistant;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;
import ab.world.ShopHandler;

public class ShopAssistant {

	private Player c;

	public ShopAssistant(Player client) {
		this.c = client;
	}

	public boolean shopSellsItem(int itemID) {
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Shops
	 **/

	public void openShop(int ShopID) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.getItems().resetItems(3823);
		resetShop(ShopID);
		c.isShopping = true;
		c.myShopId = ShopID;
		c.getPA().sendFrame248(3824, 3822);
		c.getPA().sendFrame126(ShopHandler.ShopName[ShopID], 3901);
	}

	public void updatePlayerShop() {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].isShopping == true && PlayerHandler.players[i].myShopId == c.myShopId && i != c.playerId) {
					PlayerHandler.players[i].updateShop = true;
				}
			}
		}
	}

	public void updateshop(int i) {
		resetShop(i);
	}

	public void resetShop(int ShopID) {
		// synchronized (c) {
		int TotalItems = 0;
		for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
			if (ShopHandler.ShopItems[ShopID][i] > 0) {
				TotalItems++;
			}
		}
		if (TotalItems > ShopHandler.MaxShopItems) {
			TotalItems = ShopHandler.MaxShopItems;
		}
		if (ShopID == 80) {
			c.getPA().sendFrame171(0, 28050);
			c.getPA().sendFrame126("Bounties: " + Misc.insertCommas(Integer.toString(c.getBH().getBounties())), 28052);
		} else {
			c.getPA().sendFrame171(1, 28050);
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(3900);
		c.getOutStream().writeWord(TotalItems);
		int TotalCount = 0;
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if (ShopHandler.ShopItems[ShopID][i] > 0 || i <= ShopHandler.ShopItemsStandard[ShopID]) {
				if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i]);
				} else {
					c.getOutStream().writeByte(ShopHandler.ShopItemsN[ShopID][i]);
				}
				if (ShopHandler.ShopItems[ShopID][i] > Config.ITEM_LIMIT || ShopHandler.ShopItems[ShopID][i] < 0) {
					ShopHandler.ShopItems[ShopID][i] = Config.ITEM_LIMIT;
				}
				c.getOutStream().writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i]);
				TotalCount++;
			}
			if (TotalCount > TotalItems) {
				break;
			}
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}

	public double getItemShopValue(int ItemID, int Type, int fromSlot) {
		double ShopValue = 1;
		double TotPrice = 0;
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					ShopValue = Server.itemHandler.ItemList[i].ShopValue;
				}
			}
		}

		TotPrice = ShopValue;

		if (ShopHandler.ShopBModifier[c.myShopId] == 1) {
			TotPrice *= 1;
			TotPrice *= 1;
			if (Type == 1) {
				TotPrice *= 1;
			}
		} else if (Type == 1) {
			TotPrice *= 1;
		}
		return TotPrice;
	}

	public static int getItemShopValue(int itemId) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == itemId) {
					return (int) Server.itemHandler.ItemList[i].ShopValue;
				}
			}
		}
		return 0;
	}

	/**
	 * buy item from shop (Shop Price)
	 **/

	public void buyFromShopPrice(int removeId, int removeSlot) {
		int ShopValue = (int) Math.floor(getItemShopValue(removeId, 0, removeSlot));
		ShopValue *= 1.00;
		String ShopAdd = "";
		if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " pk points.");
			return;
		}
		if (c.myShopId == 3 || c.myShopId == 4 || c.myShopId == 5 || c.myShopId == 6 
				|| c.myShopId == 8 || c.myShopId == 47 || c.myShopId == 48 || c.myShopId == 111 || c.myShopId == 113) {
			c.sendMessage("This item is for free!");
			return;
		}
		if (c.myShopId == 44) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " slayer points.");
			return;
		}
		if (c.myShopId == 80) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + " currently costs "
					+ Misc.insertCommas(Integer.toString(getBountyHunterItemCost(removeId))) + " bounties.");
			return;
		}
		if (c.myShopId == 10) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " slayer points.");
			return;
		}
		if (c.myShopId == 77) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " vote points.");
			return;
		}
		if (c.myShopId == 78) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " achievement points.");
			return;
		}
		if (c.myShopId == 75) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " PC points.");
			return;
		}
		if (c.myShopId == 9) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " donator points.");
			return;
		}
		if (c.myShopId == 29) {
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + ShopValue + " tokkul" + ShopAdd);
			return;
		}
		if (c.myShopId == 15) {
			c.sendMessage("This item current costs " + c.getItems().getUntradePrice(removeId) + " coins.");
			return;
		}
		if (c.myShopId == 79) {
			c.sendMessage("This item current costs 500,000 coins.");
			return;
		}
		if (ShopValue >= 1000 && ShopValue < 1000000) {
			ShopAdd = " (" + (ShopValue / 1000) + "K)";
		} else if (ShopValue >= 1000000) {
			ShopAdd = " (" + (ShopValue / 1000000) + " million)";
		}
		c.sendMessage(ItemAssistant.getItemName(removeId) + ": currently costs " + ShopValue + " coins" + ShopAdd);
	}

	public int getBountyHunterItemCost(int itemId) {
		switch (itemId) {
		case 12783:
			return 500_000;
		case 12804:
			return 25_000_000;
		case 12851:
			return 10_000_000;
		case 12855:
		case 12856:
			return 2_500_000;
		case 12833:
			return 50_000_000;
		case 12831:
			return 35_000_000;
		case 12829:
			return 25_000_000;
		case 14484:
			return 125_000_000;

		case 12800:
		case 12802:
			return 350_000;
		case 12786:
			return 100_000;
		case 10926:
			return 2_500;
		case 12846:
			return 8_000_000;
		case 12420:
		case 12421:
		case 12419:
		case 12457:
		case 12458:
		case 12459:
			return 10_000_000;
		case 12757:
		case 12759:
		case 12761:
		case 12763:
			return 500_000;
		case 12526:
			return 1_500_000;
		case 12849:
		case 12798:
			return 250_000;
		case 12608:
		case 12610:
		case 12612:
			return 350_000;
		case 12775:
		case 12776:
		case 12777:
		case 12778:
		case 12779:
		case 12780:
		case 12781:
		case 12782:
			return 5_000;

		default:
			return Integer.MAX_VALUE;
		}
	}

	public int getSpecialItemValue(int id) {
		switch (id) {
		/*
		 * PK STORE
		 */
		case 2379:
			return 1;
		case 13066:
			return 1;
		case 7582:
			return 50;
		case 1052:
			return 40;
		case 1249:
			return 300;
		case 2839:
			return 350;
		case 4202:
			return 200;
		case 6720:
			return 300;
		case 4081:
			return 250;
		case 3481:
		case 3483:
		case 3485:
		case 3486:
		case 3488:
		case 6856:
		case 6857:
		case 6858:
		case 6859:
		case 6860:
		case 6861:
		case 6862:
		case 6863:
			return 10;
		case 4333:
		case 4353:
		case 4373:
		case 4393:
		case 4413:
		case 11212:
			return 2;
		case 2996:
		case 1464:
			return 1;
		case 10887:
			if (c.myShopId == 9)
				return 20;
			else
				return 200;
		case 8849:
			return 20;
		case 8848:
			return 20;
		case 8850:
		case 7398:
		case 7399:
		case 7400:
			if (c.myShopId == 77)
				return 10;
			else
				return 40;
		case 8845:
			return 10;
		case 12751:
			return 500;
		case 7462:
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50)
		return 75;
			else
		return 15;
		case 2437:
		case 2441:
		case 2443:
			return 100;
		case 11937:
			return 250;
		case 7461:
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50)
			return 80;
			else
			return 8;
		case 7460:
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50)
			return 60;
			else
				return 6;
		case 7459:
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50)
			return 50;
			else
				return 5;
		case 12006:
			return 50;
		case 12000:
			return 20;
		case 11144:
			if (c.myShopId == 9)
				return 30;
			else
				return 500;
		case 2677:
			return 15;
		case 2572:
			if (c.myShopId == 77)
				return 10;
			else if (c.myShopId == 78)
				return 40;
			else
				return 20;
		case 13887:
		case 13893:
			if (c.myShopId == 9)
				return 50;
			else
				return 280;
		case 13899:
			return 120;
		case 13902:
			return 110;
		case 13905:
			return 100;
		case 14484:
			return 400;
		case 13896:
		case 13884:
		case 13890:
			if (c.myShopId == 9)
				return 40;
			else
				return 250;
		case 13858:
		case 13861:
		case 13864:
			if (c.myShopId == 9)
				return 20;
			else
				return 130;
		case 13870:
		case 13873:
		case 13876:
			if (c.myShopId == 9)
				return 30;
			else
				return 130;
		case 10551:
		case 10548:
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50)
				return 150;
			else
				return 20;
		case 12914:
			return 2;
		case 7509:
			return 1;
		case 4168:
		case 4166:
		case 4551:
		case 4164:
			return 10;
		case 6731:
		case 6735:
		case 6733:
		case 6737:
			if (c.myShopId == 77)
				return 10;
			else
				return 40;
		case 6916:
		case 6918:
		case 6920:
		case 6922:
		case 6924:
			return 150;
		case 3204:
			return 1000;
		case 6585:
		case 11840:
		case 2417:
		case 2415:
		case 2416:
			if (c.myShopId == 77)
				return 10;
			else if (c.myShopId == 9)
				return 10;
			else
				return 50;
		case 11771:
		case 11773:
		case 11772:
		case 11778:
		case 11770:
			return 25;
		case 6570:
			if (c.myShopId == 77)
				return 20;
			else
				return 60;
		case 11235:
			return 100;
		case 11785:
			if (c.myShopId == 9)
				return 40;
			else
				return 750;
		case 11791:
			if (c.myShopId == 9)
				return 50;
			else
				return 600;
		case 11061:
			if (c.myShopId == 9)
				return 50;
			else
				return 1000;
		case 11907:
			if (c.myShopId == 9)
				return 50;
			else
				return 500;
		case 8839:
		case 8840:
		case 8842:
		case 11663:
		case 11664:
		case 11665:
			return 80;
		case 6889:
			return 200;
		case 6914:
			return 200;
		case 4732:
		case 4734:
		case 4736:
		case 4738:
			if (c.myShopId == 77)
				return 4;
			else
				return 12;
		case 4716:
		case 4718:
		case 4720:
		case 4722:
			if (c.myShopId == 77)
				return 2;
			else
				return 20;
		case 4712:
		case 4710:
		case 4714:
		case 4708:
			if (c.myShopId == 77)
				return 2;
			else
				return 14;
		case 4724:
		case 4726:
		case 4728:
		case 4730:
		case 4745:
		case 4747:
		case 4749:
		case 4751:
		case 4753:
		case 4755:
		case 4757:
		case 4759:
			return 12;
		case 10338:
		case 10342:
		case 10340:
		case 1989:
			return 500;
		case 4153:
			return 20;
		case 11838:
		case 1961:
			return 100;
		case 10352:
		case 10350:
		case 10348:
		case 10346:
			return 1000;
		case 11826:
			return 80;
		case 11828:
			return 100;
		case 11830:
			return 100;
		case 11283:
		case 1959:
		case 9703:
			if (c.myShopId == 9)
				return 40;
			else
				return 600;
		case 11802:
			if (c.myShopId == 9)
				return 50;
			else
				return 900;
		case 2581:
			return 40;
		case 2577:
			return 40;
		case 11832:
		case 11834:
			if (c.myShopId == 9)
				return 40;
			else
				return 600;
		case 11804:
			return 700;
		case 11808:
		case 11806:
			return 600;
			// DONATOR
		case 1042:
			return 140;
		case 1048:
			return 120;
		case 1038:
			return 100;
		case 1046:
			return 100;
		case 1044:
			return 90;
		case 1040:
			return 80;
		case 1053:
		case 1055:
		case 1057:
			return 60;
		case 1419:
			return 40;
		case 4566:
			return 40;
		case 4084:
			return 60;
		case 1037:
			return 50;
		case 6199:
			if (c.myShopId == 77)
				return 20;
			else if (c.myShopId == 78)
				return 30;
			else
				return 20;
			// VOTE
		case 9920:
			return 10;
		case 3057:
		case 3058:
		case 3059:
		case 6654:
		case 6655:
		case 6656:
		case 6180:
		case 6181:
		case 6182:
			return 5;
		case 6666:
			return 20;
		case 1050:
			return 80;

		}
		return 0;
	}

	/**
	 * Sell item to shop (Shop Price)
	 **/
	public void sellToShopPrice(int removeId, int removeSlot) {
		for (int i : Config.ITEM_SELLABLE) {
			if (i == removeId) {
				c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
				return;
			}
		}
		boolean IsIn = false;
		if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
			for (int j = 0; j <= ShopHandler.ShopItemsStandard[c.myShopId]; j++) {
				if (removeId == (ShopHandler.ShopItems[c.myShopId][j] - 1)) {
					IsIn = true;
					break;
				}
			}
		} else {
			IsIn = true;
		}
		if (IsIn == false) {
			c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + " to this store.");
		} else {
			int ShopValue = (int) Math.floor(getItemShopValue(removeId, 1, removeSlot));
			String ShopAdd = "";
			if (c.myShopId != 26) {
				ShopValue *= 0.667;
			}
			if (ShopValue >= 1000 && ShopValue < 1000000) {
				ShopAdd = " (" + (ShopValue / 1000) + "K)";
			} else if (ShopValue >= 1000000) {
				ShopAdd = " (" + (ShopValue / 1000000) + " million)";
			}
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for "
						+ ((int) Math.ceil((getSpecialItemValue(removeId) * 0.60)) + " PKP Tickets."));
			} else if (c.myShopId == 29) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " tokkul" + ShopAdd);
			} else {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " coins" + ShopAdd);
			}
		}
	}

	@SuppressWarnings("unused")
	public boolean sellItem(int itemID, int fromSlot, int amount) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return false;
		}
		if (itemID == 863 || itemID == 11230 || itemID == 869 || itemID == 868 || itemID == 867 || itemID == 866 || itemID == 4740 || itemID == 9244
				|| itemID == 11212 || itemID == 892 || itemID == 9194 || itemID == 9243 || itemID == 9242 || itemID == 9241 || itemID == 9240
				|| itemID == 9239 || itemID == 882 || itemID == 884 || itemID == 886 || itemID == 888 || itemID == 890) {
			c.sendMessage("You can't sell this item.");
			return false;
		}
		if (c.myShopId == 14)
			return false;
		if (c.myShopId == 3 || c.myShopId == 4 || c.myShopId == 5 || c.myShopId == 6 
				|| c.myShopId == 8 || c.myShopId == 47 || c.myShopId == 48 || c.myShopId == 111 || c.myShopId == 113)
			return false;
		if (c.myShopId == 21)
			return false;
		if (c.myShopId == 22)
			return false;
		if (c.myShopId == 23)
			return false;
		if (c.myShopId == 77)
			return false;
		if (c.myShopId == 78)
			return false;
		if (c.myShopId == 75)
			return false;
		if (c.myShopId != 12 && c.myShopId != 49 && c.myShopId != 50 && c.myShopId != 26 && c.myShopId != 20) {
			return false;
		}
		/*
		 * if (c.myShopId == 47) return false; if (c.myShopId == 48) return
		 * false;
		 */
		/*
		 * if (c.myShopId == 8) return false;
		 */
		if (c.myShopId == 9)
			return false;
		for (int i : Config.ITEM_SELLABLE) {
			if (i == itemID) {
				c.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + ".");
				return false;
			}
		}
		if (c.getRights().isAdministrator() && !Config.ADMIN_CAN_SELL_ITEMS) {
			c.sendMessage("Selling items as an admin has been disabled.");
			return false;
		}
		if (amount > 0 && itemID == (c.playerItems[fromSlot] - 1)) {
			if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
				boolean IsIn = false;
				for (int i = 0; i <= ShopHandler.ShopItemsStandard[c.myShopId]; i++) {
					if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
						IsIn = true;
						break;
					}
				}
				if (IsIn == false) {
					c.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + " to this store.");
					return false;
				}
			}

			if (amount > c.playerItemsN[fromSlot]
					&& (Item.itemIsNote[(c.playerItems[fromSlot] - 1)] == true || Item.itemStackable[(c.playerItems[fromSlot] - 1)] == true)) {
				amount = c.playerItemsN[fromSlot];
			} else if (amount > c.getItems().getItemAmount(itemID) && Item.itemIsNote[(c.playerItems[fromSlot] - 1)] == false
					&& Item.itemStackable[(c.playerItems[fromSlot] - 1)] == false) {
				amount = c.getItems().getItemAmount(itemID);
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			int TotPrice3 = 0;
			// int Overstock;
			for (int i = amount; i > 0; i--) {
				TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 1, fromSlot));
				TotPrice3 = (int) Math.floor(getSpecialItemValue(itemID));
				if (c.myShopId != 26) {
					TotPrice2 *= 0.667;
				}
				if (c.getItems().freeSlots() > 0 || c.getItems().playerHasItem(995)) {
					if (Item.itemIsNote[itemID] == false) {
						c.getItems().deleteItem(itemID, c.getItems().getItemSlot(itemID), 1);
					} else {
						c.getItems().deleteItem(itemID, fromSlot, 1);
					}
					if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50) {
						c.getItems().addItem(2996, (int) Math.ceil(TotPrice3 *= 0.65));
					} else if (c.myShopId != 12 && c.myShopId != 29 && c.myShopId != 49 && c.myShopId != 50) {
						c.getItems().addItem(995, TotPrice2); 
					} else if (c.myShopId == 29) {
						c.getItems().addItem(6529, TotPrice2);
					}
					//addShopItem(itemID, 1);
				} else {
					c.sendMessage("You don't have enough space in your inventory.");
					break;
				}
			}
			c.getItems().resetItems(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return true;
	}

	public boolean addShopItem(int itemID, int amount) {
		boolean Added = false;
		if (amount <= 0) {
			return false;
		}
		if (Item.itemIsNote[itemID] == true) {
			itemID = c.getItems().getUnnotedItem(itemID);
		}
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if ((ShopHandler.ShopItems[c.myShopId][i] - 1) == itemID) {
				ShopHandler.ShopItemsN[c.myShopId][i] += amount;
				Added = true;
			}
		}
		if (Added == false) {
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[c.myShopId][i] == 0) {
					ShopHandler.ShopItems[c.myShopId][i] = (itemID + 1);
					ShopHandler.ShopItemsN[c.myShopId][i] = amount;
					ShopHandler.ShopItemsDelay[c.myShopId][i] = 0;
					break;
				}
			}
		}
		return true;
	}

	public boolean buyItem(int itemID, int fromSlot, int amount) {
		if (Server.getMultiplayerSessionListener().inAnySession(c))
			return false;
		if (c.myShopId == 14) {
			skillBuy(itemID);
			return false;

		} else if (c.myShopId == 15) {
			buyVoid(itemID);
			return false;
		}

		if (itemID != itemID) {
			return false;
		}
		if (!shopSellsItem(itemID))
			return false;
		if (amount > 0) {
			if (amount > ShopHandler.ShopItemsN[c.myShopId][fromSlot]) {
				amount = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			// int Overstock;
			int Slot = 0;
			int Slot1 = 0;// Tokkul
			if (c.myShopId == 80) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 3 || c.myShopId == 4 || c.myShopId == 5 || c.myShopId == 6 
					|| c.myShopId == 8 || c.myShopId == 47 || c.myShopId == 48 || c.myShopId == 111 || c.myShopId == 113) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 44) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 79) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 9) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 10) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 77) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 78) {
				handleOtherShop(itemID, amount);
				return false;
			}
			if (c.myShopId == 75) {
				handleOtherShop(itemID, amount);
				return false;
			}
			for (int i = amount; i > 0; i--) {
				TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 0, fromSlot));
				Slot = c.getItems().getItemSlot(995);
				Slot1 = c.getItems().getItemSlot(6529);
				if (Slot == -1 && c.myShopId != 29) {
					c.sendMessage("You don't have enough coins.");
					break;
				}
				if(Slot1 == -1 && (c.myShopId == 29)) {
					c.sendMessage("You don't have enough tokkul.");
					break;
				}
				if (TotPrice2 <= 1) {
					TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 0, fromSlot));
					TotPrice2 *= 1.66;
				}
				if (c.myShopId != 29) {
					if (c.playerItemsN[Slot] >= TotPrice2) {
						if (c.getItems().freeSlots() > 0) {
							c.getItems().deleteItem(995, c.getItems().getItemSlot(995), TotPrice2);
							c.getItems().addItem(itemID, 1);
							ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
							ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
							if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
								ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
							}
						} else {
							c.sendMessage("You don't have enough space in your inventory.");
							break;
						}
					} else {
						c.sendMessage("You don't have enough coins.");
						break;
					}
				}
				 if(c.myShopId == 29) {
	                	if (c.playerItemsN[Slot1] >= TotPrice2) {
							if (c.getItems().freeSlots() > 0) {
								c.getItems().deleteItem(6529, c.getItems().getItemSlot(6529), TotPrice2);
								c.getItems().addItem(itemID, 1);
								ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
								ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
								if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
									ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
								}
							} else {
								c.sendMessage("You don't have enough space in your inventory.");
								break;
							}
						} else {
							c.sendMessage("You don't have enough tokkul.");
							break;
						}
				 }
			}
			c.getItems().resetItems(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return false;
	}

	public void handleOtherShop(int itemID, int amount) {
		if (amount <= 0) {
			c.sendMessage("You need to buy atleast one or more of this item.");
			return;
		}
		if (!c.getItems().isStackable(itemID)) {
			if (amount > c.getItems().freeSlots()) {
				amount = c.getItems().freeSlots();
			}
		}
		if (c.myShopId == 80) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need atleast one free slot to buy this.");
				return;
			}
			int itemValue = getBountyHunterItemCost(itemID) * amount;
			if (c.getBH().getBounties() < itemValue) {
				c.sendMessage("You do not have enough bounties to buy this from the shop.");
				return;
			}
			c.getBH().setBounties(c.getBH().getBounties() - itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().resetItems(3823);
			c.getPA().sendFrame126("Bounties: " + Misc.insertCommas(Integer.toString(c.getBH().getBounties())), 28052);
			return;
		}
		if (c.myShopId == 12 && itemID == 2437 || itemID == 2441 || itemID == 2443) {
		int itemValue = getSpecialItemValue(itemID) * amount;
		if (c.pkp < itemValue) {
			c.sendMessage("You do not have enough pk points to buy this item.");
			return;
		}
		c.pkp -= itemValue;
		c.getItems().addItem(itemID, amount * 25);
		c.getItems().resetItems(3823);
		return;
		}
		if (c.myShopId == 12 && itemID == 11937) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.pkp < itemValue) {
				c.sendMessage("You do not have enough pk points to buy this item.");
				return;
			}
			c.pkp -= itemValue;
			c.getItems().addItem(itemID, amount * 50);
			c.getItems().resetItems(3823);
			return;
			}
		if (c.myShopId == 12 && itemID == 3025 || itemID == 6686) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.pkp < itemValue) {
				c.sendMessage("You do not have enough pk points to buy this item.");
				return;
			}
			c.pkp -= itemValue;
			c.getItems().addItem(itemID, amount * 10);
			c.getItems().resetItems(3823);
			return;
			}
		if (c.myShopId == 79) {
			if (itemID >= 9920 && itemID <= 9925) {
				if (c.getHolidayStages().getStage("Halloween") < 5) {
					c.sendMessage("You needed to complete the 2014 Halloween event to obtain this.");
					return;
				}
			}
			if (itemID == 12845) {
				if (c.getHolidayStages().getStage("Halloween") < 6) {
					c.sendMessage("You needed to find this item in a chest in the 2014 halloween event.");
					return;
				}
			}
			if (itemID == 10507) {
				if (c.getHolidayStages().getStage("Christmas") < HolidayController.CHRISTMAS.getMaximumStage()) {
					c.sendMessage("You did not complete the 2014 christmas event, you cannot buy this.");
					return;
				}
			}
			if (c.getItems().playerHasItem(itemID) || c.getItems().isWearingItem(itemID) || c.getItems().bankContains(itemID)) {
				c.sendMessage("You still have this item, you have not lost it.");
				return;
			}
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need atleast one free slot to buy this.");
				return;
			}
			if (!c.getItems().playerHasItem(995, 500_000)) {
				c.sendMessage("You need atleast 500,000GP to purchase this item.");
				return;
			}
			c.getItems().deleteItem2(995, 500_000);
			c.getItems().addItem(itemID, 1);
			c.getItems().resetItems(3823);
			c.sendMessage("You have redeemed the " + ItemAssistant.getItemName(itemID) + ".");
			return;
		}
		if (c.myShopId == 12 || c.myShopId == 49 || c.myShopId == 50) {
			if (c.pkp >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.pkp -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
					c.getPA().sendFrame126("@whi@Pk Points: @gre@" + c.pkp + " ", 7333);
				}
			} else {
				c.sendMessage("You do not have enough pk points to buy this item.");
			} 
		} else if (c.myShopId == 3 || c.myShopId == 4 || c.myShopId == 5 || c.myShopId == 6 
					|| c.myShopId == 8 || c.myShopId == 47 || c.myShopId == 48 || c.myShopId == 111 || c.myShopId == 113) {
				if (c.pkp >= getSpecialItemValue(itemID) * amount) {
					if (c.getItems().freeSlots() > 0) {
						c.pkp -= getSpecialItemValue(itemID) * amount;
						c.getPA().loadQuests();
						c.getItems().addItem(itemID, amount);
						c.getItems().resetItems(3823);
					}
				} else {
					c.sendMessage("Purchase failed.");
				}
		} else if (c.myShopId == 44) {
			if (c.slayerPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.slayerPoints -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
					c.getPA().sendFrame126("@whi@Slayer Points: @gre@" + c.slayerPoints + " ", 7333);
				}
			} else {
				c.sendMessage("You do not have enough slayer points to buy this item.");
			}
		} else if (c.myShopId == 9) {
			if (c.donatorPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.donatorPoints -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough donator points to buy this item.");
			}
		} else if (c.myShopId == 10) {
			if (c.slayerPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.slayerPoints -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough vote points to buy this item.");
			}
		} else if (c.myShopId == 78) {
			if (c.getAchievements().points >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.getAchievements().points -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough achievement points to buy this item.");
			}
		} else if (c.myShopId == 75) {
			if (c.pcPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.pcPoints -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough PC Points to buy this item.");
			}
		} else if (c.myShopId == 77) {
			if (c.votePoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.votePoints -= getSpecialItemValue(itemID) * amount;
					c.getPA().loadQuests();
					c.getItems().addItem(itemID, amount);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough vote points to buy this item.");
			}
		}
	}

	public void openSkillCape() {
		int capes = get99Count();
		if (capes > 1)
			capes = 1;
		else
			capes = 0;
		c.myShopId = 14;
		setupSkillCapes(capes, get99Count());
	}

	/*
	 * public int[][] skillCapes =
	 * {{0,9747,4319,2679},{1,2683,4329,2685},{2,2680
	 * ,4359,2682},{3,2701,4341,2703
	 * },{4,2686,4351,2688},{5,2689,4347,2691},{6,2692,4343,2691},
	 * {7,2737,4325,2733
	 * },{8,2734,4353,2736},{9,2716,4337,2718},{10,2728,4335,2730
	 * },{11,2695,4321,2697},{12,2713,4327,2715},{13,2725,4357,2727},
	 * {14,2722,4345
	 * ,2724},{15,2707,4339,2709},{16,2704,4317,2706},{17,2710,4361,
	 * 2712},{18,2719,4355,2721},{19,2737,4331,2739},{20,2698,4333,2700}};
	 */
	public int[] skillCapes = { 9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786,
			9810, 9765 };

	public int get99Count() {
		int count = 0;
		for (int j = 0; j < c.playerLevel.length; j++) {
			if (c.getLevelForXP(c.playerXP[j]) >= 99) {
				count++;
			}
		}
		return count;
	}

	public void setupSkillCapes(int capes, int capes2) {
		c.getPA().sendFrame171(1, 28050);
		c.getItems().resetItems(3823);
		c.isShopping = true;
		c.myShopId = 14;
		c.getPA().sendFrame248(3824, 3822);
		c.getPA().sendFrame126("Skillcape Shop", 3901);

		int TotalItems = 0;
		TotalItems = capes2;
		if (TotalItems > ShopHandler.MaxShopItems) {
			TotalItems = ShopHandler.MaxShopItems;
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(3900);
		c.getOutStream().writeWord(TotalItems);
		for (int i = 0; i < 21; i++) {
			if (c.getLevelForXP(c.playerXP[i]) < 99)
				continue;
			c.getOutStream().writeByte(1);
			c.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}

	public void skillBuy(int item) {
		int nn = get99Count();
		if (nn > 1)
			nn = 1;
		else
			nn = 0;
		for (int j = 0; j < skillCapes.length; j++) {
			if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
				if (c.getItems().freeSlots() > 1) {
					if (c.getItems().playerHasItem(995, 99000)) {
						if (c.getLevelForXP(c.playerXP[j]) >= 99) {
							c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 99000);
							c.getItems().addItem(skillCapes[j] + nn, 1);
							c.getItems().addItem(skillCapes[j] + 2, 1);
						} else {
							c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
						}
					} else {
						c.sendMessage("You need 99k to buy this item.");
					}
				} else {
					c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
				}
			}
		}
		c.getItems().resetItems(3823);
	}

	public void openVoid() {
	}

	public void buyVoid(int item) {
	}

}
