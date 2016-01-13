package ab.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ab.model.players.Player;

/**
 * A MySQL handler used to insert rows in the punishment table. Each query is
 * executed on a unique Thread to insure the response time of the query does not
 * affect the remaining tasks the original Thread has to execute. A new instance
 * of this class should be created every time a query has to be executed to
 * avoid a new query being initialized before the old one has been executed.
 * 
 * @author Snappie
 *
 */
public class PunishmentHandler extends Thread {

	/**
	 * The connection with the database.
	 */
	private Connection con;
	
	/**
	 * The Java statement.
	 */
	private Statement stmt;

	/**
	 * The query which is to be executed after initialization.
	 */
	private String query;

	/**
	 * Insert a row in the punishment database.
	 * 
	 * @param player
	 *            The punished player.
	 * @param staff
	 *            The staff member who punished the player.
	 * @param punishType
	 *            The punishment (jail, kick, mute or ban).
	 * @param reason
	 *            Why the player was punished.
	 */
	public void punishOnlinePlayer(Player player, Player staff, String punishType, String reason) {
		if (player == null || player.playerName == null || player.connectedFrom == null) {
			punishOfflinePlayer("Unknown", staff, punishType, reason);
			return;
		}
		String ip;
		if (player.getRights().isBetween(1, 3)) {
			ip = "Private";
		} else {
			ip = player.connectedFrom;
		}
		String staffName;
		if (staff == null || staff.playerName == null) {
			staffName = "Unknown";
		} else {
			staffName = staff.playerName;
		}
		query = "INSERT into punishments (TYPE, DATE, PLAYER, PLAYER_IP, STAFF, REASON) VALUES('" + punishType + "', NOW(), '" + player.playerName
				+ "', " + "'" + ip + "', '" + staffName + "', '" + reason.replaceAll("'", "\\\\'") + "')";
		run();
	}

	/**
	 * Insert a row in the punishment database when only the name of the
	 * punished player is known. Use this if there is no Client object of the
	 * punished player such as when the player is not online.
	 * 
	 * @param player
	 *            The name of the punished player.
	 * @param staff
	 *            The staff member who punished the player.
	 * @param punishType
	 *            The punishment (jail, kick, mute or ban).
	 * @param reason
	 *            Why the player was punished.
	 */
	public void punishOfflinePlayer(String player, Player staff, String punishType, String reason) {
		String staffName;
		if (staff == null || staff.playerName == null) {
			staffName = "Unknown";
		} else {
			staffName = staff.playerName;
		}
		query = "INSERT into punishments (TYPE, DATE, PLAYER, PLAYER_IP, STAFF, REASON) VALUES('" + punishType + "', NOW(), '" + player + "', "
				+ "'Unknown', '" + staffName + "', '" + reason.replaceAll("'", "\\\\'") + "')";
		run();
	}

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		if (query == null) {
			return;
		}
		makeConnection();
		executeQuery(query);
		terminateConnection();
	}

	/**
	 * Creates a connection with the database.
	 */
	private void makeConnection() {
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
		/*	con = ConnectionPool.getConnection();
			stmt = con.createStatement();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute the specified query and return the result if the query selects a
	 * portion of the database.
	 * 
	 * @param query
	 *            The query which is to be executed.
	 * @return The return set of the query, if any.
	 */
	public ResultSet executeQuery(String query) {
	/*	try {
			if (query.toLowerCase().startsWith("select")) {
				return stmt.executeQuery(query);
			}
			stmt.executeUpdate(query);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return null;
	}

	/**
	 * Terminates the existing connection with the database if existent.
	 */
	private void terminateConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}
	}

}
