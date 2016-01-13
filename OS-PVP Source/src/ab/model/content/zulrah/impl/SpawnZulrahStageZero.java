package ab.model.content.zulrah.impl;

import ab.Server;
import ab.event.CycleEventContainer;
import ab.model.content.zulrah.Zulrah;
import ab.model.content.zulrah.ZulrahLocation;
import ab.model.content.zulrah.ZulrahStage;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;
import ab.model.players.Player;
import ab.model.players.combat.CombatType;

public class SpawnZulrahStageZero extends ZulrahStage {

	public SpawnZulrahStageZero(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || player == null || player.isDead || zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int cycle = container.getTotalTicks();
		if (cycle == 8) {
			player.getPA().sendScreenFade("Welcome to Zulrah's shrine", -1, 4);
			player.getPA().movePlayer(2268, 3069, zulrah.getInstancedZulrah().getHeight());
		}
		if (cycle == 13) {
			Server.npcHandler.spawnNpc(player, 2042, 2266, 3072, zulrah.getInstancedZulrah().getHeight(), -1, 500, 41, 500, 500, false, false);
			NPC npc = NPCHandler.getNpc(2042, 2266, 3072, zulrah.getInstancedZulrah().getHeight());
			if (npc == null) {
				player.sendMessage("Something seems to have gone wrong! Please contact a member of OS-PvP staff.");
				container.stop();
				return;
			}
			zulrah.setNpc(npc);
			npc.setFacePlayer(false);
			npc.facePlayer(player.playerId);
			npc.startAnimation(5073);
		}
		if (cycle == 18) {
			zulrah.changeStage(1, CombatType.RANGE, ZulrahLocation.NORTH);
			container.stop();
		}
	}

}
