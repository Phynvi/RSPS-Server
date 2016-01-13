package ab.model.players.skills;

import ab.Config;
import ab.model.players.Player;

public class Runecrafting extends SkillHandler {

	public static void craftRunesOnAltar(Player c, int requiredlvl, int exp,
			int item, int x2, int x3, int x4) {
		int essamount = 0;
		if (!hasRequiredLevel(c, 20, requiredlvl, "runecrafting",
				"craft this runes")) {
			return;
		}
		if (!c.getItems().playerHasItem(1436)) {
			c.sendMessage("You need some rune essence to craft runes!");
			return;
		}
		c.gfx100(186);
		c.startAnimation(791);
		if ((c.playerLevel[20]) >= 0 && (c.playerLevel[20] < x2)) {
			essamount = c.getItems().getItemAmount(1436);
		}
		if (c.playerLevel[20] >= x2 && c.playerLevel[20] < x3) {
			essamount = c.getItems().getItemAmount(1436) * 2;
		}
		if (c.playerLevel[20] >= x3 && c.playerLevel[20] < x4) {
			essamount = c.getItems().getItemAmount(1436) * 3;
		}
		if (c.playerLevel[20] >= x4) {
			essamount = c.getItems().getItemAmount(1436) * 4;
		}
		for (int i = 0; i < 29; i++) {
			c.getItems().deleteItem(1436, c.getItems().getItemSlot(1436), i);
		}
		c.getPA().addSkillXP((exp * essamount) * Config.RUNECRAFTING_EXPERIENCE, 20);
		c.getItems().addItem(item, essamount);
		c.sendMessage("You bind the temple's power into " + essamount + " "
				+ c.getItems().getItemName(item) + "s.");
		c.getPA().requestUpdates();
		essamount = -1;
	}
}