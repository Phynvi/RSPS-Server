package ab.model.players.skills.agility.impl;

import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Player;

/**
 * WildernessAgility
 * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 */

public class WildernessAgility {

	public static final int WILDERNESS_PIPE_OBJECT = 2288,
			WILDERNESS_SWING_ROPE_OBJECT = 2283,
			WILDERNESS_STEPPING_STONE_OBJECT = 2311,
			WILDERNESS_LOG_BALANCE_OBJECT = 2297,
			WILDERNESS_ROCKS_OBJECT = 2328;

	public boolean wildernessCourse(final Player c, final int objectId) {
		switch (objectId) {
		case WILDERNESS_PIPE_OBJECT: // pipe
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3004, 3937)) {
				c.getAgilityHandler().walk(c, 0, 13, c.getAgilityHandler().getAnimation(objectId), 748);
			} else if (c.absX == 3004 && c.absY > 3937 && c.absY < 3950) {
				c.getPlayerAssistant().movePlayer(3004, 3950, 0);
			}
			
			c.getAgilityHandler().resetAgilityProgress();
			c.getAgilityHandler().agilityProgress[0] = true;
                    
			return true;
		case WILDERNESS_SWING_ROPE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3005, 3953)) {
				c.getAgilityHandler().walk(c, 0, 1, c.getAgilityHandler().getAnimation(objectId), -1);
				if (c.getAgilityHandler().agilityProgress[0] == true) {
					c.getAgilityHandler().agilityProgress[1] = true;
                                        
				}
				
				  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			            @Override
			            public void execute(CycleEventContainer container) {
						if (c.disconnected) {
							container.stop();
							return;
						}
						c.getPlayerAssistant().movePlayer(3005, 3958, 0);
						container.stop();
					}
					@Override
						public void stop() {
							
						}
				}, 1);
			}
			return true;
		case WILDERNESS_STEPPING_STONE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3002, 3960)) {
				c.getAgilityHandler().walk(c, -6, 0, c.getAgilityHandler().getAnimation(objectId), -1);
			} else if (c.absX > 2996 && c.absX < 3002 && c.absY == 3960) {
				c.getPlayerAssistant().movePlayer(2996, 3960, 0);
			}
			
			c.getAgilityHandler().steppingStone = 6;
			c.getAgilityHandler().steppingStoneTimer = 2;
			c.getAgilityHandler().steppingStone--;
			if (c.getAgilityHandler().agilityProgress[1] == true) {
				c.getAgilityHandler().agilityProgress[3] = true;
                             
			}
			return true;

		case WILDERNESS_LOG_BALANCE_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3002, 3945)) {
				c.getAgilityHandler().walk(c, -8, 0, c.getAgilityHandler().getAnimation(objectId), -1);
				if (c.getAgilityHandler().agilityProgress[3] == true) {
					c.getAgilityHandler().agilityProgress[5] = true;
                                  
				}
				
			} else if (c.absX > 2994 && c.absX < 3002 && c.absY == 3945) {
				c.getPlayerAssistant().movePlayer(2994, 3945, 0);
			}
			return true;

		case WILDERNESS_ROCKS_OBJECT:
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			c.getAgilityHandler().walk(c, 0, -4, c.getAgilityHandler().getAnimation(objectId), -1);
			if (c.getAgilityHandler().agilityProgress[5] == true) {
				int experience = c.playerEquipment[c.playerCape] == 10071 ? 15600 : 15600;
				c.getPlayerAssistant().addSkillXP(experience, 16);   
				c.getAgilityHandler().lapBonus = 52_000;
				
				c.getAgilityHandler().lapFinished2(c);
			} else {
				
			}
			return true;
		}
		return false;
	}

}
