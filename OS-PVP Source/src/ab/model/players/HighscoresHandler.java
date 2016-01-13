package ab.model.players;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HighscoresHandler extends Thread {
	private final String HOST = "127.0.0.1";
	private final String DATABASE = "rusepsco_hs";
	private final String PASSWORD = "inyasha";
	private final String USERNAME = "root";
	private final String PORT = "3306";
	
	private Player c;
	private Connection con;
	private Statement stmt;
	private long total_level, total_exp;
	
	public HighscoresHandler(Player c) {
		this.c = c;
		c.inProcess = true;
	}
	private long getTotalLevel() { 
		long totallevel = 0L;
		for(int i = 0; i <= 20; i++) {
			if(c.getLevelForXP(c.playerXP[i]) >= 99) 
				totallevel += 99;
			else
				totallevel += (double) c.getLevelForXP(c.playerXP[i]);
		}
		return totallevel;
	}
	private long getTotalXp() {
		long totalxp = 0L;
		for(int i = 0; i <= 20; i++) {
			totalxp += (double) c.playerXP[i];
		}
		return totalxp;
	}
	
	@Override
	public void run() {
		try {
			this.makeConnection();
			this.total_level = this.getTotalLevel();
			this.total_exp = this.getTotalXp();
			if (stmt == null) {
				destroyConnection();
				return;
			}
			ResultSet rs = query("SELECT * FROM `hs_users` WHERE `username`='"+c.playerName+"'");
			int r = 1;
			while(rs.next() && r == 1) {
				for(int i = 0; i < 21; i++) {
					String lvl = "lvl_"+(i+1);
					String xp = "xp_"+(i+1);
					int level = c.getLevelForXP(c.playerXP[i]);
					if(level > 99 && i != 20) 
						level = 99;
					query("UPDATE hs_users SET "+lvl+"='"+level+"', "+xp+"='"+c.playerXP[i]+"' WHERE username='"+c.playerName+"'");
				}
				String lvlkc = "lvl_22";
				String lvlxp = "xp_22";
				String hunternull = "lvl_23";
				String hunter = "xp_23";
				String roguenull = "lvl_24";
				String rogue = "xp_24";
			//	query("UPDATE hs_users SET "+roguenull+"='0', "+rogue+"='"+c.getBH().getTotalRogueKills()+"' WHERE username='"+c.playerName+"'");
			//	query("UPDATE hs_users SET "+hunternull+"='0', "+hunter+"='"+c.getBH().getTotalHunterKills()+"' WHERE username='"+c.playerName+"'");
			//	query("UPDATE hs_users SET "+lvlkc+"='"+c.DC+"', "+lvlxp+"='"+c.KC+"' WHERE username='"+c.playerName+"'");
				query("UPDATE hs_users SET rank='"+c.getRights().getValue()+"', total_exp='"+this.total_exp+"', total_lvl='"+this.total_level+"' WHERE username='"+c.playerName+"'");
				System.out.println("Highscores have been updated for "+c.playerName);
				r = 0;
				c.inProcess = true;
				this.destroyConnection();
				return;
			}
			String name = c.playerName;
			query("INSERT INTO `hs_users`(`username`) VALUES('"+name+"')");
			HighscoresHandler hh = new HighscoresHandler(c);
			hh.start();
		} catch(SQLException e) {
				e.printStackTrace();
		}
	}
	public ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = stmt.executeQuery(s);
				return rs;
			}
			stmt.executeUpdate(s);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private void destroyConnection() {
		try {
			this.con = null;
			this.stmt = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void makeConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":"+PORT+"/"+ DATABASE, USERNAME, PASSWORD);
			stmt = con.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
