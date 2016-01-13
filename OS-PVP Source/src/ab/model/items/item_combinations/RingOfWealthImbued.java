package ab.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import ab.model.items.GameItem;
import ab.model.items.ItemCombination;
import ab.model.players.Player;

public class RingOfWealthImbued extends ItemCombination {

	public RingOfWealthImbued(Optional<int[]> skillRequirements, GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(skillRequirements, outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(outcome.getId(), outcome.getAmount());
		player.getDH().sendStatement("You combined the items and created a ring of wealth (i).");
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}
	
	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("The ring of wealth imbued is untradable.",
				"You cannot revert this.");
	}

}
