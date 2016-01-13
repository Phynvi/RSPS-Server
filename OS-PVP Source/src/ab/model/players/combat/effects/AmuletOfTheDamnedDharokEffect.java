package ab.model.players.combat.effects;

import ab.model.items.EquipmentSet;
import ab.model.npcs.NPC;
import ab.model.players.Player;
import ab.model.players.combat.Damage;
import ab.model.players.combat.DamageEffect;
import ab.model.players.combat.Hitmark;
import ab.util.Misc;
/**
 * The Amulet of the damned has an effect when worn with the entire Dharok set. 
 * There is a 1/4 chance that during the damage step the attacker will receive 15% of
 * the damage they dealt as a recoil.
 * 
 * @author Jason MacKeigan
 * @date Nov 25, 2014, 2:53:48 AM
 */
public class AmuletOfTheDamnedDharokEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int damageDealt = (int) Math.floor(damage.getAmount() * .15);
		if (damageDealt < 1) {
			return;
		}
		defender.getDamageQueue().add(new Damage(attacker, damageDealt, 1, Hitmark.HIT));
	}
	
	@Override
	public boolean isExecutable(Player operator) {
		return EquipmentSet.DHAROK.isWearingBarrows(operator) && 
				operator.getItems().isWearingItem(12853) && Misc.random(100) < 25;
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		
	}

}
