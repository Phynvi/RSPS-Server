package ab.model.players.skills;

import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.world.Tile;
import ab.world.objects.GlobalObject;
import ab.clip.Region;
import ab.Config;

/**
 * @author Acquittal - Done
 **/

public class Firemaking {

	Player c;

	public Firemaking(Player c) {
		this.c = c;
	}

	@SuppressWarnings("unused")
	private static Tile currentTile;

	private static int[][] logsdata = {
		{ 1511, 1, 40, 5249 },
		{ 2862, 1, 40, 5249 }, 
		{ 1521, 15, 60, 5249 }, 
		{ 1519, 30, 105, 5249 },
		{ 6333, 35, 105, 5249 },
		{ 1517, 45, 135, 5249 }, 
		{ 10810, 45, 135, 5249 },
		{ 6332, 50, 158, 5249 }, 
		{ 1515, 60, 203, 5249 },
		{ 1513, 75, 304, 5249 },
		{ 7404, 1, 256, 5249 },
		{ 7405, 1, 256, 5249 },
		{ 7406, 1, 256, 5249 },
		{ 10328, 1, 256, 5249  },
		{ 10329, 1, 256, 5249 },
	};

	public static boolean playerLogs(Player c, int i, int l) {
		boolean flag = false;
		for (int kl = 0; kl < logsdata.length; kl++) {
			if ((i == logsdata[kl][0] && requiredItem(c, l)) || (requiredItem(c, i) && l == logsdata[kl][0])) {
				flag = true;
			}
		}
		return flag;
	}

	private static int getAnimation(Player c, int item, int item1) {
		int[][] data = { { 841, 6714 }, { 843, 6715 }, { 849, 6716 }, { 853, 6717 }, { 857, 6718 }, { 861, 6719 } };
		for (int i = 0; i < data.length; i++) {
			if (item == data[i][0] || item1 == data[i][0]) {
				return data[i][1];
			}
		}
		return 733;
	}

	private static boolean requiredItem(Player c, int i) {
		int[] data = { 841, 843, 849, 853, 857, 861, 590 };
		for (int l = 0; l < data.length; l++) {
			if (i == data[l]) {
				return true;
			}
		}
		return false;
	}

	public static void grabData(final Player c, final int useWith, final int withUse) {
		if (c == null)
			return;
		final int[] coords = new int[2];
		coords[0] = c.absX;
		coords[1] = c.absY;
		for (int i = 0; i < logsdata.length; i++) {
			if ((requiredItem(c, useWith) && withUse == logsdata[i][0] || useWith == logsdata[i][0] && requiredItem(c, withUse))) {
				if (c.playerLevel[11] < logsdata[i][1]) {
					c.sendMessage("You need a higher firemaking level to light this log!");
					return;
				}
				if (System.currentTimeMillis() - c.lastFire > 1200) {

					if (c.playerIsFiremaking) {
						return;
					}
					if (Region.getClipping(c.absX, c.absY, c.heightLevel) != 0 || Server.getGlobalObjects().anyExists(c.absX, c.absY, c.heightLevel)) {
						c.sendMessage("You cannot light a fire here.");
						return;
					}
					if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
						c.sendMessage("You cannot light a fire in the duel arena.");
						return;
					}
					final int[] time = new int[3];
					final int log = logsdata[i][0];
					final int fire = logsdata[i][3];
					if (System.currentTimeMillis() - c.lastFire > 3000) {
						c.startAnimation(getAnimation(c, useWith, withUse));
						time[0] = 4;
						time[1] = 3;
					} else {
						time[0] = 1;
						time[1] = 2;
					}

					c.playerIsFiremaking = true;

					Server.itemHandler.createGroundItem(c, log, coords[0], coords[1], c.heightLevel, 1, c.getId());

					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							Server.getGlobalObjects().add(new GlobalObject(
									fire, coords[0], coords[1], c.heightLevel, 0, 10, 120, -1));
							Server.itemHandler.removeGroundItem(c, log, coords[0], coords[1], c.heightLevel, false);
							c.playerIsFiremaking = false;
							container.stop();
						}

						@Override
						public void stop() {

						}
					}, time[0]);

					currentTile = new Tile(c.absX - 1, c.absY, c.heightLevel);
					if (Region.getClipping(c.getX() - 1, c.getY(), c.heightLevel, -1, 0)) {
						c.getPA().walkTo(-1, 0);
					} else if (Region.getClipping(c.getX() + 1, c.getY(), c.heightLevel, 1, 0)) {
						c.getPA().walkTo(1, 0);
					} else if (Region.getClipping(c.getX(), c.getY() - 1, c.heightLevel, 0, -1)) {
						c.getPA().walkTo(0, -1);
					} else if (Region.getClipping(c.getX(), c.getY() + 1, c.heightLevel, 0, 1)) {
						c.getPA().walkTo(0, 1);
					}
					Achievements.increase(c, AchievementType.FIREMAKING, 1);
					c.sendMessage("You light the logs.");
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							c.startAnimation(65535);
							container.stop();
						}

						@Override
						public void stop() {

						}
					}, time[1]);
					c.getPA().addSkillXP(logsdata[i][2] * Config.FIREMAKING_EXPERIENCE, 11);
					c.getItems().deleteItem(logsdata[i][0], c.getItems().getItemSlot(logsdata[i][0]), 1);
					c.turnPlayerTo(c.absX + 1, c.absY);
					c.lastFire = System.currentTimeMillis();
				}
			}
		}
	}
}