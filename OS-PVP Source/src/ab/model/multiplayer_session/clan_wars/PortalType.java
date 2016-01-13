package ab.model.multiplayer_session.clan_wars;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Chris
 * @date Aug 12, 2015 4:16:31 PM
 *
 */
public enum PortalType {
	
	CHALLENGE(26642, 0, 0),
	CHALLENGE_2(26644, 0, 0),
	FREE_FOR_ALL(26645, 3327, 4751),
	EXIT(26646, 0, 0);
	
	private final int id;
	private final int teleportX;
	private final int teleportY;
	
	/**
	 * Constructs a new {@link PortalType}.
	 * @param id		the id of the portal object
	 * @param teleportX	the x-coordinates that the player is teleported to
	 * @param teleportY	the y-coordinates that the player is teleported to
	 */
	private PortalType(int id, int teleportX, int teleportY) {
		this.id = id;
		this.teleportX = teleportX;
		this.teleportY = teleportY;
	}
	
	/**
	 * Gets the portal's x-coordinate destination.
	 * @return	the x-coordinate
	 */
	public int getX() {
		return teleportX;
	}
	
	/**
	 * Gets the portal's y-coordinate destination.
	 * @return	the y-coordinate
	 */
	public int getY() {
		return teleportY;
	}
	
	/**
	 * Gets the portal's id.
	 * @return	the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * A set of all of the elements from the {@link PortalType} enum.
	 */
	private static Set<PortalType> PORTALS = EnumSet.allOf(PortalType.class);
	
	/**
	 * Gets a {@link PortalType} for the specified id.
	 * @param id	the object id of the portal
	 * @return	the {@link PortalType}
	 */
	public static PortalType forId(int id) {
		for (PortalType portal : PORTALS) {
			if (portal.getId() == id) {
				return portal;
			}
		}
		return null;
	}

}
