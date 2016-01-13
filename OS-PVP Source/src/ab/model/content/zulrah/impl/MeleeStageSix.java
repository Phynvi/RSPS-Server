package ab.model.content.zulrah.impl;

import ab.event.CycleEventContainer;
import ab.model.content.zulrah.Zulrah;
import ab.model.content.zulrah.ZulrahLocation;
import ab.model.content.zulrah.ZulrahStage;
import ab.model.players.Player;
import ab.model.players.combat.CombatType;

public class MeleeStageSix extends ZulrahStage {

	public MeleeStageSix(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead
				|| player == null || player.isDead || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 1 && zulrah.getNpc().attackTimer == 9) {
			player.getZulrahEvent().changeStage(7, CombatType.RANGE, ZulrahLocation.EAST);
			zulrah.getNpc().totalAttacks = 0;
			zulrah.getNpc().setFacePlayer(true);
			container.stop();
			return;
		}
	}
	
}
