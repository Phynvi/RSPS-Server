package ab.model.players;

import ab.clip.Region;

/**
 * Special Gates
 * 
 * @Author Andrew
 */

public class Special {

	private final static int[] AL_KHARID_GATES = { 2882, 2883 };
	private final static int SHANTAY_GATE = 4031;

	public static void movePlayer(Player c) {
		if (Region.getClipping(c.getX() - 1, c.getY(), c.heightLevel, -1, 0)) {
			c.getPA().movePlayer(c.absX + 1, c.absY, 0);
		} else if (Region.getClipping(c.getX() + 1, c.getY(), c.heightLevel, 1,
				0)) {
			c.getPA().movePlayer(c.absX - 1, c.absY, 0);
		} else if (Region.getClipping(c.getX(), c.getY() - 1, c.heightLevel, 0,
				-1)) {
			c.getPA().movePlayer(c.absX, c.absY + 1, 0);
		} else if (Region.getClipping(c.getX(), c.getY() + 1, c.heightLevel, 0,
				1)) {
			c.getPA().movePlayer(c.absX, c.absY - 1, 0);
		}
	}

	private static boolean movePlayer2(Player c) {
		if (c.absY == 3117) {
			c.getPA().movePlayer(c.absX, c.absY - 2, 0);
			return true;
		} else if (c.absY == 3115) {
			c.getPA().movePlayer(c.absX, c.absY + 2, 0);
			return true;
		}
		c.sendMessage("Move closer so you can use the gate.");
		return false;
	}

	public static boolean openKharid(Player c, int objectId) {
		for (int i = 0; i < AL_KHARID_GATES.length; i++) {
			if (objectId == AL_KHARID_GATES[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean openShantay(Player c, int objectId, int objectX,
			int objectY) {
		if (objectId == SHANTAY_GATE) {
			if (!c.getItems().playerHasItem(1854, 1)) {
				c.sendMessage("You need a shantay pass to pass this gate!");
				return false;
			}
			if (System.currentTimeMillis() - c.miscTimer > 1200) {
				c.getItems().deleteItem2(1854, 1);
				c.sendMessage("You pass the gate.");
				movePlayer2(c);
				c.turnPlayerTo(c.objectX, c.objectY);
				c.miscTimer = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}
}
