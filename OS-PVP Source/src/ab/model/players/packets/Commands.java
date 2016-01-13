package ab.model.players.packets;

import ab.Config;
import ab.Connection;
import ab.Server;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.packets.commands.Command;
import ab.util.Misc;

/**
 * Commands
 **/
public class Commands implements PacketType {

	public static boolean executeCommand(Player c, String playerCommand, String commandPackage) {
		String commandName = Misc.findCommand(playerCommand);
		String commandInput = Misc.findInput(playerCommand);
		String className;

		if (commandName.length() <= 0) {
			return true;
		} else if (commandName.length() == 1) {
			className = commandName.toUpperCase();
		} else {
			className = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1).toLowerCase();
		}
		try {
			String path = "ab.model.players.packets.commands." + commandPackage + "." + className;
			Class<?> commandClass = Class.forName(path);
			Object instance = commandClass.newInstance();
			if (instance instanceof Command) {
				Command command = (Command) instance;
				command.execute(c, commandInput);
				return true;
			}
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (Exception e) {
			c.sendMessage("Error while executing the following command: " + playerCommand);
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString();
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c) && !c.getRights().isOwner()) {
			c.sendMessage("You cannot execute a command whilst trading, or dueling.");
			return;
		}

		if (playerCommand.startsWith("changepass")) {
			Server.getChatLogHandler().logMessage(c, "Command", "", "::changepassword");
		} else {
			Server.getChatLogHandler().logMessage(c, "Command", "", "::" + playerCommand);
		}


		if (playerCommand.startsWith("/")) {
			if (Connection.isMuted(c)) {
				c.sendMessage("You are muted for breaking a rule.");
				return;
			}
			if (c.clan != null) {
				c.clan.sendChat(c, playerCommand);
				return;
			}
			c.sendMessage("You can only do this in a clan chat..");
			return;
		}
		
		if (c.getRights().isOwner() && executeCommand(c, playerCommand, "owner")) {
			return;
		} else if (c.getRights().isBetween(2, 3) && executeCommand(c, playerCommand, "admin")) {
			return;
		} else if (c.getRights().isBetween(1, 3) && executeCommand(c, playerCommand, "moderator")) {
			return;
		} else if ((c.getRights().isBetween(1, 3) || c.getRights().isHelper()) && executeCommand(c, playerCommand, "helper")) {
			return;
		} else if (c.getRights().getValue() >= 1 && executeCommand(c, playerCommand, "donator")) {
			return;
		} else if (executeCommand(c, playerCommand, "all")) {
			return;
		}

	}
}