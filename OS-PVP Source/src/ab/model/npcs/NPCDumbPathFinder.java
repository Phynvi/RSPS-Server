package ab.model.npcs;

import ab.Server;
import ab.clip.Region;
import ab.clip.Tile;
import ab.clip.TileControl;
import ab.model.players.Player;
import ab.util.Misc;

public class NPCDumbPathFinder {
	
	private static final int NORTH = 0, EAST = 1,  SOUTH = 2, WEST = 3;
	
	public static void follow(NPC npc, Player following) {
		Tile[] npcTiles = TileControl.getTiles(npc);
		int[] npcLocation = TileControl.currentLocation(npc);
		int[] followingLocation = TileControl.currentLocation(following);
		
		boolean[] moves = new boolean[4];
		
		int dir = -1;//the direction to go.
		
		int distance = TileControl.calculateDistance(npc, following);
		
		if (distance > 16) {
			npc.killerId = 0;
			return;
		}
		npc.facePlayer(following.playerId);
		
		if (distance > 1) {
			for (int i = 0; i < moves.length; i++) {
				moves[i] = true;
			}
			/** remove false moves **/
			if (npcLocation[0] < followingLocation[0]) {
				moves[EAST] = true;	
				moves[WEST] = false;
			} else if (npcLocation[0] > followingLocation[0]) {
				moves[WEST] = true;	
				moves[EAST] = false;	
			} else {
				moves[EAST] = false;	
				moves[WEST] = false;
			}
			if (npcLocation[1] > followingLocation[1]) {
				moves[SOUTH] = true;
				moves[NORTH] = false;
			} else if (npcLocation[1] < followingLocation[1]) {
				moves[NORTH] = true;	
				moves[SOUTH] = false;
			} else {
				moves[NORTH] = false;	
				moves[SOUTH] = false;
			}	
			for (Tile tiles : npcTiles) {
				if (tiles.getTile()[0] == following.getX()) { //same x line
					moves[EAST] = false;
					moves[WEST] = false;
				} else if (tiles.getTile()[1] == following.getY()) { //same y line
					moves[NORTH] = false;
					moves[SOUTH] = false;
				}
			}
			boolean[] blocked = new boolean[3];
			
			if (moves[NORTH] && moves[EAST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedNorthEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) {
					dir = 2;
				} else if (!blocked[0]) {
					dir = 0;
				} else if (!blocked[1]) {
					dir = 4;
				}	
				
			} else if (moves[NORTH] && moves[WEST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedNorthWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //north-west
					dir = 14;
				} else if (!blocked[0]) { //north
					dir = 0;
				} else if (!blocked[1]) { //west
					dir = 12;
				}	
			} else if (moves[SOUTH] && moves[EAST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedSouthEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-east
					dir = 6; 
				} else if (!blocked[0]) { //south
					dir = 8;
				} else if (!blocked[1]) { //east
					dir = 4;
				}	
			} else if (moves[SOUTH] && moves[WEST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedSouthWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-west
					dir = 10; 
				} else if (!blocked[0]) { //south
					dir = 8;
				} else if (!blocked[1]) { //west
					dir = 12;
				}	
				
			} else if (moves[NORTH]) {
				dir = 0;
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[EAST]) {
				dir = 4;
				for (Tile tiles : npcTiles) {
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[SOUTH]) {
				dir = 8;
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[WEST]) {
				dir = 12;
				for (Tile tiles : npcTiles) {
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			}
		} else if (distance == 0) {
			for (int i = 0; i < moves.length; i++) {
				moves[i] = true;
			}
			for (Tile tiles : npcTiles) {
				
				if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[NORTH] = false;
				}
				if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[EAST] = false;
				}
				if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[SOUTH] = false;
				}
				if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[WEST] = false;
				}
			}
			int randomSelection = Misc.random(3);
			
			if (moves[randomSelection]) {
				dir = randomSelection * 4;
			} else if (moves[NORTH]) {
				dir = 0;
			} else if (moves[EAST]) {
				dir = 4;
			} else if (moves[SOUTH])	{
				dir = 8;
			} else if (moves[WEST]) {	
				dir = 12;
			}
		}
		if (dir == -1) {
			return;
		}
		dir >>= 1;	
			
		if (dir < 0) {
			return;
		}
		int x = Misc.directionDeltaX[dir];
		int y = Misc.directionDeltaY[dir];
		npc.moveX = Server.npcHandler.GetMove(npc.getX(), npcLocation[0] + x);
		npc.moveY = Server.npcHandler.GetMove(npc.getY(), npcLocation[1] + y);
		npc.getNextNPCMovement(npc.npcId);
		npc.updateRequired = true;
	}

}