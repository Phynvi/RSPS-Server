package ab.model.players.packets;

import java.util.Objects;

import ab.Server;
import ab.clip.Region;
import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.event.CycleEventHandler;
import ab.model.multiplayer_session.MultiplayerSessionFinalizeType;
import ab.model.multiplayer_session.MultiplayerSessionStage;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PacketType;
import ab.model.players.skills.Fletching;
import ab.model.players.skills.SkillHandler;
import ab.model.players.skills.farming.FarmingConstants;
import ab.util.Misc;

/**
 * Click Object
 */
public class ClickObject implements PacketType {

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
			THIRD_CLICK = 70;

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		c.clickObjectType = c.objectX = c.objectId = c.objectY = 0;
		c.objectYOffset = c.objectXOffset = 0;
		c.getPA().resetFollow();
		switch (packetType) {
		case FIRST_CLICK:
			c.objectX = c.getInStream().readSignedWordBigEndianA();
			c.objectId = c.getInStream().readUnsignedWord();
			c.objectY = c.getInStream().readUnsignedWordA();
			c.objectDistance = 1;
			if (c.objectId == 9357) {
				if (Boundary.isIn(c, Boundary.FIGHT_CAVE)) {
					c.getFightCave().leaveGame();
				}
				return;
			}
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				if (c.getRights().isOwner()) {
					c.sendMessage("Object ID: "+c.objectId+", "+c.objectX+", "+c.objectY);
				}
				return;
			}		
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}

			if (c.getRights().isOwner()) {
				c.sendMessage("objectId: " + c.objectId + " objectX: "
						+ c.objectX + " objectY: " + c.objectY);
			}
			if (Math.abs(c.getX() - c.objectX) > 25
					|| Math.abs(c.getY() - c.objectY) > 25) {
				c.resetWalkingQueue();
				break;
			}
                        	if (SkillHandler.isSkilling[9]) {
			Fletching.resetFletching(c);
			}
			switch (c.objectId) {
			case 10068:
				c.sendMessage("Talk to Zul-Areth to start a new Zulrah Instance.");
				//c.getDH().sendDialogues(638, -1);
				/*if (c.getZulrahEvent().getInstancedZulrah() != null) {
					c.sendMessage("You are already in Zul-Andra! Please relog if this is incorrect.");
					return;
				}
				c.getDH().sendDialogues(625, -1);*/
				break;
			
			case 19145:
					if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
						c.startAnimation(645);
						c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
						c.sendMessage("You recharge your prayer points.");
						c.getPA().refreshSkill(5);
					} else {
						c.sendMessage("You already have full prayer points.");
					}
				
				break;
				
				
			case 26645:
				c.getPA().movePlayer(3327, 4751, 0);
				break;
			case 26646:
				c.getPA().movePlayer(3352, 3163, 0);
				break;	
				
				
			case 16529:
				c.getPA().movePlayer(3142, 3513, 0);
				break;
			case 16530:
				c.getPA().movePlayer(3137, 3516, 0);
				break;
			case 9398:// deposit
				c.getPA().sendFrame126("The Bank of OS Perfection - Deposit Box", 7421);
				c.getPA().sendFrame248(4465, 197);// 197 just because you can't
													// see it =\
				c.getItems().resetItems(7423);
				break;
			case 20772:
			case 20771:
			case 20720:
			case 20722:
			case 20770:
			case 20721:
				c.objectDistance = 2;
				break;
			case 16671:
				c.getPA().movePlayer(2840, 3539, 2);
				break;

			case 1733:
				c.objectYOffset = 2;
				break;
			case 24600:
				c.objectDistance = 2;
				break;
			case 11744:
				c.getPA().openUpBank();
				break;
			case 3044:
			case 21764:
			case 17010:
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case FarmingConstants.GRASS_OBJECT:
			case FarmingConstants.HERB_OBJECT:
			case FarmingConstants.HERB_PATCH_DEPLETED:
				c.objectDistance = 6;
				break;
			case 11833:
			case 11834:
				c.objectDistance = 2;
				break;
			case 23271:
				c.objectDistance = 2;
			break;
			case 5094:
			case 5096:
			case 5097:
			case 5098:
				c.objectDistance = 7;
				break;

			case 245:
				c.objectYOffset = -1;
				c.objectDistance = 0;
				break;

			case 272:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 273:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 246:
				c.objectYOffset = 1;
				c.objectDistance = 0;
				break;

			case 4493:
			case 4494:
			case 4496:
			case 4495:
				c.objectDistance = 5;
				break;
			case 10229:
			case 6522:
				c.objectDistance = 2;
				break;
			case 8959:
				c.objectYOffset = 1;
				break;
			case 4417:
				if (c.objectX == 2425 && c.objectY == 3074)
					c.objectYOffset = 2;
				break;
			case 4420:
				if (c.getX() >= 2383 && c.getX() <= 2385) {
					c.objectYOffset = 1;
				} else {
					c.objectYOffset = -2;
				}
				break;
			case 6552:
			case 409:
				c.objectDistance = 2;
				break;
			case 2879:
			case 2878:
				c.objectDistance = 3;
				break;
			case 2558:
				c.objectDistance = 0;
				if (c.absX > c.objectX && c.objectX == 3044)
					c.objectXOffset = 1;
				if (c.absY > c.objectY)
					c.objectYOffset = 1;
				if (c.absX < c.objectX && c.objectX == 3038)
					c.objectXOffset = -1;
				break;
			case 9356:
				c.objectDistance = 2;
				break;
			case 5959:
			case 1815:
			case 5960:
			case 1816:
				c.objectDistance = 0;
				break;

			case 9293:
				c.objectDistance = 2;
				break;
			case 4418:
				if (c.objectX == 2374 && c.objectY == 3131)
					c.objectYOffset = -2;
				else if (c.objectX == 2369 && c.objectY == 3126)
					c.objectXOffset = 2;
				else if (c.objectX == 2380 && c.objectY == 3127)
					c.objectYOffset = 2;
				else if (c.objectX == 2369 && c.objectY == 3126)
					c.objectXOffset = 2;
				else if (c.objectX == 2374 && c.objectY == 3131)
					c.objectYOffset = -2;
				break;
			case 9706:
				c.objectDistance = 0;
				c.objectXOffset = 1;
				break;
			case 9707:
				c.objectDistance = 0;
				c.objectYOffset = -1;
				break;
			case 4419:
			case 6707: // verac
				c.objectYOffset = 3;
				break;
			case 6823:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6706: // torag
				c.objectXOffset = 2;
				break;
			case 6772:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6705: // karils
				c.objectYOffset = -1;
				break;
			case 6822:
				c.objectDistance = 2;
				c.objectYOffset = 1;
				break;

			case 6704: // guthan stairs
				c.objectYOffset = -1;
				break;
			case 6773:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;

			case 6703: // dharok stairs
				c.objectXOffset = -1;
				break;
			case 6771:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;

			case 6702: // ahrim stairs
				c.objectXOffset = -1;
				break;
			case 6821:
				c.objectDistance = 2;
				c.objectXOffset = 1;
				c.objectYOffset = 1;
				break;
				case 3192:
				c.objectDistance = 7;
				break;
			case 1276:
			case 1278:// trees
			case 1281: // oak
			case 1308: // willow
			case 1307: // maple
			case 1309: // yew
			case 1306: // yew
				c.objectDistance = 3;
				break;
			default:
				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;
			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY
					+ c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().firstClickObject(c.objectId, c.objectX,
						c.objectY);
			} else {
				c.clickObjectType = 1;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 1
								&& c.goodDistance(c.objectX + c.objectXOffset,
										c.objectY + c.objectYOffset, c.getX(),
										c.getY(), c.objectDistance)) {
							c.getActions().firstClickObject(c.objectId,
									c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType > 1 || c.clickObjectType == 0)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;

		case SECOND_CLICK:
			c.objectId = c.getInStream().readUnsignedWordBigEndianA();
			c.objectY = c.getInStream().readSignedWordBigEndian();
			c.objectX = c.getInStream().readUnsignedWordA();
			c.objectDistance = 1;
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				return;
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
	if (SkillHandler.isSkilling[9]) {
			Fletching.resetFletching(c);
			}
			if (c.getRights().isOwner()) {
				Misc.println("objectId: " + c.objectId + "  ObjectX: "
						+ c.objectX + "  objectY: " + c.objectY + " Xoff: "
						+ (c.getX() - c.objectX) + " Yoff: "
						+ (c.getY() - c.objectY));
			}

			switch (c.objectId) {
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case 2478:
			case 2483:
			case 2484:
				c.objectDistance = 3;
				break;
			case 6163:
			case 6165:
			case 6166:
			case 6164:
			case 6162:
				c.objectDistance = 2;
				break;
				case 3192:
				c.objectDistance = 7;
				break;
			default:
				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;

			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY
					+ c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().secondClickObject(c.objectId, c.objectX,
						c.objectY);
			} else {
				c.clickObjectType = 2;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 2
								&& c.goodDistance(c.objectX + c.objectXOffset,
										c.objectY + c.objectYOffset, c.getX(),
										c.getY(), c.objectDistance)) {
							c.getActions().secondClickObject(c.objectId,
									c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType < 2 || c.clickObjectType > 2)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;

		case THIRD_CLICK:
			c.objectX = c.getInStream().readSignedWordBigEndian();
			c.objectY = c.getInStream().readUnsignedWord();
			c.objectId = c.getInStream().readUnsignedWordBigEndianA();
			if (!Region.isWorldObject(c.objectId, c.objectX, c.objectY, c.heightLevel)) {
				c.sendMessage("Warning: The object could not be verified by the server. If you feel this is");
				c.sendMessage("incorrect, please contact a staff member to have this resolved.");
				return;
			}
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("Please finish what you're doing.");
				return;
			}
			duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERACTION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.getRights().isOwner()) {
				Misc.println("objectId: " + c.objectId + "  ObjectX: "
						+ c.objectX + "  objectY: " + c.objectY + " Xoff: "
						+ (c.getX() - c.objectX) + " Yoff: "
						+ (c.getY() - c.objectY));
			}

			switch (c.objectId) {
			default:
				c.objectDistance = 1;
				c.objectXOffset = 0;
				c.objectYOffset = 0;
				break;
			}
			if (c.goodDistance(c.objectX + c.objectXOffset, c.objectY
					+ c.objectYOffset, c.getX(), c.getY(), c.objectDistance)) {
				c.getActions().secondClickObject(c.objectId, c.objectX,
						c.objectY);
			} else {
				c.clickObjectType = 3;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.clickObjectType == 3
								&& c.goodDistance(c.objectX + c.objectXOffset,
										c.objectY + c.objectYOffset, c.getX(),
										c.getY(), c.objectDistance)) {
							c.getActions().thirdClickObject(c.objectId,
									c.objectX, c.objectY);
							container.stop();
						}
						if (c.clickObjectType < 3)
							container.stop();
					}

					@Override
					public void stop() {
						c.clickObjectType = 0;
					}
				}, 1);
			}
			break;
		}

	}

	public void handleSpecialCase(Player c, int id, int x, int y) {

	}

}
