package ab.model.players.combat.effects;

import ab.model.npcs.NPC;
import ab.model.players.Player;
import ab.model.players.combat.Damage;
import ab.model.players.combat.DamageEffect;
import ab.util.Misc;

public class ToxicBlowpipeEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		if (Misc.random(3) == 0 && defender.isSusceptibleToVenom()) {
			defender.setVenomDamage((byte) damage.getAmount()); 
			defender.sendMessage("You have been infected by venom.");
		}
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isExecutable(Player operator) {
		return operator.getItems().isWearingItem(12926) && operator.usingSpecial;
	}

}
