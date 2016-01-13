package ab.model.players;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import ab.Config;
import ab.model.content.kill_streaks.Killstreak;
import ab.model.content.presets.Preset;
import ab.model.content.presets.PresetContainer;
import ab.model.content.titles.Title;
import ab.model.items.GameItem;
import ab.model.items.bank.BankItem;
import ab.model.items.bank.BankTab;
import ab.model.npcs.NPCDeathTracker;
import ab.util.Misc;

public class PlayerSave {

	/**
	 * Tells us whether or not the player exists for the specified name.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean playerExists(String name) {
		File file = new File("./Data/characters/" + name + ".txt");
		return file.exists();
	}
	
	public static boolean playerExists2(String name) {
		File file = new File("./Data/characters2/" + name + ".txt");
		return file.exists();
	}
	
	public static void addItemToFile(String name, int itemId, int itemAmount) {
		if (itemId < 0 || itemAmount < 0) {
			Misc.println("Illegal operation: Item id or item amount cannot be negative.");
			return;
		}
		BankItem item = new BankItem(itemId + 1, itemAmount);
		if (!playerExists(name)) {
			Misc.println("Illegal operation: Player account does not exist, validate name.");
			return;
		}
		if (PlayerHandler.isPlayerOn(name)) {
			Misc.println("Illegal operation: Attempted to modify the account of a player online.");
			return;
		}
		File character = new File("./Data/characters/" + name + ".txt");
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(character))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch (IOException ioe) { 
			ioe.printStackTrace();
		}
		BankTab[] tabs = new BankTab[] {
			new BankTab(0), new BankTab(1), new BankTab(2), 
			new BankTab(3), new BankTab(4), new BankTab(5), 
			new BankTab(6), new BankTab(7), new BankTab(8), 
		};
		String token, token2;
		String[] token3 = new String[3];
		int spot = 0;
		for (String line : lines) {
			if (line == null) {
				continue;
			}
			line = line.trim();
			spot = line.indexOf("=");
			if (spot == -1) {
				continue;
			}
			token = line.substring(0, spot);
			token = token.trim();
			token2 = line.substring(spot + 1);
			token2 = token2.trim();
			token3 = token2.split("\t");
			if (token.equals("bank-tab")) {
				int tabId = Integer.parseInt(token3[0]);
				int id = Integer.parseInt(token3[1]);
				int amount = Integer.parseInt(token3[2]);
				tabs[tabId].add(new BankItem(id, amount));
			}
		}
		boolean inserted = false;
		for (BankTab tab : tabs) {
			if (tab.contains(item) && tab.spaceAvailable(item)) {
				tab.add(item);
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			for (BankTab tab : tabs) {
				if (tab.freeSlots() > 0) {
					tab.add(item);
					inserted = true;
					break;
				}
			}
		}
		if (!inserted) {
			Misc.println("Item could not be added to offline account, no free space in bank.");
			return;
		}
		int startIndex = Misc.indexOfPartialString(lines, "bank-tab");
		int lastIndex = Misc.lastIndexOfPartialString(lines, "bank-tab");
		if (lastIndex != startIndex && startIndex > 0 && lastIndex > 0) {
			List<String> cutout = lines.subList(startIndex, lastIndex);
			List<String> bankData = new ArrayList<>(lastIndex - startIndex);
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < Config.BANK_SIZE; j++) {
					if (j > tabs[i].size() - 1)
						break;
					BankItem bankItem = tabs[i].getItem(j);
					if (bankItem == null)
						continue;
					bankData.add("bank-tab = " + i + "\t" + bankItem.getId() + "\t" + bankItem.getAmount());
				}
			}
			lines.removeAll(cutout);
			lines.addAll(startIndex, bankData);
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(character))) {
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	/**
	 * Tells us whether or not the specified name has the friend added.
	 * 
	 * @param name
	 * @param friend
	 * @return
	 */
	public static boolean isFriend(String name, String friend) {
		long nameLong = Misc.playerNameToInt64(friend);
		long[] friends = getFriends(name);
		if (friends != null && friends.length > 0) {
			for (int index = 0; index < friends.length; index++) {
				if (friends[index] == nameLong) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isFriend2(String name, String friend) {
		long nameLong = Misc.playerNameToInt64(friend);
		long[] friends = getFriends2(name);
		if (friends != null && friends.length > 0) {
			for (int index = 0; index < friends.length; index++) {
				if (friends[index] == nameLong) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a characters friends in the form of a long array.
	 * 
	 * @param name
	 * @return
	 */
	public static long[] getFriends(String name) {
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		long[] readFriends = new long[200];
		long[] friends = null;
		int totalFriends = 0;
		int index = 0;
		File input = new File("./Data/characters/"+ name + ".txt");
		if (!input.exists()) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token3 = token2.split("\t");
					if (token.equals("character-friend")) {
						try {
							readFriends[index] = Long.parseLong(token2);
							totalFriends++;
						} catch (NumberFormatException nfe) {}
					}
				}
			}
			reader.close();
			if (totalFriends > 0) {
				friends = new long[totalFriends];
				for (int i = 0; i < totalFriends; i++) {
					friends[i] = readFriends[i];
				}
				return friends;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	public static long[] getFriends2(String name) {
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		long[] readFriends = new long[200];
		long[] friends = null;
		int totalFriends = 0;
		int index = 0;
		File input = new File("./Data/characters2/"+ name + ".txt");
		if (!input.exists()) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token3 = token2.split("\t");
					if (token.equals("character-friend")) {
						try {
							readFriends[index] = Long.parseLong(token2);
							totalFriends++;
						} catch (NumberFormatException nfe) {}
					}
				}
			}
			reader.close();
			if (totalFriends > 0) {
				friends = new long[totalFriends];
				for (int i = 0; i < totalFriends; i++) {
					friends[i] = readFriends[i];
				}
				return friends;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	/**
	 * Loading
	 **/
	public static int loadGame(Player p, String playerName, String playerPass) {
		System.out.println("Loading game: " + playerName);
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		boolean File1 = false;
		try {
			if (!Config.PLACEHOLDER_ECONOMY) {
			characterfile = new BufferedReader(new FileReader(
					"./Data/characters/" + playerName + ".txt"));
			File1 = true;
			} else {
				characterfile = new BufferedReader(new FileReader(
						"./Data/characters2/" + playerName + ".txt"));
				File1 = true;
			}
		} catch (FileNotFoundException fileex1) {
		}
		if (!Config.PLACEHOLDER_ECONOMY) {
		if (playerExists(Misc.removeSpaces(playerName))
				&& !playerExists(playerName)) {
			p.disconnected = true;
			return 3;
		}
		} else {
		if (playerExists2(Misc.removeSpaces(playerName))
				&& !playerExists2(playerName)) {
			p.disconnected = true;
			return 3;
		}
		}
		if (File1) {
			// new File ("./characters/"+playerName+".txt");
		} else {
			Misc.println(playerName + ": character file not found.");
			p.newPlayer = false;
			return 0;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			Misc.println(playerName + ": error loading file.");
			return 3;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token3 = token2.split("\t");
				switch (ReadMode) {
				case 1:
					if (token.equals("character-password")) {
						if (playerPass.equalsIgnoreCase(token2) || Misc.basicEncrypt(playerPass).equals(token2) || Misc.md5Hash(playerPass).equals(token2)) {
							playerPass = token2;
						} else {
							return 3;
						}
					}
					break;
				case 2:
					if (token.equals("character-height")) {
						p.heightLevel = Integer.parseInt(token2);
					} else if (token.equals("play-time")) {
						p.pTime = Integer.parseInt(token2);
					} else if (token.equals("character-specRestore")) {
						p.specRestore = Integer.parseInt(token2);
					} else if (token.equals("character-posx")) {
						p.teleportToX = (Integer.parseInt(token2) <= 0 ? 3210
								: Integer.parseInt(token2));
					} else if (token.equals("character-posy")) {
						p.teleportToY = (Integer.parseInt(token2) <= 0 ? 3424
								: Integer.parseInt(token2));
					} else if (token.equals("character-rights")) {
						Rights right = Rights.get(Integer.parseInt(token2));
						p.setRights(right);
					} else if (token.equals("character-title-updated")) {
						p.getTitles().setCurrentTitle(token2);
					} else if (token.equals("character-title-color")) {
						p.getTitles().setCurrentTitleColor(token2);
					} else if (token.equals("killed-players")) {
						if (!p.getPlayerKills().getList().contains(token2))
							p.getPlayerKills().getList().add(token2);
					} else if (token.equals("connected-from")) {
						p.lastConnectedFrom.add(token2);
					} else if (token.equals("desert-treasure")) {
						p.desertT = Integer.parseInt(token2);
					} else if (token.equals("dt-kill")) {
						p.lastDtKill = Integer.parseInt(token2);
					} else if (token.equals("horror-from-deep")) {
						p.horrorFromDeep = Integer.parseInt(token2);
					} else if (token.equals("rfd-round")) {
						p.rfdRound = Integer.parseInt(token2);
					} else if (token.equals("quest-points")) {
						p.questPoints = Integer.parseInt(token2);
					} else if (token.equals("bank-pin")) {
						p.getBankPin().setPin(token2);
					} else if (token.equals("bank-pin-cancellation")) {
						p.getBankPin().setAppendingCancellation(
								Boolean.parseBoolean(token2));
					} else if (token.equals("bank-pin-cancellation-delay")) {
						p.getBankPin().setCancellationDelay(
								Long.parseLong(token2));
					} else if (token.equals("cooks-assistant")) {
						p.cooksA = Integer.parseInt(token2);
					} else if (token.equals("startPack")) {
						p.startPack = Boolean.parseBoolean(token2);
					} else if (token.equals("lastLoginDate")) {
						p.lastLoginDate = Integer.parseInt(token2);
					} else if (token.equals("summonId")) {
						p.summonId = Integer.parseInt(token2);
					} else if (token.equals("has-npc")) {
						p.hasNpc = Boolean.parseBoolean(token2);
					} else if (token.equals("setPin")) {
						p.setPin = Boolean.parseBoolean(token2);
					} else if (token.equals("hasBankpin")) {
						p.hasBankpin = Boolean.parseBoolean(token2);
					} else if (token.equals("wave-id")) {
						p.waveId = Integer.parseInt(token2);
					} else if (token.equals("wave-type")) {
						p.waveType = Integer.parseInt(token2);
					} else if (token.equals("wave-info")) {
						for(int i = 0; i < p.waveInfo.length; i++)
							p.waveInfo[i] = Integer.parseInt(token3[i]);
					} else if (token.equals("zulrah-best-time")) {
						p.setBestZulrahTime(Long.parseLong(token2));
					} else if (token.equals("toxic-pipe-ammo")) {
						p.setToxicBlowpipeAmmo(Integer.parseInt(token2));
					} else if (token.equals("toxic-pipe-amount")) {
						p.setToxicBlowpipeAmmoAmount(Integer.parseInt(token2));
					} else if (token.equals("toxic-pipe-charge")) {
						p.setToxicBlowpipeCharge(Integer.parseInt(token2));
					} else if (token.equals("serpentine-helm")) {
						p.setSerpentineHelmCharge(Integer.parseInt(token2));
					} else if (token.equals("toxic-staff-of-the-dead")) {
						p.setToxicStaffOfDeadCharge(Integer.parseInt(token2));
					} else if (token.equals("trident-of-the-seas")) {
						p.setTridentCharge(Integer.parseInt(token2));
					} else if (token.equals("trident-of-the-swamp")) {
						p.setToxicTridentCharge(Integer.parseInt(token2));
					} else if (token.equals("pinRegisteredDeleteDay")) {
						p.pinDeleteDateRequested = Integer.parseInt(token2);
					} else if (token.equals("requestPinDelete")) {
						p.requestPinDelete = Boolean.parseBoolean(token2);
					} else if (token.equals("bankPin1")) {
						p.bankPin1 = Integer.parseInt(token2);
					} else if (token.equals("bankPin2")) {
						p.bankPin2 = Integer.parseInt(token2);
					} else if (token.equals("bankPin3")) {
						p.bankPin3 = Integer.parseInt(token2);
					} else if (token.equals("bankPin4")) {
						p.bankPin4 = Integer.parseInt(token2);
					} else if (token.equals("tutorial-progress")) {
						p.tutorial = Integer.parseInt(token2);
					} else if (token.equals("crystal-bow-shots")) {
						p.crystalBowArrowCount = Integer.parseInt(token2);
					} else if (token.equals("skull-timer")) {
						p.skullTimer = Integer.parseInt(token2);
					} else if (token.equals("magic-book")) {
						p.playerMagicBook = Integer.parseInt(token2);
					} else if (token.equals("slayer-recipe")) {
						p.slayerRecipe = Boolean.parseBoolean(token2);
					} else if (token.equals("claimedReward")) {
						p.claimedReward = Boolean.parseBoolean(token2);
					} else if (token.equals("brother-info")) {
						p.barrowsNpcs[Integer.parseInt(token3[0])][1] = Integer
								.parseInt(token3[1]);
					} else if (token.equals("special-amount")) {
						p.specAmount = Double.parseDouble(token2);
					} else if (token.equals("prayer-amount")) {
						p.prayerPoint = Double.parseDouble(token2);
					} else if (token.equals("selected-coffin")) {
						p.randomCoffin = Integer.parseInt(token2);
					} else if (token.equals("dragonfire-shield-charge")) {
						p.setDragonfireShieldCharge(Integer.parseInt(token2));
					} else if (token.equals("pkp")) {
						p.pkp = Integer.parseInt(token2);
					} else if (token.equals("votePoints")) {
						p.votePoints = Integer.parseInt(token2);
					} else if (token.equals("donP")) {
						p.donatorPoints = Integer.parseInt(token2);
					} else if (token.equals("donA")) {
						p.amDonated = Integer.parseInt(token2);
					} else if (token.equals("giftA")) {
						p.amountGifted = Integer.parseInt(token2);
					} else if (token.equals("xpLock")) {
						p.expLock = Boolean.parseBoolean(token2);
					} else if (line.startsWith("KC")) {
						p.KC = Integer.parseInt(token2);
					} else if (line.startsWith("DC")) {
						p.DC = Integer.parseInt(token2);

					} else if (token.equals("teleblock-length")) {
						p.teleBlockDelay = System.currentTimeMillis();
						p.teleBlockLength = Integer.parseInt(token2);
					} else if (token.equals("pc-points")) {
						p.pcPoints = Integer.parseInt(token2);
					} else if (token.equals("total-rogue-kills")) {
						p.getBH().setTotalRogueKills(Integer.parseInt(token2));
					} else if (token.equals("total-hunter-kills")) {
						p.getBH().setTotalHunterKills(Integer.parseInt(token2));
					} else if (token.equals("target-time-delay")) {
						p.getBH().setDelayedTargetTicks(Integer.parseInt(token2));
					} else if (token.equals("bh-penalties")) {
						p.getBH().setWarnings(Integer.parseInt(token2));
					} else if (token.equals("bh-bounties")) {
						p.getBH().setBounties(Integer.parseInt(token2));
					} else if (token.equals("statistics-visible")) {
						p.getBH().setStatisticsVisible(Boolean.parseBoolean(token2));
					} else if (token.equals("spell-accessible")) {
						p.getBH().setSpellAccessible(Boolean.parseBoolean(token2));
					} else if (token.equals("killStreak")) {
						p.killStreak = Integer.parseInt(token2);
					} else if(token.equals("achievement-points")) {
							p.getAchievements().setPoints(Integer.parseInt(token2));
					} else if(token.equals("achievement-items")) {
						for(int i = 0; i < token3.length; i++)
							p.getAchievements().setBoughtItem(i, Integer.parseInt(token3[i]));
					} else if (line.startsWith("fireslit")) {
						p.fireslit = Integer.parseInt(token2);
					} else if (token.equals("bonus-end")) {
						p.bonusXpTime = Long.parseLong(token2);
					} else if (token.equals("jail-end")) {
						p.jailEnd = Long.parseLong(token2);
					} else if (token.equals("mute-end")) {
						p.muteEnd = Long.parseLong(token2);
					} else if (token.equals("marketmute-end")) {
						p.marketMuteEnd = Long.parseLong(token2);
					} else if (token.equals("ban-end")) {
						p.banEnd = Long.parseLong(token2);
					} else if (token.equals("splitChat")) {
						p.splitChat = Boolean.parseBoolean(token2);
					} else if (token.equals("slayerTask")) {
						p.slayerTask = Integer.parseInt(token2);
					} else if (token.equals("slayerPoints")) {
						p.slayerPoints = Integer.parseInt(token2);
					} else if (token.equals("taskAmount")) {
						p.taskAmount = Integer.parseInt(token2);
					} else if (token.equals("magePoints")) {
						p.magePoints = Integer.parseInt(token2);
					} else if (token.equals("autoRet")) {
						p.autoRet = Integer.parseInt(token2);
					} else if (token.equals("barrowskillcount")) {
						p.barrowsKillCount = Integer.parseInt(token2);
					} else if (token.equals("flagged")) {
						p.accountFlagged = Boolean.parseBoolean(token2);
					} else if (token.equals("keepTitle")) {
						p.keepTitle = Boolean.parseBoolean(token2);
					} else if (token.equals("killTitle")) {
						p.killTitle = Boolean.parseBoolean(token2);
					} else if (token.equals("removedTask0")) {
						p.removedTasks[0] = Integer.parseInt(token2);
					} else if (token.equals("removedTask1")) {
						p.removedTasks[1] = Integer.parseInt(token2);
					} else if (token.equals("removedTask2")) {
						p.removedTasks[2] = Integer.parseInt(token2);
					} else if (token.equals("removedTask3")) {
						p.removedTasks[3] = Integer.parseInt(token2);
					} else if (token.equals("wave")) {
						p.waveId = Integer.parseInt(token2);
					} else if (token.equals("void")) {
						for (int j = 0; j < token3.length; j++) {
							p.voidStatus[j] = Integer.parseInt(token3[j]);
						}
					} else if (token.equals("gwkc")) {
						p.killCount = Integer.parseInt(token2);
					} else if (token.equals("fightMode")) {
						p.fightMode = Integer.parseInt(token2);
					} else if (token.equals("privatechat")) {
						p.setPrivateChat(Integer.parseInt(token2));
					} else if (token.equals("farming-patch-0")) {
						p.setFarmingState(0, Integer.parseInt(token3[0]));
						p.setFarmingSeedId(0, Integer.parseInt(token3[1]));
						p.setFarmingTime(0, Integer.parseInt(token3[2]));
						p.setFarmingHarvest(0, Integer.parseInt(token3[3]));
					} else if (token.equals("lost-items")) {
						System.out.println(token3.length);
						if (token3.length > 1) {
							for (int i = 0; i < token3.length; i += 2) {
								int itemId = Integer.parseInt(token3[i]);
								int itemAmount = Integer.parseInt(token3[i + 1]);
								p.getLostItems().add(new GameItem(itemId, itemAmount));
							}
						}
					}
					break;
				case 3:
					if (token.equals("character-equip")) {
						p.playerEquipment[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerEquipmentN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 4:
					if (token.equals("character-look")) {
						p.playerAppearance[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
					}
					break;
				case 5:
					if (token.equals("character-skill")) {
						p.playerLevel[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerXP[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 6:
					if (token.equals("character-item")) {
						p.playerItems[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerItemsN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 7:
					if (token.equals("character-bank")) {
						p.bankItems[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.bankItemsN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
						p.getBank().getBankTab()[0].add(new BankItem(
								Integer.parseInt(token3[1]), Integer
								.parseInt(token3[2])));
					} else if (token.equals("bank-tab")) {
						int tabId = Integer.parseInt(token3[0]);
						int itemId = Integer.parseInt(token3[1]);
						int itemAmount = Integer.parseInt(token3[2]);
						p.getBank().getBankTab()[tabId].add(new BankItem(
								itemId, itemAmount));
					}
					break;
				case 8:
					if (token.equals("character-friend")) {
						p.getFriends().add(Long.parseLong(token3[0]));
					}
					break;
				
				case 9:
					if(token3.length < 2)
						continue;
					p.getAchievements().read(token, 0, Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					break;
				case 10:
					if(token3.length < 2)
						continue;
					p.getAchievements().read(token, 1, Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					break;
				case 11:
					if(token3.length < 2)
						continue;
					p.getAchievements().read(token, 2, Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					break;
				case 12:
					if (token.equals("character-ignore")) {
						p.getIgnores().add(Long.parseLong(token3[0]));
					}
					break;
					
				case 13:
					if (token.equals("stage")) {
						p.getHolidayStages().setStage(token3[0], Integer.parseInt(token3[1]));
					}
					break;
					
				case 14:
					if(token.equals("item")) {
						p.degradableItem[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
					} else if (token.equals("claim-state")) {
						for (int i = 0; i < token3.length; i++) {
							p.claimDegradableItem[i] = Boolean.parseBoolean(token3[i]);
						}
					}
					break;
					
				case 15:
					if (token.startsWith("Names") && token3.length > 0) {
						for (int i = 0; i < token3.length; i++) {
							if (token3[i].equalsIgnoreCase("null")) {
								token3[i] = "New slot";
							}
							Preset preset = p.getPresets().getPresets().get(i);
							preset.setAlias(token3[i]);
						}
					} else if (token.startsWith("Inventory") || token.startsWith("Equipment")) {
						if (token3.length > 2) {
							int presetId = Integer.parseInt(token.split("#")[1]);
							for (int i = 0; i < token3.length; i += 3) {
								int slot = Integer.parseInt(token3[i]);
								int itemId = Integer.parseInt(token3[i + 1]);
								int amount = Integer.parseInt(token3[i + 2]);
								if (token.startsWith("Inventory")) {
									p.getPresets().getPresets().get(presetId).getInventory().getItems().put(
											slot, new GameItem(itemId, amount));
								} else {
									p.getPresets().getPresets().get(presetId).getEquipment().getItems().put(
											slot, new GameItem(itemId, amount));
								}
							}
						}
					}
					break;
					
				case 16:
					try {
						Killstreak.Type type = Killstreak.Type.get(token);
						int value = Integer.parseInt(token2);
						p.getKillstreak().getKillstreaks().put(type, value);
					} catch (NullPointerException | NumberFormatException e) {
						e.printStackTrace();
					}
					break;
					
				case 17:
					try {
						Title title = Title.valueOf(token2);
						if (title != null) {
							p.getTitles().getPurchasedList().add(title);
						}
					} catch (Exception e) { }
					break;
					
				case 18:
					NPCDeathTracker.NPCName name = NPCDeathTracker.NPCName.get(token);
					if(name != null)
					{
						p.getNpcDeathTracker().getTracker().put(name, Integer.parseInt(token2));
					}
					break;
				}
			} else {
				if (line.equals("[ACCOUNT]")) {
					ReadMode = 1;
				} else if (line.equals("[CHARACTER]")) {
					ReadMode = 2;
				} else if (line.equals("[EQUIPMENT]")) {
					ReadMode = 3;
				} else if (line.equals("[LOOK]")) {
					ReadMode = 4;
				} else if (line.equals("[SKILLS]")) {
					ReadMode = 5;
				} else if (line.equals("[ITEMS]")) {
					ReadMode = 6;
				} else if (line.equals("[BANK]")) {
					ReadMode = 7;
				} else if (line.equals("[FRIENDS]")) {
					ReadMode = 8;
				} else if (line.equals("[ACHIEVEMENTS-TIER-1]")) {
					ReadMode = 9;
				} else if (line.equals("[ACHIEVEMENTS-TIER-2]")) {
					ReadMode = 10;
				} else if (line.equals("[ACHIEVEMENTS-TIER-3]")) {
					ReadMode = 11;
				} else if (line.equals("[IGNORES]")) {
					ReadMode = 12;
				} else if (line.equals("[HOLIDAY-EVENTS]")) {
					ReadMode = 13;
				} else if (line.equals("[DEGRADEABLES]")) {
					ReadMode = 14;
				} else if (line.equals("[PRESETS]")) {
					ReadMode = 15;
				} else if (line.equals("[KILLSTREAKS]")) {
					ReadMode = 16;
				} else if (line.equals("[TITLES]")) {
					ReadMode = 17;
				} else if (line.equals("[NPC-TRACKER]")) {
					ReadMode = 18;
				} else if (line.equals("[EOF]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return 1;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}
		return 13;
	}

	public static void save(Player p) {
		saveGame(p);
	}

	/**
	 * Saving
	 **/
	public static boolean saveGame(Player p) {
		if (!p.saveFile || p.newPlayer || !p.saveCharacter) {
			// System.out.println("first");
			return false;
		}
		if (p.playerName == null || PlayerHandler.players[p.playerId] == null) {
			// System.out.println("second");
			return false;
		}
		p.playerName = p.playerName2;
		int tbTime = (int)(p.teleBlockDelay - System.currentTimeMillis() + p.teleBlockLength);
		if(tbTime > 300000 || tbTime < 0){
			tbTime = 0;
		}

		BufferedWriter characterfile = null;
		try {
			if (!Config.PLACEHOLDER_ECONOMY) {
				characterfile = new BufferedWriter(new FileWriter(
						"./Data/characters/" + p.playerName + ".txt"));
				} else {
					characterfile = new BufferedWriter(new FileWriter(
							"./Data/characters2/" + p.playerName + ".txt"));
				}


			/* ACCOUNT */
			characterfile.write("[ACCOUNT]", 0, 9);
			characterfile.newLine();
			characterfile.write("character-username = ", 0, 21);
			characterfile.write(p.playerName, 0, p.playerName.length());
			characterfile.newLine();
			characterfile.write("character-password = ", 0, 21);
			String passToWrite = Misc.md5Hash(p.playerPass);
			characterfile.write(passToWrite, 0, passToWrite.length());
			characterfile.newLine();
			characterfile.newLine();

			/* CHARACTER */
			characterfile.write("[CHARACTER]", 0, 11);
			characterfile.newLine();
			characterfile.write("character-height = ", 0, 19);
			characterfile.write(Integer.toString(p.heightLevel), 0, Integer
					.toString(p.heightLevel).length());
			characterfile.newLine();
			characterfile.write("play-time = ", 0, 12);
			characterfile.write(Integer.toString(p.pTime), 0, Integer.toString(p.pTime).length());
			characterfile.newLine();
			characterfile.write("character-specRestore = ", 0, 24);
			characterfile.write(Integer.toString(p.specRestore), 0, Integer.toString(p.specRestore).length());
			characterfile.newLine();
			characterfile.write("character-posx = ", 0, 17);
			characterfile.write(Integer.toString(p.absX), 0,
					Integer.toString(p.absX).length());
			characterfile.newLine();
			characterfile.write("character-posy = ", 0, 17);
			characterfile.write(Integer.toString(p.absY), 0,
					Integer.toString(p.absY).length());
			characterfile.newLine();
			characterfile.write("bank-pin = " + p.getBankPin().getPin());
			characterfile.newLine();
			characterfile.write("bank-pin-cancellation = "
					+ p.getBankPin().isAppendingCancellation());
			characterfile.newLine();
			characterfile.write("bank-pin-cancellation-delay = "
					+ p.getBankPin().getCancellationDelay());
			characterfile.newLine();
			characterfile.write("character-rights = " + p.getRights().getValue());
			characterfile.newLine();
			characterfile.write("character-title-updated = " + p.getTitles().getCurrentTitle());
			characterfile.newLine();
			characterfile.write("character-title-color = "+p.getTitles().getCurrentTitleColor());
			characterfile.newLine();
			for (int i = 0; i < p.lastConnectedFrom.size(); i++) {
				characterfile.write("connected-from = ", 0, 17);
				characterfile.write(p.lastConnectedFrom.get(i), 0,
						p.lastConnectedFrom.get(i).length());
				characterfile.newLine();
			}
			for (int i = 0; i < p.getPlayerKills().getList().size(); i++) {
				characterfile.write("killed-players = " + p.getPlayerKills().getList().get(i));
				characterfile.newLine();
			}
			for (int i = 0; i < p.removedTasks.length; i++) {
				characterfile.write("removedTask" + i + " = ", 0, 15);
				characterfile.write(Integer.toString(p.removedTasks[i]), 0,
						Integer.toString(p.removedTasks[i]).length());
				characterfile.newLine();
			}
			characterfile.write("desert-treasure = ", 0, 18);
			characterfile.write(Integer.toString(p.desertT), 0, Integer
					.toString(p.desertT).length());
			characterfile.newLine();
			characterfile.write("dt-kill = ", 0, 10);
			characterfile.write(Integer.toString(p.lastDtKill), 0, Integer
					.toString(p.lastDtKill).length());
			characterfile.newLine();
			characterfile.write("horror-from-deep = ", 0, 19);
			characterfile.write(Integer.toString(p.horrorFromDeep), 0, Integer
					.toString(p.horrorFromDeep).length());
			characterfile.newLine();
			characterfile.write("rfd-round = ", 0, 12);
			characterfile.write(Integer.toString(p.rfdRound), 0, Integer
					.toString(p.rfdRound).length());
			characterfile.newLine();
			characterfile.write("quest-points = ", 0, 15);
			characterfile.write(Integer.toString(p.questPoints), 0, Integer
					.toString(p.questPoints).length());
			characterfile.newLine();
			characterfile.write("cooks-assistant = ", 0, 18);
			characterfile.write(Integer.toString(p.cooksA), 0, Integer
					.toString(p.cooksA).length());
			characterfile.newLine();
			characterfile.write("lastLoginDate = ", 0, 16);
			characterfile.write(Integer.toString(p.lastLoginDate), 0, Integer
					.toString(p.lastLoginDate).length());
			characterfile.newLine();
			characterfile.write("has-npc = ", 0, 10);
			characterfile.write(Boolean.toString(p.hasNpc), 0, Boolean.toString(p.hasNpc).length());
			characterfile.newLine();
			characterfile.write("summonId = ", 0, 11);
			characterfile.write(Integer.toString(p.summonId), 0, Integer.toString(p.summonId).length());
			characterfile.newLine();
			characterfile.write("startPack = ", 0, 12);
			characterfile.write(Boolean.toString(p.startPack), 0, Boolean
					.toString(p.startPack).length());
			characterfile.newLine();
			characterfile.write("setPin = ", 0, 9);
			characterfile.write(Boolean.toString(p.setPin), 0, Boolean
					.toString(p.setPin).length());
			characterfile.newLine();
			characterfile.write("slayer-recipe = ", 0, 13);
			characterfile.write(Boolean.toString(p.slayerRecipe), 0, Boolean
					.toString(p.slayerRecipe).length());
			characterfile.newLine();
			characterfile.write("claimedReward = ", 0, 16);
			characterfile.write(Boolean.toString(p.claimedReward), 0, Boolean
					.toString(p.claimedReward).length());
			characterfile.newLine();
			characterfile.write("dragonfire-shield-charge = " + p.getDragonfireShieldCharge());
			characterfile.newLine();
			characterfile.write("wave-id = "+ p.waveId);
			characterfile.newLine();
			characterfile.write("wave-type = "+p.waveType);
			characterfile.newLine();
			characterfile.write("wave-info = "+p.waveInfo[0] + "\t" +p.waveInfo[1] + "\t" +p.waveInfo[2]);
			characterfile.newLine();
			characterfile.write("zulrah-best-time = " + p.getBestZulrahTime());
			characterfile.newLine();
			characterfile.write("toxic-pipe-ammo = " + p.getToxicBlowpipeAmmo());
			characterfile.newLine();
			characterfile.write("toxic-pipe-amount = " + p.getToxicBlowpipeAmmoAmount());
			characterfile.newLine();
			characterfile.write("toxic-pipe-charge = " + p.getToxicBlowpipeCharge());
			characterfile.newLine();
			characterfile.write("serpentine-helm = " + p.getSerpentineHelmCharge());
			characterfile.newLine();
			characterfile.write("toxic-staff-of-dead = " + p.getToxicStaffOfDeadCharge());
			characterfile.newLine();
			characterfile.write("trident-of-the-seas = " + p.getTridentCharge());
			characterfile.newLine();
			characterfile.write("trident-of-the-swamp = " + p.getToxicTridentCharge());
			characterfile.newLine();
			characterfile.write("bankPin1 = ", 0, 11);
			characterfile.write(Integer.toString(p.bankPin1), 0, Integer
					.toString(p.bankPin1).length());
			characterfile.newLine();
			characterfile.write("bankPin2 = ", 0, 11);
			characterfile.write(Integer.toString(p.bankPin2), 0, Integer
					.toString(p.bankPin2).length());
			characterfile.newLine();
			characterfile.write("bankPin3 = ", 0, 11);
			characterfile.write(Integer.toString(p.bankPin3), 0, Integer
					.toString(p.bankPin3).length());
			characterfile.newLine();
			characterfile.write("bankPin4 = ", 0, 11);
			characterfile.write(Integer.toString(p.bankPin4), 0, Integer
					.toString(p.bankPin4).length());
			characterfile.newLine();
			characterfile.write("hasBankpin = ", 0, 13);
			characterfile.write(Boolean.toString(p.hasBankpin), 0, Boolean
					.toString(p.hasBankpin).length());
			characterfile.newLine();
			characterfile.write("pinRegisteredDeleteDay = ", 0, 25);
			characterfile.write(Integer.toString(p.pinDeleteDateRequested), 0,
					Integer.toString(p.pinDeleteDateRequested).length());
			characterfile.newLine();
			characterfile.write("requestPinDelete = ", 0, 19);
			characterfile.write(Boolean.toString(p.requestPinDelete), 0,
					Boolean.toString(p.requestPinDelete).length());
			characterfile.newLine();
			characterfile.write("slayerPoints = ", 0, 14);
			characterfile.write(Integer.toString(p.slayerPoints), 0, Integer
					.toString(p.slayerPoints).length());
			characterfile.newLine();
			characterfile.write("crystal-bow-shots = ", 0, 20);
			characterfile.write(Integer.toString(p.crystalBowArrowCount), 0,
					Integer.toString(p.crystalBowArrowCount).length());
			characterfile.newLine();
			characterfile.write("skull-timer = ", 0, 14);
			characterfile.write(Integer.toString(p.skullTimer), 0, Integer
					.toString(p.skullTimer).length());
			characterfile.newLine();
			characterfile.write("magic-book = ", 0, 13);
			characterfile.write(Integer.toString(p.playerMagicBook), 0, Integer
					.toString(p.playerMagicBook).length());
			characterfile.newLine();
			for (int b = 0; b < p.barrowsNpcs.length; b++) {
				characterfile.write("brother-info = ", 0, 15);
				characterfile.write(Integer.toString(b), 0, Integer.toString(b)
						.length());
				characterfile.write("	", 0, 1);
				characterfile.write(
						p.barrowsNpcs[b][1] <= 1 ? Integer.toString(0)
								: Integer.toString(p.barrowsNpcs[b][1]), 0,
								Integer.toString(p.barrowsNpcs[b][1]).length());
				characterfile.newLine();
			}
			characterfile.write("special-amount = ", 0, 17);
			characterfile.write(Double.toString(p.specAmount), 0, Double
					.toString(p.specAmount).length());
			characterfile.newLine();
			characterfile.write("prayer-amount = ", 0, 16);
			characterfile.write(Double.toString(p.prayerPoint), 0, Double
					.toString(p.prayerPoint).length());
			characterfile.newLine();
			characterfile.write("selected-coffin = ", 0, 18);
			characterfile.write(Integer.toString(p.randomCoffin), 0, Integer
					.toString(p.randomCoffin).length());
			characterfile.newLine();

			characterfile.write("KC = ", 0, 4);
			characterfile.write(Integer.toString(p.KC), 0,
					Integer.toString(p.KC).length());
			characterfile.newLine();
			characterfile.write("DC = ", 0, 4);
			characterfile.write(Integer.toString(p.DC), 0,
					Integer.toString(p.DC).length());
			characterfile.newLine();
			characterfile.write("total-hunter-kills = "+p.getBH().getTotalHunterKills());
			characterfile.newLine();
			characterfile.write("total-rogue-kills = "+p.getBH().getTotalRogueKills());
			characterfile.newLine();
			characterfile.write("target-time-delay = "+p.getBH().getDelayedTargetTicks());
			characterfile.newLine();
			characterfile.write("bh-penalties = "+p.getBH().getWarnings());
			characterfile.newLine();
			characterfile.write("bh-bounties = "+p.getBH().getBounties());
			characterfile.newLine();
			characterfile.write("statistics-visible = "+p.getBH().isStatisticsVisible());
			characterfile.newLine();
			characterfile.write("spell-accessible = "+p.getBH().isSpellAccessible());
			characterfile.newLine();
			characterfile.write("zerkAmount = ", 0, 13);
			characterfile.newLine();
			characterfile.write("pkp = ", 0, 6);
			characterfile.write(Integer.toString(p.pkp), 0, Integer.toString(p.pkp).length());
			characterfile.newLine();
			characterfile.write("donP = ", 0, 6);
			characterfile.write(Integer.toString(p.donatorPoints), 0, Integer
					.toString(p.donatorPoints).length());
			characterfile.newLine();
			characterfile.write("donA = ", 0, 6);
			characterfile.write(Integer.toString(p.amDonated), 0, Integer
					.toString(p.amDonated).length());
			characterfile.newLine();
			characterfile.write("giftA = ", 0, 7);
			characterfile.write(Integer.toString(p.amountGifted), 0, Integer
					.toString(p.amountGifted).length());
			characterfile.newLine();
			characterfile.write("votePoints = ", 0, 13);
			characterfile.write(Integer.toString(p.votePoints), 0, Integer
					.toString(p.votePoints).length());
			characterfile.newLine();
			characterfile.write("achievement-points = "+p.getAchievements().getPoints());
			characterfile.newLine();
			characterfile.write("achievement-items = ");
			for(int i = 0; i < p.getAchievements().getBoughtItems().length; i++)
				characterfile.write("" + p.getAchievements().getBoughtItems()[i][1] + ((i == p.getAchievements().getBoughtItems().length - 1) ? "" : "\t"));
			characterfile.newLine();
			characterfile.write("xpLock = ", 0, 9);
			characterfile.write(Boolean.toString(p.expLock), 0, Boolean
					.toString(p.expLock).length());
			characterfile.newLine();
			characterfile.write("teleblock-length = ", 0, 19);
			characterfile.write(Integer.toString(tbTime), 0,
					Integer.toString(tbTime).length());
			characterfile.newLine();
			characterfile.write("pc-points = ", 0, 12);
			characterfile.write(Integer.toString(p.pcPoints), 0, Integer
					.toString(p.pcPoints).length());
			characterfile.newLine();
			characterfile.write("killStreak = ", 0, 13);
			characterfile.write(Integer.toString(p.killStreak), 0, Integer.toString(p.killStreak).length());
			characterfile.newLine();
			characterfile.write("bonus-end = ", 0, 12);
			characterfile.write(Long.toString(p.bonusXpTime), 0,
					Long.toString(p.bonusXpTime).length());
			characterfile.newLine();
			characterfile.write("jail-end = ", 0, 11);
			characterfile.write(Long.toString(p.jailEnd), 0,
					Long.toString(p.jailEnd).length());
			characterfile.newLine();
			characterfile.write("mute-end = ", 0, 11);
			characterfile.write(Long.toString(p.muteEnd), 0,
					Long.toString(p.muteEnd).length());
			characterfile.newLine();
			characterfile.write("marketmute-end = ", 0, 17);
			characterfile.write(Long.toString(p.marketMuteEnd), 0,
					Long.toString(p.marketMuteEnd).length());
			characterfile.newLine();
			characterfile.write("ban-end = ", 0, 10);
			characterfile.write(Long.toString(p.banEnd), 0,
					Long.toString(p.banEnd).length());
			characterfile.newLine();
			characterfile.write("splitChat = ", 0, 12);
			characterfile.write(Boolean.toString(p.splitChat), 0,
					Boolean.toString(p.splitChat).length());
			characterfile.newLine();
			characterfile.write("slayerTask = ", 0, 13);
			characterfile.write(Integer.toString(p.slayerTask), 0, Integer
					.toString(p.slayerTask).length());
			characterfile.newLine();
			characterfile.write("taskAmount = ", 0, 13);
			characterfile.write(Integer.toString(p.taskAmount), 0, Integer
					.toString(p.taskAmount).length());
			characterfile.newLine();
			characterfile.write("magePoints = ", 0, 13);
			characterfile.write(Integer.toString(p.magePoints), 0, Integer
					.toString(p.magePoints).length());
			characterfile.newLine();
			characterfile.write("autoRet = ", 0, 10);
			characterfile.write(Integer.toString(p.autoRet), 0, Integer
					.toString(p.autoRet).length());
			characterfile.newLine();
			characterfile.write("barrowskillcount = " + p.barrowsKillCount);
			characterfile.newLine();
			characterfile.write("flagged = ", 0, 10);
			characterfile.write(Boolean.toString(p.accountFlagged), 0, Boolean
					.toString(p.accountFlagged).length());
			characterfile.newLine();
			characterfile.write("keepTitle = ", 0, 12);
			characterfile.write(Boolean.toString(p.keepTitle), 0, Boolean
					.toString(p.keepTitle).length());
			characterfile.newLine();
			characterfile.write("killTitle = ", 0, 12);
			characterfile.write(Boolean.toString(p.killTitle), 0, Boolean
					.toString(p.killTitle).length());
			characterfile.newLine();
			characterfile.write("wave = ", 0, 7);
			characterfile.write(Integer.toString(p.waveId), 0, Integer
					.toString(p.waveId).length());
			characterfile.newLine();
			characterfile.write("gwkc = ", 0, 7);
			characterfile.write(Integer.toString(p.killCount), 0, Integer
					.toString(p.killCount).length());
			characterfile.newLine();
			characterfile.write("fightMode = ", 0, 12);
			characterfile.write(Integer.toString(p.fightMode), 0, Integer
					.toString(p.fightMode).length());
			characterfile.newLine();
			characterfile.write("privatechat = ", 0, 14);
			characterfile.write(Integer.toString(p.getPrivateChat()), 0,
					Integer.toString(p.getPrivateChat()).length());
			characterfile.newLine();
			characterfile.write("void = ", 0, 7);
			String toWrite = p.voidStatus[0] + "\t" + p.voidStatus[1] + "\t"
					+ p.voidStatus[2] + "\t" + p.voidStatus[3] + "\t"
					+ p.voidStatus[4];
			characterfile.write(toWrite);
			characterfile.newLine();
			characterfile.write("firesLit = ", 0, 11);
			characterfile.write(Integer.toString(p.fireslit), 0, Integer
					.toString(p.fireslit).length());
			characterfile.newLine();
			characterfile.write("crabsKilled = ", 0, 14);
			characterfile.write(Integer.toString(p.crabsKilled), 0, Integer
					.toString(p.crabsKilled).length());
			characterfile.newLine();
			characterfile.write("farming-patch-0 = "+p.getFarmingState(0)+"\t"+p.getFarmingSeedId(0)+"\t"+p.getFarmingTime(0)+"\t"+p.getFarmingHarvest(0));
			characterfile.newLine();
			characterfile.write("lost-items = ");
			for (GameItem item : p.getLostItems()) {
				if (item == null) {
					continue;
				}
				characterfile.write(item.getId() + "\t"+ item.getAmount() + "\t");
			}
			characterfile.newLine();

			/* EQUIPMENT */
			characterfile.write("[EQUIPMENT]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < p.playerEquipment.length; i++) {
				characterfile.write("character-equip = ", 0, 18);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i)
						.length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(p.playerEquipment[i]), 0,
						Integer.toString(p.playerEquipment[i]).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(p.playerEquipmentN[i]), 0,
						Integer.toString(p.playerEquipmentN[i]).length());
				characterfile.write("	", 0, 1);
				characterfile.newLine();
			}
			characterfile.newLine();

			/* LOOK */
			characterfile.write("[LOOK]", 0, 6);
			characterfile.newLine();
			for (int i = 0; i < p.playerAppearance.length; i++) {
				characterfile.write("character-look = ", 0, 17);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i)
						.length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(p.playerAppearance[i]), 0,
						Integer.toString(p.playerAppearance[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* SKILLS */
			characterfile.write("[SKILLS]", 0, 8);
			characterfile.newLine();
			for (int i = 0; i < p.playerLevel.length; i++) {
				characterfile.write("character-skill = ", 0, 18);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i)
						.length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(p.playerLevel[i]), 0,
						Integer.toString(p.playerLevel[i]).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(p.playerXP[i]), 0, Integer
						.toString(p.playerXP[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* ITEMS */
			characterfile.write("[ITEMS]", 0, 7);
			characterfile.newLine();
			for (int i = 0; i < p.playerItems.length; i++) {
				if (p.playerItems[i] > 0) {
					characterfile.write("character-item = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer
							.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.playerItems[i]), 0,
							Integer.toString(p.playerItems[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.playerItemsN[i]), 0,
							Integer.toString(p.playerItemsN[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* BANK */
			characterfile.write("[BANK]", 0, 6);
			characterfile.newLine();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < Config.BANK_SIZE; j++) {
					if (j > p.getBank().getBankTab()[i].size() - 1)
						break;
					BankItem item = p.getBank().getBankTab()[i].getItem(j);
					if (item == null)
						continue;
					characterfile.write("bank-tab = " + i + "\t" + item.getId() + "\t"
							+ item.getAmount());
					characterfile.newLine();
				}
			}

			characterfile.newLine();
			characterfile.newLine();

			/* FRIENDS */
			characterfile.write("[FRIENDS]", 0, 9);
			characterfile.newLine();
			for (Long friend : p.getFriends().getFriends()) {
				characterfile.write("character-friend = ", 0, 19);
				characterfile.write(Long.toString(friend), 0, Long.toString(friend).length());
				characterfile.newLine();
			}
			characterfile.newLine();
			
			characterfile.newLine();
			characterfile.write("[HOLIDAY-EVENTS]");
			characterfile.newLine();
			for (Entry<String, Integer> entry : p.getHolidayStages().getStages().entrySet()) {
				String key = entry.getKey();
				int value = entry.getValue();
				if (Objects.isNull(key)) {
					continue;
				}
				characterfile.write("stage = "+key+"\t"+value);
				characterfile.newLine();
			}
			characterfile.newLine();
			
			characterfile.write("[DEGRADEABLES]");
			characterfile.newLine();
			characterfile.write("claim-state = ");
			for (int i = 0; i < p.claimDegradableItem.length; i++) {
				characterfile.write(Boolean.toString(p.claimDegradableItem[i]));
				if (i != p.claimDegradableItem.length - 1) {
					characterfile.write("\t");
				}
			}
			characterfile.newLine();
			for(int i = 0; i < p.degradableItem.length; i++) {
				if(p.degradableItem[i] > 0) {
					characterfile.write("item = "+ i + "\t" + p.degradableItem[i]);
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			
			characterfile.newLine();
			characterfile.write("[ACHIEVEMENTS-TIER-1]");
			characterfile.newLine();
			p.getAchievements().print(characterfile, 0);
			characterfile.newLine();
			
			characterfile.newLine();
			characterfile.write("[ACHIEVEMENTS-TIER-2]");
			characterfile.newLine();
			p.getAchievements().print(characterfile, 1);
			characterfile.newLine();
			
			characterfile.newLine();
			characterfile.write("[ACHIEVEMENTS-TIER-3]");
			characterfile.newLine();
			p.getAchievements().print(characterfile, 2);
			characterfile.newLine();
			
			/* IGNORES */
			characterfile.write("[IGNORES]", 0, 9);
			characterfile.newLine();
			for (Long ignore : p.getIgnores().getIgnores()) {
				characterfile.write("character-ignore = ", 0, 19);
				characterfile.write(Long.toString(ignore), 0, Long.toString(ignore).length());
				characterfile.newLine();
			}
			characterfile.newLine();
			
			characterfile.write("[PRESETS]");
			characterfile.newLine();
			
			characterfile.write("Names = ");
			Map<Integer, Preset> presets = p.getPresets().getPresets();
			for (Entry<Integer, Preset> entry : presets.entrySet()) {
				characterfile.write(entry.getValue().getAlias() + "\t");
			}
			characterfile.newLine();
			for (Entry<Integer, Preset> entry : presets.entrySet()) {
				if (entry != null) {
					Preset preset = entry.getValue();
					PresetContainer inventory = preset.getInventory();
					characterfile.write("Inventory#" + entry.getKey()+ " = ");
					for (Entry<Integer, GameItem> item : inventory.getItems().entrySet()) {
						characterfile.write(item.getKey() + "\t" + item.getValue().getId() + "\t"
								+ item.getValue().getAmount() + "\t");
					}
					characterfile.newLine();
					PresetContainer equipment = preset.getEquipment();
					characterfile.write("Equipment#" + entry.getKey()+ " = ");
					for (Entry<Integer, GameItem> item : equipment.getItems().entrySet()) {
						characterfile.write(item.getKey() + "\t" + item.getValue().getId() + "\t"
								+ item.getValue().getAmount() + "\t");
					}
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			
			characterfile.write("[KILLSTREAKS]");
			characterfile.newLine();
			for (Entry<Killstreak.Type, Integer> entry : p.getKillstreak().getKillstreaks().entrySet()) {
				characterfile.write(entry.getKey().name() + " = "+entry.getValue());
				characterfile.newLine();
			}
			characterfile.newLine();
			
			characterfile.write("[TITLES]");
			characterfile.newLine();
			for (Title title : p.getTitles().getPurchasedList()) {
				characterfile.write("title = "+title.name());
				characterfile.newLine();
			}
			characterfile.newLine();
			
			characterfile.write("[NPC-TRACKER]");
			characterfile.newLine();
			for(Entry<NPCDeathTracker.NPCName, Integer> entry : p.getNpcDeathTracker().getTracker().entrySet()) {
				if(entry != null) {
					if(entry.getValue() > 0) {
						characterfile.write(entry.getKey().toString() + " = "+entry.getValue());
						characterfile.newLine();
					}	
				}
			}
			characterfile.newLine();
			
			characterfile.write("[EOF]", 0, 5);
			characterfile.newLine();
			characterfile.newLine();
			characterfile.close();
		} catch (IOException ioexception) {
			Misc.println(p.playerName + ": error writing file.");
			return false;
		}
		return true;
	}

}