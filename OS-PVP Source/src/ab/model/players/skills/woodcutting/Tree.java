package ab.model.players.skills.woodcutting;

public enum Tree {
	NORMAL(new int[] {1276}, 1342, 1511, 1, 5, 20, 25, 15),
	OAK(new int[] {11756}, 1356, 1521, 15, 8, 50, 38, 25),
	WILLOW(new int[] {1308, 11761}, 7399, 1519, 30, 10, 60, 68, 35),
	MAPLE(new int[] {1307, 11762}, 7400, 1517, 45, 13, 75, 100, 45),
	YEW(new int[] {1309, 11758}, 7402, 1515, 60, 15, 100, 175, 60),
	MAGIC(new int[] {1306, 11764}, 7400, 1513, 75, 20, 125, 250, 75);
	
	private int[] treeIds;
	private int stumpId, wood, levelRequired, chopsRequired, deprecationChance, experience, respawn;
	
	private Tree(int[] treeIds, int stumpId, int wood, int levelRequired, int chopsRequired, int deprecationChance,
			int experience, int respawn) {
		this.treeIds = treeIds;
		this.stumpId = stumpId;
		this.wood = wood;
		this.levelRequired = levelRequired;
		this.experience = experience;
		this.deprecationChance = deprecationChance;
		this.chopsRequired = chopsRequired;
		this.respawn = respawn;
	}
	
	public int[] getTreeIds() {
		return treeIds;
	}
	
	public int getStumpId() {
		return stumpId;
	}
	
	public int getWood() {
		return wood;
	}
	
	public int getLevelRequired() {
		return levelRequired;
	}
	
	public int getChopsRequired() {
		return chopsRequired;
	}
	
	public int getChopdownChance() {
		return deprecationChance;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public int getRespawnTime() {
		return respawn;
	}
	
	public static Tree forObject(int objectId) {
		for (Tree tree : values()) {
			for (int treeId : tree.treeIds) {
				if (treeId == objectId) {
					return tree;
				}
			}
		}
		return null;
	}

}
