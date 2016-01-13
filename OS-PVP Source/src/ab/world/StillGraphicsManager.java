package ab.world;

import ab.Server;
import ab.model.players.Player;

/**
 * Handles still graphics
 * 
 * @author Graham
 * 
 */
public class StillGraphicsManager {

	/**
	 * Nothing to load, as of yet.
	 */
	public StillGraphicsManager() {
	}

	public void stillGraphics(Player curPlr, int absX, int absY,
			int heightLevel, int id, int pause) {
		for (Player p : Server.getPlayerManager().getClientRegion(
				(curPlr).currentRegion)) {
			if (p == null)
				continue;
			if (!p.isActive)
				continue;
			if (p.disconnected)
				continue;
			Player c = p;
			if (c == curPlr || c.withinDistance(absX, absY, heightLevel)) {
				c.getPA().sendStillGraphics(id, heightLevel, absY, absX, pause);
			}
		}
	}

}
