package ab.model.players.packets.commands.all;

import java.util.Arrays;
import java.util.List;

import org.Vote.MainLoader;
import org.Vote.VoteReward;

import ab.Config;
import ab.model.items.GameItem;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.packets.commands.Command;
import ab.util.Misc;

/**
 * Give the player runes for the Barrage spell.
 * 
 * @author Emiel
 *
 */
public class Check implements Command {

	@Override
	public void execute(Player c, String input) {
		try {
			VoteReward reward = MainLoader.hasVoted(c.playerName.replaceAll(" ", "_"));
			if (reward != null) {
				switch (reward.getReward()) {
				case 0:
					if (Config.BONUS_WEEKEND) {
						c.votePoints += 1;
					}
					c.votePoints += 2;
					PlayerHandler.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(c.playerName)
							+ " </col>has just voted and received <col=CC0000>2x Vote Points</col>.");
					break;
				case 1:
					c.bonusXpTime = 3000;
					PlayerHandler.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(c.playerName)
							+ " </col>has just voted and received <col=CC0000>Bonus Experience</col>.");
					break;
				case 2:
					if (c.getItems().freeSlots() > 0) {
						c.getItems().addItem(12748, 1);
					} else {
						c.getItems().addItemToBank(12748, 1);
					}
					PlayerHandler.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(c.playerName)
							+ " </col>has just voted and received a <col=CC0000>Mysterious Emblem</col>.");
					break;

				case 3:
					List<GameItem> runes = Arrays.asList(
						new GameItem(560, 500),
						new GameItem(9075, 1000),
						new GameItem(557, 2500),
						new GameItem(565, 500),
						new GameItem(560, 1000),
						new GameItem(555, 1500)
					);
					if (c.getItems().freeSlots() > 5) {
						runes.forEach(item -> c.getItems().addItem(item.getId(), item.getAmount()));
					} else {
						runes.forEach(item -> c.getItems().addItemToBank(item.getId(), item.getAmount()));
					}
					PlayerHandler.executeGlobalMessage("<img=10></img><col=255>" + Misc.capitalize(c.playerName)
							+ " </col>has just voted and received <col=CC0000>Combat Runes (250 casts)</col>.");
					break;
				default:
					c.sendMessage("Reward not found.");
					break;
				}
				c.sendMessage("@blu@Thank you for voting! You have been rewarded.");
			} else {
				c.sendMessage("You have no items waiting for you.");
			}
		} catch (Exception e) {
			c.sendMessage("[GTL Vote] A SQL error has occured.");
		}
	}
}
