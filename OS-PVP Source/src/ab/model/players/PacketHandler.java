package ab.model.players;

import ab.model.players.packets.AttackPlayer;
import ab.model.players.packets.Bank10;
import ab.model.players.packets.Bank5;
import ab.model.players.packets.BankAll;
import ab.model.players.packets.BankAllButOne;
import ab.model.players.packets.BankModifiableX;
import ab.model.players.packets.BankX1;
import ab.model.players.packets.BankX2;
import ab.model.players.packets.ChallengePlayer;
import ab.model.players.packets.ChangeAppearance;
import ab.model.players.packets.ChangeRegions;
import ab.model.players.packets.Chat;
import ab.model.players.packets.ClickItem;
import ab.model.players.packets.ClickNPC;
import ab.model.players.packets.ClickObject;
import ab.model.players.packets.ClickingButtons;
import ab.model.players.packets.ClickingInGame;
import ab.model.players.packets.ClickingStuff;
import ab.model.players.packets.Commands;
import ab.model.players.packets.Dialogue;
import ab.model.players.packets.DropItem;
import ab.model.players.packets.FollowPlayer;
import ab.model.players.packets.IdleLogout;
import ab.model.players.packets.InputField;
import ab.model.players.packets.ItemClick2;
import ab.model.players.packets.ItemClick2OnGroundItem;
import ab.model.players.packets.ItemClick3;
import ab.model.players.packets.ItemOnGroundItem;
import ab.model.players.packets.ItemOnItem;
import ab.model.players.packets.ItemOnNpc;
import ab.model.players.packets.ItemOnObject;
import ab.model.players.packets.ItemOnPlayer;
import ab.model.players.packets.MagicOnFloorItems;
import ab.model.players.packets.MagicOnItems;
import ab.model.players.packets.Moderate;
import ab.model.players.packets.MoveItems;
import ab.model.players.packets.PickupItem;
import ab.model.players.packets.PrivateMessaging;
import ab.model.players.packets.RemoveItem;
import ab.model.players.packets.Report;
import ab.model.players.packets.SelectItemOnInterface;
import ab.model.players.packets.SilentPacket;
import ab.model.players.packets.Trade;
import ab.model.players.packets.Walking;
import ab.model.players.packets.WearItem;
import ab.model.players.packets.action.InterfaceAction;
import ab.model.players.packets.action.JoinChat;
import ab.model.players.packets.action.ReceiveString;
import ab.Config;

public class PacketHandler {

	private static PacketType packetId[] = new PacketType[256];
	
	public static Player c;
	
	static {
		SilentPacket u = new SilentPacket();
		packetId[3] = u;
		packetId[202] = u;
		packetId[77] = u;
		packetId[86] = u;
		packetId[78] = u;
		packetId[36] = u;
		packetId[226] = u;
		packetId[246] = u;
		packetId[148] = u;
		packetId[183] = u;
		packetId[230] = u;
		packetId[136] = u;
		packetId[189] = u;
		packetId[152] = u;
		packetId[200] = u;
		packetId[85] = u;
		packetId[165] = u;
		packetId[238] = u;
		packetId[150] = u;
		packetId[74] = u;
		packetId[234] = u;
		packetId[34] = u;
		packetId[68] = u;
		packetId[79] = u;
		packetId[140] = u;
		packetId[228] = u;
		//packetId[18] = u;
		packetId[223] = u;
		packetId[8] = new Moderate();
		packetId[142] = new InputField();
		packetId[253] = new ItemClick2OnGroundItem();
		packetId[218] = new Report();
		packetId[40] = new Dialogue();
		ClickObject co = new ClickObject();
		packetId[132] = co;
		packetId[252] = co;
		packetId[70] = co;
		packetId[57] = new ItemOnNpc();
		ClickNPC cn = new ClickNPC();
		packetId[72] = cn;
		packetId[131] = cn;
		packetId[155] = cn;
		packetId[17] = cn;
		packetId[21] = cn;
		packetId[18] = cn;
		packetId[124] = new SelectItemOnInterface();
		packetId[16] = new ItemClick2();
		packetId[75] = new ItemClick3();
		packetId[122] = new ClickItem(c);
		packetId[241] = new ClickingInGame();
		packetId[4] = new Chat();
		packetId[236] = new PickupItem();
		packetId[87] = new DropItem();
		packetId[185] = new ClickingButtons();
		packetId[130] = new ClickingStuff();
		packetId[103] = new Commands();
		packetId[214] = new MoveItems();
		packetId[237] = new MagicOnItems();
		packetId[181] = new MagicOnFloorItems();
		packetId[202] = new IdleLogout();
		AttackPlayer ap = new AttackPlayer();
		packetId[73] = ap;
		packetId[249] = ap;
		packetId[128] = new ChallengePlayer();
		packetId[39] = new Trade();
		packetId[139] = new FollowPlayer();
		packetId[41] = new WearItem();
		packetId[145] = new RemoveItem();
		packetId[117] = new Bank5();
		packetId[43] = new Bank10();
		packetId[129] = new BankAll();
		packetId[140] = new BankAllButOne();
		packetId[141] = new BankModifiableX();
		packetId[101] = new ChangeAppearance();
		PrivateMessaging pm = new PrivateMessaging();
		packetId[188] = pm;
		packetId[126] = pm;
		packetId[215] = pm;
		packetId[74] = pm;
		packetId[95] = pm;
		packetId[133] = pm;
		packetId[135] = new BankX1();
		packetId[208] = new BankX2();
		Walking w = new Walking();
		packetId[98] = w;
		packetId[164] = w;
		packetId[248] = w;
		packetId[53] = new ItemOnItem();
		packetId[192] = new ItemOnObject();
		packetId[25] = new ItemOnGroundItem();
		ChangeRegions cr = new ChangeRegions();
		packetId[60] = new JoinChat();
		packetId[127] = new ReceiveString();
		packetId[213] = new InterfaceAction();
		packetId[14] = new ItemOnPlayer();
		packetId[121] = cr;
		packetId[210] = cr;
	}

	public static void processPacket(Player c, int packetType, int packetSize) {
        PacketType p = packetId[packetType];
        if(p != null && packetType > 0 && packetType < 257 && packetType == c.packetType && packetSize == c.packetSize) {
            if (Config.sendServerPackets && c.getRights().isOwner()) {
                c.sendMessage("PacketType: " + packetType + ". PacketSize: " + packetSize + ".");
            }
            try {
                p.processPacket(c, packetType, packetSize);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            c.disconnected = true;
            System.out.println(c.playerName + " is sending invalid PacketType: " + packetType + ". PacketSize: " + packetSize);
        }
    }
	}