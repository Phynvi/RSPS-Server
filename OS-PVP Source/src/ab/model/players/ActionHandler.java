package ab.model.players;

import java.util.Objects;




import ab.model.players.skills.Tanning;
import ab.model.players.skills.JewelryMaking;
import ab.Server;
import ab.model.content.Obelisks;
import ab.model.content.WildernessDitch;
import ab.model.minigames.Sailing;
import ab.model.minigames.pest_control.PestControl;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.clan_wars.ClanWars;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCDefinitions;
import ab.model.npcs.NPCHandler;
import ab.model.npcs.PetHandler;
import ab.model.players.skills.Fishing;
import ab.model.players.skills.Runecrafting;
import ab.model.players.skills.mining.Mineral;
import ab.model.players.skills.thieving.Thieving.Pickpocket;
import ab.model.players.skills.thieving.Thieving.Stall;
import ab.model.players.skills.woodcutting.Tree;
import ab.model.players.skills.woodcutting.Woodcutting;
import ab.server.data.SerializablePair;
import ab.util.Location3D;
import ab.util.Misc;
import ab.util.ScriptManager;
import ab.world.objects.GlobalObject;

public class ActionHandler {

	private Player c;

	public ActionHandler(Player Client) {
		this.c = Client;
	}

	public void firstClickObject(int objectType, int obX, int obY) {
		if(Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.getPA().resetVariables();
		c.clickObjectType = 0;
		c.turnPlayerTo(obX, obY);
		c.getFarming().patchObjectInteraction(objectType, -1, obX, obY);
	//	ClanWars.enterClanPortal(c, objectType);
		Tree tree = Tree.forObject(objectType);
		if (tree != null) {
			Woodcutting.getInstance().chop(c, objectType, obX, obY);
			return;
		}
	    if (Server.getHolidayController().clickObject(c, 1, objectType, obX, obY)) {
	    	return;
	    }
		if (c.getGnomeAgility().gnomeCourse(c, objectType)) {
			return;
		}
		if (c.getWildernessAgility().wildernessCourse(c, objectType)) {
			return;
		}
		c.getMining().mine(objectType, new Location3D(obX, obY, c.heightLevel));
		Obelisks.get().activate(c, objectType);
		switch (objectType) {
		case 23271: 
			if (c.getY() >= 3523) {
				WildernessDitch.leave(c);
			} else
				WildernessDitch.enter(c);
			break;
		case 11795:
			if (!c.getRights().isPlayer()) {
				c.getPA().movePlayer(3577, 9927, 0);
				c.startAnimation(828);
				c.sendMessage("Welcome to the donator's only Slayer Cave.");
			}
		break;
		case 21725:
			c.getPA().movePlayer(2636, 9510, 2);
			break;
		case 21726:
			c.getPA().movePlayer(2636, 9517, 0);
			break;
		case 21722:
			c.getPA().movePlayer(2643, 9594, 2);
			break;
		case 21724:
			c.getPA().movePlayer(2649, 9591, 0);
			break;
		case 23566:
			if(obX == 3120 && obY == 9964) {
				c.getPA().movePlayer(3120, 9970, 0);
			} else if(obX == 3120 && obY == 9969) {
				c.getPA().movePlayer(3120, 9963, 0);
			}
			break;
		case 26760:
			if (c.absX == 3184 && c.absY == 3945) {
				c.getDH().sendDialogues(631, -1);
			} else if (c.absX == 3184 && c.absY == 3944) {
				c.getPA().movePlayer(3184, 3945, 0);
			}
			break;
		case 9326:
			if(c.playerLevel[16] < 62) {
				c.sendMessage("You need an Agility level of 62 to pass this.");
				return;
			}
			if (c.absX < 2769) {
				c.getPA().movePlayer(2775, 10003, 0);
			} else {
				c.getPA().movePlayer(2768, 10002, 0);
			}
			break;
		case 2120:
		case 2119:
			if (c.heightLevel == 2) {
				c.getPA().movePlayer(3412, 3540, 1);
			} else if (c.heightLevel == 1) {
				c.getPA().movePlayer(3418, 3540, 0);
			}
		break;
		case 16537:
		case 2114:
			if (c.heightLevel == 0)
				c.getPA().movePlayer(c.absX, c.absY, 1);
			else if (c.heightLevel == 1)
				c.getPA().movePlayer(c.absX, c.absY, 2);
		break;
		case 16538:
		case 2118:
			if (c.heightLevel == 1)
				c.getPA().movePlayer(c.absX, c.absY, 0);
			else if (c.heightLevel == 2)
				c.getPA().movePlayer(c.absX, c.absY, 1);
		break;
		case 4493:
			if (c.heightLevel == 0) {
				c.getPA().movePlayer(c.absX - 5, c.absY, 1);
			} else if (c.heightLevel == 1) {
				c.getPA().movePlayer(c.absX + 5, c.absY, 2);
			}
		break;
		
		case 4495:
			if (c.heightLevel == 1 && c.absY > 3538 && c.absY < 3543) {
				c.getPA().movePlayer(c.absX + 5, c.absY, 2);
			} else {
			c.sendMessage("I can't reach that!");
			}
		break;
		case 2623:
			if (c.absX == 2924 && c.absY == 9803) {
				c.getPA().movePlayer(c.absX - 1, c.absY, 0);
			} else if (c.absX == 2923 && c.absY == 9803) {
				c.getPA().movePlayer(c.absX + 1, c.absY, 0);
			}
		break;
		case 15644:
		case 15641:
		case 24306:
		case 24309:
			if(c.heightLevel == 2) {
				//if(Boundary.isIn(c, WarriorsGuild.WAITING_ROOM_BOUNDARY) && c.heightLevel == 2) {
					c.getWarriorsGuild().handleDoor();
					return;
				//}
			}
			if(c.heightLevel == 0) {
				if(c.absX == 2855 || c.absX == 2854) {
					if(c.absY == 3546)
						c.getPA().movePlayer(c.absX, c.absY - 1, 0);
					else if(c.absY == 3545)
						c.getPA().movePlayer(c.absX, c.absY + 1, 0);
					c.turnPlayerTo(obX, obY);
				}
			}
			break;
		case 15653:
			if(c.absY == 3546) {
				if(c.absX == 2877)
					c.getPA().movePlayer(c.absX-1, c.absY, 0);
				else if(c.absX == 2876)
					c.getPA().movePlayer(c.absX+1, c.absY, 0);
				c.turnPlayerTo(obX, obY);
			}
			break;
		case 24303:
			c.getPA().movePlayer(2840, 3539, 0);
			break;
		case 4308:
			JewelryMaking.mouldInterface(c);
			break;
		case 878:
			c.getDH().sendDialogues(613, -1);
			break;
		case 1733:
			if (c.absY > 3920 && c.inWild())
				c.getPA().movePlayer(3045, 10323, 0);
			break;
		case 1734:
			if (c.absY > 9000 && c.inWild())
				c.getPA().movePlayer(3044, 3927, 0);
			break;
		case 4156:
			if (c.absY > 3920 && c.inWild())
				c.getPA().movePlayer(3087, 3491, 0);
			break;
		case 4157:
			c.getPA().spellTeleport(2604, 3154, 0);
			c.sendMessage("This is the dicing area. Place a bet on designated hosts.");
			break;
		case 2309:
			if (c.getX() == 2998 && c.getY() == 3916) {
				c.getAgility().doWildernessEntrance(c);
			}
			break;
		case 18988:
			if (c.inWild() && c.absX == 3069 && c.absY == 10255) {
				c.getPA().movePlayer(3017, 3850, 0);
			}
			break;
		case 18987:
			if (c.inWild() && c.absY >= 3847 && c.absY <= 3860) {
				c.getPA().movePlayer(3069, 10255, 0);
			}
			break;
		case 7407:
		case 7408:
		if (c.absY < 9000) {
			if (c.absY > 3903) {
				c.getPA().movePlayer(c.absX, c.absY - 1, 0);
			} else {
				c.getPA().movePlayer(c.absX, c.absY + 1, 0);
			}
		} else if (c.absY > 9917) {
			c.getPA().movePlayer(c.absX, c.absY - 1, 0);
		} else {
			c.getPA().movePlayer(c.absX, c.absY + 1, 0);
		}
			break;
		/*case 1276:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(0, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1278:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(1, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1286:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(2, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1281:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(3, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1308:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(4, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 5552:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(5, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1307:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(6, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1309:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(7, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 1306:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(8, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 5551:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(9, c.objectX, c.objectY,
					c.clickObjectType);
			break;
		case 5553:
			if (!c.inWc() && !c.inDz()) {
				return;
			}
			c.getWoodcutting().startWoodcutting(10, c.objectX, c.objectY,
					c.clickObjectType);
			break;*/

		case 24600:
			c.getDH().sendDialogues(500, -1);
			break;
		case 6702:
		case 6703:
		case 6704:
		case 6705:
		case 6706:
		case 6707:
		case 20672:
		case 20667:
		case 20668:
		case 20670:
		case 20671:
		case 20699:
			c.getBarrows().useStairs();
			break;
		case 10284:
			c.getBarrows().useChest();
			break;

		case 20772:
			if (c.barrowsNpcs[0][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1677, c.getX(), c.getY() - 1, 3,
						0, 120, 25, 200, 200, true, true);
				c.barrowsNpcs[0][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20721:
			if (c.barrowsNpcs[1][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1676, c.getX() + 1, c.getY(), 3,
						0, 120, 20, 200, 200, true, true);
				c.barrowsNpcs[1][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20771:
			if (c.barrowsNpcs[2][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1675, c.getX(), c.getY() - 1, 3,
						0, 90, 17, 200, 200, true, true);
				c.barrowsNpcs[2][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20722:
			if (c.barrowsNpcs[3][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1674, c.getX(), c.getY() - 1, 3,
						0, 120, 23, 200, 200, true, true);
				c.barrowsNpcs[3][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20770:
			c.getDH().sendDialogues(2900, 2026);
			break;

		case 20720:
			if (c.barrowsNpcs[5][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1673, c.getX() - 1, c.getY(), 3,
						0, 90, 19, 200, 200, true, true);
				c.barrowsNpcs[5][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;
		case 14315:
			PestControl.addToLobby(c);
			break;

		case 14314:
			PestControl.removeFromLobby(c);
			break;
			
		case 14235:
		case 14233:
			if (c.objectX == 2670) {
				if (c.absX <= 2670) {
					c.absX = 2671;
				} else {
					c.absX = 2670;
				}
			}
			if (c.objectX == 2643) {
				if (c.absX >= 2643) {
					c.absX = 2642;
				} else {
					c.absX = 2643;
				}
			}
			if (c.absX <= 2585) {
				c.absY += 1;
			} else {
				c.absY -= 1;
			}
			c.getPA().movePlayer(c.absX, c.absY, 0);
			break;
			
			
		case 245:
			this.c.getPA().movePlayer(this.c.absX, this.c.absY + 2, 2);
			break;
		case 246:
			this.c.getPA().movePlayer(this.c.absX, this.c.absY - 2, 1);
			break;
		case 272:
			this.c.getPA().movePlayer(this.c.absX, this.c.absY, 1);
			break;
		case 273:
			this.c.getPA().movePlayer(this.c.absX, this.c.absY, 0);
			break;
		/* Godwars Door */
		/*
		 * case 26426: // armadyl if (c.absX == 2839 && c.absY == 5295) {
		 * c.getPA().movePlayer(2839, 5296, 2);
		 * c.sendMessage("@blu@May the gods be with you."); } else {
		 * c.getPA().movePlayer(2839, 5295, 2); } break; case 26425: // bandos
		 * if (c.absX == 2863 && c.absY == 5354) { c.getPA().movePlayer(2864,
		 * 5354, 2); c.sendMessage("@blu@May the gods be with you."); } else {
		 * c.getPA().movePlayer(2863, 5354, 2); } break; case 26428: // bandos
		 * if (c.absX == 2925 && c.absY == 5332) { c.getPA().movePlayer(2925,
		 * 5331, 2); c.sendMessage("@blu@May the gods be with you."); } else {
		 * c.getPA().movePlayer(2925, 5332, 2); } break; case 26427: // bandos
		 * if (c.absX == 2908 && c.absY == 5265) { c.getPA().movePlayer(2907,
		 * 5265, 0); c.sendMessage("@blu@May the gods be with you."); } else {
		 * c.getPA().movePlayer(2908, 5265, 0); } break;
		 */

		case 5960:
			if (c.leverClicked == false) {
				c.getDH().sendDialogues(114, 9985);
				c.leverClicked = true;
			} else {
				c.getPA().startLeverTeleport(3090, 3956, 0, "lever");
			}
			break;
		/*
		 * case 3223: case 21764: case 411: case 26289: if (c.playerLevel[5] <
		 * c.getPA().getLevelForXP(c.playerXP[5])) { c.playerLevel[5] =
		 * c.getPA().getLevelForXP(c.playerXP[5]); //
		 * c.sendMessage("You recharge your prayer points.");
		 * c.getPA().refreshSkill(5); } c.playerLevel[3] =
		 * c.getPA().getLevelForXP(c.playerXP[3]); c.getPA().refreshSkill(3);
		 * c.startAnimation(645); c.specAmount = 10.0;
		 * c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
		 * c.sendMessage(
		 * "@red@You've recharged your special attack, prayer points and hitpoints."
		 * ); break;
		 */
		case 5959:
			c.getPA().startLeverTeleport(2539, 4712, 0, "lever");
			break;
		case 1814:
			this.c.getPA().startLeverTeleport(3158, 3953, 0, "lever");
			break;
		case 1815:
			this.c.getPA().startLeverTeleport(3087, 3500, 0, "lever");
			break;
		case 1816:
			this.c.getPA().startLeverTeleport(2271, 4680, 0, "lever");
			break;
		case 1817:
			this.c.getPA().startLeverTeleport(3067, 10253, 0, "lever");
			break;
		/* Start Brimhavem Dungeon */
		case 2879:
			c.getPA().movePlayer(2542, 4718, 0);
			break;
		case 2878:
			c.getPA().movePlayer(2509, 4689, 0);
			break;
		case 5083:
			c.getPA().movePlayer(2713, 9564, 0);
			c.sendMessage("You enter the dungeon.");
			break;

		case 5103:
			if (c.absX == 2691 && c.absY == 9564) {
				c.getPA().movePlayer(2689, 9564, 0);
			} else if (c.absX == 2689 && c.absY == 9564) {
				c.getPA().movePlayer(2691, 9564, 0);
			}
			break;

		case 5106:
			if (c.absX == 2674 && c.absY == 9479) {
				c.getPA().movePlayer(2676, 9479, 0);
			} else if (c.absX == 2676 && c.absY == 9479) {
				c.getPA().movePlayer(2674, 9479, 0);
			}
			break;

		case 5105:
			if (c.absX == 2672 && c.absY == 9499) {
				c.getPA().movePlayer(2674, 9499, 0);
			} else if (c.absX == 2674 && c.absY == 9499) {
				c.getPA().movePlayer(2672, 9499, 0);
			}
			break;

		case 5107:
			if (c.absX == 2693 && c.absY == 9482) {
				c.getPA().movePlayer(2695, 9482, 0);
			} else if (c.absX == 2695 && c.absY == 9482) {
				c.getPA().movePlayer(2693, 9482, 0);
			}
			break;

		case 5104:
			if (c.absX == 2683 && c.absY == 9568) {
				c.getPA().movePlayer(2683, 9570, 0);
			} else if (c.absX == 2683 && c.absY == 9570) {
				c.getPA().movePlayer(2683, 9568, 0);
			}
			break;

		case 5100:
			if (c.absY <= 9567) {
				c.getPA().movePlayer(2655, 9573, 0);
			} else if (c.absY >= 9572) {
				c.getPA().movePlayer(2655, 9566, 0);
			}
			break;
		case 5099:
			if(c.playerLevel[16] < 34) {
				c.sendMessage("You need an Agility level of 34 to pass this.");
				return;
			}
			if(c.objectX == 2698 && c.objectY == 9498) {
				c.getPA().movePlayer(2698, 9492, 0);
			} else if(c.objectX == 2698 && c.objectY == 9493) {
				c.getPA().movePlayer(2698, 9499, 0);
			}
			break;
		case 5088:
			if(c.playerLevel[16] < 30) {
				c.sendMessage("You need an Agility level of 30 to pass this.");
				return;
			}
			c.getPA().movePlayer(2687, 9506, 0);
			break;
		case 5090:
			if(c.playerLevel[16] < 30) {
				c.sendMessage("You need an Agility level of 30 to pass this.");
				return;
			}
			c.getPA().movePlayer(2682, 9506, 0);
			break;
	/*	case 5099:
		if (c.absY <= 9493) {
				c.getPA().movePlayer(2698, 9500, 0);
			} else if (c.absY >= 9499) {
				c.getPA().movePlayer(2698, 9492, 0);
			}
			break;

		case 5088:
			if (c.absX <= 2682) {
				c.getPA().movePlayer(2687, 9506, 0);
			} else if (c.absX >= 2687) {
				c.getPA().movePlayer(2682, 9506, 0);
			}
			break;*/

		case 5110:
			if(c.playerLevel[16] < 12) {
				c.sendMessage("You need an Agility level of 12 to pass this.");
				return;
			}
			c.getPA().movePlayer(2647, 9557, 0);
			break;
		case 5111:
			if(c.playerLevel[16] < 12) {
				c.sendMessage("You need an Agility level of 12 to pass this.");
				return;
			}
			c.getPA().movePlayer(2649, 9562, 0);
			break;

		case 5084:
			c.getPA().movePlayer(2744, 3151, 0);
			c.sendMessage("You exit the dungeon.");
			break;
		/* End Brimhavem Dungeon */
		case 6481:
			c.getPA().movePlayer(3233, 9315, 0);
			break;
		/*
		 * case 17010: if (c.playerMagicBook == 0) {
		 * c.sendMessage("You switch spellbook to lunar magic.");
		 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting
		 * = false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
		 * (c.playerMagicBook == 1) {
		 * c.sendMessage("You switch spellbook to lunar magic.");
		 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting
		 * = false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
		 * (c.playerMagicBook == 2) { c.setSidebarInterface(6, 1151);
		 * c.playerMagicBook = 0; c.autocasting = false;
		 * c.sendMessage("You feel a drain on your memory."); c.autocastId = -1;
		 * c.getPA().resetAutocast(); break; } break;
		 */

		case 1551:
			if (c.absX == 3252 && c.absY == 3266) {
				c.getPA().movePlayer(3253, 3266, 0);
			}
			if (c.absX == 3253 && c.absY == 3266) {
				c.getPA().movePlayer(3252, 3266, 0);
			}
			break;
		case 1553:
			if (c.absX == 3252 && c.absY == 3267) {
				c.getPA().movePlayer(3253, 3266, 0);
			}
			if (c.absX == 3253 && c.absY == 3267) {
				c.getPA().movePlayer(3252, 3266, 0);
			}
			break;
		case 6189:
		case 26300:
			c.getSmithing().sendSmelting();
			break;
		// abyss rifts
		case 7129: // fire riff
			Runecrafting.craftRunesOnAltar(c, 14, 7, 554, 50, 60, 70);
			break;
		case 7130: // earth riff
			Runecrafting.craftRunesOnAltar(c, 9, 7, 557, 45, 55, 65);
			break;
		case 7131: // body riff
			Runecrafting.craftRunesOnAltar(c, 20, 8, 559, 55, 65, 75);
			break;
		case 7132: // cosmic riff
			Runecrafting.craftRunesOnAltar(c, 27, 10, 564, 72, 84, 96);
			break;
		case 7133: // nat riff
			Runecrafting.craftRunesOnAltar(c, 44, 9, 561, 60, 74, 91);
			break;
		case 7134: // chaos riff
			Runecrafting.craftRunesOnAltar(c, 35, 9, 562, 60, 70, 80);
			break;
		case 7135: // law riff
			Runecrafting.craftRunesOnAltar(c, 54, 10, 563, 65, 79, 93);
			break;
		case 7136: // death riff
			Runecrafting.craftRunesOnAltar(c, 65, 10, 560, 72, 84, 96);
			break;
		case 7137: // water riff
			Runecrafting.craftRunesOnAltar(c, 5, 6, 555, 30, 45, 60);
			break;
		case 7138: // soul riff
			Runecrafting.craftRunesOnAltar(c, 65, 10, 566, 72, 84, 96);
			break;
		case 7139: // air riff
			Runecrafting.craftRunesOnAltar(c, 1, 5, 556, 30, 45, 60);
			break;
		case 7140: // mind riff
			Runecrafting.craftRunesOnAltar(c, 1, 5, 558, 30, 45, 60);
			break;
		case 7141: // blood rift
			Runecrafting.craftRunesOnAltar(c, 77, 11, 565, 89, 94, 99);
			break;

		/* AL KHARID */
		case 2883:
		case 2882:
			c.getDH().sendDialogues(1023, 925);
			break;
		case 2412:
			Sailing.startTravel(c, 1);
			break;
		case 2414:
			Sailing.startTravel(c, 2);
			break;
		case 2083:
			Sailing.startTravel(c, 5);
			break;
		case 2081:
			Sailing.startTravel(c, 6);
			break;
		case 14304:
			Sailing.startTravel(c, 14);
			break;
		case 14306:
			Sailing.startTravel(c, 15);
			break;

		case 10083:
		case 11744:
		case 3045:
		case 14367:
		case 3193:
		case 10517:
		case 11402:
		case 26972:
		case 4483:
		case 25808:
		case 26707:
		case 24101:
			c.getPA().openUpBank();
			break;
			
	/*	case 26645: //Clan wars ffa enter
			c.getPA().movePlayer(3327, 4751, 0);
			break;
		case 26646: //Clan wars ffa exit
			c.getPA().movePlayer(3352, 3163, 0);
			break;*/
			

		/**
		 * Entering the Fight Caves.
		 */
		case 11833:
			if (Boundary.entitiesInArea(Boundary.FIGHT_CAVE) >= 50) {
				c.sendMessage("There are too many people using the fight caves at the moment. Please try again later");
				return;
			}

			c.getDH().sendDialogues(633, -1);
			// c.sendMessage("Temporarily disabled, sorry.");
			break;
			
		case 11834:
			if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
				c.getFightCave().leaveGame();
				return;
			}
			break;

		/**
		 * Clicking on the Ancient Altar.
		 */
		case 6552:
			if (c.inWild()) {
				return;
			}
			if (c.absY >= 3504 && c.absY <= 3507) {
				if (c.playerMagicBook == 0) {
					c.playerMagicBook = 1;
					c.setSidebarInterface(6, 28062);
					c.autocasting = false;
					c.sendMessage("An ancient wisdomin fills your mind.");
					c.getPA().resetAutocast();
				} else if (c.playerMagicBook == 1) {
					c.sendMessage("You switch to the lunar spellbook.");
					c.setSidebarInterface(6, 28064);
					c.playerMagicBook = 2;
					c.autocasting = false;
					c.autocastId = -1;
					c.getPA().resetAutocast();
				} else if (c.playerMagicBook == 2) {
					c.setSidebarInterface(6, 28060);
					c.playerMagicBook = 0;
					c.autocasting = false;
					c.sendMessage("You feel a drain on your memory.");
					c.autocastId = -1;
					c.getPA().resetAutocast();
				}
			}
			break;

		/**
		 * c.setSidebarInterface(6, 1151); Recharing prayer points.
		 */
		case 409:
			if (c.inWild()) {
				return;
			}
			if (c.absY >= 3508 && c.absY <= 3513) {
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					c.startAnimation(645);
					c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
					c.sendMessage("You recharge your prayer points.");
					c.getPA().refreshSkill(5);
				} else {
					c.sendMessage("You already have full prayer points.");
				}
			}
			break;
		case 412:
			if (c.inWild()) {
				return;
			}
			if (c.absY >= 3504 && c.absY <= 3507) {
				if (c.specAmount < 10.0) {
					if (c.specRestore > 0) {
						c.sendMessage("You have to wait another "
								+ c.specRestore + " seconds to use this altar.");
						return;
					}
					if (c.getRights().getValue() >= 1) {
						c.specRestore = 120;
						c.specAmount = 10.0;
						c.getItems().addSpecialBar(
								c.playerEquipment[c.playerWeapon]);
						c.sendMessage("Your special attack has been restored. You can restore it again in 3 minutes.");
					} else {
						c.specRestore = 240;
						c.specAmount = 10.0;
						c.getItems().addSpecialBar(
								c.playerEquipment[c.playerWeapon]);
						c.sendMessage("Your special attack has been restored. You can restore it again in 6 minutes.");
					}
				}
			}
			break;

		/**
		 * Aquring god capes.
		 */
		case 2873:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Saradomin blesses you with a cape.");
				c.getItems().addItem(2412, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;
		case 2875:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Guthix blesses you with a cape.");
				c.getItems().addItem(2413, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;
		case 2874:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Zamorak blesses you with a cape.");
				c.getItems().addItem(2414, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;

		/**
		 * Oblisks in the wilderness.
		 */
		case 14829:
		case 14830:
		case 14827:
		case 14828:
		case 14826:
		case 14831:
			
			break;

		/**
		 * Clicking certain doors.
		 */
		case 6749:
			if (obX == 3562 && obY == 9678) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (obX == 3558 && obY == 9677) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;

		case 6730:
			if (obX == 3558 && obY == 9677) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (obX == 3558 && obY == 9678) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;

		case 6727:
			if (obX == 3551 && obY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;

		case 6746:
			if (obX == 3552 && obY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;

		case 6748:
			if (obX == 3545 && obY == 9678) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (obX == 3541 && obY == 9677) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;

		case 6729:
			if (obX == 3545 && obY == 9677) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (obX == 3541 && obY == 9678) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;

		case 6726:
			if (obX == 3534 && obY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (obX == 3535 && obY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;

		case 6745:
			if (obX == 3535 && obY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (obX == 3534 && obY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;

		case 6743:
			if (obX == 3545 && obY == 9695) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (obX == 3541 && obY == 9694) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;

		case 6724:
			if (obX == 3545 && obY == 9694) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (obX == 3541 && obY == 9695) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;

		case 1516:
		case 1519:
			if (c.objectY == 9698) {
				if (c.absY >= c.objectY)
					c.getPA().walkTo(0, -1);
				else
					c.getPA().walkTo(0, 1);
			}
		break;
		case 5126:
			if (c.absY == 3554)
				c.getPA().walkTo(0, 1);
			else
				c.getPA().walkTo(0, -1);
			break;

		case 1759:
			if (c.objectX == 2884 && c.objectY == 3397)
				c.getPA().movePlayer(c.absX, c.absY + 6400, 0);
			break;
		case 7168:
		case 7169:
			if((c.objectX == 3106 || c.objectX == 3105) && c.objectY == 9944) {
				if (c.getY() > c.objectY)
					c.getPA().walkTo(0, -1);
				else
					c.getPA().walkTo(0, 1);
			} else {
				if (c.getX() > c.objectX)
					c.getPA().walkTo(-1, 0);
				else
					c.getPA().walkTo(1, 0);
			}
		break;
		case 11727:
			c.sendMessage("This door is locked.");
			break;

		case 16510:
			if (c.absX < c.objectX) {
				c.getPA().movePlayer(c.objectX + 1, c.absY, 0);
			} else if (c.absX > c.objectX) {
				c.getPA().movePlayer(c.objectX - 1, c.absY, 0);
			}
			break;

		case 16509:
			if (c.absX < c.objectX) {
				c.getPA().movePlayer(2892, 9799, 0);
			} else {
				c.getPA().movePlayer(2886, 9799, 0);
			}
			break;

		case 10529:
		case 10527:
			if (c.absY <= c.objectY)
				c.getPA().walkTo(0, 1);
			else
				c.getPA().walkTo(0, -1);
			break;

		case 733:
			if (c.inWild()) {
				c.startAnimation(451);
				if (c.objectX == 3158 && c.objectY == 3951) {
					Server.getGlobalObjects().add(new GlobalObject(734, c.objectX, c.objectY, c.heightLevel, 1, 10, 50, 733));
				} else {
				 Server.getGlobalObjects().add(new GlobalObject(734, c.objectX, c.objectY, c.heightLevel, 2, 10, 50, 733));
				}
			}
			break;

		default:
			ScriptManager.callFunc("objectClick1_" + objectType, c, objectType,
					obX, obY);
			break;

		/**
		 * Forfeiting a duel.
		 */
		case 3203:
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(session)) {
				return;
			}
			if (!Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
				return;
			}
			if (session instanceof DuelSession) {
				if (session.getRules().contains(Rule.FORFEIT)) {
					c.sendMessage("You are not permitted to forfeit the duel.");
					return;
				}
			} else {
				/**
				 * TO-DO: FORFEIT
				 */
			}
			break;

		}
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		c.getFarming().patchObjectInteraction(objectType, -1, obX, obY);
	    if (Server.getHolidayController().clickObject(c, 2, objectType, obX, obY)) {
	    	return;
	    }
		Location3D location = new Location3D(obX, obY, c.heightLevel);
		switch (objectType) {
		case 20672:
		case 20667:
		case 20668:
		case 20670:
		case 20671:
		case 20699:
			c.getBarrows().useStairs();
			break;
		case 10284:
			c.getBarrows().useChest();
			break;
		case 23271: 
			if (c.getY() >= 3523) {
				WildernessDitch.leave(c);
			} else
				WildernessDitch.enter(c);
			break;
		case 20721:
			if (c.barrowsNpcs[1][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1676, c.getX() + 1, c.getY(), 3,
						0, 120, 20, 200, 200, true, true);
				c.barrowsNpcs[1][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20771:
			if (c.barrowsNpcs[2][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1675, c.getX(), c.getY() - 1, 3,
						0, 90, 17, 200, 200, true, true);
				c.barrowsNpcs[2][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20722:
			if (c.barrowsNpcs[3][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1674, c.getX(), c.getY() - 1, 3,
						0, 120, 23, 200, 200, true, true);
				c.barrowsNpcs[3][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 20770:
			c.getDH().sendDialogues(2900, 2026);
			break;

		case 20720:
			if (c.barrowsNpcs[5][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1673, c.getX() - 1, c.getY(), 3,
						0, 90, 19, 200, 200, true, true);
				c.barrowsNpcs[5][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;
		case 20772:
			if (c.barrowsNpcs[0][1] == 0) {
				Server.npcHandler.spawnNpc(c, 1677, c.getX(), c.getY() - 1, 3,
						0, 120, 25, 200, 200, true, true);
				c.barrowsNpcs[0][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;
		case 24600:
			c.getDH().sendDialogues(500, -1);
			break;
		case 11730:
			c.getThieving().steal(Stall.CAKE, objectType, location);
			break;
		case 11731:
			c.getThieving().steal(Stall.GEM, objectType, location);
			break;
		case 11732:
			c.getThieving().steal(Stall.FUR, objectType, location);
			break;
		case 11734:
			c.getThieving().steal(Stall.SILVER, objectType, location);
			break;
		case 14011:
			c.getThieving().steal(Stall.WINE, objectType, location);
			break;
		case 11744:
			c.getPA().openUpBank();
			break;
		case 11727:
		case 11726:
			if (System.currentTimeMillis() - c.lastLockPick < 1000
					|| c.freezeTimer > 0) {
				return;
			}
			c.lastLockPick = System.currentTimeMillis();
			if (c.getItems().playerHasItem(1523, 1)) {

				if (Misc.random(10) <= 2) {
					c.sendMessage("You fail to pick the lock.");
					break;
				}
				if (c.objectX == 3044 && c.objectY == 3956) {
					if (c.absX == 3045) {
						c.getPA().walkTo(-1, 0);
					} else if (c.absX == 3044) {
						c.getPA().walkTo(1, 0);
					}

				} else if (c.objectX == 3038 && c.objectY == 3956) {
					if (c.absX == 3037) {
						c.getPA().walkTo(1, 0);
					} else if (c.absX == 3038) {
						c.getPA().walkTo(-1, 0);
					}
				} else if (c.objectX == 3041 && c.objectY == 3959) {
					if (c.absY == 3960) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 3959) {
						c.getPA().walkTo(0, 1);
					}
				} else if (c.objectX == 3191 && c.objectY == 3963) {
					if (c.absY == 3963) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 3962) {
						c.getPA().walkTo(0, 1);
					}
				} else if (c.objectX == 3190 && c.objectY == 3957) {
					if (c.absY == 3957) {
						c.getPA().walkTo(0, 1);
					} else if (c.absY == 3958) {
						c.getPA().walkTo(0, -1);
					}
				}
			} else {
				c.sendMessage("I need a lockpick to pick this lock.");
			}
			break;
		case 17010:
			if (c.playerMagicBook == 0) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 28062);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 1) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 28064);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 2) {
				c.setSidebarInterface(6, 28060);
				c.playerMagicBook = 0;
				c.autocasting = false;
				c.sendMessage("You feel a drain on your memory.");
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			break;
		/*
		 * One stall that will give different amount of money depending on your
		 * thieving level, also different amount of xp.
		 */

		case 2781:
		case 26814:
		case 11666:
		case 6189:
		case 26300:
			c.getSmithing().sendSmelting();
			break;
		/*
		 * case 2646: Flax.pickFlax(c, obX, obY); break;
		 */

		/**
		 * Opening the bank.
		 */
		case 10083:
		case 14367:
		case 11758:
		case 10517:
		case 26972:
		case 25808:
			c.getPA().openUpBank();
			break;

		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		// c.sendMessage("Object type: " + objectType);
	    if (Server.getHolidayController().clickObject(c, 3, objectType, obX, obY)) {
	    	return;
	    }
		switch (objectType) {
		default:
			ScriptManager.callFunc("objectClick3_" + objectType, c, objectType,
					obX, obY);
			break;
		}
	}

	public void firstClickNpc(int npcType) {
		if(Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickNpcType = 0;
		c.rememberNpcIndex = c.npcClickIndex;
		c.npcClickIndex = 0;
		/*
		 * if(Fishing.fishingNPC(c, npcType)) { Fishing.fishingNPC(c, 1,
		 * npcType); return; }
		 */
		if (PetHandler.talktoPet(c, npcType))
			return;
		if (Server.getHolidayController().clickNpc(c, 1, npcType)) {
			return;
		}
		switch (npcType) {
		case 2040:
			c.getDH().sendDialogues(638, -1);
			break;
		case 2184:
			c.getShops().openShop(29);
			break;
		case 6601:
			NPC golem = NPCHandler.npcs[c.rememberNpcIndex];
			if (golem != null) {
				c.getMining().mine(golem, Mineral.RUNITE, new Location3D(golem.getX(), golem.getY(), golem.heightLevel));
			}
			break;
		case 1850:
			c.getShops().openShop(112);
			break;
		case 2580:
			c.getDH().sendDialogues(629, 2580);
			break;
		case 3257:
			c.getThieving().steal(Pickpocket.FARMER, NPCHandler.npcs[c.rememberNpcIndex]);
			break;
		case 3894:
			c.getDH().sendDialogues(628, npcType);
			break;
		case 3220:
			c.getShops().openShop(25);
			break;
		case 637:
			c.getShops().openShop(6);
			break;
			
		case 732:
			c.getShops().openShop(16);
		break;
		case 527:
		c.getDH().sendDialogues(947, 527);
		break;
		case 520:
			c.getShops().openShop(114);
			break;
		case 2949:
			c.getPestControlRewards().showInterface();
		break;
		case 2461:
			c.getWarriorsGuild().handleDoor();
			break;
		case 402://slayer
			c.getDH().sendDialogues(3300, c.npcType);
			break;
		case 315:
			c.getDH().sendDialogues(539, npcType);
			break;
		case 1308:
			c.getDH().sendDialogues(538, npcType);
			break;
		case 3951:
			c.getShops().openShop(78);
			c.sendMessage("@blu@Please suggest new items to the shop at ::forums");
			break;
		case 954:
			c.getDH().sendDialogues(619, npcType);
			break;
		case 4306:
			c.getShops().openSkillCape();
			break;
		case 3341: // I prefer ''Doctor Orbon" For the NPC id, it is 290
			c.getDH().sendDialogues(2603, 959);
			break;
		case 1306:
			if (!c.ardiRizal()) {
				c.sendMessage("You must remove your equipment before changing your appearance.");
				c.canChangeAppearance = false;
			} else {
				c.getPA().showInterface(3559);
				c.canChangeAppearance = true;
			}
			break;
		case 6080:
			c.getDH().sendDialogues(14400, npcType);
			break;
		case 814:
			c.getDH().sendDialogues(12200, -1);
			//c.getShops().openShop(12);
			break;
		case 4771:
			c.getDH().sendDialogues(2401, 4771);
			break;
		case 3789:
			c.getShops().openShop(75);
			break;
		// FISHING
		case 3913: // NET + BAIT
			Fishing.attemptdata(c, 1);
			break;
		case 3317:
			Fishing.attemptdata(c, 14);
			break;
		case 4712:
			Fishing.attemptdata(c, 15);
			break;
		case 1524:
			Fishing.attemptdata(c, 11);
		break;
		case 3417: // TROUT
			Fishing.attemptdata(c, 3);
			break;
		case 3657:
			Fishing.attemptdata(c, 8);
		break;
		case 635:
			Fishing.attemptdata(c, 13); //DARK CRAB FISHING
		break;
		case 1520: // LURE
		case 310:
		case 311:
		case 314:
		case 317:
		case 318:
		case 328:
		case 331:
			Fishing.attemptdata(c, 9);
			break;

		case 944:
			c.getDH().sendOption5("Hill Giants", "Hellhounds", "Lesser Demons",
					"Chaos Dwarf", "-- Next Page --");
			c.teleAction = 7;
			break;

		case 559:
			c.getShops().openShop(16);
			break;
		case 5809:
			Tanning.sendTanningInterface(c);
			break;

		case 2913:
			c.getShops().openShop(22);
			break;
		case 403:
			c.getDH().sendDialogues(2300, npcType);
			break;
		case 1599:
			if (c.slayerTask <= 0) {
				c.getDH().sendDialogues(11, npcType);
				// c.sendMessage("Slayer will be enabled in some minutes.");
			} else {
				c.sendMessage("You must complete or reset your slayer task before start another.");
			}
			break;

		case 953: // Banker
		case 2574: // Banker
		case 166: // Gnome Banker
		case 1702: // Ghost Banker
		case 494: // Banker
		case 495: // Banker
		case 496: // Banker
		case 497: // Banker
		case 498: // Banker
		case 499: // Banker
		case 567: // Banker
		case 766: // Banker
		case 1036: // Banker
		case 1360: // Banker
		case 2163: // Banker
		case 2164: // Banker
		case 2354: // Banker
		case 2355: // Banker
		case 2568: // Banker
		case 2569: // Banker
		case 2570: // Banker
			c.getPA().openUpBank();
			break;
		case 1986:
			c.getDH().sendDialogues(2244, c.npcType);
			break;

		case 5792:
			c.getShops().openShop(9);
			break;
		case 3515:
			c.getShops().openShop(77);
			break;
		case 536:
			c.getShops().openShop(48);
			break;

		case 1785:
			c.getShops().openShop(8);
			break;

		case 1860:
			c.getShops().openShop(47);
			break;

		case 519:
			c.getShops().openShop(48);
			break;

		case 548:
			c.getDH().sendDialogues(69, c.npcType);
			break;

		case 2258:
			c.getDH().sendOption2("Teleport me to Runecrafting Abyss.",
					"I want to stay here, thanks.");
			c.dialogueAction = 2258;
			break;

		case 532:
			c.getShops().openShop(47);
			break;

		case 535:
			c.getShops().openShop(8);
			break;

		case 506:
			c.getShops().openShop(2);
			break;
		default: 
			String dialogueNPC = NPCDefinitions.get(npcType).getNpcName().toLowerCase().replaceAll(" ", "_");
			c.sendDebugMessage("Attempting to call NPC dialogue from repository: " + dialogueNPC);
			c.dialogue().start(dialogueNPC + "_dialogue", (Object) null);
			break;

		/*
		 * case 198: c.getShops().openSkillCape(); break;
		 */

		}
	}

	public void secondClickNpc(int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickNpcType = 0;
		c.rememberNpcIndex = c.npcClickIndex;
		c.npcClickIndex = 0;

		/*
		 * if(Fishing.fishingNPC(c, npcType)) { Fishing.fishingNPC(c, 2,
		 * npcType); return; }
		 */
		if (PetHandler.pickupPet(c, npcType))
			return;
		if (Server.getHolidayController().clickNpc(c, 2, npcType)) {
			return;
		}
		switch (npcType) {
		case 2040:
			SerializablePair<String, Long> pair = Server.getServerData().getZulrahTime();
			c.getDH().sendNpcChat("The current record is set by: @blu@" + pair.getFirst() + "@bla@.", "With a time of:@blu@ " + Misc.toFormattedMS(pair.getSecond()) + "@bla@.");
			break;
		case 3515:
			c.getShops().openShop(77);
			break;
		case 2184:
			c.getShops().openShop(29);
		break;
		case 2580:
			c.getPA().startTeleport(3039, 4835, 0, "modern");
			c.dialogueAction = -1;
			c.teleAction = -1;
			break;
		case 3894:
			c.getShops().openShop(26);
			break;
		case 3257:
			c.getShops().openShop(16);
			break;
		case 3078:
			c.getThieving().steal(Pickpocket.MAN, NPCHandler.npcs[c.rememberNpcIndex]);
			break;
		case 637:
			c.getShops().openShop(6);
			break;
		case 527:
		c.getDH().sendDialogues(947, 527);
		break;
		case 520:
			c.getShops().openShop(114);
			break;
		case 732:
			c.getShops().openShop(16);
			break;
		case 5809:
			c.getShops().openShop(20);
			break;
		case 402:
			c.getShops().openShop(44);
			break;
		case 315:
			c.getShops().openShop(80);
			break;
		case 1308:
			c.getShops().openShop(79);
			break;
		case 3341: // I prefer ''Doctor Orbon" For the NPC id, it is 290
			if (c.getRights().isPlayer()) {
				c.sendMessage("You need to be a donator to use this future!");
				return;
			}
			if (c.playerLevel[3] < c.getLevelForXP(c.playerXP[3]))
				c.playerLevel[3] = c.getLevelForXP(c.playerXP[3]);
			c.getPA().refreshSkill(3);
			c.sendMessage("@red@Your hitpoints have been restored!");
			break;
		case 403:
			c.getDH().sendDialogues(12001, -1);
			break;
		case 535:
			c.getShops().openShop(8);
			break;
		case 4771:
			c.getDH().sendDialogues(2400, -1);
			break;
		case 3913: // BAIT + NET
			Fishing.attemptdata(c, 2);
			break;
		case 310:
		case 311:
		case 314:
		case 317:
		case 318:
		case 328:
		case 329:
		case 331:
		case 3417: // BAIT + LURE
			Fishing.attemptdata(c, 6);
			break;
		case 3657:
		case 321:
		case 324:// SWORDIES+TUNA-CAGE+HARPOON
			Fishing.attemptdata(c, 7);
			break;
		case 1520:
		case 322:
		case 334: // NET+HARPOON
			Fishing.attemptdata(c, 10);
			break;
		case 532:
			c.getShops().openShop(47);
			break;
		case 1599:
			c.getShops().openShop(10);
			c.sendMessage("You currently have @red@" + c.slayerPoints
					+ " @bla@slayer points.");
			break;
		case 953: // Banker
		case 2574: // Banker
		case 166: // Gnome Banker
		case 1702: // Ghost Banker
		case 494: // Banker
		case 495: // Banker
		case 496: // Banker
		case 497: // Banker
		case 498: // Banker
		case 499: // Banker
		case 394:
		case 567: // Banker
		case 766:
		case 1036: // Banker
		case 1360: // Banker
		case 2163: // Banker
		case 2164: // Banker
		case 2354: // Banker
		case 2355: // Banker
		case 2568: // Banker
		case 2569: // Banker
		case 2570: // Banker
			c.getPA().openUpBank();
			break;

		case 1785:
			c.getShops().openShop(8);
			break;

		case 536:
			c.getShops().openShop(48);
			break;

		case 3796:
			c.getShops().openShop(6);
			break;

		case 1860:
			c.getShops().openShop(6);
			break;

		case 519:
			c.getShops().openShop(7);
			break;

		case 548:
			c.getDH().sendDialogues(69, c.npcType);
			break;

		case 2258:
			c.sendMessage("They do not seem interested in trading.");
			// c.getPA().startTeleport(3039, 4834, 0, "modern"); //first click
			// teleports second click open shops
			break;

		case 506:
			c.getShops().openShop(2);
			break;

		case 528:
			c.getShops().openShop(9);
			break;

		}
	}

	public void thirdClickNpc(int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickNpcType = 0;
		c.rememberNpcIndex = c.npcClickIndex;
		c.npcClickIndex = 0;
		if (PetHandler.talktoPet(c, npcType))
			return;
	    if (Server.getHolidayController().clickNpc(c, 3, npcType)) {
	    	return;
	    }
		switch (npcType) {
		case 2040:
			if (c.getLostItems().size() == 0) {
				c.getDH().sendDialogues(639, 2040);
			} else {
				c.getDH().sendDialogues(640, 2040);
			}
			break;
		case 402:
			c.getShops().openShop(44);
			c.sendMessage("I currently have @blu@"+c.slayerPoints+" @bla@slayer points.");
			break;
		case 315:
			c.getDH().sendDialogues(548, 315);
			break;
		case 403:
			c.getDH().sendDialogues(12001, -1);
			break;
		case 1599:
			c.getShops().openShop(10);
			c.sendMessage("You currently have @red@" + c.slayerPoints
					+ " @bla@slayer points.");
			break;
		case 548:
			if (!c.ardiRizal()) {
				c.sendMessage("You must remove your equipment before changing your appearence.");
				c.canChangeAppearance = false;
			} else {
				c.getPA().showInterface(3559);
				c.canChangeAppearance = true;
			}
			break;

		case 836:
			c.getShops().openShop(103);
			break;

		}
	}
	
	public void operateNpcAction4(int npcType) {
		if(Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickNpcType = 0;
		c.rememberNpcIndex = c.npcClickIndex;
		c.npcClickIndex = 0;
		
		switch (npcType) {
			case 402:
				c.getSlayer().handleInterface("buy");
				break;
			
			case 315:
				c.getDH().sendDialogues(545, npcType);
				break;
		}
	}

}