package ab.model.players.packets;

import ab.model.players.Player;
import ab.model.players.PacketType;

public class BankModifiableX implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int slot = player.getInStream().readUnsignedWordA();
		int component = player.getInStream().readUnsignedWord();
		int item = player.getInStream().readUnsignedWordA();
		int amount = player.getInStream().readDWord();
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		if(amount <= 0)
			return;
		switch(component) {
			case 5382:
            	if(player.getBank().getBankSearch().isSearching()) {
            		player.getBank().getBankSearch().removeItem(item, amount);
            		return;
            	}
                player.getItems().removeFromBank(item, amount, true);
                break;
		}
	}

}
