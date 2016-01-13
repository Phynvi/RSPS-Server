/**
 * 
 */
package ab.model.content;

import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Player;

/**
 * Version 2 of the Wilderness Ditch: an attempt at reducing lag.
 * @author Chris
 * @date Aug 12, 2015 8:27:22 PM
 *
 */
public class WildernessDitch2 {
	
	private static final int ANIMATION_ID = 762;
	private static final int offsetY = 3;
	
	/**
	 * Whether our player is in the wilderness.
	 * @param c	the player
	 * @return true if the player's absolute x positiion is 3523
	 */
	public static boolean inWilderness(Player c) {
		return c.getY() >= 3523;
	}
	
	/**
	 * Resets the player's walk index.
	 * @param c
	 */
	private static void resetWalkIndex(Player c) {
		c.playerWalkIndex = 0x333;
		c.isRunning2 = true;
		c.getPA().sendFrame36(173, 1);
		c.getPA().requestUpdates();
	}
	
	/**
	 * Enters the wilderness.
	 * @param player
	 */
	public static void enter(Player player) {
		//player.getAgilityHandler().walk(player, 0, +offsetY, ANIMATION_ID, 0x333);
		/*if (player.getAgilityHandler().hotSpot(player, player.getX(), 3520)) {
			player.getAgilityHandler().walk(player, 0, +offsetY, ANIMATION_ID, -1);
		} else if (player.absY > 3523 && player.absY < 3520) {
			player.getPlayerAssistant().movePlayer(2474, 3429, 0);
		}*/
		player.getPA().walkTo2(player.getX(), player.getY() + offsetY);
		player.getAgilityHandler().resetAgilityProgress();
		player.getAgilityHandler().agilityProgress[0] = true;
	}
	
	public static void enterWild(final Player c) {
		final long lastEntered = System.currentTimeMillis();
		if (lastEntered - System.currentTimeMillis() >= 0) {
			return;
		}
		if (c.stopPlayerPacket) {
			return;
		}
		c.stopPlayerPacket = true;
		c.startAnimation(ANIMATION_ID);
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				enter(c);
				if (c.absY <= 3523) {
					container.stop();
				} else if (c.absX <= 2998) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				resetWalkIndex(c);
				c.stopPlayerPacket = false;
			}
		}, 1);
	}

}
