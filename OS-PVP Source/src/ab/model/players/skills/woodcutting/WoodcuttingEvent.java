package ab.model.players.skills.woodcutting;

import java.util.Optional;

import ab.Config;
import ab.Server;
import ab.clip.Region;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.players.Player;
import ab.model.players.skills.Skill;
import ab.util.Misc;
import ab.world.WorldObject;
import ab.world.objects.GlobalObject;

public class WoodcuttingEvent extends CycleEvent {
	
	private Player player;
	private Tree tree;
	private Hatchet hatchet;
	private int objectId, x, y, chops;
	
	public WoodcuttingEvent(Player player, Tree tree, Hatchet hatchet, int objectId, int x, int y) {
		this.player = player;
		this.tree = tree;
		this.hatchet = hatchet;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (player == null || container.getOwner() == null) {
			container.stop();
			return;
		}
		if (!player.getItems().playerHasItem(hatchet.getItemId()) && !player.getItems().isWearingItem(hatchet.getItemId())) {
			player.sendMessage("Your axe has dissapeared.");
			container.stop();
			return;
		}
		if (player.playerLevel[player.playerWoodcutting] < hatchet.getLevelRequired()) {
			player.sendMessage("You no longer have the level required to operate this hatchet.");
			container.stop();
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You have run out of free inventory space.");
			container.stop();
			return;
		}
		if (Misc.random(300) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			container.stop();
			return;
		}
		chops++;
		int chopChance = 1 + (int) (tree.getChopsRequired() * hatchet.getChopSpeed());
		if (Misc.random(tree.getChopdownChance()) == 0 || tree.equals(Tree.NORMAL) && Misc.random(chopChance) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = Region.getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			Server.getGlobalObjects().add(new GlobalObject(tree.getStumpId(), x, y, player.heightLevel, face, 10, tree.getRespawnTime(), objectId));
			player.getItems().addItem(tree.getWood(), 1);
			player.getPA().addSkillXP(tree.getExperience() * Config.WOODCUTTING_EXPERIENCE, Skill.WOODCUTTING.getId());
			Achievements.increase(player, AchievementType.WOODCUTTING, 1);
			container.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Misc.random(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				player.getItems().addItem(tree.getWood(), 1);
				player.getPA().addSkillXP(tree.getExperience() * Config.WOODCUTTING_EXPERIENCE, Skill.WOODCUTTING.getId());
				Achievements.increase(player, AchievementType.WOODCUTTING, 1);
			}
		}
		if (container.getTotalTicks() % 4 == 0) {
			player.startAnimation(hatchet.getAnimation());
		}
	}
	
	@Override
	public void stop() {
		if (player != null) {
			player.startAnimation(65535);
		}
	}

}
