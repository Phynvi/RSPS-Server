package ab.model.players.packets.commands.all;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Shows a list of commands.
 * 
 * @author Emiel
 *
 */
public class Commands implements Command {

	@Override
	public void execute(Player c, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		for (int i = 8144; i < 8195; i++) {
			c.getPA().sendFrame126("", i);
		}
		c.getPA().sendFrame126("@dre@OS PvP Commands", 8144);
		c.getPA().sendFrame126("", 8145);
		c.getPA().sendFrame126("@blu@::players@bla@ - Shows players online", 8147);
		c.getPA().sendFrame126("@blu@::check@bla@ - Claim your voting reward", 8148);
		c.getPA().sendFrame126("@blu@::getid itemname@bla@ - Check the ID of an item", 8149);
		c.getPA().sendFrame126("@blu@::empty@bla@ - Destroy all items in your inventory", 8150);
		c.getPA().sendFrame126("@blu@::vote@bla@ - Takes you to the voting page", 8151);
		c.getPA().sendFrame126("@blu@::forums@bla@ - Takes you to the forums", 8152);
		c.getPA().sendFrame126("@blu@::store@bla@ - Takes you to the store/donation page", 8153);
		c.getPA().sendFrame126("@blu@::changepassword newpass@bla@ - Changes your password", 8154);
		c.getPA().sendFrame126("@blu@::lock@bla@ - Locks/Unlocks your XP", 8155);
		c.getPA().sendFrame126("@blu@::char@bla@ - Teleports you to the Make-over Mage", 8156);
		c.getPA().sendFrame126("@blu@::claimpayment@bla@ - Claims your donation", 8157);
		c.getPA().sendFrame126("@blu@::rules@bla@ - Brings you to the rules thread", 8158);
		c.getPA().sendFrame126("@blu@::highscores@bla@ - Brings you to the highscores", 8159);
		c.getPA().sendFrame126("@blu@::mb/::wests/::gdz/::easts@bla@ - Teles you to these hotspots", 8160);
		c.getPA().sendFrame126("@blu@::counter@bla@ - Toggles your 474 counter on/off", 8161);
		c.getPA().sendFrame126("@blu@::orbs@bla@ - Toggles your 474 orbs on/off", 8162);
		c.getPA().sendFrame126("@blu@::hotkeys@bla@ - Toggles your hotkeys", 8163);
		c.getPA().sendFrame126("@blu@::help@bla@ - Brings up the help request interface", 8164);
		c.getPA().sendFrame126("", 8170);
		c.getPA().sendFrame126("@dre@Donator's Only", 8171);
		c.getPA().sendFrame126("@dre@::yell message@bla@ - Sends a global message", 8172);
		c.getPA().sendFrame126("@dre@::dz@bla@ - Teleports you to the donator's zone", 8173);
		c.getPA().sendFrame126("@dre@::changetitle newtitle@bla@ - Changes your title for 25M", 8174);
		c.getPA().sendFrame126("@dre@::donatortitle@bla@ - Gives you back your donator title for free", 8175);
		c.getPA().sendFrame126("@dre@::killtitle@bla@ - Gives you back your kill title for free", 8176);
		c.getPA().showInterface(8134);
	}
}
