package ab.model.npcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ab.Config;
import ab.Server;
import ab.clip.Region;
import ab.event.CycleEvent;
import ab.model.content.achievement.AchievementType;
import ab.model.content.achievement.Achievements;
import ab.model.content.zulrah.Zulrah;
import ab.model.content.zulrah.ZulrahLocation;
import ab.model.items.GameItem;
import ab.model.minigames.Wave;
import ab.model.minigames.warriors_guild.AnimatedArmour;
import ab.model.npcs.drops.DropList;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.Events;
import ab.model.players.PlayerHandler;
import ab.model.players.combat.CombatType;
import ab.model.players.combat.Hitmark;
import ab.model.players.skills.Slayer.Task;
import ab.util.Location3D;
import ab.util.Misc;

public class NPCHandler {
	public static int maxNPCs = 10000;
	public static int maxListedNPCs = 10000;
	public static int maxNPCDrops = 10000;
	public static NPC npcs[] = new NPC[maxNPCs];
	private static NPCDef[] npcDef = new NPCDef[maxListedNPCs];
	private static DropList dropList = new DropList("./Data/data/drops.dat");

	public NPCHandler() {
		for (int i = 0; i < maxNPCs; i++) {
			npcs[i] = null;
			NPCDefinitions.getDefinitions()[i] = null;
		}
		if (!Config.PLACEHOLDER_ECONOMY) {
			loadNPCList("./Data/cfg/npc.cfg");
			loadAutoSpawn("./Data/cfg/spawn-config.cfg");
			loadNPCSizes("./Data/cfg/npc_sizes.txt");
		} else {
			loadNPCList("./Data/cfg/npc2.cfg");
			loadAutoSpawn("./Data/cfg/spawn-config2.cfg");
		}
	}

	public void spawnNpc3(Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence,
			boolean attackPlayer, boolean headIcon, boolean summonFollow) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.maximumHealth = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getId();
		newNPC.underAttack = true;
		newNPC.facePlayer(c.playerId);
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (summonFollow) {
			newNPC.summoner = true;
			newNPC.summonedBy = c.playerId;
			c.hasNpc = true;
		}
		if (attackPlayer) {
			newNPC.underAttack = true;
			if (c != null) {
				newNPC.killerId = c.playerId;
			}
		}
		npcs[slot] = newNPC;
	}

	public void stepAway(int i) {
		if (Region.getClipping(npcs[i].getX() - 1, npcs[i].getY(), npcs[i].heightLevel, -1, 0)) {
			npcs[i].moveX = -1;
			npcs[i].moveY = 0;
		} else if (Region.getClipping(npcs[i].getX() + 1, npcs[i].getY(), npcs[i].heightLevel, 1, 0)) {
			npcs[i].moveX = 1;
			npcs[i].moveY = 0;
		} else if (Region.getClipping(npcs[i].getX(), npcs[i].getY() - 1, npcs[i].heightLevel, 0, -1)) {
			npcs[i].moveX = 0;
			npcs[i].moveY = -1;
		} else if (Region.getClipping(npcs[i].getX(), npcs[i].getY() + 1, npcs[i].heightLevel, 0, 1)) {
			npcs[i].moveX = 0;
			npcs[i].moveY = 1;
		}
		Server.npcHandler.handleClipping(i);
		npcs[i].getNextNPCMovement(i);
		npcs[i].updateRequired = true;
	}
	
	public boolean isUndead(int index) {
		String name = getNpcListName(npcs[index].npcType);
		for(String s : Config.UNDEAD)
			if(s.equalsIgnoreCase(name))
				return true;
		return false;
	}
	public void multiAttackGfx(int i, int gfx) {
		if (npcs[i].projectileId < 0)
			return;
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = PlayerHandler.players[j];
				if (c.heightLevel != npcs[i].heightLevel)
					continue;
				if (PlayerHandler.players[j].goodDistance(c.absX, c.absY, npcs[i].absX, npcs[i].absY, 15)) {
					int nX = NPCHandler.npcs[i].getX() + offset(i);
					int nY = NPCHandler.npcs[i].getY() + offset(i);
					int pX = c.getX();
					int pY = c.getY();
					int offX = (nY - pY) * -1;
					int offY = (nX - pX) * -1;
					c.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(i), npcs[i].projectileId,
							getProjectileStartHeight(npcs[i].npcType, npcs[i].projectileId),
							getProjectileEndHeight(npcs[i].npcType, npcs[i].projectileId), -c.getId() - 1, 65);
				}
			}
		}
	}

	public boolean switchesAttackers(int i) {
		switch (npcs[i].npcType) {
		case 2208:
		case 6560:
		case 6611:
		case 6612:
		case 3998:
		case 497:
		case 2551:
		case 6609:
		case 2552:
		case 2553:
		case 2559:
		case 2560:
		case 2561:
		case 2563:
		case 2564:
		case 2565:
		case 2892:
		case 2894:
		case 6528:
			return true;

		}

		return false;
	}

	public static boolean isSpawnedBy(Player player, NPC npc) {
		if (player != null && npc != null)
			if (npc.spawnedBy == player.playerId || npc.killerId == player.playerId)
				return true;
		return false;
	}

	public int getCloseRandomPlayer(int i) {
		ArrayList<Integer> players = new ArrayList<>();
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
					if (!Boundary.isIn(PlayerHandler.players[j], Boundary.GODWARS_BOSSROOMS)) {
						npcs[i].killerId = 0;
						continue;
					}
				}
				if (goodDistance(PlayerHandler.players[j].absX, PlayerHandler.players[j].absY, npcs[i].absX, npcs[i].absY, distanceRequired(i)
						+ followDistance(i))
						|| isFightCaveNpc(i)) {
					if ((PlayerHandler.players[j].underAttackBy <= 0 && PlayerHandler.players[j].underAttackBy2 <= 0)
							|| PlayerHandler.players[j].inMulti())
						if (PlayerHandler.players[j].heightLevel == npcs[i].heightLevel)
							players.add(j);
				}
			}
		}
		if (players.size() > 0)
			return players.get(Misc.random(players.size() - 1));
		else
			return 0;
	}

	public boolean isAggressive(int i) {
		if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
			return true;
		}
		switch (npcs[i].npcType) {
		case 2042:
		case 6560:
		case 1739:
		case 1740:
		case 1741:
		case 1742:
		case 2044:
		case 2043:
		case Zulrah.SNAKELING:
		case 5054:
		case 6611:
		case 6612:
		case 6610:
		case 3998:
		case 497:
		case 2550:
		case 2551:
		case 50:
		case 28:
		case 2552:
		case 6609:
		case 2553:
		case 2558:
		case 2559:
		case 2560:
		case 2561:
		case 2562:
		case 2563:
		case 2564:
		case 2565:
		case 2892:
		case 2894:
		case 6581:
		case 6580:
		case 6579:
		case 2035:
		case 5779:
		case 291:
			// GWD
		case 2207:// Npcs That Give ArmaKC
		case 6230:
		case 6231:
		case 6229:
		case 6232:
		case 6240:
		case 6241:
		case 6242:
		case 6233:
		case 6234:
		case 6243:
		case 6244:
		case 6245:
		case 6246:
		case 6238:
		case 6239:
		case 3165:
		case 6625:
		case 3163:
		case 6573: // end of armadyl npcs
		case 122:// Npcs That Give BandosKC
		case 6278:
		case 6277:
		case 6276:
		case 6283:
		case 6282:
		case 6281:
		case 6280:
		case 6279:
		case 6271:
		case 6272:
		case 6273:
		case 6274:
		case 6269:
		case 6270:
		case 6268:
		case 2218:
		case 2217:
		case 2216:
		case 6574: // end of bandos npcs
		case 6221:
		case 6219:
		case 6220:
		case 6217:
		case 6216:
		case 6215:
		case 6214:
		case 6213:
		case 6212:
		case 6211:
		case 6218:
		case 3132:
		case 3131:
		case 3130:
		case 6576:
		case 6275:
		case 6257:// Npcs That Give SaraKC
		case 6255:
		case 6256:
		case 6258:
		case 6259:
		case 6254:
		case 2208:
		case 2206:
		case 6575:
		case 1689:
		case 1694:
		case 1699:
		case 1704:
		case 1709:
		case 1714:
		case 1724:
		case 1734:
			return true;
		case 1524:
		case 6600:
		case 6601:
		case 6602:
			return false;
		}
		if (npcs[i].inWild() && npcs[i].maximumHealth > 0)
			return true;
		if (isFightCaveNpc(i))
			return true;
		return false;
	}

	public static boolean isFightCaveNpc(int i) {
		if (npcs[i] == null)
			return false;
		switch (npcs[i].npcType) {
			case Wave.TZ_KEK_SPAWN:
			case Wave.TZ_KIH:
			case Wave.TZ_KEK:
			case Wave.TOK_XIL:
			case Wave.YT_MEJKOT:
			case Wave.KET_ZEK:
			case Wave.TZTOK_JAD:
			return true;
		}
		return false;
	}

	/**
	 * Summon npc, barrows, etc
	 **/
	public void spawnNpc(final Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence,
			boolean attackPlayer, boolean headIcon) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		final NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.maximumHealth = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getId();
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (attackPlayer) {
			newNPC.underAttack = true;
			if (c != null) {
				newNPC.killerId = c.playerId;
			}
		}
		for (int[] Guard : Events.NPCs) {
			if (newNPC.npcType == Guard[2]) {
				newNPC.forceChat("Halt, Thief!");
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						newNPC.isDead = true;
						newNPC.updateRequired = true;
						c.hasEvent = false;
					}
				}, 200);
			}
		}
		npcs[slot] = newNPC;
	}

	/**
	 * Emotes
	 **/

	public static int getAttackEmote(int i) {
		if (AnimatedArmour.isAnimatedArmourNpc(npcs[i].npcType)) {
			return 412;
		}
		switch (NPCHandler.npcs[i].npcType) {
		case Zulrah.SNAKELING:
			return 1741;
		case 319:
			return 1682;
		case 2208:
			return 7009;
		case 6600:
			return 153;
		case 2043:
			return 5806;
		case 411:
			return 1512;
		case 1734:
			return 3897;
		case 1724:
			return 3920;
		case 1709:
			return 3908;
		case 1704:
			return 3915;
		case 1699:
			return 3908;
		case 1689:
			return 3891;
		case 437:
			return -1;
		case 3209:
		if (npcs[i].attackType == 0)
			return 4234;
		else if (npcs[i].attackType == 2)
			return 4237;
		case 2037://Skeleton
		case 70:
			return 5485;
		case 2042:
		case 2044:
			return 5069;
		case 5054:
			return 6562;
		case 6528:
			return 711;
		case 6611:
		case 6612:
			return Misc.random(1) == 0 ? 5485 : 5487;
		case 6610:
			return 5327;
		case 419:
			return -1;
		case 3998:
			return 3991;
		case 6609: // Callisto
			return 4925;
		case 73:
		case 5399:
		case 751: // zombie
		case 77:
			return 5568;
		case 497:
			return 3618;
		case 484:
			return 1552;
		case 28:
		case 420:
		case 421:
		case 422:
		case 423:
			return 5568;
		case 5779: // giant mole
			return 3312;
		case 438:
		case 439:
		case 440:
		case 441:
		case 442: // Tree spirit
		case 443:
			return 94;
		case 391:
		case 392:
		case 393:
		case 394:
		case 395:// river troll
		case 396:
			return 284;
		case 891: // moss
			return 4658;
		case 85: // ghost
			return 5540;
		case 2834: // bats
			return 4915;
		case 414: // banshee
			return 1523;
		case 4005: // dark beast
			return 2731;
		case 2206:
			return 6376;
		case 2207:
			return 7018;
		case 2216:
		case 2217:
		case 2218:
			return 6154;
		case 3130:
		case 3131:
		case 3132:
			return 6945;
		case 135:
			return 6579;
		case 6575:
			return 6964;
		case 6576:
			return 6945;
		case 3163:
		case 3164:
		case 3165:
			return 6953;
		case 6573:
			return 6976;
		case 6267:
			return 359;
		case 6268:
			return 2930;
		case 6269:
			return 4652;
		case 6270:
			return 4652;
		case 6271:
			return 4320;
		case 6272:
			return 4320;
		case 6273:
			return 4320;
		case 6274:
			return 4320;
		case 1459:
			return 1402;
		case 6574:
		if (npcs[i].attackType == 0)
			return 7060;
		else if (npcs[i].attackType > 0)
			return 7063;
		case 86:
		case 87:
			return 4933;
		case 871:// Ogre Shaman
		case 5181:// Ogre Shaman
		case 5184:// Ogre Shaman
		case 5187:// Ogre Shaman
		case 5190:// Ogre Shaman
		case 5193:// Ogre Shaman
			return 359;

		case 2892:
		case 2894:
			return 2868;
		case 3116:
			return 2621;
		case 3120:
			return 2625;
		case 3123:
			return 2637;
		case 2746:
			return 2637;
		case 3121:
		case 2167:
			return 2611;
		case 3125:// 360
			return 2647;
		case 5247:
			return 5411;
		case 13: // wizards
			return 711;

		case 655:
			return 5532;

		case 424:
			return 1557;

		case 448:
			return 1590;

		case 415: // abby demon
			return 1537;

		case 11: // nech
			return 1528;

		case 1543:
		case 1611: // garg
			return 1519;

		case 417: // basilisk
			return 1546;

			// case 924: //skele
			// return 260;

		case 6560:// drags
			return npcs[i].attackType == 0 ? 80 : 81;
		case 247:
		case 259:
		case 268:
		case 264:
		case 2919:
		case 270:
		case 1270:
		case 273:
		case 274:
			if (npcs[i].attackType == 3) {
				return 84;
			} else if (npcs[i].attackType == 0) {
			return 80;
			}
		case 2840: // earth warrior
			return 390;

		case 803: // monk
			return 422;

		case 52: // baby drag
			return 25;

		case 58: // Shadow Spider
		case 59: // Giant Spider
		case 60: // Giant Spider
		case 61: // Spider
		case 62: // Jungle Spider
		case 63: // Deadly Red Spider
		case 64: // Ice Spider
		case 3021:
			return 143;

		case 105: // Bear
		case 106:// Bear
			return 41;

		case 412:
			// case 2834:
			return 30;

		case 2033: // rat
			return 138;

		case 2031: // bloodworm
			return 2070;

		case 1769:
		case 1770:
		case 1771:
		case 1772:
		case 1773:
		case 101: // goblin
			return 6184;

		case 1767:
		case 397:
		case 1766:
		case 1768:
		case 81: // cow
			return 5849;

		case 21: // hero
			return 451;

		case 41: // chicken
			return 55;

		case 9: // guard
		case 32: // guard
		case 20: // paladin
			return 451;

		case 1338: // dagannoth
		case 1340:
		case 1342:

			return 1341;

		case 19: // white knight
			return 406;

		case 2084:
		case 111: // ice giant
		case 2098:
		case 2463:
			return 4651;
		case 3127:
			if (npcs[i].attackType == 2)
				return 2656;
			else if (npcs[i].attackType == 1)
				return 2652;
			else if (npcs[i].attackType == 0)
				return 2655;
		case 2452:
			return 1312;

		case 2889:
			return 2859;

		case 118:
		case 291:
			return 99;

		case 2006:// Lesser Demon
		case 2026:// Greater Demon
		case 1432:// Black Demon
		case 1472:// jungle demon
			return 64;

		case 1267:
		case 100:
			return 1312;

		case 2841: // ice warrior
		case 178:
			return 451;

		case 1153: // Kalphite Worker
		case 1154: // Kalphite Soldier
		case 1155: // Kalphite guardian
		case 1156: // Kalphite worker
		case 1157: // Kalphite guardian
			return 1184;

		case 123:
		case 122:
			return 164;

		case 1675: // karil
			return 2075;

		case 1672: // ahrim
			return 729;

		case 1673: // dharok
			return 2067;

		case 1674: // guthan
			return 2080;

		case 1676: // torag
			return 0x814;

		case 1677: // verac
			return 2062;

		case 6581: // supreme
			return 2855;

		case 6580: // prime
			return 2854;

		case 6579: // rex
			return 2851;
		case 6342:
			int test = Misc.random(2);
			if (test == 2) {
				return 5895;
			} else if (test == 1) {
				return 5894;
			} else {
				return 5896;
			}

		case 2054:
			return 3146;


		default:
			return 0x326;
		}
	}
	
	public int getDeadEmote(int i) {
		if (AnimatedArmour.isAnimatedArmourNpc(npcs[i].npcType)) {
			return 836;
		}
		switch (npcs[i].npcType) {
		case 2042:
		case 2043:
		case 2044:
			return -1;
			
		case Zulrah.SNAKELING:
			return 2408;
		case 319:
			return 4929;
		case 6601:
		case 6602:
			return 65535;
		case 1739:
		case 1740:
		case 1741:
		case 1742:
			return 65535;
		case 1734:
			return 3894;
		case 1724:
			return 3922;
		case 1709:
			return 3910;
		case 1704:
			return 3917;
		case 1699:
			return 3901;
		case 1689:
			return 3888;
		case 2037://Skeleton
		case 70:
			return 5491;
		case 437:
			return -1;
		case 3209:
		return 4233;
		case 411:
			return 1513;
		case 5054:
			return 6564;
		case 6611:
		case 6612:
			return 5491;
		case 6610:
			return 5329;
		case 3998:
			return 3987;
		case 871:// Ogre Shaman
			return 361;
		case 6609: // Castillo
			return 4929;
		case 73:
		case 5399:
		case 77:
			return 5569;
		case 438:
		case 439:
		case 440:
		case 441:
		case 442: // Tree spirit
		case 443:
			return 97;
		case 391:
		case 392:
		case 393:
		case 394:
		case 395:// river troll
		case 396:
			return 287;
		case 420:
		case 421:
		case 422:
		case 423:
		case 28:
			return 5569;
			// begin new updates
		case 891: // moss
			return 4659;
		case 85: // ghost
			return 5542;
		case 2834: // bats
			return 4917;
			// end
		case 3163:
		case 3164:
		case 3165:
			return 6956;
		case 2206:
			return 6377;
		case 2207:
			return 7016;
		case 2216:
		case 2217:
		case 2218:
			return 6156;
		case 3130:
		case 3131:
		case 3132:
			return 6946;
		case 6142:
			return 1;
		case 6143:
			return 1;
		case 6144:
			return 1;
		case 6145:
			return 1;
		case 100:
			return 1314;
		case 414:// Battle Mage
			return 1524;
		case 3742:// Ravager
		case 3743:
		case 3744:
		case 3745:
		case 3746:
			return 3916;
		case 3772:// Brawler
		case 3773:
		case 3774:
		case 3775:
		case 3776:
			return 3894;
		case 5779: // giant mole
			return 3313;
		case 135:
			return 6576;
		case 6575:
			return 6965;
		case 6576:
			return 6945;
		case 6574:
			return 7062;
		case 6573:
			return 6975;
		case 6267:
			return 357;
		case 6268:
			return 2938;
		case 6269:
			return 4653;
		case 6270:
			return 4653;
		case 6271:
			return 4321;
		case 6272:
			return 4321;
		case 6273:
			return 4321;
		case 6274:
			return 4321;
		case 2098:
		case 2463:
			return 4651;
		case 1459:
			return 1404;
			/*
			 * case 414: // banshee return 1524;
			 */
		case 2559:
		case 2560:
		case 2561:
			return 6956;
		case 3121:
		case 2167:
			return 2607;
		case 3116:
			return 2620;
		case 3120:
			return 2627;
		case 3118:
			return 2627;
		case 3123:
			return 2638;
		case 2746:
			return 2638;
		case 3125:
			return 2646;
		case 3127:
			return 2654;
		case 3777:
		case 3778:
		case 3779:
		case 3780:
			return -1;
		case 6342:
			return 5898;
		case 2054:
			return 3147;
		case 2035: // spider
			return 146;
		case 2033: // rat
			return 141;
		case 2031: // bloodvel
			return 2073;
		case 1769:
		case 1770:
		case 1771:
		case 1772:
		case 1773:
		case 101: // goblin
			return 6182;
		case 1767:
		case 397:
		case 1766:
		case 1768:
		case 81: // cow
			return 5851;
		case 41: // chicken
			return 57;
		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1342;
		case 6581:
		case 6580:
		case 6579:
			return 2856;
		case 111: // ice giant
			return 131;
		case 2841: // ice warrior
			return 843;
		case 751:// Zombies!!
			return 302;
		case 1626:
		case 1627:
		case 1628:
		case 1629:
		case 1630:
		case 1631:
		case 1632: // turoth!
			return 1597;
		case 417: // basilisk
			return 1548;
		case 1653: // hand
			return 1590;
		case 2006:// demons
		case 2026:
		case 1432:
			return 67;
		case 6:// abby spec
			return 1508;
		case 51:// baby drags
		case 52:
		case 1589:
		case 3376:
			return 28;
		case 1543:
		case 1611:
			return 1518;
		case 484:
		case 1619:
			return 1553;
		case 419:
		case 1621:
			return 1563;
		case 4005:
			return 2732;
		case 415:
			return 1538;
		case 424:
			return 1558;
		case 11:
			return 1530;
		case 435:
		case 1634:
		case 1635:
		case 1636:
			return 1580;
		case 448:
		case 1649:
		case 1650:
		case 1651:
		case 1652:
		case 1654:
		case 1655:
		case 1656:
		case 1657:
			return 1590;
		case 102:
			return 313;
		case 105:
		case 106:
			return 44;
		case 412:
			// case 2834:
			return 36;
		case 122:
		case 123:
			return 167;
		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 3021:
			return 146;
		case 1153:
		case 1154:
		case 1155:
		case 1156:
		case 1157:
			return 1190;
		case 104:
			return 5534;
		case 118:
		case 291:
			return 102;
		case 6560:// drags
			return 92;
		case 247:
		case 259:
		case 268:
		case 264:
		case 270:
		case 2919:
		case 1270:
		case 273:
		case 274:
			return 92;
		default:
			return 2304;
		}
	}

	/**
	 * Attack delays
	 **/
	public int getNpcDelay(int i) {
		switch (npcs[i].npcType) {
		case 6611:
		case 6612:
			return npcs[i].attackType == 2 ? 6 : 5;
		
		case 2025:
		case 2028:
			return 7;

		case 3127:
			return 8;
			
		case 6575:
			return 4;

		case 2558:
		case 2559:
		case 2560:
		case 2561:
		case 6574:
			return 6;
			// saradomin gw boss
		case 2562:
			return 2;

		default:
			return 5;
		}
	}

	private int getProjectileStartHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 3127:
			return 110;
		case 2044:
			return 60;
		case 3163:
		case 3164:
		case 3165:
			return 60;
		case 6610:
			switch (projectileId) {
			case 165:
				return 20;
			}
			break;
		}
		return 43;
	}

	private int getProjectileEndHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 6610:
			switch (projectileId) {
			case 165:
				return 30;
			}
			break;
		}
		return 31;
	}

	/**
	 * Hit delays
	 **/
	public int getHitDelay(int i) {
		switch (npcs[i].npcType) {
		case 6611:
		case 6612:
			return npcs[i].attackType == 2 ? 3 : 2;
		case 6528:
		case 6610:
			return 3;
		case 6581:
		case 6580:
		case 2054:
		case 2892:
		case 2894:
			return 3;

		case 3125:
		case 3121:
		case 2167:
		case 2558:
		case 2559:
		case 2560:
			return 3;

		case 3127:
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return 5;
			else
				return 2;

		case 2025:
			return 4;
		case 2028:
			return 3;

		default:
			return 2;
		}
	}

	/**
	 * Npc respawn time
	 **/
	public int getRespawnTime(int i) {
		switch (npcs[i].npcType) {
		case 6600:
		case 6601:
		case 6602:
			return -1;
		case 5054:
			return -1;
		case 6611:
		case 6612:
			return 90;
		case 319:
			return 90;
		case 6528:
			return 60;
		case 2402:
		case 2401:
		case 2400:
		case 2399:
			return -1;
		case 6609:
			return 70;
		case 6581:
		case 6580:
		case 6579:
		case 2558:
		case 2559:
		case 2560:
		case 2561:
		case 2562:
		case 2563:
		case 2564:
		case 6574:
		case 6573:
		case 6576:
		case 6575:
			return 100;
		case 3777:
		case 3778:
		case 3779:
		case 3780:
			return 500;
		default:
			return 25;
		}
	}

	public void newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found

		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.maximumHealth = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
	}

	public void newNPCList(int npcType, String npcName, int combat, int HP) {
		NPCDefinitions newNPCList = new NPCDefinitions(npcType);
		newNPCList.setNpcName(npcName);
		newNPCList.setNpcCombat(combat);
		newNPCList.setNpcHealth(HP);
		NPCDefinitions.getDefinitions()[npcType] = newNPCList;
	}

	public void handleClipping(int i) {
		NPC npc = npcs[i];
		if (npc.moveX == 1 && npc.moveY == 1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY + 1, npc.heightLevel) & 0x12801e0) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) == 0)
					npc.moveY = 1;
				else
					npc.moveX = 1;
			}
		} else if (npc.moveX == -1 && npc.moveY == -1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY - 1, npc.heightLevel) & 0x128010e) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) == 0)
					npc.moveY = -1;
				else
					npc.moveX = -1;
			}
		} else if (npc.moveX == 1 && npc.moveY == -1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY - 1, npc.heightLevel) & 0x1280183) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) == 0)
					npc.moveY = -1;
				else
					npc.moveX = 1;
			}
		} else if (npc.moveX == -1 && npc.moveY == 1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY + 1, npc.heightLevel) & 0x128013) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) == 0)
					npc.moveY = 1;
				else
					npc.moveX = -1;
			}
		} // Checking Diagonal movement.

		if (npc.moveY == -1) {
			if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) != 0)
				npc.moveY = 0;
		} else if (npc.moveY == 1) {
			if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) != 0)
				npc.moveY = 0;
		} // Checking Y movement.
		if (npc.moveX == 1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY, npc.heightLevel) & 0x1280180) != 0)
				npc.moveX = 0;
		} else if (npc.moveX == -1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY, npc.heightLevel) & 0x1280108) != 0)
				npc.moveX = 0;
		}
	}

	public void process() {
		for (int i = 0; i < maxNPCs; i++) {
			if (npcs[i] == null)
				continue;
			npcs[i].clearUpdateFlags();

		}
		for (int i = 0; i < maxNPCs; i++) {
			if (npcs[i] != null) {
				int type = npcs[i].npcType;
				Player slaveOwner = (PlayerHandler.players[npcs[i].summonedBy] != null ? (Player) PlayerHandler.players[npcs[i].summonedBy]
						: null);
				if (npcs[i] != null && slaveOwner == null && npcs[i].summoner) {
					npcs[i].absX = 0;
					npcs[i].absY = 0;
				}
				if (npcs[i] != null && slaveOwner != null && slaveOwner.hasNpc && (!slaveOwner.goodDistance(
						npcs[i].getX(), npcs[i].getY(), slaveOwner.absX, slaveOwner.absY, 15) || slaveOwner.heightLevel
						!= npcs[i].heightLevel) && npcs[i].summoner) {
					npcs[i].absX = slaveOwner.absX;
					npcs[i].absY = slaveOwner.absY;
					npcs[i].heightLevel = slaveOwner.heightLevel;

				}
				if (npcs[i] != null && slaveOwner != null && slaveOwner.hasNpc && slaveOwner.absX == npcs[i].getX()
						&& slaveOwner.absY == npcs[i].getY() && npcs[i].summoner) {
					stepAway(i);
				}
				if (npcs[i].actionTimer > 0) {
					npcs[i].actionTimer--;
				}

				if (npcs[i].freezeTimer > 0) {
					npcs[i].freezeTimer--;
				}

				if (npcs[i].hitDelayTimer > 0) {
					npcs[i].hitDelayTimer--;
				}

				if (npcs[i].hitDelayTimer == 1) {
					npcs[i].hitDelayTimer = 0;
					applyDamage(i);
				}

				if (npcs[i].attackTimer > 0) {
					npcs[i].attackTimer--;
				}
				
				if (npcs[i].HP > 0 && !npcs[i].isDead) {
					if (npcs[i].npcType == 6611 || npcs[i].npcType == 6612) {
						if (npcs[i].HP < (npcs[i].maximumHealth / 2) && !npcs[i].spawnedMinions) {
							NPCHandler.spawnNpc(5054, npcs[i].getX() - 1, npcs[i].getY(), 0, 1, 175, 14, 100, 120);
							NPCHandler.spawnNpc(5054, npcs[i].getX() + 1, npcs[i].getY(), 0, 1, 175, 14, 100, 120);
							npcs[i].spawnedMinions = true;
						}
					}
				}
				if (npcs[i].npcType == 6602 && !npcs[i].isDead) {
					NPC runiteGolem = getNpc(6600);
					if (runiteGolem != null && !runiteGolem.isDead) {
						npcs[i].isDead = true;
						npcs[i].needRespawn = false;
						npcs[i].actionTimer = 0;
					}
				}
				if (npcs[i].spawnedBy > 0) { // delete summons npc
					if (PlayerHandler.players[npcs[i].spawnedBy] == null
							|| PlayerHandler.players[npcs[i].spawnedBy].heightLevel != npcs[i].heightLevel
							|| PlayerHandler.players[npcs[i].spawnedBy].respawnTimer > 0
							|| !PlayerHandler.players[npcs[i].spawnedBy].goodDistance(npcs[i].getX(), npcs[i].getY(),
									PlayerHandler.players[npcs[i].spawnedBy].getX(), PlayerHandler.players[npcs[i].spawnedBy].getY(),
									NPCHandler.isFightCaveNpc(i) ? 60 : 20)) {

						if (PlayerHandler.players[npcs[i].spawnedBy] != null) {
							for (int o = 0; o < PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs.length; o++) {
								if (npcs[i].npcType == PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][0]) {
									if (PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][1] == 1)
										PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][1] = 0;
								}
							}
						}
						npcs[i] = null;
					}
				}
				if (npcs[i] == null)
					continue;
				if (npcs[i].lastX != npcs[i].getX() || npcs[i].lastY != npcs[i].getY()) {
					npcs[i].lastX = npcs[i].getX();
					npcs[i].lastY = npcs[i].getY();
				}
				
				if (type >= 2042 && type <= 2044 && npcs[i].HP > 0) {
					Player player = PlayerHandler.players[npcs[i].spawnedBy];
					if (player != null && player.getZulrahEvent().getNpc() != null && npcs[i].equals(player.getZulrahEvent().getNpc())) {
						int stage = player.getZulrahEvent().getStage();
						if (type == 2042) {
							if (stage == 0 || stage == 1 || stage == 4 || stage == 9 && npcs[i].totalAttacks >= 20
									|| stage == 11 && npcs[i].totalAttacks >= 5) {
								continue;
							}
						}
						if (type == 2044) {
							if ((stage == 5 || stage == 8) && npcs[i].totalAttacks >= 5) {
								continue;
							}
						}
					}
				}
				/**
				 * Attacking player
				 **/
				if (isAggressive(i) && !npcs[i].underAttack && !npcs[i].isDead && !switchesAttackers(i)) {
					npcs[i].killerId = getCloseRandomPlayer(i);
				} else if (isAggressive(i) && !npcs[i].underAttack && !npcs[i].isDead && switchesAttackers(i)) {
					npcs[i].killerId = getCloseRandomPlayer(i);
				}

				if (System.currentTimeMillis() - npcs[i].lastDamageTaken > 5000) {
					npcs[i].underAttackBy = 0;
					npcs[i].underAttack = false;
				}
				if ((npcs[i].killerId > 0 || npcs[i].underAttack) && !npcs[i].walkingHome && retaliates(npcs[i].npcType)) {
					if (!npcs[i].isDead) {
						int p = npcs[i].killerId;
						if (PlayerHandler.players[p] != null) {
							if (npcs[i].summoner == false) {
								Player c = Server.playerHandler.players[p];
								followPlayer(i, c.playerId);
								if (npcs[i] == null)
									continue;
								if (npcs[i].attackTimer == 0) {
									if (c != null) {
										attackPlayer(c, i);
										// npcs[i].lastKillerId = c.playerId;
									} else {
										npcs[i].killerId = 0;
										npcs[i].underAttack = false;
										npcs[i].facePlayer(0);
									}
								}
							} else {

								Player c = Server.playerHandler.players[p];
								followPlayer(i, c.playerId);

							}
						} else {
							npcs[i].killerId = 0;
							npcs[i].underAttack = false;
							npcs[i].facePlayer(0);
						}
					}
				}

				/**
				 * Random walking and walking home
				 **/
				if (npcs[i] == null)
					continue;
				if ((!npcs[i].underAttack || npcs[i].walkingHome) && !isFightCaveNpc(i) && npcs[i].randomWalk && !npcs[i].isDead) {
					npcs[i].facePlayer(0);
					npcs[i].killerId = 0;
					if (npcs[i].spawnedBy == 0) {
						if ((npcs[i].absX > npcs[i].makeX + Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absX < npcs[i].makeX - Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absY > npcs[i].makeY + Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absY < npcs[i].makeY - Config.NPC_RANDOM_WALK_DISTANCE)) {
							npcs[i].walkingHome = true;
						}
					}

					if (npcs[i].walkingHome && npcs[i].absX == npcs[i].makeX && npcs[i].absY == npcs[i].makeY) {
						npcs[i].walkingHome = false;
					} else if (npcs[i].walkingHome) {
						npcs[i].moveX = GetMove(npcs[i].absX, npcs[i].makeX);
						npcs[i].moveY = GetMove(npcs[i].absY, npcs[i].makeY);
						handleClipping(i);
						npcs[i].getNextNPCMovement(i);
						npcs[i].updateRequired = true;
					}
					if (npcs[i].walkingType >= 0) {
						switch (npcs[i].walkingType) {

						case 5:
							npcs[i].turnNpc(npcs[i].absX - 1, npcs[i].absY);
							break;

						case 4:
							npcs[i].turnNpc(npcs[i].absX + 1, npcs[i].absY);
							break;

						case 3:
							npcs[i].turnNpc(npcs[i].absX, npcs[i].absY - 1);
							break;
						case 2:
							npcs[i].turnNpc(npcs[i].absX, npcs[i].absY + 1);
							break;

						default:
							if (npcs[i].walkingType >= 0) {
								npcs[i].turnNpc(npcs[i].absX, npcs[i].absY);
							}
							break;
						}
					}
					if (npcs[i].walkingType == 1) {
						if (Misc.random(3) == 1 && !npcs[i].walkingHome) {
							int MoveX = 0;
							int MoveY = 0;
							int Rnd = Misc.random(9);
							if (Rnd == 1) {
								MoveX = 1;
								MoveY = 1;
							} else if (Rnd == 2) {
								MoveX = -1;
							} else if (Rnd == 3) {
								MoveY = -1;
							} else if (Rnd == 4) {
								MoveX = 1;
							} else if (Rnd == 5) {
								MoveY = 1;
							} else if (Rnd == 6) {
								MoveX = -1;
								MoveY = -1;
							} else if (Rnd == 7) {
								MoveX = -1;
								MoveY = 1;
							} else if (Rnd == 8) {
								MoveX = 1;
								MoveY = -1;
							}

							if (MoveX == 1) {
								if (npcs[i].absX + MoveX < npcs[i].makeX + 1) {
									npcs[i].moveX = MoveX;
								} else {
									npcs[i].moveX = 0;
								}
							}

							if (MoveX == -1) {
								if (npcs[i].absX - MoveX > npcs[i].makeX - 1) {
									npcs[i].moveX = MoveX;
								} else {
									npcs[i].moveX = 0;
								}
							}

							if (MoveY == 1) {
								if (npcs[i].absY + MoveY < npcs[i].makeY + 1) {
									npcs[i].moveY = MoveY;
								} else {
									npcs[i].moveY = 0;
								}
							}

							if (MoveY == -1) {
								if (npcs[i].absY - MoveY > npcs[i].makeY - 1) {
									npcs[i].moveY = MoveY;
								} else {
									npcs[i].moveY = 0;
								}
							}
							handleClipping(i);
							npcs[i].getNextNPCMovement(i);
							npcs[i].updateRequired = true;
						}
					}
				}
				if (npcs[i].isDead == true) {
					Player player = PlayerHandler.players[npcs[i].spawnedBy];
					if (npcs[i].actionTimer == 0 && npcs[i].applyDead == false && npcs[i].needRespawn == false) {
						if (npcs[i].npcType == 6611) {
							npcs[i].requestTransform(6612);
							npcs[i].HP = 255;
							npcs[i].isDead = false;
							npcs[i].spawnedMinions = false;
							npcs[i].forceChat("Do it again!!");
						} else {
							if (npcs[i].npcType == 6612) {
								npcs[i].npcType = 6611;
								npcs[i].spawnedMinions = false;
							}
							npcs[i].updateRequired = true;
							npcs[i].facePlayer(0);
							npcs[i].killedBy = getNpcKillerId(i);
							npcs[i].animNumber = getDeadEmote(i); // dead emote
							npcs[i].animUpdateRequired = true;
							npcs[i].freezeTimer = 0;
							npcs[i].applyDead = true;
							killedBarrow(i);
							killedCrypt(i);
							if (player != null) {
								this.tzhaarDeathHandler(player, i);
								continue;
							}
							npcs[i].actionTimer = 4; // delete time
							resetPlayersInCombat(i);
						}
					} else if (npcs[i].actionTimer == 0 && npcs[i].applyDead == true && npcs[i].needRespawn == false) {
						npcs[i].needRespawn = true;
						npcs[i].actionTimer = getRespawnTime(i); // respawn time
						dropItems(i);
						appendSlayerExperience(i);
						appendBossKC(i);
						appendKillCount(i);
						npcs[i].absX = npcs[i].makeX;
						npcs[i].absY = npcs[i].makeY;
						npcs[i].HP = npcs[i].maximumHealth;
						npcs[i].animNumber = 0x328;
						npcs[i].updateRequired = true;
						npcs[i].animUpdateRequired = true;
						if (npcs[i].npcType == 2745) {
							handleJadDeath(i);
						}
						if (npcs[i].npcType == 6600) {
							spawnNpc(6601, npcs[i].absX, npcs[i].absY, 0, 0, 0, 0, 0, 0);
						} else if (npcs[i].npcType == 6601) {
							spawnNpc(6602, npcs[i].absX, npcs[i].absY, 0, 0, 0, 0, 0, 0);
							npcs[i] = null;
							NPC golem = getNpc(6600);
							if (golem != null) {
								golem.actionTimer = 150;
							}
						}
					} else if (npcs[i].actionTimer == 0 && npcs[i].needRespawn == true && npcs[i].npcType != 1739
							&& npcs[i].npcType != 1740
							&& npcs[i].npcType != 1741
							&& npcs[i].npcType != 1742) {
						if (player != null) {
							npcs[i] = null;
						} else {
							int old1 = npcs[i].npcType;
							int old2 = npcs[i].makeX;
							int old3 = npcs[i].makeY;
							int old4 = npcs[i].heightLevel;
							int old5 = npcs[i].walkingType;
							int old6 = npcs[i].maximumHealth;
							int old7 = npcs[i].maxHit;
							int old8 = npcs[i].attack;
							int old9 = npcs[i].defence;
							npcs[i] = null;
							newNPC(old1, old2, old3, old4, old5, old6, old7, old8, old9);
						}
					}
				}
			}
		}
	}

	public void applyDamage(int i) {
		if (npcs[i] != null) {
			if (PlayerHandler.players[npcs[i].oldIndex] == null) {
				return;
			}
			if (npcs[i].isDead)
				return;
			if (npcs[i].npcType >= 1739 && npcs[i].npcType <= 1742) {
				return;
			}
			Player c = PlayerHandler.players[npcs[i].oldIndex];
			if (multiAttacks(i)) {
				multiAttackDamage(i);
				return;
			}
			if (c.playerIndex <= 0 && c.npcIndex <= 0)
				if (c.autoRet == 1)
					c.npcIndex = i;
			if (c.attackTimer <= 3 || c.attackTimer == 0 && c.npcIndex == 0 && c.oldNpcIndex == 0) {
				if (!NPCHandler.isFightCaveNpc(i))
					c.startAnimation(c.getCombat().getBlockEmote());
			}
			npcs[i].totalAttacks++;
			boolean protectionIgnored = prayerProtectionIgnored(i);
			if (c.respawnTimer <= 0) {
				int damage = 0;
				int secondDamage = -1;
				if (npcs[i].attackType == 0) {
					damage = Misc.random(getMaxHit(i));
					if (10 + Misc.random(c.getCombat().calculateMeleeDefence()) > Misc.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
					}
					if (npcs[i].npcType == 2043 && c.getZulrahEvent().getNpc() != null && c.getZulrahEvent().getNpc().equals(npcs[i])) {
						Boundary boundary = new Boundary(npcs[i].targetedLocation.getX(), npcs[i].targetedLocation.getY(),
								npcs[i].targetedLocation.getX(), npcs[i].targetedLocation.getY());
						if (!Boundary.isIn(c, boundary)) {
							return;
						}
						damage = 20 + Misc.random(25);
					}
					if (c.prayerActive[18] && !protectionIgnored) { // protect
																	// from
																	// melee
						if (npcs[i].npcType == 1677 || npcs[i].npcType == 1158 || npcs[i].npcType == 1160 || npcs[i].npcType == 8349
								|| npcs[i].npcType == 8133 || npcs[i].npcType == 6342 || npcs[i].npcType == 2054 || npcs[i].npcType == 6560
								|| npcs[i].npcType == 998 || npcs[i].npcType == 999 || npcs[i].npcType == 1000)
							damage = (damage / 2);
						else
							damage = 0;
					}
					if (c.playerEquipment[c.playerShield] == 12817) {
						if (Misc.random(100) > 30 && damage > 0) {
							damage *= .75;
						}
					}
					if (c.playerLevel[3] - damage < 0) {
						damage = c.playerLevel[3];
					}
				} else if (npcs[i].attackType == 1) { // range
					damage = Misc.random(getMaxHit(i));
					if (10 + Misc.random(c.getCombat().calculateRangeDefence()) > Misc.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
					}
					if (c.prayerActive[17] && !protectionIgnored) { // protect
																	// from
																	// range
						if (npcs[i].npcType == 1677 || npcs[i].npcType == 1158 || npcs[i].npcType == 1160 || npcs[i].npcType == 8349
								|| npcs[i].npcType == 8133 || npcs[i].npcType == 6342 || npcs[i].npcType == 2054 || npcs[i].npcType == 6560) {
							damage = (damage / 2);
						} else {
							damage = 0;
						}
						if (c.playerLevel[3] - damage < 0) {
							damage = c.playerLevel[3];
						}
					}
					if (npcs[i].npcType == 2042 || npcs[i].npcType == 2044) {
						if (c.isSusceptibleToVenom()) {
							c.setVenomDamage((byte) 6);
						}
					}
					if (npcs[i].endGfx > 0 && isFightCaveNpc(i)) {
						c.gfx100(npcs[i].endGfx);
					}
				} else if (npcs[i].attackType == 2) { // magic
					damage = Misc.random(getMaxHit(i));
					boolean magicFailed = false;
					if (npcs[i].npcType == 6575) {
						secondDamage = Misc.random(27);
					}
					if (10 + Misc.random(c.getCombat().mageDef()) > Misc.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
						if (secondDamage > -1) {
							secondDamage = 0;
						}
						magicFailed = true;
					}
					if (npcs[i].npcType == 6609) {
						c.sendMessage("Callisto's fury sends an almighty shockwave through you.");
					}
					if (c.prayerActive[16] && !protectionIgnored) {
						if (npcs[i].npcType == 3998 || npcs[i].npcType == 497) {
							int max = npcs[i].npcType == 3998 ? 2 : 0;
							if (Misc.random(2) == 0) {
								damage = 1 + Misc.random(max);
							} else {
								damage = 0;
								if (secondDamage > -1) {
									secondDamage = 0;
								}
							}
						} else if (npcs[i].npcType == 1677 || npcs[i].npcType == 1158 || npcs[i].npcType == 1160 || npcs[i].npcType == 8349
								|| npcs[i].npcType == 8133 || npcs[i].npcType == 6342 || npcs[i].npcType == 2054 || npcs[i].npcType == 6560
								|| npcs[i].npcType == 6528) {
							damage /= 2;
						} else {
							damage = 0;
							if (secondDamage > -1) {
								secondDamage = 0;
							}
							magicFailed = true;
						}
					}
					if (c.playerLevel[3] - damage < 0) {
						damage = c.playerLevel[3];
					}
					if (npcs[i].endGfx > 0 && (!magicFailed || isFightCaveNpc(i))) {
						c.gfx100(npcs[i].endGfx);
					} else {
						c.gfx100(85);
					}
				} else if (npcs[i].attackType == 3) { // fire breath
					int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(11283) ||
							c.getItems().isWearingItem(11284) ? 1 : 0;
					if (System.currentTimeMillis() - c.lastAntifirePotion < c.antifireDelay) {
						resistance++;
					}
					if (resistance == 0) {
						damage = Misc.random(getMaxHit(i));
						c.sendMessage("You are badly burnt by the dragon fire!");
					} else if (resistance == 1) {
						damage = Misc.random(15);
					} else if (resistance == 2) {
						damage = 0;
					}
					if (npcs[i].endGfx != 430 && resistance == 2) {
						damage = 5 + Misc.random(5);
					}
					switch (npcs[i].endGfx) {
						case 429:
							if (c.isSusceptibleToPoison()) {
								c.setPoisonDamage((byte) 6);
							}
							break;
							
						case 428:
							c.freezeTimer = 10;
							break;
							
						case 431:
							c.lastSpear.reset();
							break;
					}
					if (c.playerLevel[3] - damage < 0)
						damage = c.playerLevel[3];
					c.gfx100(npcs[i].endGfx);
				} else if (npcs[i].attackType == 4) { // special attacks
					damage = Misc.random(getMaxHit(i));
					switch (npcs[i].npcType) {
					case 6576:
						int prayerReduction = c.playerLevel[5] / 2;
						if (prayerReduction < 1) {
							break;
						}
						c.playerLevel[5] -= prayerReduction;
						if (c.playerLevel[5] < 0) {
							c.playerLevel[5] = 0;
						}
						c.getPA().refreshSkill(5);
						c.sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
						break;
					case 6528:
						prayerReduction = c.playerLevel[5] / 10;
						if (prayerReduction < 1) {
							break;
						}
						c.playerLevel[5] -= prayerReduction;
						if (c.playerLevel[5] < 0) {
							c.playerLevel[5] = 0;
						}
						c.getPA().refreshSkill(5);
						c.sendMessage("Your prayer has been drained drastically.");
						break;
					case 6609:
						damage = 3;
						c.gfx0(80);
						c.lastSpear.reset();
						c.getPA().getSpeared(npcs[i].absX, npcs[i].absY, 3);
						c.sendMessage("Callisto's roar sends your backwards.");
						break;
					case 6610:
						if (c.prayerActive[16]) {
							damage *= .7;
						}
						secondDamage = Misc.random(getMaxHit(i));
						if (secondDamage > 0) {
							c.gfx0(80);
						}
						break;
					}
				}
				int poisonDamage = getPoisonDamage(npcs[i]);
				if (poisonDamage > 0 && c.isSusceptibleToPoison() && Misc.random(10) == 1) {
					c.setPoisonDamage((byte) poisonDamage);
				}
				if (c.playerLevel[3] - damage < 0 || secondDamage > -1 && c.playerLevel[3] - secondDamage < 0) {
					damage = c.playerLevel[3];
					if (secondDamage > -1) {
						secondDamage = 0;
					}
				}
				handleSpecialEffects(c, i, damage);
				c.logoutDelay.reset();
				if (damage > -1) {
					c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
				}
				if (secondDamage > -1) {
					c.appendDamage(secondDamage, secondDamage > 0 ? Hitmark.HIT : Hitmark.MISS);
				}
				c.getCombat().applyRecoilNPC(damage + (secondDamage > 0 ? secondDamage : 0), i);
				if (c.playerLevel[3] <= 0 && npcs[i].npcType == 28) {
					c.setKilledByZombie(true);					
				}
				c.getPA().refreshSkill(3);
				c.updateRequired = true;
			}
		}
	}
	
	private int getPoisonDamage(NPC npc) {
		switch (npc.npcType) {
			case 6576:
				return 16;
		}
		return 0;
	}
	
	private int multiAttackDistance(NPC npc) {
		if (npc == null) {
			return 0;
		}
		switch (npc.npcType) {
			case 6560:
				return 35;
		}
		return 15;
	}

	public void multiAttackDamage(int i) {
		int damage = Misc.random(getMaxHit(i));
		Hitmark hitmark = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = PlayerHandler.players[j];
				if (c.isDead || c.heightLevel != npcs[i].heightLevel)
					continue;
				if (PlayerHandler.players[j].goodDistance(c.absX, c.absY, npcs[i].absX, npcs[i].absY, multiAttackDistance(npcs[i]))) {
					if (npcs[i].attackType == 4) {
						if (npcs[i].npcType == 6611 || npcs[i].npcType == 6612) {
							if (!(c.absX > npcs[i].absX - 5 && c.absX < npcs[i].absX + 5 && c.absY > npcs[i].absY - 5
									&& c.absY < npcs[i].absY + 5)) {
								continue;
							}
							c.sendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you.");
							createVetionEarthquake(c);
						}
						c.appendDamage(damage, hitmark);
					} else if (npcs[i].attackType == 3) {
						int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(11283) ||
								c.getItems().isWearingItem(11284) ? 1 : 0;
						if (System.currentTimeMillis() - c.lastAntifirePotion < c.antifireDelay) {
							resistance++;
						}
						c.sendMessage("Resistance: "+resistance);
						if (resistance == 0) {
							damage = Misc.random(getMaxHit(i));
							c.sendMessage("You are badly burnt by the dragon fire!");
						} else if (resistance == 1)
							damage = Misc.random(15);
						else if (resistance == 2)
							damage = 0;
						if (c.playerLevel[3] - damage < 0)
							damage = c.playerLevel[3];
						c.gfx100(npcs[i].endGfx);
						c.appendDamage(damage, hitmark);
					} else if (npcs[i].attackType == 2) {
						if (npcs[i].npcType == 6611 || npcs[i].npcType == 6612) {
							if (vetionSpellCoordinates.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
								continue;
							}
						}
						if (!c.prayerActive[16]) {
							if (Misc.random(500) + 200 > Misc.random(c.getCombat().mageDef())) {
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else {
								c.appendDamage(0, Hitmark.MISS);
							}
						} else {
							if (npcs[i].npcType == 6610) {
								damage *= .7;
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else if (npcs[i].npcType == 6528) {
								damage *= .5;
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else {
								c.appendDamage(0, Hitmark.MISS);
							}
						}
					} else if (npcs[i].attackType == 1) {
						if (!c.prayerActive[17]) {
							if (Misc.random(500) + 200 > Misc.random(c.getCombat().calculateRangeDefence())) {
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else {
								c.appendDamage(0, Hitmark.MISS);
							}
						} else {
							c.appendDamage(0, Hitmark.MISS);
						}
					}
					if (npcs[i].endGfx > 0) {
						c.gfx0(npcs[i].endGfx);
					}
				}
				c.getPA().refreshSkill(3);
			}
		}
	}

	public boolean getsPulled(int i) {
		switch (npcs[i].npcType) {
		case 6574:
			if (npcs[i].firstAttacker > 0)
				return false;
			break;
		}
		return true;
	}

	public boolean multiAttacks(int i) {
		switch (npcs[i].npcType) {
		case 6611:
		case 6612:
			return npcs[i].attackType == 2 || npcs[i].attackType == 4 ? true : false;
		case 6528:
			return npcs[i].attackType == 2 || npcs[i].attackType == 4 && Misc.random(3) == 0 ? true : false;
		case 6610:
			return npcs[i].attackType == 2;
		case 2558:
			return true;
		case 2562:
			if (npcs[i].attackType == 2)
				return true;
		case 6574:
			return npcs[i].attackType == 1;
		case 6573:
			return true;
		default:
			return false;
		}

	}

	/**
	 * Npc killer id?
	 **/

	public int getNpcKillerId(int npcId) {
		int oldDamage = 0;
		int count = 0;
		int killerId = 0;
		for (int p = 1; p < Config.MAX_PLAYERS; p++) {
			if (PlayerHandler.players[p] != null) {
				if (PlayerHandler.players[p].lastNpcAttacked == npcId) {
					if (PlayerHandler.players[p].totalDamageDealt > oldDamage) {
						oldDamage = PlayerHandler.players[p].totalDamageDealt;
						killerId = p;
					}
					PlayerHandler.players[p].totalDamageDealt = 0;
				}
			}
		}
		return killerId;
	}

	/**
	 * 
	 */
	private void killedCrypt(int i) {
		Player c = Server.playerHandler.players[npcs[i].killedBy];
		if (c != null) {
			for (int o = 0; o < c.barrowCrypt.length; o++) {
				if (npcs[i].npcType == c.barrowCrypt[o][0]) {
					c.barrowsKillCount++;
					c.getPA().sendFrame126("" + c.barrowsKillCount, 16137);
				}
			}
		}
	}

	private void killedBarrow(int i) {
		Player c = Server.playerHandler.players[npcs[i].killedBy];
		if (c != null) {
			for (int o = 0; o < c.barrowsNpcs.length; o++) {
				if (npcs[i].npcType == c.barrowsNpcs[o][0]) {
					c.barrowsNpcs[o][1] = 2; // 2 for dead
					c.barrowsKillCount++;
				}
			}
		}
	}

	private void tzhaarDeathHandler(Player player, int i) {// hold a vit plz
		if (npcs[i] != null) {
			if (player != null) {
				if (player.getFightCave() != null) {
					if (isFightCaveNpc(i))
						killedTzhaar(player, i);
					if (npcs[i] != null && npcs[i].npcType == 2745) {
						this.handleJadDeath(i);
					}
				}
			}
		}
	}

	private void killedTzhaar(Player player, int i) {
		if (player.getFightCave() != null) {
			player.getFightCave().setKillsRemaining(player.getFightCave().getKillsRemaining() - 1);
			if (player.getFightCave().getKillsRemaining() == 0) {
				player.waveId++;
				player.getFightCave().spawn();
			}
		}
	}

	public void handleJadDeath(int i) {
		Player c = PlayerHandler.players[npcs[i].spawnedBy];
		c.getItems().addItem(6570, 1);
		// c.getDH().sendDialogues(69, 2617);
		c.getFightCave().stop();
		c.waveId = 300;
	}

	/**
	 * Dropping Items!
	 **/
	public void dropItems(int i) {
		Player c = PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			c.getAchievements().kill(npcs[i]);
			c.getNpcDeathTracker().add(getNpcListName(npcs[i].npcType));
			c.getWarriorsGuild().dropDefender(npcs[i].absX, npcs[i].absY);
			if(AnimatedArmour.isAnimatedArmourNpc(npcs[i].npcType))
				AnimatedArmour.dropTokens(c, npcs[i].npcType, npcs[i].absX, npcs[i].absY);
			int random = Misc.random(1500);
			int jadpet = Misc.random(125);
			int zulrahpet = Misc.random(3000);
			if (npcs[i].npcType == 2054 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(15568, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Chaos Elemental pet.");
					}
				}
			}
			if (npcs[i].npcType == 2745 && jadpet == 100) {
				c.sendMessage("@red@You receive a TzRek-Jad pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12941, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x TzRek-Jad.");
					}
				}
			}
			if ((npcs[i].npcType >= 2042 && npcs[i].npcType <= 2044) && zulrahpet == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12921, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Zulrah snakeling.");
					}
				}
			}
			if ((npcs[i].npcType >= 2042 && npcs[i].npcType <= 2044) && zulrahpet == 201) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12939, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Zulrah snakeling.");
					}
				}
			}
			if ((npcs[i].npcType >= 2042 && npcs[i].npcType <= 2044) && zulrahpet == 202) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12940, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Zulrah snakeling.");
					}
				}
			}
			if (npcs[i].npcType == 6560 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12653, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Prince Black Dragon.");
					}
				}
			}
			if (npcs[i].npcType == 6342 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(15567, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Barrelchest pet.");
					}
				}
			}
			if (npcs[i].npcType == 6581 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12643, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Dagannoth Supreme Pet.");
					}
				}
			}
			if (npcs[i].npcType == 6579 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12645, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Dagannoth Rex pet.");
					}
				}
			}
			if (npcs[i].npcType == 6576 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12652, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x K'ril Tsutsaroth pet.");
					}
				}
			}
			if (npcs[i].npcType == 6575 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12651, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Commander Zilyana pet.");
					}
				}
			}
			if (npcs[i].npcType == 6573 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12649, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Kree'Arra pet.");
					}
				}
			}
			if (npcs[i].npcType == 6574 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12650, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x General Graardor Jr.");
					}
				}
			}
			if (npcs[i].npcType == 3998 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(12655, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Kraken Jr.");
					}
				}
			}
			if (npcs[i].npcType == 6609 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(15572, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Callisto pet.");
					}
				}
			}
			if ((npcs[i].npcType == 6611 || npcs[i].npcType == 6612) && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(15573, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Vet'ion pet.");
					}
				}
			}
			if (npcs[i].npcType == 6610 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(8135, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Venenatis pet.");
					}
				}
			}
			if (npcs[i].npcType == 5779 && random == 200) {
				c.sendMessage("@red@You receive a boss pet. It has been added to your bank. Congratulations!");
				c.getItems().addItemToBank(15571, 1);
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Player c2 = Server.playerHandler.players[j];
						c2.sendMessage("<col=006600>" + c.playerName + " received a drop: 1 x Baby Mole.");
					}
				}
			}
			if (npcs[i].npcType == 912 || npcs[i].npcType == 913 || npcs[i].npcType == 914)
				c.magePoints += 1;
			int dropX = npcs[i].absX;
			int dropY = npcs[i].absY;
			int dropHeight = npcs[i].heightLevel;
			if (npcs[i].npcType == 3998) {
				dropX = 3282;
				dropY = 3627;
			}
			if (npcs[i].npcType == 2042 || npcs[i].npcType == 2043 || npcs[i].npcType == 2044 || npcs[i].npcType == 6720) {
				dropX = 2268;
				dropY = 3069;
				c.getZulrahEvent().stop();
			}
			if (dropList.containsKey(npcs[i].npcType)) {
				double modifier = 1;
				if (c.getRights().isBetween(5, 6)) {
					modifier *= 1.05;
				} else if (c.getRights().isBetween(7, 9)) {
					modifier *= 1.15;
				} else if (c.getRights().isBetween(1, 4)) {
					//modifier *= c.getDropModifier();
				}
				if (c.playerEquipment[c.playerRing] == 2572) {
					modifier *= 1.05;
				} else if (c.playerEquipment[c.playerRing] == 12785) {
					modifier *= 1.10;
				}
				for (GameItem item : dropList.get(npcs[i].npcType).getDrops(modifier)) {
					handleLootShare(c, item.getId(), item.getAmount());
					Server.itemHandler.createGroundItem(c, item.getId(), dropX, dropY, dropHeight, item.getAmount(), c.playerId);
				}				
			}

		}
	}

	public void sendLootShareMessage(String message) {
		for (int j = 0; j < Server.playerHandler.players.length; j++) {
			if (Server.playerHandler.players[j] != null) {
				Player c2 = Server.playerHandler.players[j];
				c2.sendMessage("<col=006600>" + message + "");
			}
		}
	}

	public int[] unallowed = { 2366, 592, 4587, 1149, 530, 526, 536, 1333, 1247, 1089, 1047, 1319 };

	public void handleLootShare(Player c, int item, int amount) {
		for (int i = 0; i < unallowed.length; i++) {
			if (item == unallowed[i]) {
				return;
			}
		}
		if (c.getShops().getItemShopValue(item, 1, c.getItems().getItemSlot(item)) > 100000) {
			sendLootShareMessage(c.playerName + " received a drop: " + amount + " x "
					+ ab.model.items.Item.getItemName(item));
		}
	}

	// id of bones dropped by npcs
	public int boneDrop(int type) {
		switch (type) {
		case 1:// normal bones
		case 9:
		case 12:
		case 17:
		case 803:
		case 18:
		case 81:
		case 101:
		case 41:
		case 19:
		case 90:
		case 75:
		case 86:
		case 2834:
		case 1543:
		case 1611:
		case 414:
		case 448:
		case 446:
		case 484:
		case 424:
		case 181:
		case 291:
		case 135:
		case 26:
		case 1341:
			return 526;
		case 2098:
		case 2463:
			return 532;// big bones
		case 6560:// drags
		case 247:
		case 259:
		case 268:
		case 264:
		case 2919:
		case 1270:
		case 270:
		case 273:
		case 274:
			return 536;
		case 1432:
		case 415:
		case 11:
		case 2006:
		case 2054:
			return 592;
		case 6581:
		case 6580:
		case 6579:
			return 6729;
		default:
			return -1;
		}
	}

	public void appendKillCount(int i) {
		Player c = PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			int[] kcMonsters = { 122, 49, 2558, 2559, 2560, 2561, 2550, 2551, 2552, 2553, 2562, 2563, 2564, 2565 };
			for (int j : kcMonsters) {
				if (npcs[i].npcType == j) {
					if (c.killCount < 20) {
						// c.killCount++;
						// c.sendMessage("Killcount: " + c.killCount);
					} else {
						// c.sendMessage("You already have 20 kill count");
					}
					break;
				}
			}
		}
	}

	public void appendBossKC(int i) {
		Player c = PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			int[] bossKC = {
					3998, 5779, 6560, 2054, 6581,
					6580, 6579, 1158, 1159, 1160,
					6575, 6573, 6576, 6574, 6342,
					6609, 6610
			};
			for (int j : bossKC) {
				if (npcs[i].npcType == j) {
					Achievements.increase(c, AchievementType.KILL_BOSS, 1);
					c.bossKills += 1;
					break;
				}
			}
		}
	}

	public int getStackedDropAmount(int itemId, int npcId) {
		switch (itemId) {
		case 995:
			switch (npcId) {
			case 1:
				return 50 + Misc.random(50);
			case 9:
				return 133 + Misc.random(100);
			case 424:
				return 1000 + Misc.random(300);
			case 484:
				return 1000 + Misc.random(300);
			case 446:
				return 1000 + Misc.random(300);
				/*
				 * case 1543: return 1000 + Misc.random(1000);
				 */
			case 11:
				return 1500 + Misc.random(1250);
			case 415:
				return 3000;
			case 18:
				return 500;
			case 101:
				return 60;
			case 1611:
			case 1543:
			case 414:
				return 750 + Misc.random(500);
				/*
				 * case 414: return 250 + Misc.random(500);
				 */
			case 448:
				return 250 + Misc.random(250);
			case 90:
				return 200;
			case 2006:
				return 1000 + Misc.random(455);
			case 52:
				return 400 + Misc.random(200);
			case 135:
				return 1500 + Misc.random(2000);
			case 1341:
				return 1500 + Misc.random(500);
			case 26:
				return 500 + Misc.random(100);
			case 20:
				return 750 + Misc.random(100);
			case 21:
				return 890 + Misc.random(125);
			case 2098:
				return 500 + Misc.random(250);
			case 3121:
				return 500 + Misc.random(350);
			}
			break;
		case 11212:
			return 10 + Misc.random(4);
		case 565:
		case 561:
			return 10;
		case 560:
		case 563:
		case 562:
			return 15;
		case 555:
		case 554:
		case 556:
		case 557:
			return 20;
		case 892:
			return 40;
		case 886:
			return 100;
		case 6522:
			return 6 + Misc.random(5);

		}

		return 1;
	}

	/**
	 * Slayer Experience
	 **/
	public void appendSlayerExperience(int i) {
		@SuppressWarnings("unused")
		int npc = 0;
		Player c = PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			if (c.getSlayer().isSlayerTask(npcs[i].npcType)) {
				c.taskAmount--;
				if (c.playerEquipment[c.playerHands] == 6720) {
					c.getPA().addSkillXP(npcs[i].maximumHealth * (Config.SLAYER_EXPERIENCE * 1.10), 18);
				} else {
					c.getPA().addSkillXP(npcs[i].maximumHealth * Config.SLAYER_EXPERIENCE, 18);
				}
				if (c.taskAmount <= 0) {
					if (c.playerEquipment[c.playerHands] == 6720) {
						c.getPA().addSkillXP((npcs[i].maximumHealth * 8) * (Config.SLAYER_EXPERIENCE * 1.10), 18);
					} else {
						c.getPA().addSkillXP((npcs[i].maximumHealth * 8) * Config.SLAYER_EXPERIENCE, 18);
					}
					Task task = Task.forNpc(c.slayerTask);
					int points = task.getDifficulty() * 4;
					c.slayerTask = -1;
					c.getPA().loadQuests();
					c.slayerPoints += points;
					Achievements.increase(c, AchievementType.SLAYER, 1);
					c.sendMessage("Task complete! You receive: " + points + " slayer points. Report back to the Slayer master!");
				}
			}
		}
	}
	/**
	 * Resets players in combat
	 */
	public static NPC getNpc(int npcType) {
		for (NPC npc : npcs)
			if (npc != null && npc.npcType == npcType)
				return npc;
		return null;
	}

	public static void spawnNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.maximumHealth = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
	}

	public void resetPlayersInCombat(int i) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null)
				if (PlayerHandler.players[j].underAttackBy2 == i)
					PlayerHandler.players[j].underAttackBy2 = 0;
		}
	}

	public static NPC getNpc(int npcType, int x, int y) {
		for (NPC npc : npcs)
			if (npc != null && npc.npcType == npcType && npc.absX == x && npc.absY == y)
				return npc;
		return null;
	}
	
	public static NPC getNpc(int npcType, int x, int y, int height) {
		for (NPC npc : npcs) {
			if (npc != null && npc.npcType == npcType && npc.absX == x && npc.absY == y
					&& npc.heightLevel == height) {
				return npc;
			}
		}
		return null;
	}

	/**
	 * Npc names
	 **/

	public String getNpcName(int npcId) {
		if (npcId <= -1) {
			return "None";
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return "None";
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcName();
	}

	/**
	 * Npc Follow Player
	 **/

	public int GetMove(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	public boolean followPlayer(int i) {
		switch (npcs[i].npcType) {
		case 2042:
		case 2043:
		case 2044:
		case 3998:
		case 497:
		case 2892:
		case 2894:
		case 1739:
		case 1740:
		case 1741:
		case 1742:
			return false;
		}
		return true;
	}

	public void followPlayer(int i, int playerId) {
		if (PlayerHandler.players[playerId] == null) {
			return;
		}
		Player player = PlayerHandler.players[playerId];
		if (PlayerHandler.players[playerId].respawnTimer > 0) {
			npcs[i].facePlayer(0);
			npcs[i].randomWalk = true;
			npcs[i].underAttack = false;
			return;
		}
		if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
			if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
				npcs[i].killerId = 0;
				return;
			}
		}
		if (Boundary.isIn(npcs[i], Zulrah.BOUNDARY) && (npcs[i].npcType >= 2042 && npcs[i].npcType <= 2044 || npcs[i].npcType == 6720)) {
			return;
		}
		if (!followPlayer(i)) {
			npcs[i].facePlayer(playerId);
			return;
		}
		if (npcs[i].npcType >= 1739 && npcs[i].npcType <= 1742) {
			return;
		}
		int playerX = PlayerHandler.players[playerId].absX;
		int playerY = PlayerHandler.players[playerId].absY;
		npcs[i].randomWalk = false;
		if (goodDistance(npcs[i].getX(), npcs[i].getY(), playerX, playerY, distanceRequired(i)))
			return;
		if ((npcs[i].spawnedBy > 0)
				|| ((npcs[i].absX < npcs[i].makeX + Config.NPC_FOLLOW_DISTANCE) && (npcs[i].absX > npcs[i].makeX - Config.NPC_FOLLOW_DISTANCE)
						&& (npcs[i].absY < npcs[i].makeY + Config.NPC_FOLLOW_DISTANCE) && (npcs[i].absY > npcs[i].makeY - Config.NPC_FOLLOW_DISTANCE))) {
			if (npcs[i].heightLevel == PlayerHandler.players[playerId].heightLevel) {
				if (PlayerHandler.players[playerId] != null && npcs[i] != null) {
					NPCDumbPathFinder.follow(npcs[i], player);
				}
			}
		} else {
			npcs[i].facePlayer(0);
			npcs[i].randomWalk = true;
			npcs[i].underAttack = false;
		}
	}

	public void loadSpell(Player player, int i) {
		int chance = 0;
		switch (npcs[i].npcType) {
			case 2218:
				npcs[i].projectileId = 48;
				npcs[i].endGfx = 1206;
				npcs[i].attackType = 1;
				break;
			case 2217:
				npcs[i].projectileId = 1203;
				npcs[i].endGfx = 1204;
				npcs[i].attackType = 2;
				break;
				
			case 2042:
				chance = 1;
				if (player != null) {
					if (player.getZulrahEvent().getStage() == 9) {
						chance = 2;
					}
				}
				chance = Misc.random(chance);
				npcs[i].setFacePlayer(true);
				if (chance < 2) {
					npcs[i].attackType = 1;
					npcs[i].projectileId = 97;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					npcs[i].attackTimer = 4;
				} else {
					npcs[i].attackType = 2;
					npcs[i].projectileId = 156;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					npcs[i].attackTimer = 4;
				}
				break;
				
			case 2044:
				npcs[i].setFacePlayer(true);
				if (Misc.random(3) > 0) {
					npcs[i].attackType = 2;
					npcs[i].projectileId = 1046;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					npcs[i].attackTimer = 4;
				} else {
					npcs[i].attackType = 1;
					npcs[i].projectileId = 1044;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					npcs[i].attackTimer = 4;
				}
				break;
				
			case 2043:
				npcs[i].setFacePlayer(false);
				npcs[i].turnNpc(player.getX(), player.getY());
				npcs[i].targetedLocation = new Location3D(player.getX(), player.getY(), player.heightLevel);
				npcs[i].attackType = 0;
				npcs[i].attackTimer = 9;
				npcs[i].hitDelayTimer = 6;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				break;
			
		case 6611:
		case 6612:
			chance = Misc.random(100);
			if (chance < 25) {
				npcs[i].attackType = 2;
				npcs[i].attackTimer = 7;
				npcs[i].hitDelayTimer = 4;
				createVetionSpell(npcs[i], player);
			} else if (chance > 90 && System.currentTimeMillis() - npcs[i].lastSpecialAttack > 15_000) {
				npcs[i].attackType = 4;
				npcs[i].attackTimer = 5;
				npcs[i].hitDelayTimer = 2;
				npcs[i].lastSpecialAttack = System.currentTimeMillis();
			} else {
				npcs[i].attackType = 0;
				npcs[i].attackTimer = 5;
				npcs[i].hitDelayTimer = 2;
			}
			break;
		case 6528:
			if (Misc.random(10) > 0) {
				npcs[i].attackType = 2;
				npcs[i].gfx100(194);
				npcs[i].projectileId = 195;
				npcs[i].endGfx = 196;
			} else {
				npcs[i].attackType = 4;
				npcs[i].gfx100(194);
				npcs[i].projectileId = 195;
				npcs[i].endGfx = 576;

			}
			break;
		case 6610:
			if (Misc.random(15) > 0) {
				npcs[i].attackType = 2;
				npcs[i].gfx100(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			} else {
				npcs[i].attackType = 4;
				npcs[i].gfx0(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			}
			break;

		case 6609:
			if (player != null) {
				int randomHit = Misc.random(20);
				boolean distance = !player.goodDistance(npcs[i].absX, npcs[i].absY, player.getX(), player.getY(), 5);
				if (randomHit < 15 && !distance) {
					npcs[i].attackType = 0;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
				} else if (randomHit >= 15 && randomHit < 20 || distance) {
					npcs[i].attackType = 2;
					npcs[i].projectileId = 395;
					npcs[i].endGfx = 431;
				} else {
					npcs[i].attackType = 4;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
				}
			}
			break;
		case 497:
		case 3998:
			npcs[i].attackType = 2;
			if (Misc.random(5) > 0 && npcs[i].npcType == 3998 || npcs[i].npcType == 497) {
				npcs[i].gfx0(161);
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			} else {
				npcs[i].gfx0(155);
				npcs[i].projectileId = 156;
				npcs[i].endGfx = 157;
			}
			break;
		case 2892:
			npcs[i].projectileId = 94;
			npcs[i].attackType = 2;
			npcs[i].endGfx = 95;
			break;
		case 2894:
			npcs[i].projectileId = 298;
			npcs[i].attackType = 1;
			break;
		case 264:
		case 259:
		case 247:
		case 268:
		case 270:
		case 274:
		case 273:
		case 2919:
		int random2 = Misc.random(2);
		if (random2 == 0) {
			npcs[i].projectileId = 393;
			npcs[i].endGfx = 430;
			npcs[i].attackType = 3;
		} else {
			npcs[i].attackType = 0;
			npcs[i].projectileId = -1;
			npcs[i].endGfx = -1;
		}
		break;
		case 6560:
			int random = Misc.random(100);
			int distance = player.distanceToPoint(npcs[i].absX, npcs[i].absY);
			if (random >= 60 && random < 65) {
				npcs[i].projectileId = 394; // green
				npcs[i].endGfx = 429;
				npcs[i].attackType = 3;
			} else if (random >= 65 && random < 75) {
				npcs[i].projectileId = 395; // white
				npcs[i].endGfx = 431;
				npcs[i].attackType = 3;
			} else if (random >= 75 && random < 80) {
				npcs[i].projectileId = 396; // blue
				npcs[i].endGfx = 428;
				npcs[i].attackType = 3;
			} else if (random >= 80 && distance <= 4) {
				npcs[i].projectileId = -1; // melee
				npcs[i].endGfx = -1;
				npcs[i].attackType = 0;
			} else {
				npcs[i].projectileId = 393; // red
				npcs[i].endGfx = 430;
				npcs[i].attackType = 3;
			}
			break;
		// arma npcs
		case 2561:
			npcs[i].attackType = 0;
			break;
		case 2560:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1190;
			break;
		case 2559:
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1203;
			break;
		case 2558:
			random = Misc.random(1);
			npcs[i].attackType = 1 + random;
			if (npcs[i].attackType == 1) {
				npcs[i].projectileId = 1197;
			} else {
				npcs[i].attackType = 2;
				npcs[i].projectileId = 1198;
			}
			break;
		// sara npcs
		case 2562: // sara
			random = Misc.random(1);
			if (random == 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = 1224;
				npcs[i].projectileId = -1;
			} else if (random == 1)
				npcs[i].attackType = 0;
			break;
		case 2563: // star
			npcs[i].attackType = 0;
			break;
		case 2564: // growler
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1203;
			break;
		case 2565: // bree
			npcs[i].attackType = 1;
			npcs[i].projectileId = 9;
			break;
		case 2551:
			npcs[i].attackType = 0;
			break;
		case 2552:
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1203;
			break;
		case 2553:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1206;
			break;
		case 2025:
			npcs[i].attackType = 2;
			int r = Misc.random(3);
			if (r == 0) {
				npcs[i].gfx100(158);
				npcs[i].projectileId = 159;
				npcs[i].endGfx = 160;
			}
			if (r == 1) {
				npcs[i].gfx100(161);
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			}
			if (r == 2) {
				npcs[i].gfx100(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			}
			if (r == 3) {
				npcs[i].gfx100(155);
				npcs[i].projectileId = 156;
			}
			break;
		case 6581:// supreme
			npcs[i].attackType = 1;
			npcs[i].projectileId = 298;
			break;

		case 6580:// prime
			npcs[i].attackType = 2;
			npcs[i].projectileId = 162;
			npcs[i].endGfx = 477;
			break;

		case 2028:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 27;
			break;

		case 2054:
			int r2 = Misc.random(1);
			if (r2 == 0) {
				npcs[i].attackType = 1;
				npcs[i].gfx100(550);
				npcs[i].projectileId = 551;
				npcs[i].endGfx = 552;
			} else {
				npcs[i].attackType = 2;
				npcs[i].gfx100(553);
				npcs[i].projectileId = 554;
				npcs[i].endGfx = 555;
			}
			break;
		case 3163:
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1190;
			npcs[i].endGfx = -1;
			break;
		case 3164:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1190;
			npcs[i].endGfx = -1;
			break;
		case 3165:
			npcs[i].attackType = 0;
			npcs[i].projectileId = 1190;
			npcs[i].endGfx = -1;
			break;
		case 6257:// saradomin strike
			npcs[i].attackType = 2;
			npcs[i].endGfx = 76;
			break;
		case 6221:// zamorak strike
			npcs[i].attackType = 2;
			npcs[i].endGfx = 78;
			break;
		case 6231:// arma
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1199;
			break;
		case 6573:// kree
			random = Misc.random(10);
			if (random < 2) {
				npcs[i].attackType = 2;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else if (random > 1 && random < 10){
				npcs[i].attackType = 1;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;
		// sara npcs
		case 6576:
			random = Misc.random(15);
			if (random > 0 && random < 7) {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
			} else if (random >= 7){
				npcs[i].attackType = 2;
				npcs[i].projectileId = 1211;
			} else if (random == 0) {
				npcs[i].attackType = 4;
				npcs[i].projectileId = -1;
			}
			break;
		case 6575: // sara
			random = Misc.random(3);
			if (random > 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = -1;
			} else if (random == 0) {
				npcs[i].attackType = 0;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = -1;
			}
			break;
		case 2206: // star
			npcs[i].attackType = 0;
			break;
		case 2207: // growler
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1203;
			break;
		case 2208: // bree
			npcs[i].attackType = 1;
			npcs[i].projectileId = 9;
			break;
		// bandos npcs
		case 6574:// bandos
			random = Misc.random(3);
			if (random == 0 || random == 1) {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].attackType = 1;
				npcs[i].projectileId = 1200;
				npcs[i].endGfx = -1;
			}
			break;
		case 3209:// cave horror
			random = Misc.random(3);
			if (random == 0 || random == 1) {
				npcs[i].attackType = 0;
			} else {
				npcs[i].attackType = 2;
			}
			break;
		case 3127:
			int r3 = 0;
			if (goodDistance(npcs[i].absX, npcs[i].absY, PlayerHandler.players[npcs[i].spawnedBy].absX,
					PlayerHandler.players[npcs[i].spawnedBy].absY, 1))
				r3 = Misc.random(2);
			else
				r3 = Misc.random(1);
			if (r3 == 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = 157;
				npcs[i].projectileId = 448;
			} else if (r3 == 1) {
				npcs[i].attackType = 1;
				npcs[i].endGfx = 451;
				npcs[i].projectileId = -1;
				npcs[i].hitDelayTimer = 6;
				npcs[i].attackTimer = 9;
			} else if (r3 == 2) {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;
		case 3125:
			npcs[i].attackType = 2;
			npcs[i].projectileId = 445;
			npcs[i].endGfx = 446;
			break;

		case 3121:
		case 2167:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 443;
			break;
		}
	}

	/**
	 * Distanced required to attack
	 **/
	public int distanceRequired(int i) {
		switch (npcs[i].npcType) {
		case Zulrah.SNAKELING:
			return 2;
		case 3163:
		case 3164:
		case 3165:
		case 2208:
			return 8;
		case 6574:
			return npcs[i].attackType == 0 ? npcs[i].getSize() : 6;
		case 2217:
		case 2218:
			return 6;
		case 2044:
		case 2043:
		case 2042:
			return 20;
		case 6611:
		case 6612:
			return npcs[i].attackType == 4 ? 8 : 3;
		case 6528:
		case 6610:
			return 8;
		case 3998:
		case 6609:
		case 497:
			return 10;
		case 2025:
		case 2028:
			return 6;
		case 2562:
		case 3131:
		case 3132:
		case 3130:
		case 2206:
		case 2207:
			return 2;
		case 6581:// dag kings
		case 6580:
		case 2054:// chaos ele
		case 3125:
		case 3121:
		case 2167:
		case 3127:
			return 8;
		case 6579:
			return 4;
		case 6560:
			return npcs[i].attackType == 3 ? 18 : 4;
		case 2552:
		case 2553:
		case 2556:
		case 2557:
		case 2558:
		case 2559:
		case 2560:
		case 2564:
		case 2565:
			return 9;
			// things around dags
		case 2892:
		case 2894:
			return 10;
		default:
			return 1;
		}
	}

	public int followDistance(int i) {
		if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
			return 8;
		}
		switch (npcs[i].npcType) {
		case 2045:
			return 20;
		case 1739:
		case 1740:
		case 1741:
		case 1742:
			return -1;
		case 6560:
			return 40;
		case 6611:
		case 6612:
			return 15;
		case 6574:
		case 2551:
		case 2562:
		case 2563:
			return 8;
		case 2054:
			return 10;
		case 6581:
		case 6580:
			return 35;
		case 6579:
			return 6;

		}
		return 0;

	}

	public int getProjectileSpeed(int i) {
		switch (npcs[i].npcType) {
		case 6581:
		case 6580:
		case 2054:
			return 85;

		case 3127:
			return 130;

		case 6560:
			return 90;

		case 2025:
			return 85;

		case 2028:
			return 80;

		default:
			return 85;
		}
	}

	/**
	 * NPC Attacking Player
	 **/

	public void attackPlayer(Player c, int i) {
		if (npcs[i].lastX != npcs[i].getX() || npcs[i].lastY != npcs[i].getY()) {
			return;
		}
		if (npcs[i] != null) {
			if (npcs[i].isDead)
				return;
			if (!npcs[i].inMulti() && npcs[i].underAttackBy > 0 && npcs[i].underAttackBy != c.playerId) {
				npcs[i].killerId = 0;
				return;
			}
			if (!npcs[i].inMulti() && (c.underAttackBy > 0 || (c.underAttackBy2 > 0 && c.underAttackBy2 != i))) {
				npcs[i].killerId = 0;
				return;
			}
			if (npcs[i].heightLevel != c.heightLevel) {
				npcs[i].killerId = 0;
				return;
			}
			if (npcs[i].npcType >= 1739 && npcs[i].npcType <= 1742 || 
					npcs[i].npcType > 6600 && npcs[i].npcType <= 6602) {
				npcs[i].killerId = 0;
				return;
			}
			if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
				if (!Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
					npcs[i].killerId = 0;
					return;
				}
			}
			npcs[i].facePlayer(c.playerId);
			if (goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(), c.getY(), distanceRequired(i))) {
				if (c.respawnTimer <= 0) {
					npcs[i].attackTimer = getNpcDelay(i);
					npcs[i].hitDelayTimer = getHitDelay(i);
					npcs[i].attackType = 0;
					loadSpell(c, i);
					if (npcs[i].attackType == 3) {
						npcs[i].hitDelayTimer += 2;
						c.getCombat().absorbDragonfireDamage();
					}
					if (multiAttacks(i)) {
						multiAttackGfx(i, npcs[i].projectileId);
						startAnimation(getAttackEmote(i), i);
						npcs[i].oldIndex = c.playerId;
						return;
					}
					if (npcs[i].projectileId > 0) {
						int nX = NPCHandler.npcs[i].getX() + offset(i);
						int nY = NPCHandler.npcs[i].getY() + offset(i);
						int pX = c.getX();
						int pY = c.getY();
						int offX = (nX - pX) * -1;
						int offY = (nY - pY) * -1;
						c.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(i), npcs[i].projectileId,
								getProjectileStartHeight(npcs[i].npcType, npcs[i].projectileId),
								getProjectileEndHeight(npcs[i].npcType, npcs[i].projectileId), -c.getId() - 1, 65);
					}
					c.underAttackBy2 = i;
					c.singleCombatDelay2.reset();
					npcs[i].oldIndex = c.playerId;
					startAnimation(getAttackEmote(i), i);
					c.getPA().removeAllWindows();
					if (c.teleporting) {
						c.startAnimation(65535);
						c.teleporting = false;
						c.isRunning = false;
						c.gfx0(-1);
						c.startAnimation(-1);
					}
				}
			}
		}
	}

	public int offset(int i) {
		switch (npcs[i].npcType) {
		case 2044:
			return 0;
		case 6611:
		case 6612:
			return 3;
		case 6610:
			return 2;
		case 6560:
			return 2;
		case 6581:
		case 6580:
			return 1;
		case 3127:
		case 3125:
			return 1;
		}
		return 0;
	}

	public boolean retaliates(int npcType) {
		return npcType < 3777 || npcType > 3780 && !(npcType >= 2440 && npcType <= 2446);
	}

	private boolean prayerProtectionIgnored(int npcId) {
		switch (npcs[npcId].npcType) {
		case 6611:
		case 6612:
			return npcs[npcId].attackType == 2 ? true : false;
		case 6609:
			return npcs[npcId].attackType == 2 || npcs[npcId].attackType == 4 ? true : false;
		}
		return false;
	}

	public void handleSpecialEffects(Player c, int i, int damage) {
		if (npcs[i].npcType == 2892 || npcs[i].npcType == 2894) {
			if (damage > 0) {
				if (c != null) {
					if (c.playerLevel[5] > 0) {
						c.playerLevel[5]--;
						c.getPA().refreshSkill(5);
						//c.getPA().appendPoison(12);
					}
				}
			}
		}

	}

	public static void startAnimation(int animId, int i) {
		npcs[i].animNumber = animId;
		npcs[i].animUpdateRequired = true;
		npcs[i].updateRequired = true;
	}

	public NPC[] getNPCs() {
		return npcs;
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY - playerY <= distance && objectY - playerY >= -distance));
	}

	public int getMaxHit(int i) {
		switch (npcs[i].npcType) {
		case 2042:
		case 2043:
		case 2044:
			return 41;
		case 6560:
			return npcs[i].attackType == 3 ? 50 : 20;
		case 2208:
		case 2207:
		case 2206:
			return 16;
		case 6576:
			return npcs[i].attackType == 0 ? 60 : npcs[i].attackType == 4 ? 49 : 30;
		case 6611:
		case 6612:
			return npcs[i].attackType == 0 ? 30 : npcs[i].attackType == 2 ? 34 : 46;
		case 6528:
			return npcs[i].attackType == 2 ? 40 : 50;
		case 6610:
			return 30;
		case 6609:
			return npcs[i].attackType == 4 ? 3 : npcs[i].attackType == 2 ? 60 : 40;
		case 2558:
			return npcs[i].attackType == 2 ? 38 : 68;
		case 2562:
			return 31;
		case 6574:
			return npcs[i].attackType == 0 ? 65 : 35;
		case 6573:
			return npcs[i].attackType == 1 ? 71 : npcs[i].attackType == 2 ? 21 : 15;
		case 1158:
			if (npcs[i].attackType == 2)
				return 30;
			else
				return 21;
		case 1160:
			if (npcs[i].attackType == 2 || npcs[i].attackType == 1)
				return 30;
			else
				return 21;
		case 6575:
			return 27;
		}
		return npcs[i].maxHit == 0 ? 1 : npcs[i].maxHit;
	}

	public boolean loadAutoSpawn(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("./" + FileName));
		} catch (FileNotFoundException fileex) {
			Misc.println(FileName + ": file not found.");
			return false;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			Misc.println(FileName + ": error loading file.");
			return false;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("spawn")) {
					newNPC(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]),
							Integer.parseInt(token3[4]), getNpcListHP(Integer.parseInt(token3[0])), Integer.parseInt(token3[5]),
							Integer.parseInt(token3[6]), Integer.parseInt(token3[7]));

				}
			} else {
				if (line.equals("[ENDOFSPAWNLIST]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}
		return false;
	}

	public int getNpcListHP(int npcId) {
		if (npcId <= -1) {
			return 0;
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return 0;
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcHealth();

	}

	public String getNpcListName(int npcId) {
		if (npcId <= -1) {
			return "None";
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return "None";
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcName();
	}
	
	private void loadNPCSizes(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new RuntimeException("ERROR: "+fileName+" does not exist.");
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				int npcId, size;
				try {
					npcId = Integer.parseInt(line.split("\t")[0]);
					size = Integer.parseInt(line.split("\t")[1]);
					if (npcId > -1 && size > -1) {
						if (NPCDefinitions.getDefinitions()[npcId] == null) {
							NPCDefinitions.create(npcId, "None", 0, 0, size);
						} else {
							NPCDefinitions.getDefinitions()[npcId].setSize(size);
						}
					}
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean loadNPCList(String fileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		File file = new File("./" + fileName);
		if (!file.exists()) {
			throw new RuntimeException("ERROR: NPC Configuration file does not exist.");
		}
		try (BufferedReader characterfile = new BufferedReader(new FileReader("./" + fileName))) {
			while ((line = characterfile.readLine()) != null && !line.equals("[ENDOFNPCLIST]")) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token2_2 = token2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token3 = token2_2.split("\t");
					if (token.equals("npc")) {
						newNPCList(Integer.parseInt(token3[0]), token3[1], Integer.parseInt(token3[2]), Integer.parseInt(token3[3]));
						System.out.println("Loaded NPC Definitions");
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		return true;
	}

	public static void spawnNpc2(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.maximumHealth = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
	}

	public static NPCDef[] getNpcDef() {
		return npcDef;
	}
	
	private ArrayList<int[]> vetionSpellCoordinates = new ArrayList<>(3);
	
	private void createVetionSpell(NPC npc, Player player) {
		if (player == null) {
			return;
		}
		int x = player.getX();
		int y = player.getY();
		vetionSpellCoordinates.add(new int[] {x, y});
		for (int i = 0; i < 2; i++) {
			vetionSpellCoordinates.add(new int[] {(x - 1) + Misc.random(3), (y - 1) + Misc.random(3)});
		}
		for (int[] point : vetionSpellCoordinates) {
			int nX = npc.absX + 2;
			int nY = npc.absY + 2;
			int x1 = point[0] + 1;
			int y1 = point[1] + 2;
			int offY = (nX - x1) * -1;
			int offX = (nY - y1) * -1;
			player.getPA().createPlayersProjectile(nX, nY, offX, offY, 40, getProjectileSpeed(npc.npcId), 280,
					31, 0, -1, 5);
			
		}
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				for (int[] point : vetionSpellCoordinates) {
					int x2 = point[0];
					int y2 = point[1];
					player.getPA().createPlayersStillGfx(281, x2, y2, 0, 5);
				}
				vetionSpellCoordinates.clear();
				container.stop();
			}
			
		}, 4);
	}
	
	
	public static void kill(int npcType, int height) {
		Arrays.asList(npcs).stream().filter(Objects::nonNull).filter(n -> n.npcType == npcType
				&& n.heightLevel == height).forEach(npc -> npc.isDead = true);
	}
	
	private void createVetionEarthquake(Player player) {
		player.getPA().shakeScreen(3, 2, 3, 2);
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				player.getPA().sendFrame107();
				container.stop();
			}
			
		}, 4);
	}
	
}
