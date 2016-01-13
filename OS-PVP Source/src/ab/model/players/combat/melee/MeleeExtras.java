package ab.model.players.combat.melee;

import java.util.Objects;

import ab.Server;
import ab.model.multiplayer_session.MultiplayerSessionType;
import ab.model.multiplayer_session.duel.DuelSession;
import ab.model.multiplayer_session.duel.DuelSessionRules.Rule;
import ab.model.npcs.NPCHandler;
import ab.model.players.Boundary;
import ab.model.players.Player;
import ab.model.players.PlayerHandler;
import ab.model.players.combat.Hitmark;
import ab.util.Misc;

public class MeleeExtras {

	public static void applySmite(Player c, int index, int damage) {
		if (!c.prayerActive[23])
			return;
		if (damage <= 0)
			return;
		if (PlayerHandler.players[index] != null) { 
			Player c2 = PlayerHandler.players[index];
			c2.playerLevel[5] -= damage/4;
			if (c2.playerLevel[5] <= 0) {
				c2.playerLevel[5] = 0;
				c2.getCombat().resetPrayers();
			}
			c2.getPA().refreshSkill(5);
		}
	}

	public static void appendVengeanceNPC(Player c, int otherPlayer, int damage) {
		if (damage <= 0)
			return;
		if (c.npcIndex > 0 && NPCHandler.npcs[c.npcIndex] != null) {
			c.forcedText = "Taste vengeance!";
			c.forcedChatUpdateRequired = true;
			c.updateRequired = true;
			c.vengOn = false;
			if ((NPCHandler.npcs[c.npcIndex].HP - damage) > 0) {
				damage = (int)(damage * 0.75);
				if (damage > NPCHandler.npcs[c.npcIndex].HP) {
					damage = NPCHandler.npcs[c.npcIndex].HP;
				}
				NPCHandler.npcs[c.npcIndex].HP -= damage;
				NPCHandler.npcs[c.npcIndex].handleHitMask(damage);
			}
		}	
		c.updateRequired = true;
	}

	public static void appendVengeance(Player c, int otherPlayer, int damage) {
		if (damage <= 0)
			return;
		Player o = PlayerHandler.players[otherPlayer];
		o.forcedText = "Taste vengeance!";
		o.forcedChatUpdateRequired = true;
		o.updateRequired = true;
		o.vengOn = false;
		if ((o.playerLevel[3] - damage) > 0) {
			damage = (int)(damage * 0.75);
			c.appendDamage(damage,  damage > 0 ? Hitmark.HIT : Hitmark.MISS);
			c.addDamageReceived(o.playerName, damage);
			c.getPA().refreshSkill(3);
		}	
		c.updateRequired = true;
	}

	public static void applyRecoilNPC(Player c, int damage, int i) {
		if (damage > 0 && c.playerEquipment[c.playerRing] == 2550) {
			int recDamage = damage / 10;
			if (recDamage < 1) {
				recDamage = 1;
			}
			if (NPCHandler.npcs[i].HP <= 0 || NPCHandler.npcs[i].isDead) {
				return;
			}
			NPCHandler.npcs[i].HP -= recDamage;
			NPCHandler.npcs[i].handleHitMask(recDamage);
			removeRecoil(c);
			c.recoilHits += damage;
		}
	}

	public static void applyRecoil(Player c, int damage, int i) {
		if (Boundary.isIn(c, Boundary.DUEL_ARENAS)) {
			DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(
					c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(session)) {
				if (session.getRules().contains(Rule.NO_RINGS)) {
					return;
				}
			}
		}
		if (damage > 0 && PlayerHandler.players[i].playerEquipment[c.playerRing] == 2550) {
			int recDamage = damage / 10 + 1;
			c.appendDamage(recDamage, recDamage > 0 ? Hitmark.HIT : Hitmark.MISS);
			c.addDamageReceived(PlayerHandler.players[i].playerName, recDamage);
			c.updateRequired = true;
			removeRecoil(c);
			c.recoilHits += damage;
		}	
	}

	public static void removeRecoil(Player c) {
		if(c.recoilHits >= 200) {
                        if (c.playerEquipment[c.playerRing] == 2550) {
			c.getItems().removeItem(2550, c.playerRing);
			c.getItems().deleteItem(2550, c.getItems().getItemSlot(2550), 1);
			c.sendMessage("Your ring of recoil shatters!");
			c.recoilHits = 0;
                        }
		} else {
			c.recoilHits++;
		}
	}

	public static void graniteMaulSpecial(Player c) {
		if (c.playerIndex > 0 && c.playerLevel[3] > 0) {
			Player o = PlayerHandler.players[c.playerIndex];
			if (c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(), c.getCombat().getRequiredDistance())) {
 				if (c.getCombat().checkReqs()) {
					if (c.getCombat().checkSpecAmount(4153) || c.getCombat().checkSpecAmount(12848)) {						
 						boolean hit = Misc.random(c.getCombat().calculateMeleeAttack()) > Misc.random(o.getCombat().calculateMeleeDefence());
						int damage = 0;
						if (hit)
							damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
						if (o.prayerActive[18])
							damage *= .6;
						if(o.playerLevel[3] - damage <= 0) {
							damage = o.playerLevel[3];
						}
						if(o.playerLevel[3] > 0) {
							o.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							c.startAnimation(1667);
							o.gfx100(337);
							o.addDamageReceived(c.playerName, damage);
							o.getPA().refreshSkill(3);
						}
					}	
				}	
			}			
		} else if(c.npcIndex > 0) {
			int x = NPCHandler.npcs[c.npcIndex].absX;
			int y = NPCHandler.npcs[c.npcIndex].absY;
			if (c.goodDistance(c.getX(), c.getY(), x, y, 2)) {
				if (c.getCombat().checkSpecAmount(4153) || c.getCombat().checkSpecAmount(12848)) {						
					int damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
					if(NPCHandler.npcs[c.npcIndex].HP - damage < 0) {
						damage = NPCHandler.npcs[c.npcIndex].HP;
					}
					if(NPCHandler.npcs[c.npcIndex].HP > 0) {
						NPCHandler.npcs[c.npcIndex].HP -= damage;
						NPCHandler.npcs[c.npcIndex].handleHitMask(damage);
						c.startAnimation(1667);
						c.gfx100(337);
					}
				}
			}
		}
	}
}