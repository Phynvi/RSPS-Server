package ab;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;

import com.rspserver.motivote.MotivoteHandler;
import com.rspserver.motivote.Reward;

public class RewardHandler extends MotivoteHandler<Reward> {
	@Override
	public void onCompletion(Reward reward) {
		// SOME OF THIS CODE WILL BE DIFFERENT FOR YOUR SERVER, CHANGE IT
		// ACCORDINGLY. everything to do with motivote will stay the same!
		int itemID = -1;

		if (reward.rewardName().equalsIgnoreCase("vote points")) {
			itemID = 1464;
		}
		if (reward.rewardName().equalsIgnoreCase("pk points")) {
			itemID = 2996;
		}
		if (PlayerHandler.isPlayerOn(reward.username())) {
			Player p = PlayerHandler.getPlayer(reward.username());

			if (p != null && p.isActive == true) // check isActive to make sure
													// player is active. some
													// servers, like project
													// insanity, need extra
													// checks.
			{
				synchronized (p) {
					Player c = p;
					if (c.getItems().addItem(itemID, reward.amount())) {
						for (int j = 0; j < Server.playerHandler.players.length; j++) {
							if (Server.playerHandler.players[j] != null) {
								Player c2 = Server.playerHandler.players[j];
								c2.sendMessage("<img=10> <col=255>" + Misc.capitalize(c.playerName)
										+ " <col=0>has just voted for the server and been rewarded! <col=255>::vote <img=10>");
							}
						}
						c.sendMessage("<col=255>You've received your vote reward! Congratulations!");
						if (Config.BONUS_WEEKEND && itemID == 1464) {
							c.getItems().addItem(1464, 1);
						}
						if (Config.BONUS_WEEKEND && itemID == 2996) {
							c.getItems().addItem(2996, 10);
						}
						reward.complete();
					} else {
						c.sendMessage("<col=255>Could not give you your reward item, try creating space.");
					}
				}
			}
		}
	}
}