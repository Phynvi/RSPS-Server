package ab.model.players.packets.commands.moderator;

import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.Rights;
import ab.model.players.packets.commands.Command;


public class Random implements Command {

	@Override
	public void execute(Player player, String input) {
		String[] args = input.split("-");
		boolean ignore = false;
		if (args.length > 2) {
			player.sendMessage("Incorrect syntax; '::random-name'");
			return;
		}
		if (args.length == 2) {
			if (args[1].equals("ignore")) {
				if (!player.getRights().inherits(Rights.ADMINISTRATOR)) {
					player.sendMessage("You cannot force this, you do not inherit Administrative rights.");
					return;
				}
				ignore = true;
			} else {
				player.sendMessage("The second argument should contain 'ignore', please try again.");
				return;
			}
		}
		Optional<Player> online = PlayerHandler.getOptionalPlayer(args[0]);
		if (!online.isPresent()) {
			player.sendMessage("This player is not online.");
			return;
		}
		Player target = online.get();
		if (target.getInterfaceEvent().isActive()) {
			player.sendMessage("The event is already active for this player. It should resolve shortly.");
			return;
		}
		if (!target.getInterfaceEvent().isExecutable() && !ignore) {
			player.sendMessage("The event is deemed as 'un-executable'. This could be due to a number of reasons.");
			player.sendMessage("If you believe the event should ignore this, type ::random-name-ignore to do so.");
			player.sendMessage("Only administrative staff have this access.");
			return;
		}
		player.sendMessage("You have executed the random event for this player.");
		target.getInterfaceEvent().execute();
		
	}

}
