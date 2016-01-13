package ab.model.items.item_combinations;

import java.util.List;
import java.util.Optional;

import ab.model.items.GameItem;
import ab.model.items.ItemCombination;
import ab.model.players.Player;

public class YellowDarkBow extends ItemCombination {

	public YellowDarkBow(Optional<int[]> skillRequirements, GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(skillRequirements, outcome, revertedItems, items);
	}
	
	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(super.outcome.getId(), super.outcome.getAmount());
		player.getDH().sendStatement("You combined the items and created the Yellow dark bow.");
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}
	
	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("The Yellow dark bow is untradeable.", 
				"You cannot revert this item either.");
	}

}
