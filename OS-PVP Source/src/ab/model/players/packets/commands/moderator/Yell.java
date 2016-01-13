package ab.model.players.packets.commands.moderator;

import ab.Connection;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;
import ab.util.Misc;

/**
 * Send a global message.
 * 
 * @author Emiel
 */
public class Yell implements Command {
	
	static final String[] ILLEGAL_ARGUMENTS = {
		":tradereq:", "<img", "@cr", "<tran", "#url#", ":duelreq:", ":chalreq:"
	};

	@Override
	public void execute(Player c, String input) {
		String rank = "";
		String message = input;
		if (Connection.isMuted(c)) {
			c.sendMessage("You are muted and can therefore not yell.");
			return;
		}
		if (System.currentTimeMillis() < c.muteEnd) {
			c.sendMessage("You are muted and can therefore not yell.");
			return;
		}
		String playerTitle = c.getTitles().getCurrentTitle();
		if (c.getRights().isContributor()) {

			rank = "[@cr4@@red@" + playerTitle + "@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isSponsor()) {

			rank = "[@cr5@@blu@" + playerTitle + "@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isSupporter()) {

			rank = "[@cr6@<col=148200>" + playerTitle + "</col>@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isVIP()) {

			rank = "[@cr7@<col=FF00CD>" + playerTitle + "</col>@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isSuperVIP()) {

			rank = "[@cr8@@yel@" + playerTitle + "@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isRespectedMember()) {

			rank = "[@cr9@<col=FF00CD>" + playerTitle + "</col>@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isHelper()) {

			rank = "[@cr10@@blu@" + playerTitle + "@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		/* Staff */
		if (c.getRights().isModerator()) {

			rank = "[@cr1@<col=148200>Moderator</col>@bla@][@blu@" + c.playerName + "@bla@]:@dre@";
		}
		if (c.getRights().isAdministrator()) {

			rank = "[@cr2@@yel@Administrator@bla@][@blu@" + Misc.ucFirst(c.playerName) + "@bla@]:@dre@";
		}
		if (c.getRights().isOwner() && !c.playerName.equalsIgnoreCase("developer j")) {
			rank = "[@cr2@<col=A67711>Owner</col>@bla@][" + Misc.ucFirst(c.playerName) + "]:@dre@";
		}
		if (c.playerName.equalsIgnoreCase("developer j")) {
			rank = "[@cr2@<col=5E14A7>Developer</col>@bla@][" + Misc.ucFirst(c.playerName) + "]:@dre@";
		}
		message = message.toLowerCase();
		for (String argument : ILLEGAL_ARGUMENTS) {
			if (message.contains(argument)) {
				c.sendMessage("Your message contains an illegal set of characters, you cannot yell this.");
				return;
			}
		}
		message = Misc.ucFirst(message);
		PlayerHandler.executeGlobalMessage(rank + message);
	}
}
