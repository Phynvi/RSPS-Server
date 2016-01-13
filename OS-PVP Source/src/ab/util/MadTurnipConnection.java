package ab.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ab.Config;
import ab.Server;
import ab.util.Misc;
import ab.model.players.Player;
import ab.model.players.Rights;
import ab.model.players.UpdateRank;

public class MadTurnipConnection extends Thread {

	public static Connection con = null;
	public static Statement stm;

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(
					"jdbc:mysql://162.248.95.73/donate",
					"root", "bLuB321");
			stm = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
	}

	public MadTurnipConnection() {

	}

	@Override
	public void run() {
		if (!Config.mySql) {
			return;
		}
		while (true) {
			try {
				if (con == null)
					createConnection();
				else
					ping();
				Thread.sleep(10000);// 10 seconds
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void ping() {
		try {
			String query = "SELECT * FROM donation WHERE username = 'null'";
			query(query);
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
	}

	public static void addDonateItems(final Player c, final String name) {
		if (con == null) {
			if (stm != null) {
				try {
					stm = con.createStatement();
				} catch (Exception e) {
					con = null;
					stm = null;
					// put a sendmessage here telling them to relog in 30
					// seconds
					c.sendMessage("You must relog for 30 seconds!");
					return;
				}
			} else {
				// put a sendmessage here telling them to relog in 30 seconds
				c.sendMessage("You must relog for 30 seconds!");
				return;
			}
		}
		new Thread() {
			@Override
			public void run() {
				try {
					String name2 = name.replaceAll(" ", "_");
					String query = "SELECT * FROM donation WHERE username = '"
							+ name2 + "'";
					ResultSet rs = query(query);
					boolean b = false;
					while (rs.next()) {
						int prod = Integer.parseInt(rs.getString("productid"));
						int price = Integer.parseInt(rs.getString("price"));
						if (prod == 1 && price == 10) {
						c.sendMessage("Thanks for your donation, @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@20 Donator points@bla@!");
						c.donatorPoints += 20;
						c.amDonated += 10;
						c.updateRank();
							for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");							}
							}
							b = true;
						} else if (prod == 2 && price == 20) {
								c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@45 Donator points@bla@!");
						c.donatorPoints += 45;
						c.updateRank();
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
						} else if (prod == 3 && price == 30) {
														c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@70 Donator points@bla@!");
						c.donatorPoints += 70;
						c.amDonated += 30;
						c.updateRank();
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
						} else if (prod == 4 && price == 50) {
										c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@125 Donator points@bla@!");
						c.donatorPoints += 125;
						c.amDonated += 50;
						c.updateRank();
									for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
						} else if (prod == 5 && price == 75) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@200 Donator points@bla@!");
						c.donatorPoints += 200;
						c.amDonated += 75;
						c.updateRank();
									for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
						} else if (prod == 6 && price == 100) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@275 Donator points@bla@!");
						c.donatorPoints += 275;
						c.amDonated += 100;
						c.updateRank();
									for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
						} else if (prod == 7 && price == 150) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@430 Donator points@bla@!");
						c.donatorPoints += 430;
						c.amDonated += 150;
						c.updateRank();
											for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");						}
											}
							b = true;
						} else if (prod == 8 && price == 250) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@750 Donator points@bla@!");
						c.donatorPoints += 750;
						c.amDonated += 250;
						c.updateRank();
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
												} else if (prod == 9 && price == 5) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@1x Mystery box@bla@. It has been placed in your bank!");
						c.amDonated += 5;
						c.updateRank();
						c.getItems().addItemToBank(6199, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
								} else if (prod == 10 && price == 30) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@Gambler Scroll@bla@. It has been placed in your bank!");
						c.amDonated += 30;
						c.getItems().addItemToBank(2701, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
								} else if (prod == 11 && price == 10) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@contributor scroll@bla@. It has been placed in your bank!");
						c.getItems().addItemToBank(2697, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
									} else if (prod == 12 && price == 25) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@sponsor scroll@bla@. It has been placed in your bank!");

						c.getItems().addItemToBank(2698, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
									} else if (prod == 13 && price == 65) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@Supporter scroll@bla@. It has been placed in your bank!");
						c.getItems().addItemToBank(2699, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
								} else if (prod == 14 && price == 130) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
									c.sendMessage("You are given @blu@V.I.P scroll@bla@. It has been placed in your bank!");
									c.getItems().addItemToBank(2700, 1);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<img=8> <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated to help support the server! <col=255>::store");								}
							}
							b = true;
								} else if (prod == 15 && price == 25) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@650 PKP Tickets@bla@. They have been placed in your bank!");
						c.amDonated += 25;
						c.getItems().addItemToBank(2996, 650);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<col=16711680>[DONATIONS] <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated for @blu@650 PKP tickets@bla@! ::donate");
								}
							}
							b = true;
									} else if (prod == 16 && price == 50) {
									c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@1400 PKP Tickets@bla@. They have been placed in your bank!");
						c.amDonated += 50;
						c.getItems().addItemToBank(2996, 1400);
										for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
							c2.sendMessage("<col=16711680>[DONATIONS] <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated for @blu@1400 PKP tickets@bla@! ::donate");
								}
							}
							
							b = true;
								if (prod == 17 && price == 100) {
						c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@200 Donator points@bla@!");
						c.donatorPoints += 200;
						c.amDonated += 100;
							for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
						c2.sendMessage("<col=16711680>[DONATIONS] <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated for the @cr7@<col=FF00CD>V.I.P</col>@bla@ rank! ::donate");
							}
							}
							}
							b = true;
								if (prod == 18 && price == 200) {
						c.sendMessage("Thanks for your donation @blu@"+c.playerName+"@bla@!");
						c.sendMessage("You are given @blu@450 Donator points@bla@!");
						c.donatorPoints += 450;
						c.amDonated += 200;
							for (int j = 0; j < Server.playerHandler.players.length; j++) {
						if (Server.playerHandler.players[j] != null) {
							Player c2 = (Player)Server.playerHandler.players[j];
						c2.sendMessage("<col=16711680>[DONATIONS] <col=255>"+Misc.capitalize(c.playerName)+" <col=0>has just donated for the @cr8@@yel@Super V.I.P@bla@ rank! ::donate");
							}
							}
							}
							b = true;
						}
					}
					if (b) {
						query("DELETE FROM `donation` WHERE `username` = '"
								+ name2 + "';");
					}
				} catch (Exception e) {
					e.printStackTrace();
					con = null;
					stm = null;
				}
			}
		}.start();
	}

	public static ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = stm.executeQuery(s);
				return rs;
			} else {
				stm.executeUpdate(s);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
		return null;
	}
}