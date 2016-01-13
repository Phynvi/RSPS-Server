package ab.model.players.skills;


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ab.model.players.Player;
import ab.util.Misc;

public class Slayer {

    public static final int EASY_TASK = 1, MEDIUM_TASK = 2, HARD_TASK = 3;

    private List<Task> tasks = new ArrayList<>();

    private Player player;

    public Slayer(Player player) {
        this.player = player;
    }

    public enum Task {
    	CHAOS_DRUID(2878, 1, EASY_TASK, "Taverly Dungeon"),
    	BLACK_KNIGHT(1545, 1, EASY_TASK, "Taverly Dungeon"),
    	MAGIC_AXE(2844, 1, EASY_TASK, "Taverly Dungeon"),
        ABYSSAL_DEMON(415, 85, HARD_TASK, "Slayer Tower"), 
        BANSHEE(414, 15, EASY_TASK, "Slayer Tower"), 
		BASILISK(417, 40, MEDIUM_TASK, "Relleka Dungeon"),
		COCKATRICE(419, 25, MEDIUM_TASK,"Relleka Dungeon"),
		KURASK(411, 70, HARD_TASK, "Relleka Dungeon"), 
        KURASK2(411, 70, MEDIUM_TASK, "Relleka Dungeon"),
		PYREFIEND(435, 31, EASY_TASK, "Relleka Dungeon"), 
        GIANT_BAT(2834, 1, EASY_TASK, "Taverley Dungeon"), 
        BLACK_DEMON(1432, 1, MEDIUM_TASK, "Taverley Dungeon"), 
        BLACK_DRAGON(259, 1, HARD_TASK, "Taverley Dungeon"), 
        BLOODVELD(484, 50, MEDIUM_TASK, "Slayer Tower"), 
        BLUE_DRAGON(268, 1, MEDIUM_TASK, "Taverley Dungeon"), 
        BRONZE_DRAGON(270, 1, HARD_TASK, "Brimhaven Dungeon"), 
        CRAWLING_HAND(448, 5, EASY_TASK,"Slayer Tower"), 
        DARK_BEAST(4005, 90, HARD_TASK, "Taverly Dungeon"), 
        DUST_DEVIL(424, 65, MEDIUM_TASK, "Slayer Tower"),
		DUST_DEVIL2(424, 65, HARD_TASK, "Slayer Tower"), 
        EARTH_WARRIOR(2840, 1, EASY_TASK, "Edgeville Dungeon"), 
        FIRE_GIANT(2084, 1, MEDIUM_TASK, "Brimhaven Dungeon"), 
        GARGOYLE(1543, 75, HARD_TASK, "Slayer Tower"),
        GHOST(85, 1, EASY_TASK, "Taverley Dungeon"), 
        CAVE_HORROR(3209, 58, MEDIUM_TASK, "Relleka Dungeon"),
        GREATER_DEMON(2026, 1, MEDIUM_TASK, "Brimhaven Dungeon"),
        GREATER_DEMON2(2026, 1, HARD_TASK, "Brimhaven Dungeon"),
        GREEN_DRAGON(264, 1, MEDIUM_TASK, "The Wilderness"), 
        HELLHOUND(135, 1, MEDIUM_TASK, "Taverley Dungeon"), 
		HELLHOUND2(135, 1, HARD_TASK, "Taverley Dungeon"),
        HILL_GIANT(2098, 1, MEDIUM_TASK, "Edgeville Dungeon"), 
        INFERNAL_MAGE(446, 45, MEDIUM_TASK, "Slayer Tower"), 
		IRON_DRAGON(273, 1, HARD_TASK, "Brimhaven Dungeon"), 
		LESSER_DEMON(2006, 1, MEDIUM_TASK, "Taverley Dungeon"), 
		MOSS_GIANT(891, 1, MEDIUM_TASK, "Brimhaven Dungeon"), 
		NECHRYAELS(11, 80, HARD_TASK, "Slayer Tower"),
		RED_DRAGON(247, 1, MEDIUM_TASK, "Brimhaven Dungeon"), 
		SKELETON(70, 1, EASY_TASK, "Edgeville Dungeon"),
		STEEL_DRAGON(274, 1, HARD_TASK, "Brimhaven Dungeon"),
		TZHAAR(2167, 1, HARD_TASK, "Tzhaar Cave");

        private int npcId, levelReq, diff;
        private String location;

        private Task(int npcId, int levelReq, int difficulty, String location) {
            this.npcId = npcId;
            this.levelReq = levelReq;
            this.location = location;
            this.diff = difficulty;
        }

        public int getNpcId() {
            return npcId;
        }

        public int getLevelReq() {
            return levelReq;
        }

        public int getDifficulty() {
            return diff;
        }

        public String getLocation() {
            return location;
        }
        
        static final Set<Task> TASKS = Collections.unmodifiableSet(EnumSet.allOf(Task.class));
        
        public static Task forNpc(int npc) {
        	Optional<Task> task = TASKS.stream().filter(t -> t.getNpcId() == npc).findFirst();
        	return task.orElse(null);
        }
    }

    public void resizeTable(int difficulty) {
    	tasks.clear();
    	int level = player.playerLevel[Skill.SLAYER.getId()];
    	for (Task task : Task.TASKS) {
    		outer:
    		for (int removed : player.removedTasks) {
    			if (task.getNpcId() == removed) {
    				continue outer;
    			}
    		}
    		if (level < task.getLevelReq()) {
    			continue;
    		}
    		if (task.getLevelReq() > 1 && level >= task.getLevelReq() && difficulty == HARD_TASK) {
    			tasks.add(task);
    			continue;
    		}
    		if (task.getDifficulty() == difficulty) {
    			tasks.add(task);
    			continue;
    		}
    	}
    }


    public int getRequiredLevel(int npcId) {
        for (Task task : Task.values())
            if (task.npcId == npcId)
                return task.levelReq;
        return -1;
    }


    public String getLocation(int npcId) {
        for (Task task : Task.values())
            if (task.npcId == npcId)
                return task.location;
        return "";
    }


    public boolean isSlayerNpc(int npcId) {
        for (Task task : Task.values()) {
            if (task.getNpcId() == npcId)
                return true;
        }
        return false;
    }


    public boolean isSlayerTask(int npcId) {
        if (isSlayerNpc(npcId)) {
            if (player.slayerTask == npcId) {
                return true;
            }
        }
        return false;
    }


    public String getTaskName(int npcId) {
        for (Task task : Task.values())
            if (task.npcId == npcId)
                return task.name().replaceAll("_", " ").replaceAll("2", "").toLowerCase();
        return "";
    }


    public int getTaskId(String name) {
        for (Task task : Task.values())
            if (task.name() == name)
                return task.npcId;
        return -1;
    }


    public boolean hasTask() {
        return player.slayerTask > 0 || player.taskAmount > 0;
    }


    public void generateTask() {
        if (hasTask() && !player.needsNewTask) {
            player.getDH().sendDialogues(3307, 1597);
            return;
        }
        if (hasTask() && player.needsNewTask) {
        	Task task = Task.forNpc(player.slayerTask);
            int difficulty = task.getDifficulty();
            if (difficulty == EASY_TASK) {
                player.getDH().sendDialogues(3309, 1597);
                player.needsNewTask = false;
            } else {
            	difficulty -= 1;
            	resizeTable(difficulty);
            	int taskId = player.slayerTask;
            	while (taskId == player.slayerTask) {
            		taskId = getRandomTask(difficulty);
            	}
                player.slayerTask = taskId;
                player.taskAmount = getTaskAmount(difficulty);
                player.needsNewTask = false;
                player.getDH().sendDialogues(3306, 1597);
            }
            return;
        }
    	int difficulty = getSlayerDifficulty();
    	resizeTable(difficulty);
        player.slayerTask = getRandomTask(difficulty);
        player.taskAmount = getTaskAmount(difficulty);
        player.getDH().sendDialogues(3306, 1597);
        player.sendMessage("You have been assigned " + player.taskAmount
                + " " + getTaskName(player.slayerTask) + ". Good luck, "
                + Misc.capitalize(player.playerName) + ".");
    }


    public int getTaskAmount(int difficulty) {
    	switch (difficulty) {
	    	case EASY_TASK:
	    		return 30 + Misc.random(30);
	    	case MEDIUM_TASK:
	    		return 60 + Misc.random(30);
	    	case HARD_TASK:
	    		return 90 + Misc.random(30);
    	}
    	return 50 + Misc.random(15);
    }


    public int getRandomTask(int difficulty) {
    	if (tasks.size() == 0) {
    		resizeTable(difficulty);
    	}
    	if (tasks.size() == 0) {
    		return Task.CRAWLING_HAND.getNpcId();
    	}
    	Task task = tasks.get(Misc.random(tasks.size() - 1));
    	return task.getNpcId();
    }


    public int getSlayerDifficulty() {
    	int level = player.playerLevel[Skill.SLAYER.getId()];
        if (player.combatLevel > 0 && player.combatLevel <= 45 || level < 40) {
            return EASY_TASK;
        } else if (player.combatLevel > 45 && player.combatLevel <= 110) {
            return MEDIUM_TASK;
        } else if (player.combatLevel > 100) {
            return HARD_TASK;
        }
        return EASY_TASK;
    }


    public void handleInterface(String shop) {
        if (shop.equalsIgnoreCase("buy")) {
            player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 41011);
            player.getPA().showInterface(41000);
        } else if (shop.equalsIgnoreCase("learn")) {
            player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 41511);
            player.getPA().showInterface(41500);
        } else if (shop.equalsIgnoreCase("assignment")) {
            player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 42011);
            updateCurrentlyRemoved();
            player.getPA().showInterface(42000);
        }
    }
    
    public void cancelTask() {
        if(!hasTask()) {
            player.sendMessage("You must have a task to cancel first.");
            return;
        }
        if(player.slayerPoints < 30) {
            player.sendMessage("This requires atleast 30 slayer points, which you don't have.");
            return;
        }
        player.sendMessage("You have cancelled your current task of "+player.taskAmount+" "+getTaskName(player.slayerTask)+".");
        player.slayerTask = -1;
        player.taskAmount = 0;
        player.slayerPoints -= 30;
    }
    
    public void removeTask() {
        int counter = 0;
        if(!hasTask()) {
            player.sendMessage("You must have a task to remove first.");
            return;
        }
        if(player.slayerPoints < 100) {
            player.sendMessage("This requires atleast 100 slayer points, which you don't have.");
            return;
        } 
        for(int i = 0; i < player.removedTasks.length; i++) {
            if(player.removedTasks[i] != -1) {
                counter++;
            }
            if(counter == 4) {
                player.sendMessage("You don't have any open slots left to remove tasks.");
                return;
            }
            if(player.removedTasks[i] == -1) {
                player.removedTasks[i] = player.slayerTask;
                player.slayerPoints -= 100;
                player.slayerTask = -1;
                player.taskAmount = 0;
                player.sendMessage("Your current slayer task has been removed, you can't obtain this task again.");
                updateCurrentlyRemoved();
                return;
            }
        }
    }
    
    public void updatePoints() {
        player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 41011);
        player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 41511);
        player.getPA().sendFrame126("Slayer Points: " + player.slayerPoints, 42011);
        player.getPA().sendFrame126("@red@Slayer Points: @or2@"+player.slayerPoints, 7336);
    }
    
    public void updateCurrentlyRemoved() {
        int line[] = {42014, 42015, 42016, 42017};
        for(int i = 0; i < player.removedTasks.length; i++) {
            if(player.removedTasks[i] != -1) {
                player.getPA().sendFrame126(this.getTaskName(player.removedTasks[i]), line[i]);
            } else {
                player.getPA().sendFrame126("", line[i]);
            }
        }
    }


    public void buySlayerExperience() {
        if(System.currentTimeMillis() - player.buySlayerTimer < 500)
            return;
        if(player.slayerPoints < 50) {
            player.sendMessage("You need at least 50 slayer points to gain 60,000 Experience.");
            return;
        }
        player.buySlayerTimer = System.currentTimeMillis();
        player.slayerPoints -= 50;
        player.getPA().addSkillXP(60000, 18);
        player.sendMessage("You spend 50 slayer points and gain 60,000 experience in slayer.");
        updatePoints();
    }
    
    public void buySlayerDart() {
        if(System.currentTimeMillis() - player.buySlayerTimer < 500)
            return;
        if(player.slayerPoints < 35) {
            player.sendMessage("You need at least 35 slayer points to buy Slayer darts.");
            return;
        }
        if(player.getItems().freeSlots() < 2 && !player.getItems().playerHasItem(560) && !player.getItems().playerHasItem(558)) {
            player.sendMessage("You need at least 2 free lots to purchase this.");
            return;
        }


        player.buySlayerTimer = System.currentTimeMillis();
        player.slayerPoints -= 35;
        player.sendMessage("You spend 35 slayer points and aquire 250 casts of Slayer darts.");
        player.getItems().addItem(558, 1000);
        player.getItems().addItem(560, 250);
        updatePoints();
    }
    
    public void buyBroadArrows() {
        if(System.currentTimeMillis() - player.buySlayerTimer < 500)
            return;
        if(player.slayerPoints < 25) {
            player.sendMessage("You need at least 25 slayer points to buy Broad arrows.");
            return;
        }
        if(player.getItems().freeSlots() < 1 && !player.getItems().playerHasItem(4160)) {
            player.sendMessage("You need at least 1 free lot to purchase this.");
            return;
        }
        player.buySlayerTimer = System.currentTimeMillis();
        player.slayerPoints -= 25;
        player.sendMessage("You spend 35 slayer points and aquire 250 Broad arrows.");
        player.getItems().addItem(4160, 250);
        updatePoints();
    }
    
    public void buyRespite() {
        if(System.currentTimeMillis() - player.buySlayerTimer < 1000)
            return;
        if(player.slayerPoints < 25) {
            player.sendMessage("You need at least 25 slayer points to buy Slayer's respite.");
            return;
        }
        if(player.getItems().freeSlots() < 1) {
            player.sendMessage("You need at least 1 free lot to purchase this.");
            return;
        }
        player.buySlayerTimer = System.currentTimeMillis();
        player.slayerPoints -= 25;
        player.sendMessage("You spend 25 slayer points and aquire a useful Slayer's respite.");
        player.getItems().addItem(5759, 1);
        updatePoints();
    }


}