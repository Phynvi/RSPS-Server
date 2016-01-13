package ab.model.players.packets.commands.moderator;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Move the player a given amount of tiles in a given direction.
 * 
 * @author Emiel
 *
 */
public class Move implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			String[] args = input.split(" ");
			int positionOffset = Integer.parseInt(args[1]);
			int x = c.absX;
			int y = c.absY;
			int height = c.heightLevel;
			switch (args[0].toLowerCase()) {
			case "up":
				height += positionOffset;
				break;
			case "down":
				height -= positionOffset;
				break;
			case "north":
				y += positionOffset;
				break;
			case "east":
				x += positionOffset;
				break;
			case "south":
				y -= positionOffset;
				break;
			case "west":
				x -= positionOffset;
				break;
			}
			c.getPA().movePlayer(x, y, height);
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::move up/down/north/east/south/west amount");
		}
	}
}
