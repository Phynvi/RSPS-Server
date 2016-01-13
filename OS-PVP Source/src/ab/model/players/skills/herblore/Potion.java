package ab.model.players.skills.herblore;

import ab.model.items.GameItem;

public enum Potion {
	ATTACK_POTION(new GameItem(121), 3, 25, new GameItem(249), new GameItem(221)),
	ANTIPOISON(new GameItem(175), 5, 37, new GameItem(251), new GameItem(235)),
	STRENGTH_POTION(new GameItem(115), 12, 50, new GameItem(253), new GameItem(1526)),
	RESTORE_POTION(new GameItem(127), 22, 62, new GameItem(255), new GameItem(223)),
	GUTHIX_BALANCE(new GameItem(7662), 30, 75, new GameItem(257), new GameItem(223), new GameItem(1550), new GameItem(7650)),
	COMBAT_POTION(new GameItem(9741), 36, 84, new GameItem(255), new GameItem(9736)),
	PRAYER_POTION(new GameItem(139), 38, 87, new GameItem(257), new GameItem(231)),
	SUPER_ATTACK(new GameItem(145), 45, 100, new GameItem(259), new GameItem(221)),
	SUPER_ANTIPOISON(new GameItem(181), 48, 106, new GameItem(259), new GameItem(235)),
	SUPER_ENERGY(new GameItem(3018), 52, 117, new GameItem(261), new GameItem(2970)),
	SUPER_STRENGTH(new GameItem(157), 55, 125, new GameItem(263), new GameItem(225)),
	SUPER_RESTORE(new GameItem(3026), 63, 142, new GameItem(3000), new GameItem(223)),
	SUPER_DEFENCE(new GameItem(163), 66, 150, new GameItem(265), new GameItem(239)),
	ANTIDOTE_PLUS(new GameItem(5945), 68, 155, new GameItem(2998), new GameItem(6049)),
	ANTIFIRE(new GameItem(2454), 69, 157, new GameItem(2481), new GameItem(243)),
	RANGING(new GameItem(169), 72, 162, new GameItem(267), new GameItem(245)),
	MAGIC(new GameItem(3042), 76, 172, new GameItem(2481), new GameItem(3138)),
	ANTIDOTE_PLUS_PLUS(new GameItem(5954), 79, 178, new GameItem(259)),
	SARADOMIN_BREW(new GameItem(6687), 81, 180, new GameItem(2998), new GameItem(6693)),
	SUPER_COMBAT_POTION(new GameItem(12697), 90, 190, new GameItem(269), new GameItem(2436), new GameItem(2440), new GameItem(2442)),
	ANTI_VENOM(new GameItem(12907), 87, 120, new GameItem(5954), new GameItem(12934, 15)),
	ANTI_VENOM_PLUS(new GameItem(12915), 94, 125, new GameItem(12907), new GameItem(269));
	
	/**
	 * The primary ingredient required
	 */
	private final GameItem primary;
	
	/**
	 * An array of {@link GameItem} objects that represent the ingredients
	 */
	private final GameItem[] ingredients;
	
	/**
	 * The item received from combining the ingredients
	 */
	private final GameItem result;
	
	/**
	 * The level required to make this potion
	 */
	private final int level;
	
	/**
	 * The experience gained from making this potion
	 */
	private final int experience;
	
	/**
	 * Creates a new in-game potion that will be used in herblore
	 * @param result		the result from combining ingredients
	 * @param level			the level required
	 * @param experience	the experience
	 * @param ingredients	the ingredients to make the result
	 */
	private Potion(GameItem result, int level, int experience, GameItem primary, GameItem... ingredients) {
		this.result = result;
		this.level = level;
		this.experience = experience;
		this.primary = primary;
		this.ingredients = ingredients;
	}
	
	/**
	 * The result from combining the ingredients
	 * @return	the result
	 */
	public GameItem getResult() {
		return result;
	}
	
	/**
	 * The level required to combine the ingredients
	 * @return	the level required
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * The total amount of experience gained in the herblore skill
	 * @return	the experience gained
	 */
	public int getExperience() {
		return experience;
	}
	
	/**
	 * An array of {@link GameItem} objects that represent the ingredients required
	 * to create this potion.
	 * @return	the ingredients required
	 */
	public GameItem[] getIngredients() {
		return ingredients;
	}
	
	/**
	 * The primary ingredient required for the potion
	 * @return	the primary ingredient
	 */
	public GameItem getPrimary() {
		return primary;
	}

}
