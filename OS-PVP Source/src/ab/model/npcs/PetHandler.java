package ab.model.npcs;

import ab.Server;
import ab.clip.Region;
import ab.model.players.Player;
import ab.model.players.PlayerSave;

/**
 *
 * @author DF
 *
 **/

public class PetHandler {

	public static final int RATS_NEEDED_TO_GROW = 25;

	private static enum Pets {

		/*
		 * KITTEN_A(1555, 761), KITTEN_B(1556, 762), KITTEN_C(1557, 763),
		 * KITTEN_D(1558, 764), KITTEN_E(1559, 765), KITTEN_F(1560, 766),
		 * CAT_A(1561, 768), CAT_B(1562, 769), CAT_C(1563, 770), CAT_D(1564,
		 * 771), CAT_E(1565, 772), CAT_F(1566, 773),
		 */
		GRAARDOR(12650, 4438), 
		KREE(12649, 4437), 
		ZILLY(12651, 4443), 
		TSUT(12652, 4449), 
		BARRELCHEST(15567, 4442), 
		PRIME(12644, 4436), 
		REX(12645, 4441), 
		SUPREME(12643, 4439), 
		CHAOS(15568, 2055), 
		KBD(12653, 4446), 
		KRAKEN(12655, 3996), 
		HELL_CAT(7582, 3504),  
		CALLISTO(15572, 3997),
		MOLE(15571, 3999), 
		ZULRAH_GREEN(12921, 2127), 
		ZULRAH_RED(12939, 2128), 
		ZULRAH_BLUE(12940, 2129), 
		JAD(13209, 4435), 
		VETION(15573, 3994), 
		VENENATIS(8135, 3995);
		private int itemId, npcId;

		private Pets(int itemId, int npcId) {
			this.itemId = itemId;
			this.npcId = npcId;
		}
	}

	public static Pets forItem(int id) {
		for (Pets t : Pets.values()) {
			if (t.itemId == id) {
				return t;
			}
		}
		return null;
	}

	public static Pets forNpc(int id) {
		for (Pets t : Pets.values()) {
			if (t.npcId == id) {
				return t;
			}
		}
		return null;
	}

	public static int getItemIdForNpcId(int npcId) {
		return forNpc(npcId).itemId;
	}

	public static boolean spawnPet(Player c, int itemId, int slot,
			boolean ignore) {
		Pets pets = forItem(itemId);
		if (pets != null) {
			int npcId = pets.npcId;
			if (c.hasNpc && !ignore) {
				c.sendMessage("You already have a follower!");
				return true;
			}
			int offsetX = 0;
			int offsetY = 0;
			if (Region
					.getClipping(c.getX() - 1, c.getY(), c.heightLevel, -1, 0)) {
				offsetX = -1;
			} else if (Region.getClipping(c.getX() + 1, c.getY(),
					c.heightLevel, 1, 0)) {
				offsetX = 1;
			} else if (Region.getClipping(c.getX(), c.getY() - 1,
					c.heightLevel, 0, -1)) {
				offsetY = -1;
			} else if (Region.getClipping(c.getX(), c.getY() + 1,
					c.heightLevel, 0, 1)) {
				offsetY = 1;
			}
			Server.npcHandler.spawnNpc3(c, npcId, c.absX + offsetX, c.absY
					+ offsetY, c.heightLevel, 0, 120, 25, 200, 200, true,
					false, true);
			
			
			//ADD FOLLOWING HERE
			if (!ignore) {
				c.getItems().deleteItem(itemId, slot, c.playerItemsN[slot]);
				c.hasNpc = true;
				c.summonId = itemId;
				PlayerSave.saveGame(c);
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean pickupPet(Player c, int npcId) {
		Pets pets = forNpc(npcId);
		if (pets != null) {
			if (Server.npcHandler.npcs[c.rememberNpcIndex].spawnedBy == c.playerId) {
				int itemId = pets.itemId;
				if (c.getItems().freeSlots() > 0) {
					Server.npcHandler.npcs[c.rememberNpcIndex].absX = 0;
					Server.npcHandler.npcs[c.rememberNpcIndex].absY = 0;
					Server.npcHandler.npcs[c.rememberNpcIndex] = null;
					c.startAnimation(827);
					c.getItems().addItem(itemId, 1);
					c.summonId = -1;
					c.hasNpc = false;
					c.sendMessage("You pick up your pet.");
				} else {
					c.sendMessage("You do not have enough inventory space to do this.");
				}
			} else {
				c.sendMessage("This is not your pet.");
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean talktoPet(Player c, int npcId) {
		Pets pets = forNpc(npcId);
		if (pets != null) {
			if (NPCHandler.npcs[c.rememberNpcIndex].spawnedBy == c.playerId) {
				switch (npcId) {
				case 4441:
					c.getDH().sendDialogues(14000, 3200);
					break;
				case 4439:
					c.getDH().sendDialogues(14003, 3200);
					break;
				case 4440:
					c.getDH().sendDialogues(14006, 3200);
					break;
				case 4446:
					c.getDH().sendDialogues(14009, 3200);
					break;
				case 4442:
					c.getDH().sendDialogues(14011, 3200);
					break;
				case 4438:
					c.getDH().sendDialogues(14014, 3200);
					break;
				case 4435:
					c.getDH().sendDialogues(14017, 4435);
					break;
				}
			} else {
				c.sendMessage("This is not your pet.");
			}
			return true;
		} else {
			return false;
		}
	}

}