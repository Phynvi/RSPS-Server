package ab.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.Rights;

public class DonationQuery extends Thread {
	
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
	 * The player this is created for
	 */
	private Player player;

	/**
	 * Creates a new donation query. In doing so we check for the players
	 * name in the list to see if they obtained their item. 
	 * @param player	the player
	 */
	public DonationQuery(Player player) { 
		this.player = player;
		this.run();
	}

	/**
	 * Executes the query on a new thread.
	 */
	@Override
	public void run() {
		query = "SELECT * FROM donations";
		makeConnection();
		ResultSet result = executeQuery(query);
		int identificationValue = -1;
		try {
			while (result.next()) {
				String name = result.getString("name");
				if (name.equalsIgnoreCase(player.playerName)) {
					int claimed = result.getInt("claimed");
					if (claimed != 0) {
						continue;
					}
					String status = result.getString("status");
					if (!status.equalsIgnoreCase("completed")) {
						continue;
					}
					String reward = result.getString("item");
					double price = result.getDouble("amount");
					Optional<DonatablePackage> dpOp = DonatablePackage.forName(reward);
					if (dpOp.isPresent()) {
						DonatablePackage dp = dpOp.get();
						if (dp.price == price) {
							dp.reward.append(player);
							if (dp.increasesTotal) {
								player.amDonated += price;
								upgradeRank();
							}
							PlayerHandler.executeGlobalMessage("<img=10></img>[<col=CC0000>News</col>] " + player.playerName + 
									" donated $" + Double.toString(price) + " and received <col=CC0000>"+dp.packageName+"</col>.");
							identificationValue = result.getInt("id");
						}
						break;
					}
				}
			}
			if (identificationValue != -1) {
				PreparedStatement ps = con.prepareStatement("UPDATE donations SET claimed = '1' WHERE id = ? ");
				ps.setInt(1, identificationValue);
				ps.executeUpdate();
			} else {
				player.sendMessage("You don't have any available donations to claim.");
			}
			terminateConnection();
		} catch (SQLException e) {
			terminateConnection();
			e.printStackTrace();
		}
	}
	
	private void upgradeRank() {
		Rights myRights = player.getRights();
		if (myRights.isStaff() || myRights.isRespectedMember() || myRights.isHelper()) {
			return;
		}
		ArrayList<RankUpgrade> orderedList = new ArrayList<>(Arrays.asList(RankUpgrade.values()));
		orderedList.sort((one, two) -> Integer.compare(two.amount, one.amount));
		Optional<RankUpgrade> upgrade = orderedList.stream().filter(r -> player.amDonated >= r.amount).findFirst();
		upgrade.ifPresent(rank -> {
			if (rank.rights.getValue() > myRights.getValue()) {
				player.sendMessage("Congratulations, your rank has been upgraded to " + rank.rights.toString() + ".");
				player.sendMessage("You will need to re-log for these changes to apply properly.");
				player.setRights(rank.rights);
			}
		});
	}

	/**
	 * Creates a connection with the database.
	 */
	private void makeConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		//	con = ConnectionPool.getConnection();
			stmt = con.createStatement();
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
	
	private enum RankUpgrade {
		CONTRIBUTOR(Rights.CONTRIBUTOR, 10),
		SPONSOR(Rights.SPONSOR, 30),
		SUPPORTER(Rights.SUPPORTER, 75),
		VIP(Rights.V_I_P, 150),
		SUPER_VIP(Rights.SUPER_V_I_P, 300);
		
		/**
		 * The rights that will be appended if upgraded
		 */
		private final Rights rights;
		
		/**
		 * The amount required for the upgrade
		 */
		private final int amount;
		
		private RankUpgrade(Rights rights, int amount) {
			this.rights = rights;
			this.amount = amount;
		}
	}
	
	private enum DonatablePackage {
		DONATOR_POINTS_1("20x donator points", 10.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 20;
				player.sendMessage("You have received 20 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_2("45x donator points", 20.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 45;
				player.sendMessage("You have received 45 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_3("70x donator points", 30.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 70;
				player.sendMessage("You have received 70 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_4("125x donator points", 50.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 125;
				player.sendMessage("You have received 125 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_5("200x donator points", 75.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 200;
				player.sendMessage("You have received 200 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_6("275x donator points", 100.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 275;
				player.sendMessage("You have received 275 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_7("430x donator points", 150.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 430;
				player.sendMessage("You have received 430 donator points for donating, thank you!");
			}
			
		}),
		DONATOR_POINTS_8("700x donator points", 250.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.donatorPoints += 700;
				player.sendMessage("You have received 700 donator points for donating, thank you!");
			}
			
		}),
		MYSTER_BOX_1("1x mystery box", 10.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 1);
				player.sendMessage("You have received 1x mystery box for donating, thank you!");
			}
			
		}),
		MYSTER_BOX_3("3x mystery box", 25.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 3);
				player.sendMessage("You have received 3x mystery box for donating, thank you!");
			}
			
		}),
		MYSTER_BOX_5("5x mystery box", 40.00, true, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(6199, 5);
				player.sendMessage("You have received 5x mystery box for donating, thank you!");
			}
			
		}),
		GAMBLER_SCROLL("1x gambler scroll", 40.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2701, 1);
				player.sendMessage("You have received 1x gambler scroll for donating, thank you!");
			}
			
		}),
		CONTRIBUTION_SCROLL("1x contributor scroll", 10.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2697, 1);
				player.sendMessage("You have received 1x contributor scroll for donating, thank you!");
			}
			
		}),
		SPONSOR_SCROLL("1x sponsor scroll", 25.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2698, 1);
				player.sendMessage("You have received 1x sponsor scroll for donating, thank you!");
			}
			
		}),
		SUPPORTER_SCROLL("1x supporter scroll", 65.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2699, 1);
				player.sendMessage("You have received 1x sponsor scroll for donating, thank you!");
			}
			
		}),
		VIP_SCROLL("1x VIP scroll", 130.00, false, new Reward() {

			@Override
			public void append(Player player) {
				player.getItems().addItemToBank(2700, 1);
				player.sendMessage("You have received 1x VIP scroll for donating, thank you!");
			}
			
		});
		
		/**
		 * The name of the package
		 */
		private final String packageName;
		
		/**
		 * The price of the package
		 */
		private final double price;
		
		/**
		 * Determines if this package increases your total donated amount
		 */
		private final boolean increasesTotal;
		
		/**
		 * The reward received from this package
		 */
		private final Reward reward;
		
		/**
		 * Creates a new donatable package with a set name and price
		 * @param packageName	the name of the package
		 * @param price			the cost of the package
		 * @param reward		the reward received
		 */
		private DonatablePackage(String packageName, double price, boolean increasesTotal, Reward reward) {
			this.packageName = packageName;
			this.price = price;
			this.increasesTotal = increasesTotal;
			this.reward = reward;
		}
		
		/**
		 * Returns an Optional of type {@code DonatablePackage} that is
		 * generated by comparing the name of the package provided to the 
		 * array of available packages.
		 * 
		 * @param name	the name of the package
		 * @return		the package
		 */
		static Optional<DonatablePackage> forName(String name) {
			return Arrays.asList(values()).stream().filter(pack -> pack.packageName.equals(name)).findFirst();
		}
	}
	
	private interface Reward {
		
		/**
		 * Appends the reward to the player
		 * @param player	the player receiving the reward
		 */
		void append(Player player);
	}

}
