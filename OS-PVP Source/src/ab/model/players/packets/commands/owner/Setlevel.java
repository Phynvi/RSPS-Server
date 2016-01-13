package ab.model.players.packets.commands.owner;

import ab.model.content.SkillConstants;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;

/**
 * Change the level of a given skill.
 * 
 * @author Emiel
 */
public class Setlevel implements Command {

	@Override
	public void execute(Player c, String input) {
		int skillId;
		int skillLevel;
		String[] args = input.split(" ");
		System.out.println(input);
		if (args.length < 2) {
			throw new IllegalArgumentException();
		}
		try {
			skillId = Integer.parseInt(args[0]);
			skillLevel = Integer.parseInt(args[1]);
			if (skillId < 0 || skillId > c.playerLevel.length - 1) {
				c.sendMessage("Unable to set level, skill id cannot exceed the range of 0 -> " + (c.playerLevel.length - 1) + ".");
				return;
			}
			if (skillLevel < 1) {
				skillLevel = 1;
			} else if (skillLevel > 99) {
				skillLevel = 99;
			}
			c.playerLevel[skillId] = skillLevel;
			c.playerXP[skillId] = c.getPA().getXPForLevel(skillLevel) + 1;
			c.getPA().refreshSkill(skillId);
			c.sendMessage("set com_" + SkillConstants.SKILL_NAMES[skillId] + ": to " + skillLevel);
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::setlevel skillid level");
		}
	}
}
