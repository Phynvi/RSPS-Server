package ab.model.players;

import ab.Server;
import ab.util.Misc;

public class Events {
	
		/*
		 * 
		 * Guard Event
		 * (Relates to Thieving)
		 * Randomly Called through the stealFromStall method
		 *
		 * @param c
		 */
	
		public static int[][] NPCs = {
			{3, 	10, 	21, 	19, 	1},
			{11, 	20, 	21, 	40, 	1},
			{21, 	40, 	21, 	80, 	3},
			{61, 	90, 	21, 	105, 	4},
			{91, 	110, 	21, 	120, 	5},
			{111, 	138, 	21, 	150, 	7},
		};
	
		public static void Guard(Player c) {
			for (int[] Guard : NPCs) {
				if(!c.hasEvent) {
					if (c.combatLevel >= Guard[0] && c.combatLevel <= Guard[1]) {
						Server.npcHandler.spawnNpc(c, Guard[2], c.getX() + Misc.random(1), c.getY() + Misc.random(1), c.heightLevel, 0, Guard[3], Guard[4], Guard[4] * 10, Guard[4] * 10, true, false);
						c.sendMessage("You were caught thieving from the stall!");
						c.hasEvent = true;
					}
				}
			}
		}
	}
