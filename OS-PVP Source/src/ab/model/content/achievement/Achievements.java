package ab.model.content.achievement;

import java.util.EnumSet;
import java.util.Set;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;
/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 26, 2014
 */
public class Achievements {
	
	public enum Achievement {
		/**
		 * Tier 1 Achievement Start
		 */
		INTERMEDIATE_PKER(0, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 25 Players", 25, 1),
                ADVANCED_PKER(1, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 50 Players", 50, 1),
                FIGHTER(2, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 75 Players", 75, 1),
                WARRIOR(3, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 100 Players", 100, 1),
                KILLER(4, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 150 Players", 150, 1),
                ASSASSIN(5, AchievementTier.TIER_1, AchievementType.KILL_PLAYER, null, "Kill 200 Players", 200, 1),
                BOSS_KILLER(6, AchievementTier.TIER_1, AchievementType.KILL_BOSS, null, "Kill 100 Bosses", 100, 1),
                BOSS_SLAYER(7, AchievementTier.TIER_1, AchievementType.KILL_BOSS, null, "Kill 250 Bosses", 250, 1),
                AGILE(8, AchievementTier.TIER_1, AchievementType.AGILITY, null, "Complete 100 laps", 100, 1),
                FIREFIGHTER(9, AchievementTier.TIER_1, AchievementType.FIREMAKING, null, "Light 400 logs", 400, 1),
                HOBBY_FISHER(10, AchievementTier.TIER_1, AchievementType.FISHING, null, "Catch 500 fish", 500, 1),
                HERBLORE_STARTER(11, AchievementTier.TIER_1, AchievementType.HERBLORE, null, "Make 500 potions", 500, 1),
                WOODCUTTER(12, AchievementTier.TIER_1, AchievementType.WOODCUTTING, null, "Cut 500 logs", 500, 1),
                MINER(13, AchievementTier.TIER_1, AchievementType.MINING, null, "Mine 500 ores", 500, 1),
                SLAYER_NOOB(14, AchievementTier.TIER_1, AchievementType.SLAYER, null, "Complete 30 Slayer Tasks", 30, 1),
                CAVE_STROLLER(15, AchievementTier.TIER_1, AchievementType.TZ_TOK_JAD, null, "Complete the fight cave minigame three times", 3, 1),
                ARCHEOLOGIST(16, AchievementTier.TIER_1, AchievementType.BARROWS, null, "Complete the barrows minigame 100 times", 100, 1),
                PVM_STARTER(17, AchievementTier.TIER_1, AchievementType.CASKET, null, "Loot 100 clue scroll caskets", 100, 1),
		/**
		 * Tier 2 Achievement Start
		 */
		AMAZING_PKER(0, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 250 Players", 250, 2),
                HEAVENLY_PKER(1, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 300 Players", 300, 2),
                GODLIKE_PKER(2, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 400 Players", 400, 2),
                FURIOUS(3, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 500 Players", 500, 2),
                HELLRAISER(4, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 600 Players", 600, 2),
                MANIAC(5, AchievementTier.TIER_2, AchievementType.KILL_PLAYER, null, "Kill 700 Players", 700, 2),
                BOSS_SLAUGHTERER(6, AchievementTier.TIER_2, AchievementType.KILL_BOSS, null, "Kill 500 Bosses", 500, 2),
                BOSS_ADDICT(7, AchievementTier.TIER_2, AchievementType.KILL_BOSS, null, "Kill 1000 Bosses", 1000, 2),
                SNEAKY(8, AchievementTier.TIER_2, AchievementType.AGILITY, null, "Complete 300 laps", 300, 2),
                PYRO_STARTER(9, AchievementTier.TIER_2, AchievementType.FIREMAKING, null, "Light 750 logs", 750, 2),
                PRO_FISHER(10, AchievementTier.TIER_2, AchievementType.FISHING, null, "Catch 1000 fish", 1000, 2),
                HERBALIST(11, AchievementTier.TIER_2, AchievementType.HERBLORE, null, "Make 1000 potions", 1000, 2),
                ADVANCED_WOODCUTTER(12, AchievementTier.TIER_2, AchievementType.WOODCUTTING, null, "Cut 1000 logs", 1000, 2),
                MINING_MASTER(13, AchievementTier.TIER_2, AchievementType.MINING, null, "Mine 1000 ores", 1000, 2),
                SLAYER_ADDICT(14, AchievementTier.TIER_2, AchievementType.SLAYER, null, "Complete 75 Slayer Tasks", 75, 2),
                JADS_FRIEND(15, AchievementTier.TIER_2, AchievementType.TZ_TOK_JAD, null, "Complete the fight cave minigame five times", 5, 2),
                GRAVEDIGGER(16, AchievementTier.TIER_2, AchievementType.BARROWS, null, "Complete the barrows minigame 200 times", 200, 2),
                PVM_MASTER(17, AchievementTier.TIER_2, AchievementType.CASKET, null, "Loot 175 clue scroll caskets", 175, 2),
                
		/**
		 * Tier 3 Achievement Start
		 */
		INSANE(0, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 800 Players", 800, 3),
                PSYCHO(1, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 900 Players", 900, 3),
                PROFESSIONAL_PKER(2, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 1000 Players", 1000, 3),
                MASTER_PKER(3, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 1250 Players", 1250, 3),
                LEGENDARY_PKER(4, AchievementTier.TIER_3, AchievementType.KILL_PLAYER, null, "Kill 1500 Players", 1500, 3),
                BOSS_LYNCHER(5, AchievementTier.TIER_3, AchievementType.KILL_BOSS, null, "Kill 1750 Bosses", 1750, 3),
                BOSS_EXPERT(6, AchievementTier.TIER_3, AchievementType.KILL_BOSS, null, "Kill 2500 Bosses", 2500, 3),
                MASTER_OF_MOVEMENT(7, AchievementTier.TIER_3, AchievementType.AGILITY, null, "Complete 500 laps", 500, 3),
                PYROMASTER(8, AchievementTier.TIER_3, AchievementType.FIREMAKING, null, "Light 1250 logs", 1250, 3),
                MASTERFISHER(9, AchievementTier.TIER_3, AchievementType.FISHING, null, "Catch 2000 fish", 2000, 3),
                MASTER_OF_HERBLORE(10, AchievementTier.TIER_3, AchievementType.HERBLORE, null, "Make 2000 potions", 2000, 3),
                COMBAT_MIXER(11, AchievementTier.TIER_3, AchievementType.SUPER_COMBAT, null, "Make 1000 super combat potions", 1000, 3),
                LUMBERJACK(12, AchievementTier.TIER_3, AchievementType.WOODCUTTING, null, "Cut 2000 logs", 2000, 3),
                GOLD_DIGGER(13, AchievementTier.TIER_3, AchievementType.MINING, null, "Mine 2000 ores", 2000, 3),
                SLAYER_MASTER(14, AchievementTier.TIER_3, AchievementType.SLAYER, null, "Complete 150 Slayer Tasks", 150, 3),
                JADS_FEAR(15, AchievementTier.TIER_3, AchievementType.TZ_TOK_JAD, null, "Complete the fight cave minigame ten times", 10, 3),
                DHAROKS_COUSIN(16, AchievementTier.TIER_3, AchievementType.BARROWS, null, "Complete the barrows minigame 300 times", 300, 3),
                PVM_VETERAN(17, AchievementTier.TIER_3, AchievementType.CASKET, null, "Loot 250 clue scroll caskets", 250, 3);

		private AchievementTier tier;
		private AchievementRequirement requirement;
		private AchievementType type;
		private String description;
		private int amount, identification, points;
		
		Achievement(int identification, AchievementTier tier, AchievementType type, AchievementRequirement requirement, String description, int amount, int points) {
			this.identification = identification;
			this.tier = tier;
			this.type = type;
			this.requirement = requirement;
			this.description = description;
			this.amount = amount;
			this.points = points;
		}
		
		public int getId() {
			return identification;
		}
		
		public AchievementTier getTier() {
			return tier;
		}
		
		public AchievementType getType() {
			return type;
		}
		
		public AchievementRequirement getRequirement() {
			return requirement;
		}
		
		public String getDescription() {
			return description;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public int getPoints() {
			return points;
		}
		
		public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
		
		public static Achievement getAchievement(AchievementTier tier, int ordinal) {
			for(Achievement achievement : ACHIEVEMENTS)
				if(achievement.getTier() == tier && achievement.ordinal() == ordinal)
					return achievement;
			return null;
		}
		
		public static boolean hasRequirement(Player player, AchievementTier tier, int ordinal) {
			for(Achievement achievement : ACHIEVEMENTS) {
				if(achievement.getTier() == tier && achievement.ordinal() == ordinal) {
					if(achievement.getRequirement() == null)
						return true;
					if(achievement.getRequirement().isAble(player))
						return true;
				}
			}
			return false;
		}
	}
	
	public static void increase(Player player, AchievementType type, int amount) {
		/*for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getType() == type) {
				if(achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
					int currentAmount = player.getAchievements().getAmountRemaining(achievement.getTier().ordinal(), achievement.getId());
					int tier = achievement.getTier().ordinal();
					if(currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
						player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
						if((currentAmount + amount) >= achievement.getAmount()) {
							String name = achievement.name().replaceAll("_", " ");
							player.getAchievements().setComplete(tier, achievement.getId(), true);
							player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
							player.sendMessage("Achievement completed on tier "+(tier + 1)+": '"+achievement.name().toLowerCase().replaceAll("_", " ")+"' and receive "+achievement.getPoints()+" point(s).", 255);
							if(achievement.getTier().ordinal() > 0) {
								for(Player p : PlayerHandler.players) {
									if(p == null)
										continue;
									Player c = p;
									c.sendMessage("@red@[ACHIEVEMENT]@blu@ "+Misc.ucFirst(player.playerName)+" @bla@completed the achievement @blu@"+name+" @bla@on tier @blu@"+(tier + 1)+".");
								}
							}
						}
					}
				}
			}
		}*/
	}
	
	public static void reset(Player player, AchievementType type) {
		/*for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getType() == type) {
				if(achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
					if(!player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
						player.getAchievements().setAmountRemaining(achievement.getTier().ordinal(), achievement.getId(),
								0);
					}
				}
			}
		}*/
	}
	
	public static void complete(Player player, AchievementType type) {
		/*for(Achievement achievement : Achievement.ACHIEVEMENTS) {
			if(achievement.getType() == type) {
				if(achievement.getRequirement() != null && achievement.getRequirement().isAble(player)
						&& !player.getAchievements().isComplete(achievement.getTier().ordinal(), achievement.getId())) {
					int tier = achievement.getTier().ordinal();
					//String name = achievement.name().replaceAll("_", " ");
					player.getAchievements().setAmountRemaining(tier, achievement.getId(), achievement.getAmount());
					player.getAchievements().setComplete(tier, achievement.getId(), true);
					player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
					player.sendMessage("Achievement completed on tier "+(tier + 1)+": '"+achievement.name().toLowerCase().replaceAll("_", " ")+"' and receive "+achievement.getPoints()+" point(s).", 255);
				}
			}
		}*/
	}
	
	@SuppressWarnings("unused")
	public static void checkIfFinished(Player player) {
		//complete(player, AchievementType.LEARNING_THE_ROPES);
	}
	
	public static int getMaximumAchievements() {
		return Achievement.ACHIEVEMENTS.size();
	}
}
