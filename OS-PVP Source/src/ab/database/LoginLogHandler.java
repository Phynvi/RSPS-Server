package ab.database;

import java.sql.SQLException;
import java.sql.Timestamp;

import ab.Config;
import ab.model.players.Player;

public class LoginLogHandler {
	
	private static final int BATCH_SIZE = 2;
	
	private final String query = "INSERT INTO logins (DATE, TYPE, PLAYER, IP, MAC) VALUES (?, ?, ?, ?, ?)";
	
	private BatchQuery batch;
	private int batchCounter;
	
	public LoginLogHandler() {
		resetBatch();
	}
	
	public synchronized void logLogin(Player player, String type) {
		if (Config.mySql) {
		try {
			batch.getStatement().setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			batch.getStatement().setString(2, type);
			batch.getStatement().setString(3, player.playerName);
			batch.getStatement().setString(4, getIP(player));
			batch.getStatement().setString(5, getMac(player));
			batch.getStatement().addBatch();
			batchCounter++;
			if (batchCounter >= BATCH_SIZE) {
				System.out.println("Executing login log Query");
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
	
	private String getMac(Player c) {
		if (c.getRights().isBetween(1, 3)) {
			return "Private";
		}
		return c.getMacAddress();
	}

}
