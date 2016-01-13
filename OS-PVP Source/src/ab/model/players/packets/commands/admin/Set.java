/**
 * 
 *//*
package ab.model.players.packets.commands.admin;

import java.util.EnumSet;
import java.util.Collection;

import ab.model.players.Player;
import ab.model.players.packets.commands.Command;
import ab.model.players.skills.Skill;

*//**
 * A new version of our set command!
 * @author Chris
 * @date Aug 12, 2015 12:40:37 AM
 *
 *//*
public class Set implements Command {
	
	private enum VariableTypes {
		COM(1, "god"),
		CONFIG(1, "bh", "dxp"),
		TZHAAR(62, "wave");
		
		*//**
		 * The maximum variable number the user is able to set.
		 *//*
		private int maxVar;
		
		*//**
		 * Variable subtypes of the given main variable type.
		 *//*
		private String[] parameters;
		
		*//**
		 * Gets the initial parameters of the given variable.
		 * @return
		 *//*
		private String getParams() {
			return parameters[0];
		}
		
		*//**
		 * Gets a variable type given the specified {@link String} parameters.
		 * @param parameters	the parameters
		 * @return	a variable type
		 *//*
		private VariableTypes forParameters(String parameters) {
			Set<VariableTypes> type = EnumSet<VariableTypes>.allOf(VariableTypes);
			return VariableTypes;
		}

		
		*//**
		 * Constructs a new {@link VariableTypes}
		 * @param maxVar the maximum integer the user is able to set the variable to
		 * @param parameters parameters of the variable
		 *//*
		private VariableTypes(int maxVar, String... parameters) {
			this.maxVar = maxVar;
			this.parameters = parameters;
		}
		
	}

	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		VariableTypes type = VariableTypes.fo
		for (VariableTypes type : VariableTypes.values()) {
			if (args[0].startsWith(type.toString().toLowerCase())) {
				if (args[0].endsWith(type.getParams())) {
					switch (type)
				}
			} else {
				throwInvalid(c);
			}
		}
	}
	
	*//**
	 * Throws a message that notifies the player that they should revise their arguments.
	 * @param player	the player
	 *//*
	private static void throwInvalid(Player player) {
		player.sendMessage("Code red! Invalid var arguments specified! Try again?");
	}
}

*/