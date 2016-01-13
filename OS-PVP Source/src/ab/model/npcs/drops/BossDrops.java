package ab.model.npcs.drops;

import java.util.ArrayList;
import java.util.Random;

import ab.model.items.GameItem;

/**
 * Class representing an {@link ArrayList} of all possible drops a boss can
 * drop.
 * 
 * @author Snappie
 *
 */
public class BossDrops extends ArrayList<Drop> {

	private static final long serialVersionUID = 8162026669568252114L;

	/**
	 * Method which determines which items should be dropped.
	 * 
	 * @param modifier
	 *            The drop chance modifier. A higher modifier means there is a
	 *            higher chance for items to be dropped. E.g. 1.5 as modifier
	 *            would be a 50% higher drop chance
	 * @return An {@link ArrayList} of all items that should be dropped.
	 * 
	 */
	public ArrayList<GameItem> getDrops(double modifier) {
		ArrayList<GameItem> drops = new ArrayList<>();
		ArrayList<GameItem> common = new ArrayList<>();
		Random rand = new Random();
		for (Drop drop : this) {
			double dropRate = drop.getRarity() / modifier;
			int amount = rand.nextInt(drop.getMaxAmount() - drop.getMinAmount() + 1) + drop.getMinAmount();
			if (rand.nextDouble() * dropRate <= 1) {
				drops.add(new GameItem(drop.getItemId(), amount));
			}
			if (drop.getRarity() <= 50) {
				common.add(new GameItem(drop.getItemId(), amount));
			}
		}
		if (drops.size() == 0 && common.size() > 0) {
			int index = rand.nextInt(common.size() - 1);
			drops.add(common.get(index));
		}
		return drops;
	}

	public Drop getDrop(Integer itemID) {
		for (Drop drop : this) {
			if (drop.getItemId() == itemID) {
				return drop;
			}
		}
		return null;
	}

}
