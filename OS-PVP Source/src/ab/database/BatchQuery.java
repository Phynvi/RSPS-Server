package ab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchQuery implements Runnable {
	
	/**
	 * The connection with the database.
	 */
	private Connection con;
	
	/**
	 * The Java statement.
	 */
	private PreparedStatement stmt;

	/**
	 * The query which is to be executed after initialization.
	 */
	private String query;
	
	/**
	 * @param query The query which is to be executed.
	 */
	public BatchQuery(String query) {
		this.query = query;
		setupConnection(query);
	}
	
	/**
	 * Create a new Thread and execute the Query.
	 */
	public void execute() {
		new Thread(this).start();
	}
	
	public synchronized void setValues(String[] values) throws SQLException {
		int index = 1;
		for (String value : values) {
			stmt.setString(index, value);
			index++;
		}
		stmt.addBatch();
	}
	
	public PreparedStatement getStatement() {
		return stmt;
	}
	
	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		if (query == null) {
			return;
		}
		executeQuery();
		terminateConnection();
	}
	
	/**
	 * Creates a connection with the database.
	 * @param query The query 
	 */
	private void setupConnection(String query) {
		try {
		/*	Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = ConnectionPool.getConnection();
			stmt = con.prepareStatement(query);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute the specified query and return the result if the query selects a
	 * portion of the database.
	 * 
	 * @return The return set of the query, if any.
	 */
	private int[] executeQuery() {
		try {
			if (query.toLowerCase().startsWith("select")) {
				return stmt.executeBatch();
			}
			stmt.executeBatch();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
