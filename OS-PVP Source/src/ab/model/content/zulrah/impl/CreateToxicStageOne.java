package ab.model.content.zulrah.impl;

import java.util.Arrays;

import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.content.zulrah.DangerousEntity;
import ab.model.content.zulrah.DangerousLocation;
import ab.model.content.zulrah.SpawnDangerousEntity;
import ab.model.content.zulrah.Zulrah;
import ab.model.content.zulrah.ZulrahLocation;
import ab.model.content.zulrah.ZulrahStage;
import ab.model.players.Player;
import ab.model.players.combat.CombatType;

public class CreateToxicStageOne extends ZulrahStage {

	public CreateToxicStageOne(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead
				|| player == null || player.isDead || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (container.getTotalTicks() == 1) {
			zulrah.getNpc().setFacePlayer(false);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player, Arrays.asList(
					DangerousLocation.values()), DangerousEntity.TOXIC_SMOKE, 40), 1);
		} else if (container.getTotalTicks() >= 19) {
			zulrah.getNpc().totalAttacks = 0;
			zulrah.changeStage(2, CombatType.MELEE, ZulrahLocation.NORTH);
			container.stop();
		}
	}

}
