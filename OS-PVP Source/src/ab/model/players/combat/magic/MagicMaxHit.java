package ab.model.players.combat.magic;

import ab.model.players.Player;

public class MagicMaxHit {

	public static int mageAttack(Player c) {
		int attackLevel = c.playerLevel[6];

		if (c.prayerActive[4]) {
			attackLevel = (int) (attackLevel * 1.05D);
		} else if (c.prayerActive[12]) {
			attackLevel = (int) (attackLevel * 1.1D);
		} else if (c.prayerActive[20]) {
			attackLevel = (int) (attackLevel * 1.15D);
		}

		return (int) (attackLevel + c.playerBonus[3] * 2.25D);
	}

	public static int mageDefence(Player c) {
		int defenceLevel = c.playerLevel[1] / 2 + c.playerLevel[6] / 2;
		if (c.prayerActive[4]) {
			defenceLevel = (int) (defenceLevel + c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05D);
		} else if (c.prayerActive[12]) {
			defenceLevel = (int) (defenceLevel + c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.1D);
		} else if (c.prayerActive[20]) {
			defenceLevel = (int) (defenceLevel + c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15D);
		}
		return defenceLevel + c.playerBonus[8] + c.playerBonus[8] / 4;
	}

	public static int magiMaxHit(Player c) {
		double damage = c.MAGIC_SPELLS[c.oldSpellId][6];
		double damageMultiplier = 1;
		switch (c.playerEquipment[c.playerWeapon]) {
			case 6914:
				damageMultiplier += .04;
				break;
				case 11907:
				case 11791:
				damageMultiplier += 0.10;
				break;
		}
		if (c.playerEquipment[c.playerAmulet] == 11144 && c.playerEquipment[c.playerWeapon] != 6914 && c.playerEquipment[c.playerWeapon] != 11791) {
			damageMultiplier += .10;
		} else if (c.playerEquipment[c.playerAmulet] == 11144 && c.playerEquipment[c.playerWeapon] == 11791) {
			damageMultiplier += .15;
		} else if (c.playerEquipment[c.playerAmulet] == 11144 && c.playerEquipment[c.playerWeapon] == 11907) {
			damageMultiplier += .15;
		} else if (c.playerEquipment[c.playerAmulet] == 11144 && c.playerEquipment[c.playerWeapon] == 12899) {
			damageMultiplier += .15;
		} else if (c.playerEquipment[c.playerAmulet] == 11144 && c.playerEquipment[c.playerWeapon] == 6914) {
			damageMultiplier += .12;
		}
		switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 12037:
				damage += c.playerLevel[6] / 10;
				break;
		}

		damage *= damageMultiplier;
		return (int)damage;
	}
}