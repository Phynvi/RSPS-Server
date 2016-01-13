package ab.model.content;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Player;
import ab.model.players.skills.Agility;
import ab.model.players.skills.agility.AgilityHandler;
import ab.world.objects.GlobalObject;

/**
 * Handles the wilderness ditch event. Note, this is NOT the recommended way of doing this.
 * @author Chris
 * @date Aug 12, 2015 8:32:41 AM
 *
 */
public class WildernessDitch {
	
	private static final int ANIMATION_ID = 6132;
	private static int OFFSET_Y = 3;
	
	/**
	 * An instance of the stopwatch to track the player's last ditch hop.
	 */
	private Stopwatch stopwatch = Stopwatch.createUnstarted();
	
	/**
	 * Whether our player is in the wilderness.
	 * @param c	the player
	 * @return true if the player's absolute x positiion is 3523
	 */
	public static boolean inWilderness(Player c) {
		return c.getY() >= 3523;
	}

	/**
	 * Crosses the ditch.
	 * @param c	the player
	 * @param x	the x-coordinate to walk to
	 * @param y the y-coordinate to walk to
	 */
	public static void crossDitch(Player c, int x, int y) {
		c.resetWalkingQueue();
		//c.getAgilityHandler().walk(c, x, y, ANIMATION_ID, 0x33);
		c.teleportToX = x;
       	c.teleportToY = y;
		c.getPA().requestUpdates();
	}

	/**
	 * Starts the enter process for the player.
	 * @param c	the player
	 */
	public static void enter(final Player c) {
		final long lastEntered = System.currentTimeMillis();
		//long time = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		if (c.stopPlayerPacket) {
			return;
		}
		c.stopPlayerPacket = true;
		c.startAnimation(ANIMATION_ID);
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				crossDitch(c, c.absX, c.absY + OFFSET_Y);
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
	
	

	/**
	 * Starts the leave process for the player.
	 * @param c	the player
	 */
	public static void leave(final Player c) {
		final long lastEntered = System.currentTimeMillis();

		if (c.stopPlayerPacket) {
			return;
		}
		c.stopPlayerPacket = true;
		c.startAnimation(ANIMATION_ID);
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				crossDitch(c, c.absX, c.absY - OFFSET_Y);
				if (c.absY <= 3523) {
					container.stop();
				} else if (c.absX <= 2995) {
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
	
	/**
	 * Resets the player's walk index when the event has concluded.
	 * @param c	the player
	 */
	private static void resetWalkIndex(Player c) {
		c.isRunning2 = true;
		c.getPA().sendFrame36(173, 1);
		c.playerWalkIndex = 0x333;
		c.getPA().requestUpdates();
	}
}

