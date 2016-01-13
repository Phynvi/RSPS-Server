/**
 * 
 */
package ab.model.multiplayer_session.clan_wars;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import ab.model.players.Boundary;

/**
 * Handles several different Clan Wars map types.
 * @author Chris
 * @date Aug 12, 2015 4:45:12 PM
 *
 */
public enum ClanWarsMap {
	
	/**
	 * The lobby, an unselectable map.
	 */
	LOBBY(new Boundary(3369, 3145, 3382, 3159), false),
	
	/**
	 * The free-for-all map (Wasteland).
	 */
	FREE_FOR_ALL(new Boundary(3264, 4760, 3391, 4863), false),
	
	/**
	 * Wasteland, a default map of clan wars.
	 */
	WASTELAND(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } }, 
			  new Boundary(3264, 4736, 3391, 4863), false),
	/**
	 * Plateau, a cold, dark area.
	 */
	PLATEAU(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } }, 
			  new Boundary(0, 0, 0, 0), false),
			  
	SYLVAN_GLADE(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } }, 
			  new Boundary(0, 0, 0, 0), false),
			  
	FORSAKEN_QUARRY(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), false),
			  
	TURRETS(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), false),
			  
	CLAN_CUP_ARENA(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), false),
			  
	GHASTLY_SWAMP(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), true),
			  
	NORTHLEACH_QUELL(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), true),
			  
	GRIDLOCK(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), true),
			  
	ETHEREAL(new int[][] { new int[] { 3331, 4846 } , 
			  new int[] { 3329, 4774 } },
			  new Boundary(0, 0, 0, 0), true);
	
	/**
	 * The boundaries (syntax: minx, miny, maxx, maxy) of the clan wars map.
	 */
	private Boundary boundary;
	
	/**
	 * Represents whether the map is only accessible to donators.
	 */
	private boolean donator;
	
	/**
	 * A set of coordinates that determine where each team will spawn.
	 */
	private Optional<int[][]> spawnPoint = Optional.empty();
	
	/**
	 * The type of map we are using for this instance of {@link ClanWars}.
	 * @param boundary
	 * @param donator
	 */
	private ClanWarsMap(int[][] spawnPoint, Boundary boundary, boolean donator) {
		this.boundary = boundary;
		this.donator = donator;
		this.spawnPoint = Optional.of(spawnPoint);
	}
	
	/**
	 * The type of map we are using for this instance of {@link ClanWars}.
	 * @param boundary
	 * @param donator
	 */
	private ClanWarsMap(Boundary boundary, boolean donator) {
		this.boundary = boundary;
		this.donator = donator;
	}
	
	/**
	 * Gets the minimum x-coordinate value for the {@link ClanWarsMap} boundary.
	 * @param map	the {@link ClanWarsMap}
	 * @return		the minimum x-coordinate
	 */
	public int getMinimumX(ClanWarsMap map) {
		return map.boundary.getMinimumX();
	}
	
	/**
	 * Gets the minimum y-coordinate value for the {@link ClanWarsMap} boundary.
	 * @param map	the {@link ClanWarsMap}
	 * @return		the minimum y-coordinate
	 */
	public int getMinimumY(ClanWarsMap map) {
		return map.boundary.getMinimumY();
	}
	
	/**
	 * Gets the maximum x-coordinate value for the {@link ClanWarsMap} boundary.
	 * @param map	the {@link ClanWarsMap}
	 * @return		the maximum x-coordinate
	 */
	public int getMaximumX(ClanWarsMap map) {
		return map.boundary.getMaximumX();
	}
	
	/**
	 * Gets the maximum y-coordinate value for the {@link ClanWarsMap} boundary.
	 * @param map	the {@link ClanWarsMap}
	 * @return		the maximum y-coordinate
	 */
	public int getMaximumY(ClanWarsMap map) {
		return map.boundary.getMaximumY();
	}
	
	/**
	 * Gets the {@link Boundary} for the specified map.
	 * @param map	the clan wars map
	 * @return boundary	the boundary
	 */
	public Boundary forMap(ClanWarsMap map) {
		return boundary;
	}
	
	/**
	 * Gets a {@code Boundary} associated with a map.
	 * @return	boundary	the boundary
	 */
	public Boundary getBoundary() {
		return boundary;
	}
	
	/**
	 * Gets an array of the hosting team's spawn coordinates.
	 * @return	the one-dimensional set of 2 coordinates.
	 */
	public final int[] getHostCoords() {
		return spawnPoint.get()[0];
	}
	
	/**
	 * Gets an array of the opposing team's spawn coordinates.
	 * @return	the one-dimensional set of 2 coordinates
	 */
	public final int[] getOpposingCoords() {
		return spawnPoint.get()[1];
	}
	
	/**
	 * Gets whether the specified map is a donator-only map.
	 * @param map	the {@link ClanWarsMap}
	 * @return	true if the map is a donator-only map
	 */
	public boolean isDonator(ClanWarsMap map) {
		return donator;
	}
	
	/**
	 * A {@code Set} of all of the elements from the {@link ClanWarsMap}.
	 */
	private static final Set<ClanWarsMap> MAPS = EnumSet.allOf(ClanWarsMap.class);
	
	/**
	 * Gets all boundaries.
	 * @return
	 */
	public static final Boundary getBoundaries() {
		for (ClanWarsMap map : MAPS) {
			return map.boundary;
		}
		return null;
	}

}
