package ab.model.holiday;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;

import ab.Server;
import ab.model.items.GameItem;
import ab.model.items.GroundItem;
import ab.model.items.Item;
import ab.model.items.ItemAssistant;
import ab.model.items.ItemDefinition;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;
import ab.world.ItemHandler;

/**
 * A simple tool used to spawn a random rare item on the ground during holidays.
 * @author Chris
 * @date Aug 14, 2015 12:18:55 AM
 *
 */
public class HolidayTool {
	
	/**
	 * An array that contains the names of Halloween-based rare items.
	 */
	private static final String[] HALLOWEEN_ITEMS = { "pumpkin", "red h'ween mask", "green h'ween mask", 
													  "blue h'ween mask", "skeleton mask", "skeleton top",
													  "skeleton legs", "skeleton boots", "skeleton gloves",
													  "grim reaper hood", "antisanta mask", "antisanta jacket",
													  "antisanta pantaloons", "antisanta gloves", "antisanta boots",
													  "santa mask", "santa jacket", "santa pantaloons",
													  "santa gloves", "santa boots" };

	/**
	 * An array that contains the names of Christmas-based holiday event/rare items.
	 */
	private static final String[] CHRISTMAS_ITEMS = { "santa hat", "red partyhat", "yellow partyhat", 
													  "blue partyhat", "purple partyhat", "white partyhat",
													  "green partyhat", "reindeer hat", "christmas cracker" };
	
	private static final String[] DISCONTINUED_ITEM = { "disk of returning", "half full wine jug", "war ship"};
	
	private static String itemName;
	private static int randomItem;
	
	/**
	 * Spawns a randomized rare item based solely upon the active holiday event.
	 * @param player
	 */
	public static void spawnRare(Player player) {
		if (!Server.getHolidayController().HALLOWEEN.isActive() && !HolidayController.CHRISTMAS.isActive()) {
			player.sendMessage("You may only spawn a rare whilst a holiday event is active!");
			return;
		}
		if (!player.lastRare.elapsed(60000)) {
			player.sendMessage("You must wait " + (60 - (player.lastRare.elapsedTime()) / 1000) + " seconds before spawning another rare!");
			return;
		} 
			itemName = (HolidayController.HALLOWEEN.isActive() ? HALLOWEEN_ITEMS[Misc.random(HALLOWEEN_ITEMS.length - 1)] : 
					CHRISTMAS_ITEMS[Misc.random(CHRISTMAS_ITEMS.length - 1)]);
			randomItem = player.getItems().getItemId(itemName);
			Server.itemHandler.createGroundItem(player, randomItem, player.absX + Misc.random(2), 
												player.absY + Misc.random(2), 
												player.heightLevel, 1, player.getId());
			player.lastRare.reset();
			
		
	}

}
