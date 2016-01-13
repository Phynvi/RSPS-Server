package ab.model.multiplayer_session.duel;

import java.util.Arrays;
import java.util.Objects;

import ab.Config;
import ab.Server;
import ab.model.multiplayer_session.Multiplayer;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.players.Player;
import ab.util.Misc;

public class Duel extends Multiplayer {
	
	public Duel(Player player) {
		super(player);
	}

	@Override
	public boolean requestable(Player requested) {
		if (!Config.NEW_DUEL_ARENA_ACTIVE) {
			player.getDH().sendStatement("@red@Dueling Temporarily Disabled",
					"The duel arena minigame is currently being rewritten.",
					"No player has access to this minigame during this time.",
					"", "Thank you for your patience, Developer J.");
			player.nextChat = -1;
			return false;
		}
		if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, 
				MultiplayerSessionType.DUEL) != null) {
			player.sendMessage("You have already sent a request to this player.");
			return false;
		}
		if (Server.UpdateServer) {
			player.sendMessage("You cannot request or accept a duel request at this time.");
			player.sendMessage("The server is currently being updated.");
			return false;
		}
		if (player.connectedFrom.equalsIgnoreCase(requested.connectedFrom) && (player.getRights().getValue() < 2 || player.getRights().getValue() > 3)) {
			player.sendMessage("You cannot request a duel from someone on the same network.");
			return false;
		}
		if (Misc.distanceToPoint(player.getX(), player.getY(), requested.getX(), requested.getY()) > 15) {
			player.sendMessage("You are not close enough to the other player to request or accept.");
			return false;
		}
		if (!player.inDuelArena()) {
			player.sendMessage("You must be in the duel arena area to do this.");
			return false;
		}
		if (!requested.inDuelArena()) {
			player.sendMessage("The challenger must be in the duel arena area to do this.");
			return false;
		}
		if (player.getBH().hasTarget()) {
			if (player.getBH().getTarget().getName().equalsIgnoreCase(requested.playerName)) {
				player.sendMessage("You cannot duel your bounty hunter target.");
				return false;
			}
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.sendMessage("You cannot request a duel whilst in a session.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(requested)) {
			player.sendMessage("This player is currently is a session with another player.");
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
			player.sendMessage("The player cannot be found, try again shortly.");
			return;
		}
		if (Objects.equals(player, requested)) {
			player.sendMessage("You cannot trade yourself.");
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
    			player.sendMessage("Sending duel request...");
    			requested.sendMessage(player.playerName+":duelreq:");
     			session.getStage().setAttachment(player);
    			Server.getMultiplayerSessionListener().add(session);
    		}
		}
	}

}
