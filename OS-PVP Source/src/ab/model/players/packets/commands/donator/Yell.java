package ab.model.players.packets.commands.donator;

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
		if (c.getRights().isStaff()) {
			return;
		}
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
		if (!c.lastYell.elapsed(35000) && c.getRights().isContributor()) {
			c.sendMessage("You are a @red@Contributor@bla@ and must wait 35 seconds between each yell.");
			return;
		}
		if (!c.lastYell.elapsed(30000) && c.getRights().isSponsor()) {
			c.sendMessage("You are a @blu@Sponsor@bla@ and must wait 30 seconds between each yell.");
			return;
		}
		if (!c.lastYell.elapsed(25000) && c.getRights().isSupporter()) {
			c.sendMessage("You are a @gre@Supporter@bla@ and must wait 25 seconds between each yell.");
			return;
		}
		if (!c.lastYell.elapsed(15000) && c.getRights().isVIP()) {
			c.sendMessage("You are a <col=FF00CD>VIP</col>@bla@ and must wait 15 seconds between each yell.");
			return;
		}
		if (!c.lastYell.elapsed(10000) && c.getRights().isSuperVIP()) {
			c.sendMessage("You are a @yel@Super VIP@bla@ and must wait 10 seconds between each yell.");
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
		c.lastYell.reset();
		message = Misc.ucFirst(message);
		PlayerHandler.executeGlobalMessage(rank + message);
	}
}
