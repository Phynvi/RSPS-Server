package ab.model.players;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ab.Server;
import ab.event.*;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.players.combat.Hitmark;

/**
 * @author Sanity
 */

public class Potions {

	private Player c;

	public Potions(Player c) {
		this.c = c;
	}

	public void handlePotion(int itemId, int slot) {
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			if (itemId >= 11730 && itemId <= 11733) {
				c.sendMessage("You are not allowed to drink overloads whilst in the duel arena.");
				return;
			}
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(
					c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(Rule.NO_DRINKS)) {
					c.sendMessage("Drinks have been disabled for this duel.");
					return;
				}
			}
		}
		if (c.isDead) {
			return;
		}
		if (!c.lastSpear.elapsed(4000)) {
			c.sendMessage("You are stunned and can not drink!");
			return;
		}
		// System.out.println(c +" POTION TIMER= "+c.potionTimer.elapsed());
		if (c.potionTimer.elapsed(1200)) {
			c.potionTimer.reset();
			//c.foodDelay = c.potDelay;
			//c.getCombat().resetPlayerAttack();
			//c.attackTimer++;
			switch (itemId) {
			case 3040:
				drinkMagicPotion(itemId, 3042, slot, 6, false); // Magic pots
				break;
			case 3042:
				drinkMagicPotion(itemId, 3044, slot, 6, false);
				break;
			case 3044:
				drinkMagicPotion(itemId, 3046, slot, 6, false);
				break;
			case 3046:
				drinkMagicPotion(itemId, 229, slot, 6, false);
				break;
			case 6685: // brews
				doTheBrew(itemId, 6687, slot);
				break;
			case 6687:
				doTheBrew(itemId, 6689, slot);
				break;
			case 6689:
				doTheBrew(itemId, 6691, slot);
				break;
			case 6691:
				doTheBrew(itemId, 229, slot);
				break;
			case 2436:
				drinkStatPotion(itemId, 145, slot, 0, true); // sup attack
				break;
			case 145:
				drinkStatPotion(itemId, 147, slot, 0, true);
				break;
			case 147:
				drinkStatPotion(itemId, 149, slot, 0, true);
				break;
			case 149:
				drinkStatPotion(itemId, 229, slot, 0, true);
				break;
			case 2440:
				drinkStatPotion(itemId, 157, slot, 2, true); // sup str
				break;
			case 157:
				drinkStatPotion(itemId, 159, slot, 2, true);
				break;
			case 159:
				drinkStatPotion(itemId, 161, slot, 2, true);
				break;
			case 161:
				drinkStatPotion(itemId, 229, slot, 2, true);
				break;
			case 2444:
				drinkStatPotion(itemId, 169, slot, 4, false); // range pot
				break;
			case 169:
				drinkStatPotion(itemId, 171, slot, 4, false);
				break;
			case 171:
				drinkStatPotion(itemId, 173, slot, 4, false);
				break;
			case 173:
				drinkStatPotion(itemId, 229, slot, 4, false);
				break;
			case 2432:
				drinkStatPotion(itemId, 133, slot, 1, false); // def pot
				break;
			case 133:
				drinkStatPotion(itemId, 135, slot, 1, false);
				break;
			case 135:
				drinkStatPotion(itemId, 137, slot, 1, false);
				break;
			case 137:
				drinkStatPotion(itemId, 229, slot, 1, false);
				break;
			case 113:
				drinkStatPotion(itemId, 115, slot, 2, false); // str pot
				break;
			case 115:
				drinkStatPotion(itemId, 117, slot, 2, false);
				break;
			case 117:
				drinkStatPotion(itemId, 119, slot, 2, false);
				break;
			case 119:
				drinkStatPotion(itemId, 229, slot, 2, false);
				break;
			case 2428:
				drinkStatPotion(itemId, 121, slot, 0, false); // attack pot
				break;
			case 121:
				drinkStatPotion(itemId, 123, slot, 0, false);
				break;
			case 123:
				drinkStatPotion(itemId, 125, slot, 0, false);
				break;
			case 125:
				drinkStatPotion(itemId, 229, slot, 0, false);
				break;
			case 2442:
				drinkStatPotion(itemId, 163, slot, 1, true); // super def pot
				break;
			case 163:
				drinkStatPotion(itemId, 165, slot, 1, true);
				break;
			case 165:
				drinkStatPotion(itemId, 167, slot, 1, true);
				break;
			case 167:
				drinkStatPotion(itemId, 229, slot, 1, true);
				break;
			case 3024:
				drinkPrayerPot(itemId, 3026, slot, true); // sup restore
				break;
			case 3026:
				drinkPrayerPot(itemId, 3028, slot, true);
				break;
			case 3028:
				drinkPrayerPot(itemId, 3030, slot, true);
				break;
			case 3030:
				drinkPrayerPot(itemId, 229, slot, true);
				break;
			case 10925:
				drinkPrayerPot(itemId, 10927, slot, true); // sanfew serums
				curePoison(0);
				break;
			case 10927:
				drinkPrayerPot(itemId, 10929, slot, true);
				curePoison(0);
				break;
			case 10929:
				drinkPrayerPot(itemId, 10931, slot, true);
				curePoison(0);
				break;
			case 10931:
				drinkPrayerPot(itemId, 229, slot, true);
				curePoison(0);
				break;
			case 2434:
				drinkPrayerPot(itemId, 139, slot, false); // pray pot
				break;
			case 139:
				drinkPrayerPot(itemId, 141, slot, false);
				break;
			case 141:
				drinkPrayerPot(itemId, 143, slot, false);
				break;
			case 143:
				drinkPrayerPot(itemId, 229, slot, false);
				break;
			case 2446:
				drinkAntiPoison(itemId, 175, slot, 0); // anti poisons
				break;
			case 175:
				drinkAntiPoison(itemId, 177, slot, 0);
				break;
			case 177:
				drinkAntiPoison(itemId, 179, slot, 0);
				break;
			case 179:
				drinkAntiPoison(itemId, 229, slot, 0);
				break;
			case 2448:
				drinkAntiPoison(itemId, 181, slot, TimeUnit.MINUTES.toMillis(6)); // anti poisons
				break;
			case 181:
				drinkAntiPoison(itemId, 183, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 183:
				drinkAntiPoison(itemId, 185, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 185:
				drinkAntiPoison(itemId, 229, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 12695:
				drinkStatPotion(itemId, 12697, slot, 0, true);
				enchanceStat(1, true);
				enchanceStat(2, true);
				break;
			case 12697:
				drinkStatPotion(itemId, 12699, slot, 0, true);
				enchanceStat(1, true);
				enchanceStat(2, true);
				break;
			case 12699:
				drinkStatPotion(itemId, 12701, slot, 0, true);
				enchanceStat(1, true);
				enchanceStat(2, true);
				break;
			case 12701:
				drinkStatPotion(itemId, 229, slot, 0, true);
				enchanceStat(1, true);
				enchanceStat(2, true);
				break;
			case 11730:
				doOverload(itemId, 11731, slot);
				break;
			case 11731:
				doOverload(itemId, 11732, slot);
				break;
			case 11732:
				doOverload(itemId, 11733, slot);
				break;
			case 11733:
				doOverload(itemId, 229, slot);
				break;
			case 12905:
				drinkAntiVenom(12907, slot, 0);
				break;
			case 12907:
				drinkAntiVenom(12909, slot, 0);
				break;
			case 12909:
				drinkAntiVenom(12911, slot, 0);
				break;
			case 12911:
				drinkAntiVenom(229, slot, 0);
				break;
			case 12913:
				drinkAntiVenom(12915, slot, TimeUnit.MINUTES.toMillis(5));
				break;
			case 12915:
				drinkAntiVenom(12917, slot, TimeUnit.MINUTES.toMillis(5));
				break;
			case 12917:
				drinkAntiVenom(12919, slot, TimeUnit.MINUTES.toMillis(5));
				break;
			case 12919:
				drinkAntiVenom(229, slot, TimeUnit.MINUTES.toMillis(5));
				break;
			case 2452:
				drinkAntifire(2454, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 2454:
				drinkAntifire(2456, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 2456:
				drinkAntifire(2458, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			case 2458:
				drinkAntifire(229, slot, TimeUnit.MINUTES.toMillis(6));
				break;
			/*
			 * case 3144: drinkStatPotion2(itemId, -1, slot, 1, true);
			 * c.sendMessage("Eating it"); break;
			 */
			}
		}
	}
	
	public void drinkAntifire(int replaceItem, int slot, long duration) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.sendMessage("You now have resistance against dragon fire.");
		c.lastAntifirePotion = System.currentTimeMillis();
		c.antifireDelay = duration;
	}
	
	public void drinkAntiVenom(int replaceItem, int slot, long duration) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		if (c.getVenomDamage() > 0) {
			c.sendMessage("You have been cured of the venom.");
		}
		c.setVenomImmunity(duration);
		c.setVenomDamage((byte) 0);
		c.setLastVenomCure(System.currentTimeMillis());
		c.getPA().requestUpdates();
	}

	public void drinkAntiPoison(int itemId, int replaceItem, int slot, long duration) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		curePoison(duration);
		c.getPA().requestUpdates();
	}

	public void curePoison(long duration) {
		c.setLastPoisonCure(System.currentTimeMillis());
		c.setPoisonImmunity(duration);
		c.setPoisonDamage((byte) 0);
		c.sendMessage("You have cured yourself of the poison.");
		c.getPA().requestUpdates();
	}

	public void eatChoc(int itemId, int replaceItem, int slot, int stat, boolean sup) {
		if (c.potionTimer.elapsed(1200)) {
			c.potionTimer.reset();
			c.startAnimation(829);
			c.getItems().deleteItem(9553, slot, 1);
			if (c.playerLevel[3] < c.getMaximumHealth()) {
				c.playerLevel[3] += 10;
				c.sendMessage("The choc. bomb heals you.");
				if (c.playerLevel[3] > c.getMaximumHealth()) {
					c.playerLevel[3] = c.getMaximumHealth();
				}
			}
			c.getPA().refreshSkill(3);
		}
	}

	public void drinkStatPotion(int itemId, int replaceItem, int slot, int stat, boolean sup) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		enchanceStat(stat, sup);
	}

	public void drinkPrayerPot(int itemId, int replaceItem, int slot, boolean rest) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.playerLevel[5] += (c.getLevelForXP(c.playerXP[5]) * .33);
		if (rest)
			c.playerLevel[5] += 1;
		if (c.playerLevel[5] > c.getLevelForXP(c.playerXP[5]))
			c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
		c.getPA().refreshSkill(5);
		if (rest)
			restoreStats();
	}

	public void restoreStats() {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3)
				continue;
			if (c.playerLevel[j] < c.getLevelForXP(c.playerXP[j])) {
				c.playerLevel[j] += (c.getLevelForXP(c.playerXP[j]) * .33);
				if (c.playerLevel[j] > c.getLevelForXP(c.playerXP[j])) {
					c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
				}
				c.getPA().refreshSkill(j);
				c.getPA().setSkillLevel(j, c.playerLevel[j], c.playerXP[j]);
			}
		}
	}

	public void drinkMagicPotion(int itemId, int replaceItem, int slot, int stat, boolean sup) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		enchanceMagic(stat, sup);

	}

	public void enchanceMagic(int skillID, boolean sup) {
		c.playerLevel[skillID] += getBoostedMagic(skillID, sup);
		c.getPA().refreshSkill(skillID);
	}

	public int getBoostedMagic(int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .06);
		else
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .06);
		if (c.playerLevel[skill] + increaseBy > c.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
			return c.getLevelForXP(c.playerXP[skill]) + increaseBy - c.playerLevel[skill];
		}
		return increaseBy;
	}

	public void doTheBrew(int itemId, int replaceItem, int slot) {
		if (c.playerLevel[3] <= 0 || c.isDead) {
			return;
		}
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(
				c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(session)) {
			if (session.getRules().contains(Rule.NO_FOOD)) {
				c.sendMessage("The saradomin brew has been disabled because of its healing effect.");
				return;
			}
		}
		if (c.foodDelay.elapsed(1800) && !c.getFood().isFood(c.lastClickedItem)) {
			c.foodDelay.reset();
		}
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		int[] toDecrease = { 0, 2, 4, 6 };

		int[] toIncrease = { 1, 3 };
		for (int tD : toDecrease) {
			c.playerLevel[tD] -= getBrewStat(tD, .10);
			if (c.playerLevel[tD] < 0)
				c.playerLevel[tD] = 1;
			c.getPA().refreshSkill(tD);
			c.getPA().setSkillLevel(tD, c.playerLevel[tD], c.playerXP[tD]);
		}
		c.playerLevel[1] += getBrewStat(1, .20);
		if (c.playerLevel[1] > (c.getLevelForXP(c.playerXP[1]) * 1.2 + 1)) {
			c.playerLevel[1] = (int) (c.getLevelForXP(c.playerXP[1]) * 1.2);
		}
		c.getPA().refreshSkill(1);

		c.playerLevel[3] += getBrewStat(3, .15);
		if (c.playerLevel[3] > (c.getMaximumHealth()) * 1.17 + 1) {
			c.playerLevel[3] = (int) (c.getMaximumHealth() * 1.17);
		}
		c.getPA().refreshSkill(3);
	}

	public void enchanceStat(int skillID, boolean sup) {
		c.playerLevel[skillID] += getBoostedStat(skillID, sup);
		c.getPA().refreshSkill(skillID);
	}

	public int getBrewStat(int skill, double amount) {
		return (int) (c.getLevelForXP(c.playerXP[skill]) * amount);
	}

	public int getBoostedStat(int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .20);
		else
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .13) + 1;
		if (c.playerLevel[skill] + increaseBy > c.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
			return c.getLevelForXP(c.playerXP[skill]) + increaseBy - c.playerLevel[skill];
		}
		return increaseBy;
	}

	public void doOverload(int itemId, int replaceItem, int slot) {
		int health = c.playerLevel[3];
		if (health <= 50) {
			c.sendMessage("I should get some more lifepoints before using this!");
			return;
		}
		c.hasOverloadBoost = false;
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.hasOverloadBoost = true;
		createOverloadDamageEvent();
		doOverloadBoost();
		handleOverloadTimers();
		c.getPA().refreshSkill(0);
		c.getPA().refreshSkill(1);
		c.getPA().refreshSkill(2);
		c.getPA().refreshSkill(3);
		c.getPA().refreshSkill(4);
		c.getPA().refreshSkill(6);
	}

	private void createOverloadDamageEvent() {
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_HITMARK_ID);
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_HITMARK_ID, c, new CycleEvent() {
			int time = 5;

			@Override
			public void execute(CycleEventContainer b) {
				if (c == null) {
					b.stop();
					return;
				}
				if (time <= 0) {
					b.stop();
					return;
				}
				if (time > 0) {
					if (c.playerLevel[3] <= 10) {
						b.stop();
						return;
					}
					time--;
					c.startAnimation(3170);
					c.appendDamage(10, Hitmark.HIT);
					c.getPA().refreshSkill(3);
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	public void resetOverload() {
		if (!c.hasOverloadBoost)
			return;
		c.hasOverloadBoost = false;
		int[] toNormalise = { 0, 1, 2, 4, 6 };
		for (int i = 0; i < toNormalise.length; i++) {
			c.playerLevel[toNormalise[i]] = c.getLevelForXP(c.playerXP[toNormalise[i]]);
			c.getPA().refreshSkill(toNormalise[i]);
		}
		c.sendMessage("The effects of the potion have worn off...");
	}

	public void handleOverloadTimers() {
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_BOOST_ID);
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_BOOST_ID, c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer b) {
				if (c == null) {
					b.stop();
					return;
				}
				resetOverload();
			}

			@Override
			public void stop() {

			}
		}, 500); // 5 minutes
	}

	public void doOverloadBoost() {
		int[] toIncrease = { 0, 1, 2, 4, 6 };
		int boost;
		for (int i = 0; i < toIncrease.length; i++) {
			boost = (int) (getOverloadBoost(toIncrease[i]));
			c.playerLevel[toIncrease[i]] += boost;
			if (c.playerLevel[toIncrease[i]] > (c.getLevelForXP(c.playerXP[toIncrease[i]]) + boost))
				c.playerLevel[toIncrease[i]] = (c.getLevelForXP(c.playerXP[toIncrease[i]]) + boost);
			c.getPA().refreshSkill(toIncrease[i]);
		}
	}

	public double getOverloadBoost(int skill) {
		double boost = 1;
		switch (skill) {
		case 0:
		case 1:
		case 2:
			boost = 5 + (c.getLevelForXP(c.playerXP[skill]) * .22);
			break;
		case 4:
			boost = 3 + (c.getLevelForXP(c.playerXP[skill]) * .22);
			break;
		case 6:
			boost = 7;
			break;
		}
		return boost;
	}

	public boolean isPotion(int itemId) {
		String name = c.getItems().getItemName(itemId);
		return name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)");
	}
	
	
}