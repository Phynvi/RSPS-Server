package ab.database;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import ab.model.items.GameItem;
import ab.model.items.ItemAssistant;
import ab.model.players.Player;

public class KillLogHandler {
	
	private static final int BATCH_SIZE = 1;
	private final String query = "INSERT INTO kills (DATE, WINNER, IP, LOSER, IP2, DROPPED) VALUES (?, ?, ?, ?, ?, ?)";
	
	private BatchQuery batch;
	private int batchCounter;
	
	public KillLogHandler() {
		resetBatch();
	}
	
	public synchronized void logKill(Player killer, Player loser, List<GameItem> droppedItems) {
		try {
			String dropString = createDropString(droppedItems);
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			if (Objects.isNull(killer)) {
				batch.getStatement().setString(2, "Unknown");
				batch.getStatement().setString(3, "Unknown");				
			} else {
				batch.getStatement().setString(2, killer.playerName);
				batch.getStatement().setString(3, getIP(killer));				
			}
			if (Objects.isNull(loser)) {
				batch.getStatement().setString(4, "Unknown");
				batch.getStatement().setString(5, "Unknown");				
			} else {
				batch.getStatement().setString(4, loser.playerName);
				batch.getStatement().setString(5, getIP(loser));				
			}
			batch.getStatement().setString(6, dropString);
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				System.out.println("Executing kill log Query");
				batch.execute();
				resetBatch();				
			}
		} catch (SQLException e) {
			batch.execute();
			resetBatch();
			e.printStackTrace();
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(query);
		batchCounter = 0;
	}
	
	private String createDropString(List<GameItem> droppedItems) {
		if (Objects.isNull(droppedItems) || droppedItems.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (GameItem item : droppedItems) {
			sb.append(ItemAssistant.getItemName(item.getId()));
			if (item.getAmount() > 1) {
				sb.append(" x" + item.getAmount() + "");
			}
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2);
	}
	
	private String getIP(Player c) {
		if (c.getRights().isBetween(1, 3)) {
			return "Private";
		}
		return c.connectedFrom;
	}

}
