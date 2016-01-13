package ab.model.players.combat.magic;

import ab.model.players.Player;
import ab.model.players.PlayerHandler;

public class MagicData {
	public static int getMagicGraphic(Player c, int i) {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 12891:
				return PlayerHandler.players[i].freezeTimer > 0 ? 369 : 369;
		}
		return c.MAGIC_SPELLS[c.oldSpellId][5];
	}

	public static boolean multiSpells(Player c) {
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 12891:
			case 12881:
			case 13011:
			case 13023:
			case 12919: // blood spells
			case 12929:
			case 12963:
			case 12975:
			return true;
		}
		return false;
	}

	public static int getStartGfxHeight(Player c) {
		switch(c.MAGIC_SPELLS[c.spellId][0]) {
			case 12871:
			case 12891:
			return 0;
			
			default:
			return 100;
		}
	}

	public static int getEndGfxHeight(Player c) {
		switch(c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 12987:	
			case 12901:		
			case 12861:
			case 12445:
			case 1192:
			case 13011:
			case 12919:
			case 12881:
			case 12999:
			case 12911:
			case 12871:
			case 13023:
			case 12929:
			case 12891:
			return 0;
			
			default:
			return 100;
		}
	}

	public static boolean godSpells(Player c) {
		switch(c.MAGIC_SPELLS[c.spellId][0]) {	
			case 1190:
			return true;
			
			case 1191:
			return true;
			
			case 1192:
			return true;
			
			default:
			return false;
		}
	}

	public static int getStaffNeeded(Player c) {
		switch(c.MAGIC_SPELLS[c.spellId][0]) {
			case 1539:
			return 1409;
			
			case 12037:
			return 4170;
			
			case 1190:
			return 2415;
			
			case 1191:
			return 2416;
			
			case 1192:
			return 2417;
			
			default:
			return 0;
		}
	}

	public static int getStartDelay(Player c) {
		switch(c.MAGIC_SPELLS[c.spellId][0]) {
			case 1539:
			return 60;
			
			default:
			return 53;
		}
	}

	public static int getEndHeight(Player c) {
		if (c.spellId == 52) {
			return 25;
		}
		switch(c.MAGIC_SPELLS[c.spellId][0]) {
			case 1562: // stun
			return 10;
			
			case 12939: // smoke rush
			return 20;
			
			case 12987: // shadow rush
			return 28;
			
			case 12861: // ice rush
			return 10;
			
			case 12951:  // smoke blitz
			return 28;
			
			case 12999: // shadow blitz
			return 15;
			
			case 12911: // blood blitz
			return 10;
				
			default:
			return 31;
		}
	}

	public static int getStartHeight(Player c) {
		switch(c.MAGIC_SPELLS[c.spellId][0]) {
			case 1562: // stun
			return 25;
			
			case 12939:// smoke rush
			return 35;
			
			case 12987: // shadow rush
			return 38;
			
			case 12861: // ice rush
			return 15;
			
			case 12951:  // smoke blitz
			return 38;
			
			case 12999: // shadow blitz
			return 25;
			
			case 12911: // blood blitz
			return 25;
			
			default:
			return 43;
		}
	}

	public static int getFreezeTime(Player c) {
		switch(c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 1572:
			case 12861: // ice rush
			return 10;
						
			case 1582:
			case 12881: // ice burst
			return 17;
			
			case 1592:
			case 12871: // ice blitz
			return 25;
			
			case 12891: // ice barrage
			return 33;
			
			default:
			return 0;
		}
	}

}