package ab.model.npcs;

import ab.model.content.zulrah.Zulrah;
import ab.model.players.Boundary;
import ab.util.Location3D;
import ab.util.Misc;
import ab.util.Stream;

public class NPC {

	public int npcId;
	public int npcType;
        public int summonedBy;
	public int absX, absY;
	public int heightLevel;
	public int makeX, makeY, maxHit, defence, attack, moveX, moveY, direction,
			walkingType;
	public int spawnX, spawnY;
	public int viewX, viewY;
	public int hp;
	public int lastX, lastY;
        public boolean summoner = false;
	public long singleCombatDelay = 0;
	
	private int size = 1;
	
	private boolean transformUpdateRequired;
	private int transformId;
	public Location3D targetedLocation;

	/**
	 * attackType: 0 = melee, 1 = range, 2 = mage
	 */
	public long lastSpecialAttack;
	
	public boolean spawnedMinions;
	
	public int attackType, projectileId, endGfx, spawnedBy, hitDelayTimer, HP,
			maximumHealth, hitDiff, animNumber, actionTimer, enemyX, enemyY;
	public boolean applyDead, isDead, needRespawn, respawns;
	public boolean walkingHome, underAttack;
	public int freezeTimer, attackTimer, killerId, killedBy, oldIndex,
			underAttackBy;
	public long lastDamageTaken;
	public boolean randomWalk;
	public boolean dirUpdateRequired;
	public boolean animUpdateRequired;
	public boolean hitUpdateRequired;
	public boolean updateRequired;
	public boolean forcedChatRequired;
	public boolean faceToUpdateRequired;
	public int firstAttacker;
	public String forcedText;
	public int stage;
	public int totalAttacks;
	private boolean facePlayer = true;

	public NPC(int _npcId, int _npcType) {
		NPCDefinitions definition = NPCDefinitions.get(_npcType);
		npcId = _npcId;
		npcType = _npcType;
		direction = -1;
		isDead = false;
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
		if (definition != null) {
			size = definition.getSize();
			if (size < 1) {
				size = 1;
			}
		}
	}
	
	/**
	 * Determines if the npc can face another player
	 * @return	{@code true} if the npc can face players
	 */
	public boolean canFacePlayer() {
		return facePlayer;
	}
	
	/**
	 * Makes the npcs either able or unable to face other players
	 * @param facePlayer	{@code true} if the npc can face players
	 */
	public void setFacePlayer(boolean facePlayer) {
		this.facePlayer = facePlayer;
	}
	
	/**
	 * Sends the request to a client that the npc should be transformed into another.
	 * @param Id	the id of the new npc
	 */
	public void requestTransform(int id) {
    	transformId = id;
    	npcType = id;
    	transformUpdateRequired = true;
    	updateRequired = true;
	}
	
	public void appendTransformUpdate(Stream str) {
    	str.writeWordBigEndianA(transformId);
	}

	public void updateNPCMovement(Stream str) {
		if (direction == -1) {

			if (updateRequired) {

				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {

			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, Misc.xlateDirectionToClient[direction]);
			if (updateRequired) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}

	public void dealDamage(int damage) {
		if (damage > HP) {
			damage = HP;
		}
		HP -= damage;
	}

	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		updateRequired = true;
	}

	/**
	 * @return the npcId
	 */
	public int getPlayerIdentifier() {
		return npcId;
	}

	/**
	 * Graphics
	 **/

	public int mask80var1 = 0;
	public int mask80var2 = 0;
	protected boolean mask80update = false;

	public void appendMask80Update(Stream str) {
		str.writeWord(mask80var1);
		str.writeDWord(mask80var2);
	}

	public void gfx100(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 6553600;
		mask80update = true;
		updateRequired = true;
	}

	public void gfx0(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 65536;
		mask80update = true;
		updateRequired = true;
	}
	
	public int getOffset() {
		return (int) Math
				.floor(NPCHandler.getNpcDef()[this.npcType].size / 2);
	}
	
	public static int getSpeedForDistance(int distance) {
		switch (distance) {
		case 1:
			return 90;
		case 2:
			return 95;
		case 3:
			return 100;
		case 4:
			return 105;
		case 5:
			return 115;
		case 6:
			return 125;
		case 7:
			return 135;
		case 8:
			return 150;
		default:
			return 150;
		}
	}

	public void appendAnimUpdate(Stream str) {
		str.writeWordBigEndian(animNumber);
		str.writeByte(1);
	}

	public int FocusPointX = -1, FocusPointY = -1;
	public int face = 0;

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void turnNpc(int i, int j) {
		FocusPointX = 2 * i + 1;
		FocusPointY = 2 * j + 1;
		updateRequired = true;

	}

	public void appendFaceEntity(Stream str) {
		str.writeWord(face);
	}

	public void facePlayer(int player) {
		if (!facePlayer) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = player + 32768;
		}
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public void appendFaceToUpdate(Stream str) {
		str.writeWordBigEndian(viewX);
		str.writeWordBigEndian(viewY);
	}

	public void appendNPCUpdateBlock(Stream str) {
		if (!updateRequired)
			return;
		int updateMask = 0;
		if (animUpdateRequired)
			updateMask |= 0x10;
		if (hitUpdateRequired2)
			updateMask |= 8;
		if (mask80update)
			updateMask |= 0x80;
		if (dirUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (transformUpdateRequired)
			updateMask |= 2;
		if (FocusPointX != -1)
			updateMask |= 4;

		str.writeByte(updateMask);

		if (animUpdateRequired)
			appendAnimUpdate(str);
		if (hitUpdateRequired2)
			appendHitUpdate2(str);
		if (mask80update)
			appendMask80Update(str);
		if (dirUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (hitUpdateRequired)
			appendHitUpdate(str);
		if (transformUpdateRequired)
			appendTransformUpdate(str);
		if (FocusPointX != -1)
			appendSetFocusDestination(str);
	}

	public void clearUpdateFlags() {
		updateRequired = false;
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		animUpdateRequired = false;
		dirUpdateRequired = false;
		transformUpdateRequired = false;
		mask80update = false;
		forcedText = null;
		moveX = 0;
		moveY = 0;
		direction = -1;
		FocusPointX = -1;
		FocusPointY = -1;
	}

	public int getNextWalkingDirection() {
		int dir;
		dir = Misc.direction(absX, absY, (absX + moveX), (absY + moveY));
		if (dir == -1)
			return -1;
		dir >>= 1;
		absX += moveX;
		absY += moveY;
		return dir;
	}
	
	public void startAnimation(int animationId) {
		animNumber = animationId;
		animUpdateRequired = true;
		updateRequired = true;
	}

	public void getNextNPCMovement(int i) {
		direction = -1;
		if (NPCHandler.npcs[i].freezeTimer == 0) {
			direction = getNextWalkingDirection();
		}
	}

	public void appendHitUpdate(Stream str) {
		if (HP <= 0) {
			isDead = true;
		}
		str.writeByteC(hitDiff);
		if (hitDiff > 0) {
			str.writeByteS(1);
		} else {
			str.writeByteS(0);
		}
		/*
		 * str.writeByteS(HP); str.writeByteC(MaxHP);
		 */
		str.writeByteS(Misc.getCurrentHP(HP, maximumHealth, 100));
		str.writeByteC(100);
	}

	public int hitDiff2 = 0;
	public boolean hitUpdateRequired2 = false;

	public void appendHitUpdate2(Stream str) {
		if (HP <= 0) {
			isDead = true;
		}
		str.writeByteA(hitDiff2);
		if (hitDiff2 > 0) {
			str.writeByteC(1);
		} else {
			str.writeByteC(0);
		}
		str.writeByteA(HP);
		str.writeByte(maximumHealth);
	}

	public void handleHitMask(int damage) {
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
		}
		updateRequired = true;
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	/*
	 * public NPCDefinition getDefinition() { return definition; }
	 */

	public boolean inMulti() {
		if (Boundary.isIn(this, Zulrah.BOUNDARY)) {
			return true;
		}
		if ((absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607)
				|| (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
				|| (absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967)
				|| (absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967)
				|| (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831)
				|| (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
				|| (absX >= 2824 && absX <= 2944 && absY >= 5258 && absY <= 5369)
				|| (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
				|| (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647)
				|| (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
				|| (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117)
				|| (absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630)
				|| (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464)
				|| (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
				|| (absX >= 2962 && absX <= 3006 && absY >= 3621 && absY <= 3659)
				|| (absX >= 3155 && absX <= 3214 && absY >= 3755 && absY <= 3803)) {
			return true;
		}
		return false;
	}

	public boolean inWild() {
		if (absX > 2941 && absX < 3392 && absY > 3518 && absY < 3966
				|| absX > 2941 && absX < 3392 && absY > 9918 && absY < 10366) {
			return true;
		}
		return false;
	}
	
	public int getSize() {
		return size;
	}
}
