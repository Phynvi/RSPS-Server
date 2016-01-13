package ab.model.multiplayer_session.clan_wars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ab.Server;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.world.Clan;

/**
 * A minigame in which two clans are able to compete against one another.
 * @author Chris
 * @date Aug 12, 2015 4:14:54 PM
 * @see http://2007.runescape.wikia.com/wiki/Clan_Wars
 *
 */
public class ClanWars {
	
	public static final boolean CHALLENGE_ENABLED = false;
	public static final boolean FFA_ENABLED = true;
	public static final boolean JOIN_IN_PROGRESS = false;
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * The {@link ClanWarsMap}.
	 */
	private static ClanWarsMap boundary;
	
	/**
	 * A set of rules associated with an object of Clan Wars.
	 */
	private static ClanWarsRules rules;
	
	/**
	 * A {@link ArrayList} of all clans currently in the minigame.
	 */
	private static ArrayList<Clan> clans = new ArrayList<>();
	
	/**
	 * Maps the parameters of the {@link ClanWars} object.
	 */
	private static Map<Integer, Object> parameters = new HashMap<>();
	
	
	/**
	 * Constructs a new {@link ClanWars}.
	 * @param player	the player
	 * @param parameters	a set of parameters associated with this object
	 */
	public ClanWars(Player player, Map<Integer, Object> parameters) {
		this.player = player;
		this.parameters = parameters;
	}
	
	/**
	 * Populates the map with parameters specified for the {@link ClanWars}.
	 * @param war	a {@link ClanWars} object
	 * @param map	a map to initiate the war on
	 * @param rules	a set of rules associated with the clan war
	 */
	public static void putParameters(ClanWars war) {
		parameters.put(1, getMap());
		parameters.put(2, getRules());
	}
	
	public static void getParameters() {
		parameters.get(1);
		parameters.get(2);
	}
	
	/**
	 * Gets the {@code ClanWarsMap} for the {@code ClanWars} war.
	 * @param war	the clan wars object
	 * @return		the clan wars map
	 */
	public ClanWarsMap forWar(ClanWars war) {
		return boundary;
		//return parameters.get(1); TODO: Proper data pulling from hashmapped parameters.
	}
	
	/**
	 * Requests a war.
	 * @param player	the player initiating the request with the specified parameters
	 * @param other		the player receiving the clan war request
	 */
	public static void requestWar(Player player, Player other) {
		if (player.clan.getRank(player.playerName) < Clan.Rank.CAPTAIN) {
			player.sendMessage("You must be ranked a Captain or higher to challenge.");
			return;
		}
		other.sendMessage(player.playerName + " wishes to challenge your clan to a Clan War.");
	}
	
	/**
	 * Gets the {@code ClanWarsMap} for the specified {@code ClanWars} war.
	 * @return boundary	the map
	 */
	private static ClanWarsMap getMap() {
		return boundary;
	}
	
	/**
	 * Gets a set of <code>ClanWarsRules</code> associated with an object of Clan Wars.
	 * @return	rules	the rules
	 */
	private static ClanWarsRules getRules() {
		return rules;
	}
	
	/**
	 * Adds members of both clans to the <code>ClanWarsMap</code>.
	 * @param player	the host player & their clan
	 * @param other		the opposing player & their clan
	 */
	public static void addMembersToMap(Clan host, Clan other) {
		Boundary map = getMap().getBoundary();
		for (String activeHostMembers : host.activeMembers) {
			Player members = PlayerHandler.getPlayer(activeHostMembers);
			if (Boundary.isIn(members, boundary.forMap(ClanWarsMap.LOBBY))) {
				members.getPA().movePlayer(getMap().getHostCoords()[0], 
										   getMap().getHostCoords()[1], 0);
			}
		}
		for (String activeOpposingMembers : other.activeMembers){
			Player opposing = PlayerHandler.getPlayer(activeOpposingMembers);
			if (Boundary.isIn(opposing, boundary.forMap(ClanWarsMap.LOBBY))) {
				opposing.getPA().movePlayer(getMap().getOpposingCoords()[0], 
										    getMap().getOpposingCoords()[1], 0);
			}
		}
	}
	
	/**
	 * Initializes an instance of the {@link ClanWars} minigame.
	 * @param player	the player
	 * @param other		the opposing player
	 * @param war		a clan war with mapped parameters
	 */
	public static void initializeClanWars(Clan host, Clan other) {
		if (clans.contains(host)|| clans.contains(other)) {
			return;
		}
		Boundary map = getMap().getBoundary();
		clans.add(host);
		clans.add(other);
		if (isAtWar(host) || isAtWar(other)) {
			addMembersToMap(host, other);
		}
	}
	
	
	/**
	 * Forfeits the instance of {@code ClanWars}.
	 * @param host	the clan that initiated the forfeit
	 */
	public static void forfeitClanWars(Clan host) {
		Clan war = host.belligerent;
		for (String other : war.activeMembers) {
			Player opposing = PlayerHandler.getPlayer(other);
			opposing.sendMessage("Victory! " + host.getTitle() + " has forfeited!");
		}
		for (String local : host.activeMembers){
			Player forfeited = PlayerHandler.getPlayer(local);
			forfeited.sendMessage("Defeat! Your clan has forfeited the match!");
			forfeited.getPA().movePlayer(PortalType.EXIT.getX(), PortalType.EXIT.getY(), 0);
		}
		if (isAtWar(host))
			clans.remove(host);
		if (isAtWar(war)) 
			clans.remove(war);
	}
	
	/**
	 * Gets whether the specified clan is in a clan war.
	 * @param clan	the clan
	 * @return		true if the clan is in a clan war
	 */
	public static boolean isAtWar(Clan clan) {
		return clans.contains(clan);
	}
	
	/**
	 * Represents whether the player has sufficient privileges to initiate or
	 * forfeit an instance of {@code ClanWars}.
	 * @param player	the player
	 * @return			true if the player's privileges are synonymous with a sufficient rank.
	 */
	public static boolean sufficientRank(Player player) {
		return player.clan.getRank(player.playerName) >= Clan.Rank.CAPTAIN;
	}
	
	
	/**
	 * Enters the player in the clan wars map.
	 * @param player	the player
	 * @param type		the type of portal being entered.
	 */
	public static void enterClanPortal(Player player, int id) {
		final PortalType type = PortalType.forId(id);
		if (Server.UpdateServer) {
			player.sendMessage("The server is currently being updated.");
			player.sendMessage("Please try again once updates have been completed.");
			return;
		}
		switch (type) {
		case CHALLENGE:
		case CHALLENGE_2:
			if (!CHALLENGE_ENABLED) {
				player.sendMessage("The challenge portal is currently unavailable.");
				return;
			}
			if (!isAtWar(player.clan)) {
				player.sendMessage("Your clan is not in a war.");
				return;
			}
			if (JOIN_IN_PROGRESS) {
				if (player.clan.isWarHost()) {
					player.getPA().movePlayer(getMap().getHostCoords()[0], getMap().getHostCoords()[1], 0);
				} else {
					player.getPA().movePlayer(getMap().getOpposingCoords()[0], getMap().getOpposingCoords()[1], 0);
				}
			}
			break;
		case FREE_FOR_ALL:
			if (!FFA_ENABLED) {
				player.sendMessage("The free-for-all portal is current unavailable.");
				return;
			}
			player.getPA().movePlayer(3327, 4751, 0);
			break;
		case EXIT:
			if (!isAtWar(player.clan)) { 
				player.playerLevel[3] = player.getPA().getLevelForXP(3);
				player.setPoisonDamage((byte) 0);
				player.runEnergy = 100;
				player.getPA().requestUpdates();
				player.sendMessage("You have been healed. (hitpoints, poison, & run energy)");
			} else if (isAtWar(player.clan) && sufficientRank(player)) {
				forfeitClanWars(player.clan);
			}
			break;
		}
		player.getPA().movePlayer(type.getX(), type.getY(), 0);
	}

}
