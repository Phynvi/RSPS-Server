package ab.database;

import java.sql.SQLException;
import java.sql.Timestamp;

import ab.Config;
import ab.model.players.Player;

public class ChatLogHandler {
	
	private static final int BATCH_SIZE = 5;
	private final String query = "INSERT INTO chat (DATE, TYPE, PLAYER, IP, MESSAGE, RECIPIENT) VALUES (?, ?, ?, ?, ?, ?)";
	
	private BatchQuery batch;
	private int batchCounter;
	
	public ChatLogHandler() {
		resetBatch();
	}
	
	public synchronized void logMessage(Player c, String type, String recipient, String message) {
		if (Config.mySql) { 
		try {
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis())); 
			batch.getStatement().setString(2, type);
			batch.getStatement().setString(3, c.playerName);
			batch.getStatement().setString(4, getIP(c));
			batch.getStatement().setString(5, message);
			batch.getStatement().setString(6, recipient);
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				System.out.println("Executing chat log Query");
				batch.execute();
				resetBatch();				
			}
		} catch (SQLException e) {
			batch.execute();
			resetBatch();
			e.printStackTrace();
			}
		}
	}

	private void resetBatch() {
		batch = new BatchQuery(query);
		batchCounter = 0;
	}
	
	private String getIP(Player c) {
		if (c.getRights().isBetween(1, 3)) {
			return "Private";
		}
		return c.connectedFrom;
	}

}
