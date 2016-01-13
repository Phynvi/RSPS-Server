package ab.social_media.hitbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.parser.ParseException;

import ab.model.players.PlayerHandler;

public class Hitbox {
	
	/**
	 * A list of all {@link HitboxUser} objects
	 */
	List<HitboxUser> users = new ArrayList<>(Arrays.asList(
			new HitboxUser("kingrandy", "Theo"),
			new HitboxUser("rilesthegiles", "Riley"),
			new HitboxUser("pinkbuny", "Pinkbuny"),
			new HitboxUser("CrewDino", "Dino"),
			new HitboxUser("jamianxd", "Jamian"),
			new HitboxUser("Austyn", "Austyn")
	));
	
	/**
	 * Updates each of the {@link HitboxUser} objects by calling the
	 * read function of each user.
	 */
	public void update() {
		Iterator<HitboxUser> iterator = users.iterator();
		while (iterator.hasNext()) {
			HitboxUser user = iterator.next();
			try {
				user.read();
				checkLiveStatus(user);
			} catch (IllegalStateException | IOException | ParseException e) {
				//iterator.remove();
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Checks the status of the live stream and will announce to all players
	 * that there is an active live stream if one is live.
	 * @param user	the {@link HitboxUser}
	 */
	private void checkLiveStatus(HitboxUser user) {
		if (!user.isLive() && !user.isLiveAnnounced()) {
			return;
		}
		if (user.isLive() && user.isLiveAnnounced()) {
			return;
		}
		if (!user.isLive() && user.isLiveAnnounced()) {
			PlayerHandler.executeGlobalMessage("<col=ff00ff>Broadcast:</col> The livestream '"+user.getName()+"' is now offline. Please tune in next time.");
			user.setLiveAnnounced(false);
			return;
		}
		if (user.isLive() && !user.isLiveAnnounced()) {
			PlayerHandler.executeBroadcast(user.getPlayerName() + " is now streaming live! Join them at hitbox.tv/" + user.getName());
			user.setLiveAnnounced(true);
		}
	}

}
