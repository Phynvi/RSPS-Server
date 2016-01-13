package ab.model.players.packets;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.PlayerHandler;
import ab.model.content.help.HelpDatabase;
import ab.model.content.help.HelpRequest;
import ab.model.content.presets.Preset;
import ab.model.items.bank.BankPin;

public class InputField implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int id = player.inStream.readDWord();
		String text = player.inStream.readString();
		if (player.getRights().isOwner()) {
			player.sendMessage("Component; "+id+", input; " + text);
		}
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		switch (id) {
		
		case 33205:
			player.getPunishmentPanel().setReason(text);
			break;
			
		case 33211:
			player.getPunishmentPanel().setDuration(Integer.parseInt(text));
			break;
			
			case 33036:
				if (text.length() > 16) {
					player.sendMessage("Custom title length can only be sixteen characters, no more.");
					return;
				}
				player.getTitles().setTemporaryCustomTitle(text);
				break;
		
			case 59527:
				if (text.length() < 25) {
					player.sendMessage("Your help request must contain 25 characters for the description.");
					return;
				}
				List<Player> staff = PlayerHandler.getPlayerList().stream().filter(Objects::nonNull)
						.filter(p -> p.getRights().isBetween(1, 3) || p.getRights().isHelper()).collect(Collectors.toList());
				if (HelpDatabase.getDatabase().requestable(player)) {
					HelpDatabase.getDatabase().add(new HelpRequest(player.playerName, player.connectedFrom, text));
					if (staff.size() > 0) {
						PlayerHandler.sendMessage("[HelpDB] " + WordUtils.capitalize(player.playerName) + ""
								+ " is requesting help, type ::helpdb to view their request.", staff);
						player.sendMessage("You request has been sent, please wait as a staff member gets back to you.");
					} else {
						player.sendMessage("There are no staff online to help you at this time, please be patient.");
					}
				}
				player.getPA().removeAllWindows();
				break;
	
			case 32002:
				Preset preset = player.getPresets().getCurrent();
				if (preset == null) {
					player.sendMessage("You must select a preset before changing the name.");
					return;
				}
				preset.setAlias(text);
				player.getPresets().refreshMenus(preset.getMenuSlot(), preset.getMenuSlot() + 1);
				break;
	
			case 58063:
				if (player.getPA().viewingOtherBank) {
					player.getPA().resetOtherBank();
					return;
				}
				if (player.isBanking) {
					player.getBank().getBankSearch().setText(text);
					player.getBank().setLastSearch(System.currentTimeMillis());
					if (text.length() > 2) {
						player.getBank().getBankSearch().updateItems();
						player.getBank().setCurrentBankTab(player.getBank().getBankSearch().getTab());
						player.getItems().resetBank();
						player.getBank().getBankSearch().setSearching(true);
					} else {
						if (player.getBank().getBankSearch().isSearching())
							player.getBank().getBankSearch().reset();
						player.getBank().getBankSearch().setSearching(false);
					}
				}
				break;
	
			case 59507:
				if (player.getBankPin().getPinState() == BankPin.PinState.CREATE_NEW)
					player.getBankPin().create(text);
				else if (player.getBankPin().getPinState() == BankPin.PinState.UNLOCK)
					player.getBankPin().unlock(text);
				else if (player.getBankPin().getPinState() == BankPin.PinState.CANCEL_PIN)
					player.getBankPin().cancel(text);
				else if (player.getBankPin().getPinState() == BankPin.PinState.CANCEL_REQUEST)
					player.getBankPin().cancel(text);
				break;
	
			default:
				break;
		}
	}

}
