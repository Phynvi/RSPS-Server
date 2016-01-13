package ab.model.players.combat.magic;

import ab.model.players.*;
import ab.model.players.combat.Hitmark;
import ab.*;
import ab.util.Misc;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;

public class MagicExtras {

	public static void multiSpellEffectNPC(Player c, int npcId, int damage) {					
		switch(c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 12891:
			case 12881:
				if (NPCHandler.npcs[npcId].freezeTimer < -4) {
					NPCHandler.npcs[npcId].freezeTimer = c.getCombat().getFreezeTime();
				}
			break;
		}	
	}

	public static boolean checkMultiBarrageReqsNPC(int i) {
		if(NPCHandler.npcs[i] == null) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkMultiBarrageReqs(Player c, int i) {
		if(PlayerHandler.players[i] == null) {
			return false;
		}
		if (i == c.playerId)
			return false;
		if(!PlayerHandler.players[i].inWild() && !PlayerHandler.players[i].inCamWild()) {
			return false;
		}
		if(Config.COMBAT_LEVEL_DIFFERENCE) {
			int combatDif1 = c.getCombat().getCombatDifference(c.combatLevel, PlayerHandler.players[i].combatLevel);
			if(combatDif1 > c.wildLevel || combatDif1 > PlayerHandler.players[i].wildLevel) {
				c.sendMessage("Your combat level difference is too great to attack that player here.");
				return false;
			}
		}
		
		if(Config.SINGLE_AND_MULTI_ZONES) {
			if(!PlayerHandler.players[i].inMulti()) {	// single combat zones
				if(PlayerHandler.players[i].underAttackBy != c.playerId  && PlayerHandler.players[i].underAttackBy != 0) {
					return false;
				}
				if(PlayerHandler.players[i].playerId != c.underAttackBy && c.underAttackBy != 0) {
					c.sendMessage("You are already in combat.");
					return false;
				}
			}
		}
		return true;
	}

	public static void appendMultiBarrageNPC(Player c, int npcId, boolean splashed) {
		if (NPCHandler.npcs[npcId] != null) {
			NPC n = NPCHandler.npcs[npcId];
			if (n.isDead)
				return;
			if (checkMultiBarrageReqsNPC(npcId)) {
				c.barrageCount++;
				c.multiAttacking = true;
				NPCHandler.npcs[npcId].underAttackBy = c.playerId;
				NPCHandler.npcs[npcId].underAttack = true;
				if (Misc.random(c.getCombat().mageAtk()) > Misc.random(NPCHandler.npcs[npcId].defence) && !c.magicFailed) {
					if(c.getCombat().getEndGfxHeight() == 100){ // end GFX
						n.gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
					} else {
						n.gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
					}
					int damage = Misc.random(c.getCombat().magicMaxHit());
					if (NPCHandler.npcs[npcId].HP - damage < 0) { 
						damage = NPCHandler.npcs[npcId].HP;
					}		
					NPCHandler.npcs[npcId].handleHitMask(damage);
					NPCHandler.npcs[npcId].dealDamage(damage);
					c.getCombat().multiSpellEffectNPC(npcId, damage);
					c.totalDamageDealt += damage;
					c.getCombat().multiSpellEffectNPC(npcId, damage);
				} else {
					n.gfx100(85);
				}			
			}		
		}	
	}

	public static void multiSpellEffect(Player c, int playerId, int damage) {					
		switch(c.MAGIC_SPELLS[c.oldSpellId][0]) {
			case 13011:
			case 13023:
			if(System.currentTimeMillis() - PlayerHandler.players[playerId].reduceStat > 35000) {
				PlayerHandler.players[playerId].reduceStat = System.currentTimeMillis();
				PlayerHandler.players[playerId].playerLevel[0] -= ((PlayerHandler.players[playerId].getLevelForXP(PlayerHandler.players[playerId].playerXP[0]) * 10) / 100);
			}	
			break;
			case 12919: // blood spells
			case 12929:
				int heal = damage / 4;
				if(c.playerLevel[3] + heal >= c.getMaximumHealth()) {
					c.playerLevel[3] = c.getMaximumHealth();
				} else {
					c.playerLevel[3] += heal;
				}
				c.getPA().refreshSkill(3);
			break;
			case 12891:
			case 12881:
				if (PlayerHandler.players[playerId].freezeTimer < -4) {
					PlayerHandler.players[playerId].freezeTimer = c.getCombat().getFreezeTime();
					PlayerHandler.players[playerId].stopMovement();
				}
			break;
		}	
	}

	public static void appendMultiBarrage(Player c, int playerId, boolean splashed) {
		if (PlayerHandler.players[playerId] != null) {
			Player c2 = PlayerHandler.players[playerId];
			if (c2.isDead || c2.respawnTimer > 0)
				return;
			if (c.getCombat().checkMultiBarrageReqs(playerId)) {
				c.barrageCount++;
				if (Misc.random(c.getCombat().mageAtk()) > Misc.random(c2.getCombat().mageDef()) && !c.magicFailed) {
					if(c.getCombat().getEndGfxHeight() == 100){ // end GFX
						c2.gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
					} else {
						c2.gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
					}
					int damage = Misc.random(c.getCombat().magicMaxHit());
					if (c2.prayerActive[12]) {
						damage *= (int)(.60);
					}
					if (c2.playerLevel[3] - damage < 0) {
						damage = c2.playerLevel[3];					
					}
					c.getPA().addSkillXP((c.MAGIC_SPELLS[c.oldSpellId][7] + damage*Config.MAGIC_EXP_RATE), 6); 
					c.getPA().addSkillXP((c.MAGIC_SPELLS[c.oldSpellId][7] + damage*Config.MAGIC_EXP_RATE/3), 3);
					c2.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
					c2.addDamageReceived(c.playerName, damage);
					c2.getPA().refreshSkill(3);
					c.getCombat().multiSpellEffect(playerId, damage);
				} else {
					c2.gfx100(85);
				}			
			}		
		}	
	}
}