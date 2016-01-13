package ab.model.players.combat.effects;

import java.util.Objects;

import ab.model.items.EquipmentSet;
import ab.model.npcs.NPC;
import ab.model.players.Player;
import ab.model.players.combat.Damage;
import ab.model.players.combat.DamageEffect;
import ab.model.players.combat.Hitmark;
import ab.util.Misc;
/**
 * The Amulet of the Damned has a special effect when wearing full kerils and combating
 * another player. The effect has a 1/4 chance of executing which deals 1/2 of the damage
 * dealt to the player in the last damage step.
 * 
 * @author Jason MacKeigan
 * @date Nov 25, 2014, 2:52:54 AM
 */
public class AmuletOfTheDamnedKarilEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		if (Objects.isNull(attacker) || Objects.isNull(defender) || Objects.isNull(damage)) {
			return;
		}
		if (damage.getAmount() < 2) {
			return;
		}
		int appendedDamage = damage.getAmount() / 2;
		attacker.getDamageQueue().add(new Damage(defender, appendedDamage, 1, Hitmark.HIT));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return EquipmentSet.KARIL.isWearingBarrows(operator) && Misc.random(100) < 25;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {

	}

}
