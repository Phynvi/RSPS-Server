package ab.model.content.instances.impl;

import ab.Server;
import ab.model.content.instances.SingleInstancedArea;
import ab.model.content.zulrah.Zulrah;
import ab.model.npcs.NPCHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;

public class SingleInstancedZulrah extends SingleInstancedArea {
	
	public SingleInstancedZulrah(Player player, Boundary boundary, int height) {
		super(player, boundary, height);
	}
	
	@Override
	public void onDispose() {
		Zulrah zulrah = player.getZulrahEvent();
		if (zulrah.getNpc() != null) {
			NPCHandler.kill(zulrah.getNpc().npcType, height);
		}
		Server.getGlobalObjects().remove(17000, height);
		NPCHandler.kill(Zulrah.SNAKELING, height);
	}

}
