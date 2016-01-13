package ab.model.holiday.halloween;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventHandler;
import ab.model.holiday.Holiday;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.util.Misc;

public class Halloween extends Holiday {

	private HalloweenSearchGame searchGame = new HalloweenSearchGame();
	
	public Halloween(String name, Calendar start, Calendar end, CycleEvent event) {
		super(name, start, end, event);
	}

	@Override
	public void initializeHoliday() {
		NPCHandler.spawnNpc(6390, 3088, 3495, 0, 0, 0, 0, 0, 0);
		CycleEventHandler.getSingleton().addEvent(this, super.event, (int) Misc.toCycles(10, TimeUnit.MINUTES));
		searchGame.update();
	}
	
	@Override
	public void finalizeHoliday() {
		System.out.println("Holiday Event finalized.");
		Server.getGlobalObjects().remove(searchGame.chest);
		for (int npcId : Arrays.asList(6390, 2399, 2400, 2401, 2402)) {
			NPC npc = NPCHandler.getNpc(npcId);
			if (npc == null) {
				continue;
			}
			npc.absX = 0;
			npc.absY = 0;
			npc.makeX = 0;
			npc.makeY = 0;
			npc.updateRequired = true;
		}
		for (Player player : PlayerHandler.players) {
			if (player == null) {
				continue;
			}
			Player client = player;
			Server.getGlobalObjects().updateRegionObjects(client);
		}
	}
	
	@Override 
	public boolean completed(Player player) {
		return false;
	}
	
	@Override
	public int getMinimumStage() {
		return 0;
	}

	@Override
	public int getMaximumStage() {
		return 6;
	}

	@Override
	public boolean clickNpc(Player player, int type, int npcId) {
		switch (npcId){
    		case 6390:
    			player.getDH().sendDialogues(505, npcId);
    			return true;
		}
		return false;
	}

	@Override
	public boolean clickObject(Player player, int type, int objectId, int x, int y) {
		switch (objectId) {
			case HalloweenSearchGame.CHEST_ID:
				searchGame.receive(player, x, y);
				return true;
		}
		return false;
	}

	@Override
	public boolean clickButton(Player player, int buttonId) {
		if (player.getHolidayStages().getStage("Halloween") == 0) {
			if (buttonId == 9167) {
				player.getHolidayStages().setStage("Halloween", 1);
				player.getDH().sendDialogues(514, 6390);
				return true;
			}
			if (buttonId == 9168) {
				player.getHolidayStages().setStage("Halloween", 1);
				player.getDH().sendDialogues(515, 6390);
				return true;
			}
			if (buttonId == 9169) {
				player.getDH().sendDialogues(516, 6390);
				return true;
			}
		} else if (player.getHolidayStages().getStage("Halloween") >= 5) {
			if (buttonId == 9167) {
				player.getDH().sendDialogues(536, 6390);
				return true;
			}
			if (buttonId == 9168) {
				player.getDH().sendDialogues(537, 6390);
				return true;
			}
			if (buttonId == 9169) {
				player.getPA().closeAllWindows();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean clickItem(Player player, int itemId) {
		if (itemId == 611) {
			if (player.getHolidayStages().getStage("Halloween") >= 4) {
				searchGame.operateLocator(player);
				return true;
			}
		}
		return false;
	}

	@Override
	public void receive(Player player) {
		for(int i = 0; i < 6; i++) {
			if (player.getItems().freeSlots() > 0) {
				player.getItems().addItem(9920 + i, 1);
			} else {
				player.getItems().sendItemToAnyTab(9920 + i, 1);
			}
		}
		player.getPA().closeAllWindows();
		player.nextChat = -1;
	}
	
	public HalloweenSearchGame getSearchGame() {
		return searchGame;
	}

}
