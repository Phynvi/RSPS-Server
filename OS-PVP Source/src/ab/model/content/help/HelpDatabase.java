package ab.model.content.help;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;

import ab.Server;
import ab.model.players.Player;
/**
 * 
 * @author Jason MacKeigan
 * @date Nov 5, 2014, 04:39:16 AM
 */
public class HelpDatabase {
	
	/**
	 * The maximum amount requests allowed in the database at once
	 */
	private static final int REQUEST_LIMIT = 20;
	
	/**
	 * Static, singular instance of the HelpDatabase class
	 */
	private static HelpDatabase database = new HelpDatabase();
	
	/**
	 * A collection of all of the available requests in the game
	 */
	private List<HelpRequest> requests = new ArrayList<>();
	
	/**
	 * Adds a help request to the list of current open requests
	 * @param request	the request being added
	 */
	public void add(HelpRequest request) {
		if (Objects.nonNull(request)) {
			requests.add(request);
		}
	}
	
	/**
	 * View a certain request by clicking on a button. 
	 * @param player	the player viewing the request
	 * @param buttonId	the button id
	 */
	public void view(Player player, int buttonId) {
		Optional<Button> button = EnumSet.allOf(Button.class).stream().filter(
				b -> b.viewId == buttonId).findFirst();
		if (!button.isPresent()) {
			return;
		}
		if (requests.size() == 0) {
			player.sendMessage("There are currently no requests to view.");
			return;
		}
		HelpRequest request = requests.get(button.get().ordinal());
		if (Objects.isNull(request)) {
			player.sendMessage("The request you're trying to view cannot be found.");
			return;
		}
		player.getPA().sendFrame126(WordUtils.capitalize(request.getName()) + " ["+request.getDate()+"]", 59556);
		String[] output = WordUtils.wrap(request.getMessage(), 60).split("\n");
		for (int i = 0; i < output.length; i++) {
			player.getPA().sendFrame126(output[i], 59557 + i);
		}
		for (int i = output.length; i < 4; i++) {
			player.getPA().sendFrame126("-", 59557 + i);
		}
	}
	
	/**
	 * Deletes a help request from the database by clicking a button
	 * @param player	the player deleting the request
	 * @param buttonId	the button id
	 */
	public void delete(Player player, int buttonId) {
		Optional<Button> button = EnumSet.allOf(Button.class).stream().filter(
				b -> b.removeId == buttonId).findFirst();
		if (!button.isPresent()) {
			return;
		}
		if (requests.size() == 0) {
			player.sendMessage("There are currently no requests to remove.");
			return;
		}
		HelpRequest request = requests.get(button.get().ordinal());
		if (Objects.isNull(request)) {
			player.sendMessage("The request you're trying to delete cannot be found.");
			return;
		}
		Date future = DateUtils.addMinutes(request.getDate(), 1);
		future = DateUtils.addSeconds(future, 30);
		if (future.after(Server.getCalendar().getInstance().getTime())) {
			player.sendMessage("Help requests must remain alive for 1 minute and 30 seconds before deleting.");
			return;
		}
		requests.remove(request);
		openDatabase(player);
	}
	
	/**
	 * Updates all of the limits in the interfaces fairly efficiently
	 * @param player	the player requesting the elements be updated
	 */
	public void updateElements(Player player) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd, HH:mm");
		for (int i = 0; i < requests.size(); i++) {
			HelpRequest request = requests.get(i);
			player.getPA().sendFrame126(WordUtils.capitalize(request.getName())
					+ " " + format.format(request.getDate()), 59573 + (i * 5));
			player.getPA().sendFrame171(0, 59571 + (i * 5));
		}
		for (int i = requests.size(); i < REQUEST_LIMIT; i++) {
			player.getPA().sendFrame171(1, 59571 + (i * 5));
		}
	}
	
	/**
	 * Opens the database (the interface) for the player requesting it
	 * @param player	the player requesting the database be opened
	 */
	public void openDatabase(Player player) {
		updateElements(player);
		for (int i = 0; i < 5; i++)
			player.getPA().sendFrame126("-", 59556 + i);
		player.getPA().showInterface(59550);
	}
	
	/**
	 * Determines if the player is able to request help based on a predicate
	 * @param player	the player requesting help
	 * @return			true if they can, otherwise false
	 */
	public boolean requestable(Player player) {
		if (requests.size() >= REQUEST_LIMIT) {
			player.sendMessage("We're currently experiencing a high volume of tickets right now.");
			player.sendMessage("You may have to submit a request at a later date.");
			return false;
		}
		if (requests.stream().anyMatch(unrequestable(player))) {
			player.sendMessage("You either have an open request, or someone on your IP does.");
			player.sendMessage("You cannot send a request until the current one is cleared.");
			return false;
		}
		return true;
	}
	
	/**
	 * Determines if the player is unrequestable based on some characteristics
	 * @param player	the player
	 * @return			the predicate
	 */
	private Predicate<HelpRequest> unrequestable(Player player) {
		return req -> req.getName().equals(player.playerName) ||
				req.getProtocol().equals(player.connectedFrom);
	}
	
	/**
	 * Returns a single instance of the HelpDatabase class
	 * @return	the help database
	 */
	public static HelpDatabase getDatabase() {
		return database;
	}
	
	private enum Button {
		SLOT_1(232182, 232183),
		SLOT_2(232187, 232188),
		SLOT_3(232192, 232193),
		SLOT_4(232197, 232198),
		SLOT_5(232202, 232203),
		SLOT_6(232207, 232208),
		SLOT_7(232212, 232213),
		SLOT_8(232217, 232218),
		SLOT_9(232222, 232223),
		SLOT_10(232227, 232228),
		SLOT_11(232232, 232233),
		SLOT_12(232237, 232238),
		SLOT_13(232242, 232243),
		SLOT_14(232247, 232248),
		SLOT_15(232252, 232253),
		SLOT_16(233001, 233002),
		SLOT_17(233006, 233007),
		SLOT_18(233011, 233012),
		SLOT_19(233016, 233017),
		SLOT_20(233021, 233022);
		
		private int viewId, removeId;
		
		private Button(int viewId, int removeId) {
			this.viewId = viewId;
			this.removeId = removeId;
		}
	}
}
