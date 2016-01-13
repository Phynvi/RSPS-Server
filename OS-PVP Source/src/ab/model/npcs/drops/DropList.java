package ab.model.npcs.drops;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class representing a {@link HashMap} of all bosses and their respective
 * drops. Each key represents the ID of a boss and maps to a {@link BossDrops},
 * which is an {@link ArrayList} of all possible {@link Drop}s the given boss
 * may drop.
 * 
 * @author Snappie
 *
 */
public class DropList extends HashMap<Integer, BossDrops> {

	private static final long serialVersionUID = 3563200724596871610L;

	/**
	 * Initialize the {@link DropList} from a file.
	 * 
	 * @param path The path to a file containing data about bosses and drops.
	 */
	public DropList(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				processLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse a line of text. If the line has the valid format (5 integers separated with whitespaces), it will create a {@link Drop} Object and store it 
	 * @param line
	 */
	private void processLine(String line) {
		line = line.replaceAll("[^0-9\\s]", "");
		String[] args = line.split(" ");
		if (args.length == 5) {
			int[] values = new int[args.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = Integer.parseInt(args[i]);
			}
			Drop drop = new Drop(values[1], values[2], values[3], values[4]);
			if (containsKey(values[0])) {
				BossDrops bossDrops = get(values[0]);
				bossDrops.add(drop);
			} else {
				BossDrops bossDrops = new BossDrops();
				bossDrops.add(drop);
				put(values[0], bossDrops);
			}
		}
	}

}
