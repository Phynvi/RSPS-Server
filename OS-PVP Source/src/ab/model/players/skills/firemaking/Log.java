package ab.model.players.skills.firemaking;

public enum Log {
	NORMAL(1511, 1, 40),
	OAK(1521, 15, 60),
	WILLOW(1519, 30, 90),
	MAPLE(1517, 45, 135),
	YEW(1515, 60, 202),
	MAGIC(1513, 75, 303);
	
	/**
	 * The item id of the log
	 */
	private final int itemId;
	
	/**
	 * The level required to burn the log
	 */
	private final int level;
	
	/**
	 * The experience gained from burning a log
	 */
	private final int experience;
	
	/**
	 * Constructs a new {@link Log} element
	 * @param itemId		the item id of the log
	 * @param level			the level required
	 * @param experience	the experience gained from burning
	 */
	private Log(int itemId, int level, int experience) {
		this.itemId = itemId;
		this.level = level;
		this.experience = experience;
	}
	
	/**
	 * The item id of the log
	 * @return	the item id
	 */
	public int getItemId() {
		return itemId;
	}
	
	

}
