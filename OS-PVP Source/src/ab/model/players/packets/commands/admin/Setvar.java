package ab.model.players.packets.commands.admin;



import ab.Config;
import ab.model.players.Player;
import ab.model.players.packets.commands.Command;
import ab.model.players.skills.Skill;
import ab.model.wogw.WellOfGoodWill;

/**
 * Mirror, mirror on the wall. Set the variables of them all!
 * @author Chris
 *
 */
public class Setvar implements Command {
	
	@Override
	public void execute(Player c, String input) {
		String[] args = input.split(" ");
		if (args.length != 2) {
			c.sendMessage("Code red! Invalid var arguments specified! Try again?");
			return;
		}
		if (args[0].startsWith("com_")) {
			if (args[0].endsWith("god")) {
				c.setGodmode(Integer.parseInt(args[1]) == 1 ? true : false);
				if (Integer.parseInt(args[1]) == 0) {
					c.playerLevel[3] = 99;
					c.getPA().refreshSkill(3);
					c.playerLevel[5] = 99;
					c.specAmount = 10.0;
					c.getPA().refreshSkill(5);
					c.getPA().requestUpdates();
					c.setSafemode(false);
				} else { 
					c.playerLevel[Skill.STRENGTH.getId()] = 9999;
					c.getPA().refreshSkill(Skill.STRENGTH.getId());
					c.playerLevel[3] = Integer.MAX_VALUE;
					c.getPA().refreshSkill(3);
					c.playerLevel[5] = Integer.MAX_VALUE;
					c.getPA().refreshSkill(5);
					c.specAmount = Integer.MAX_VALUE;
					c.getPA().requestUpdates();
					c.setSafemode(true);
				}
			} else if (args[0].endsWith("debug")) {
				c.setDebug(Integer.parseInt(args[1]));
			} else {
				throwInvalid(c);
				return;
			}
			c.sendMessage("set " + args[0] + ": to " + args[1]);
		} else if (args[0].startsWith("config_")) {
			if (args[0].endsWith("bh")) {
				Config.BOUNTY_HUNTER_ACTIVE = Integer.parseInt(args[1]) == 1 ? true : false;			
			} else if (args[0].endsWith("dxp")) {
				Config.BONUS_WEEKEND = Integer.parseInt(args[1]) == 1 ? true : false;
			} else if (args[0].endsWith("attackable")) {
				Config.ADMIN_ATTACKABLE = Integer.parseInt(args[1]) >= 1 ? true : false;
			} else {
				throwInvalid(c);
				return;
			}
			c.sendMessage("set " + args[0] + ": to " + args[1]);
		} else if (args[0].startsWith("tzhaar_")) {
			if (args[0].endsWith("wave")) {
				c.waveId = Integer.parseInt(args[1]);
			}
			c.sendMessage("set " + args[0] + ": to " + args[1] + " basevar: " + c.waveType);
		} else if (args[0].startsWith("clanwars_")) {
			if (args[0].endsWith("ffa")) {
				//ClanWars.FFA_ENABLED = Integer.parseInt(args[1]) >= 1 ? true : false;
			} else if (args[0].endsWith("challenge")) {
				//ClanWars.CHALLENGE_ENABLED = Integer.parseInt(args[1]) >= 1 ? true : false;
			} else {
				throwInvalid(c);
				return;
			}
			c.sendMessage("set " + args[0] + ": to " + args[1]);
		} else if (args[0].startsWith("wep_")) {
			if (args[0].endsWith("blowpipe")) {
				c.setToxicBlowpipeCharge(Integer.parseInt(args[1]));
				c.setToxicBlowpipeAmmo(Integer.parseInt(args[1]));
				c.setToxicBlowpipeAmmoAmount(Integer.parseInt(args[1]));
			} else if (args[0].endsWith("trident")) {
				c.setTridentCharge(Integer.parseInt(args[1]));
				c.setToxicTridentCharge(Integer.parseInt(args[1]));
			} else if (args[0].endsWith("serp")) {
				c.setSerpentineHelmCharge(Integer.parseInt(args[1]));
			} else if (args[0].endsWith("toxic")) {
				c.setToxicStaffOfDeadCharge(Integer.parseInt(args[1]));
			} else {
				throwInvalid(c);
				return;
			}
			c.sendMessage("set " + args[0] + ": to " + args[1]);
		} else if (args[0].equalsIgnoreCase("help")) {
			c.getDH().sendStatement("Var arguments: com_god, config_bh, config_dxp, tzhaar_wave, config_attackable");
		} else { 
			throwInvalid(c);
		}
	}
	
	/**
	 * Throws a lovely invalid message should the player's arguments be invalid.
	 * @param c	the player
	 */
	private static void throwInvalid(Player c) {
		c.sendMessage("Code red! Invalid var arguments specified! Try again?");
	}

}
