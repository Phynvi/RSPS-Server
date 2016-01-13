package ab.model.players;

import ab.model.players.Player;
import ab.model.players.Tile;
import ab.model.npcs.NPC;

public class TileControl {
	
	public static Tile generate(int x, int y, int z) {
		return new Tile(x, y, z);
	}

	public static Tile[] getTiles(Player entity) {
		
		int size = 1, tileCount = 0;
		
		Tile[] tiles = new Tile[size * size];
		
		if (tiles.length == 1) 
			tiles[0] = generate(entity.getX(), entity.getY(), entity.getHeightLevel());
		else {
			for (int x = 0; x < size; x++) 
				for (int y = 0; y < size; y++) 
					tiles[tileCount++] = generate(entity.getX() + x, entity.getY() + y, entity.getHeightLevel());
		}	
		return tiles;
	}
	
	public static Tile[] getTiles(NPC entity) {
		
		int size = 1, tileCount = 0;
		
		Tile[] tiles = new Tile[size * size];
		
		if (tiles.length == 1) 
			tiles[0] = generate(entity.getX(), entity.getY(), entity.heightLevel);
		else {
			for (int x = 0; x < size; x++) 
				for (int y = 0; y < size; y++) 
					tiles[tileCount++] = generate(entity.getX() + x, entity.getY() + y, entity.heightLevel);
		}	
		return tiles;
	}
	
	public static Tile[] getTiles(Player entity, int[] location) {
		
		int size = 1, tileCount = 0;
		
		Tile[] tiles = new Tile[size * size];
		
		if (tiles.length == 1)
			tiles[0] = generate(location[0], location[1], location[2]);
		else {
			for (int x = 0; x < size; x++)
				for (int y = 0; y < size; y++)
					tiles[tileCount++] = generate(location[0] + x, location[1] + y, location[2]);
		}	
		return tiles;
	}	
	
	public static int calculateDistanceNPC(Player entity, NPC following) {
		
		Tile[] tiles = getTiles(entity);
		
		int[] location = currentLocationPlayer(entity);
		int[] pointer = new int[tiles.length];
		
		int lowestCount = 20, count = 0;
		
		for (Tile newTiles : tiles) {
			if (newTiles.getTile() == location)
				pointer[count++] = 0;
			else 
				pointer[count++] = calculateDistanceNPC(newTiles, following);
		}
		for (int i = 0; i < pointer.length; i++)
			if (pointer[i] < lowestCount)
				lowestCount = pointer[i];
		
		return lowestCount;
	}
	
	public static int calculateDistanceNPC(NPC entity, Player following) { //NPC following player
		
		Tile[] tiles = getTiles(entity);
		
		int[] location = currentLocationNPC(entity);
		int[] pointer = new int[tiles.length];
		
		int lowestCount = 20, count = 0;
		
		for (Tile newTiles : tiles) {
			if (newTiles.getTile() == location)
				pointer[count++] = 0;
			else 
				pointer[count++] = calculateDistancePlayer(newTiles, following);
		}
		for (int i = 0; i < pointer.length; i++)
			if (pointer[i] < lowestCount)
				lowestCount = pointer[i];
		
		return lowestCount;
	}
	
	public static int calculateDistancePlayer(Player entity, Player following) {
		
		Tile[] tiles = getTiles(entity);
		
		int[] location = currentLocationPlayer(entity);
		int[] pointer = new int[tiles.length];
		
		int lowestCount = 20, count = 0;
		
		for (Tile newTiles : tiles) {
			if (newTiles.getTile() == location)
				pointer[count++] = 0;
			else 
				pointer[count++] = calculateDistancePlayer(newTiles, following);
		}
		for (int i = 0; i < pointer.length; i++)
			if (pointer[i] < lowestCount)
				lowestCount = pointer[i];
		
		return lowestCount;
	}
	
	public static int calculateDistancePlayer(Tile location, Player other) {
		int X = Math.abs(location.getTile()[0] - other.getX());
		int Y = Math.abs(location.getTile()[1] - other.getY());
		return X > Y ? X : Y;
	}
	
	public static int calculateDistanceNPC(Tile location, NPC other) {
		int X = Math.abs(location.getTile()[0] - other.getX());
		int Y = Math.abs(location.getTile()[1] - other.getY());
		return X > Y ? X : Y;
	}
	
	public static int calculateDistance(int[] location, Player other) {
		int X = Math.abs(location[0] - other.getX());
		int Y = Math.abs(location[1] - other.getY());
		return X > Y ? X : Y;
	}
	
	public static int calculateDistance(int[] location, int[] other) {
		int X = Math.abs(location[0] - other[0]);
		int Y = Math.abs(location[1] - other[1]);
		return X > Y ? X : Y;
	}
	
	public static int[] currentLocationPlayer(Player entity) {
		int[] currentLocation = new int[3];
		if(entity != null) {
			currentLocation[0] = entity.getX();
			currentLocation[1] = entity.getY();
			currentLocation[2] = entity.getHeightLevel();
		}
		return currentLocation;
	}
	
	public static int[] currentLocationNPC(NPC entity) {
		int[] currentLocation = new int[3];
		if(entity != null) {
			currentLocation[0] = entity.getX();
			currentLocation[1] = entity.getY();
			currentLocation[2] = entity.heightLevel;
		}
		return currentLocation;
	}
	
	public static int[] currentLocation(Tile tileLocation) {
		int[] currentLocation = new int[3];
		if(tileLocation != null) {
			currentLocation[0] = tileLocation.getTile()[0];
			currentLocation[1] = tileLocation.getTile()[1];
			currentLocation[2] = tileLocation.getTile()[2];
		}
		return currentLocation;
	}
}