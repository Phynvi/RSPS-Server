package ab.model.players.packets.action;

import ab.Server;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.PlayerSave;
import ab.util.Misc;
import ab.world.Clan;

public class ReceiveString implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		String text = player.getInStream().readString();
		int index = text.indexOf(",");
		int id = Integer.parseInt(text.substring(0, index));
		String string = text.substring(index + 1);
		switch (id) {
		case 0:
			if (player.clan != null) {
				player.clan.removeMember(player);
				player.lastClanChat = "";
			}
			break;
		case 1:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 15) {
				string = string.substring(0, 15);
			}
			Clan clan = player.getPA().getClan();
			if (clan == null) {
				Server.clanManager.create(player);
				clan = player.getPA().getClan();
			}
			if (clan != null) {
				clan.setTitle(string);
				player.getPA().sendFrame126(clan.getTitle(), 18306);
				clan.save();
			}
			break;
		case 2:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.playerName)) {
				break;
			}
			if (!PlayerSave.playerExists(string)) {
				player.sendMessage("This player doesn't exist!");
				break;
			}
			clan = player.getPA().getClan();
			if (clan.isBanned(string)) {
				player.sendMessage("You cannot promote a banned member.");
				break;
			}
			if (clan != null) {
				clan.setRank(Misc.formatPlayerName(string), 1);
				player.getPA().setClanData();
				clan.save();
			}
			break;
		case 3:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.playerName)) {
				break;
			}
			if (!PlayerSave.playerExists(string)) {
				player.sendMessage("This player doesn't exist!");
				break;
			}
			clan = player.getPA().getClan();
			if (clan.isRanked(string)) {
				player.sendMessage("You cannot ban a ranked member.");
				break;
			}
			if (clan != null) {
				clan.banMember(Misc.formatPlayerName(string));
				player.getPA().setClanData();
				clan.save();
			}
			break;
		default:
			System.out.println("Received string: identifier=" + id
					+ ", string=" + string);
			break;
		}
	}

}
