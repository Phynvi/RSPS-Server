package ab.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query implements Runnable {
	
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
	 * @param query The query which is to be executed.
	 */
	public Query(String query) {
		this.query = query;
	}
	
	/**
	 * Create a new Thread and execute the Query.
	 */
	public void execute() {
		new Thread(this).start();
	}

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		if (query == null) {
			return;
		}
		setupConnection();
		executeQuery(query);
		terminateConnection();
		System.out.println("Query took: " + (System.currentTimeMillis() - start) + "ms.");
	}
	
	/**
	 * Creates a connection with the database.
	 */
	private void setupConnection() {
		try {
		/*	Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = ConnectionPool.getConnection();
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
	private ResultSet executeQuery(String query) {
		try {
			if (query.toLowerCase().startsWith("select")) {
				return stmt.executeQuery(query);
			}
			stmt.executeUpdate(query);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
