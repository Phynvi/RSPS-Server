package ab.model.players.skills.firemaking;

import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.model.players.Player;

public class Firemaking extends CycleEvent {
	
	/**
	 * The player that will be making fires
	 */
	private final Player player;
	
	/**
	 * Constructs a new {@link Firemaking} class that will be used for making fires
	 * @param player
	 */
	public Firemaking(Player player) {
		this.player = player;
	}

	@Override
	public void execute(CycleEventContainer container) {
		
	}
}
