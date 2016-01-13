package ab.model.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import ab.model.items.item_combinations.*;

public enum ItemCombinations {
	
	SARADOMINS_BLESSED_SWORD(
			new SaradominsBlessedSword(Optional.empty(), new GameItem(12809), 
					Optional.of(Arrays.asList(new GameItem(11838))),
						new GameItem[] {new GameItem(12804), new GameItem(11838)})
	),

	AMULET_OF_FURY(
			new AmuletOfFury(Optional.empty(), new GameItem(12436), 
					Optional.of(Arrays.asList(new GameItem(6585), new GameItem(12526))),
						new GameItem[] {new GameItem(6585), new GameItem(12526)})
	),
	
	BLUE_DARK_BOW(
			new BlueDarkBow(Optional.empty(), new GameItem(12765),
					Optional.empty(),
						new GameItem[] {new GameItem(11235), new GameItem(12757)})
	),
	
	GREEN_DARK_BOW(
			new GreenDarkBow(Optional.empty(), new GameItem(12766),
					Optional.empty(),
						new GameItem[] {new GameItem(11235), new GameItem(12759)})
	),
	
	YELLOW_DARK_BOW(
			new YellowDarkBow(Optional.empty(), new GameItem(12767),
					Optional.empty(),
						new GameItem[] {new GameItem(11235), new GameItem(12761)})
	),
	
	WHITE_DARK_BOW(
			new WhiteDarkBow(Optional.empty(), new GameItem(12768),
					Optional.empty(),
						new GameItem[] {new GameItem(11235), new GameItem(12763)})
	),
	
	MALEDICTION_WARD(
			new MaledictionWard(Optional.empty(), new GameItem(12806),
					Optional.of(Arrays.asList(new GameItem(11924))),
						new GameItem[] {new GameItem(11924), new GameItem(12802)})
	),
	
	ODIUM_WARD(
			new OdiumWard(Optional.empty(), new GameItem(12807),
					Optional.of(Arrays.asList(new GameItem(11926))),
						new GameItem[] {new GameItem(11926), new GameItem(12802)})
	),
	STEAM_STAFF(
			new SteamStaff(Optional.empty(), new GameItem(12796),
					Optional.of(Arrays.asList(new GameItem(11789))),
						new GameItem[] {new GameItem(11789), new GameItem(12798)})
	),
	
	GRANITE_MAUL(
			new GraniteMaul(Optional.empty(), new GameItem(12848),
					Optional.of(Arrays.asList(new GameItem(4153))),
						new GameItem[] {new GameItem(4153), new GameItem(12849)})
	),
	
	DRAGON_PICKAXE(
			new DragonPickaxe(Optional.empty(), new GameItem(12797),
					Optional.of(Arrays.asList(new GameItem(11920))),
						new GameItem[] {new GameItem(12800), new GameItem(11920)})
	),
	
	BLESSED_SPIRIT_SHIELD(
			new BlessedSpiritShield(Optional.empty(), new GameItem(12831),
					Optional.empty(), new GameItem[] {new GameItem(12829), new GameItem(12833)})
	),
	
	ARCANE_SPIRIT_SHIELD(
			new ArcaneSpiritShield(Optional.of(new int[] { 13, 85 }), new GameItem(12825), Optional.empty(), new GameItem[] {
				new GameItem(12827), new GameItem(12831)})
	),
	
	ELYSIAN_SPIRIT_SHIELD(
			new ElysianSpiritShield(Optional.of(new int[] { 13, 85 }), new GameItem(12817), Optional.empty(), new GameItem[] {
				new GameItem(12819), new GameItem(12831)})
	),
	
	SPECTRAL_SPIRIT_SHIELD(
			new SpectralSpiritShield(Optional.of(new int[] { 13, 85 }), new GameItem(12821), Optional.empty(), new GameItem[] {
				new GameItem(12823), new GameItem(12831)})
	),
	
	TENTACLE_WHIP(
			new TentacleWhip(Optional.empty(), new GameItem(12006), Optional.empty(), new GameItem[] {
				new GameItem(12004), new GameItem(4151)})
	),
	
	RING_OF_WEALTH_IMBUED(
			new RingOfWealthImbued(Optional.empty(), new GameItem(12785), Optional.empty(), new GameItem[] {
				new GameItem(2572), new GameItem(12783)})
	);
	
	private ItemCombination itemCombination;
	
	private ItemCombinations(ItemCombination itemCombination) {
		this.itemCombination = itemCombination;
	}
	
	public ItemCombination getItemCombination() {
		return itemCombination;
	}
	
	static final Set<ItemCombinations> COMBINATIONS = Collections.unmodifiableSet(EnumSet.allOf(ItemCombinations.class));
	
	public static Optional<ItemCombinations> isCombination(GameItem item1, GameItem item2) {
		Optional<ItemCombinations> available = COMBINATIONS.stream().filter(combos -> combos.getItemCombination().
				allItemsMatch(item1, item2)).findFirst();
		return available.isPresent() ? available : Optional.empty();
	}
	
	public static Optional<ItemCombination> isRevertable(GameItem item) {
		Predicate<ItemCombinations> itemMatches = ic -> ic.getItemCombination().getRevertItems().isPresent() &&
				ic.getItemCombination().getOutcome().getId() == item.getId();
		Optional<ItemCombinations> revertable = COMBINATIONS.stream().filter(itemMatches).findFirst();
		if (revertable.isPresent() && revertable.get().getItemCombination().isRevertable()) {
			return Optional.of(revertable.get().getItemCombination());
		}
		return Optional.empty();
	}

}
