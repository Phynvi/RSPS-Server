package ab.model.multiplayer_session.clan_wars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ab.Server;
import ab.model.multiplayer_session.Multiplayer;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.util.Misc;
import ab.world.Clan;

/**
 * A multiplayer request of Clan Wars.
 * @author Chris
 * @date Aug 13, 2015 11:41:09 AM
 *
 */
public class ClanWarsRequest extends Multiplayer {
	
	/**
	 * A {@link ArrayList} of all clans currently in the minigame.
	 */
	private static ArrayList<Clan> clans = new ArrayList<>();
	
	public ClanWarsRequest(Player player) {
		super(player);
	}
	
	@Override
	public boolean requestable(Player requested) {
		if (!ClanWars.CHALLENGE_ENABLED) {
			return false;
		}
		if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, 
				MultiplayerSessionType.CLAN_WARS) != null) {
			player.sendMessage("You have already sent a request to this player.");
			return false;
		}
		if (Server.UpdateServer) {
			player.sendMessage("You cannot request or accept a Clan Wars challenge at this time.");
			player.sendMessage("The server is currently being updated.");
			return false;
		}
		if (!Boundary.isIn(requested, ClanWarsMap.LOBBY.getBoundary())) {
			requested.sendMessage("The challenger must be in the Clan Wars lobby to do this.");
			return false;
		}
		if (!Boundary.isIn(player, ClanWarsMap.LOBBY.getBoundary())) {
			player.sendMessage("You must be in the Clan Wars lobby to do this.");
			return false;
		}
		if (player.connectedFrom.equalsIgnoreCase(requested.connectedFrom) && (player.getRights().getValue() < 2 || player.getRights().getValue() > 3)) {
			player.sendMessage("You cannot challenge someone on the same network!");
			return false;
		}
		if (Misc.distanceToPoint(player.getX(), player.getY(), requested.getX(), requested.getY()) > 15) {
			player.sendMessage("You are not close enough to the other player to request or accept.");
			return false;
		}
		if (player.getBH().hasTarget()) {
			if (player.getBH().getTarget().getName().equalsIgnoreCase(requested.playerName)) {
				player.sendMessage("You cannot challenge your bounty hunter target.");
				return false;
			}
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.sendMessage("You cannot challenge a player whilst in a session.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(requested)) {
			player.sendMessage("This player is currently in a session with another player.");
			return false;
		}
		if (player.teleTimer > 0 || requested.teleTimer > 0) {
			player.sendMessage("You cannot request or accept whilst you, or the other player are teleporting.");
			return false;
		}
		return true;
	}
	
	@Override
	public void request(Player requested) {
		if (Objects.isNull(requested)) {
			player.sendMessage("The player cannot be found, please try again shortly.");
			return;
		}
		if (Objects.equals(player, requested)) {
			player.sendMessage("You cannot challenge yourself!");
			return;
		}
		player.faceUpdate(requested.playerId);
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().requestAvailable(player, requested,
				MultiplayerSessionType.DUEL);
		if (session != null) {
			session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
			session.populatePresetItems();
			session.updateMainComponent();
			session.sendDuelEquipment();
			Server.getMultiplayerSessionListener().removeOldRequests(player);
			Server.getMultiplayerSessionListener().removeOldRequests(requested);
			session.getStage().setAttachment(null);
		} else {
    		session = new DuelSession(Arrays.asList(player, requested), MultiplayerSessionType.DUEL);
    		if (Server.getMultiplayerSessionListener().appendable(session)) {
    			player.sendMessage("Sending Clan Wars challenge...");
    			requested.sendMessage(player.playerName+":duelreq:");
     			session.getStage().setAttachment(player);
    			Server.getMultiplayerSessionListener().add(session);
    		}
		}
	}

}
