package ab.model.players.packets;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.text.WordUtils;

import ab.model.players.combat.Degrade;
import ab.model.players.combat.Degrade.DegradableItem;
import ab.model.players.skills.LeatherMaking;
import ab.model.players.skills.Tanning;
import ab.model.players.skills.CraftingData.tanningData;
import ab.Config;
import ab.Server;
import ab.model.content.help.HelpDatabase;
import ab.model.content.kill_streaks.Killstreak;
import ab.model.items.ItemAssistant;
import ab.model.items.ItemCombination;
import ab.model.items.bank.BankItem;
import ab.model.items.bank.BankTab;
import ab.model.minigames.bounty_hunter.BountyHunterEmblem;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.npcs.NPCDeathTracker.NPCName;
import ab.model.players.Player;
import ab.model.players.DiceHandler;
import ab.model.players.PacketType;
import ab.model.players.Rights;
import ab.model.players.PlayerAssistant.PointExchange;
import ab.model.players.skills.Cooking;
import ab.model.players.skills.Fletching;
import ab.model.players.skills.Smelting;
import ab.server.data.SerializablePair;
import ab.util.Misc;

/**
 * Clicking most buttons
 *
 */
public class ClickingButtons implements PacketType {

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		int actionButtonId = Misc.hexToInt(c.getInStream().buffer, 0, packetSize);
		if (c.isDead || c.playerLevel[3] <= 0) {
			return;
		}
		if (c.getRights().isOwner()) {
			Misc.println(c.playerName + " - actionbutton: " + actionButtonId);
		}
		if (c.getInterfaceEvent().isActive()) {
			c.getInterfaceEvent().clickButton(actionButtonId);
			return;
		}
		if (actionButtonId >= 232182 && actionButtonId <= 233022) {
			HelpDatabase.getDatabase().view(c, actionButtonId);
			HelpDatabase.getDatabase().delete(c, actionButtonId);
			return;
		}
		c.getPestControlRewards().click(actionButtonId);
		if (c.getTitles().click(actionButtonId)) {
			return;
		}
		if(c.craftDialogue) {
			LeatherMaking.craftLeather(c, actionButtonId);
		}
		for (tanningData t : tanningData.values()) {
			if (actionButtonId == t.getButtonId(actionButtonId)) {
				Tanning.tanHide(c, actionButtonId);
			}
		}
		if (c.getPresets().clickButton(actionButtonId)) {
			return;
		}
		int[] spellIds = { 4128, 4130, 4132, 4134, 4136, 4139, 4142, 4145, 4148, 4151, 4153, 4157, 4159, 4161, 4164, 4165, 4129, 4133, 4137, 6006,
				6007, 6026, 6036, 6046, 6056, 4147, 6003, 47005, 4166, 4167, 4168, 48157, 50193, 50187, 50101, 50061, 50163, 50211, 50119, 50081,
				50151, 50199, 50111, 50071, 50175, 50223, 50129, 50091 };
		for (int i = 0; i < spellIds.length; i++) {
			if (actionButtonId == spellIds[i]) {
				c.autocasting = true;
				c.autocastId = i;
			}
		}
		if (Server.getHolidayController().clickButton(c, actionButtonId)) {
			return;
		}
		if (c.getPunishmentPanel().clickButton(actionButtonId)) {
			return;
		}
		DuelSession duelSession = null;
		Fletching.attemptData(c, actionButtonId);
		switch (actionButtonId) {
		case 109157:
		case 109159:
		case 109161:
			c.getBH().teleportToTarget();
			break;
		case 55095:// This is the button id
			c.getPA().destroyItem(c.droppedItem);// Choosing Yes will delete the
													// item and make it
													// dissapear
			c.droppedItem = -1;
			break;
		case 55096:// This is the button id
			c.getPA().removeAllWindows();// Choosing No will remove all the
											// windows
			c.droppedItem = -1;
			break;

		case 191109:
			c.getAchievements().currentInterface = 0;
			c.getAchievements().drawInterface(0);
			break;

		case 191110:
			c.getAchievements().currentInterface = 1;
			c.getAchievements().drawInterface(1);
			break;

		case 191111:
			c.getAchievements().currentInterface = 2;
			c.getAchievements().drawInterface(2);
			break;

		case 20174:
			c.getPA().closeAllWindows();
			ab.model.items.bank.BankPin pin = c.getBankPin();
			if (pin.getPin().length() <= 0)
				c.getBankPin().open(1);
			else if (!pin.getPin().isEmpty() && !pin.isAppendingCancellation())
				c.getBankPin().open(3);
			else if (!pin.getPin().isEmpty() && pin.isAppendingCancellation())
				c.getBankPin().open(4);
			break;

		case 226162:
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
				return;
			}
			if (!c.isBanking)
				return;
			if (System.currentTimeMillis() - c.lastBankDeposit < 3000)
				return;
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			c.lastBankDeposit = System.currentTimeMillis();
			for (int slot = 0; slot < c.playerItems.length; slot++) {
				if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
					c.getItems().addToBank(c.playerItems[slot] - 1, c.playerItemsN[slot], false);
				}
			}
			c.getItems().updateInventory();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			break;

		case 226170:
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
				return;
			}
			if (!c.isBanking)
				return;
			if (System.currentTimeMillis() - c.lastBankDeposit < 3000)
				return;
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			c.lastBankDeposit = System.currentTimeMillis();
			for (int slot = 0; slot < c.playerEquipment.length; slot++) {
				if (c.playerEquipment[slot] > 0 && c.playerEquipmentN[slot] > 0) {
					c.getItems().addEquipmentToBank(c.playerEquipment[slot], slot, c.playerEquipmentN[slot], false);
					c.getItems().wearItem(-1, 0, slot);
				}
			}
			c.getItems().updateInventory();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			break;

		case 226186:
		case 226198:
		case 226209:
		case 226220:
		case 226231:
		case 226242:
		case 226253:
		case 227008:
		case 227019:
			if (!c.isBanking) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			int tabId = actionButtonId == 226186 ? 0 : actionButtonId == 226198 ? 1 : actionButtonId == 226209 ? 2 : actionButtonId == 226220 ? 3
					: actionButtonId == 226231 ? 4 : actionButtonId == 226242 ? 5 : actionButtonId == 226253 ? 6 : actionButtonId == 227008 ? 7
							: actionButtonId == 227019 ? 8 : -1;
			if (tabId <= -1 || tabId > 8)
				return;
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset(tabId);
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			BankTab tab = c.getBank().getBankTab(tabId);
			if (tab.getTabId() == c.getBank().getCurrentBankTab().getTabId())
				return;
			if (tab.size() <= 0 && tab.getTabId() != 0) {
				c.sendMessage("Drag an item into the new tab slot to create a tab.");
				return;
			}
			c.getBank().setCurrentBankTab(tab);
			c.getPA().openUpBank();
			break;

		case 226197:
		case 226208:
		case 226219:
		case 226230:
		case 226241:
		case 226252:
		case 227007:
		case 227018:
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
				return;
			}
			if (!c.isBanking) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			tabId = actionButtonId == 226197 ? 1 : actionButtonId == 226208 ? 2 : actionButtonId == 226219 ? 3 : actionButtonId == 226230 ? 4
					: actionButtonId == 226241 ? 5 : actionButtonId == 226252 ? 6 : actionButtonId == 227007 ? 7 : actionButtonId == 227018 ? 8 : -1;
			tab = c.getBank().getBankTab(tabId);
			if (tab == null || tab.getTabId() == 0 || tab.size() == 0) {
				c.sendMessage("You cannot collapse this tab.");
				return;
			}
			if (tab.size() + c.getBank().getBankTab()[0].size() >= Config.BANK_SIZE) {
				c.sendMessage("You cannot collapse this tab. The contents of this tab and your");
				c.sendMessage("main tab are greater than " + Config.BANK_SIZE + " unique items.");
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			for (BankItem item : tab.getItems()) {
				c.getBank().getBankTab()[0].add(item);
			}
			tab.getItems().clear();
			if (tab.size() == 0) {
				c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
			}
			c.getPA().openUpBank();
			break;

		case 226185:
		case 226196:
		case 226207:
		case 226218:
		case 226229:
		case 226240:
		case 226251:
		case 227006:
		case 227017:
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
				return;
			}
			if (!c.isBanking) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			tabId = actionButtonId == 226185 ? 0 : actionButtonId == 226196 ? 1 : actionButtonId == 226207 ? 2 : actionButtonId == 226218 ? 3
					: actionButtonId == 226229 ? 4 : actionButtonId == 226240 ? 5 : actionButtonId == 226251 ? 6 : actionButtonId == 227006 ? 7
							: actionButtonId == 227017 ? 8 : -1;
			tab = c.getBank().getBankTab(tabId);
			long value = 0;
			if (tab == null || tab.size() == 0)
				return;
			for (BankItem item : tab.getItems()) {
				long tempValue = item.getId() - 1 == 995 ? 1 : c.getShops().getItemShopValue(item.getId() - 1);
				value += tempValue * item.getAmount();
			}
			c.sendMessage("<col=255>The total networth of tab " + tab.getTabId() + " is </col><col=600000>" + Long.toString(value) + " gp</col>.");
			break;

		case 22024:
		case 86008:
			c.getPA().openUpBank();
			break;

		case 226154:
			c.takeAsNote = !c.takeAsNote;// rerun that
			break;

		case 10252:
			c.antiqueSelect = 0;
			c.sendMessage("You select Attack");
			break;
		  case 113238:
              if (c.inTrade || c.inDuel) {
                  return;
              }
          c.getDH().sendDialogues(12000, -1);
              break;
          case 113239:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else {
                  c.getItems().addItem(386, 1000);
              }
              break;
          case 113240:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else {
                  c.getItems().addItem(6686, 1000);
                  c.getItems().addItem(3025, 1000);
                  c.getItems().addItem(2437, 1000);
                  c.getItems().addItem(2441, 1000);
                  c.getItems().addItem(3041, 1000);
                  c.getItems().addItem(2445, 1000);
              }
              break;
          case 113246:
            if (c.inWild() || c.inCamWild() || c.inTrade || c.inDuelArena() || c.underAttackBy > 0 || c.underAttackBy2 > 0) {
                return;
            } else {
                c.getDH().sendDialogues(14200, -1);
                }
      break;
                 case 113247:
            if (c.inWild() || c.inCamWild() || c.inTrade || c.inDuelArena() || c.underAttackBy > 0 || c.underAttackBy2 > 0) {
                return;
            } else {
                c.getDH().sendDialogues(14201, -1);
                }
      break;
          case 113241:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else {
                  c.getDH().sendOption2("Veng Runes", "Barrage Runes");
                  c.dialogueAction = 113239;
              }
              break;
          case 113242:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else if (c.getItems().freeSlots() >= 20) {
                  c.getItems().addItem(4091, 1);
                  c.getItems().addItem(4093, 1);
                  c.getItems().addItem(3840, 1);
                  c.getItems().addItem(2412, 1);
                  c.getItems().addItem(4097, 1);
                  c.getItems().addItem(7461, 1);
                  c.getItems().addItem(10828, 1);
                  c.getItems().addItem(1712, 1);
                  c.getItems().addItem(2550, 1);
                  c.getItems().addItem(4675, 1);
                  c.getItems().addItem(1201, 1);
                  c.getItems().addItem(1127, 1);
                  c.getItems().addItem(4151, 1);
                  c.getItems().addItem(5698, 1);
                  c.getItems().addItem(2414, 1);
                  c.getItems().addItem(11840, 1);
                  c.getItems().addItem(2550, 1);
                  c.getItems().addItem(1079, 1);
                  c.getItems().addItem(2503, 1);
                  c.getItems().addItem(2497, 1);
              } else {
                  c.sendMessage("Please make atleast 20 free slots before spawning this set!");
              }
              break;
          case 113243:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else if (c.getItems().freeSlots() >= 11) {
                  c.getItems().addItem(7461, 1);
                  c.getItems().addItem(10828, 1);
                  c.getItems().addItem(1712, 1);
                  c.getItems().addItem(1201, 1);
                  c.getItems().addItem(1127, 1);
                  c.getItems().addItem(4151, 1);
                  c.getItems().addItem(5698, 1);
                  c.getItems().addItem(2414, 1);
                  c.getItems().addItem(11840, 1);
                  c.getItems().addItem(2550, 1);
                  c.getItems().addItem(1079, 1);
              } else {
                  c.sendMessage("Please make atleast 11 free slots before spawning this set!");
              }
              break;
          case 113244:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else if (c.getItems().freeSlots() >= 12) {
                  c.getItems().addItem(3105, 1);
                  c.getItems().addItem(1712, 1);
                  c.getItems().addItem(4151, 1);
                  c.getItems().addItem(5698, 1);
                  c.getItems().addItem(6107, 1);
                  c.getItems().addItem(6108, 1);
                  c.getItems().addItem(4502, 1);
                  c.getItems().addItem(6568, 1);
                  c.getItems().addItem(3842, 1);
                  c.getItems().addItem(2497, 1);
                  c.getItems().addItem(7458, 1);
                  c.getItems().addItem(2550, 1);
              } else {
                  c.sendMessage("Please make atleast 12 free slots before spawning this set!");
              }
              break;
          case 113245:
              if (c.inWild() || c.inCamWild()) {
                  c.sendMessage("You can't spawn in the wilderness.");
              } else if (c.getItems().freeSlots() >= 13) {
                  c.getItems().addItem(11840, 1);
                  c.getItems().addItem(10499, 1);
                  c.getItems().addItem(9185, 1);
                  c.getItems().addItem(9244, 100);
                  c.getItems().addItem(861, 1);
                  c.getItems().addItem(892, 100);
                  c.getItems().addItem(3749, 1);
                  c.getItems().addItem(1712, 1);
                  c.getItems().addItem(7461, 1);
                  c.getItems().addItem(2550, 1);
                  c.getItems().addItem(2503, 1);
                  c.getItems().addItem(2497, 1);
                  c.getItems().addItem(1187, 1);
              } else {
                  c.sendMessage("Please make atleast 13 free slots before spawning this set!");
              }
              break;
		case 113230:
			if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c) || c.underAttackBy > 0) {
				c.sendMessage("Please finish what you are doing before viewing your achievements.");
				return;
			}
			c.getAchievements().drawInterface(0);
			break;
		 case 164034:
	            c.removedTasks[0] = -1;
	            c.getSlayer().updateCurrentlyRemoved();
	            break;
	            
	        case 164035:
	            c.removedTasks[1] = -1;
	            c.getSlayer().updateCurrentlyRemoved();
	            break;
	            
	        case 164036:
	            c.removedTasks[2] = -1;
	            c.getSlayer().updateCurrentlyRemoved();
	            break;
	            
	        case 164037:
	            c.removedTasks[3] = -1;
	            c.getSlayer().updateCurrentlyRemoved();
	            break;


	        case 164028:
	            c.getSlayer().cancelTask();
	            break;
	        case 164029:
	            c.getSlayer().removeTask();
	            break;    
	        case 160052:
	            c.getSlayer().buySlayerExperience();
	            break;
	        case 160053:
	            c.getSlayer().buyRespite();
	            break;
	        case 160054:
	            c.getSlayer().buySlayerDart();
	            break;
	        case 160055:
	            c.getSlayer().buyBroadArrows();
	            break;

	        case 160045:
	        case 162033:
	        case 164021:
	            if (c.interfaceId != 41000)
	                c.getSlayer().handleInterface("buy");
	            break;


	        case 160047:
	        case 162035:
	        case 164023:
	            if (c.interfaceId != 41500)
	                c.getSlayer().handleInterface("learn");
	            break;


	        case 160049:
	        case 162037:
	        case 164025:
	            if (c.interfaceId != 42000)
	                c.getSlayer().handleInterface("assignment");
	            break;

	        case 162030:
	        case 164018:
	        case 160042:
	            c.getPA().removeAllWindows();
	            break;	
		case 113236:
			DecimalFormat df = new DecimalFormat("#.##");
			double ratio = ((double) c.KC) / ((double) c.DC);
			c.forcedChat("I have a total of "+c.KC+" kills and "+c.DC+" deaths. Kill/Death ratio is: " + df.format(ratio) + "");
			break;
		case 113237:
			c.forcedChat("My Hunter killstreak is: " + c.getKillstreak().getAmount(Killstreak.Type.HUNTER) + " and my Rogue killstreak is: " + c.getKillstreak().getAmount(Killstreak.Type.ROGUE) + " ");
			break;
		case 113235:
			c.forcedChat("I currently have: " + c.pkp + " PK Points.");
			break;
		case 10253:
			c.antiqueSelect = 2;
			c.sendMessage("You select Strength");
			break;
		case 10254:
			c.antiqueSelect = 4;
			c.sendMessage("You select Ranged");
			break;
		case 10255:
			c.antiqueSelect = 6;
			c.sendMessage("You select Magic");
			break;
		case 11000:
			c.antiqueSelect = 1;
			c.sendMessage("You select Defence");
			break;
		case 11001:
			c.antiqueSelect = 3;
			c.sendMessage("You select Hitpoints");
			break;
		case 11002:
			c.antiqueSelect = 5;
			c.sendMessage("You select Prayer");
			break;
		case 11003:
			c.antiqueSelect = 16;
			c.sendMessage("You select Agility");
			break;
		case 11004:
			c.antiqueSelect = 15;
			c.sendMessage("You select Herblore");
			break;
		case 11005:
			c.antiqueSelect = 17;
			c.sendMessage("You select Thieving");
			break;
		case 11006:
			c.sendMessage("Sorry, but you can not select Slayer.");
			break;
		case 11007:
			c.antiqueSelect = 20;
			c.sendMessage("You select Runecrafting");
			break;
		case 47002:
			c.sendMessage("Sorry, but you can not select Slayer.");
			break;
		case 54090:
			c.antiqueSelect = 19;
			c.sendMessage("You select Farming");
			break;
		case 11008:
			c.antiqueSelect = 14;
			c.sendMessage("You select Mining");
			break;
		case 11009:
			c.antiqueSelect = 13;
			c.sendMessage("You select Smithing");
			break;
		case 11010:
			c.antiqueSelect = 10;
			c.sendMessage("You select Fishing");
			break;
		case 11011:
			c.antiqueSelect = 7;
			c.sendMessage("You select Cooking");
			break;
		case 11012:
			c.antiqueSelect = 11;
			c.sendMessage("You select Firemaking");
			break;
		case 11013:
			c.antiqueSelect = 8;
			c.sendMessage("You select Woodcutting");
			break;
		case 11014:
			c.antiqueSelect = 9;
			c.sendMessage("You select Fletching");
			break;
		case 11015:
		/*	if (c.usingLamp) {
				if (c.antiqueLamp && !c.normalLamp) {
					c.usingLamp = false;
					c.getPA().addSkillXP(13100000, c.antiqueSelect);
					c.getItems().deleteItem2(4447, 1);
					c.sendMessage("The lamp mysteriously vanishes...");
					c.getPA().closeAllWindows();
				}
				if (c.normalLamp && !c.antiqueLamp) {
					c.usingLamp = false;
					c.getPA().addSkillXP(150000, c.antiqueSelect);
					c.getItems().deleteItem2(2528, 1);
					c.sendMessage("The lamp mysteriously vanishes...");
					c.sendMessage("...and you gain some experience!");
					c.getPA().closeAllWindows();
				}
			} else {
				c.sendMessage("You must rub a lamp to gain the experience.");
				return;
			}*/
			break;

		/*
		 * case 28172: if (c.expLock == false) { c.expLock = true;
		 * c.sendMessage(
		 * "Your experience is now locked. You will not gain experience.");
		 * c.getPA().sendFrame126("@whi@EXP: @gre@LOCKED", 7340); } else {
		 * c.expLock = false; c.sendMessage(
		 * "Your experience is now unlocked. You will gain experience.");
		 * c.getPA().sendFrame126("@whi@EXP: @gre@UNLOCKED", 7340); } break;
		 */
		case 28215:
			if (c.slayerTask <= 0) {
				c.sendMessage("You do not have a task, please talk with a slayer master.");
			} else {
				c.forcedText = "I must slay another " + c.taskAmount + " " + Server.npcHandler.getNpcListName(c.slayerTask) + ".";
				c.forcedChatUpdateRequired = true;
				c.updateRequired = true;
			}
			break;
		/* End Quest */
		case 15147:// Bronze, 1
			Smelting.startSmelting(c, actionButtonId, 0, 0);
			break;
		case 15146:// Bronze, 5
			Smelting.startSmelting(c, actionButtonId, 0, 1);
			break;
		case 10247:// Bronze, 10
			Smelting.startSmelting(c, actionButtonId, 0, 2);
			break;
		case 9110:// Bronze, 28
			Smelting.startSmelting(c, actionButtonId, 0, 3);
			break;
		case 15151:// Iron, 1
			Smelting.startSmelting(c, actionButtonId, 1, 0);
			break;
		case 15150:// Iron, 5
			Smelting.startSmelting(c, actionButtonId, 1, 1);
			break;
		case 15149:// Iron, 10
			Smelting.startSmelting(c, actionButtonId, 1, 2);
			break;
		case 15148:// Iron, 28
			Smelting.startSmelting(c, actionButtonId, 1, 3);
			break;
		case 15155:// silver, 1
			Smelting.startSmelting(c, actionButtonId, 2, 0);
			break;
		case 15154:// silver, 5
			Smelting.startSmelting(c, actionButtonId, 2, 1);
			break;
		case 15153:// silver, 10
			Smelting.startSmelting(c, actionButtonId, 2, 2);
			break;
		case 15152:// silver, 28
			Smelting.startSmelting(c, actionButtonId, 2, 3);
			break;
		case 15159:// steel, 1
			Smelting.startSmelting(c, actionButtonId, 3, 0);
			break;
		case 15158:// steel, 5
			Smelting.startSmelting(c, actionButtonId, 3, 1);
			break;
		case 15157:// steel, 10
			Smelting.startSmelting(c, actionButtonId, 3, 2);
			break;
		case 15156:// steel, 28
			Smelting.startSmelting(c, actionButtonId, 3, 3);
			break;
		case 15163:// gold, 1
			Smelting.startSmelting(c, actionButtonId, 4, 0);
			break;
		case 15162:// gold, 5
			Smelting.startSmelting(c, actionButtonId, 4, 1);
			break;
		case 15161:// gold, 10
			Smelting.startSmelting(c, actionButtonId, 4, 2);
			break;
		case 15160:// gold, 28
			Smelting.startSmelting(c, actionButtonId, 4, 3);
			break;
		case 29017:// mithril, 1
			Smelting.startSmelting(c, actionButtonId, 5, 0);
			break;
		case 29016:// mithril, 5
			Smelting.startSmelting(c, actionButtonId, 5, 1);
			break;
		case 24253:// mithril, 10
			Smelting.startSmelting(c, actionButtonId, 5, 2);
			break;
		case 16062:// mithril, 28
			Smelting.startSmelting(c, actionButtonId, 5, 3);
			break;
		case 29022:// addy, 1
			Smelting.startSmelting(c, actionButtonId, 6, 0);
			break;
		case 29021:// addy, 5
			Smelting.startSmelting(c, actionButtonId, 6, 1);
			break;
		case 29019:// addy, 10
			Smelting.startSmelting(c, actionButtonId, 6, 2);
			break;
		case 29018:// addy, 28
			Smelting.startSmelting(c, actionButtonId, 6, 3);
			break;
		case 29026:// rune, 1
			Smelting.startSmelting(c, actionButtonId, 7, 0);
			break;
		case 29025:// rune, 5
			Smelting.startSmelting(c, actionButtonId, 7, 1);
			break;
		case 29024:// rune, 10
			Smelting.startSmelting(c, actionButtonId, 7, 2);
			break;
		case 29023:// rune, 28
			Smelting.startSmelting(c, actionButtonId, 7, 3);
			break;

		/*
		 * case 58025: case 58026: case 58027: case 58028: case 58029: case
		 * 58030: case 58031: case 58032: case 58033: case 58034:
		 * c.getBankPin().pinEnter(actionButtonId); break;
		 */

		case 53152:
			Cooking.getAmount(c, 1);
			break;
		case 53151:
			Cooking.getAmount(c, 5);
			break;
		case 53150:
			Cooking.getAmount(c, 10);
			break;
		case 53149:
			Cooking.getAmount(c, 28);
			break;
		case 33206:
			c.outStream.createFrame(27);
			c.attackSkill = true;
			break;
			case 33209:
			c.outStream.createFrame(27);
			c.strengthSkill = true;
			break;
			case 33212:
			c.outStream.createFrame(27);
			c.defenceSkill = true;
			break;
			case 33215:
			c.outStream.createFrame(27);
			c.rangeSkill = true;
			break;
			case 33218:
			c.outStream.createFrame(27);
			c.prayerSkill = true;
			break;
			case 33221:
			c.outStream.createFrame(27);
			c.mageSkill = true;
			break;
			case 33207:
			c.outStream.createFrame(27);
			c.healthSkill = true;
			break;
		case 33224: // runecrafting
			c.getSI().runecraftingComplex(1);
			c.getSI().selected = 6;
			break;
		case 33210: // agility
			c.getSI().agilityComplex(1);
			c.getSI().selected = 8;
			break;
		case 33213: // herblore
			c.getSI().herbloreComplex(1);
			c.getSI().selected = 9;
			break;
		case 33216: // theiving
			c.getSI().thievingComplex(1);
			c.getSI().selected = 10;
			break;
		case 33219: // crafting
			c.getSI().craftingComplex(1);
			c.getSI().selected = 11;
			break;
		case 33222: // fletching
			c.getSI().fletchingComplex(1);
			c.getSI().selected = 12;
			break;
		case 47130:// slayer
			c.getSI().slayerComplex(1);
			c.getSI().selected = 13;
			break;
		case 33208:// mining
			c.getSI().miningComplex(1);
			c.getSI().selected = 14;
			break;
		case 33211: // smithing
			c.getSI().smithingComplex(1);
			c.getSI().selected = 15;
			break;
		case 33214: // fishing
			c.getSI().fishingComplex(1);
			c.getSI().selected = 16;
			break;
		case 33217: // cooking
			c.getSI().cookingComplex(1);
			c.getSI().selected = 17;
			break;
		case 33220: // firemaking
			c.getSI().firemakingComplex(1);
			c.getSI().selected = 18;
			break;
		case 33223: // woodcut
			c.getSI().woodcuttingComplex(1);
			c.getSI().selected = 19;
			break;
		case 54104: // farming
			c.getSI().farmingComplex(1);
			c.getSI().selected = 20;
			break;

		case 34142: // tab 1
			c.getSI().menuCompilation(1);
			break;

		case 34119: // tab 2
			c.getSI().menuCompilation(2);
			break;

		case 34120: // tab 3
			c.getSI().menuCompilation(3);
			break;

		case 34123: // tab 4
			c.getSI().menuCompilation(4);
			break;

		case 34133: // tab 5
			c.getSI().menuCompilation(5);
			break;

		case 34136: // tab 6
			c.getSI().menuCompilation(6);
			break;

		case 34139: // tab 7
			c.getSI().menuCompilation(7);
			break;

		case 34155: // tab 8
			c.getSI().menuCompilation(8);
			break;

		case 34158: // tab 9
			c.getSI().menuCompilation(9);
			break;

		case 34161: // tab 10
			c.getSI().menuCompilation(10);
			break;

		case 59199: // tab 11
			c.getSI().menuCompilation(11);
			break;

		case 59202: // tab 12
			c.getSI().menuCompilation(12);
			break;
		case 59203: // tab 13
			c.getSI().menuCompilation(13);
			break;

		case 150:
			if (c.autoRet == 0) {
				c.autoRet = 1;
			} else {
				c.autoRet = 0;
			}
			break;
		// 1st tele option
		case 9190:
			if (c.dialogueAction == 128) {
				c.getFightCave().create(1);
				return;
			}
			switch(c.teleAction) {
			case 2:
				c.getPA().spellTeleport(3429+Misc.random(1), 3538+Misc.random(1), 0);
				break;
			}
			if (c.dialogueAction == 123) {
				DegradableItem[] claimable = Degrade.getClaimedItems(c);
				if (claimable.length == 0) {
					return;
				}
				c.getPA().removeAllWindows();
				Degrade.claim(c, claimable[0].getItemId());
				return;
			}
			if (c.dialogueAction == 121) {
				c.getDH().sendDialogues(614, -1);
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 14) {
				c.getPA().spellTeleport(2900, 4449, 0);
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 12) {
				c.getPA().spellTeleport(3302, 9361, 0);
			}
			if (c.teleAction == 11) {
				c.getPA().spellTeleport(3228, 9392, 0);
			}
			if (c.teleAction == 10) {
				c.getPA().spellTeleport(2705, 9487, 0);
			}
			if (c.teleAction == 9) {
				c.getPA().spellTeleport(3226, 3263, 0);
			}
			if (c.teleAction == 8) {
				c.getPA().spellTeleport(3293, 3178, 0);
			}
			if (c.teleAction == 7) {
				c.getPA().spellTeleport(3118, 9851, 0);
			}
			if (c.teleAction == 1) {
				// rock crabs
				c.getPA().spellTeleport(2561, 3311, 0);
			} else if (c.teleAction == 200) {
				// barrows
				// c.getPA().spellTeleport(3565, 3314, 0);
				// c.getItems().addItem(952, 1);
			} else if (c.teleAction == 3) {
				c.getPA().spellTeleport(3005, 3850, 0);
			} else if (c.teleAction == 4) {
				// varrock wildy
				c.getPA().spellTeleport(3025, 3379, 0);
			} else if (c.teleAction == 5) {
				c.getPA().spellTeleport(3046, 9779, 0);
			} else if (c.teleAction == 2000) {
				// lum
				c.getPA().spellTeleport(3222, 3218, 0);// 3222 3218
			} else {
				DiceHandler.handleClick(c, actionButtonId);
			}
			if (c.dialogueAction == 10) {
				c.getPA().spellTeleport(2845, 4832, 0);
				c.dialogueAction = -1;

			} else if (c.dialogueAction == 11) {
				c.getPA().spellTeleport(2786, 4839, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 12) {
				c.getPA().spellTeleport(2398, 4841, 0);
				c.dialogueAction = -1;
			}
			break;
		// mining - 3046,9779,0
		// smithing - 3079,9502,0

		// 2nd tele option
		case 9191:
			if (c.dialogueAction == 128) {
				c.getFightCave().create(2);
				return;
			}
			switch(c.teleAction) {
			case 2:
				c.getPA().spellTeleport(3133+Misc.random(1), 9911+Misc.random(1), 0);
				break;
			}
			if (c.dialogueAction == 123) {
				DegradableItem[] claimable = Degrade.getClaimedItems(c);
				if (claimable.length < 2) {
					return;
				}
				c.getPA().removeAllWindows();
				Degrade.claim(c, claimable[1].getItemId());
				return;
			}
			if (c.dialogueAction == 121) {
				c.getDH().sendDialogues(616, -1);
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 14) {
				c.getPA().spellTeleport(3292, 3648, 0);
				c.sendMessage("@blu@The kraken is roughly 20 steps south-west of this location.");
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			}
			if (c.teleAction == 12) {
				c.getPA().spellTeleport(2908, 9694, 0);
			}
			if (c.teleAction == 11) {
				c.getPA().spellTeleport(3237, 9384, 0);
			}
			if (c.teleAction == 10) {
				c.getPA().spellTeleport(3219, 9366, 0);
			}
			if (c.teleAction == 9) {
				c.getPA().spellTeleport(2916, 9800, 0);
			}
			if (c.teleAction == 8) {
				c.getPA().spellTeleport(2903, 9849, 0);
			}
			if (c.teleAction == 7) {
				c.getPA().spellTeleport(2859, 9843, 0);
			}
			if (c.teleAction == 3) {
				// kbd
				// c.sendMessage("King Black Dragon has been disabled.");
				c.getPA().spellTeleport(3262, 3929, 0);
			}
			// c.getPA().closeAllWindows();
			/*
			 * if (c.teleAction == 1) { //rock crabs
			 * c.getPA().spellTeleport(2676, 3715, 0); } else if (c.teleAction
			 * == 2) { //taverly dungeon c.getPA().spellTeleport(2884, 9798, 0);
			 * } else if (c.teleAction == 3) { //kbd
			 * c.getPA().spellTeleport(3007, 3849, 0); } else if (c.teleAction
			 * == 4) { //west lv 10 wild c.getPA().spellTeleport(2979, 3597, 0);
			 * } else if (c.teleAction == 5) {
			 * c.getPA().spellTeleport(3079,9502,0); }
			 */
			if (c.teleAction == 1) {
				// slay dungeon
				c.getPA().spellTeleport(2980, 3871, 0);
			} else if (c.teleAction == 200) {
				// pest control
				// c.getPA().spellTeleport(3252, 3894, 0);
			} else if (c.teleAction == 4) {
				// graveyard
				c.getPA().spellTeleport(3043, 9779, 0);
			} else if (c.teleAction == 5) {
				c.getPA().spellTeleport(3079, 9502, 0);

			} else if (c.teleAction == 2000) {
				c.getPA().spellTeleport(3210, 3424, 0);// 3210 3424
			} else if (c.dialogueAction == 10) {
				c.getPA().spellTeleport(2796, 4818, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 11) {
				c.getPA().spellTeleport(2527, 4833, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 12) {
				c.getPA().spellTeleport(2464, 4834, 0);
				c.dialogueAction = -1;
			}
			// 3rd tele option

		case 9192:
			if (c.dialogueAction == 128) {
				c.getFightCave().create(3);
				return;
			}
			switch(c.teleAction) {
			case 2:
				c.getPA().spellTeleport(2884+Misc.random(1), 9799+Misc.random(1), 0);
				break;
			}
			if (c.dialogueAction == 123) {
				DegradableItem[] claimable = Degrade.getClaimedItems(c);
				if (claimable.length < 3) {
					return;
				}
				c.getPA().removeAllWindows();
				Degrade.claim(c, claimable[2].getItemId());
				return;
			}
			if (c.dialogueAction == 121) {
				c.getDH().sendDialogues(617, -1);
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 14) {
				c.getPA().spellTeleport(3234, 3745, 0);
			}
			if (c.teleAction == 12) {
				c.getPA().spellTeleport(2739, 5088, 0);
			}
			if (c.teleAction == 11) {
				c.getPA().spellTeleport(3280, 9372, 0);
			}
			if (c.teleAction == 10) {
				c.getPA().spellTeleport(3241, 9364, 0);
			}
			if (c.teleAction == 9) {
				c.getPA().spellTeleport(3159, 9895, 0);
			}
			if (c.teleAction == 8) {
				c.getPA().spellTeleport(2912, 9831, 0);
			}
			if (c.teleAction == 7) {
				c.getPA().spellTeleport(2843, 9555, 0);
			}
			if (c.teleAction == 3) {
				c.getDH().sendOption4("Armadyl", "Bandos", "Zamorak", "Saradomin");
				c.teleAction = 13;
			}
			/*
			 * if (c.teleAction == 1) { //experiments
			 * c.getPA().spellTeleport(3555, 9947, 0); } else if (c.teleAction
			 * == 2) { //brimhavem dung c.getPA().spellTeleport(2709, 9564, 0);
			 * } else if (c.teleAction == 3) { //dag kings
			 * c.getPA().spellTeleport(2479, 10147, 0); } else if (c.teleAction
			 * == 4) { //easts lv 18 c.getPA().spellTeleport(3351, 3659, 0); }
			 * else if (c.teleAction == 5) {
			 * c.getPA().spellTeleport(2813,3436,0); }
			 */
			if (c.teleAction == 1) {
				// slayer tower
				// c.getPA().spellTeleport(2859, 9843, 0);
				c.getPA().spellTeleport(2979, 3597, 0);
			} else if (c.teleAction == 200) {
				// tzhaar
				// c.getPA().spellTeleport(2438, 5168, 0);
				// c.sendMessage("To fight Jad, enter the cave.");
				// c.sendMessage("Climb down the ladder to get into the lair.");
			} else if (c.teleAction == 4) {
				// Hillz
				c.getPA().spellTeleport(2726, 3487, 0);
			} else if (c.teleAction == 5) {
				c.getPA().spellTeleport(2813, 3436, 0);
			} else if (c.teleAction == 2000) {
				c.getPA().spellTeleport(3222, 3219, 0);
			}
			if (c.dialogueAction == 10) {
				c.getPA().spellTeleport(2713, 4836, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 11) {
				c.getPA().spellTeleport(2162, 4833, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 12) {
				c.getPA().spellTeleport(2207, 4836, 0);
				c.dialogueAction = -1;
			}
			break;

		// 4th tele option
		case 9193:
			if (c.dialogueAction == 128) {
				c.getDH().sendDialogues(634, -1);
				return;
			}
			switch(c.teleAction) {
			case 2:
				c.getPA().spellTeleport(2678+Misc.random(3), 9563+Misc.random(2), 0);
				break;
		}
			if (c.teleAction == 14) {
				c.getPA().startTeleport(3179, 3774, 0, "modern");
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 123) {
				DegradableItem[] claimable = Degrade.getClaimedItems(c);
				if (claimable.length < 4) {
					return;
				}
				c.getPA().removeAllWindows();
				Degrade.claim(c, claimable[3].getItemId());
				return;
			}
			if (c.dialogueAction == 121) {
				c.getDH().sendDialogues(618, -1);
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 12) {
				c.getDH().sendOption5("GarGoyle", "Bloodveld", "Banshee", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 11;
				break;
			}
			if (c.teleAction == 11) {
				c.getDH().sendOption5("Black Demon", "Dust Devils", "Nechryael", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 10;
				break;
			}
			if (c.teleAction == 10) {
				c.getDH().sendOption5("Goblins", "Baby blue dragon", "Moss Giants", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 9;
				break;
			}
			if (c.teleAction == 9) {
				c.getDH().sendOption5("Al-kharid warrior", "Ghosts", "Giant Bats", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 8;
				break;
			}
			if (c.teleAction == 8) {
				c.getDH().sendOption5("Hill Giants", "Hellhounds", "Lesser Demons", "Chaos Dwarf", "-- Next Page --");
				c.teleAction = 7;
				break;
			}
			if (c.teleAction == 7) {
				c.getPA().spellTeleport(2923, 9759, 0);
			}
			if (c.teleAction == 1) {
				// brimhaven dungeon
				c.getPA().spellTeleport(3288, 3631, 0);
			} else if (c.teleAction == 200) {
				// duel arena
				c.getPA().spellTeleport(3366, 3266, 0);
			} else if (c.teleAction == 3) {
				c.getPA().spellTeleport(3331, 3706, 0);

			} else if (c.teleAction == 4) {
				// Fala
				/*
				 * c.getPA().removeAllWindows(); c.teleAction = 0;
				 */
				c.getPA().spellTeleport(2815, 3461, 0);
				c.getDH().sendStatement("You need a Rake, Watering can, Seed Dibber and a seed.");
			} else if (c.teleAction == 5) {
				c.getPA().spellTeleport(2724, 3484, 0);
				c.sendMessage("For magic logs, try north of the duel arena.");
			}
			if (c.dialogueAction == 10) {
				c.getPA().spellTeleport(2660, 4839, 0);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 11) {
				// c.getPA().spellTeleport(2527, 4833, 0); astrals here
				// c.getRunecrafting().craftRunes(2489);
				c.dialogueAction = -1;
			} else if (c.dialogueAction == 12) {
				// c.getPA().spellTeleport(2464, 4834, 0); bloods here
				// c.getRunecrafting().craftRunes(2489);
				c.dialogueAction = -1;
			}
			break;
		// 5th tele option
		case 9194:
			if (c.dialogueAction == 128) {
				c.getDH().sendDialogues(636, -1);
				return;
			}
			switch(c.teleAction) {
			case 2:
				c.getDH().sendDialogues(3333, -1);
				return;
			case 14:
				c.getDH().sendDialogues(3325, -1);
				return;
			}
			if (c.dialogueAction == 121 || c.dialogueAction == 123) {
				c.getPA().removeAllWindows();
				c.teleAction = -1;
				c.dialogueAction = -1;
				return;
			}
			if (c.teleAction == 8) {
				c.getDH().sendOption5("Goblins", "Baby blue dragon", "Moss Giants", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 9;
				break;
			}
			if (c.teleAction == 9) {
				c.getDH().sendOption5("Black Demon", "Dust Devils", "Nechryael", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 10;
				break;
			}
			if (c.teleAction == 11) {
				c.getDH().sendOption5("Infernal Mage", "Dark Beasts", "Abyssal Demon", "-- Previous Page --", "");
				c.teleAction = 12;
				break;
			}
			if (c.teleAction == 10) {
				c.getDH().sendOption5("GarGoyle", "Bloodveld", "Banshee", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 11;
				break;
			}
			if (c.teleAction == 7) {
				c.getDH().sendOption5("Al-kharid warrior", "Ghosts", "Giant Bats", "-- Previous Page --", "-- Next Page --");
				c.teleAction = 8;
				break;
			}
			/*
			 * if (c.teleAction == 1) { //island c.getPA().spellTeleport(2895,
			 * 2727, 0); } else if (c.teleAction == 200) { //last minigame spot
			 * c.sendMessage("Suggest something for this spot on the forums!");
			 * c.getPA().closeAllWindows(); } else if (c.teleAction == 3) {
			 * //last monster spot
			 * c.sendMessage("Suggest something for this spot on the forums!");
			 * c.getPA().closeAllWindows(); } else if (c.teleAction == 4) {
			 * //dark castle multi easts c.getPA().spellTeleport(3037, 3652, 0);
			 * } else if (c.teleAction == 5) {
			 * c.getPA().spellTeleport(2812,3463,0); }
			 */
			if (c.teleAction == 1) {
				// traverly
				// c.getPA().spellTeleport(3297, 9824, 0);
				// c.sendMessage("@red@There's just frost dragons, if you want to kill green dragons you must go wilderness.");
				c.getPA().spellTeleport(3073, 3932, 0);
				// c.getPA().removeAllWindows();
				// 2884 9798
			} else if (c.teleAction == 200) {
				// last minigame spot
				// c.sendMessage("Suggest something for this spot on the forums!");
				// c.getPA().closeAllWindows();
				// c.getPA().spellTeleport(2876, 3546, 0);
			} else if (c.teleAction == 3) {
				c.getDH().sendOption5("Dagannoth Kings", "Kraken @red@(Level 17 & Multi)", "Venenatis @red@(Level 29 & Multi)", "Vet'ion @red@(Level 34 & Multi)", "@blu@Previous");
				c.teleAction = 14;
			} else if (c.teleAction == 4) {
				// ardy lever
				/*
				 * c.getPA().removeAllWindows(); c.teleAction = 0;
				 */
				c.getPA().spellTeleport(3039, 4836, 0);
			} else if (c.teleAction == 5) {
				c.getPA().spellTeleport(2812, 3463, 0);
			}
			if (c.dialogueAction == 10 || c.dialogueAction == 11) {
				c.dialogueId++;
				c.getDH().sendDialogues(c.dialogueId, 0);
			} else if (c.dialogueAction == 12) {
				c.dialogueId = 17;
				c.getDH().sendDialogues(c.dialogueId, 0);
			}
			break;

		// case 58253:
		case 108005:
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			c.getPA().showInterface(15106);
			// c.getItems().writeBonus();
			break;
		case 108006: // items kept on death
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			c.getPA().sendFrame126("OS Perfection - Items Kept on Death", 17103);
			c.StartBestItemScan(c);
			c.EquipStatus = 0;
			for (int k = 0; k < 4; k++) {
				c.getPA().sendFrame34a(10494, -1, k, 1);
			}
			for (int k = 0; k < 39; k++) {
				c.getPA().sendFrame34a(10600, -1, k, 1);
			}
			if (c.WillKeepItem1 > 0) {
				c.getPA().sendFrame34a(10494, c.WillKeepItem1, 0, c.WillKeepAmt1);
			}
			if (c.WillKeepItem2 > 0) {
				c.getPA().sendFrame34a(10494, c.WillKeepItem2, 1, c.WillKeepAmt2);
			}
			if (c.WillKeepItem3 > 0) {
				c.getPA().sendFrame34a(10494, c.WillKeepItem3, 2, c.WillKeepAmt3);
			}
			if (c.WillKeepItem4 > 0 && c.prayerActive[10]) {
				c.getPA().sendFrame34a(10494, c.WillKeepItem4, 3, 1);
			}
			for (int ITEM = 0; ITEM < 28; ITEM++) {
				if (c.playerItems[ITEM] - 1 > 0 && !(c.playerItems[ITEM] - 1 == c.WillKeepItem1 && ITEM == c.WillKeepItem1Slot)
						&& !(c.playerItems[ITEM] - 1 == c.WillKeepItem2 && ITEM == c.WillKeepItem2Slot)
						&& !(c.playerItems[ITEM] - 1 == c.WillKeepItem3 && ITEM == c.WillKeepItem3Slot)
						&& !(c.playerItems[ITEM] - 1 == c.WillKeepItem4 && ITEM == c.WillKeepItem4Slot)) {
					c.getPA().sendFrame34a(10600, c.playerItems[ITEM] - 1, c.EquipStatus, c.playerItemsN[ITEM]);
					c.EquipStatus += 1;
				} else if (c.playerItems[ITEM] - 1 > 0 && (c.playerItems[ITEM] - 1 == c.WillKeepItem1 && ITEM == c.WillKeepItem1Slot)
						&& c.playerItemsN[ITEM] > c.WillKeepAmt1) {
					c.getPA().sendFrame34a(10600, c.playerItems[ITEM] - 1, c.EquipStatus, c.playerItemsN[ITEM] - c.WillKeepAmt1);
					c.EquipStatus += 1;
				} else if (c.playerItems[ITEM] - 1 > 0 && (c.playerItems[ITEM] - 1 == c.WillKeepItem2 && ITEM == c.WillKeepItem2Slot)
						&& c.playerItemsN[ITEM] > c.WillKeepAmt2) {
					c.getPA().sendFrame34a(10600, c.playerItems[ITEM] - 1, c.EquipStatus, c.playerItemsN[ITEM] - c.WillKeepAmt2);
					c.EquipStatus += 1;
				} else if (c.playerItems[ITEM] - 1 > 0 && (c.playerItems[ITEM] - 1 == c.WillKeepItem3 && ITEM == c.WillKeepItem3Slot)
						&& c.playerItemsN[ITEM] > c.WillKeepAmt3) {
					c.getPA().sendFrame34a(10600, c.playerItems[ITEM] - 1, c.EquipStatus, c.playerItemsN[ITEM] - c.WillKeepAmt3);
					c.EquipStatus += 1;
				} else if (c.playerItems[ITEM] - 1 > 0 && (c.playerItems[ITEM] - 1 == c.WillKeepItem4 && ITEM == c.WillKeepItem4Slot)
						&& c.playerItemsN[ITEM] > 1) {
					c.getPA().sendFrame34a(10600, c.playerItems[ITEM] - 1, c.EquipStatus, c.playerItemsN[ITEM] - 1);
					c.EquipStatus += 1;
				}
			}
			for (int EQUIP = 0; EQUIP < 14; EQUIP++) {
				if (c.playerEquipment[EQUIP] > 0 && !(c.playerEquipment[EQUIP] == c.WillKeepItem1 && EQUIP + 28 == c.WillKeepItem1Slot)
						&& !(c.playerEquipment[EQUIP] == c.WillKeepItem2 && EQUIP + 28 == c.WillKeepItem2Slot)
						&& !(c.playerEquipment[EQUIP] == c.WillKeepItem3 && EQUIP + 28 == c.WillKeepItem3Slot)
						&& !(c.playerEquipment[EQUIP] == c.WillKeepItem4 && EQUIP + 28 == c.WillKeepItem4Slot)) {
					c.getPA().sendFrame34a(10600, c.playerEquipment[EQUIP], c.EquipStatus, c.playerEquipmentN[EQUIP]);
					c.EquipStatus += 1;
				} else if (c.playerEquipment[EQUIP] > 0 && (c.playerEquipment[EQUIP] == c.WillKeepItem1 && EQUIP + 28 == c.WillKeepItem1Slot)
						&& c.playerEquipmentN[EQUIP] > 1 && c.playerEquipmentN[EQUIP] - c.WillKeepAmt1 > 0) {
					c.getPA().sendFrame34a(10600, c.playerEquipment[EQUIP], c.EquipStatus, c.playerEquipmentN[EQUIP] - c.WillKeepAmt1);
					c.EquipStatus += 1;
				} else if (c.playerEquipment[EQUIP] > 0 && (c.playerEquipment[EQUIP] == c.WillKeepItem2 && EQUIP + 28 == c.WillKeepItem2Slot)
						&& c.playerEquipmentN[EQUIP] > 1 && c.playerEquipmentN[EQUIP] - c.WillKeepAmt2 > 0) {
					c.getPA().sendFrame34a(10600, c.playerEquipment[EQUIP], c.EquipStatus, c.playerEquipmentN[EQUIP] - c.WillKeepAmt2);
					c.EquipStatus += 1;
				} else if (c.playerEquipment[EQUIP] > 0 && (c.playerEquipment[EQUIP] == c.WillKeepItem3 && EQUIP + 28 == c.WillKeepItem3Slot)
						&& c.playerEquipmentN[EQUIP] > 1 && c.playerEquipmentN[EQUIP] - c.WillKeepAmt3 > 0) {
					c.getPA().sendFrame34a(10600, c.playerEquipment[EQUIP], c.EquipStatus, c.playerEquipmentN[EQUIP] - c.WillKeepAmt3);
					c.EquipStatus += 1;
				} else if (c.playerEquipment[EQUIP] > 0 && (c.playerEquipment[EQUIP] == c.WillKeepItem4 && EQUIP + 28 == c.WillKeepItem4Slot)
						&& c.playerEquipmentN[EQUIP] > 1 && c.playerEquipmentN[EQUIP] - 1 > 0) {
					c.getPA().sendFrame34a(10600, c.playerEquipment[EQUIP], c.EquipStatus, c.playerEquipmentN[EQUIP] - 1);
					c.EquipStatus += 1;
				}
			}
			c.ResetKeepItems();
			c.getPA().showInterface(17100);
			break;

		case 59004:
			c.getPA().removeAllWindows();
			break;

		case 9178:
			switch (c.teleAction) {
			case 1:
				c.getPA().spellTeleport(3011, 3632, 0);
				break;
			case 2:
				c.getPA().spellTeleport(2441, 3090, 0);
				break;
		}
			if (c.dialogueAction == 3301) {
				c.getDH().sendDialogues(3302, c.npcType);
			}
			if (c.dialogueAction == 122) {
				c.getDH().sendDialogues(621, 954);
				return;
			}
			if (c.teleAction == 13) {
				c.getPA().spellTeleport(2839, 5296, 2);
				// //c.sendMessage("You must know it's not easy, get a team to own that boss!");
			}
			if (c.teleAction == 3) {
				c.getPA().spellTeleport(2273, 4681, 0);
			}
			if (c.teleAction == 200) {
				// pest
				c.getPA().spellTeleport(2662, 2652, 0);
			}
			if (c.teleAction == 201) {
				// pest
				c.getPA().spellTeleport(3565, 3308, 0);
			}
			if (c.dialogueAction == 2299) {
				c.playerXP[0] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[0] = c.getPA().getLevelForXP(c.playerXP[0]);
				c.getPA().refreshSkill(0);
				c.playerXP[1] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[1] = c.getPA().getLevelForXP(c.playerXP[1]);
				c.getPA().refreshSkill(1);
				c.playerXP[2] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[2] = c.getPA().getLevelForXP(c.playerXP[2]);
				c.getPA().refreshSkill(2);
				c.playerXP[3] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.playerXP[4] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[4] = c.getPA().getLevelForXP(c.playerXP[4]);
				c.getPA().refreshSkill(4);
				c.playerXP[5] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.getPA().refreshSkill(5);
				c.playerXP[6] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[6] = c.getPA().getLevelForXP(c.playerXP[6]);
				c.getPA().refreshSkill(6);
				c.getItems().addItem(7461, 1);
				c.getItems().addItem(10828, 1);
				c.getItems().addItem(1712, 1);
				c.getItems().addItem(1201, 1);
				c.getItems().addItem(1127, 1);
				c.getItems().addItem(4151, 1);
				c.getItems().addItem(5698, 1);
				c.getItems().addItem(2414, 1);
				c.getItems().addItem(11840, 1);
				c.getItems().addItem(2550, 1);
				c.getItems().addItem(1079, 1);
				c.sendMessage("<img=10>An appropriate starter package has been given to you.");
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().removeAllWindows();
			}
			if (c.dialogueAction == 1658) {
				if (!c.getItems().playerHasItem(995, 2230000)) {
					c.sendMessage("You must have 2,230,000 coins to buy this package.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
					break;
				}
				c.dialogueAction = 0;
				c.getItems().addItemToBank(560, 4000);
				c.getItems().addItemToBank(565, 2000);
				c.getItems().addItemToBank(555, 6000);
				c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 2230000);
				c.sendMessage("@red@The runes has been added to your bank.");
				c.getPA().removeAllWindows();
				break;
			} else if (c.dialogueAction == 114) {
				if (c.getItems().playerHasItem(6737, 1) && c.pkp >= 300) {
					c.getItems().deleteItem(6737, 1);
					c.pkp -= 300;
					c.getItems().addItem(11773, 1);
					c.sendMessage("You imbue your berserker ring for the cost of @blu@300 PKP@bla@.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				} else {
					c.sendMessage("You need 300 PKP and a Berserker ring to do this.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				}
			}
			if (c.usingROW) // c.getPA().useCharge();
			{
			c.getPA().startTeleport(Config.MISC_X, Config.MISC_Y, 0, "modern");
			}
			if (c.usingGlory) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "glory");
			}
			if (c.dialogueAction == 2) {
				c.getPA().startTeleport(3428, 3538, 0, "modern");
			}
			if (c.dialogueAction == 3) {
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
			}
			if (c.dialogueAction == 4) {
				c.getPA().startTeleport(3565, 3314, 0, "modern");
			}
			if (c.dialogueAction == 20) {
				c.getPA().startTeleport(2897, 3618, 4, "modern");
				c.killCount = 0;
			} else if (c.teleAction == 200) {
				// barrows
				c.getPA().spellTeleport(3565, 3314, 0);
			}

			if (c.caOption4a) {
				c.getDH().sendDialogues(102, c.npcType);
				c.caOption4a = false;
			}
			if (c.caOption4c) {
				c.getDH().sendDialogues(118, c.npcType);
				c.caOption4c = false;
			}
			break;

		case 9179:
			switch (c.teleAction) {
			case 1:
				c.getPA().spellTeleport(3170, 3886, 0);
				break;
			case 2:
				c.getPA().spellTeleport(2400, 5179, 0);
				break;
		}
			if (c.dialogueAction == 122) {
				c.getDH().sendDialogues(623, 954);
				return;
			}
			if (c.dialogueAction == 3301) {
				c.getDH().sendDialogues(3304, c.npcType);
			}
			if (c.teleAction == 13) {
				c.getPA().spellTeleport(2864, 5354, 2);
				// c.sendMessage("You must know it's not easy, get a team to own that boss!");
			}
			if (c.teleAction == 200) {
				c.sendMessage("@red@Stake only what you can afford to lose!");
				
				c.getPA().spellTeleport(3365, 3266, 0);

			}
			if (c.teleAction == 201) {
				// warr guild
				c.getPA().spellTeleport(2847, 3543, 0);
				c.sendMessage("@blu@Use the animators to gain tokens, then head upstairs to the cyclops.");
			}
			if (c.teleAction == 3) {
				//c.getPA().spellTeleport(3262, 3929, 0);
				c.getZulrahEvent().initialize();
			}
			if (c.dialogueAction == 2299) {
				c.playerXP[0] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[0] = c.getPA().getLevelForXP(c.playerXP[0]);
				c.getPA().refreshSkill(0);
				c.playerXP[1] = c.getPA().getXPForLevel(45) + 5;
				c.playerLevel[1] = c.getPA().getLevelForXP(c.playerXP[1]);
				c.getPA().refreshSkill(1);
				c.playerXP[2] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[2] = c.getPA().getLevelForXP(c.playerXP[2]);
				c.getPA().refreshSkill(2);
				c.playerXP[3] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.playerXP[4] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[4] = c.getPA().getLevelForXP(c.playerXP[4]);
				c.getPA().refreshSkill(4);
				c.playerXP[5] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.getPA().refreshSkill(5);
				c.playerXP[6] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[6] = c.getPA().getLevelForXP(c.playerXP[6]);
				c.getPA().refreshSkill(6);
				c.getItems().addItem(7461, 1);
				c.getItems().addItem(3751, 1);
				c.getItems().addItem(1712, 1);
				c.getItems().addItem(1201, 1);
				c.getItems().addItem(1127, 1);
				c.getItems().addItem(4151, 1);
				c.getItems().addItem(5698, 1);
				c.getItems().addItem(2414, 1);
				c.getItems().addItem(4131, 1);
				c.getItems().addItem(2550, 1);
				c.getItems().addItem(1079, 1);
				c.sendMessage("<img=10>An appropriate starter package has been given to you.");
				c.getPA().removeAllWindows();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.dialogueAction = 0;
			}
			if (c.dialogueAction == 1658) {
				if (!c.getItems().playerHasItem(995, 912000)) {
					c.sendMessage("You must have 912,000 coins to buy this package.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
					break;
				}
				c.dialogueAction = 0;
				c.getItems().addItemToBank(560, 2000);
				c.getItems().addItemToBank(9075, 4000);
				c.getItems().addItemToBank(557, 10000);
				c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 912000);
				c.sendMessage("@red@The runes has been added to your bank.");
				c.getPA().removeAllWindows();
				break;
			} else if (c.dialogueAction == 114) {
				if (c.getItems().playerHasItem(6733, 1) && c.pkp >= 300) {
					c.getItems().deleteItem(6733, 1);
					c.pkp -= 300;
					c.getItems().addItem(11771, 1);
					c.sendMessage("You imbue your archer ring for the cost of @blu@300 PKP@bla@.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				} else {
					c.sendMessage("You need 300 PKP and an Archer ring to do this.");
					c.dialogueAction = 0;
					c.getPA().removeAllWindows();
				}
			}
			if (c.usingROW) // c.getPA().useCharge();
			{
			c.getPA().startTeleport(Config.GE_X, Config.GE_Y, 0, "modern");
			}
			if (c.usingGlory) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.AL_KHARID_X, Config.AL_KHARID_Y, 0, "glory");
			}
			if (c.dialogueAction == 2) {
				c.getPA().startTeleport(2884, 3395, 0, "modern");
			}
			if (c.dialogueAction == 3) {
				c.getPA().startTeleport(3243, 3513, 0, "modern");
			}
			if (c.dialogueAction == 4) {
				c.getPA().startTeleport(2444, 5170, 0, "modern");
			}
			if (c.dialogueAction == 20) {
				c.getPA().startTeleport(2897, 3618, 12, "modern");
				c.killCount = 0;
			} else if (c.teleAction == 200) {
				// assault
				c.getPA().spellTeleport(2605, 3153, 0);
			}
			if (c.caOption4c) {
				c.getDH().sendDialogues(120, c.npcType);
				c.caOption4c = false;
			}
			if (c.caPlayerTalk1) {
				c.getDH().sendDialogues(125, c.npcType);
				c.caPlayerTalk1 = false;
			}
			break;

		case 9180:
			if (c.dialogueAction == 129) {
				SerializablePair<String, Long> pair = Server.getServerData().getZulrahTime();
				if (pair == null || pair.getFirst() == null || pair.getSecond() == null) {
					c.getDH().sendDialogues(643, 2040);
				} else {
					c.getDH().sendDialogues(644, 2040);
				}
				return;
			}
			switch (c.teleAction) {
			case 1:
				c.getPA().spellTeleport(3289, 3639, 0);
				break;
			case 2:
				c.getPA().spellTeleport(2846, 3541, 0);
				break;
			/*case 2:
			c.getPA().spellTeleport(2667, 3424, 0);
			break;*/
		}
			if (c.dialogueAction == 122) {
				c.getDH().sendDialogues(624, 954);
				return;
			}
			if (c.dialogueAction == 3301) {
				c.getDH().sendDialogues(3310, c.npcType);
			}
			if (c.teleAction == 13) {
			//	c.getPA().spellTeleport(2925, 5331, 2);
				c.getDH().sendStatement("We are sorry, but the Zamorak boss is under","re-construction due to animation issues. He will", "be back shortly!");
				// c.sendMessage("You must know it's not easy, get a team to own that boss!");
			}
			if (c.teleAction == 200) {
				c.getPA().spellTeleport(2439, 5169, 0);
				c.sendMessage("Use the cave entrance to start.");
			}
			if (c.dialogueAction == 2299) {
				c.playerXP[0] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[0] = c.getPA().getLevelForXP(c.playerXP[0]);
				c.getPA().refreshSkill(0);
				c.playerXP[1] = c.getPA().getXPForLevel(1) + 5;
				c.playerLevel[1] = c.getPA().getLevelForXP(c.playerXP[1]);
				c.getPA().refreshSkill(1);
				c.playerXP[2] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[2] = c.getPA().getLevelForXP(c.playerXP[2]);
				c.getPA().refreshSkill(2);
				c.playerXP[3] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.playerXP[4] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[4] = c.getPA().getLevelForXP(c.playerXP[4]);
				c.getPA().refreshSkill(4);
				c.playerXP[5] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.getPA().refreshSkill(5);
				c.playerXP[6] = c.getPA().getXPForLevel(99) + 5;
				c.playerLevel[6] = c.getPA().getLevelForXP(c.playerXP[6]);
				c.getPA().refreshSkill(6);
				c.getItems().addItem(3105, 1);
				c.getItems().addItem(1712, 1);
				c.getItems().addItem(4151, 1);
				c.getItems().addItem(5698, 1);
				c.getItems().addItem(6107, 1);
				c.getItems().addItem(6108, 1);
				c.getItems().addItem(4502, 1);
				c.getItems().addItem(6568, 1);
				c.getItems().addItem(3842, 1);
				c.getItems().addItem(2497, 1);
				c.getItems().addItem(7458, 1);
				c.getItems().addItem(2550, 1);
				c.sendMessage("<img=10>An appropriate starter package has been given to you.");
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.dialogueAction == 1658) {
				if (!c.getItems().playerHasItem(995, 1788000)) {
					c.sendMessage("You must have 1,788,000 coins to buy this package.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
					break;
				}
				c.dialogueAction = 0;
				c.getItems().addItemToBank(556, 1000);
				c.getItems().addItemToBank(554, 1000);
				c.getItems().addItemToBank(558, 1000);
				c.getItems().addItemToBank(557, 1000);
				c.getItems().addItemToBank(555, 1000);
				c.getItems().addItemToBank(560, 1000);
				c.getItems().addItemToBank(565, 1000);
				c.getItems().addItemToBank(566, 1000);
				c.getItems().addItemToBank(9075, 1000);
				c.getItems().addItemToBank(562, 1000);
				c.getItems().addItemToBank(561, 1000);
				c.getItems().addItemToBank(563, 1000);
				c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 1788000);
				c.sendMessage("@red@The runes has been added to your bank.");
				c.getPA().removeAllWindows();
				break;
			} else if (c.dialogueAction == 114) {
				if (c.getItems().playerHasItem(6731, 1) && c.pkp >= 300) {
					c.getItems().deleteItem(500, 1);
					c.pkp -= 300;
					c.getItems().addItem(11770, 1);
					c.sendMessage("You imbue your seers ring for the cost of @blu@300 PKP@bla@.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				} else {
					c.sendMessage("You need 300 PKP and n Seers ring to do this.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				}
			}
			if (c.usingROW) // c.getPA().useCharge();
			{
			c.getPA().startTeleport(Config.FALADOR_PARK_X, Config.FALADOR_PARK_Y, 0, "modern");
			}
			if (c.usingGlory) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.KARAMJA_X, Config.KARAMJA_Y, 0, "glory");
			}
			if (c.dialogueAction == 2) {
				c.getPA().startTeleport(2471, 10137, 0, "modern");
			}
			if (c.dialogueAction == 3) {
				c.getPA().startTeleport(3363, 3676, 0, "modern");
			}
			if (c.dialogueAction == 4) {
				c.getPA().startTeleport(2659, 2676, 0, "modern");
			}
			if (c.dialogueAction == 20) {
				c.getPA().startTeleport(2897, 3618, 8, "modern");
				c.killCount = 0;
			} else if (c.teleAction == 200) {
				// duel arena
				c.getPA().spellTeleport(3366, 3266, 0);
			}
			if (c.caOption4c) {
				c.getDH().sendDialogues(122, c.npcType);
				c.caOption4c = false;
			}
			if (c.caPlayerTalk1) {
				c.getDH().sendDialogues(127, c.npcType);
				c.caPlayerTalk1 = false;
			}
			break;

		case 9181:
			switch (c.teleAction) {
			case 1:
				c.getPA().spellTeleport(3153, 3923, 0);
				break;
			case 2:
				c.getDH().sendDialogues(3325, -1);
				return;
			}
			if (c.dialogueAction == 3301 || c.dialogueAction == 122) {
				c.getPA().closeAllWindows();
			}
			if (c.teleAction == 201 || c.dialogueAction == 129) {
				// pest
				c.getPA().removeAllWindows();
			}
			if (c.teleAction == 13) {
				c.getPA().spellTeleport(2907, 5265, 0);
				// c.sendMessage("You must know it's not easy, get a team to own that boss!");
			}
			if (c.teleAction == 3) {
				c.getPA().spellTeleport(3331, 3706, 0);
			}
			if (c.teleAction == 200) {
				c.getDH().sendDialogues(2002, -1);
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 2299) {
				c.sendMessage("<img=10>You can set your stats by clicking them in the stats tab.");
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.dialogueAction == 1658) {
				c.getShops().openShop(5);
				c.dialogueAction = 0;
			} else if (c.dialogueAction == 114) {
				if (c.getItems().playerHasItem(6735, 1) && c.pkp >= 500) {
					c.getItems().deleteItem(6735, 1);
					c.pkp -= 500;
					c.getItems().addItem(11772, 1);
					c.sendMessage("You imbue your warrior ring for the cost of @blu@500 PKP@bla@.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				} else {
					c.sendMessage("You need 500 PKP and n Warrior ring to do this.");
					c.getPA().removeAllWindows();
					c.dialogueAction = 0;
				}
			}
			if (c.usingROW) // c.getPA().useCharge();
			{
			c.getPA().startTeleport(Config.ZULANDRA_X, Config.ZULANDRA_Y, 0, "modern");//zul-andra
			}
			if (c.usingGlory) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.MAGEBANK_X, Config.MAGEBANK_Y, 0, "glory");
			}
			if (c.dialogueAction == 2) {
				c.getPA().startTeleport(2669, 3714, 0, "modern");
			}
			if (c.dialogueAction == 3) {
				c.getPA().startTeleport(2540, 4716, 0, "modern");
			}
			if (c.dialogueAction == 4) {
				c.getPA().startTeleport(3366, 3266, 0, "modern");

			} else if (c.teleAction == 200) {
				// tzhaar
				c.getPA().spellTeleport(2444, 5170, 0);
			}
			if (c.dialogueAction == 20) {
				// c.getPA().startTeleport(3366, 3266, 0, "modern");
				// c.killCount = 0;
				c.sendMessage("This will be added shortly");
			}
			if (c.caOption4c) {
				c.getDH().sendDialogues(124, c.npcType);
				c.caOption4c = false;
			}
			if (c.caPlayerTalk1) {
				c.getDH().sendDialogues(130, c.npcType);
				c.caPlayerTalk1 = false;
			}
			break;
		case 26010:
			c.getPA().resetAutocast();
			break;
		case 1093:
		case 1094:
		case 1097:
			if (c.autocastId > 0) {
				c.getPA().resetAutocast();
			} else {
				if (c.playerMagicBook == 1) {
					if (c.playerEquipment[c.playerWeapon] == 4675 || c.playerEquipment[c.playerWeapon] == 6914) {
						c.setSidebarInterface(0, 1689);
					} else {
						c.sendMessage("You can't autocast ancients without a proper staff.");
					}
				} else if (c.playerMagicBook == 0) {
					if (c.playerEquipment[c.playerWeapon] == 4170) {
						c.setSidebarInterface(0, 12050);
					} else {
						c.setSidebarInterface(0, 1829);
					}
				}
			}
			break;

		case 9157:
			if (c.dialogueAction == 130) {
				if (c.getLostItems().size() > 0) {
					c.getLostItems().retain();
				}
				return;
			}
			if (c.dialogueAction == 127) {
				if (c.absX == 3184 && c.absY == 3945) {
					if (c.getItems().playerHasItem(995, 50_000)) {
						c.getPA().movePlayer(3184, 3944, 0);
						c.getItems().deleteItem2(995, 50_000);
						c.getPA().removeAllWindows();
					} else {
						c.getDH().sendStatement("You need at least 50,000 gp to enter this area.");
					}
				}
				c.dialogueAction = -1;
				c.nextChat = -1;
				c.teleAction = -1;
				return;
			}
			if (c.dialogueAction == 947) {
				c.getShops().openShop(113);
				c.dialogueAction = -1;
			}
			if (c.dialogueAction == 126) {
				c.getPA().startTeleport(3039, 4835, 0, "modern");
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			}
			if (c.dialogueAction == 125) {
				if(c.getItems().playerHasItem(8851, 200)) {
					c.getPA().movePlayer(2847, 3540, 2);
					c.getPA().removeAllWindows();
					c.getWarriorsGuild().cycle();
				} else {
					c.getDH().sendNpcChat2("You need atleast 200 warrior guild tokens.", "You can get some by operating the armour animator.", 4289, "Kamfreena");
					c.nextChat = 0;
				}
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			}
			if (c.dialogueAction == 120) {
				if (c.getItemOnPlayer() == null) {
					return;
				}
				if (!c.getItems().playerHasItem(962)) {
					return;
				}
				if (c.getItemOnPlayer().getItems().freeSlots() < 1) {
					c.sendMessage("The other player must have at least 1 free slot.");
					return;
				}
				int[] partyHats = {1038, 1040, 1042, 1044, 1046, 1048};
				int hat = partyHats[Misc.random(partyHats.length - 1)];
				Player winner = Misc.random(1) == 0 ? c : c.getItemOnPlayer();
				Player loser = winner == c ? c.getItemOnPlayer() : c;
				if (Objects.equals(winner, loser)) {
					return;
				}
				c.getPA().closeAllWindows();
				loser.turnPlayerTo(winner.getX(), winner.getY());
				winner.turnPlayerTo(loser.getX(), loser.getY());
				winner.startAnimation(881);
				loser.startAnimation(881);
				c.getItems().deleteItem(962, 1);
				winner.getItems().addItem(hat, 1);
				loser.getItems().addItem(2996, 200);
				winner.sendMessage("You have received a "+ItemAssistant.getItemName(hat)+" from the christmas cracker.");
				loser.sendMessage("Awee you didn't get the partyhat. You received 200 pk tickets as consolation.");
			}
			if (c.dialogueAction == -1 && c.getCurrentCombination().isPresent()) {
				ItemCombination combination = c.getCurrentCombination().get();
				if (combination.isCombinable(c)) {
					combination.combine(c);
				} else {
					c.getDH().sendStatement("You don't have all the items you need for this combination.");
					c.nextChat = -1;
					c.setCurrentCombination(Optional.empty());
				}
				return;
			}
			if(c.dialogueAction == 3308) {
				c.needsNewTask = true;
				c.getSlayer().generateTask();
			}
			if (c.dialogueAction == 100) {
				c.getPoints().giveReward();
			}
			if (c.dialogueAction == 115) {
				if (c.getItems().playerHasItem(12526) && c.getItems().playerHasItem(6585)) {
					c.getItems().deleteItem2(12526, 1);
					c.getItems().deleteItem2(6585, 1);
					c.getItems().addItem(12436, 1);
					c.getDH().sendDialogues(582, -1);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 114) {
				c.getDH().sendDialogues(579, -1);
				return;
			}
			if (c.dialogueAction == 110) {
				if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12757)) {
					c.getItems().deleteItem2(11235, 1);
					c.getItems().deleteItem2(12757, 1);
					c.getItems().addItem(12766, 1);
					c.getDH().sendDialogues(568, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 111) {
				if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12759)) {
					c.getItems().deleteItem2(11235, 1);
					c.getItems().deleteItem2(12759, 1);
					c.getItems().addItem(12765, 1);
					c.getDH().sendDialogues(571, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 112) {
				if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12761)) {
					c.getItems().deleteItem2(11235, 1);
					c.getItems().deleteItem2(12761, 1);
					c.getItems().addItem(12767, 1);
					c.getDH().sendDialogues(574, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 113) {
				if (c.getItems().playerHasItem(11235) && c.getItems().playerHasItem(12763)) {
					c.getItems().deleteItem2(11235, 1);
					c.getItems().deleteItem2(12763, 1);
					c.getItems().addItem(12768, 1);
					c.getDH().sendDialogues(577, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 109) {
				if (c.getItems().playerHasItem(4153) && c.getItems().playerHasItem(12849)) {
					c.getItems().deleteItem2(4153, 1);
					c.getItems().deleteItem2(12849, 1);
					c.getItems().addItem(12848, 1);
					c.getDH().sendDialogues(565, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 108) {
				if (c.getItems().playerHasItem(11924) && c.getItems().playerHasItem(12802)) {
					c.getItems().deleteItem2(11924, 1);
					c.getItems().deleteItem2(12802, 1);
					c.getItems().addItem(12806, 1);
					c.getDH().sendDialogues(560, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 107) {
				if (c.getItems().playerHasItem(11926) && c.getItems().playerHasItem(12802)) {
					c.getItems().deleteItem2(11926, 1);
					c.getItems().deleteItem2(12802, 1);
					c.getItems().addItem(12807, 1);
					c.getDH().sendDialogues(560, 315);
				} else {
					c.getPA().removeAllWindows();
				}
			}
			if (c.dialogueAction == 106) {
				int worth = c.getBH().getNetworthForEmblems();
				long total = (long) worth + c.getBH().getBounties();
				if (total > Integer.MAX_VALUE) {
					c.sendMessage("You have to spend some bounties before obtaining any more.");
					c.getPA().removeAllWindows();
					c.nextChat = -1;
					return;
				}
				if (worth > 0) {
					BountyHunterEmblem.EMBLEMS.forEach(emblem -> c.getItems().deleteItem2(emblem.getItemId(),
							c.getItems().getItemAmount(emblem.getItemId())));
					c.getBH().setBounties(c.getBH().getBounties() + worth);
					c.sendMessage("You sold all of the emblems in your inventory for "+Misc.insertCommas(Integer.toString(worth)) +" bounties.");
					c.getDH().sendDialogues(557, 315);
				} else {
					c.nextChat = -1;
					c.getPA().closeAllWindows();
				}
				return;
			}
			if (c.dialogueAction == 105) {
				if (c.getItems().playerHasItem(12804) && c.getItems().playerHasItem(11838)) {
					c.getItems().deleteItem2(12804, 1);
					c.getItems().deleteItem2(11838, 1);
					c.getItems().addItem(12809, 1);
					c.getDH().sendDialogues(552, -1);
				} else {
					c.getPA().removeAllWindows();
				}
				c.dialogueAction = -1;
				c.nextChat = -1;
				return;
			}
			if (c.dialogueAction == 104) {
				c.getDH().sendDialogues(549, 315);
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 101) {
				c.getDH().sendDialogues(546, 315);
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 102) {
				c.getDH().sendDialogues(547, 315);
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 200) {
				c.getPA().exchangeItems(PointExchange.PK_POINTS, 2996, 1);
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			} else if (c.dialogueAction == 201) {
				c.getDH().sendDialogues(503, -1);
				return;
			} else if (c.dialogueAction == 202) {
				c.getPA().exchangeItems(PointExchange.VOTE_POINTS, 1464, 1);
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			}
			if (c.dialogueAction == 2258) {
				c.getPA().startTeleport(3039, 4834, 0, "modern"); // first click
				// teleports
				// second
				// click
				// open
				// shops
			}
			if (c.dialogueAction == 12000) {
				for (int i = 8144; i < 8195; i++) {
					c.getPA().sendFrame126("", i);
				}
				c.getPA().sendFrame126("@dre@Account Information for @blu@" + c.playerName + "", 8144);
				c.getPA().sendFrame126("", 8145);
				c.getPA().sendFrame126("@blu@Donator Points@bla@ - " + c.donatorPoints + "", 8150);
				c.getPA().sendFrame126("@blu@Vote Points@bla@ - " + c.votePoints + "", 8149);
				c.getPA().sendFrame126("@blu@Amount Donated@bla@ - " + c.amDonated + "", 8151);
				c.getPA().sendFrame126("@blu@PC Points@bla@ - " + c.pcPoints + "", 8152);
				c.getPA().sendFrame126("@blu@Time Played: @bla@" + c.pTime / 2 / 60 + " mins.", 8153);
				c.getPA().showInterface(8134);
			}
			if (c.dialogueAction == 4000) {
				if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (c.getItems().playerHasItem(2697, 1)) {
					if (c.getRights().getValue() >= 1) {
						c.getDH().sendStatement("You cannot read this scroll as you are already a contributor or higher.");
						return;
					}
					c.getItems().deleteItem(2697, 1);
					c.gfx100(263);
					c.setRights(Rights.CONTRIBUTOR);
					c.sendMessage("You are now a contributor. You must relog for changes to take effect!");
					c.getPA().closeAllWindows();
				}
			}
			if (c.dialogueAction == 4001) {
				if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (c.getItems().playerHasItem(2698, 1)) {
					if (c.getRights().getValue() > 5) {
						c.getDH().sendStatement("You cannot read this scroll as you are already a Sponsor or higher.");
						return;
					}
					c.getItems().deleteItem(2698, 1);
					c.gfx100(263);
					c.setRights(Rights.SPONSOR);
					c.sendMessage("You are now a Sponsor. You must relog for changes to take effect!");
					c.getPA().closeAllWindows();
				}
			}
			if (c.dialogueAction == 4002) {
				if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (c.getItems().playerHasItem(2699, 1)) {
					if (c.getRights().getValue() >= 1 && c.getRights().getValue() < 5 || c.getRights().getValue() == 7) {
						c.getDH().sendStatement("You cannot read this scroll as you are already a Supporter.");
						return;
					}
					c.getItems().deleteItem(2699, 1);
					c.gfx100(263);
					c.setRights(Rights.SUPPORTER);
					c.sendMessage("You are now a Supporter. You must relog for changes to take effect!");
					c.getPA().closeAllWindows();
				}
			}
			if (c.dialogueAction == 4003) {
				if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (c.getItems().playerHasItem(2700, 1)) {
					if (c.getRights().getValue() >= 1 && c.getRights().getValue() < 5 || c.getRights().getValue() == 8 || c.getRights().getValue() == 9 ) {
						c.getDH().sendStatement("You cannot read this scroll as you are already a VIP or higher.");
						return;
					}
					c.getItems().deleteItem(2700, 1);
					c.gfx100(263);
					c.setRights(Rights.V_I_P);
					c.sendMessage("You are now a VIP. You must relog for changes to take effect!");
					c.getPA().closeAllWindows();
				}
			}
			if (c.dialogueAction == 4004) {
				if (c.inWild() || c.inCamWild() || c.inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (c.getItems().playerHasItem(2701, 1)) {
					c.getItems().deleteItem(2701, 1);
					c.gfx100(263);
					c.playerTitle = "Gambler";
					c.getItems().addItemUnderAnyCircumstance(15098, 1);
					c.sendMessage("You are now a Gambler. A dice has been added to your bank!");
					c.getPA().closeAllWindows();
				}
			}
			if (c.dialogueAction == 206) {
				c.getItems().resetItems(3214);
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 2109) {
				if (c.absX >= 2438 && c.absX <= 2439 && c.absY >= 5168 && c.absY <= 5169) {
					c.getFightCave().create(1);
				}
			}
			if (c.dialogueAction == 113239) {
				if (c.inDuelArena()) {
					return;
				}
				c.getItems().addItem(557, 1000);
				c.getItems().addItem(560, 1000);
				c.getItems().addItem(9075, 1000);
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.newPlayerAct == 1) {
				// c.isNewPlayer = false;
				c.newPlayerAct = 0;
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
				c.getPA().removeAllWindows();
			}
			if (c.dialogueAction == 6) {
				c.sendMessage("Slayer will be enabled in some minutes.");
				// c.getSlayer().generateTask();
				// c.getPA().sendFrame126("@whi@Task: @gre@"+Server.npcHandler.getNpcListName(c.slayerTask)+
				// " ", 7383);
				// c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 29) {
				if (c.isInBarrows() || c.isInBarrows2()) {
					c.getBarrows().checkCoffins();
					c.getPA().removeAllWindows();
					return;
				} else {
					c.getPA().removeAllWindows();
					c.sendMessage("@blu@You can only do this while you're at barrows, fool.");
				}
			} else if (c.dialogueAction == 27) {
				c.getBarrows().cantWalk = false;
				c.getPA().removeAllWindows();
				// c.getBarrowsChallenge().start();
				return;
			} else if (c.dialogueAction == 25) {
				c.getDH().sendDialogues(26, 0);
				return;
			}
			if (c.dialogueAction == 162) {
				c.sendMessage("You successfully emptied your inventory.");
				c.getPA().removeAllItems();
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 508) {
				c.getDH().sendDialogues(1030, 925);
				return;
			}
			if (c.doricOption2) {
				c.getDH().sendDialogues(310, 284);
				c.doricOption2 = false;
			}
			if (c.rfdOption) {
				c.getDH().sendDialogues(26, -1);
				c.rfdOption = false;
			}
			if (c.horrorOption) {
				c.getDH().sendDialogues(35, -1);
				c.horrorOption = false;
			}
			if (c.dtOption) {
				c.getDH().sendDialogues(44, -1);
				c.dtOption = false;
			}
			if (c.dtOption2) {
				if (c.lastDtKill == 0) {
					c.getDH().sendDialogues(65, -1);
				} else {
					c.getDH().sendDialogues(49, -1);
				}
				c.dtOption2 = false;
			}

			if (c.caOption2) {
				c.getDH().sendDialogues(106, c.npcType);
				c.caOption2 = false;
			}
			if (c.caOption2a) {
				c.getDH().sendDialogues(102, c.npcType);
				c.caOption2a = false;
			}

			if (c.dialogueAction == 1) {
				c.getDH().sendDialogues(38, -1);
			}
			break;

		case 9167:
			if (c.dialogueAction == 129) {
				if (c.getLostItems().size() > 0) {
					c.getDH().sendDialogues(642, 2040);
					c.nextChat = -1;
				} else {
					c.getZulrahEvent().initialize();
				}
				return;
			}
			switch(c.teleAction){
			case 2:
				c.getPA().spellTeleport(2804, 10001, 0);
				break;
			}
			
			if (c.usingDueling) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.DUEL_ARENA_X, Config.DUEL_ARENA_Y, 0, "modern");
			}
			if (c.dialogueAction == 12200) {
				c.getShops().openShop(12);
				return;
			}
			if (c.dialogueAction == 100) {
				c.getShops().openShop(80);
				return;
			}
			if (c.dialogueAction == 14400) {
				c.getPA().startTeleport(2474, 3438, 0, "modern");
				c.sendMessage("You will gain XP after each lap");
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 2245) {
				c.getPA().startTeleport(2110, 3915, 0, "modern");
				c.sendMessage("High Priest teleported you to @red@Lunar Island@bla@.");
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 508) {
				c.getDH().sendDialogues(1030, 925);
				return;
			}
			if (c.dialogueAction == 502) {
				c.getDH().sendDialogues(1030, 925);
				return;
			}
			if (c.dialogueAction == 251) {
				c.getPA().openUpBank();
			}
			if (c.teleAction == 200) {
				c.getPA().spellTeleport(2662, 2652, 0);
			}
			if (c.doricOption) {
				c.getDH().sendDialogues(306, 284);
				c.doricOption = false;
			}
			break;
		case 9168:
			if (c.dialogueAction == 129) {
				SerializablePair<String, Long> pair = Server.getServerData().getZulrahTime();
				if (pair == null || pair.getFirst() == null || pair.getSecond() == null) {
					c.getDH().sendDialogues(643, 2040);
				} else {
					c.getDH().sendDialogues(644, 2040);
				}
				return;
			}
			switch(c.teleAction){
			case 2:
				c.getPA().spellTeleport(1748, 5326, 0);
				c.teleAction = -1;
				break;
			}
			if (c.usingDueling) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.CASTLE_WARS_X, Config.CASTLE_WARS_Y, 0, "modern");
			}
			if (c.dialogueAction == 12200) {
				c.getShops().openShop(49);
				return;
			}
			if (c.dialogueAction == 100) {
				c.getDH().sendDialogues(545, 315);
				return;
			}
			if (c.dialogueAction == 14400) {
				c.getPA().startTeleport(3004, 3935, 0, "modern");
				c.sendMessage("You will gain XP after each lap. Use the portal at the gate to get home.");
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 2245) {
				c.getPA().startTeleport(3230, 2915, 0, "modern");
				c.sendMessage("High Priest teleported you to @red@Desert Pyramid@bla@.");
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 508) {
				c.getDH().sendDialogues(1027, 925);
				return;
			}
			if (c.dialogueAction == 502) {
				c.getDH().sendDialogues(1027, 925);
				return;
			}
			if (c.teleAction == 200) {
				c.getPA().spellTeleport(3365, 3266, 0);

			}
			if (c.doricOption) {
				c.getDH().sendDialogues(303, 284);
				c.doricOption = false;
			}

			break;
		case 9169:
			if (c.dialogueAction == 129) {
				if (c.getLostItems().size() == 0) {
					c.getDH().sendDialogues(639, 2040);
				} else {
					c.getDH().sendDialogues(640, 2040);
				}
				return;
			}
			switch(c.teleAction){
			case 2:
				c.getDH().sendDialogues(3324, -1);
				return;
		}
			if (c.usingDueling) // c.getPA().useCharge();
			{
				c.getPA().startTeleport(Config.CLAN_WARS_X, Config.CLAN_WARS_Y, 0, "modern");
			}
			if (c.dialogueAction == 14400 || c.dialogueAction == 100) {
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 12200) {
				c.getShops().openShop(50);
				return;
			}
			if (c.dialogueAction == 2245) {
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 508) {
				c.nextChat = 0;
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 502) {
				c.nextChat = 0;
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 251) {
				c.getDH().sendDialogues(1015, 394);
			}
			if (c.teleAction == 200) {
				c.getPA().spellTeleport(2439, 5169, 0);
				c.sendMessage("Use the cave entrance to start.");
			}
			if (c.doricOption) {
				c.getDH().sendDialogues(299, 284);
			}
			break;

		case 9158:
			if (c.dialogueAction == 149) {
				c.getShops().openShop(9);
				c.dialogueAction = -1;
				return;
			}
			if (c.dialogueAction == 126 || c.dialogueAction == 130) {
				c.getPA().removeAllWindows();
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			}
			if (c.dialogueAction == 947) {
				c.getShops().openShop(111);
				c.dialogueAction = -1;
			}
			if (c.dialogueAction == -1 && c.getCurrentCombination().isPresent()) {
				c.setCurrentCombination(Optional.empty());
				c.getPA().removeAllWindows();
				return;
			}
			if (c.dialogueAction == 3308) {
				c.getPA().removeAllWindows();
			}
			if (c.dialogueAction == 100 || c.dialogueAction == 120) {
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 200 || c.dialogueAction == 202
					|| c.dialogueAction >= 101 && c.dialogueAction <= 103 || c.dialogueAction == 106
					|| c.dialogueAction >= 109 && c.dialogueAction <= 114) {
				c.getPA().removeAllWindows();
				c.dialogueAction = -1;
				c.teleAction = -1;
				return;
			} else if (c.dialogueAction == 201) {
				c.getDH().sendDialogues(501, -1);
				return;
			}
			if (c.dialogueAction == 162) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 12001) {
				c.getPA().closeAllWindows();
			}
			if (c.dialogueAction == 12000) {
				for (int i = 8144; i < 8195; i++) {
					c.getPA().sendFrame126("", i);
				}
				int[] frames = {
						8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162,
						8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175
				};
				c.getPA().sendFrame126("@dre@Kill Tracker for @blu@" + c.playerName + "", 8144);
				c.getPA().sendFrame126("", 8145);
				c.getPA().sendFrame126("@blu@Total kills@bla@ - " + c.getNpcDeathTracker().getTotal() + "", 8147);
				c.getPA().sendFrame126("", 8148);
				int index = 0;
				for (Entry<NPCName, Integer> entry : c.getNpcDeathTracker().getTracker().entrySet()) {
					if (entry == null) {
						continue;
					}
					if (index > frames.length - 1) {
						break;
					}
					if (entry.getValue() > 0) {
						c.getPA().sendFrame126("@blu@"+WordUtils.capitalize(entry.getKey().name().toLowerCase())
								+": @red@"+entry.getValue(), frames[index]);
						index++;
					}
				}
				c.getPA().showInterface(8134);
			}
			if (c.dialogueAction == 109) {
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.dialogueAction == 113239) {
				if (c.inDuelArena()) {
					return;
				}
				c.getItems().addItem(555, 1000);
				c.getItems().addItem(560, 1000);
				c.getItems().addItem(565, 1000);
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.dialogueAction == 2301) {
				c.getPA().removeAllWindows();
				c.dialogueAction = 0;
			}
			if (c.newPlayerAct == 1) {
				c.newPlayerAct = 0;
				c.sendMessage("@red@There is nothing to do in Crandor, i must teleport home and start playing ab.");
				c.getPA().removeAllWindows();
			}
			if (c.doricOption2) {
				c.getDH().sendDialogues(309, 284);
				c.doricOption2 = false;
			}
			/*
			 * if (c.dialogueAction == 8) { c.getPA().fixAllBarrows(); } else {
			 * c.dialogueAction = 0; c.getPA().removeAllWindows(); }
			 */
			if (c.dialogueAction == 27) {
				c.getPA().removeAllWindows();
			}
			if (c.caOption2a) {
				c.getDH().sendDialogues(136, c.npcType);
				c.caOption2a = false;
			}
			break;
		/* VENG */
		case 118098:
			c.getPA().castVeng();
			break;
		/**
		 * Specials *
		 */
		case 29188:
			c.specBarId = 7636; // the special attack text - sendframe126(S P E
			// C I A L A T T A C K, c.specBarId);
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29163:
			c.specBarId = 7611;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 33033:
			c.specBarId = 8505;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29038:
			if (c.playerEquipment[c.playerWeapon] == 4153 || c.playerEquipment[c.playerWeapon] == 12848) {
				c.specBarId = 7486;
				c.getCombat().handleGmaulPlayer();
				c.getItems().updateSpecialBar();
			} else {
				c.specBarId = 7486;
				c.usingSpecial = !c.usingSpecial;
				c.getItems().updateSpecialBar();
			}
			break;

		/*
		 * case 29038: c.specBarId = 7486; if (c.specAmount >= 5.0D &&
		 * c.playerEquipment[c.playerWeapon] == 4153 && c.getAttacking() != null
		 * && Region.canAttack(c, c.getAttacking()) && c.getCombatExecutor()
		 * .canAttack(c.getAttacking()) &&
		 * c.getCombatExecutor().getDistanceRequired() <= Misc
		 * .distanceBetween(c, c.getAttacking())) { c.startAnimation(1667);
		 * c.gfx100(340); c.specAccuracy = 1.25D; if (Misc.random(c.meleeAtk())
		 * > Misc .random(c.getAttacking().meleeDef())) { new Hit(c,
		 * c.getAttacking(), Misc.random(CombatFormulas .maxMeleeHit(c)), 1); }
		 * else { new Hit(c, c.getAttacking(), 0, 1); }
		 * 
		 * if (c.getAttacking().isPlayer()) {
		 * c.getCombatExecutor().handleSkulling(); }
		 * 
		 * c.specAmount -= 5.0D;
		 * c.getCombatExecutor().putInCombat(c.getAttacking()); }
		 * 
		 * if (c.playerEquipment[c.playerWeapon] != 4153) { c.usingSpecial =
		 * !c.usingSpecial; }
		 * 
		 * c.specAccuracy = 1.0D; c.getItems().addSpecialBar(
		 * c.playerEquipment[c.playerWeapon]); break;
		 */
			
		case 30108:
			c.specBarId = 7812;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 48023:
			c.specBarId = 12335;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29138:
			c.specBarId = 7586;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29113:
			c.specBarId = 7561;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29238:
			c.specBarId = 7686;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		/**
		 * Dueling *
		 */
		/*case 26065: // no forfeit
		case 26040:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(0);
			break;*/
			
		case 26065:
		case 26040:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.FORFEIT);
			break;

		case 26066: // no movement
		case 26048:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			if (!duelSession.getRules().contains(Rule.FORFEIT)) {
				duelSession.toggleRule(c, Rule.FORFEIT);
			}
			duelSession.toggleRule(c, Rule.NO_MOVEMENT);
			break;

		case 26069: // no range
		case 26042:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_RANGE);
			break;

		case 26070: // no melee
		case 26043:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_MELEE);
			break;

		case 26071: // no mage
		case 26041:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_MAGE);
			break;

		case 26072: // no drinks
		case 26045:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_DRINKS);
			break;

		case 26073: // no food
		case 26046:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_FOOD);
			break;

		case 26074: // no prayer
		case 26047:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_PRAYER);
			break;

		case 26076: // obsticals
		case 26075:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.OBSTACLES);
			break;

		case 2158: // fun weapons
		case 2157:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			if (duelSession.getRules().contains(Rule.WHIP_AND_DDS)) {
				duelSession.toggleRule(c, Rule.WHIP_AND_DDS);
				return;
			}
			if (!Rule.WHIP_AND_DDS.getReq().get().meets(c)) {
				c.getPA().sendString("You must have a whip and dragon dagger to select this.", 6684);
				return;
			}
			if (!Rule.WHIP_AND_DDS.getReq().get().meets(duelSession.getOther(c))) {
				c.getPA().sendString("Your opponent does not have a whip and dragon dagger.", 6684);
				return;
			}
			if (duelSession.getStage().getStage() != MultiplayerSessionStage.OFFER_ITEMS) {
				c.sendMessage("You cannot change rules whilst on the second interface.");
				return;
			}
			duelSession.getRules().reset();
			for (Rule rule : Rule.values()) {
				int index = rule.ordinal();
				if (index == 3 || index == 8 || index == 10 || index == 14) {
					continue;
				}
				duelSession.toggleRule(c, rule);
			}
			break;

		case 30136: // sp attack
		case 30137:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_SPECIAL_ATTACK);
			break;

		case 53245: // no helm
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_HELM);
			break;

		case 53246: // no cape
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_CAPE);
			break;

		case 53247: // no ammy
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_AMULET);
			break;

		case 53249: // no weapon
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_WEAPON);
			break;

		case 53250: // no body
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_BODY);
			break;

		case 53251: // no shield
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_SHIELD);
			break;

		case 53252: // no legs
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_LEGS);
			break;

		case 53255: // no gloves
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_GLOVES);
			break;

		case 53254: // no boots
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_BOOTS);
			break;

		case 53253: // no rings
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_RINGS);
			break;

		case 53248: // no arrows
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(duelSession)) {
				return;
			}
			duelSession.toggleRule(c, Rule.NO_ARROWS);
			break;

		case 26018:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (System.currentTimeMillis() - c.getDuel().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			if (Objects.nonNull(duelSession) && duelSession instanceof DuelSession) {
				duelSession.accept(c, MultiplayerSessionStage.OFFER_ITEMS);
			}
			break;

		case 25120:
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (System.currentTimeMillis() - c.getDuel().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			if (Objects.nonNull(duelSession) && duelSession instanceof DuelSession) {
				duelSession.accept(c, MultiplayerSessionStage.CONFIRM_DECISION);
			}
			break;

		case 4169: // god spell charge
			c.usingMagic = true;
			if (c.getCombat().checkMagicReqs(48)) {
				if (System.currentTimeMillis() - c.godSpellDelay < 300000L) {
					c.sendMessage("You still feel the charge in your body!");
				} else {
					c.godSpellDelay = System.currentTimeMillis();
					c.sendMessage("You feel charged with a magical power!");
					c.gfx100(c.MAGIC_SPELLS[48][3]);
					c.startAnimation(c.MAGIC_SPELLS[48][2]);
					c.usingMagic = false;
				}
			}
			break;

		/*
		 * case 152: c.isRunning2 = !c.isRunning2; int frame = c.isRunning2 ==
		 * true ? 1 : 0; c.getPA().sendFrame36(173,frame); break;
		 */
		case 154:
			if (c.getPA().wearingCape(c.playerEquipment[c.playerCape])) {
				c.stopMovement();
				c.gfx0(c.getPA().skillcapeGfx(c.playerEquipment[c.playerCape]));
				c.startAnimation(c.getPA().skillcapeEmote(c.playerEquipment[c.playerCape]));
			} else {
				c.sendMessage("You must be wearing a Skillcape to do this emote.");
			}
			break;
		case 152:
			if (c.runEnergy < 1) {
				c.isRunning = false;
				c.getPA().setConfig(173, 0);
				return;
			}
			c.isRunning2 = !c.isRunning2;
			// c.getPA().setConfig(173, c.isRunning ? 0 : 1);
			int frame = c.isRunning2 == true ? 1 : 0;
			c.getPA().sendFrame36(173, frame);
			break;
		case 9154:
			long buttonDelay = 0;
			if (System.currentTimeMillis() - buttonDelay > 2000) {
				c.logout();
				buttonDelay = System.currentTimeMillis();
			}
			break;

		case 21010:
			c.takeAsNote = true;
			break;

		case 21011:
			c.takeAsNote = false;
			break;

		// home teleports
		case 4171:
		case 117048:
		case 75010:
			c.getPA().spellTeleport(3087, 3491, 0);
			break;
		case 50056:
			c.getPA().spellTeleport(3087, 3491, 0);
			break;
		/*
		 * case 4171: case 50056: case 117048: if (c.homeTeleDelay <= 0) {
		 * c.homeTele = 10; } else if (c.homeTeleDelay <= 0) { c.homeTele = 10;
		 * }
		 */
		/*
		 * if (c.reset == false) { c.HomePort(); //String type =
		 * c.playerMagicBook == 0 ? "modern" : "ancient";
		 * //c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0,
		 * type); } else if (c.reset == true) { c.resetHomePort(); }
		 */

		/*
		 * case 50235: case 4140: c.getPA().startTeleport(Config.VARROCK_X,
		 * Config.VARROCK_Y, 0, "modern"); c.teleAction = 1; break;
		 * 
		 * case 4143: case 50245: c.getPA().startTeleport(Config.LUMBY_X,
		 * Config.LUMBY_Y, 0, "modern"); c.teleAction = 2; break;
		 * 
		 * case 50253: case 4146: c.getPA().startTeleport(Config.FALADOR_X,
		 * Config.FALADOR_Y, 0, "modern"); c.teleAction = 3; break;
		 * 
		 * 
		 * case 51005: case 4150: c.getPA().startTeleport(Config.CAMELOT_X,
		 * Config.CAMELOT_Y, 0, "modern"); c.teleAction = 4; break;
		 * 
		 * case 51013: case 6004: c.getPA().startTeleport(Config.ARDOUGNE_X,
		 * Config.ARDOUGNE_Y, 0, "modern"); c.teleAction = 5; break;
		 * 
		 * 
		 * case 51023: case 6005: c.getPA().startTeleport(Config.WATCHTOWER_X,
		 * Config.WATCHTOWER_Y, 0, "modern"); c.teleAction = 6; break;
		 * 
		 * 
		 * case 51031: case 29031: c.getPA().startTeleport(Config.TROLLHEIM_X,
		 * Config.TROLLHEIM_Y, 0, "modern"); c.teleAction = 7; break;
		 * 
		 * case 72038: case 51039: //c.getPA().startTeleport(Config.TROLLHEIM_X,
		 * Config.TROLLHEIM_Y, 0, "modern"); //c.teleAction = 8; break;
		 */
		/*
		 * case 50235: case 4140: case 117112: c.getDH().sendOption5("Cows",
		 * "Rock Crabs", "Experiments", "Earth warriors", "idk"); c.teleAction =
		 * 1; break;
		 * 
		 * case 4143: case 50245: case 117123:
		 * c.getDH().sendOption5("Varrock Dungeon", "Taverly Dungeon",
		 * "Brimhaven Dungeon", "idk", "idk"); c.teleAction = 2;
		 * //c.getPA().startTeleport(3094, 3478, 0, "modern");
		 * //c.sendMessage("NOTHING!"); break;
		 * 
		 * case 50253: case 4146: case 117131: c.getPA().startTeleport(3366,
		 * 3275, 0, "modern"); break;
		 * 
		 * case 51005: case 4150: case 117154:
		 * c.getDH().sendOption5("Edgeville", "Wests Dragons (10 Wild))",
		 * "Easts Dragons (18 Wild)", "Multi-Easts (44 Wild)",
		 * "Dark Castle Multi (17 Wild)"); c.teleAction = 4; break;
		 * 
		 * case 51013: case 6004: case 118242: if
		 * (c.getItems().playerHasItem(555, 2) &&
		 * c.getItems().playerHasItem(563, 2) && c.playerLevel[6] > 50) {
		 * c.getPA().startTeleport(2662, 3305, 0, "modern");
		 * c.getItems().deleteItem2(555, 2); c.getItems().deleteItem2(563, 2); }
		 * else {
		 * c.sendMessage("You don't have the required items and or level."); }
		 * break;
		 * 
		 * case 51023: case 6005: if (c.getItems().playerHasItem(557, 2) &&
		 * c.getItems().playerHasItem(563, 2) && c.playerLevel[6] > 57) {
		 * c.getPA().startTeleport(2549, 3112, 0, "modern");
		 * c.getItems().deleteItem2(557, 2); c.getItems().deleteItem2(563, 2); }
		 * else {
		 * c.sendMessage("You don't have the required items and or level."); }
		 * break;
		 * 
		 * case 29031: if (c.getItems().playerHasItem(554, 2) &&
		 * c.getItems().playerHasItem(563, 2) && c.playerLevel[6] > 60) {
		 * c.getPA().startTeleport(2888, 3674, 0, "modern");
		 * c.getItems().deleteItem2(563, 2); c.getItems().deleteItem2(554, 2); }
		 * else {
		 * c.sendMessage("You don't have the required items and or level."); }
		 * break;
		 * 
		 * case 72038: case 51039: case 118266: if
		 * (c.getItems().playerHasItem(554, 2) &&
		 * c.getItems().playerHasItem(555, 2) && c.getItems().playerHasItem(563,
		 * 2) && c.getItems().playerHasItem(1963, 1) && c.playerLevel[6] > 63) {
		 * c.getPA().startTeleport(2755, 2784, 0, "modern");
		 * c.getItems().deleteItem2(554, 2); c.getItems().deleteItem2(555, 2);
		 * c.getItems().deleteItem2(563, 2); c.getItems().deleteItem2(1963, 1);
		 * } else {
		 * c.sendMessage("You don't have the required items and or level."); }
		 * break;
		 */

		/* TELEPORTS */
		case 6005:
		case 51023:
		case 117210:
			c.getPA().spellTeleport(2672, 3712, 0);
			break;
		case 51031:
			c.getPA().startTeleport(3287, 3887, 0, "Ancient");
			break;
		case 50235:
		case 4140:
		case 117112:
			// c.getPA().startTeleport(Config.LUMBY_X, Config.LUMBY_Y, 0,
			// "modern");
			c.getDH().sendOption5("Ardougne Lever", "44 Portals", "West Drags", "Hill Giants", "Giant Moles @red@(50+ Wild)");
			c.teleAction = 1;
			break;

		case 4143:
		case 50245:
		case 117123:
			// c.getDH().sendOption5("Barrows", "", "", "Duel Arena", "");
			// c.teleAction = 2;
			c.getDH().sendOption4("Pest Control", "Duel Arena", "Fight Caves", "@blu@Next");
			c.teleAction = 200;
			break;

		case 50253:
		case 117131:
		case 4146:
			c.getDH().sendOption5("KBD Entrance @red@(DEEP WILD)", "Chaos Elemental @red@(DEEP WILD)", "Godwars", "Barrelchest @red@(Lvl 20+ Wild)",
					"@blu@Next");
			c.teleAction = 3;
			break;

		case 51005:
		case 117154:
		case 4150:
			c.getPA().startTeleport(3027, 3379, 0, "modern");
			break;
		case 117186:
		case 6004:
		case 51013:
		c.getDH().sendOption5("Slayer Tower", "Edgeville Dungeon", "Taverly Dungeon", "Brimhaven Dungeon", "@blu@Next");
		c.teleAction = 2;
		break;

		/*
		 * case 51013: case 6004: case 118242:
		 * c.getDH().sendOption5("Lumbridge", "Varrock", "Camelot", "Falador",
		 * "Canifis"); c.teleAction = 20; break;
		 */

		case 9125: // Accurate
		case 6221: // range accurate
		case 48010: // flick (whip)
		case 21200: // spike (pickaxe)
		case 1080: // bash (staff)
		case 6168: // chop (axe)
		case 6236: // accurate (long bow)
		case 17102: // accurate (darts)
		case 8234: // stab (dagger)
		case 22230: // punch
			c.fightMode = 0;
			if (c.autocasting) {
				c.getPA().resetAutocast();
			}
			break;

		case 9126: // Defensive
		case 48008: // deflect (whip)
		case 21201: // block (pickaxe)
		case 1078: // focus - block (staff)
		case 6169: // block (axe)
		case 33019: // fend (hally)
		case 18078: // block (spear)
		case 8235: // block (dagger)
			// case 22231: //unarmed
		case 22228: // unarmed
			c.fightMode = 1;
			if (c.autocasting) {
				c.getPA().resetAutocast();
			}
			break;

		case 9127: // Controlled
		case 48009: // lash (whip)
		case 33018: // jab (hally)
		case 6234: // longrange (long bow)
		case 6219: // longrange
		case 18077: // lunge (spear)
		case 18080: // swipe (spear)
		case 18079: // pound (spear)
		case 17100: // longrange (darts)
			c.fightMode = 3;
			if (c.autocasting) {
				c.getPA().resetAutocast();
			}
			break;

		case 9128: // Aggressive
		case 6220: // range rapid
		case 21203: // impale (pickaxe)
		case 21202: // smash (pickaxe)
		case 1079: // pound (staff)
		case 6171: // hack (axe)
		case 6170: // smash (axe)
		case 33020: // swipe (hally)
		case 6235: // rapid (long bow)
		case 17101: // repid (darts)
		case 8237: // lunge (dagger)
		case 8236: // slash (dagger)
		case 22229: // kick
			c.fightMode = 2;
			if (c.autocasting) {
				c.getPA().resetAutocast();
			}
			break;

		/**
		 * Prayers *
		 */
		case 21233: // thick skin
			c.getCombat().activatePrayer(0);
			break;
		case 21234: // burst of str
			c.getCombat().activatePrayer(1);
			break;
		case 21235: // charity of thought
			c.getCombat().activatePrayer(2);
			break;
		case 70080: // range
			c.getCombat().activatePrayer(3);
			break;
		case 70082: // mage
			c.getCombat().activatePrayer(4);
			break;
		case 21236: // rockskin
			c.getCombat().activatePrayer(5);
			break;
		case 21237: // super human
			c.getCombat().activatePrayer(6);
			break;
		case 21238: // improved reflexes
			c.getCombat().activatePrayer(7);
			break;
		case 21239: // hawk eye
			c.getCombat().activatePrayer(8);
			break;
		case 21240:
			c.getCombat().activatePrayer(9);
			break;
		case 21241: // protect Item
			c.getCombat().activatePrayer(10);
			break;
		case 70084: // 26 range
			c.getCombat().activatePrayer(11);
			break;
		case 70086: // 27 mage
			c.getCombat().activatePrayer(12);
			break;
		case 21242: // steel skin
			c.getCombat().activatePrayer(13);
			break;
		case 21243: // ultimate str
			c.getCombat().activatePrayer(14);
			break;
		case 21244: // incredible reflex
			c.getCombat().activatePrayer(15);
			break;
		case 21245: // protect from magic
			c.getCombat().activatePrayer(16);
			break;
		case 21246: // protect from range
			c.getCombat().activatePrayer(17);
			break;
		case 21247: // protect from melee
			c.getCombat().activatePrayer(18);
			break;
		case 70088: // 44 range
			c.getCombat().activatePrayer(19);
			break;
		case 70090: // 45 mystic
			c.getCombat().activatePrayer(20);
			break;
		case 2171: // retrui
			c.getCombat().activatePrayer(21);
			break;
		case 2172: // redem
			c.getCombat().activatePrayer(22);
			break;
		case 2173: // smite
			c.getCombat().activatePrayer(23);
			break;
		case 70092: // chiv
			if (c.playerLevel[1] >= 65) {
				c.getCombat().activatePrayer(24);
			} else {
				c.sendMessage("You must have a defence level of 65 to use this prayer.");
				c.getPA().sendFrame36(c.PRAYER_GLOW[24], 0);
			}
			break;
		case 70094: // piety
			if (c.playerLevel[1] >= 70) {
				c.getCombat().activatePrayer(25);
			} else {
				c.sendMessage("You must have a defence level of 70 to use this prayer.");
				c.getPA().sendFrame36(c.PRAYER_GLOW[25], 0);
			}
			break;

		case 13092:
			if (!Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
        		c.sendMessage("You are not trading!");
        		return;
        	}
			if (System.currentTimeMillis() - c.getTrade().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).accept(c,
					MultiplayerSessionStage.OFFER_ITEMS);
			break;

		case 13218:
			if (!Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
        		c.sendMessage("You are not trading!");
        		return;
        	}
			if (System.currentTimeMillis() - c.getTrade().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).accept(c,
					MultiplayerSessionStage.CONFIRM_DECISION);
			break;

		case 125011: // Click agree
			if (!c.ruleAgreeButton) {
				c.ruleAgreeButton = true;
				c.getPA().sendFrame36(701, 1);
			} else {
				c.ruleAgreeButton = false;
				c.getPA().sendFrame36(701, 0);
			}
			break;
		case 125003:// Accept
			if (c.ruleAgreeButton) {
				c.getPA().showInterface(3559);
				c.newPlayer = false;
			} else if (!c.ruleAgreeButton) {
				c.sendMessage("You need to agree before you can carry on.");
			}
			break;
		case 125006:// Decline
			c.sendMessage("You have chosen to decline, Client will be disconnected from the server.");
			break;
		/* End Rules Interface Buttons */
		/* Player Options */
		case 74176:
			if (!c.mouseButton) {
				c.mouseButton = true;
				c.getPA().sendFrame36(500, 1);
				c.getPA().sendFrame36(170, 1);
			} else if (c.mouseButton) {
				c.mouseButton = false;
				c.getPA().sendFrame36(500, 0);
				c.getPA().sendFrame36(170, 0);
			}
			break;
		case 74184:
		case 3189:
			if (!c.splitChat) {
				c.splitChat = true;
				c.getPA().sendFrame36(502, 1);
				c.getPA().sendFrame36(287, 1);
			} else {
				c.splitChat = false;
				c.getPA().sendFrame36(502, 0);
				c.getPA().sendFrame36(287, 0);
			}
			break;
		case 74180:
			if (!c.chatEffects) {
				c.chatEffects = true;
				c.getPA().sendFrame36(501, 1);
				c.getPA().sendFrame36(171, 0);
			} else {
				c.chatEffects = false;
				c.getPA().sendFrame36(501, 0);
				c.getPA().sendFrame36(171, 1);
			}
			break;
		case 74188:
			if (!c.acceptAid) {
				c.acceptAid = true;
				c.getPA().sendFrame36(503, 1);
				c.getPA().sendFrame36(427, 1);
			} else {
				c.acceptAid = false;
				c.getPA().sendFrame36(503, 0);
				c.getPA().sendFrame36(427, 0);
			}
			break;
		case 74192:
			if (!c.isRunning2) {
				c.isRunning2 = true;
				c.getPA().sendFrame36(504, 1);
				c.getPA().sendFrame36(173, 1);
			} else {
				c.isRunning2 = false;
				c.getPA().sendFrame36(504, 0);
				c.getPA().sendFrame36(173, 0);
			}
			break;
		case 74201:// brightness1
			c.getPA().sendFrame36(505, 1);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 1);
			break;
		case 74203:// brightness2
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 1);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 2);
			break;

		case 74204:// brightness3
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 1);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 3);
			break;

		case 74205:// brightness4
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 1);
			c.getPA().sendFrame36(166, 4);
			break;
		case 74206:// area1
			c.getPA().sendFrame36(509, 1);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74207:// area2
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 1);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74208:// area3
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 1);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74209:// area4
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 1);
			break;
		case 168:
			c.startAnimation(855);
			break;
		case 169:
			c.startAnimation(856);
			break;
		case 162:
			c.startAnimation(857);
			break;
		case 164:
			c.startAnimation(858);
			break;
		case 165:
			c.startAnimation(859);
			break;
		case 161:
			c.startAnimation(860);
			break;
		case 170:
			c.startAnimation(861);
			break;
		case 171:
			c.startAnimation(862);
			break;
		case 163:
			c.startAnimation(863);
			break;
		case 167:
			c.startAnimation(864);
			break;
		case 172:
			c.startAnimation(865);
			break;
		case 166:
			c.startAnimation(866);
			break;
		case 52050:
			c.startAnimation(2105);
			break;
		case 52051:
			c.startAnimation(2106);
			break;
		case 52052:
			c.startAnimation(2107);
			break;
		case 52053:
			c.startAnimation(2108);
			break;
		case 52054:
			c.startAnimation(2109);
			break;
		case 52055:
			c.startAnimation(2110);
			break;
		case 52056:
			c.startAnimation(2111);
			break;
		case 52057:
			c.startAnimation(2112);
			break;
		case 52058:
			c.startAnimation(2113);
			break;
		case 43092:
			c.startAnimation(0x558);
			break;
		case 2155:
			c.startAnimation(0x46B);
			break;
		case 25103:
			c.startAnimation(0x46A);
			break;
		case 25106:
			c.startAnimation(0x469);
			break;
		case 2154:
			c.startAnimation(0x468);
			break;
		case 52071:
			c.startAnimation(0x84F);
			break;
		case 52072:
			c.startAnimation(0x850);
			break;
		case 59062:
			c.startAnimation(2836);
			break;
		case 72032:
			c.startAnimation(3544);
			break;
		case 72033:
			c.startAnimation(3543);
			break;
		/*
		 * case 72254: //c.startAnimation(3866); break; /* END OF EMOTES
		 */

		case 24017:
			c.getPA().resetAutocast();
			// c.sendFrame246(329, 200, c.playerEquipment[c.playerWeapon]);
			c.getItems().sendWeapon(c.playerEquipment[c.playerWeapon], c.getItems().getItemName(c.playerEquipment[c.playerWeapon]));
			// c.setSidebarInterface(0, 328);
			// c.setSidebarInterface(6, c.playerMagicBook == 0 ? 1151 :
			// c.playerMagicBook == 1 ? 12855 : 1151);
			break;
		}
		if (c.isAutoButton(actionButtonId)) {
			c.assignAutocast(actionButtonId);
		}
	}
}
