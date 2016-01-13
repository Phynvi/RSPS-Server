package ab.model.minigames.fight_cave;

import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.items.ItemAssistant;
import ab.model.npcs.NPCHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 17, 2013
 */
public class FightCave {
	
	private Player player;
	private int killsRemaining;
	
	public FightCave(Player player) {
		this.player = player;
	}
	
	public void spawn() {
		final int[][] type = Wave.getWaveForType(player);
		if(player.waveId >= type.length && player.waveType > 0 && Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
			stop();
			return;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer event) {
				if(player == null) {
					event.stop();
					return;
				}
				if(!Boundary.isIn(player, Boundary.FIGHT_CAVE)) {
					player.waveId = 0;
					player.waveType = 0;
					event.stop();
					return;
				}
				if(player.waveId >= type.length && player.waveType > 0) {
					stop();
					event.stop();
					return;
				}
				if(player.waveId != 0 && player.waveId < type.length)
					player.sendMessage("You are now on wave "+(player.waveId + 1)+" of "+type.length+".", 255);
				setKillsRemaining(type[player.waveId].length);
				for(int i = 0; i < getKillsRemaining(); i++) {
					int npcType = type[player.waveId][i];
					int index = Misc.random(Wave.SPAWN_DATA.length - 1);
					int x = Wave.SPAWN_DATA[index][0];
					int y = Wave.SPAWN_DATA[index][1];
					Server.npcHandler.spawnNpc(player, npcType, x, y, player.playerId * 4,
							1, Wave.getHp(npcType), Wave.getMax(npcType), Wave.getAtk(npcType), Wave.getDef(npcType), true, false);
				}
				event.stop();
			}
			
			@Override
			public void stop() {

				
			}
		}, 16);
	}
	
	public void leaveGame() {
		player.sendMessage("You have left the Fight Cave minigame.");
		player.getPA().movePlayer(2438, 5168, 0);
		player.waveType = 0;
		player.waveId = 0;
	}
	
	public void create(int type) {
		player.getPA().removeAllWindows();
		player.getPA().movePlayer(2413,5117, player.playerId * 4);
		player.waveType = type;
		player.sendMessage("Welcome to the Fight Cave minigame. Your first wave will start soon.", 255);
		player.waveId = 0;
		spawn();
	}
	
	public void stop() {
		reward();
		player.getPA().movePlayer(2438, 5168, 0);
		player.getDH().sendStatement("Congratulations for finishing Fight Caves on level ["+player.waveType+"]");
		player.waveInfo[player.waveType - 1] += 1;
		player.waveType = 0;
		player.waveId = 0;
		player.nextChat = 0;
		killAllSpawns();
	}
	
	public void handleDeath() {
		player.getPA().movePlayer(2438, 5168, 0);
		player.getDH().sendStatement("Unfortunately you died on wave "+player.waveId+". Better luck next time.");
		player.nextChat = 0;
		player.waveType = 0;
		player.waveId = 0;
		killAllSpawns();
	}
	
	public void killAllSpawns() {
		for(int i = 0; i < NPCHandler.npcs.length; i++) {
			if(NPCHandler.npcs[i] != null) {
				if(NPCHandler.isFightCaveNpc(i)) {
					if(NPCHandler.isSpawnedBy(player, NPCHandler.npcs[i])) {
						NPCHandler.npcs[i] = null;
					}
				}
			}
		}
	}
	
	private static final int[] REWARD_ITEMS = { 6571, 6528, 11128, 6523, 6524, 6525, 6526, 6527, 6568, 6523, 6524, 6525, 6526, 6527, 6568 };
	
	public static final int FIRE_CAPE = 6570;
	
	public static final int TOKKUL = 6529;
	
	public void reward() {
		switch(player.waveType) {
			case 1:
				player.getItems().addItem(FIRE_CAPE, 1);
				break;
			case 2:
				player.getItems().addItem(FIRE_CAPE, 1);
				break;
			case 3:
				int item = REWARD_ITEMS[Misc.random(REWARD_ITEMS.length - 1)];
				player.getItems().addItem(FIRE_CAPE, 1);
				player.getItems().addItem(item, 1);
				PlayerHandler.executeGlobalMessage(player.playerName + " has completed 63 waves of jad and received "
						+ ItemAssistant.getItemName(item) + ".");
				break;
		}
		player.getItems().addItem(TOKKUL, (10000 * player.waveType) + Misc.random(5000));
		Achievements.increase(player, AchievementType.TZ_TOK_JAD, 1);
	}

	public int getKillsRemaining() {
		return killsRemaining;
	}

	public void setKillsRemaining(int remaining) {
		this.killsRemaining = remaining;
	}

}
