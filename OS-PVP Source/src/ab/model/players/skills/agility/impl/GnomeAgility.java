package ab.model.players.skills.agility.impl;

import ab.model.players.Player;

/**
 * GnomeAgility
 * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 */

public class GnomeAgility {
	
	private static long clickTimer = 0;

	public static final int LOG_OBJECT = 2295, NET1_OBJECT = 2285,
			TREE_OBJECT = 2313, ROPE_OBJECT = 2312, TREE_BRANCH_OBJECT = 2314,
			NET2_OBJECT = 2286, PIPES1_OBJECT = 154, PIPES2_OBJECT = 4058;// gnome
																			// course
																			// objects

	public boolean gnomeCourse(Player c, int objectId) {
		switch (objectId) {
		case LOG_OBJECT:
			if (c.getAgilityHandler().hotSpot(c, 2474, 3436)) {
				c.getAgilityHandler().walk(c, 0, -7, c.getAgilityHandler().getAnimation(objectId), -1);
			} else if (c.absX == 2474 && c.absY > 3429 && c.absY < 3436) {
				c.getPlayerAssistant().movePlayer(2474, 3429, 0);
			}
			
			c.getAgilityHandler().resetAgilityProgress();
			c.getAgilityHandler().agilityProgress[0] = true;
			return true;

		case NET1_OBJECT:
			c.getAgilityHandler().climbUp(c, c.getX(), c.getY() - 2, 1);
			
			if (c.getAgilityHandler().agilityProgress[0] == true) {
				c.getAgilityHandler().agilityProgress[1] = true;
			}
			return true;

		case TREE_OBJECT:
			c.getAgilityHandler().climbUp(c, c.getX(), c.getY() - 3, 2);
			
			if (c.getAgilityHandler().agilityProgress[1] == true) {
				c.getAgilityHandler().agilityProgress[2] = true;
			}
			return true;

		case ROPE_OBJECT:
			if (c.getAgilityHandler().hotSpot(c, 2477, 3420)) {
				c.getAgilityHandler().walk(c, 6, 0, c.getAgilityHandler().getAnimation(objectId), -1);
			} else if (c.absY == 3420 && c.absX > 2477 && c.absX < 2483) {
				c.getPlayerAssistant().movePlayer(2483, 3420, 2);
			}
			
			if (c.getAgilityHandler().agilityProgress[2] == true) {
				c.getAgilityHandler().agilityProgress[3] = true;
			}
			return true;

		case TREE_BRANCH_OBJECT:
			c.getAgilityHandler().climbDown(c, c.getX(), c.getY(), 0);
			
			if (c.getAgilityHandler().agilityProgress[3] == true) {
				c.getAgilityHandler().agilityProgress[4] = true;
			}
			return true;

		case NET2_OBJECT:
			if (System.currentTimeMillis() - clickTimer < 1800) {
				return false;
			}
			if (c.getY() == 3425 && System.currentTimeMillis() - clickTimer > 1800) {
				c.getAgilityHandler().climbUp(c, c.getX(), c.getY() + 2, 0);
				
				clickTimer = System.currentTimeMillis();
				if (c.getAgilityHandler().agilityProgress[4] == true) {
					c.getAgilityHandler().agilityProgress[5] = true;
				}
			}
			return true;

		case PIPES1_OBJECT:
			if (c.getAgilityHandler().hotSpot(c, 2484, 3430)) {
				c.getAgilityHandler().walk(c, 0, 7, c.getAgilityHandler().getAnimation(objectId), 748);
				if (c.getAgilityHandler().agilityProgress[5] == true) {
					int experience = c.playerEquipment[c.playerCape] == 10071 ? 6000 : 6000;
					c.getPlayerAssistant().addSkillXP(experience, 16);
					
					c.getAgilityHandler().lapBonus = 6000;
					c.getAgilityHandler().lapFinished(c);
				} else {
					
				}
				c.getAgilityHandler().resetAgilityProgress();
			} else if (c.absY > 3430 && c.absY < 3436 && System.currentTimeMillis() - clickTimer > 1800) {
				c.getPlayerAssistant().movePlayer(2484, 3437, 0);
			}
			return true;

		case PIPES2_OBJECT:
			if (c.getAgilityHandler().hotSpot(c, 2487, 3430)) {
				c.getAgilityHandler().walk(c, 0, 7, c.getAgilityHandler().getAnimation(objectId), 748);
				if (c.getAgilityHandler().agilityProgress[5] == true) {
					int experience = c.playerEquipment[c.playerCape] == 10071 ? 6000 : 6000;
					c.getPlayerAssistant().addSkillXP(experience, 16);
					
					c.getAgilityHandler().lapBonus = 6000;
					
					c.getAgilityHandler().lapFinished(c);
				} else {
					
				}
				c.getAgilityHandler().resetAgilityProgress();
			} else if (c.absY > 3430 && c.absY < 3436) {
				c.getPlayerAssistant().movePlayer(2487, 3437, 0);
			}
			return true;
		}
		return false;
	}
}
