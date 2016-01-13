package ab.model.players.packets;

import ab.model.content.presets.Preset;
import ab.model.content.presets.PresetSlotAction;
import ab.model.content.presets.PresetType;
import ab.model.items.GameItem;
import ab.model.items.ItemDefinition;
import ab.model.players.Player;
import ab.model.players.PacketType;

/**
 * @author Jason MacKeigan
 * @date Dec 29, 2014, 1:12:35 PM
 */
public class SelectItemOnInterface implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int interfaceId = player.getInStream().readDWord();
		int slot = player.getInStream().readDWord();
		int itemId = player.getInStream().readDWord();
		int itemAmount = player.getInStream().readDWord();
		switch (interfaceId) {
			case 32011:
				PresetType type = player.getPresets().getCurrent().getEditingType();
				Preset preset = player.getPresets().getCurrent();
				GameItem item = new GameItem(itemId, itemAmount);
				if (type.isEquipment()) {
					ItemDefinition itemDefinition = ItemDefinition.forId(itemId);
					if (itemDefinition != null) {
						int equipmentSlot = PresetSlotAction.getEquipmentSlot(type, preset.getSelectedSlot());
						if (!itemDefinition.isWearable()) {
							player.sendMessage("This item cannot be worn.");
							return;
						}
						if (itemDefinition.getSlot() != equipmentSlot) {
							player.sendMessage("This item cannot be inserted into this equipment slot.");
							return;
						}
					} else {
						player.sendMessage("This item is currently unavailable.");
						return;
					}
					preset.getEquipment().add(player, preset.getSelectedSlot(), item);
				} else if (type.isInventory()) {
					preset.getInventory().add(player, preset.getSelectedSlot(), item);
				}
				player.getPresets().hideSearch();
				break;
		}
	}

}
