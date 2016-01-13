package ab.model.holiday.christmas;

import java.util.Arrays;
import java.util.Calendar;

import ab.event.CycleEvent;
import ab.model.holiday.Holiday;
import ab.model.items.bank.BankItem;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;
import ab.model.players.Player;

public class Christmas extends Holiday {

	public Christmas(String name, Calendar start, Calendar end, CycleEvent event) {
		super(name, start, end, event);
	}

	@Override
	public boolean clickNpc(Player player, int type, int npcId) {
		switch (type) {
			case 1:
				switch (npcId) {
					case 3115:
						switch (player.getHolidayStages().getStage("Christmas")) {
							case 0:
								player.getDH().sendDialogues(580, npcId);
								return true;
								
							case 1:
								player.getDH().sendDialogues(588, npcId);
								return true;
								
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
								player.getDH().sendDialogues(599, npcId);
								return true;
								
							case 7:
								player.getDH().sendDialogues(610, npcId);
								return true;
						}
						break;
						
					case 4895:
						player.getDH().sendDialogues(604, npcId);
						return true;
						
					case 4893:
						player.getDH().sendDialogues(608, npcId);
						return true;
				}
				break;
		}
		return false;
	}

	@Override
	public boolean clickObject(Player player, int type, int objectId, int x, int y) {
		switch (objectId) {
			case 2147:
				if (x == 2957 && y == 3704 || x == 2952 && y == 3821) {
					player.getDH().sendStatement(
							"This ladder leads to an underground mine.",
							"The area seems to be blocked off by rocks.",
							"This must be the cave-in santa was referring to.");
					player.nextChat = -1;
					return true;
				}
				break;
		}
		return false;
	}

	@Override
	public boolean clickButton(Player player, int buttonId) {
		switch (buttonId) {
			case 9157:
				if (player.dialogueAction == 115) {
					player.getDH().sendDialogues(585, 3115);
					return true;
				} else if (player.dialogueAction == 116) {
					player.getHolidayStages().setStage("Christmas", 1);
					player.getDH().sendDialogues(588, 3115);
					return true;
				} else if (player.dialogueAction == 117) {
					player.getDH().sendDialogues(589, 3115);
					return true;
				} else if (player.dialogueAction == 119) {
					player.getPA().spellTeleport(2981, 3632, 0);
					return true;
				}
				break;
				
			case 9158:
				if (player.dialogueAction == 115) {
					player.getDH().sendDialogues(584, 3115);
					return true;
				} else if (player.dialogueAction == 116) {
					player.getDH().sendDialogues(587, 3115);
					return true;
				} else if (player.dialogueAction == 117) {
					player.getHolidayStages().setStage("Christmas", 2);
					player.getDH().sendDialogues(598, 3115);
					return true;
				} else if (player.dialogueAction == 119) {
					player.getPA().closeAllWindows();
					return true;
				}
				break;
				
			case 9178:
				if (player.dialogueAction == 118) {
					player.getDH().sendDialogues(609, 3115);
					return true;
				}
				break;
				
			case 9179:
				if (player.dialogueAction == 118) {
					player.getDH().sendDialogues(589, 3115);
					return true;
				}
				break;
				
			case 9180:
				if (player.dialogueAction == 118) {
					player.getDH().sendDialogues(602, 3115);
					return true;
				}
				break;
				
			case 9181:
				if (player.dialogueAction == 118) {
					player.getPA().closeAllWindows();
					return true;
				}
				break;
		}
		return false;
	}

	@Override
	public boolean clickItem(Player player, int itemId) {
		return false;
	}

	@Override
	public void receive(Player player) {
		if (!player.getItems().addItem(10507, 1)) {
			player.getItems().sendItemToAnyTabOrDrop(new BankItem(10507, 1), player.getX(), player.getY());
		}
	}

	@Override
	public void initializeHoliday() {
		NPCHandler.spawnNpc(3115, 3087, 3497, 0, 1, 0, 0, 0, 0);
		NPCHandler.spawnNpc(6528, 2982, 3642, 0, 1, 1000, 40, 250, 400);
		NPCHandler.spawnNpc(4895, 2950, 3823, 0, 0, 0, 0, 0, 0);
		NPCHandler.spawnNpc(4893, 2960, 3703, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public void finalizeHoliday() {
		for (int npcId : Arrays.asList(3115, 4895, 4893, 6528)) {
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
	}

	@Override
	public boolean completed(Player player) {
		return player.getHolidayStages().getStage(name) == getMaximumStage();
	}

	@Override
	public int getMinimumStage() {
		return 0;
	}

	@Override
	public int getMaximumStage() {
		return 7;
	}
	
	public ChristmasToy forStage(int stage) {
		switch(stage) {
			case 2:
				return ChristmasToy.STAR;
			case 3:
				return ChristmasToy.BOX;
			case 4:
				return ChristmasToy.DIAMOND;
			case 5:
				return ChristmasToy.TREE;
			case 6:
				return ChristmasToy.BELL;
		}
		return null;
	}
	
	public boolean hasToy(Player player) {
		int[] toys = forStage(player.getHolidayStages().getStage("Christmas")).getItems();
		for (int toy : toys) {
			if (player.getItems().playerHasItem(toy) || player.getItems().bankContains(toy)) {
				return true;
			}
		}
		return false;
	}

}
