package ab.model.players.combat;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import ab.Config;
import ab.Server;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.holiday.HolidayController;
import ab.model.items.ItemAssistant;
import ab.model.minigames.pest_control.PestControl;
import ab.model.minigames.warriors_guild.WarriorsGuild;
import ab.model.npcs.NPC;
import ab.model.npcs.NPCHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.combat.range.RangeData;
import ab.model.players.combat.range.RangeExtras;
import ab.model.players.skills.Skill;
import ab.model.players.skills.Slayer.Task;
import ab.model.players.skills.mining.Pickaxe;
import ab.util.Misc;

public class AttackNPC {


	public static boolean kalphite1(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 1158:
			return true;
		}
		return false;
	}

	public static boolean kalphite2(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 1160:
			return true;
		}
		return false;
	}
	public static int salveDamage(Player c)
	{
		int damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
		return damage *= 1.15;
	}
	
	public static void applyNpcMeleeDamage(Player c, int i, int damageMask, int damage, int defence) {
		if (!c.usingClaws) {
			damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
		}
		if (NPCHandler.npcs[i].npcType == 6600) {
			Pickaxe pickaxe = Pickaxe.getBestPickaxe(c);
			if (pickaxe != null && c.getItems().isWearingItem(pickaxe.getItemId())) {
				damage += Misc.random(pickaxe.getPriority() * 4);
			}
		}
		c.previousDamage = damage;

		boolean fullVeracsEffect = c.getPA().fullVeracs() && Misc.random(3) == 1;
		if (NPCHandler.npcs[i].HP - damage < 0) {
			damage = NPCHandler.npcs[i].HP;
		}
		if (NPCHandler.npcs[i].npcType == 5666) {
			damage = damage / 4;
			if (damage < 0) {
				damage = 0;
			}
		}
		if(c.getPA().salveAmulet()) {		
				if (Server.npcHandler.isUndead(i) && damage < 0) {
					damage = salveDamage(c);
					if(damage > Server.npcHandler.npcs[i].HP) {
						damage = Server.npcHandler.npcs[i].HP;
					} else {
						damage = damage;
					}
				}
			}
		if (!fullVeracsEffect) {
			if (Misc.random(defence) > 10 + Misc.random(c.getCombat().calculateMeleeAttack())) {
				damage = 0;
			} else if (NPCHandler.npcs[i].npcType == 2882 || NPCHandler.npcs[i].npcType == 2883) {
				damage = 0;
			}
		}

		boolean guthansEffect = false;
		if (c.getPA().fullGuthans()) {
			if (Misc.random(3) == 1) {
				guthansEffect = true;
			}
		}

		if (c.playerEquipment[c.playerWeapon] == 4718 && c.playerEquipment[c.playerHat] == 4716 && c.playerEquipment[c.playerChest] == 4720
				&& c.playerEquipment[c.playerLegs] == 4722) {
			damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
		}

		if (NPCHandler.npcs[i].HP - damage < 0) {
			damage = NPCHandler.npcs[i].HP;
		}
		if (c.fightMode == 3) {
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 3);
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 0);
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 1);
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 2);
			c.getPA().refreshSkill(0);
			c.getPA().refreshSkill(1);
			c.getPA().refreshSkill(2);
			c.getPA().refreshSkill(3);
		} else {
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 3);
			c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE), c.fightMode);
			c.getPA().refreshSkill(c.fightMode);
			c.getPA().refreshSkill(3);
		}
		if (damage > 0) {
			// Pest Control shit was here
		}
		if (damage > 0 && guthansEffect) {
			c.playerLevel[3] += damage;
			if (c.playerLevel[3] > c.getMaximumHealth())
				c.playerLevel[3] = c.getMaximumHealth();
			c.getPA().refreshSkill(3);
			NPCHandler.npcs[i].gfx0(398);
		}

		NPCHandler.npcs[i].underAttack = true;
		c.killingNpcIndex = c.npcIndex;
		c.lastNpcAttacked = i;

		switch (c.specEffect) {
		case 4:
			if (damage > 0) {
				if (c.playerLevel[3] + damage > c.getMaximumHealth())
					c.playerLevel[3] = c.getMaximumHealth();
				else
					c.playerLevel[3] += damage;
				c.getPA().refreshSkill(3);
			}
			break;

		case 5:
			if (damage > 0) {
				c.playerLevel[5] += damage;
				if (c.playerLevel[5] < 0)
					c.playerLevel[5] = 0;
				else if (c.playerLevel[5] > c.getPA().getLevelForXP(c.playerXP[5]))
					c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.getPA().refreshSkill(5);
			}
			break;
		}
		c.specEffect = 0;
		switch (damageMask) {
		case 1:
			NPCHandler.npcs[i].hitDiff = damage;
			NPCHandler.npcs[i].HP -= damage;
			c.totalDamageDealt += damage;
			NPCHandler.npcs[i].hitUpdateRequired = true;
			NPCHandler.npcs[i].updateRequired = true;
			if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
				c.pestControlDamage += damage;
			}
			break;

		case 2:
			NPCHandler.npcs[i].hitDiff2 = damage;
			NPCHandler.npcs[i].HP -= damage;
			c.totalDamageDealt += damage;
			NPCHandler.npcs[i].hitUpdateRequired2 = true;
			NPCHandler.npcs[i].updateRequired = true;
			if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
				c.pestControlDamage += damage;
			}
			c.doubleHit = false;
			break;

		}
	}
	
	private static int getBonusDefence(Player player, NPC npc, CombatType type) {
		if (type.equals(CombatType.MELEE)) {
			
		} else if (type.equals(CombatType.MAGE)) {
			switch (npc.npcType) {
				case 2042:
					return -100; 
				case 2044:
					return +100;
			}
		} else if (type.equals(CombatType.RANGE)) {
			switch (npc.npcType) {
				case 2042:
					return 100; 
				case 2044:
					return -100;
			}
		}
		return 0;
	}

	public static void delayedHit(final Player c, final int i) {
		NPC npc = NPCHandler.npcs[i];
		if (npc == null || npc.isDead || npc.HP <= 0) {
			return;
		}
		CombatType style = !c.castingMagic && c.projectileStage > 0 ? CombatType.RANGE :
			c.projectileStage > 0 ? CombatType.MAGE : CombatType.MELEE; 
		int defence = npc.defence + getBonusDefence(c, npc, style);
		if (defence < 0) {
			defence = 0;
		}
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].isDead) {
				c.npcIndex = 0;
				return;
			}
			boolean rejectsFaceUpdate = false;
			if (npc.npcType >= 2042 && npc.npcType <= 2044 || npc.npcType == 6720) {
				if (c.getZulrahEvent().getNpc() != null && c.getZulrahEvent().getNpc().equals(npc)) {
					if (c.getZulrahEvent().getStage() == 1) {
						rejectsFaceUpdate = true;
					}
				}
				if (c.getZulrahEvent().isTransforming()) {
					return;
				}
			}
			if (!rejectsFaceUpdate) {
				NPCHandler.npcs[i].facePlayer(c.playerId);
			}

			if (NPCHandler.npcs[i].underAttackBy > 0 && Server.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			} else if (NPCHandler.npcs[i].underAttackBy < 0 && !Server.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			}

			c.lastNpcAttacked = i;

			if (c.projectileStage == 0 && !c.usingMagic) {
				if (!c.usingClaws) {
					c.getCombat().applyNpcMeleeDamage(i, 1, Misc.random(c.getCombat().calculateMeleeMaxHit()), defence);
					if (c.doubleHit) {
						c.getCombat().applyNpcMeleeDamage(i, 2, Misc.random(c.getCombat().calculateMeleeMaxHit()), defence);
					}
				} else {
					c.usingClaws = false;
					final int hit1 = Misc.random(c.getCombat().calculateMeleeMaxHit());
					final int hit2 = hit1 / 2;
					final int hit3 = hit2 / 2;
					final int hit4 = hit3;
					npc.dealDamage(hit1);
					npc.handleHitMask(hit1);
					npc.dealDamage(hit2);
					npc.handleHitMask(hit2);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

						@Override
						public void execute(CycleEventContainer container) {
							if (container.getOwner() == null) {
								container.stop();
								return;
							}
							npc.dealDamage(hit3);
							npc.handleHitMask(hit3);
							npc.dealDamage(hit4);
							npc.handleHitMask(hit4);
							container.stop();
						}
						
					}, 1);
				}
			} else if (!c.castingMagic && c.projectileStage > 0) { // range hit damage
				int damage = Misc.random(c.getCombat().rangeMaxHit());
				int damage2 = -1;

				if (c.lastWeaponUsed == 11235 || c.lastWeaponUsed == 12765 || c.lastWeaponUsed == 12766 || c.lastWeaponUsed == 12767
						|| c.lastWeaponUsed == 12768 || c.bowSpecShot == 1)
					damage2 = Misc.random(c.getCombat().rangeMaxHit());
				if (damage > 0 && RangeExtras.wearingCrossbow(c)) {
					if (RangeExtras.wearingBolt(c)) {
						if (RangeExtras.boltSpecialAvailable(c)) {
							RangeExtras.crossbowSpecial(c, i);
						}
					}
				}
				if (damage > 0 && Misc.random(5) == 1
						&& c.lastArrowUsed == 9242) {
					if (c.playerLevel[3] - c.playerLevel[3]/10 < 1) {
						return;
					}
					c.playerLevel[3] -= c.playerLevel[3]/10;
					c.sendMessage("<col=255>Your health was drained...");
					damage += NPCHandler.npcs[i].HP / 5;
					NPCHandler.npcs[i].gfx0(754);
				}
				if (damage > 0 && Misc.random(5) == 1
						&& c.lastArrowUsed == 9244) {
					damage *= 1.45;
					NPCHandler.npcs[i].gfx0(756);
				}
				if (Misc.random(defence) > Misc.random(10 + c.getCombat().calculateRangeAttack()) && !c.ignoreDefence) {
					damage = 0;
				} else if (NPCHandler.npcs[i].npcType == 2881 || NPCHandler.npcs[i].npcType == 2883 && !c.ignoreDefence) {
					damage = 0;
				}
				if (c.lastWeaponUsed == 11235 || c.lastWeaponUsed == 12765 || c.lastWeaponUsed == 12766 || c.lastWeaponUsed == 12767
						|| c.lastWeaponUsed == 12768 || c.bowSpecShot == 1) {
					if (Misc.random(NPCHandler.npcs[i].defence) > Misc.random(10 + c.getCombat().calculateRangeAttack()))
						damage2 = 0;
				}
				if (c.dbowSpec) {
					NPCHandler.npcs[i].gfx100(c.lastArrowUsed == 11212 ? 1100 : 1103);
					if (damage < 8)
						damage = 8;
					if (damage2 < 8)
						damage2 = 8;
					c.dbowSpec = false;
				}

				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}
				if (damage2 > 0) {
					if (damage == NPCHandler.npcs[i].HP && NPCHandler.npcs[i].HP - damage2 > 0) {
						damage2 = 0;
					}
				}
				if (c.fightMode == 3) {
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3), 4);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3), 1);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3), 3);
					c.getPA().refreshSkill(1);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				} else {
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE), 4);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3), 3);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				}
				if (damage > 0) {

				}
				boolean dropArrows = true;

				for (int noArrowId : c.NO_ARROW_DROP) {
					if (c.lastWeaponUsed == noArrowId) {
						dropArrows = false;
						break;
					}
				}
				if (dropArrows) {
					c.getItems().dropArrowNpc();
					if (c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766
							|| c.playerEquipment[3] == 12767 || c.playerEquipment[3] == 12768) {
						c.getItems().dropArrowNpc();
					}
				}
				if (Server.npcHandler.getNPCs()[i].attackTimer > 3) {
					if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044 && npc.npcType != 3127) {
						NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);
					}
				}
				c.rangeEndGFX = RangeData.getRangeEndGFX(c);

				if ((c.playerEquipment[3] == 10034 || c.playerEquipment[3] == 10033)) {
					for (int j = 0; j < NPCHandler.npcs.length; j++) {
						if (NPCHandler.npcs[j] != null && NPCHandler.npcs[j].maximumHealth > 0) {
							int nX = NPCHandler.npcs[j].getX();
							int nY = NPCHandler.npcs[j].getY();
							int pX = NPCHandler.npcs[i].getX();
							int pY = NPCHandler.npcs[i].getY();
							if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
								if (NPCHandler.npcs[i].inMulti()) {
									Player p = PlayerHandler.players[c.playerId];
									c.getCombat().appendMutliChinchompa(j);
									Server.npcHandler.attackPlayer(p, j);
								}
							}
						}
					}
				}
				if (!c.multiAttacking) {
					NPCHandler.npcs[i].underAttack = true;
					NPCHandler.npcs[i].hitDiff = damage;
					NPCHandler.npcs[i].HP -= damage;
					if (damage2 > -1) {
						NPCHandler.npcs[i].hitDiff2 = damage2;
						NPCHandler.npcs[i].HP -= damage2;
						c.totalDamageDealt += damage2;
					}
					if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
						c.pestControlDamage += damage;
						if (damage2 > 0) {
							c.pestControlDamage += damage;
						}
						
					}
				}
				c.ignoreDefence = false;
				c.multiAttacking = false;

				if (c.rangeEndGFX > 0) {
					if (c.rangeEndGFXHeight) {
						NPCHandler.npcs[i].gfx100(c.rangeEndGFX);
					} else {
						NPCHandler.npcs[i].gfx0(c.rangeEndGFX);
					}
				}
				if (c.killingNpcIndex != c.oldNpcIndex) {
					c.totalDamageDealt = 0;
				}
				c.killingNpcIndex = c.oldNpcIndex;
				c.totalDamageDealt += damage;
				NPCHandler.npcs[i].hitUpdateRequired = true;
				if (damage2 > -1)
					NPCHandler.npcs[i].hitUpdateRequired2 = true;
				NPCHandler.npcs[i].updateRequired = true;

			} else if (c.projectileStage > 0) { // magic hit damage
				if (NPCHandler.npcs[i].HP <= 0) {
					return;
				}
				if (c.spellSwap) {
					c.spellSwap = false;
					c.setSidebarInterface(6, 16640);
					c.playerMagicBook = 2;
					c.gfx0(-1);
				}
				int damage = 0;
				c.usingMagic = true;
				if (c.fullVoidMage() && c.playerEquipment[c.playerWeapon] == 8841) {
					damage = Misc.random(c.getCombat().magicMaxHit() + 10);
				} else {
					damage = Misc.random(c.getCombat().magicMaxHit());
				}
				if (c.getCombat().godSpells()) {
					if (System.currentTimeMillis() - c.godSpellDelay < Config.GOD_SPELL_CHARGE) {
						damage += Misc.random(10);
					}
				}
				boolean magicFailed = false;
				if (Misc.random(defence) > 10 + Misc.random(c.getCombat().mageAtk())) {
					damage = 0;
					magicFailed = true;
				} else if (NPCHandler.npcs[i].npcType == 2881 || NPCHandler.npcs[i].npcType == 2882) {
					damage = 0;
					magicFailed = true;
				}
				for (int j = 0; j < NPCHandler.npcs.length; j++) {
					if (NPCHandler.npcs[j] != null && NPCHandler.npcs[j].maximumHealth > 0) {
						if (NPCHandler.npcs[i].heightLevel != c.heightLevel) {
							continue;
						}
						int nX = NPCHandler.npcs[j].getX();
						int nY = NPCHandler.npcs[j].getY();
						int pX = NPCHandler.npcs[i].getX();
						int pY = NPCHandler.npcs[i].getY();
						if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
							if (c.getCombat().multis() && NPCHandler.npcs[i].inMulti() && NPCHandler.npcs[i].heightLevel == c.heightLevel) {
								Player p = PlayerHandler.players[c.playerId];
								c.getCombat().appendMultiBarrageNPC(j, c.magicFailed);
								Server.npcHandler.attackPlayer(p, j);
							}
						}
					}
				}
				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}
				if (c.magicDef) {
					c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 2), 1);
					c.getPA().refreshSkill(1);
				}
				c.getPA().addSkillXP((c.MAGIC_SPELLS[c.oldSpellId][7] + damage * Config.MAGIC_EXP_RATE), 6);
				c.getPA().addSkillXP((c.MAGIC_SPELLS[c.oldSpellId][7] + damage * Config.MAGIC_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);
				if (c.getCombat().getEndGfxHeight() == 100 && !magicFailed) { // end
																				// GFX
					NPCHandler.npcs[i].gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
					if (Server.npcHandler.getNPCs()[i].attackTimer > 3) {
						if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044 && npc.npcType != 3127) {
							NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);
						}
					}
				} else if (!magicFailed) {
					NPCHandler.npcs[i].gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
				}

				if (magicFailed) {
					if (Server.npcHandler.getNPCs()[i].attackTimer > 3) {
						if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044) {
							NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);
						}
					}
					NPCHandler.npcs[i].gfx100(85);
				}
				if (c.playerEquipment[c.playerWeapon] == 11907) {
					c.setTridentCharge(c.getTridentCharge() - 1);
				} else if (c.playerEquipment[c.playerWeapon] == 12899) {
					c.setToxicTridentCharge(c.getToxicTridentCharge() - 1);
				}
				if (!magicFailed) {
					int freezeDelay = c.getCombat().getFreezeTime();// freeze
					if (freezeDelay > 0 && NPCHandler.npcs[i].freezeTimer == 0) {
						NPCHandler.npcs[i].freezeTimer = freezeDelay;
					}
					switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
					case 12901:
					case 12919: // blood spells
					case 12911:
					case 12929:
						int heal = Misc.random(damage / 2);
						if (c.playerLevel[3] + heal >= c.getMaximumHealth()) {
							c.playerLevel[3] = c.getMaximumHealth();
						} else {
							c.playerLevel[3] += heal;
						}
						c.getPA().refreshSkill(3);
						break;
					}

				}
				NPCHandler.npcs[i].underAttack = true;
				if (c.getCombat().magicMaxHit() != 0) {
					if (!c.multiAttacking) {
						NPCHandler.npcs[i].hitDiff = damage;
						NPCHandler.npcs[i].HP -= damage;
						NPCHandler.npcs[i].hitUpdateRequired = true;
						c.totalDamageDealt += damage;
					}
					if (Boundary.isIn(c, PestControl.GAME_BOUNDARY)) {
						c.pestControlDamage += damage;
					}
				}
				c.multiAttacking = false;
				c.killingNpcIndex = c.oldNpcIndex;
				NPCHandler.npcs[i].updateRequired = true;
				c.usingMagic = false;
				c.castingMagic = false;
				c.oldSpellId = 0;
			}
		}
		c.getCombat().checkVenomousItems();
		Degrade.degrade(c);
		if (c.bowSpecShot <= 0) {
			c.oldNpcIndex = 0;
			c.projectileStage = 0;
			c.doubleHit = false;
			c.lastWeaponUsed = 0;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot >= 2) {
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot == 1) {
			c.getCombat().fireProjectileNpc();
			c.hitDelay = 2;
			c.bowSpecShot = 0;
		}
		if (c.usingSpecial) {
			c.usingSpecial = false;
			c.getItems().updateSpecialBar();
		}
		c.specAccuracy = 1.0;
		c.specDamage = 1.0;
	}

	public static boolean armaNpc(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 6229:
		case 6230:
		case 6231:
		case 6232:
		case 6233:
		case 6234:
		case 6222:
		case 6573:
		case 3163:
		case 3164:
		case 3165:
		case 6235:
		case 6236:
		case 6237:
		case 6238:
		case 6239:
		case 6240:
		case 6241:
		case 6242:
		case 6243:
		case 6244:
		case 6245:
		case 6246:
			return true;
		}
		return false;
	}

	public static void resetSpells(Player c) {
		if (c.playerMagicBook == 0) {
			c.setSidebarInterface(6, 1151); // modern
		}
		if (c.playerMagicBook == 1) {
			c.setSidebarInterface(6, 12855); // ancient
		}
		if (c.playerMagicBook == 2) {
			c.setSidebarInterface(6, 29999); // lunar
		}
	}

	public static void attackNpc(Player c, int i) {
		if (NPCHandler.npcs[i] == null) {
			System.out.println("here");
			return;
		}
		NPC npc = NPCHandler.npcs[i];
		Task task = Task.forNpc(NPCHandler.npcs[i].npcType);
		if (task != null) {
			if (c.playerLevel[Skill.SLAYER.getId()] < task.getLevelReq()) {
				c.sendMessage("You need at least " + task.getLevelReq() + " slayer to attack this slayer npc.");
				c.getCombat().resetPlayerAttack();
				return;
			}
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (npc.npcType >= 2042 && npc.npcType <= 2044 || npc.npcType == 6720) {
			if (c.getZulrahEvent().isTransforming()) {
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (c.getZulrahEvent().getStage() == 0 && !c.getRights().isOwner()) {
				c.getCombat().resetPlayerAttack();
				return;
			}
		}
		if (npc.npcType >= 2042 && npc.npcType <= 2044) {
			if (c.usingMelee) {
				c.getCombat().resetPlayerAttack();
				return;
			}
		}
		c.slayerHelmetEffect = c.playerEquipment[c.playerHat] == 11864 && c.slayerTask == i;
		resetSpells(c);
		if (NPCHandler.npcs[i] != null) {
			c.getCombat().strBonus = c.playerBonus[10];
			if (NPCHandler.npcs[i].isDead || NPCHandler.npcs[i].maximumHealth <= 0) {
				c.usingMagic = false;
				c.faceUpdate(0);
				c.npcIndex = 0;
				return;
			}
			if (c.teleTimer > 0) {
				return;
			}
			if (c.respawnTimer > 0) {
				c.npcIndex = 0;
				return;
			}
			if (c.playerEquipment[c.playerWeapon] == 4734 && c.playerEquipment[c.playerArrows] > 0 && c.playerEquipment[c.playerArrows] != 4740) {
				c.sendMessage("You must use bolt racks with the karil's crossbow.");
				c.npcIndex = 0;
				return;
			}
			if (NPCHandler.npcs[i].npcType == 6611 || NPCHandler.npcs[i].npcType == 6612) {
				List<NPC> minion = Arrays.asList(NPCHandler.npcs);
				if (minion.stream().filter(Objects::nonNull).anyMatch(n -> n.npcType == 5054 && !n.isDead && n.HP > 0)) {
					c.sendMessage("You must kill Vet'ions minions before attacking him.");
					c.npcIndex = 0;
					return;
				}
			}
			if (NPCHandler.npcs[i].npcType == 6528) {
				if (c.getHolidayStages().getStage("Christmas") != HolidayController.CHRISTMAS.getMaximumStage()) {
					c.sendMessage("You must complete the christmas event to attack anti-santa.");
					c.npcIndex = 0;
					return;
				}
				if (c.playerEquipment[c.playerHat] != 10507) {
					c.sendMessage("You need to be wearing the reindeer hat to attack anti-santa.");
					c.npcIndex = 0;
					return;
				}
			}
			if (NPCHandler.npcs[i].underAttackBy > 0 && NPCHandler.npcs[i].underAttackBy != c.playerId && !NPCHandler.npcs[i].inMulti()) {
				c.npcIndex = 0;
				c.sendMessage("This monster is already in combat.");
				return;
			}
			if ((c.underAttackBy > 0 || c.underAttackBy2 > 0) && c.underAttackBy2 != i && !c.inMulti()) {
				c.getCombat().resetPlayerAttack();
				c.sendMessage("I am already under attack.");
				return;
			}
			if (!c.getCombat().goodSlayer(i)) {
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (NPCHandler.npcs[i].spawnedBy != c.playerId && NPCHandler.npcs[i].spawnedBy > 0) {
				c.getCombat().resetPlayerAttack();
				c.sendMessage("This monster was not spawned for you.");
				return;
			}
			if (c.getX() == NPCHandler.npcs[i].getX() && c.getY() == NPCHandler.npcs[i].getY()) {
				c.getPA().walkTo(0, 1);
			}
			if (Boundary.isIn(NPCHandler.npcs[i], Boundary.GODWARS_BOSSROOMS)
					&& !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
				c.getCombat().resetPlayerAttack();
				c.sendMessage("You cannot attack that npc from outside the room.");
				return;
			}
			int npcType = NPCHandler.npcs[i].npcType;
			if (npcType == 2463 || npcType == 2464) {
				if (Boundary.isIn(c, WarriorsGuild.CYCLOPS_BOUNDARY)) {
					if (!c.getWarriorsGuild().isActive()) {
						c.sendMessage("You cannot attack a cyclops without talking to kamfreena.");
						c.getCombat().resetPlayerAttack();
						return;
					}
				}
			}
			c.followId2 = i;
			c.followId = 0;
			if (c.attackTimer <= 0) {
				c.usingBow = false;
				c.usingArrows = false;
				c.usingOtherRangeWeapons = false;
				c.usingCross = c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 11785;
				c.bonusAttack = 0;
				c.rangeItemUsed = 0;
				c.projectileStage = 0;
				c.usingRangeWeapon = false;
				if (c.autocasting) {
					c.spellId = c.autocastId;
					c.usingMagic = true;
				}
				if (c.spellId > 0) {
					c.usingMagic = true;
				}
				if (c.playerEquipment[c.playerWeapon] == 11907) {
					if (c.getTridentCharge() <= 0) {
						c.sendMessage("Your trident of the seas has no more charges.");
						c.getCombat().resetPlayerAttack();
						return;
					}
					c.usingMagic = true;
					c.autocasting = true;
					c.castingMagic = true;
					c.spellId = 52;
				}
				if (c.playerEquipment[c.playerWeapon] == 12899) {
					if (c.getToxicTridentCharge() <= 0) {
						c.sendMessage("Your trident of the swamp has no more charges.");
						c.getCombat().resetPlayerAttack();
						return;
					}
					c.usingMagic = true;
					c.autocasting = true;
					c.castingMagic = true;
					c.spellId = 53;
				}
				c.attackTimer = c.getCombat().getAttackDelay(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
				c.specAccuracy = 1.0;
				c.specDamage = 1.0;
				if (c.getItems().isWearingItem(12931)) {
					if (c.getSerpentineHelmCharge() <= 0) {
						c.sendMessage("Your serpentine helm has no charge, you need to recharge it.");
						c.getCombat().resetPlayerAttack();
						return;
					}
				}
				if (c.getItems().isWearingItem(12904)) {
					if (c.getToxicStaffOfDeadCharge() <= 0) {
						c.sendMessage("Your toxic staff of the dead has no charge, you need to recharge it.");
						c.getCombat().resetPlayerAttack();
						return;
					}
				}
				if (!c.usingMagic) {
					for (int bowId : c.BOWS) {
						if (c.playerEquipment[c.playerWeapon] == bowId && c.switchDelay.elapsed(100)) {
							c.usingBow = true;
							c.rangeDelay = 3;
							for (int arrowId : c.ARROWS) {
								if (c.playerEquipment[c.playerArrows] == arrowId) {
									c.usingArrows = true;
								}
							}
						}
					}

					for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
						if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
							c.usingOtherRangeWeapons = true;
						}
					}
				}
				if (c.getItems().isWearingItem(12926)) {
					if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0 || c.getToxicBlowpipeCharge() == 0) {
						c.sendMessage("Your blowpipe is out of ammo or charge.");
						c.getCombat().resetPlayerAttack();
						return;
					}
					c.usingBow = true;
					c.usingArrows = true;
				}
				if (armaNpc(i) && !c.usingCross && !c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You need to range attack this monster!");
					return;
				}
				/*if (npcType == 6579 && !c.usingMagic) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You need to magic attack this monster!");
					return;
				}
				if (npcType == 6580 && !c.usingCross && !c.usingBow && !c.usingOtherRangeWeapons) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You need to range attack this monster!");
					return;
				}*/
				if ((!c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 2) && (c.getCombat().usingHally()
						&& !c.usingOtherRangeWeapons && !c.usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 4) && (c.usingOtherRangeWeapons
								&& !c.usingBow && !c.usingMagic))
						|| (!c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 1) && (!c.usingOtherRangeWeapons
								&& !c.getCombat().usingHally() && !c.usingBow && !c.usingMagic))
						|| ((!c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 8) && (c.usingBow || c.usingMagic)))) {
					c.attackTimer = 2;
					return;
				}

				if (!c.usingCross && !c.usingArrows && c.usingBow
						&& (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223)) {
					c.sendMessage("You have run out of arrows!");
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}
				if (c.getCombat().correctBowAndArrows() < c.playerEquipment[c.playerArrows] && Config.CORRECT_ARROWS && c.usingBow
						&& !c.getCombat().usingCrystalBow() && c.playerEquipment[c.playerWeapon] != 9185
						&& c.playerEquipment[c.playerWeapon] != 11785 && !c.getItems().isWearingItem(12926)) {
					c.sendMessage("You can't use " + ItemAssistant.getItemName(c.playerEquipment[c.playerArrows]).toLowerCase() + "s with a "
							+ ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase() + ".");
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}

				if (c.playerEquipment[c.playerWeapon] == 9185 && !c.getCombat().properBolts() || c.playerEquipment[c.playerWeapon] == 11785
						&& !c.getCombat().properBolts()) {
					c.sendMessage("You must use bolts with a crossbow.");
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.usingBow
						|| c.castingMagic
						|| c.usingOtherRangeWeapons
						|| (c.getCombat().usingHally() && c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 2))) {
					c.stopMovement();
				}
				if (!c.getCombat().checkMagicReqs(c.spellId)) {
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}
				c.faceUpdate(i);
				NPCHandler.npcs[i].underAttackBy = c.playerId;
				NPCHandler.npcs[i].lastDamageTaken = System.currentTimeMillis();
				if (c.usingSpecial && !c.usingMagic) {
					if (c.getCombat().checkSpecAmount(c.playerEquipment[c.playerWeapon])) {
						c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
						c.lastArrowUsed = c.playerEquipment[c.playerArrows];
						c.getCombat().activateSpecial(c.playerEquipment[c.playerWeapon], i);
						//c.usingSpecial = false;
						//c.getItems().updateSpecialBar();
						return;
					} else {
						c.sendMessage("You don't have the required special energy to use this attack.");
						c.usingSpecial = false;
						c.getItems().updateSpecialBar();
						c.npcIndex = 0;
						return;
					}
				}
				c.specMaxHitIncrease = 0;
				if (c.playerLevel[3] > 0 && !c.isDead && NPCHandler.npcs[i].maximumHealth > 0) {
					if (!c.usingMagic) {
						c.startAnimation(c.getCombat().getWepAnim(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase()));
						if (Server.npcHandler.getNPCs()[i].attackTimer > 3) {
							if (npcType != 2042 && npcType != 2043 & npcType != 2044 && npcType != 3127) {
								NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);
							}
						}
					} else {
						c.startAnimation(c.MAGIC_SPELLS[c.spellId][2]);
					}
				}
				c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
				c.lastArrowUsed = c.playerEquipment[c.playerArrows];

				if (!c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons) { // melee
																					// hit
																					// delay
					c.followId2 = NPCHandler.npcs[i].npcId;
					c.getPA().followNpc();
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
			 		c.projectileStage = 0;
					c.oldNpcIndex = i;
				}

				if (c.usingBow && !c.usingOtherRangeWeapons && !c.usingMagic || c.usingCross) { // range
																								// hit
																								// delay
					if (c.usingCross)
						c.usingBow = true;
					if (c.fightMode == 2)
						c.attackTimer--;
					c.followId2 = NPCHandler.npcs[i].npcId;
					c.getPA().followNpc();
					c.lastArrowUsed = c.playerEquipment[c.playerArrows];
					c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
					c.gfx100(c.getCombat().getRangeStartGFX());
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					if (c.playerEquipment[c.playerWeapon] >= 4212 && c.playerEquipment[c.playerWeapon] <= 4223) {
						c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
						c.crystalBowArrowCount++;
						c.lastArrowUsed = 0;
						c.getCombat().fireProjectileNpc();
					} else if (c.playerEquipment[c.playerWeapon] == 12926) {
						c.getCombat().fireProjectileNpc();
					} else {
						c.rangeItemUsed = c.playerEquipment[c.playerArrows];
						c.getItems().deleteArrow();
						if (c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766
								|| c.playerEquipment[3] == 12767 || c.playerEquipment[3] == 12768) {
							c.getItems().deleteArrow();
						}
						c.getCombat().fireProjectileNpc();
					}
				}

				if (c.usingOtherRangeWeapons && !c.usingMagic && !c.usingBow) {
					c.usingRangeWeapon = true;
					c.followId2 = NPCHandler.npcs[i].npcId;
					c.getPA().followNpc();
					c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
					c.getItems().deleteEquipment();
					c.gfx100(c.getCombat().getRangeStartGFX());
					c.lastArrowUsed = 0;
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					if (c.fightMode == 2)
						c.attackTimer--;
					c.getCombat().fireProjectileNpc();
				}

				if (c.usingMagic) { // magic hit delay
					int pX = c.getX();
					int pY = c.getY();
					int nX = NPCHandler.npcs[i].getX();
					int nY = NPCHandler.npcs[i].getY();
					int offX = (pY - nY) * -1;
					int offY = (pX - nX) * -1;
					c.castingMagic = true;
					c.projectileStage = 2;
					c.stopMovement();
					if (c.MAGIC_SPELLS[c.spellId][3] > 0) {
						if (c.getCombat().getStartGfxHeight() == 100) {
							c.gfx100(c.MAGIC_SPELLS[c.spellId][3]);
						} else {
							c.gfx0(c.MAGIC_SPELLS[c.spellId][3]);
						}
					}
					if (c.MAGIC_SPELLS[c.spellId][4] > 0) {
						c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78, c.MAGIC_SPELLS[c.spellId][4], c.getCombat().getStartHeight(),
								c.getCombat().getEndHeight(), i + 1, 50);
					}
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.oldNpcIndex = i;
					c.oldSpellId = c.spellId;
					c.spellId = 0;
					if (!c.autocasting)
						c.npcIndex = 0;
				}

				if (c.usingBow && Config.CRYSTAL_BOW_DEGRADES) { // crystal bow
																	// degrading
					if (c.playerEquipment[c.playerWeapon] == 4212) { // new
																		// crystal
																		// bow
																		// becomes
																		// full
																		// bow
																		// on
																		// the
																		// first
																		// shot
						c.getItems().wearItem(4214, 1, 3);
					}

					if (c.crystalBowArrowCount >= 250) {
						switch (c.playerEquipment[c.playerWeapon]) {

						case 4223: // 1/10 bow
							c.getItems().wearItem(-1, 1, 3);
							c.sendMessage("Your crystal bow has fully degraded.");
							if (!c.getItems().addItem(4207, 1)) {
								Server.itemHandler.createGroundItem(c, 4207, c.getX(), c.getY(), c.heightLevel, 1, c.getId());
							}
							c.crystalBowArrowCount = 0;
							break;

						default:
							c.getItems().wearItem(++c.playerEquipment[c.playerWeapon], 1, 3);
							c.sendMessage("Your crystal bow degrades.");
							c.crystalBowArrowCount = 0;
							break;
						}
					}
				}
			}
		}
	}
}