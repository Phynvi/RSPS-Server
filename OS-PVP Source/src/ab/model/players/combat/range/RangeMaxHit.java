package ab.model.players.combat.range;

import ab.model.players.*;

public class RangeMaxHit extends RangeData {

	public static int calculateRangeDefence(Player c) {
		int defenceLevel = c.playerLevel[1];
		if (c.prayerActive[0]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[24]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.2;
		} else if (c.prayerActive[25]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return defenceLevel + c.playerBonus[9];
	}

	public static int calculateRangeAttack(Player c) {
		int rangeLevel = c.playerLevel[4];
		rangeLevel *= c.specAccuracy;
 		if (c.fullVoidRange()){
			rangeLevel += c.getLevelForXP(c.playerXP[c.playerRanged]) * 0.1;
		}
		if (c.prayerActive[3]) {
			rangeLevel *= 1.05;
		} else if (c.prayerActive[11]) {
			rangeLevel *= 1.10;
		} else if (c.prayerActive[19]) {
			rangeLevel *= 1.15;
		}
		if (c.fullVoidRange() && c.specAccuracy > 1.15) {
			rangeLevel *= 1.75;		
		}
	   	return (int) (rangeLevel + (c.playerBonus[4] * 2));
	}

	public static int maxHit(Player c) {
		int a = c.playerLevel[4];
		int d = getRangeStr(c.usingBow && c.playerEquipment[c.playerWeapon] != 12926 ? c.playerEquipment[c.playerArrows]
				: c.playerEquipment[c.playerWeapon]);
		double b = 1.00;
		if (c.prayerActive[3]) {
			b *= 1.05;
		} else if (c.prayerActive[11]) {
			b *= 1.10;
		} else if (c.prayerActive[19]) {
			b *= 1.15;
		}
		if (c.fullVoidRange()) {
			b *= 1.20;
		}
		double e = Math.floor(a * b);
		if(c.fightMode == 0) {
			e = (e + 3.0);
		}
		double darkbow = 1.0;
		if(c.usingSpecial) {
			if(c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766 || c.playerEquipment[3] == 12767 || c.playerEquipment[3] == 12768) {
				if(c.lastArrowUsed == 11212) {
					darkbow = 1.4;
				} else {
					darkbow = 1.3;
				}
			}
		}
		double max = (1.3 + e/10 + d/80 + e*d/640) * darkbow;
		return (int)max;
	}
}