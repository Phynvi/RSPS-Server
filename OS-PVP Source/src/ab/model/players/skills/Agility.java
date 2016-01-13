package ab.model.players.skills;

import ab.Config;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Player;
/**
 * Agility.java
 * 
 * @author Acquittal
 *
 *
 **/
 
public class Agility {

	public Player client;
	public int agtimer = 10;
	public boolean bonus = false;

	public Agility(Player c) {
		client = c;
	}	
	
	public void brimhavenMonkeyBars(Player c, String Object, int level, int x, int y, int a, int b, int xp)
	{
		if (c.playerLevel[c.playerAgility] < level) {
			c.sendMessage("You need a Agility level of "+ level +" to pass this " + Object + ".");
			return;
		}
		if (c.absX == a && c.absY == b) { 
			c.getPA().walkTo3(x, y);
			c.getPA().addSkillXP(xp, c.playerAgility);
			c.getPA().refreshSkill(c.playerAgility);
		}
	}
	
	/*
	 * Wilderness course
	 */
	
	public void wildernessEntrance(Player c, String Object, int level, int x, int y, int a, int b, int xp) {
		if (c.playerLevel[c.playerAgility] < level) {
			c.sendMessage("You need a Agility level of "+ level +" to pass this " + Object + ".");
			return;
		}
		if (c.absX == a && c.absY == b) { 
			c.getPA().walkTo3(x, y);
			c.getPA().addSkillXP(xp, c.playerAgility);
			c.getPA().refreshSkill(c.playerAgility);
		}
	}
	
	public void doWildernessEntrance(final Player c) {
		if (!c.foodDelay.elapsed(2000)) {
			return;
		}
			c.stopMovement();
			c.freezeTimer = 16;
			c.playerWalkIndex = 762;
			c.updateRequired = true;
			c.appearanceUpdateRequired = true;
			c.getAgility().wildernessEntrance(c, "Door", 1, 0, +15, 2998, 3917, 40 * Config.AGILITY_EXPERIENCE);
			c.foodDelay.reset();
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					c.playerStandIndex = 0x328;
					c.playerTurnIndex = 0x337;
					c.playerWalkIndex = 0x333;
					c.playerTurn180Index = 0x334;
					c.playerTurn90CWIndex = 0x335;
					c.playerTurn90CCWIndex = 0x336;
					c.playerRunIndex = 0x338;
					c.updateRequired = true;
					c.appearanceUpdateRequired = true;
					container.stop();
				}
				@Override
				public void stop() {
				}
			}, 14);
	}
}