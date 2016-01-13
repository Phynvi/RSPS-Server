package ab.model.multiplayer_session.clan_wars;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ab.model.multiplayer_session.duel.DuelSessionRules.RuleRequirement;
import ab.model.players.Player;

/**
 * Several rules of clan wars.
 * @author Chris
 * @date Aug 12, 2015 5:59:26 PM
 *
 */
public class ClanWarsRules {
	
	static ArrayList<ClanWarsRule> war_rules = new ArrayList<>();
	
	private int totalRules;
	
	/**
	 * Represents whether the <code>ClanWarsRules</code> contains the specified rule.
	 * @param rule	the <code>ClanWarsRules</code>
	 * @return	true if the set of rules contains the specified rule
	 */
	public static boolean contains(ClanWarsRule rule) {
		Optional<ClanWarsRule> ruleavailable = war_rules.stream().filter(r -> r.ordinal() == rule.ordinal()).findFirst();
		return ruleavailable.isPresent();
	}
	
	/**
	 * Adds the rule to the {@link ArrayList} of rules.
	 * @param rule	the rule
	 */
	public void addRule(ClanWarsRule rule) {
		war_rules.add(rule);
	}
	
	/**
	 * Removes the specified rule from the {@link ArrayList}.
	 * @param rule	the rule to remove.
	 */
	public void removeRule(ClanWarsRule rule) {
		war_rules.remove(rule);
	}
	
	/**
	 * Resets the {@code ClanWarsRule} array list.
	 */
	public void reset() {
		war_rules.clear();
		totalRules = 0;
	}
	
	/**
	 * Gets the total sum of rules added to the rule list.
	 * @return	totalRules	the total amount of rules
	 */
	public int getRuleTotal() {
		return totalRules;
	}
	
	/**
	 * Sets the value of the total amount of rules.
	 * @param ruleTotal
	 */
	public void setRuleTotal(int ruleTotal) {
		this.totalRules = ruleTotal;
	}
	
	private enum ClanWarsRule {
		
		LAST_TEAM_STANDING(113078, "The battle ends when all members of a team have been defeated. Fighters may"
							+ " not rejoin or join the battle after it has begun."),
		IGNORE_FREEZING(3, "The effects of freezing will not be felt."),
		PJ_TIMER(9, "Players must wait 5 seconds before initiating combat."),
		SINGLE_SPELLS(9, "Multi-combat spells will only impact one player."),
		ALLOW_TRIDENT_IN_PVP(9, "Trident of the Seas is allowed in PvP combat."),
		MELEE_DISABLED(113082, "Melee combat is prohibited."),
		RANGE_DISABLED(113128, "Ranging is prohibited."),
		ALL_SPELLBOOKS(6, "All spellbooks are allowed."),
		BINDING_ONLY(13, "Players may only use binding spells."),
		NO_MAGIC(14, "Players may not cast spells in combat."),
		NO_FOOD(113093, "Food may NOT be eaten during the battle."),
		DRINKS(8, "Drinks, such as potions, may be used during the battle."),
		NO_SPECIAL_ATTACKS(113107, "Use of special attacks is prohibited."),
		NO_SOTD(113103, "Toxic Staff of the Dead special attack is prohibited."),
		NO_PRAYER(113, "Prayer is disabled."),
		KILL_ALL_STRAGGLERS(113114, "Stragglers will be killed."),
		IGNORE_5(113118, "5 stragglers will be ignored.");
		
		private int value;
		
		private String description;
		
		private Optional<RuleRequirement> requirement = Optional.empty();
		
		ClanWarsRule(int value, String description) {
			this.value = value;
			this.description = description;
		}
		
		ClanWarsRule(int value, String description, RuleRequirement requirement) {
			this.value = value;
			this.description = description;
			this.requirement = Optional.of(requirement);
		}
		
		/**
		 * Gets the value of the {@code ClanWarsRule}.
		 * @return
		 */
		public int getValue() {
			return value;
		}
		
		/**
		 * Gets a rule requirement.
		 * @return	the requirement, if present, else empty.
		 */
		public Optional<RuleRequirement> getRequirement() {
			return requirement;
		}
		
		private Set<ClanWarsRule> RULES = EnumSet.allOf(ClanWarsRules.ClanWarsRule.class);
		
		/**
		 * Gets whether the rule exists for the specified component.
		 * @param value	the component value of the rule.
		 * @return	true if the rule exists
		 */
		private boolean ruleExists(int value) {
			for (ClanWarsRule rule : RULES) {
				if (rule.getValue() == value) {
					return true;
				}
			}
			return false;
		}
	}
	
	public interface RuleRequirement {
		
		boolean meets(Player player);
		
	}

}
