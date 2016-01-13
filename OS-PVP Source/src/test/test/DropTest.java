package test.test;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import ab.model.items.GameItem;
import ab.model.npcs.drops.DropList;

public class DropTest {
	
	private static final int iterations = 100000;
	private static final int bossID = 6449;
	private static final double modifier = 1.0 * 1.3 * 1.2;
	
	private static DropList dropList = new DropList("./Data/data/drops.dat");
	private static TreeMap<Integer, Integer> itemMap = new TreeMap<>();

	public static void main(String[] args) {
		for (int i = 0; i < iterations; i++) {
			ArrayList<GameItem> drops = dropList.get(bossID).getDrops(modifier);
			for (GameItem drop : drops) {
				if (itemMap.get(drop.getId()) == null) {
					itemMap.put(drop.getId(), 0);
				} else {
					itemMap.put(drop.getId(), itemMap.get(drop.getId()) + 1);
				}
			}
		}
		for (Entry<Integer, Integer> entry : itemMap.entrySet()) {
			try {
				System.out.println(""
						+ "Item ID: " + entry.getKey() + ", "
						+ "Rarity: " + Math.round(dropList.get(bossID).getDrop(entry.getKey()).getRarity() / modifier) + " "
						+ "Drop rate: " + iterations / entry.getValue());				
			} catch (ArithmeticException e) {
				// Didn't drop so drop rate was divided by zero.
				System.out.println(""
						+ "Item ID: " + entry.getKey() + ", "
						+ "Rarity: " + Math.round(dropList.get(bossID).getDrop(entry.getKey()).getRarity() / modifier) + " "
						+ "Drop rate: Didn't drop");				
			}
		}
	}

}
