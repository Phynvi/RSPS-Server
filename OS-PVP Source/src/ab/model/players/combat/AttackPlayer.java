package ab.model.players.combat;

import java.util.Arrays;
import java.util.Objects;

import ab.Config;
import ab.Server;
import ab.model.items.EquipmentSet;
import ab.model.items.ItemAssistant;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.combat.effects.AmuletOfTheDamnedDharokEffect;
import ab.model.players.combat.effects.AmuletOfTheDamnedKarilEffect;
import ab.model.players.combat.effects.SerpentineHelmEffect;
import ab.model.players.combat.effects.ToxicBlowpipeEffect;
import ab.model.players.combat.range.RangeData;
import ab.model.players.combat.range.RangeExtras;
import ab.util.Misc;

public class AttackPlayer {

	public static void applyPlayerHit(final Player c, final int i, final Damage damage) {
		c.getCombat().applyPlayerMeleeDamage(i, 1, damage.getAmount(), damage.getHitmark());
	}

	/**
	 * @author Chris
	 * @param defence
	 */

	public static boolean calculateBlockedHit(Player c, int defence) {
		if (defence > 450 && Misc.random(5) == 1)
			return true;
		if (defence > 400 && Misc.random(5) == 1)
			return true;
		if (defence > 350 && Misc.random(6) == 1)
			return true;
		if (defence > 300 && Misc.random(6) == 1)
			return true;
		if (Misc.random(6) == 1 && defence > 150)
			return true;
		if (defence > 10 && Misc.random(7) == 1)
			return true;
		return false;
	}

	/**
	 * @author Chris
	 * @param i
	 *            (Returns player index for meleeDefence())
	 * @param damage
	 * @return
	 */
	public static int calculateDefenceDamageReduction(Player c, int i, int damage) {
		Player o = PlayerHandler.players[i];
		int defence = o.getCombat().calculateMeleeDefence();
		if (calculateBlockedHit(c, defence))
			return 0;
		if (defence > 450)
			return damage *= .635;
		if (defence > 400)
			return damage *= .655;
		if (defence > 350)
			return damage *= .715;
		if (defence > 300)
			return damage *= .775;
		if (defence > 250)
			return damage *= .835;
		if (defence > 200)
			return damage *= .85;
		if (defence > 150)
			return damage *= .9125;
		if (defence > 100)
			return damage *= .975;
		if (defence > 10)
			return damage *= .99;
		return damage;
	}

	public static void applyPlayerMeleeDamage(Player c, int i, int damageMask, int damage, Hitmark hitmark) {
		c.previousDamage = damage;

		Player o = PlayerHandler.players[i];
		if (o == null) {
			return;
		}
		boolean guthansEffect = false;
		if (c.getPA().fullGuthans()) {
			if (Misc.random(4) == 1) {
				guthansEffect = true;
			}
		}
		if (c.playerEquipment[c.playerWeapon] == 5698) {
			c.bonusAttack += damage / 3;
			if (o.isSusceptibleToPoison() && Misc.random(4) == 0) {
				o.setPoisonDamage((byte) 6);
			}
		}
		if (damage > 0 && guthansEffect) {
			c.playerLevel[3] += damage;
			if (c.playerLevel[3] > c.getMaximumHealth())
				c.playerLevel[3] = c.getMaximumHealth();
			c.getPA().refreshSkill(3);
			o.gfx0(398);
		}
		if (c.ssSpec && damageMask == 2) {
			damage = 5 + Misc.random(11);
			c.ssSpec = false;
		}
		if (PlayerHandler.players[i].playerLevel[3] - damage < 0) {
			damage = PlayerHandler.players[i].playerLevel[3];
		}
		if (o.vengOn && damage > 0)
			c.getCombat().appendVengeance(i, damage);
		if (damage > 0)
			c.getCombat().applyRecoil(damage, i);
		switch (c.specEffect) {
			case 1: // dragon scimmy special
				if (damage > 0) {
					if (o.prayerActive[16] || o.prayerActive[17] || o.prayerActive[18]) {
						o.headIcon = -1;
						o.getPA().sendFrame36(c.PRAYER_GLOW[16], 0);
						o.getPA().sendFrame36(c.PRAYER_GLOW[17], 0);
						o.getPA().sendFrame36(c.PRAYER_GLOW[18], 0);
					}
					o.sendMessage("You have been injured!");
					o.stopPrayerDelay = System.currentTimeMillis();
					o.prayerActive[16] = false;
					o.prayerActive[17] = false;
					o.prayerActive[18] = false;
					o.getPA().requestUpdates();
				}
				break;
				
		case 2:
			if (damage > 0) {
				if (o.freezeTimer <= 0)
					o.freezeTimer = 30;
				o.gfx0(369);
				o.getPA().sendFrame126("freezetimer:30", 1810);
				o.sendMessage("You have been frozen.");
				o.frozenBy = c.playerId;
				o.stopMovement();
				c.sendMessage("You freeze your enemy.");
			}
			break;
		case 3:
			if (damage > 0) {
				o.playerLevel[1] -= damage;
				o.sendMessage("You feel weak.");
				if (o.playerLevel[1] < 1)
					o.playerLevel[1] = 1;
				o.getPA().refreshSkill(1);
			}
			break;
		case 4:
			if (damage > 0) {
				if (c.playerLevel[3] + damage > c.getLevelForXP(c.playerXP[3]))
					c.playerLevel[3] = c.getMaximumHealth();
				else
					c.playerLevel[3] += damage;
				c.getPA().refreshSkill(3);
			}
			break;

		case 5:
			if (c.playerIndex > 0) {
				if (damage > 0) {
					c.playerLevel[5] += damage;
					if (c.playerLevel[5] < 0)
						c.playerLevel[5] = 0;
					else if (c.playerLevel[5] > c.getPA().getLevelForXP(c.playerXP[5]))
						c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
					c.getPA().refreshSkill(5);

					o.playerLevel[5] -= damage;
					if (o.playerLevel[5] < 0)
						o.playerLevel[5] = 0;
					else if (o.playerLevel[5] > c.getPA().getLevelForXP(o.playerXP[5]))
						o.playerLevel[5] = c.getPA().getLevelForXP(o.playerXP[5]);
					o.getPA().refreshSkill(5);
				}
			}
			break;
		}
		c.specEffect = 0;
		o.logoutDelay.reset();
		o.underAttackBy = c.playerId;
		o.killerId = c.playerId;
		o.singleCombatDelay.reset();
		c.killedBy = PlayerHandler.players[i].playerId;
		c.getCombat().applySmite(i, damage);
		o.appendDamage(damage, hitmark);
		o.addDamageReceived(c.playerName, damage);
	}

	public static void addCombatXP(Player c, CombatType type, int damage) {
		switch (type) {
			default:
			case MELEE:
				c.getPA().addSkillXP((int) Math.ceil((damage * Config.MELEE_EXP_RATE)) / 3, 3);
				switch (c.fightMode) {
					case 0: // Accurate
						c.getPA().addSkillXP((int) Math.ceil(damage * Config.MELEE_EXP_RATE), 0);
						break;
					case 1: // Block
						if (c.playerLevel[1] == 1) {
							c.getPA().addSkillXP((int) Math.ceil(damage * Config.MELEE_EXP_RATE), 2);
						} else {
						c.getPA().addSkillXP((int) Math.ceil(damage * Config.MELEE_EXP_RATE), 1);
						}
						break;
					case 2: // Aggressive
						c.getPA().addSkillXP((int) Math.ceil(damage * Config.MELEE_EXP_RATE), 2);
						break;
					case 3: // Controlled
						if (c.playerLevel[1] == 1) {
							c.getPA().addSkillXP((int) Math.ceil(damage * Config.MELEE_EXP_RATE), 2);
						} else {
						for (int i = 0; i < 3; i++)
							c.getPA().addSkillXP((int) Math.ceil((damage * Config.MELEE_EXP_RATE) / 3), i);// 1.3
						}
						break;
					}
				break;
			case RANGE:
				c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE) / 3, 3);
				switch (c.fightMode) {
					case 0: // Accurate
					case 2: // Rapid
						c.getPA().addSkillXP(
								damage * Config.RANGE_EXP_RATE, 4);
						break;
					case 1: // Block
						c.getPA().addSkillXP(
								damage * Config.RANGE_EXP_RATE, 1);
						c.getPA().addSkillXP(
								damage * Config.RANGE_EXP_RATE / 2, 4);
						break;
					}
				break;
			case MAGE:
				c.getPA().addSkillXP((damage * Config.MAGIC_EXP_RATE) / 3, 3);
				int magicXP = damage * Config.MAGIC_EXP_RATE;
				c.getPA().addSkillXP(magicXP, 6);
				break;
		}
	}

	public static void playerDelayedHit(final Player c, final int i, final Damage d) {
		if (PlayerHandler.players[i] != null) {
			Player o = PlayerHandler.players[i];
			if (o == null || o.isDead || c.isDead || PlayerHandler.players[i].playerLevel[3] <= 0 || c.playerLevel[3] <= 0) {
				c.playerIndex = 0;
				return;
			}
			if (o.respawnTimer > 0) {
				c.faceUpdate(0);
				c.playerIndex = 0;
				return;
			}
			int damage = d.getAmount();
			o.getPA().removeAllWindows();
			if (o.playerIndex <= 0 && o.npcIndex <= 0) {
				if (o.autoRet == 1) {
					o.playerIndex = c.playerId;
				}
			}
			if (c.hasOverloadBoost) {
				c.getPotions().resetOverload();
			}
			if (o.attackTimer <= 3 || o.attackTimer == 0 && o.playerIndex == 0 && !c.castingMagic) { // block animation
				o.startAnimation(o.getCombat().getBlockEmote());
			}
			if (c.projectileStage == 0 && !c.usingMagic) { // melee hit damage
				c.getCombat().applyPlayerHit(c, i, d);
			}
			DamageEffect effect = new AmuletOfTheDamnedDharokEffect();
			if (effect.isExecutable(o)) {
				effect.execute(c, o, d);
			}
			if (o.getItems().isWearingItem(12931)) {
				DamageEffect venom = new SerpentineHelmEffect();
				if (venom.isExecutable(o)) {
					venom.execute(c, o, new Damage(6));
				}
			}
			if (!c.castingMagic && c.projectileStage > 0) { // range hit damage
				c.rangeEndGFX = RangeData.getRangeEndGFX(c);
				if (damage > 0 && RangeExtras.wearingCrossbow(c)) {
					if (RangeExtras.wearingBolt(c)) {
						if (RangeExtras.boltSpecialAvailable(c)) {
							RangeExtras.crossbowSpecial(c, i);
						}
					}
				}
				if (damage > 0 && Misc.random(5) == 1
						&& c.lastArrowUsed == 9244) {
					damage *= 1.45;
					o.gfx0(756);
				}
				DamageEffect karilEffect = new AmuletOfTheDamnedKarilEffect();
				if (karilEffect.isExecutable(c)) {
					karilEffect.execute(c, o, d);
				}
				if (o.vengOn) {
					c.getCombat().appendVengeance(i, damage);
				}
				if (damage > 0)
					c.getCombat().applyRecoil(damage, i);
				boolean dropArrows = true;
				for (int noArrowId : c.NO_ARROW_DROP) {
					if (c.lastWeaponUsed == noArrowId) {
						dropArrows = false;
						break;
					}
				}
				if (dropArrows) {
					c.getItems().dropArrowPlayer();
				}
				if (c.rangeEndGFX > 0 && !c.getCombat().usingBolts(c.lastArrowUsed)) {
					if (c.rangeEndGFXHeight) {
						o.gfx100(c.rangeEndGFX);
						c.inSpecMode = false;
					} else {
						o.gfx0(c.rangeEndGFX);
						c.inSpecMode = false;
					}
				}
				if (c.dbowSpec) {
					o.gfx100(c.lastArrowUsed == 11212 ? 1100 : 1103);
					c.dbowSpec = false;
				}
				if (damage > PlayerHandler.players[i].playerLevel[3]) {
					damage = PlayerHandler.players[i].playerLevel[3];
				}
				o.underAttackBy = c.playerId;
				o.logoutDelay.reset();
				o.singleCombatDelay.reset();
				o.killerId = c.playerId;
				o.appendDamage(damage, d.getHitmark());
				o.addDamageReceived(c.playerName, damage);
				c.killedBy = PlayerHandler.players[i].playerId;
				c.ignoreDefence = false;
				o.updateRequired = true;
				c.getCombat().applySmite(i, damage);
			} else if (c.projectileStage > 0) {
				if (c.spellSwap) {
					c.spellSwap = false;
					c.setSidebarInterface(6, 16640);
					c.playerMagicBook = 2;
					c.gfx0(-1);
				}
				if (o.vengOn)
					c.getCombat().appendVengeance(i, damage);
				if (damage > 0)
					c.getCombat().applyRecoil(damage, i);
				if (c.getCombat().getEndGfxHeight() == 100 && !c.magicFailed) { // end
					PlayerHandler.players[i].gfx100(c.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (!c.magicFailed) {
					PlayerHandler.players[i].gfx0(c.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (c.magicFailed) {
					PlayerHandler.players[i].gfx100(85);
				}
				if (!c.magicFailed) {
					if (System.currentTimeMillis() - PlayerHandler.players[i].reduceStat > 35000) {
						PlayerHandler.players[i].reduceStat = System.currentTimeMillis();
						switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {
						case 12987:
						case 13011:
						case 12999:
						case 13023:
							PlayerHandler.players[i].playerLevel[0] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[0]) * 10) / 100);
							break;
						}
					}

					switch (c.MAGIC_SPELLS[c.oldSpellId][0]) {

					case 12901:
					case 12919: // blood spells
					case 12911:
					case 12929:
						int heal = damage / 4;
						if (c.playerLevel[3] + heal > c.getMaximumHealth()) {
							c.playerLevel[3] = c.getMaximumHealth();
						} else {
							c.playerLevel[3] += heal;
						}
						c.getPA().refreshSkill(3);
						break;

					case 1153:
						PlayerHandler.players[i].playerLevel[0] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[0]) * 5) / 100);
						o.sendMessage("Your attack level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(0);
						break;

					case 1157:
						o.playerLevel[2] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[2]) * 5) / 100);
						o.sendMessage("Your strength level has been reduced!");
						o.reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(2);
						break;

					case 1161:
						o.playerLevel[1] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[1]) * 5) / 100);
						o.sendMessage("Your defence level has been reduced!");
						o.reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(1);
						break;

					case 1542:
						o.playerLevel[1] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[1]) * 10) / 100);
						o.sendMessage("Your defence level has been reduced!");
						o.reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(1);
						break;

					case 1543:
						o.playerLevel[2] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[2]) * 10) / 100);
						o.sendMessage("Your strength level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(2);
						break;

					case 1562:
						o.playerLevel[0] -= ((o.getPA().getLevelForXP(PlayerHandler.players[i].playerXP[0]) * 10) / 100);
						o.sendMessage("Your attack level has been reduced!");
						PlayerHandler.players[i].reduceSpellDelay[c.reduceSpellId] = System.currentTimeMillis();
						o.getPA().refreshSkill(0);
						break;
					}
				}
				if (damage > PlayerHandler.players[i].playerLevel[3]) {
					damage = PlayerHandler.players[i].playerLevel[3];
				}
				o.logoutDelay.reset();
				o.underAttackBy = c.playerId;
				o.killerId = c.playerId;
				o.singleCombatDelay.reset();
				if (c.getCombat().magicMaxHit() != 0) {
					o.addDamageReceived(c.playerName, damage);
					if (!c.magicFailed) {
						o.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
					}
				}
				c.getCombat().checkVenomousItems();
				c.getCombat().applySmite(i, damage);
				c.killedBy = PlayerHandler.players[i].playerId;
				o.getPA().refreshSkill(3);
				o.updateRequired = true;
				c.usingMagic = false;
				c.castingMagic = false;
				if (o.inMulti() && c.getCombat().multis()) {
					c.barrageCount = 0;
					for (int j = 0; j < PlayerHandler.players.length; j++) {
						if (PlayerHandler.players[j] != null) {
							if (j == o.playerId)
								continue;
							if (c.barrageCount >= 9)
								break;
							if (o.goodDistance(o.getX(), o.getY(), PlayerHandler.players[j].getX(), PlayerHandler.players[j].getY(), 1))
								c.getCombat().appendMultiBarrage(j, c.magicFailed);
						}
					}
				}
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);
				c.oldSpellId = 0;
			}
		}
		Degrade.degrade(c);
		c.getPA().requestUpdates();
		if (c.bowSpecShot <= 0) {
			c.oldPlayerIndex = 0;
			c.projectileStage = 0;
			c.lastWeaponUsed = 0;
			c.doubleHit = false;
			c.usingClaws = false;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot != 0) {
			c.bowSpecShot = 0;
		}
	}
	
	public static void calculateCombatDamage(Player attacker, Player defender) {
		int damage = 0;
		int damage2 = -1;
		int damage3 = -1;
		int damage4 = -1;
		int defence = 0;
		Hitmark hitmark1 = null;
		Hitmark hitmark2 = null;
		Hitmark hitmark3 = null;
		Hitmark hitmark4 = null;
		if (Objects.nonNull(attacker) && Objects.nonNull(defender)) {
			if (attacker.projectileStage == 0 && !attacker.usingMagic) {
				damage = Misc.random(attacker.getCombat().calculateMeleeMaxHit());
				defence = 10 + defender.getCombat().calculateMeleeDefence();
				boolean veracsEffect = false;
				if (attacker.getPA().fullVeracs()) {
					if (Misc.random(4) == 1) {
						veracsEffect = true;
					}
				}
				if (defender.playerEquipment[defender.playerShield] == 12817) {
					if (Misc.random(100) > 30 && damage > 0) {
						damage *= .75;
					}
				}
				if (EquipmentSet.TORAG.isWearingBarrows(defender) && defender.getItems().isWearingItem(12853)) {
					defence += defender.getLevelForXP(defender.playerXP[defender.playerHitpoints])
							- defender.playerLevel[defender.playerHitpoints];
				}
				if (Misc.random(defence) > Misc.random(10 + attacker.getCombat().calculateMeleeAttack())
						&& !veracsEffect) {
					damage = 0;
					attacker.bonusAttack = 0;
				}
				if (attacker.doubleHit) {
					damage2 = Misc.random(attacker.getCombat().calculateMeleeMaxHit());
				}
				if (defender.prayerActive[18]/* && System.currentTimeMillis() - defender.protMeleeDelay > 1000*/ && !veracsEffect) {
					damage = damage * 60 / 100;
					if (damage2 > 0) {
						damage2 = damage2 * 60 / 100;
					}
				}
				if (attacker.usingSpecial && attacker.usingClaws) {
					damage2 = damage / 2;
					damage3 = damage / 4;
					damage4 = damage3;
				}
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark4 = damage4 > 0 ? Hitmark.HIT : Hitmark.MISS;
				AttackPlayer.addCombatXP(attacker, CombatType.MELEE, damage + (damage2 > 0 ? damage2 : 0) + 
						(damage3 > 0 ? damage3 : 0) + (damage4 > 0 ? damage4 : 0));
			} else if (!attacker.castingMagic && attacker.projectileStage > 0) {
				damage = Misc.random(attacker.getCombat().rangeMaxHit());
				defence = 10 + defender.getCombat().calculateRangeDefence();
				if (EquipmentSet.TORAG.isWearing(defender) && defender.getItems().isWearingItem(12853)) {
					defence += defender.getLevelForXP(defender.playerXP[defender.playerHitpoints])
							- defender.playerLevel[defender.playerHitpoints];
				}
				if (Misc.random(defence) > Misc.random(10 + attacker.getCombat().calculateRangeAttack())
						&& !attacker.ignoreDefence) {
					damage = 0;
				}
				if (attacker.lastWeaponUsed == 11235 || attacker.lastWeaponUsed == 12765 || attacker.lastWeaponUsed == 12766 || attacker.lastWeaponUsed == 12767 || attacker.lastWeaponUsed == 12768 || attacker.bowSpecShot == 1) {
					damage2 = Misc.random(attacker.getCombat().rangeMaxHit());
					if (Misc.random(defence) > Misc.random(10 + attacker.getCombat().calculateRangeAttack())) {
						damage2 = 0;
					}
				}
				DamageEffect venomEffect = new ToxicBlowpipeEffect();
				if (venomEffect.isExecutable(attacker)) {
					venomEffect.execute(attacker, defender, new Damage(6));
				}
				if (attacker.dbowSpec) {
					if (damage < 8) {
						damage = 8;
					}
					if (damage2 < 8) {
						damage2 = 8;
					}
				}
				if (defender.prayerActive[17]/* && System.currentTimeMillis() - defender.protRangeDelay > 1000*/) {
					damage = damage * 60 / 100;
					if (attacker.lastWeaponUsed == 11235 || attacker.lastWeaponUsed == 12765 || attacker.lastWeaponUsed == 12766 || attacker.lastWeaponUsed == 12767 || attacker.lastWeaponUsed == 12768 || attacker.bowSpecShot == 1)
						damage2 = damage2 * 60 / 100;
				}
				if (defender.playerLevel[3] - damage < 0) {
					damage = defender.playerLevel[3];
				}
				if (defender.playerLevel[3] - damage - damage2 < 0) {
					damage2 = defender.playerLevel[3] - damage;
				}
				if (damage < 0)
					damage = 0;
				if (damage2 < 0 && damage2 != -1)
					damage2 = 0;
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark4 = damage4 > 0 ? Hitmark.HIT : Hitmark.MISS;
				addCombatXP(attacker, CombatType.RANGE, damage + (damage2 > 0 ? damage2 : 0));
			} else if (attacker.projectileStage > 0) {
				if (attacker.fullVoidMage() && attacker.playerEquipment[attacker.playerWeapon] == 8841) {
					damage = Misc.random(attacker.getCombat().magicMaxHit() + 13);
				} else {
					damage = Misc.random(attacker.getCombat().magicMaxHit());
				}
				if (damage > 0 && EquipmentSet.AHRIM.isWearing(attacker) && attacker.getItems().isWearingItem(12853)
						&& Misc.random(100) < 30) {
					damage = Misc.random((int) (attacker.getCombat().magicMaxHit() + (attacker.getCombat().magicMaxHit() * .3)));
				}
				if (attacker.getCombat().godSpells()) {
					if (System.currentTimeMillis() - attacker.godSpellDelay < Config.GOD_SPELL_CHARGE) {
						damage += 10;
					}
				}
				if (damage == 0 && attacker.MAGIC_SPELLS[attacker.oldSpellId][6] != 0) {
					attacker.magicFailed = true;
				}
				if (attacker.magicFailed) {
					damage = 0;
				}
				if (defender.prayerActive[16]) {
					damage = damage * 60 / 100;
				}
				if (defender.playerLevel[3] - damage < 0) {
					damage = defender.playerLevel[3];
				}
				if (attacker.magicDef) {
					attacker.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 1);
					attacker.getPA().refreshSkill(1);
				}
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark4 = damage4 > 0 ? Hitmark.HIT : Hitmark.MISS;
				addCombatXP(attacker, CombatType.MAGE, damage + (damage2 > 0 ? damage2 : 0));
				attacker.getPA().refreshSkill(3);
				attacker.getPA().refreshSkill(6);
			}
		}
		attacker.attackTimer = attacker.getCombat().getAttackDelay(ItemAssistant.getItemName(attacker.playerEquipment[attacker.playerWeapon]).toLowerCase());
		if (attacker.playerEquipment[attacker.playerWeapon] == 1249 && attacker.usingSpecial) {
			return;
		}
		
		int delay = attacker.hitDelay;
		Damage hit1 = new Damage(defender, damage, delay, hitmark1);
		attacker.getDamageQueue().add(hit1);
		if (damage2 > -1) {
			attacker.getDamageQueue().add(new Damage(defender, damage2, delay, hitmark2));
		}
		delay += 1;
		if (damage3 > -1) {
			attacker.getDamageQueue().add(new Damage(defender, damage3, delay, hitmark3));
		}
		if (damage4 > -1) {
			attacker.getDamageQueue().add(new Damage(defender, damage4, delay, hitmark4));
		}
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

	public static void attackPlayer(Player c, int i) {
		if (PlayerHandler.players[i] == null) {
			return;
		}
		c.slayerHelmetEffect = false;
		Player o = PlayerHandler.players[i];
		if (!Config.ADMIN_ATTACKABLE && o.getRights().isBetween(1, 4)) {
			c.sendMessage("You cannot attack " + Config.SERVER_NAME + " administrators!");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (!c.lastSpear.elapsed(4000)) {
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("This player needs to finish what they're doing.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c.playerEquipment[c.playerWeapon] == 4734 && c.playerEquipment[c.playerArrows] > 0 && c.playerEquipment[c.playerArrows] != 4740) {
			c.sendMessage("You must use bolt racks with the karil's crossbow.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c.isInvisible()) {
			c.sendMessage("You cannot attack another player whilst you are invisible.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (o != null && o.isInvisible()) {
			c.sendMessage("You cannot attack another player whilst they are invisible.");
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c == o) {
			c.sendMessage("You cannot attack yourself, "+o.playerName+".");
			return;
		}
		if (c.playerEquipment[c.playerWeapon] == 11907 || c.playerEquipment[c.playerWeapon] == 12899) {
			c.sendMessage("You cannot attack another player with this staff.");
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			if (Boundary.isIn(o, Boundary.DUEL_ARENAS)) {
				DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
				if (Objects.isNull(session)) {
					c.sendMessage("You cannot attack this player.");
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (!session.getPlayers().containsAll(Arrays.asList(o, c))) {
					c.sendMessage("This player is not your opponent.");
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (session instanceof DuelSession) {
					if (!Boundary.isInSameBoundary(c, session.getOther(c), Boundary.DUEL_ARENAS)) {
						c.sendMessage("You cannot attack a player if you're not in the same arena.");
						c.getPA().movePlayer(session.getArenaBoundary().getMinimumX(), session.getArenaBoundary().getMinimumX(), 0);
						c.getCombat().resetPlayerAttack();
						return;
					}
					if (!session.isAttackingOperationable()) {
						c.sendMessage("You cannot attack your opponent yet.");
						c.getCombat().resetPlayerAttack();
						return;
					}
				}
			} else {
				c.sendMessage("You cannot attack a player outside of the duel arena.");
				c.getCombat().resetPlayerAttack();
				return;
			}
		}
		if (c.hasOverloadBoost) {
			c.sendMessage("<col=CC0000>You cannot use overload effects against another player.</col>");
			c.getPotions().resetOverload();
			for (int skillId = 0; skillId < 7; skillId++) {
				if (skillId == 3 || skillId == 5)
					continue;
				c.getPotions().enchanceStat(skillId, true);
			}
			c.getCombat().resetPlayerAttack();
			return;
		}
		if (c.checkCombatDistance(c, o)) {
			resetSpells(c);
			if (c.playerIndex >= PlayerHandler.players.length || c.playerIndex < 0) {
				return;
			}
			if (PlayerHandler.players[i] != null) {
				c.getCombat().strBonus = c.playerBonus[10];
				if (PlayerHandler.players[i].isDead) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.teleTimer > 0) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				Player c2 = PlayerHandler.players[i];
				if (c.connectedFrom.equals(c2.connectedFrom)  && (c.getRights().getValue() < 2 || c.getRights().getValue() > 3) && Server.serverlistenerPort != 5555) {
					c.sendMessage("You cannot attack players on the same connection.");
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.playerEquipment[c.playerWeapon] == 9703) {
					c.sendMessage("You cannot attack players with training sword.");
					return;
				}
				if (c.playerEquipment[c.playerWeapon] == 4212 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4213 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4214 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4215 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4216 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4217 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4218 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4219 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4220 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4221 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4222 && c.playerEquipment[c.playerArrows] != 892
						|| c.playerEquipment[c.playerWeapon] == 4223 && c.playerEquipment[c.playerArrows] != 892) {
					c.sendMessage("You must use Rune Arrows with this bow.");
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.respawnTimer > 0 || PlayerHandler.players[i].respawnTimer > 0) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (!c.getCombat().checkReqs()) {
					return;
				}
				boolean sameSpot = c.absX == PlayerHandler.players[i].getX() && c.absY == PlayerHandler.players[i].getY();
				if (!c.goodDistance(PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), c.getX(), c.getY(), 25) && !sameSpot) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (PlayerHandler.players[i].respawnTimer > 0) {
					PlayerHandler.players[i].playerIndex = 0;
					c.getCombat().resetPlayerAttack();
					return;
				}

				if (PlayerHandler.players[i].heightLevel != c.heightLevel) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				c.followId = i;
				c.followId2 = 0;
				if (c.attackTimer <= 0) {
					c.usingBow = false;
					c.specEffect = 0;
					c.usingRangeWeapon = false;
					c.rangeItemUsed = 0;
					c.usingBow = false;
					c.usingArrows = false;
					c.usingOtherRangeWeapons = false;
					c.usingCross = c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 11785;
					c.projectileStage = 0;

					if (c.absX == PlayerHandler.players[i].absX && c.absY == PlayerHandler.players[i].absY) {
						if (c.freezeTimer > 0) {
							c.getCombat().resetPlayerAttack();
							return;
						}
						c.followId = i;
						c.attackTimer = 0;
						return;
					}
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
							if (c.playerEquipment[c.playerWeapon] == bowId && c.switchDelay.elapsed(2)) {
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
						
						if (c.getItems().isWearingItem(12926)) {
							if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0 || c.getToxicBlowpipeCharge() == 0) {
								c.sendMessage("Your blowpipe is out of ammo or charge.");
								c.getCombat().resetPlayerAttack();
								return;
							}
							c.usingBow = true;
							c.usingArrows = true;
						}
					}
					if (c.autocasting) {
						c.spellId = c.autocastId;
						c.usingMagic = true;
						if (c.MAGIC_SPELLS[c.spellId][0] >= 12861 && c.MAGIC_SPELLS[c.spellId][0] <= 13023) {
							if (c.playerEquipment[c.playerWeapon] != 4675 && c.playerEquipment[c.playerWeapon] != 6914) {
								c.sendMessage("You cannot autocast with your current weapon.");
								c.getCombat().resetPlayerAttack();
								c.getPA().resetAutocast();
								c.stopMovement();
								return;
							}
						}
					}
					if (c.spellId > 0) {
						c.usingMagic = true;
					}
					if (c.clanWarRule[2] && (c.usingBow || c.usingOtherRangeWeapons)) {
						c.sendMessage("You are not allowed to use ranged during this war!");
						return;
					}

					if (c.clanWarRule[0] && (!c.usingBow && !c.usingOtherRangeWeapons && !c.usingMagic)) {
						c.sendMessage("You are not allowed to use melee during this war!");
						return;
					}

					if (c.clanWarRule[1] && c.usingMagic) {
						c.sendMessage("You are not allowed to use magic during this war!");
						c.getCombat().resetPlayerAttack();
						return;
					}
					if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
						DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
								MultiplayerSessionType.DUEL);
						if (!Objects.isNull(session)) {
							if (session.getRules().contains(Rule.NO_RANGE) && (c.usingBow || c.usingOtherRangeWeapons)) {
								c.sendMessage("<Range has been disabled in this duel!");
								c.getCombat().resetPlayerAttack();
								return;
							}
							if (session.getRules().contains(Rule.NO_MELEE) && (!c.usingBow && !c.usingOtherRangeWeapons && !c.usingMagic)) {
								c.sendMessage("<col=CC0000>Melee has been disabled in this duel!");
								c.getCombat().resetPlayerAttack();
								return;
							}
							if (session.getRules().contains(Rule.NO_MAGE) && c.usingMagic) {
								c.sendMessage("<col=CC0000>Magic has been disabled in this duel!");
								c.getCombat().resetPlayerAttack();
								return;
							}
							if (session.getRules().contains(Rule.WHIP_AND_DDS)) {
								String weaponName = ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase();
								if (!weaponName.contains("whip") && !weaponName.contains("dragon dagger") || weaponName.contains("tentacle")) {
									c.sendMessage("<col=CC0000>You can only use a whip and dragon dagger in this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_HELM)) {
								if (c.playerEquipment[c.playerHat] > -1) {
									c.sendMessage("<col=CC0000>Wearing helmets has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_AMULET)) {
								if (c.playerEquipment[c.playerAmulet] > -1) {
									c.sendMessage("<col=CC0000>Wearing amulets has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_ARROWS)) {
								if (c.playerEquipment[c.playerArrows] > -1) {
									c.sendMessage("<col=CC0000>Wearing arrows has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_BODY)) {
								if (c.playerEquipment[c.playerChest] > -1) {
									c.sendMessage("Wearing platebodies has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_BOOTS)) {
								if (c.playerEquipment[c.playerFeet] > -1) {
									c.sendMessage("<col=CC0000>Wearing boots has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_GLOVES)) {
								if (c.playerEquipment[c.playerHands] > -1) {
									c.sendMessage("<col=CC0000>Wearing gloves has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_CAPE)) {
								if (c.playerEquipment[c.playerCape] > -1) {
									c.sendMessage("<col=CC0000>Wearing capes has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_LEGS)) {
								if (c.playerEquipment[c.playerLegs] > -1) {
									c.sendMessage("<col=CC0000>Wearing platelegs has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_RINGS)) {
								if (c.playerEquipment[c.playerRing] > -1) {
									c.sendMessage("<col=CC0000>Wearing a ring has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_WEAPON)) {
								if (c.playerEquipment[c.playerWeapon] > -1) {
									c.sendMessage("<col=CC0000>Wearing weapons has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
							if (session.getRules().contains(Rule.NO_SHIELD)) {
								if (c.playerEquipment[c.playerShield] > -1 || c.getItems().is2handed(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase(), c.playerEquipment[c.playerWeapon])) {
									c.sendMessage("<col=CC0000>Wearing shields has been disabled for this duel.");
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
						}
					}

					if ((!c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), 4) && (c.usingOtherRangeWeapons
							&& !c.usingBow && !c.usingMagic))
							|| (!c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), 2) && (!c.usingOtherRangeWeapons
									&& c.getCombat().usingHally() && !c.usingBow && !c.usingMagic))
							|| (!c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), c.getCombat()
									.getRequiredDistance()) && (!c.usingOtherRangeWeapons && !c.getCombat().usingHally() && !c.usingBow && !c.usingMagic))
							|| (!c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[i].getX(), PlayerHandler.players[i].getY(), 10) && (c.usingBow || c.usingMagic))) {
						c.attackTimer = 1;
						if (!c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons && c.freezeTimer > 0) {
							c.getCombat().resetPlayerAttack();
							return;
						}
					}

					if (!c.usingCross && !c.usingArrows && c.usingBow
							&& (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223) && !c.usingMagic) {
						c.sendMessage("You have run out of arrows!");
						c.stopMovement();
						c.getCombat().resetPlayerAttack();
						return;
					}
					if (c.getCombat().correctBowAndArrows() < c.playerEquipment[c.playerArrows] && Config.CORRECT_ARROWS && c.usingBow
							&& !c.getCombat().usingCrystalBow() && c.playerEquipment[c.playerWeapon] != 9185
							&& c.playerEquipment[c.playerWeapon] != 11785 && !c.usingMagic && !c.getItems().isWearingItem(12926)) {
						c.sendMessage("You can't use " + c.getItems().getItemName(c.playerEquipment[c.playerArrows]).toLowerCase() + "'s with a "
								+ c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase() + ".");
						c.stopMovement();
						c.getCombat().resetPlayerAttack();
						return;
					}
					
					if (c.playerEquipment[c.playerWeapon] == 9185 && !c.getCombat().properBolts() && !c.usingMagic
							|| c.playerEquipment[c.playerWeapon] == 11785 && !c.getCombat().properBolts() && !c.usingMagic) {
						c.sendMessage("You must use bolts with a crossbow.");
						c.stopMovement();
						c.getCombat().resetPlayerAttack();
						return;
					}

					if (c.usingBow || c.usingMagic || c.usingOtherRangeWeapons || c.getCombat().usingHally()) {
						c.stopMovement();
					}

					if (!c.getCombat().checkMagicReqs(c.spellId)) {
						c.stopMovement();
						c.getCombat().resetPlayerAttack();
						return;
					}

					c.faceUpdate(i + 32768);
					if (!Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
						if (!c.attackedPlayers.contains(c.playerIndex) && !PlayerHandler.players[c.playerIndex].attackedPlayers.contains(c.playerId)) {
							c.attackedPlayers.add(c.playerIndex);
							c.isSkulled = true;
							c.skullTimer = Config.SKULL_TIMER;
							c.headIconPk = 0;
							c.getPA().requestUpdates();
						}
					}
					c.specAccuracy = 1.0;
					c.specDamage = 1.0;
					c.delayedDamage = c.delayedDamage2 = 0;
					if (c.usingSpecial && !c.usingMagic) {
						if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
							DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(
									c, MultiplayerSessionType.DUEL);
							if (Objects.nonNull(session)) {
								if (session.getRules().contains(Rule.NO_SPECIAL_ATTACK)) {
									c.sendMessage("Special attacks have been disabled during this duel!");
									c.usingSpecial = false;
									c.getItems().updateSpecialBar();
									c.getCombat().resetPlayerAttack();
									return;
								}
								if (c.playerEquipment[c.playerWeapon] == 1249) {
									c.sendMessage("You cannot use this special attack whilst in the duel arena.");
									c.usingSpecial = false;
									c.getItems().updateSpecialBar();
									c.getCombat().resetPlayerAttack();
									return;
								}
							}
						}
						if (c.getCombat().checkSpecAmount(c.playerEquipment[c.playerWeapon])) {
							c.lastArrowUsed = c.playerEquipment[c.playerArrows];
							c.getCombat().activateSpecial(c.playerEquipment[c.playerWeapon], i);
							c.followId = c.playerIndex;
							AttackPlayer.calculateCombatDamage(c, PlayerHandler.players[i]);
						} else {
							c.sendMessage("You don't have the required special energy to use this attack.");
							c.usingSpecial = false;
							c.getItems().updateSpecialBar();
							c.playerIndex = 0;
							c.getCombat().resetPlayerAttack();
							return;
						}
						if (c.usingSpecial) {
							c.usingSpecial = false;
							c.getItems().updateSpecialBar();
							return;
						}
					}
					if (c.playerLevel[3] > 0 && !c.isDead && PlayerHandler.players[i].playerLevel[3] > 0) {
						if (!c.usingMagic) {
							c.startAnimation(c.getCombat().getWepAnim(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase()));
							c.mageFollow = false;
						} else {
							c.startAnimation(c.MAGIC_SPELLS[c.spellId][2]);
							c.mageFollow = true;
							c.followId = c.playerIndex;
						}
					}
					c.logoutDelay.reset();
					Player target = PlayerHandler.players[i];
					target.underAttackBy = c.playerId;
					target.logoutDelay.reset();
					target.singleCombatDelay.reset();
					target.killerId = c.playerId;
					c.lastArrowUsed = 0;
					c.rangeItemUsed = 0;
					if (!c.usingBow && !c.usingMagic && !c.usingSpecial && !c.usingOtherRangeWeapons) { // melee
						c.followId = PlayerHandler.players[c.playerIndex].playerId;
						c.getPA().followPlayer();
						c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
						c.projectileStage = 0;
						c.oldPlayerIndex = i;
					}

					if (c.usingBow && !c.usingOtherRangeWeapons && !c.usingMagic || c.usingCross) { // range hit
																									// delay
						if (c.playerEquipment[c.playerWeapon] >= 4212 && c.playerEquipment[c.playerWeapon] <= 4223) {
							c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
							c.crystalBowArrowCount++;
						} else {
							c.rangeItemUsed = c.playerEquipment[c.playerArrows];
							c.getItems().deleteArrow();
						}
						if (c.usingCross)
							c.usingBow = true;
						c.usingBow = true;
						c.inSpecMode = true;
						c.followId = PlayerHandler.players[c.playerIndex].playerId;
						c.getPA().followPlayer();
						c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
						c.lastArrowUsed = c.playerEquipment[c.playerArrows];
						c.gfx100(c.getCombat().getRangeStartGFX());
						c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
						c.projectileStage = 1;
						c.oldPlayerIndex = i;
						c.getCombat().fireProjectilePlayer();
					}

					if (c.usingOtherRangeWeapons) { // knives, darts, etc hit
													// delay
						c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
						c.getItems().deleteEquipment();
						c.usingRangeWeapon = true;
						c.followId = PlayerHandler.players[c.playerIndex].playerId;
						c.getPA().followPlayer();
						c.gfx100(c.getCombat().getRangeStartGFX());
						c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
						c.projectileStage = 1;
						c.oldPlayerIndex = i;
						c.getCombat().fireProjectilePlayer();
					}

					if (c.usingMagic) { // magic hit delay
						int pX = c.getX();
						int pY = c.getY();
						int nX = PlayerHandler.players[i].getX();
						int nY = PlayerHandler.players[i].getY();
						int offX = (pY - nY) * -1;
						int offY = (pX - nX) * -1;
						c.castingMagic = true;
						c.projectileStage = 2;
						if (c.MAGIC_SPELLS[c.spellId][3] > 0) {
							if (c.getCombat().getStartGfxHeight() == 100) {
								c.gfx100(c.MAGIC_SPELLS[c.spellId][3]);
							} else {
								c.gfx0(c.MAGIC_SPELLS[c.spellId][3]);
							}
						}
						if (c.MAGIC_SPELLS[c.spellId][4] > 0) {
							c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78, c.MAGIC_SPELLS[c.spellId][4],
									c.getCombat().getStartHeight(), c.getCombat().getEndHeight(), -i - 1, c.getCombat().getStartDelay());
						}
						if (c.autocastId > 0) {
							c.followId = c.playerIndex;
							c.followDistance = 5;
						}
						c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
						c.oldPlayerIndex = i;
						c.oldSpellId = c.spellId;
						c.spellId = 0;
						if (c.MAGIC_SPELLS[c.oldSpellId][0] == 12891 && target.isMoving) {
							c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 85, 368, 25, 25, -i - 1, c.getCombat().getStartDelay());
						}
						if (Misc.random(target.getCombat().mageDef()) > Misc.random(c.getCombat().mageAtk())) {
							c.magicFailed = true;
						} else {
							c.magicFailed = false;
						}
						if (c.MAGIC_SPELLS[c.oldSpellId][0] == 12445 && !c.magicFailed) {
							if (System.currentTimeMillis() - o.teleBlockDelay > o.teleBlockLength) {
								o.teleBlockDelay = System.currentTimeMillis();
								o.sendMessage("You have been teleblocked.");
								if (o.prayerActive[16] && System.currentTimeMillis() - o.protMageDelay > 1500)
									o.teleBlockLength = 150000;
								else
									o.teleBlockLength = 300000;
							}
						}
						int freezeDelay = c.getCombat().getFreezeTime();
						if (freezeDelay > 0 && PlayerHandler.players[i].freezeTimer <= -3 && !c.magicFailed) {
							PlayerHandler.players[i].freezeTimer = freezeDelay;
							target.resetWalkingQueue();
							target.getPA().sendFrame126("freezetimer:" + (freezeDelay + 2), 1810);
							target.sendMessage("You have been frozen.");
							target.frozenBy = c.playerId;
						}
						if (!c.autocasting && c.spellId <= 0)
							c.playerIndex = 0;
					}
					if (System.currentTimeMillis() - c.lastDamageCalculation > 1000) {
						calculateCombatDamage(c, PlayerHandler.players[i]);
						c.lastDamageCalculation = System.currentTimeMillis();
					}
					if (c.usingOtherRangeWeapons || c.usingBow) {
						if (c.fightMode == 2)
							c.attackTimer--;
					}
					if (c.usingBow && Config.CRYSTAL_BOW_DEGRADES) { // crystal
																		// bow
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
}