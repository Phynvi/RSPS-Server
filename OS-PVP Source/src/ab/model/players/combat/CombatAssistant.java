package ab.model.players.combat;

import ab.model.items.ItemAssistant;
import ab.model.npcs.NPCHandler;
import ab.model.players.Player;
import ab.model.players.combat.magic.MagicData;
import ab.model.players.combat.magic.MagicExtras;
import ab.model.players.combat.magic.MagicMaxHit;
import ab.model.players.combat.magic.MagicRequirements;
import ab.model.players.combat.melee.CombatPrayer;
import ab.model.players.combat.melee.MeleeData;
import ab.model.players.combat.melee.MeleeExtras;
import ab.model.players.combat.melee.MeleeMaxHit;
import ab.model.players.combat.melee.MeleeRequirements;
import ab.model.players.combat.melee.MeleeSpecial;
import ab.model.players.combat.range.RangeData;
import ab.model.players.combat.range.RangeExtras;
import ab.model.players.combat.range.RangeMaxHit;

public class CombatAssistant {
	
	private Player c;

	public CombatAssistant(Player Client) {
		this.c = Client;
	}

	public int[][] slayerReqs = {
		{1648,5},{1612,15},{1643,45},{1618,50},
		{1624,65},{1610,75},{1613,80},{1615,85},
		{2783,90}
	};

	public boolean goodSlayer(int i) {
		for (int j = 0; j < slayerReqs.length; j++) {
			if (slayerReqs[j][0] == NPCHandler.npcs[i].npcType) {
				if (slayerReqs[j][1] > c.playerLevel[c.playerSlayer]) {
					c.sendMessage("You need a slayer level of " + slayerReqs[j][1] + " to harm this NPC.");
					return false;
				}
			}
		}
		return true;
	}
	public double strBonus;
	public double strengthBonus;
	public boolean kalphite1(int i) {
		switch (NPCHandler.npcs[i].npcType) {
			case 1158:
			return true;	
		}
		return false;	
	}
	
	public boolean kalphite2(int i) {
		switch (NPCHandler.npcs[i].npcType) {
			case 1160:
			return true;	
		}
		return false;	
	}
	
	public void absorbDragonfireDamage() {
		int shieldId = c.playerEquipment[c.playerShield];
		String shieldName = ItemAssistant.getItemName(shieldId).toLowerCase();
		if (shieldName.contains("dragonfire")) {
			int charges = c.getDragonfireShieldCharge();
			if (charges < 50) {
				c.setDragonfireShieldCharge(charges++);
				if (charges == 50) {
					c.sendMessage("<col=255>Your dragonfire shield has completely finished charging.");
				}
				c.startAnimation(6695);
				c.gfx0(1164);
				c.setDragonfireShieldCharge(charges);
				return;
			}
		}
		return;
	}

	public void resetPlayerAttack() {
		MeleeData.resetPlayerAttack(c);
	}
	
	public int getCombatDifference(int combat1, int combat2) {
		return MeleeRequirements.getCombatDifference(combat1, combat2);
	}

	public boolean checkReqs() {
		return MeleeRequirements.checkReqs(c);
	}
	
	public boolean checkMultiBarrageReqs(int i) {
		return MagicExtras.checkMultiBarrageReqs(c, i);
	}
	
	public int getRequiredDistance() {
		return MeleeRequirements.getRequiredDistance(c);
	}

	public void multiSpellEffectNPC(int npcId, int damage) {
		MagicExtras.multiSpellEffectNPC(c, npcId, damage);
	}

	public boolean checkMultiBarrageReqsNPC(int i) {
		return MagicExtras.checkMultiBarrageReqsNPC(i);
	}

	public void appendMultiBarrageNPC(int npcId, boolean splashed) {
		MagicExtras.appendMultiBarrageNPC(c, npcId, splashed);
	}

	public void attackNpc(int i) {
		AttackNPC.attackNpc(c, i);
	}

	public void delayedHit(final Player c, final int i) {
		AttackNPC.delayedHit(c, i);
	}

	public void applyNpcMeleeDamage(int i, int damageMask, int damage, int defence) {
		AttackNPC.applyNpcMeleeDamage(c, i, damageMask, damage, defence);
	}

	public void attackPlayer(int i) {
		AttackPlayer.attackPlayer(c, i);
	}

	public void playerDelayedHit(final Player c, final int i, final Damage damage) {
		AttackPlayer.playerDelayedHit(c, i, damage);
	}

	public void applyPlayerMeleeDamage(int i, int damageMask, int damage, Hitmark hitmark){
		AttackPlayer.applyPlayerMeleeDamage(c, i, damageMask, damage, hitmark);
	}

	public void applyPlayerHit(Player c, final int i, final Damage damage) {
		AttackPlayer.applyPlayerHit(c, i, damage);
	}
	
	public void fireProjectileNpc() {
		RangeData.fireProjectileNpc(c);
	}

	public void fireProjectilePlayer() {
		RangeData.fireProjectilePlayer(c);
	}
	
	public boolean usingCrystalBow() {
		return c.playerEquipment[c.playerWeapon] >= 4212 && c.playerEquipment[c.playerWeapon] <= 4223;	
	}
	
	public boolean multis() {
		return MagicData.multiSpells(c);
	}

	public void appendMultiBarrage(int playerId, boolean splashed) {
		MagicExtras.appendMultiBarrage(c, playerId, splashed);
	}
	
	public void multiSpellEffect(int playerId, int damage) {					
		MagicExtras.multiSpellEffect(c, playerId, damage);
	}

	public void applySmite(int index, int damage) {
		MeleeExtras.applySmite(c, index, damage);
	}

	public boolean usingDbow() {
		return c.playerEquipment[c.playerWeapon] == 11235 ||
				c.playerEquipment[c.playerWeapon] == 12765 || 
				c.playerEquipment[c.playerWeapon] == 12766 ||
				c.playerEquipment[c.playerWeapon] == 12767 ||
				c.playerEquipment[c.playerWeapon] == 12768;
	}
	
	public boolean usingHally() {
		return MeleeData.usingHally(c);
	}

	public void getPlayerAnimIndex(String weaponName){
		MeleeData.getPlayerAnimIndex(c, weaponName);
	}
	
	public int getWepAnim(String weaponName) {
		return MeleeData.getWepAnim(c, weaponName);
	}

	public int getBlockEmote() {
		return MeleeData.getBlockEmote(c);
	}
	
	public int getAttackDelay(String s) {
		return MeleeData.getAttackDelay(c, s);
	}

	public int getHitDelay(int i, String weaponName) {
		return MeleeData.getHitDelay(c, i, weaponName);
	}

	public int npcDefenceAnim(int i) {
		return MeleeData.npcDefenceAnim(i);
	}
	
	public int calculateMeleeAttack() {
		return MeleeMaxHit.calculateMeleeAttack(c);
	}

	public int bestMeleeAtk() {
		return MeleeMaxHit.bestMeleeAtk(c);
	}
	
	public int calculateMeleeMaxHit() {
		return (int)MeleeMaxHit.calculateBaseDamage(c, c.usingSpecial);
	}

	public int calculateMeleeDefence() {
		return MeleeMaxHit.calculateMeleeDefence(c);
	}
	
	public int bestMeleeDef() {
		return MeleeMaxHit.bestMeleeDef(c);
	}

	public void appendVengeanceNPC(int otherPlayer, int damage) {
		MeleeExtras.appendVengeanceNPC(c, otherPlayer, damage);
	}

	public void appendVengeance(int otherPlayer, int damage) {
		MeleeExtras.appendVengeance(c, otherPlayer, damage);
	}

	public void applyRecoilNPC(int damage, int i) {
		MeleeExtras.applyRecoilNPC(c, damage, i);
	}

	public void applyRecoil(int damage, int i) {
		MeleeExtras.applyRecoil(c, damage, i);
	}

	public void removeRecoil(Player c) {
		MeleeExtras.removeRecoil(c);
	}

	public void handleGmaulPlayer() {
		MeleeExtras.graniteMaulSpecial(c);
	}

	public void activateSpecial(int weapon, int i){
		MeleeSpecial.activateSpecial(c, weapon, i);
	}

	public boolean checkSpecAmount(int weapon) {
		return MeleeSpecial.checkSpecAmount(c, weapon);
	}
	
	public int calculateRangeAttack() {
		return RangeMaxHit.calculateRangeAttack(c);
	}
	
	public int calculateRangeDefence() {
		return RangeMaxHit.calculateRangeDefence(c);
	}

	public int rangeMaxHit() {
		return RangeMaxHit.maxHit(c);
	}

	public int getRangeStr(int i) {
		return RangeData.getRangeStr(i);
	}

	public int getRangeStartGFX() {
		return RangeData.getRangeStartGFX(c);
	}

	public int getRangeProjectileGFX() {
		return RangeData.getRangeProjectileGFX(c);
	}

	public int correctBowAndArrows() {
		return RangeData.correctBowAndArrows(c);
	}

	public int getProjectileShowDelay() {
		return RangeData.getProjectileShowDelay(c);
	}

	public int getProjectileSpeed() {
		return RangeData.getProjectileSpeed(c);
	}

	public void crossbowSpecial(Player c, int i) {
		RangeExtras.crossbowSpecial(c, i);
	}

	public void appendMutliChinchompa(int npcId) {
		RangeExtras.appendMutliChinchompa(c, npcId);
	}

	public boolean properBolts() {
		return usingBolts(c.playerEquipment[c.playerArrows]);
	}

	public boolean usingBolts(int i) {
		return (i >= 9140 && i <= 9145) || (i >= 9236 && i <= 9245);
	}
	
	public int mageAtk() {
		return MagicMaxHit.mageAttack(c);
	}

	public int mageDef() {
		return MagicMaxHit.mageDefence(c);
	}

	public int magicMaxHit() {
		return MagicMaxHit.magiMaxHit(c);
	}
	
	public boolean wearingStaff(int runeId) {
		return MagicRequirements.wearingStaff(c, runeId);
	}
	
	public boolean checkMagicReqs(int spell) {
		return MagicRequirements.checkMagicReqs(c, spell);
	}

	public int getMagicGraphic(Player c, int i) {
		return MagicData.getMagicGraphic(c, i);
	}
	
	public int getFreezeTime() {
		return MagicData.getFreezeTime(c);
	}

	public int getStartHeight() {
		return MagicData.getStartHeight(c);
	}

	public int getEndHeight() {
		return MagicData.getEndHeight(c);
	}
	
	public int getStartDelay() {
		return MagicData.getStartDelay(c);
	}
	
	public int getStaffNeeded() {
		return MagicData.getStaffNeeded(c);
	}
	
	public boolean godSpells() {
		return MagicData.godSpells(c);
	}
		
	public int getEndGfxHeight() {
		return MagicData.getEndGfxHeight(c);
	}
	
	public int getStartGfxHeight() {
		return MagicData.getStartGfxHeight(c);
  	}

	public void handlePrayerDrain() {
		CombatPrayer.handlePrayerDrain(c);
	}
	
	public void reducePrayerLevel() {
		CombatPrayer.reducePrayerLevel(c);
	}
	
	public void resetPrayers() {
		CombatPrayer.resetPrayers(c);
	}

	public void activatePrayer(int i) {
		CombatPrayer.activatePrayer(c, i);
	}
	
	public void checkVenomousItems() {
		if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == 3) {
			c.setToxicBlowpipeAmmoAmount(c.getToxicBlowpipeAmmoAmount() - 1);
			c.setToxicBlowpipeCharge(c.getToxicBlowpipeCharge() - 1);
			if (c.getToxicBlowpipeAmmoAmount() % 500 == 0 && c.getToxicBlowpipeAmmoAmount() > 0) {
				c.sendMessage("<col=255>You have "+c.getToxicBlowpipeAmmoAmount()+" ammo in your blow pipe remaining.</col>");
			}
			if (c.getToxicBlowpipeAmmoAmount() <= 0 && c.getToxicBlowpipeCharge() <= 0) {
				c.sendMessage("Your toxic blowpipe has lost all charge.");
				c.getItems().wearItem(-1, 0, 3);
				if (!c.getItems().addItem(12924, 1)) {
					c.getItems().addItemToBank(12924, 1);
				}
				c.setToxicBlowpipeAmmo(0);
				c.setToxicBlowpipeAmmoAmount(0);
				c.setToxicBlowpipeCharge(0);
			}
		}
		if (c.getItems().isWearingItem(12931) && c.getItems().getWornItemSlot(12931) == c.playerHat) {
			c.setSerpentineHelmCharge(c.getSerpentineHelmCharge() - 1);
			if (c.getSerpentineHelmCharge() % 500 == 0 && c.getSerpentineHelmCharge() != 0) {
				c.sendMessage("<col=255>You have "+c.getSerpentineHelmCharge()+" charges remaining in your serpentine helm.</col>");
			}
			if (c.getSerpentineHelmCharge() <= 0) {
				c.sendMessage("Your serpentine helm has lost all of it's charge.");
				c.getItems().wearItem(-1, 0, c.playerHat);
				if (!c.getItems().addItem(12929, 1)) {
					c.getItems().addItemToBank(12929, 1);
				}
				c.setSerpentineHelmCharge(0);
			}
		}
		if (c.getItems().isWearingItem(12904) && c.getItems().getWornItemSlot(12904) == c.playerWeapon) {
			c.setToxicStaffOfDeadCharge(c.getToxicStaffOfDeadCharge() - 1);
			if (c.getToxicStaffOfDeadCharge() % 500 == 0 && c.getToxicStaffOfDeadCharge() != 0) {
				c.sendMessage("<col=255>You have "+c.getToxicStaffOfDeadCharge()+" charges remaining in your toxic staff of the dead.</col>");
			}
			if (c.getToxicStaffOfDeadCharge() <= 0) {
				c.sendMessage("Your toxic staff of the dead has lost all of its charge.");
				c.getItems().wearItem(-1, 0, c.playerWeapon);
				if (!c.getItems().addItem(12904, 1)) {
					c.getItems().addItemToBank(12904, 1);
				}
				c.setToxicStaffOfDeadCharge(0);
			}
		}
	}

}
