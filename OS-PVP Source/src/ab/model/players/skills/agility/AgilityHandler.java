package ab.model.players.skills.agility;

import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.players.skills.agility.impl.GnomeAgility;
import ab.model.players.skills.agility.impl.WildernessAgility;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Player;


/**
 * AgilityHandler
 * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 */

public class AgilityHandler {

	public boolean[] agilityProgress = new boolean[6];
	public int lapBonus = 0;

	public static final int LOG_EMOTE = 762, PIPES_EMOTE = 844,
			CLIMB_UP_EMOTE = 828, CLIMB_DOWN_EMOTE = 827,
			CLIMB_UP_MONKEY_EMOTE = 3487, WALL_EMOTE = 840;
	
	public int steppingStone, steppingStoneTimer = 0, agilityTimer = -1,
			moveHeight = -1, tropicalTreeUpdate = -1, zipLine = -1;
	
	private int moveX, moveY, moveH;

	public void resetAgilityProgress() {
		for (int i = 0; i < 6; i++) {
			agilityProgress[i] = false;
		}
		lapBonus = 0;
	}
	
	/**
	 * sets a specific emote to walk to point x
	 */
	private void walkToEmote(Player c, int id) {
		c.isRunning2 = false;
		c.playerWalkIndex = id;
		c.getPA().requestUpdates(); // this was needed to make the
													// agility work
	}

	/**
	 * resets the player animation
	 */
	private void stopEmote(Player c) {
		//c.getCombat().getPlayerAnimIndex(c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
		c.getPlayerAction().setAction(false);
		c.getPlayerAction().canWalk(true);
		c.getPA().requestUpdates(); // this was needed to make the
													// agility work
		c.isRunning2 = true;
	}

	/**
	 * walk to point x with s a specific animation
	 */

	public void walk(Player c, int EndX, int EndY, int Emote, int endingAnimation) {
		c.getPlayerAction().setAction(true);
		c.getPlayerAction().canWalk(false);
		walkToEmote(c, Emote);
		c.getPA().walkTo2(EndX, EndY);
		destinationReached(c, EndX, EndY, endingAnimation);
	}

	/**
	 * when a player reaches he's point the stopEmote() method gets called this
	 * method calculates when the player reached he's point
	 */

	public void destinationReached(final Player c, int x2, int y2, final int endingEmote) {
		if (x2 >= 0 && y2 >= 0 && x2 != y2) {
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.disconnected) {
						container.stop();
						return;
					}
					if (moveHeight >= 0) {
						c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
						moveHeight = -1;
					}
					stopEmote(c);
					c.startAnimation(endingEmote);
					container.stop();
				}
				@Override
					public void stop() {
						
					}
			}, x2 + y2);
		} else if (x2 == y2) {
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.disconnected) {
						container.stop();
						return;
					}
					if (moveHeight >= 0) {
						c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
						moveHeight = -1;
					}
					stopEmote(c);
					c.startAnimation(endingEmote);
					container.stop();
				}
					@Override
					public void stop() {
						
					}
			}, x2);
		} else if (x2 < 0) {
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.disconnected) {
						container.stop();
						return;
					}
					if (moveHeight >= 0) {
						c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
						moveHeight = -1;

					}
					stopEmote(c);
					c.startAnimation(endingEmote);
					container.stop();
				}
				@Override
					public void stop() {
						
					}
			}, -x2 + y2);
		} else if (y2 < 0) {
			  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (c.disconnected) {
						container.stop();
						return;
					}
					if (moveHeight >= 0) {
						c.getPA().movePlayer(c.getX(), c.getY(), moveHeight);
						moveHeight = -1;

					}
					stopEmote(c);
					c.startAnimation(endingEmote);
					container.stop();
				}
				@Override
					public void stop() {
						
					}
			}, x2 - y2);
		}
	}

	/**
	 * @param objectId
	 *            : the objectId to know how much exp a player receives
	 */

	public double getXp(int objectId) {
		switch (objectId) {
		case GnomeAgility.TREE_OBJECT:
		case GnomeAgility.TREE_BRANCH_OBJECT:
			return 5;
		case GnomeAgility.LOG_OBJECT:
		case GnomeAgility.PIPES1_OBJECT:
		case GnomeAgility.PIPES2_OBJECT:
		case GnomeAgility.NET2_OBJECT:
		case GnomeAgility.NET1_OBJECT:
		case GnomeAgility.ROPE_OBJECT:
			return 7.5;
		case WildernessAgility.WILDERNESS_PIPE_OBJECT:
			return 12;
		case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
		case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
		case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
			return 20;
		case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
			return 0;
		}
		return -1;
	}

	/**
	 * @param objectId
	 *            : the objectId to fit with the right agility level required
	 */

	private int getLevelRequired(int objectId) {
		switch (objectId) {
		case WildernessAgility.WILDERNESS_PIPE_OBJECT:
		case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
		case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
		case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
		case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
			return 52;
		}
		return -1;
	}

	/**
	 * @param objectId
	 *            : the objectId to fit with the right animation played
	 */

	public int getAnimation(int objectId) {
		switch (objectId) {
		case GnomeAgility.LOG_OBJECT:
		case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
		case GnomeAgility.ROPE_OBJECT:
		case 2332:
			return LOG_EMOTE;
		case 154:
		case 4084:
		case 9330:
		case 9228:
		case 5100:
		case WildernessAgility.WILDERNESS_PIPE_OBJECT:
			return PIPES_EMOTE;
		case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
			return 3067;
		case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
			return 1604; // 2588
		case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
			return 1148;
		}
		return -1;
	}

	/**
	 * climbUp a ladder or anything. small delay before getting teleported to
	 * destination
	 */

	public void climbUp(final Player c, final int moveX, final int moveY, final int moveH) {
		c.startAnimation(CLIMB_UP_EMOTE);
		c.getPlayerAction().setAction(true);
		c.getPlayerAction().canWalk(false);
		  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				if (c.disconnected) {
					stop();
					return;
				}
				c.getPlayerAction().setAction(false);
				c.getPlayerAction().canWalk(true);
				c.getPA().movePlayer(moveX, moveY, moveH);
				container.stop();
			}
			@Override
				public void stop() {
					
				}
		}, 1);
	}

	/**
	 * climbDown a ladder or anything. small delay before getting teleported to
	 * destination
	 */

	public void climbDown(final Player c, final int moveX, final int moveY, final int moveH) {
		c.startAnimation(CLIMB_DOWN_EMOTE);
		c.getPlayerAction().setAction(true);
		c.getPlayerAction().canWalk(false);
		  CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				if (c.disconnected) {
					stop();
					return;
				}
				c.getPlayerAction().setAction(false);
				c.getPlayerAction().canWalk(true);
				c.getPA().movePlayer(moveX, moveY, moveH);
				container.stop();
			}
			@Override
				public void stop() {
					
				}
		}, 1);
	}

	/**
	 * a specific position the player has to stand on before the action is set
	 * to true
	 */

	public boolean hotSpot(Player c, int hotX, int hotY) {
		if (c.getX() == hotX && c.getY() == hotY) {
			return true;
		}
		return false;
	}

	public void lapFinished2(Player c) {
		if (agilityProgress[5]) {
			c.sendMessage("You received some bonus XP and coins for completing the track!");
                        Achievements.increase(c, AchievementType.AGILITY, 1);
			resetAgilityProgress();
		}
	}
	public void lapFinished(Player c) {
		if (agilityProgress[5]) {
			c.sendMessage("You received some bonus XP and coins for completing the track!");
                        Achievements.increase(c, AchievementType.AGILITY, 1);
			resetAgilityProgress();
		}
	}

	/**
	 * 600 ms process for some agility actions
	 */

	public void agilityProcess(Player c) {
		if (steppingStone > 0 && steppingStoneTimer == 0) {
			walk(c, -1, 0, getAnimation(WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT), -1);
			steppingStone--;
			steppingStoneTimer = 2;
		}

		if (steppingStoneTimer > 0) {
			steppingStoneTimer--;
		}

		if (hotSpot(c, 3363, 2851)) {
			moveX = 3368;
			moveY = 2851;
			moveH = 1;
			walk(c, 1, 0, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3372, 2832)) {
			moveX = 3367;
			moveY = 2832;
			moveH = 1;
			walk(c, -1, 0, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3364, 2832)) {
			moveX = 3359;
			moveY = 2832;
			moveH = 1;
			walk(c, -1, 0, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3357, 2836)) {
			moveX = 3357;
			moveY = 2841;
			moveH = 2;
			walk(c, 0, 1, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3357, 2846)) {
			moveX = 3357;
			moveY = 2849;
			moveH = 2;
			walk(c, 0, 1, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3359, 2849)) {
			moveX = 3366;
			moveY = 2849;
			moveH = 2;
			walk(c, 1, 0, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3372, 2841)) {
			moveX = 3372;
			moveY = 2836;
			moveH = 2;
			walk(c, 0, -1, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3366, 2834)) {
			moveX = 3363;
			moveY = 2834;
			moveH = 2;
			walk(c, -1, 0, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3359, 2842)) {
			moveX = 3359;
			moveY = 2847;
			moveH = 3;
			walk(c, 0, 1, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
			agilityTimer = 2;
		}

		if (hotSpot(c, 3370, 2843)) {
			moveX = 3370;
			moveY = 2840;
			moveH = 3;
			walk(c, 0, -1, 2753, -1);
			c.getPA().addSkillXP(14, c.playerAgility);
		}

		if (agilityTimer > 0) {
			agilityTimer--;
		}

		if (agilityTimer == 0) {
			c.getPA().movePlayer(moveX, moveY, moveH);
			moveX = -1;
			moveY = -1;
			moveH = 0;
			agilityTimer = -1;
		}

	}

	public boolean checkLevel(Player c, int objectId) {
		if (getLevelRequired(objectId) > c.playerLevel[c.playerAgility]) {
			c.sendMessage("You need atleast " + getLevelRequired(objectId) + " agility to do this.");
			return true;
		}
		return false;
	}

	static int changeObjectTimer = 10;
	static int rndChance;
	static int newObjectX, newObjectY;

}
