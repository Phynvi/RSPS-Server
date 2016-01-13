package ab.model.players.combat.effects;

import ab.model.npcs.NPC;
import ab.model.players.Player;
import ab.model.players.combat.Damage;
import ab.model.players.combat.DamageEffect;
import ab.util.Misc;

public class SerpentineHelmEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		attacker.setVenomDamage((byte) damage.getAmount()); 
		attacker.sendMessage("You have been infected by venom.");
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExecutable(Player operator) {
		return operator.getItems().isWearingItem(12931) && Misc.random(5) == 1;
	}

}
