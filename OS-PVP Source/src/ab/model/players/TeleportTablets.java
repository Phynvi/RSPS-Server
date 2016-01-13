package ab.model.players;

import ab.Config;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;

/**
 * 
 * @author Mack
 *
 */
public class TeleportTablets {
	
	public enum TabType {
		HOME(8013, Config.RESPAWN_X, Config.RESPAWN_Y, 0),
		ANNAKARL(12775, Config.ANNAKARL_X, Config.ANNAKARL_Y, 0),
		CARRALLANGER(12776, Config.CARRALLANGAR_X, Config.CARRALLANGAR_Y, 0),
		DAREEYAK(12777, Config.DAREEYAK_X, Config.DAREEYAK_Y, 0),
		GHORROCK(12778, Config.GHORROCK_X, Config.GHORROCK_Y, 0),
		KHARYRLL(12779, Config.KHARYRLL_X, Config.KHARYRLL_Y, 0),
		LASSAR(12780, Config.LASSAR_X, Config.LASSAR_Y, 0),
		PADDEWWA(12781, Config.PADDEWWA_X, Config.PADDEWWA_Y, 0),
		SENNTISTEN(12782, Config.SENNTISTEN_X, Config.SENNTISTEN_Y, 0),
		WILDY_RESOURCE(12409, 3184, 3945, 0),
		PIRATE_HUT(12407, 3045, 3956, 0),
		MAGE_BANK(12410, 2538, 4716, 0),
		CALLISTO(12408, 3325, 3849, 0),
		KBD_LAIR(12411, 2271, 4681, 0);
		
		private int tab;
		private int x;
		private int y;
		private int z;
		
		public int getTabId() {
			return tab;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
		}
		
		TabType(int tab, int x, int y, int z) {
			this.tab = tab;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	};
	
	/**
	 * Operates the teleport tab
	 * @param player
	 * @param item
	 */
	public static void operate(final Player player, int item) {
		for (TabType type : TabType.values()) {
			if (type.getTabId() == item) {
				if (!player.getPA().canTeleport("modern")) {
					return;
				}
				player.teleporting = true;
				player.getItems().deleteItem(type.getTabId(), 1);
				player.startAnimation(4731, 0);
				player.gfx0(678);
				final int x = type.getX();
				final int y = type.getY();
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						player.teleportToX = x;
						player.teleportToY = y;
						player.teleporting = false;
						player.gfx0(-1);
						player.startAnimation(65535);
						container.stop();
					}
					
				}, 3);
			}
		}
	}

}
