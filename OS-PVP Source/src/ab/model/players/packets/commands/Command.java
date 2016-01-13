package ab.model.players.packets.commands;

import ab.model.players.Player;

public interface Command {
	
	public void execute(Player c, String input);

}
