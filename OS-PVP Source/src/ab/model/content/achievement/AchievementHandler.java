package ab.model.content.achievement;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import ab.Server;
import ab.model.content.achievement.Achievements.Achievement;
import ab.model.npcs.NPC;
import ab.model.players.Player;
/**
 * 
 * @author Jason MacKeigan (http://www.rune-server.org/members/Jason)
 */
public class AchievementHandler {
	Player player;
	public int currentInterface;
	private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
	private static final int MAXIMUM_TIERS = 3;
	private int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
	private boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
	
	public int points;
	
	/**
	 * WARNING: ADD TO THE END OF THE LIST.
	 */
	private int boughtItems[][] = {
			{7409, -1},
			{13659, -1},
			{20120, -1},
			{88, -1},
			{13281, -1},
			{2379, -1},
			{20235, -1},
			{13845, -1},
			{13846, -1},
			{13847, -1},
			{13848, -1},
			{13849, -1},
			{13850, -1},
			{13851, -1},
			{13852, -1},
			{13853, -1},
			{13854, -1},
			{13855, -1},
			{13856, -1},
			{13857, -1},
			{20220, -1},
			{20221, -1},
			{20222, -1},
	};
	
	public AchievementHandler(Player player) {
		this.player = player;
	}
	
	public void print(BufferedWriter writer, int tier) {
		try {
			for(Achievements.Achievement achievement : Achievement.ACHIEVEMENTS) {
				if(achievement.getTier().ordinal() == tier) {
					if(amountRemaining[tier][achievement.getId()] > 0) {
						writer.write(achievement.name().toLowerCase() + " = "+amountRemaining[tier][achievement.getId()]+ "\t"+completed[tier][achievement.getId()]);
						writer.newLine();
					}
				}
			}
		} catch (Exception e) {}
	}
	
	public void read(String name, int tier, int amount, boolean state) {
		for(Achievements.Achievement achievement : Achievements.Achievement.ACHIEVEMENTS) {
			if(achievement.getTier().ordinal() == tier) {
				if(achievement.name().toLowerCase().equals(name)) {
					this.setComplete(tier, achievement.getId(), state);
					this.setAmountRemaining(tier, achievement.getId(), amount);
					break;
				}
			}
		}
	}
	
	public void drawInterface(int tier) {
		int scrollId = tier == 0 ? 49101 : tier == 1 ? 51101 : 53101;
		player.getPA().sendFrame171(tier == 0 ? 0 : 1, 49100);
		player.getPA().sendFrame171(tier == 1 ? 0 : 1, 51100);
		player.getPA().sendFrame171(tier == 2 ? 0 : 1, 53100);
		player.getPA().sendFrame36(800, tier == 0 ? 1 : 0);
		player.getPA().sendFrame36(801, tier == 1 ? 1 : 0);
		player.getPA().sendFrame36(802, tier == 2 ? 1 : 0);
		player.getPA().sendFrame126(Integer.toString(this.getPoints()), 49016);
		int components = 0;
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getTier().ordinal() == tier) {
				components++;
				int amount = getAmountRemaining(achievement.getTier().ordinal(), achievement.getId());
				if(amount > achievement.getAmount())
					amount = achievement.getAmount();
				player.getPA().sendFrame126(Integer.toString(achievement.getPoints()), scrollId + 300 + achievement.getId());
				player.getPA().sendFrame126(achievement.name().toUpperCase().replaceAll("_", " "), scrollId + 400 + achievement.getId());
				player.getPA().sendFrame126(achievement.getDescription(), scrollId + 500 + achievement.getId());
				player.getPA().sendFrame126(amount+"/"+achievement.getAmount(), scrollId + 700 + achievement.getId());
			}
		}
		player.getPA().sendFrame126(Integer.toString(components), 49020);
		player.getPA().showInterface(49000);
	}
	
	public void kill(NPC npc) {
		/*String name = Server.npcHandler.getNpcListName(npc.npcType).toLowerCase().replaceAll("_", " ");
		Achievements.increase(player, AchievementType.KILL_NPC, 1);
		if(name.contains("dragon") && !name.contains("baby"))
			Achievements.increase(player, AchievementType.KILL_DRAGON, 1);
		else if(name.contains("dragon") && name.contains("baby"))
			Achievements.increase(player, AchievementType.KILL_BABY_DRAGON, 1);
		List<String> checked = new ArrayList<>();
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(!achievement.getType().name().toLowerCase().contains("kill"))
				continue;
			if(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "").equalsIgnoreCase(name)) {
				if(checked.contains(achievement.getType().name().toLowerCase().replaceAll("_", " ")
						.replaceAll("kill ", "")))
					continue;
				Achievements.increase(player, achievement.getType(), 1);
				checked.add(achievement.getType().name().toLowerCase().replaceAll("_", " ")
					.replaceAll("kill ", ""));
			}
		}*/
	}
	
	public boolean hasCompletedAll() {
		int amount = 0;
		for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(isComplete(achievement.getTier().ordinal(), achievement.getId()))
				amount++;
		}
		return amount == Achievements.getMaximumAchievements();
	}
	
	public boolean completedTier(AchievementTier tier) {
		/*	for(Achievement achievement : Achievement.ACHIEVEMENTS)
				if(achievement.getTier() == tier)
					if(!isComplete(achievement.getTier().ordinal(), achievement.getId()))
						return false;*/
			return true;
	}
	
	public boolean isComplete(int tier, int index) {
		return completed[tier][index];
	}
	
	public boolean setComplete(int tier, int index, boolean state) {
		return this.completed[tier][index] = state;
	}

	public int getAmountRemaining(int tier, int index) {
		return amountRemaining[tier][index];
	}

	public void setAmountRemaining(int tier, int index, int amountRemaining) {
		this.amountRemaining[tier][index] = amountRemaining;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public boolean isAchievementItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				return true;
		return false;
	}
	
	public boolean hasBoughtItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				if(boughtItems[i][1] != -1)
					return true;
		return false;
	}
	
	public void setBoughtItem(int itemId) {
		for(int i = 0; i < boughtItems.length; i++)
			if(boughtItems[i][0] == itemId)
				boughtItems[i][1] = 1;
	}
	
	public int[][] getBoughtItems() {
		return this.boughtItems;
	}
	
	public void setBoughtItem(int index, int value) {
		if(index > this.boughtItems.length - 1)
			return;
		this.boughtItems[index][1] = value;
	}
	
}
