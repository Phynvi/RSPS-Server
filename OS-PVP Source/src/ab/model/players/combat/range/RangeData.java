package ab.model.players.combat.range;

import ab.model.npcs.NPCHandler;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;

public class RangeData {

	public static void fireProjectileNpc(Player c) {
		if(c.oldNpcIndex > 0) {
			if(NPCHandler.npcs[c.oldNpcIndex] != null) {
				c.projectileStage = 2;
				int pX = c.getX();
				int pY = c.getY();
				int nX = NPCHandler.npcs[c.oldNpcIndex].getX();
				int nY = NPCHandler.npcs[c.oldNpcIndex].getY();
				int offX = (pY - nY)* -1;
				int offY = (pX - nX)* -1;
				if (c.lastWeaponUsed == 12926) {
					c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 25, 10, c.oldNpcIndex + 1, 45);
				} else {
					c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 43, 31, c.oldNpcIndex + 1, c.getCombat().getProjectileShowDelay());
				}	
				if (c.getCombat().usingDbow())
					c.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 60, 31,  c.oldNpcIndex + 1, c.getCombat().getStartDelay(), 35);
			}
		}
	}

	public static void fireProjectilePlayer(Player c) {
		if(c.oldPlayerIndex > 0) {
			if(PlayerHandler.players[c.oldPlayerIndex] != null) {
				c.projectileStage = 2;
				int pX = c.getX();
				int pY = c.getY();
				int oX = PlayerHandler.players[c.oldPlayerIndex].getX();
				int oY = PlayerHandler.players[c.oldPlayerIndex].getY();
				int offX = (pY - oY)* -1;
				int offY = (pX - oX)* -1;	
				if (!c.msbSpec && c.lastWeaponUsed != 12926) {
					c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 43, 31, - c.oldPlayerIndex - 1, c.getCombat().getStartDelay());
				} else if (c.lastWeaponUsed == 12926) {
						c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 25, 10, c.oldNpcIndex + 1, 45);
				} else if (c.msbSpec) {
					c.getPA().createPlayersProjectile2(pX, pY, offX, offY, 50, c.getCombat().getProjectileSpeed(), c.getCombat().getRangeProjectileGFX(), 43, 31, - c.oldPlayerIndex - 1, c.getCombat().getStartDelay(), 10);
					c.msbSpec = false;
				}
				if (c.getCombat().usingDbow())
					c.getPA().createProjectile3(pY, pX, offY, offX, c.getCombat().getRangeProjectileGFX(), 53, 31, 100, - c.oldPlayerIndex - 1);

				}
			}
		}

	public static int getRangeStr(int i) {
		int str = 0;
		int[][] data = {
			{877,  10}, {9140, 46}, {9145, 36}, {9141, 64}, 
			{9142, 82}, {9143,100}, {9144,115}, {9236, 14}, 
			{9237, 30}, {9238, 48}, {9239, 66}, {9240, 83}, 
			{9241, 85}, {9242,103}, {9243,105}, {9244,117}, 
			{9245,120}, {882, 7}, {884, 10}, {886, 16}, 
			{888, 22}, {890, 31}, {892, 45}, {4740, 44}, 
			{11212, 60}, {806, 1}, {807, 3}, {808, 4}, 
			{809, 7}, {810,10}, {811,14}, {11230, 18},
			{864, 3},  {863, 4}, {865, 7}, {866, 10}, 
			{867, 14}, {868, 24}, {825, 6}, {826,10}, 
			{827,12}, {828,18}, {829,28}, {830,42},
			{800, 5}, {801, 7}, {802,11}, {803,16}, 
			{804,23}, {805,36}, {9976, 0}, {9977, 15},
			{4212, 70}, {4214, 70}, {4215, 70}, {4216, 70},
			{4217, 70}, {4218, 70}, {4219, 70}, {4220, 70},
			{4221, 70}, {4222, 70}, {4223, 70}, {6522, 49},
			{10034, 15}, {12926, 48}
		};
		for(int l = 0; l < data.length; l++) {
			if(i == data[l][0]) {
				str = data[l][1];
			}
		}
		return str;
	}

	public static int getRangeStartGFX(Player c) {
		int str = -1;
		int[][] data = {
			//	KNIFES
			{863, 220}, {864, 219}, {865, 221}, {866, 223},
			{867, 224}, {868, 225}, {869, 222},

			//	DARTS
			{806, 1234}, {807, 1235}, {808, 1236}, 
			{809, 1238}, {810, 1239}, {811, 1240},
			{11230, 1242},

			//	JAVELIN
			{825, 206}, {826, 207}, {827, 208}, {828, 209},
			{829, 210}, {830, 211},

			//	AXES
			{800, 42}, {801, 43}, {802, 44}, {803, 45},
			{804, 46}, {805, 48},

			//	ARROWS
			{882, 19}, {884, 18}, {886, 20}, {888, 21},
			{890, 22}, {892, 24},

			//	CRYSTAL_BOW
			{4212, 250}, {4214, 250}, {4215, 250}, {4216, 250},
			{4217, 250}, {4218, 250}, {4219, 250}, {4220, 250},
			{4221, 250}, {4222, 250}, {4223, 250},
		};
		for(int l = 0; l < data.length; l++) {
			if(c.rangeItemUsed == data[l][0]) {
				str = data[l][1];
			}
		}
		if(c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766 || c.playerEquipment[3] == 12767 || c.playerEquipment[3] == 12768) {
			int[][] moreD = {
				{882, 1104}, {884, 1105}, {886, 1106}, {888, 1107},
				{890, 1108}, {892, 1109}, {11212, 1111},
			};
			for(int l = 0; l < moreD.length; l++) {
				if(c.playerEquipment[c.playerArrows] == moreD[l][0]) {
					str = moreD[l][1];
				}
			}
		}
		return str;
	}

	public static int getRangeProjectileGFX(Player c) {
		if (c.getItems().isWearingItem(12926)) {
			final int[][] DARTS = {
				{806, 226}, {807, 227}, {808, 228}, {809, 229},
				{810, 230}, {811, 231}, {11230, 1123}
			};
			for (int index = 0; index < DARTS.length; index++) {
				if (DARTS[index][0] == c.getToxicBlowpipeAmmo()) {
					return DARTS[index][1];
				}
			}
		}
		if (c.dbowSpec) {
			return c.playerEquipment[c.playerArrows] == 11212 ? 1099 : 1101;
		}
		if(c.bowSpecShot > 0) {
			switch(c.rangeItemUsed) {
				default:
				return 249;
			}
		}
		boolean castingMagic = (c.usingMagic || c.mageFollow || c.autocasting || c.spellId > 0);
		if(castingMagic) {
			return -1;
		}
		if (c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 11785)
			return 27;
		int str = -1;
		int[][] data = {
			//	KNIFES
			{863, 213}, {864, 212}, {865, 214}, {866, 216},
			{867, 217}, {868, 218}, {869, 215},

			//	DARTS
			{806, 226}, {807, 227}, {808, 228}, {809, 229},
			{810, 230}, {811, 231},

			//	JAVELINS
			{825, 200}, {826, 201}, {827, 202}, {828, 203},
			{829, 204}, {830, 205},

			//	AXES
			{800, 36}, {801, 35}, {802, 37}, {803, 38},
			{804, 39}, {805, 40},

			//	ARROWS
			{882, 10}, {884, 9}, {886, 11}, {888, 12},
			{890, 13}, {892, 15}, {11212, 1120},

			//	CHINCHOMPA
			{10033, 908}, {10034, 909},

			//	OTHERS
			{6522, 442}, {4740, 27},
			{4212, 249}, {4214, 249}, {4215, 249}, {4216, 249},
			{4217, 249}, {4218, 249}, {4219, 249}, {4220, 249},
			{4221, 249}, {4222, 249}, {4223, 249},
		};
		for(int l = 0; l < data.length; l++) {
			if(c.rangeItemUsed == data[l][0]) {
				str = data[l][1];
			}
		}
		return str;
	}

	public static int getRangeEndGFX(Player c) {
		int str = -1;
		int gfx = 0;
		int[][] data = {
			{10033, 157, 100}, {10034, 157, 100},
		};
		for(int l = 0; l < data.length; l++) {
			if(c.playerEquipment[c.playerWeapon] == data[l][0]) {
				str = data[l][1];
				gfx = data[l][2];
			}
		}
		if(gfx == 100) {
			c.rangeEndGFXHeight = true;
		}
		return str;
	}

	public static int correctBowAndArrows(Player c) {
		if (c.getCombat().properBolts())
			return -1;
		switch(c.playerEquipment[c.playerWeapon]) {
			
			case 839:
			case 841:
			return 882;
			
			case 843:
			case 845:
			return 884;
			
			case 847:
			case 849:
			return 886;
			
			case 851:
			case 853:
			return 888;        
			
			case 855:
			case 857:
			return 890;
			
			case 859:
			case 861:
			case 12424:
			case 12788:
			case 4212:
			case 4213:
			case 4214:
			case 4215:
			case 4216:
			case 4217:
			case 4218:
			case 4219:
			case 4220:
			case 4221:
			case 4222:
			case 4223:
			case 4224:
			
			return 892;
			case 4734:
			case 4935:
			case 4936:
			case 4937:
			return 4740;
			
			case 11235:
			case 12765:
			case 12766:
			case 12767:
			case 12768:
			return 11212;
		}
		return -1;
	}

	public static int getProjectileSpeed(Player c) {
		if (c.dbowSpec)
			return 100;
		switch(c.playerEquipment[3]) {
			case 10033:
			case 10034:
				return 60;
		}
		return 70;
	}

	public static int getProjectileShowDelay(Player c) {
		int[] data = {
			806, 806, 808, 809, 810, 811,
			10033, 10034, 11230,
		};
		int str = 53;
		for(int i = 0; i < data.length; i++) {
			if(c.playerEquipment[c.playerWeapon] == data[i]) {
				str = 32;
			}
		}
		return str;
	}
}