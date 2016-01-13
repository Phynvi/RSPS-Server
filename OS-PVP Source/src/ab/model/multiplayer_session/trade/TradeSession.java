package ab.model.multiplayer_session.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import com.mchange.v1.util.SimpleMapEntry;

import ab.Server;
import ab.database.Query;
import ab.model.items.GameItem;
import ab.model.items.ItemAssistant;
import ab.model.multiplayer_session.MultiplayerSession;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.players.Player;
import ab.util.Misc;

public class TradeSession extends MultiplayerSession {

	public TradeSession(List<Player> players, MultiplayerSessionType type) {
		super(players, type);
	}
	
	@Override public void accept(Player player, Player recipient, int stageId) {
		switch (stageId) {
			case MultiplayerSessionStage.OFFER_ITEMS:
				if (recipient.getItems().freeSlots() < getItems(player).size()) {
					player.sendMessage(recipient.playerName + " only has "+recipient.getItems().freeSlots()+ ", you need to remove items.");
					player.getPA().sendFrame126("You have offered more items than "+recipient.playerName+" has free space.", 3431);
					recipient.getPA().sendFrame126("You do not have enough inventory space to accept this trade.", 3431);
					return;
				}
				for (Player p : players) {
					GameItem overlap = getOverlappedItem(p);
	    			if (overlap != null) {
	    				p.getPA().sendFrame126("Too many of one item! The other player has "+Misc.getValueRepresentation(overlap.getAmount())+" "+ItemAssistant.getItemName(overlap.getId())+" in their inventory.", 3431);
	    				getOther(p).getPA().sendFrame126("The other player has offered too many of one item, they must remove some.", 3431);
	    				return;
	    			}
				}
				if (stage.hasAttachment() && stage.getAttachment() != player) {
					stage.setStage(MultiplayerSessionStage.CONFIRM_DECISION);
					stage.setAttachment(null);
					updateMainComponent();
					return;
				}
				player.getPA().sendFrame126("Waiting for other player...", 3431);
				stage.setAttachment(player);
	            recipient.getPA().sendFrame126("Other player has accepted", 3431);
				break;
				
			case MultiplayerSessionStage.CONFIRM_DECISION:
				if (recipient.getItems().freeSlots() < getItems(player).size()) {
					player.sendMessage(recipient.playerName + " only has "+recipient.getItems().freeSlots()+ ", the items could not be traded, they would be lost.");
					recipient.sendMessage(player.playerName + " had too many items to offer, they could have been lost in the trade.");
					finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					return;
				}
				if (stage.hasAttachment() && stage.getAttachment() != player) {
					finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
					player.sendMessage("Trade successfully completed with "+recipient.playerName);
					recipient.sendMessage("Trade successfully completed with "+player.playerName);
					return;
				}
				stage.setAttachment(player);
				player.getPA().sendFrame126("Waiting for other player...", 3535);
	            recipient.getPA().sendFrame126("Other player has accepted", 3535);
				break;
				
			default:
				finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				break;
		}
	}
	
	@Override
	public void updateOfferComponents() {
		for (Player player : items.keySet()) {
	   		player.getItems().resetItems(3322);
    		refreshItemContainer(player, player, 3415);
    		refreshItemContainer(player, getOther(player), 3416);
    		player.getPA().sendFrame126("", 3431);
    		player.getPA().sendFrame126("Trading with: " + getOther(player).playerName + " who has @gre@"
    				+ getOther(player).getItems().freeSlots() + " free slots.", 3417);
		}
	}

	@Override
	public boolean itemAddable(Player player, GameItem item) {
		if (!player.getItems().isTradeable(item.getId())) {
			player.sendMessage("You cannot trade this item, it is deemed as untradable.");
			return false;
		}
		if (item.getId() == 12926 || item.getId() == 12931 || item.getId() == 12904) {
			player.sendMessage("You cannot trade this item, it is deemed as untradable.");
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}

	@Override
	public boolean itemRemovable(Player player, GameItem item) {
		if (!Server.getMultiplayerSessionListener().inAnySession(player)) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (stage.getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		return true;
	}
	
	@Override
	public void updateMainComponent() {
		if (stage.getStage() == MultiplayerSessionStage.OFFER_ITEMS) {
    		for (Player player : players) {
    			player.setTrading(true);
    			player.getItems().resetItems(3322);
    			refreshItemContainer(player, player, 3415);
    			refreshItemContainer(player, player, 3416);
    			player.getPA().sendFrame126("Trading with: " + getOther(player).playerName + " who has @gre@"
    					+ getOther(player).getItems().freeSlots() + "", 3417);
    			player.getPA().sendFrame126("", 3431);
    			player.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
    			player.getPA().sendFrame248(3323, 3321);
    		}
		} else if (stage.getStage() == MultiplayerSessionStage.CONFIRM_DECISION) {
			for (Player player : players) {
				Player recipient = getOther(player);
				player.getItems().resetItems(3214);
				int column = 0;
				List<GameItem> items = getItems(player);
				for (GameItem item : items) {
					if (item.getId() > 0 && item.getAmount() > 0) {
						player.getPA().sendFrame126(ItemAssistant.getItemName(item.getId())
							+ " x "+Misc.getValueRepresentation(item.getAmount()), 55011 + column);
						column++;
					}
				}
				for (; column < 28; column++) {
					player.getPA().sendFrame126("", 55011 + column);
				}
				column = 0;
				items = getItems(recipient);
				for (GameItem item : items) {
					if (item.getId() > 0 && item.getAmount() > 0) {
						player.getPA().sendFrame126(ItemAssistant.getItemName(item.getId())
							+ " x "+Misc.getValueRepresentation(item.getAmount()), 55051 + column);
						column++;
					}
				}
				for (; column < 28; column++) {
					player.getPA().sendFrame126("", 55051 + column);
				}
    			player.getPA().sendFrame126("Are you sure you want to make this trade?", 3535);
				player.getPA().sendFrame248(55000, 197);
			}
		}
	}

	@Override
	public void give() {
		if (players.stream().anyMatch(client -> Objects.isNull(client))) {
			finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				getOther(player).getItems().addItem(item.getId(), item.getAmount());
			}
		}
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void withdraw() {
		for (Player player : items.keySet()) {
			if (Objects.isNull(player)) {
				continue;
			}
			if (items.get(player).size() <= 0) {
				continue;
			}
			for (GameItem item : items.get(player)) {
				player.getItems().addItem(item.getId(), item.getAmount());
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void logSession(MultiplayerSessionFinalizeType type) {
		if (type == MultiplayerSessionFinalizeType.WITHDRAW_ITEMS) {
			return;
		}
		ArrayList<Entry<Player, String>> participantList = new ArrayList<>();
		for (Player player : items.keySet()) {
			String items = createItemList(player);
			Entry<Player, String> participant = new SimpleMapEntry(player, items);
			participantList.add(participant);
		}
		if (participantList.size() == 2) {
			String query = createQuery(participantList, type);
			new Query(query).execute();
		}
	}

	private String createQuery(ArrayList<Entry<Player, String>> participantList, MultiplayerSessionFinalizeType type) {
		String status;
		switch (type) {
		case GIVE_ITEMS:
			status = "Completed";
			break;
		default:
			status = "Error";
			break;
		}
		
		Player player = participantList.get(0).getKey();
		String items = participantList.get(0).getValue();
		Player playerOther = participantList.get(1).getKey();
		String itemsOther = participantList.get(1).getValue();
		
		String query = "INSERT into trades (DATE, TRADESTATUS, PLAYER, IP, GIVEN, OTHERPLAYER, OTHERIP, OTHERGIVEN) VALUES(NOW(), '" + status + "', "
				+ "'" + player.playerName + "', '" + getIp(player) + "', '" + items + "', '" + playerOther.playerName + "', '" + getIp(playerOther)
				+ "', '" + itemsOther + "')";
		return query;
	}
	
	private String getIp(Player player) {
		if (player.getRights().isBetween(1, 3)) {
			return "Private";
		}
		return player.connectedFrom;
	}

	private String createItemList(Player player) {
		if (items.get(player).size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (GameItem item : items.get(player)) {
			sb.append(ItemAssistant.getItemName(item.getId()));
			if (item.getAmount() != 1) {
				sb.append(" x" + item.getAmount());
			}
			sb.append(", ");
		}
		return sb.substring(0, sb.length() - 2).replaceAll("'", "\\\\'");
	}
	
}
