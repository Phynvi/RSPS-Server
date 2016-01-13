package ab.model.content.zulrah;

import ab.event.CycleEvent;
import ab.model.players.Player;

public abstract class ZulrahStage extends CycleEvent {
	
	protected Zulrah zulrah;
	
	protected Player player;
	
	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
