package ab.social_media.hitbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HitboxUser {
	/**
	 * The URL of the location where the user will be found
	 */
	private static final String HITBOX_USER_URL = "http://api.hitbox.tv/user/";
	
	/**
	 * Determines if an announcement was made to determine the user was live or not
	 */
	private boolean liveAnnounced;
	
	/**
	 * The name of the user
	 */
	private final String name;
	
	/**
	 * The name of the player
	 */
	private final String playerName;
	
	/**
	 * An object containing all of the data of the user
	 */
	private JSONObject data;
	
	/**
	 * Creates a new {@link HitboxUser} based on the given name
	 * @param name			the name of the user
	 * @param playerName	the player name
	 */
	public HitboxUser(final String name, final String playerName) {
		this.name = name;
		this.playerName = playerName;
	}
	
	/**
	 * Attempts to read the context of the hitbox user. The context
	 * is broken up into keys and values and stored within the user.
	 * 
	 * @throws IOException				An IOException is thrown when the connection to the
	 * URL cannot be created, or if the context of the URL cannot be read. 
	 * 
	 * @throws IllegalStateException	An IllegalStateException occurs when the context when
	 * read is empty. It is only empty when the user does not exist.
	 * 
	 * @throws ParseException			A ParseException occurs when the information read
	 * cannot be parsed as {@link JSONObject}.
	 * 
	 * @return a new HitboxUser object from the name of the reader
	 */
	public void read() throws IOException, IllegalStateException, ParseException {
		URL url = new URL(HITBOX_USER_URL + name);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			sb.append(reader.readLine());
		}
		if (sb.length() == 0 || sb.toString().equals("[]")) {
			throw new IllegalStateException();
		}
		data = (JSONObject) new JSONParser().parse(sb.toString());
	}
	
	/**
	 * Determines if the user is broadcasting at this time.
	 * @return	true if the user is broadcasting, otherwise false.
	 */
	public boolean isLive() {
		Object o = data.get("is_live");
		if (o == null) {
			return false;
		}
		int value = Integer.parseInt((String) o);
		return value != 0;
	}
	
	/**
	 * The name of the Hitbox user.
	 * @return	the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The name or alias of the player 
	 * @return	the name of the player
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	/**
	 * When an announcement is made whether the player is live or not, the 
	 * state changes to true. When the state is true but the player is off
	 * it is set to false.
	 * @param liveAnnounced	the state of the announcement
	 */
	public void setLiveAnnounced(boolean liveAnnounced) {
		this.liveAnnounced = liveAnnounced;
	}
	
	/**
	 * Determines if live has been announced
	 * @return	true if the announcement has been made
	 */
	public boolean isLiveAnnounced() {
		return liveAnnounced;
	}
}
