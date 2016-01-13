package ab.model.players.packets.commands.moderator;

import java.util.Optional;
import ab.Connection;
import ab.Server;
import ab.model.multiplayer_session.MultiplayerSession;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;

/**
 * A command used to lock the given player's account in instances where theft is suspected.
 * @author Chris
 * @date Aug 21, 2015 10:54:27 PM
 *
 */
public class Lock implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split(" ");
			if (args.length != 1) {
				c.sendMessage("Invalid arguments specified!");
				throw new IllegalArgumentException();
			}
			String name = args[0];
			Connection.lockAccount(name);
			Connection.addNameToLocks(name);

			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayer(name);
			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();
				if (c2.getRights().isBetween(2,  3)) {
					c.sendMessage("You cannot lock this player's account!");
					return;
				}
				if (Server.getMultiplayerSessionListener().inAnySession(c2)) {
					MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c2);
					session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				}
				c2.disconnected = true;
				c.sendMessage(name + "'s account has been locked.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			c.sendMessage("Correct usage: ::lock playername");
		}
	}
}
