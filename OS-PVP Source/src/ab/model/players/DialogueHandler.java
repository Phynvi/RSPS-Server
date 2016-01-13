package ab.model.players;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import ab.Config;
import ab.Server;
import ab.event.CycleEventHandler;
import ab.model.holiday.HolidayController;
import ab.model.holiday.christmas.ChristmasToy;
import ab.model.holiday.christmas.ChristmasToyUpgrade;
import ab.model.items.ItemAssistant;
import ab.model.minigames.Dicing;
import ab.model.minigames.bounty_hunter.BountyHunterEmblem;
import ab.model.players.combat.Degrade;
import ab.model.players.combat.Degrade.DegradableItem;
import ab.server.data.SerializablePair;
import ab.util.Misc;

public class DialogueHandler {

	public Player c;

	public DialogueHandler(Player client) {
		this.c = client;
	}

	/**
	 * Handles all talking
	 * 
	 * @param dialogue
	 *            The dialogue you want to use
	 * @param npcId
	 *            The npc id that the chat will focus on during the chat
	 */
	public final int 
	HAPPY = 588, 
	CALM = 589, 
	CALM_CONTINUED = 590, 
	CONTENT = 591, 
	EVIL = 592, 
	EVIL_CONTINUED = 593, 
	DELIGHTED_EVIL = 594, 
	ANNOYED = 595, 
	DISTRESSED = 596, 
	DISTRESSED_CONTINUED = 597, 
	NEAR_TEARS = 598,
	SAD = 599, 
	DISORIENTED_LEFT = 600, 
	DISORIENTED_RIGHT = 601, 
	UNINTERESTED = 602, 
	SLEEPY = 603, 
	PLAIN_EVIL = 604, 
	LAUGHING = 605, 
	LONGER_LAUGHING = 606, 
	LONGER_LAUGHING_2 = 607, 
	LAUGHING_2 = 608, 
	EVIL_LAUGH_SHORT = 609, 
	SLIGHTLY_SAD = 610, 
	VERY_SAD = 611, 
	OTHER = 612, 
	NEAR_TEARS_2 = 613, 
	ANGRY_1 = 614, 
	ANGRY_2 = 615, 
	ANGRY_3 = 616, 
	ANGRY_4 = 617;
	public void sendDialogues(int dialogue, int npcId) {
		if (c.dialogue().isActive()) {
			return;
		}
		c.talkingNpc = npcId;
		if (dialogue == 0 && npcId == -1 && c.getCurrentCombination().isPresent()) {
			c.getCurrentCombination().get().sendCombineConfirmation(c);
			return;
		}
		switch (dialogue) {
		case 2002:
			sendOption4("Barrows","Warriors Guild", "","@blu@Close");
			c.teleAction = 201;
			c.nextChat = -1;
			break;
		case 947:
			sendOption2("Supply Shop", "Weapon Shop");
			c.dialogueAction = 947;
			c.nextChat = -1;
			break;
		case 3300:
			sendNpcChat1("What do you want?", c.talkingNpc, "Mazchna");
			c.nextChat = 3301;
		break;
		case 149:
			sendOption2("Claim rewards", "Open shop");
			c.dialogueAction = 149;
			c.nextChat = -1;
			break;
		case 3301:
			sendOption4("I'd like to see the slayer interface.", "I need another assignment, please!",
			"Could you tell me where I can find my current task?", "Nothing, sorry!");
			c.dialogueAction = 3301;
			break;
		case 3325:
			c.getDH().sendOption5("KBD Entrance @red@(DEEP WILD)", "Chaos Elemental @red@(DEEP WILD)", "Godwars", "Barrelchest @red@(Lvl 20+ Wild)",
					"@blu@Next");
			c.teleAction = 3;
			c.dialogueAction = -1;
			break;
		case 3324:
			sendOption5("Slayer Tower", "Edgeville Dungeon", "Taverly Dungeon", "Brimhaven Dungeon", "@blu@Next"); //Rellekka Slayer Dungeon //
			c.teleAction = 2;
			c.dialogueAction = -1;
			break;
		case 3333:
			sendOption3("Rellekka Slayer Dungeon", "Mithril Dragons", "@blu@Previous");
			c.teleAction = 2;
			c.dialogueAction = -1;
			break;
		case 3302:
			sendPlayerChat1("I'd like to see the slayer interface.");
			c.nextChat = 3303;
			break;
			
		case 3303:
			c.getSlayer().handleInterface("buy");
			c.nextChat = 0;
			break;
			
		case 3304:
			sendPlayerChat1("I need another assignment, please!");
			c.nextChat = 3305;
			break;
			
		case 3305:
			c.getSlayer().generateTask();
			break;
			
		case 3306:
			sendNpcChat2("Your new task is to kill "+c.taskAmount+" "+c.getSlayer().getTaskName(c.slayerTask)+".", "Good luck, "+Misc.capitalize(c.playerName)+".", 402, "Mazchna");
			c.nextChat = 0;
			break;
			
		case 3307:
			sendNpcChat3("You currently have "+c.taskAmount+" "+c.getSlayer().getTaskName(c.slayerTask)+" to kill.", "If you would like I could give you an easier task.", "Although if I do this, you won't recieve as many points.", 402, "Mazchna");
			c.nextChat = 3308;
			break;
			
		case 3308:
			sendOption2("Yes, I would like an easier task.", "No, I want to keep hunting on my current task.");
			c.dialogueAction = 3308;
			break;
			
		case 3309:
			sendNpcChat2("Sorry, but your current task is already easy.", "Please come back when you've finished it.", 402, "Mazchna");
			c.nextChat = 0;
			break;
			
		case 3310:
			sendNpcChat1("Your task can be found in the "+c.getSlayer().getLocation(c.slayerTask)+"", 402, "Mazchna");
			c.nextChat = 3311;
			break;
			
		case 3311:
			sendPlayerChat1("Great, thanks you grumpy man!");
			c.nextChat = 0;
			break;
		case 14400:
			sendNpcChat2("Hello there, human!",
					"I can take you to the agility courses.", c.talkingNpc,
					"Gnome trainer");
			c.nextChat = 14401;
			break;
		case 14401:
			sendOption3("Gnome Agility", "Wilderness Agility @red@(DEEP WILD)",
					"Cancel");
			c.dialogueAction = 14400;
			c.nextChat = 0;
			break;
		case 14000:
			sendNpcChat1("What is it, Human?", c.talkingNpc, "Dagganoth Rex Jr");
			c.nextChat = 14001;
			break;
		case 14001:
			sendPlayerChat1("Wow you're fiesty aren't you! I like my pets fiesty");
			c.nextChat = 14002;
			break;
		case 14002:
			sendNpcChat2("And I like my humans crispy.",
					"You'll regret keeping me prisoner.", c.talkingNpc,
					"Dagganoth Rex Jr");
			c.nextChat = 0;
			break;
		case 14003:
			sendPlayerChat1("Hey, what does Prime say to Rex when Rex dies?");
			c.nextChat = 14004;
			break;
		case 14004:
			sendNpcChat1("I don't know, tell me.", c.talkingNpc,
					"Dagganoth Supreme Jr");
			c.nextChat = 14005;
			break;
		case 14005:
			sendPlayerChat1("Rext kid");
			c.nextChat = 0;
			break;
		case 14006:
			sendNpcChat2("ARE YOU NOT ENTERTAINED?",
					"IS THIS NOT WHY YOU ARE HERE?", c.talkingNpc,
					"Dagganoth Prime Jr");
			c.nextChat = 14007;
			break;
		case 14007:
			sendPlayerChat1("Yes, you are quite entertaining! Who's a good Prime!");
			c.nextChat = 14008;
			break;
		case 14008:
			sendNpcChat1(
					"I'll have you know the ladies call me the Gladiator.",
					c.talkingNpc, "Dagganoth Prime Jr");
			c.nextChat = 0;
			break;
		case 14009:
			sendNpcChat2("...and then she mentioned something called a BBC?",
					"Oh, err, hello human.", c.talkingNpc,
					"King Black Dragon Jr");
			c.nextChat = 14010;
			break;
		case 14010:
			sendPlayerChat2("Oh, well okay then.",
					"I'll let you get back to your conversation.");
			c.nextChat = 0;
			break;
		case 14011:
			sendNpcChat1("Hey kid, you diggin' the helmet?", c.talkingNpc,
					"Barrelchest Jr");
			c.nextChat = 14012;
			break;
		case 14012:
			sendPlayerChat3("Yeah! I've never seen one before.",
					"I guess it's one small step for NPC's,",
					"one giant leap for OS PvP!");
			c.nextChat = 14013;
			break;
		case 14013:
			sendNpcChat1("Precisely. Talk to you later, Neil! Err..."
					+ c.playerName + "", c.talkingNpc, "Barrelchest Jr");
			c.nextChat = 0;
			break;
		case 14014:
			sendNpcChat3("I have failed my master on Gielinor.",
					"The Allspark has been...", "Human! what do you want!",
					c.talkingNpc, "General Graardor Jr");
			c.nextChat = 14015;
			break;
		case 14015:
			sendPlayerChat1("I'll do what you say, all right?! Just don't hurt me...");
			c.nextChat = 14016;
			break;
		case 14016:
			sendNpcChat2("PUNY but smart Human!",
					"You know to fear Graardor, leader of the Jogres.",
					c.talkingNpc, "General Graardor Jr");
			c.nextChat = 0;
			break;
		case 14017:
			sendPlayerChat1("Do you miss your people?");
			c.nextChat = 14018;
			break;
		case 14018:
			sendNpcChat1("Mej-TzTok-Jad Kot-Kl! (TzTok-Jad will protect us!)",
					c.talkingNpc, "TzRek-Jad");
			c.nextChat = 14019;
			break;
		case 14019:
			sendPlayerChat1("No.. I don't think so.");
			c.nextChat = 14020;
			break;
		case 14020:
			sendNpcChat1("Jal-Zek Kl? (Foreigner hurt us?)",
					c.talkingNpc, "TzRek-Jad");
			c.nextChat = 14021;
			break;
		case 14021:
			sendPlayerChat1("No, no, I wouldn't hurt you.");
			c.nextChat = 0;
			break;
		case 4000:
			sendOption2(
					"Yes, read this scroll. I understand I cannot change my mind.",
					"No, don't read it.");
			c.dialogueAction = 4000;
			c.nextChat = 0;
			break;
		case 12001:
			sendOption2("Change task for 20 PKP (Chance of same)",
					"I'll do my assigned task.");
			c.dialogueAction = 12001;
			c.nextChat = 0;
			break;
		case 4001:
			sendOption2(
					"Yes, read this scroll. I understand I cannot change my mind.",
					"No, don't read it.");
			c.dialogueAction = 4001;
			c.nextChat = 0;
			break;
		case 4002:
			sendOption2(
					"Yes, read this scroll. I understand I cannot change my mind.",
					"No, don't read it.");
			c.dialogueAction = 4002;
			c.nextChat = 0;
			break;
		case 4003:
			sendOption2(
					"Yes, read this scroll. I understand I cannot change my mind.",
					"No, don't read it.");
			c.dialogueAction = 4003;
			c.nextChat = 0;
			break;
		case 4004:
			sendOption2(
					"Yes, read this scroll. I understand I cannot change my mind.",
					"No, don't read it.");
			c.dialogueAction = 4004;
			c.nextChat = 0;
			break;
		case 2244:
			sendNpcChat1("Do you want change your spellbooks?", c.talkingNpc,
					"High Priest");
			c.nextChat = 2245;
			break;
		case 2299:
			c.getDH().sendOption4("Main", "Zerker", "Pure",
					"I'll set my stats myself.");
			c.dialogueAction = 2299;
			c.nextChat = 0;
			break;
			
		case 2400:
			sendOption4("Imbue Berserker Ring @red@(300 PKP)",
					"Imbue Archer Ring @red@(300 PKP)",
					"Imbue Seers Ring @red@(300 PKP)",
					"Imbue Warrior Ring @red@(300 PKP)");
			c.dialogueAction = 114;
			c.teleAction = -1;
			c.nextChat = 0;
			break;
		case 2401:
			c.getDH().sendNpcChat2(
					"Hello. I can take one of your regular rings",
					"and imbue it for 300 PKP.", c.talkingNpc, "Amik Varze");
			c.nextChat = 0;
			break;
		case 2603:
			c.getDH().sendNpcChat2("Hello. I can restore your HP if you",
					"are a donator or higher!", c.talkingNpc, "A'abla");
			c.nextChat = 0;
			break;
		case 9994:
			sendPlayerChat1("Enter a minimum amount:");
			c.nextChat = 9995;
			break;
		case 9995:
			if (c.getOutStream() != null) {
				c.getOutStream().createFrame(27);
				c.flushOutStream();
			}
			c.settingMin = true;
			break;
		case 9998:
			c.settingMin = false;
			c.settingMax = true;
			sendPlayerChat1("Minimum bet amount set to: " + c.diceMin);
			c.nextChat = 9996;
			break;
		case 9996:
			sendPlayerChat1("Enter a maximum amount:");
			c.nextChat = 9997;
			break;
		case 9997:
			if (c.getOutStream() != null) {
				c.getOutStream().createFrame(27);
				c.flushOutStream();
			}
			c.nextChat = 9999;
			break;
		case 9999:
			c.settingMax = true;
			c.settingMin = false;
			sendPlayerChat1("Maximum bet amount set to: " + c.diceMax);
			c.nextChat = -1;
			break;
		case 11000:
			Player o = PlayerHandler.players[c.otherDiceId];
			sendPlayerChat1("Enter a number to bet between " + o.diceMin
					+ " and " + o.diceMax);
			c.nextChat = 11001;
			break;
		case 11001:
			if (c.getOutStream() != null) {
				c.getOutStream().createFrame(27);
				c.flushOutStream();
			}
			c.nextChat = -1;
			break;
		case 11002:
			o = PlayerHandler.players[c.otherDiceId];
			sendPlayerChat1("Bet amount with " + o.playerName + ": "
					+ o.betAmount);
			o.sendMessage("Rolling dice...");
			Dicing.rollDice(c);
			// c.nextChat = -1;
			break;
		case 11003:
			o = PlayerHandler.players[c.otherDiceId];
			sendPlayerChat1("Enter the amount you want to bet with "
					+ o.playerName);
			c.settingMax = false;
			c.settingMin = false;
			c.settingBet = true;
			c.nextChat = 11001;
			break;

		case 1023:
			sendOption2("Yes", "No");
			c.dialogueAction = 110;
			break;
		case 2245:
			c.getDH().sendOption3(
					"Teleport me to Lunar Island, for lunars spellbook!",
					"Teleport me to Desert Pyramid, for ancients spellbook!",
					"No thanks, i will stay here.");
			c.dialogueAction = 2245;
			c.nextChat = 0;
			break;
		case 69:
			c.getDH().sendNpcChat1(
					"Hello! Do you want to choose your clothes?", c.talkingNpc,
					"Thessalia");
			c.sendMessage("@red@You must right-click Thessalia to change your clothes.");
			c.nextChat = 0;
			break;
		case 6969:
			c.getDH().sendNpcChat2("I'm not working right now sir.",
					"If you wan't me to work, talk to Ardi give me a job.",
					c.talkingNpc, "Unemployed");
			c.sendMessage("This NPC do not have an action, if you have any suggestion for this NPC, post on forums.");
			c.nextChat = 0;
			break;
		/* LOGIN 1st TIME */
		case 769:
			c.getDH().sendNpcChat2("Welcome to Nothing!",
					"You must select your starter package.", c.talkingNpc,
					"Guide");
			c.nextChat = 770;
			break;
		case 770:
			sendStatement("Remember we're on beta, we will have a reset before official release.");
			c.nextChat = 771;
			break;
		case 771:
			c.getDH().sendOption3("Master (levels & items)",
					"Zerker (levels & items)", "Pure (levels & items)");
			c.dialogueAction = 771;
			break;
		/* END LOGIN */
		case 691:
			c.getDH().sendNpcChat2("Welcome to 2007remake.",
					"Please, read what i've to tell you...", c.talkingNpc,
					"Mod Ardi");
			c.nextChat = 692;
			// c.loggedIn = 1;
			break;
		case 692:
			sendNpcChat4("2007remake's on pre-alpha state.",
					"Then you can spawn items, and set your levels.",
					"But remember, it's just for pre-alpha sir...",
					"When we do the official release...", c.talkingNpc,
					"Mod Ardi");
			c.nextChat = 693;
			break;
		case 693:
			sendNpcChat4("We will have economy reset and,",
					"this commands will be removed too...",
					"Please, report glitches, and post suggestions",
					"on forums, for i can code, and we get 100% ready!",
					c.talkingNpc, "Mod Ardi");
			c.sendMessage("@red@You're online in 2007remake pre-alpha.");
			c.sendMessage("@red@Pre-alpha's to find glitches, and post suggestions in forums...");
			c.sendMessage("@red@Then our developer 'Mod Ardi' can code it, and we get official release in less time.");
			c.sendMessage("@red@Thanks for your attention sir.");
			c.nextChat = 0;
			break;
		/* AL KHARID */
		case 1022:
			c.getDH().sendPlayerChat1("Can I come through this gate?");
			c.nextChat = 1023;
			break;

		/*
		 * case 1023: c.getDH().sendNpcChat1(
		 * "You must pay a toll of 10 gold coins to pass.", c.talkingNpc,
		 * "Border Guard"); c.nextChat = 1024; break;
		 */
		case 1024:
			c.getDH().sendOption3("Okay, I'll pay.",
					"Who does my money go to?", "No thanks, I'll walk around.");
			c.dialogueAction = 502;
			break;
		case 1025:
			c.getDH().sendPlayerChat1("Okay, I'll pay.");
			c.nextChat = 1026;
			break;
		case 1026:
			c.getDH().sendPlayerChat1("Who does my money go to?");
			c.nextChat = 1027;
			break;
		case 1027:
			c.getDH().sendNpcChat2("The money goes to the city of Al-Kharid.",
					"Will you pay the toll?", c.talkingNpc, "Border Guard");
			c.nextChat = 1028;
			break;
		case 1028:
			c.getDH().sendOption2("Okay, I'll pay.",
					"No thanks, I'll walk around.");
			c.dialogueAction = 508;
			break;
		case 1029:
			c.getDH().sendPlayerChat1("No thanks, I'll walk around.");
			c.nextChat = 0;
			break;

		case 1030:
			if (!c.getItems().playerHasItem(995, 10)) {
				c.getDH().sendPlayerChat1("I haven't got that much.");
				c.nextChat = 0;
			} else {
				c.getDH().sendNpcChat1(
						"As you wish. Don't get too close to the scorpions.",
						c.talkingNpc, "Border Guard");
				c.getItems().deleteItem2(995, 10);
				c.sendMessage("You pass the gate.");
				Special.movePlayer(c);
				Special.openKharid(c, c.objectId);
				c.turnPlayerTo(c.objectX, c.objectY);
				c.nextChat = 0;
			}
			break;

		case 1031:
			c.getDH().sendNpcChat1(
					"As you wish. Don't get too close to the scopions.",
					c.talkingNpc, "Border Guard");
			c.getItems().deleteItem2(995, 10);
			c.sendMessage("You pass the gate.");
			Special.movePlayer(c);
			Special.openKharid(c, c.objectId);
			c.turnPlayerTo(c.objectX, c.objectY);
			c.nextChat = 0;
			break;

		case 22:
			sendOption2("Pick the flowers", "Leave the flowers");
			c.nextChat = 0;
			c.dialogueAction = 22;
			break;
		/** Bank Settings **/
		case 1013:
			c.getDH().sendNpcChat1("Good day. How may I help you?",
					c.talkingNpc, "Banker");
			c.nextChat = 1014;
			break;
		case 1014:// bank open done, this place done, settings done, to do
				  // delete pin
			c.getDH().sendOption3(
					"I'd like to access my bank account, please.",
					"I'd like to check my my P I N settings.",
					"What is this place?");
			c.dialogueAction = 251;
			break;
		/** What is this place? **/
		case 1015:
			c.getDH().sendPlayerChat1("What is this place?");
			c.nextChat = 1016;
			break;
		case 1016:
			c.getDH().sendNpcChat2("This is the bank of 2007remake.",
					"We have many branches in many towns.", c.talkingNpc,
					"Banker");
			c.nextChat = 0;
			break;
		/**
		 * Note on P I N. In order to check your "Pin Settings. You must have
		 * enter your Bank Pin first
		 **/
		/** I don't know option for Bank Pin **/
		case 1017:
			c.getDH()
					.sendStartInfo(
							"Since you don't know your P I N, it will be deleted in @red@3 days@bla@. If you",
							"wish to cancel this change, you may do so by entering your P I N",
							"correctly next time you attempt to use your bank.",
							"", "", false);
			c.nextChat = 0;
			break;
		case 0:
			c.talkingNpc = -1;
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 1:
			sendStatement("You found a hidden tunnel! Do you want to enter it?");
			c.dialogueAction = 1;
			c.nextChat = 2;
			break;
		case 2:
			sendOption2("Yea! I'm fearless!", "No way! That looks scary!");
			c.dialogueAction = 1;
			c.nextChat = 0;
			break;
		case 3:
			sendNpcChat4(
					"Hello!",
					"My name is Duradel and I am a master of the slayer skill.",
					"I can assign you a slayer task suitable to your combat level.",
					"Would you like a slayer task?", c.talkingNpc, "Duradel");
			c.nextChat = 4;
			break;
		case 5:
			sendNpcChat4("Hello adventurer...",
					"My name is Kolodion, the master of this mage bank.",
					"Would you like to play a minigame in order ",
					"to earn points towards recieving magic related prizes?",
					c.talkingNpc, "Kolodion");
			c.nextChat = 6;
			break;
		case 6:
			sendNpcChat4("The way the game works is as follows...",
					"You will be teleported to the wilderness,",
					"You must kill mages to recieve points,",
					"redeem points with the chamber guardian.", c.talkingNpc,
					"Kolodion");
			c.nextChat = 15;
			break;
		case 11:
			sendNpcChat4(
					"Hello!",
					"My name is Duradel and I am a master of the slayer skill.",
					"I can assign you a slayer task suitable to your combat level.",
					"Would you like a slayer task?", c.talkingNpc, "Duradel");
			c.nextChat = 12;
			break;
		case 12:
			sendOption2("Yes I would like a slayer task.",
					"No I would not like a slayer task.");
			c.dialogueAction = 5;
			c.nextChat = 0;
			break;
		case 13:
			sendNpcChat4(
					"Hello!",
					"My name is Duradel and I am a master of the slayer skill.",
					"I see I have already assigned you a task to complete.",
					"Would you like me to give you an easier task?",
					c.talkingNpc, "Duradel");
			c.nextChat = 14;
			break;
		case 14:
			sendOption2("Yes I would like an easier task.",
					"No I would like to keep my task.");
			c.dialogueAction = 6;
			c.nextChat = 0;
			break;
		case 15:
			sendOption2("Yes I would like to play",
					"No, sounds too dangerous for me.");
			c.dialogueAction = 7;
			break;
		case 16:
			sendOption2("I would like to reset my barrows brothers.",
					"I would like to fix all my barrows");
			c.dialogueAction = 8;
			break;
		case 17:
			sendOption5("Air", "Mind", "Water", "Earth", "More");
			c.dialogueAction = 10;
			c.dialogueId = 17;
			c.teleAction = -1;
			break;
		case 18:
			sendOption5("Fire", "Body", "Cosmic", "Astral", "More");
			c.dialogueAction = 11;
			c.dialogueId = 18;
			c.teleAction = -1;
			break;
		case 19:
			sendOption5("Nature", "Law", "Death", "Blood", "More");
			c.dialogueAction = 12;
			c.dialogueId = 19;
			c.teleAction = -1;
			break;
		case 20:
			sendNpcChat4(
					"Haha, hello",
					"My name is Wizard Distentor! I am the master of clue scroll reading.",
					"I can read the magic signs of a clue scroll",
					"You got to pay me 100K for reading the clue though!",
					c.talkingNpc, "Wizard Distentor");
			c.nextChat = 21;
			break;

		case 23000:
			c.getDH()
					.sendStartInfo(
							"As you collect your reward, you notice an aweful smell.",
							"You look below the remaining debris to the bottom of the",
							"chest. You see a trapdoor. You open it and it leads to a ladder",
							"that goes down a long ways.", "Continue?");
			break;
		case 24000:
			c.getDH().sendStatement("Would you like to continue?");
			c.nextChat = 2500;
			break;
		case 2500:
			c.dialogueAction = 25;
			c.getDH().sendOption2("Yes, I'm not afraid of anything!",
					"No way, the smell itself turns me away.");
			break;
		case 2600:
			c.getDH().sendStatement(
					"This is a very dangerous minigame, are you sure?");
			c.nextChat = 2700;
			break;
		case 2700:
			c.dialogueAction = 27;
			sendOption2("Yes, I'm a brave warrior!",
					"Maybe I shouldn't, I could lose my items!");
			break;
		case 2800:
			c.getDH()
					.sendStatement(
							"Congratulations, "
									+ c.playerName
									+ ". You've completed the barrows challenge & your reward has been delivered.");
			c.nextChat = 0;
			break;
		case 2900:
			sendStatement("Are you ready to visit the chest room?");
			c.nextChat = 3000;
			c.dialogueAction = 29;
			break;
		case 3000:
			sendOption2("Yes, I've killed all the other brothers!",
					"No, I still need to kill more brothers");
			c.nextChat = 0;
			break;
		case 21:
			sendOption2("Yes I would like to pay 100K", "I don't think so sir");
			c.dialogueAction = 50;
			break;
		case 206:
			sendOption2("Stop viewing", "");
			c.dialogueAction = 206;
			break;
		case 23:
			sendNpcChat4("Greetings, Adventure",
					"I'm the legendary Vesta seller",
					"With 120 noted Lime Stones, and 20 Million GP",
					"I'll be selling you the Vesta's Spear", c.talkingNpc,
					"Legends Guard");
			c.nextChat = 24;
			break;
		case 54:
			sendOption2("Buy Vesta's Spear", "I can't afford that");
			c.dialogueAction = 51;
			break;
		case 56:
			sendStatement("Hello " + c.playerName + ", you currently have "
					+ c.pkp + " PK points.");
			break;

		case 57:
			c.getPA().sendFrame126("Teleport to shops?", 2460);
			c.getPA().sendFrame126("Yes.", 2461);
			c.getPA().sendFrame126("No.", 2462);
			c.getPA().sendFrame164(2459);
			c.dialogueAction = 27;
			break;

		/**
		 * Recipe for disaster - Sir Amik Varze
		 **/

		case 25:
			sendOption2("Yes", "No");
			c.rfdOption = true;
			c.nextChat = 0;
			break;
		case 26:
			sendPlayerChat1("Yes");
			c.nextChat = 28;
			break;
		case 27:
			sendPlayerChat1("No");
			c.nextChat = 29;
			break;

		case 29:
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 30:
			sendNpcChat4("Congratulations!",
					"You have defeated all Recipe for Disaster bosses",
					"and have now gained access to the Culinaromancer's chest",
					"and the Culinaromancer's item store.", c.talkingNpc,
					"Sir Amik Varze");
			c.nextChat = 0;
			PlayerSave.saveGame(c);
			break;
		case 31:
			sendNpcChat4("", "You have been defeated!", "You made it to round "
					+ c.roundNpc, "", c.talkingNpc, "Sir Amik Varze");
			c.roundNpc = 0;
			c.nextChat = 0;
			break;

		/**
		 * Horror from the deep
		 **/
		case 32:
			sendNpcChat4("", "Would you like to start the quest",
					"Horror from the Deep?", "", c.talkingNpc, "Jossik");
			c.nextChat = 33;
			break;
		case 33:
			sendNpcChat4("", "You will have to be able to defeat a level-100 ",
					"Dagannoth mother with different styles of attacks.", "",
					c.talkingNpc, "Jossik");
			c.nextChat = 34;
			break;
		case 34:
			sendOption2("Yes I am willing to fight!",
					"No thanks, I am not strong enough.");
			c.horrorOption = true;
			break;
		case 35:
			sendPlayerChat1("Yes I am willing to fight!");
			c.nextChat = 37;
			break;
		case 36:
			sendPlayerChat1("No thanks, I am not strong enough.");
			c.nextChat = 0;
			break;
		case 37:
			c.horrorFromDeep = 1;
			c.height = (c.playerId * 4);
			c.getPA().movePlayer(2515, 10008, c.height);
			Server.npcHandler.spawnNpc(c, 1351, 2521, 10024, c.height, 0, 100,
					16, 75, 75, true, true);
			c.getPA().removeAllWindows();
			c.getPA().loadQuests();
			c.inHfd = true;
			break;

		/**
		 * Desert Treasure dialogue
		 */
		case 41:
			sendNpcChat4("", "Do you want to start the quest",
					"Desert treasure?", "", c.talkingNpc, "Archaeologist");
			c.nextChat = 42;
			break;
		case 42:
			sendNpcChat4("", "You will have to fight four high level bosses,",
					"after each boss you will be brought back",
					"here to refill your supplies if it is needed.",
					c.talkingNpc, "Archaeologist");
			c.nextChat = 43;
			break;
		case 43:
			sendOption2("Yes I want to fight!", "No thanks, I am not ready.");
			c.dtOption = true;
			break;
		case 44:
			sendPlayerChat1("Yes I want to fight!");
			c.nextChat = 51;
			break;
		case 45:
			sendPlayerChat1("No thanks, I am not ready.");
			c.nextChat = 0;
			break;

		case 48:
			sendOption2("Yes, I am ready!", "No, I am not ready.");
			c.dtOption2 = true;
			break;
		case 49:
			sendPlayerChat1("Yes, I am ready!");
			c.nextChat = 52;
			break;
		case 50:
			sendPlayerChat1("No, I am not ready.");
			c.nextChat = 0;
			break;
		case 51:
			c.desertT++;
			c.height = (c.playerId * 4);
			c.getPA().movePlayer(3310, 9376, c.height);
			Server.npcHandler.spawnNpc(c, 1977, 3318, 9376, c.height, 0, 130,
					40, 70, 90, true, true);
			c.getPA().removeAllWindows();
			c.getPA().loadQuests();
			c.inDt = true;
			break;

		/**
		 * Cook's Assistant
		 */
		case 100:
			sendOption2("Yes, offer the item. ", "No, don't offer the item.");
			c.dialogueAction = 100;
			c.nextChat = 0;
			break;
	/*	case 100:
			sendNpcChat1("What am I to do?", c.talkingNpc, "Cook");
			c.nextChat = 101;
			break;
		case 101:
			sendOption4("What`s wrong?", "Can you make me a cake?",
					"You don`t look very happy.", "Nice hat!");
			c.caOption4a = true;
			c.caPlayerTalk1 = true;
			break;
		case 102:
			sendPlayerChat1("What`s wrong?");
			c.nextChat = 103;
			break;
		case 103:
			sendNpcChat3(
					"Oh dear, oh dear, oh dear, Im in a terrible terrible",
					"mess! It`s the Duke`s birthday today, and I should be",
					"making him a lovely big birthday cake.", c.talkingNpc,
					"Cook");
			c.nextChat = 104;
			break;*/
		case 104:
			sendNpcChat4(
					"I`ve forgotten to buy the ingredients. I`ll never get",
					"them in time now. He`ll sack me! What will I do? I have",
					"four children and a goat to look after. Would you help",
					"me? Please?", c.talkingNpc, "Cook");
			c.nextChat = 105;
			break;
		case 105:
			sendOption2("Im always happy to help an cook in distress.",
					"I can`t right now, Maybe later.");
			c.caOption2 = true;
			break;
		case 106:
			c.cooksA++;
			c.getPA().loadQuests();
			sendPlayerChat1("Yes, I`ll help you.");
			c.nextChat = 107;
			break;
		case 107:
			sendNpcChat2("Oh thank you, thank you. I need milk, an egg and",
					"flour. I`d be very grateful if you can get them for me.",
					c.talkingNpc, "Cook");
			c.nextChat = 108;
			break;
		case 108:
			sendOption4("Where do I find some flour.", "How about some milk?",
					"And eggs? where are they found?",
					"Actually, I know where to find these stuff.");
			c.caOption4c = true;
			c.caOption4b = true;
			break;
		case 109:
			sendNpcChat1(
					"How are you getting on with finding the ingredients?",
					c.talkingNpc, "Cook");
			c.nextChat = 110;
			break;
		case 110:
			sendPlayerChat1("Here's a bucket of milk.");
			c.getItems().deleteItem(1927, 1);
			c.nextChat = 111;
			break;
		case 111:
			sendPlayerChat1("Here's a pot of flour.");
			c.getItems().deleteItem(1933, 1);
			c.nextChat = 112;
			break;
		case 112:
			c.cooksA++;
			c.getPA().loadQuests();
			sendPlayerChat1("Here's a fresh egg.");
			c.getItems().deleteItem(1944, 1);
			c.nextChat = 113;
			break;
		case 113:
			sendNpcChat2("You've brough me everything I need! I am saved!",
					"Thank you!", c.talkingNpc, "Cook");
			c.nextChat = 0;
			break;
		/*
		 * case 114: sendPlayerChat1("So do I get to go the Duke's Party?");
		 * c.nextChat = 115; break; case 115:
		 * sendNpcChat2("I'm afraid not, only the big cheeses get to dine with the"
		 * , "Duke.", c.talkingNpc, "Cook"); c.nextChat = 116; break; case 116:
		 * sendPlayerChat2
		 * ("Well, maybe one day I'll be important enough to sit on",
		 * "the Duke's table."); c.nextChat = 117; break; case 117:
		 * sendNpcChat1("Maybe, but I won't be holding my breath.",
		 * c.talkingNpc, "Cook"); c.cooksA++; c.cooksA++;
		 * c.getPA().loadQuests(); c.getAA2().COOK2(); c.nextChat = 0; break;
		 */

		// ** Getting Items - Cook's Assistant **//
		case 118:
			sendNpcChat3("There`s a mill fairly close, Go North then West.",
					"Mill Lane Mill is just off the road to Draynor. I",
					"usually get my flour from there.", c.talkingNpc, "Cook");
			c.nextChat = 119;
			break;
		case 119:
			sendNpcChat2(
					"Talk to Millie, she`ll help, she`s a lovely girl and a fine",
					"Miller.", c.talkingNpc, "Cook");
			c.nextChat = 108;
			break;
		case 12000:
			sendOption2("View account info", "Open NPC kill tracker");
			c.dialogueAction = 12000;
			c.nextChat = 0;
			break;
		case 12200:
			sendOption3("Melee PK Point Shop", "Ranged PK Point Shop", "Magic PK Point Shop");
			c.dialogueAction = 12200;
			c.nextChat = 0;
			break;
		case 120:
			sendNpcChat2(
					"There is a cattle field on the other side of the river,",
					"just across the road from the Groats` Farm.",
					c.talkingNpc, "Cook");
			c.nextChat = 121;
			break;
		case 121:
			sendNpcChat3(
					"Talk to Gillie Groats, she looks after the Dairy Cows -",
					"She`ll tell you everything you need to know about",
					"milking cows!", c.talkingNpc, "Cook");
			c.nextChat = 108;
			break;
		case 122:
			sendNpcChat2("I normally get my eggs from the Groats` farm on the",
					"other side of the river.", c.talkingNpc, "Cook");
			c.nextChat = 123;
			break;
		case 123:
			sendNpcChat1("But any chicken should lay eggs.", c.talkingNpc,
					"Cook");
			c.nextChat = 108;
			break;
		case 124:
			sendPlayerChat1("Actually, I know where to find these stuff");
			c.nextChat = 0;
			break;
		case 125:
			sendPlayerChat1("You're a cook why, don't you bake me a cake?");
			c.nextChat = 126;
			break;
		case 126:
			sendNpcChat1("*sniff* Dont talk to me about cakes...",
					c.talkingNpc, "Cook");
			c.nextChat = 102;
			break;
		case 127:
			sendPlayerChat1("You don't look very happy.");
			c.nextChat = 128;
			break;
		case 128:
			sendNpcChat2(
					"No, I`m not. The world is caving in around me - I am",
					"overcome by dark feelings of impending doom.",
					c.talkingNpc, "Cook");
			c.nextChat = 129;
			break;
		case 129:
			sendOption2("What's wrong?",
					"I'd take off the rest of the day if I were you.");
			c.caOption2a = true;
			break;
		case 130:
			sendPlayerChat1("Nice hat!");
			c.nextChat = 131;
			break;
		case 131:
			sendNpcChat1(
					"Err thank you. It`s a pretty ordinary cook`s hat really.",
					c.talkingNpc, "Cook");
			c.nextChat = 132;
			break;
		case 132:
			sendPlayerChat1("Still, suits you. The trousers are pretty special too.");
			c.nextChat = 133;
			break;
		case 133:
			sendNpcChat1("It`s all standard cook`s issue uniform...",
					c.talkingNpc, "Cook");
			c.nextChat = 134;
			break;
		case 134:
			sendPlayerChat2(
					"The whole hat, apron, stripey trousers ensemble -",
					"it works. It makes you look like a real cook.");
			c.nextChat = 135;
			break;
		case 135:
			sendNpcChat2(
					"I am a real cook!, I haven`t got time to be chatting",
					"about Culinary Fashion. I`m in desperate need of help.",
					c.talkingNpc, "Cook");
			c.nextChat = 102;
			break;
		case 136:
			sendPlayerChat1("I'd take off the rest of the day if I were you.");
			c.nextChat = 137;
			break;
		case 137:
			sendNpcChat2(
					"No, that`s the worst thing I could do. I`d get in terrible",
					"trouble.", c.talkingNpc, "Cook");
			c.nextChat = 138;
			break;
		case 138:
			sendPlayerChat1("Well maybe you need to take a holiday...");
			c.nextChat = 139;
			break;
		case 139:
			sendNpcChat2(
					"That would be nice but the duke doesn`t allow holidays",
					"for core staff.", c.talkingNpc, "Cook");
			c.nextChat = 140;
			break;
		case 140:
			sendPlayerChat2("Hmm, why not run away to the sea and start a new",
					"life as a Pirate.");
			c.nextChat = 141;
			break;
		case 141:
			sendNpcChat2(
					"My wife gets sea sick, and i have an irrational fear of",
					"eyepatches. I don`t see it working myself.", c.talkingNpc,
					"Cook");
			c.nextChat = 142;
			break;
		case 142:
			sendPlayerChat1("I`m afraid I've run out of ideas.");
			c.nextChat = 143;
			break;
		case 143:
			sendNpcChat1("I know I`m doomed.", c.talkingNpc, "Cook");
			c.nextChat = 102;
			break;

		//

		case 144:
			sendNpcChat1("Nice day, isn't it?", c.talkingNpc, "");
			c.nextChat = 0;
			break;

		/*
		 * Doric's Quest
		 */

		case 300:
			sendNpcChat1("Why hello there adventurer, how can I help you?",
					c.talkingNpc, "Doric");
			c.nextChat = 301;
			break;

		case 301:
			sendOption3("I'm looking for a quest.", "Nice place you got here.",
					"Just passing by.");
			c.doricOption = true;
			break;

		case 299:
			sendPlayerChat1("I'm just passing by.");
			c.nextChat = 302;
			break;

		case 302:
			sendNpcChat1("Very well, so long.", c.talkingNpc, "Doric");
			c.nextChat = 0;
			break;

		case 303:
			sendPlayerChat1("Nice place you got here.");
			c.nextChat = 304;
			break;

		case 304:
			sendNpcChat1("Why thank you kind sir.", c.talkingNpc, "Doric");
			c.nextChat = 305;
			break;

		case 305:
			sendPlayerChat1("My pleasure.");
			c.nextChat = 0;
			break;

		case 306:
			sendPlayerChat1("I'm looking for a quest.");
			c.nextChat = 307;
			break;

		case 307:
			sendNpcChat2("A quest you say? Hmm...",
					"Can you run me a quick errand?", c.talkingNpc, "Doric");
			c.nextChat = 308;
			break;

		case 308:
			sendOption2("Of course.", "I need to go.");
			c.doricOption2 = true;
			break;

		case 309:
			sendPlayerChat1("I need to go.");
			c.nextChat = 0;
			break;

		case 310:
			sendPlayerChat1("Of course!");
			c.nextChat = 311;
			break;

		case 311:
			sendNpcChat3("Very good! I need some materials for a new ",
					"pickaxe I'm working on, is there any way you ",
					"could go get these?", c.talkingNpc, "Doric");
			c.nextChat = 312;
			break;

		case 312:
			sendPlayerChat1("Sure, what materials?");
			c.nextChat = 313;
			break;

		case 313:
			sendNpcChat3("6 lumps of clay,", "4 copper ores,",
					"and 2 iron ores.", c.talkingNpc, "Doric");
			c.nextChat = 314;
			break;

		case 314:
			sendPlayerChat1("Sounds good, I will be back soon!");
			c.nextChat = 315;
			c.doricQuest = 5;
			break;

		case 315:
			sendNpcChat1("Thank you adventurer, hurry back!", c.talkingNpc,
					"Doric");
			c.nextChat = 0;
			break;

		case 316:
			sendNpcChat1("Have you got all the materials yet?", c.talkingNpc,
					"Doric");
			c.nextChat = 317;
			break;

		case 317:
			sendPlayerChat1("Not all of them.");
			c.nextChat = 0;
			break;

		case 318:
			sendNpcChat1("Have you got all the materials yet?", c.talkingNpc,
					"Doric");
			c.nextChat = 319;
			break;

		case 319:
			sendPlayerChat1("Yep! Right here.");
			c.nextChat = 320;
			c.getItems().deleteItem(434, 6);
			c.getItems().deleteItem(436, 4);
			c.getItems().deleteItem(440, 2);
			break;

		case 320:
			sendNpcChat2("Thank you so much adventurer, heres a reward",
					"for any hardships you may have encountered.",
					c.talkingNpc, "Doric");
			c.nextChat = 0;
			c.sendMessage("Congradulations, you have completed Doric's Quest!");
			break;

		case 321:
			sendNpcChat1("Welcome to my home, feel free to use my anvils!",
					c.talkingNpc, "Doric");
			c.nextChat = 0;
			break;
		case 500:
			sendOption2("Vote tickets", "PKP tickets");
			c.dialogueAction = 201;
			c.nextChat = 0;
			break;
		case 501:
			sendStatement(
					"Would you like to exchange all of the PK point tickets",
					"in your inventory for PK points?");
			c.nextChat = 502;
			break;

		case 502:
			sendOption2("Yes", "No");
			c.dialogueAction = 200;
			break;
		case 503:
			sendStatement("Would you like to exchange all of the vote tickets",
					"in your inventory for vote points?");
			c.nextChat = 504;
			break;
		case 504:
			sendOption2("Yes", "No");
			c.dialogueAction = 202;
			break;

		case 505:
			switch (c.getHolidayStages().getStage("Halloween")) {
    			case 0:
    				sendNpcChat2("What do you want mortal?", "Do you seek death?", c.talkingNpc, "Grim Reaper");
    				c.nextChat = 506;
    				break;
    				
    			case 1:
    				sendDialogues(514, c.talkingNpc);
    				break;
    				
    			case 2:
    				sendDialogues(524, c.talkingNpc);
    				break;
    				
    			case 3:
    				sendDialogues(526, c.talkingNpc);
    				break;
    				
    			case 4:
    				sendDialogues(528, c.talkingNpc);
    				break;
    				
    			case 5:
    			case 6:
    				sendDialogues(533, c.talkingNpc);
    				break;
			}
			break;
			
		case 506:
			sendPlayerChat1("I...", 596);
			c.nextChat = 507;
			break;
			
		case 507:
			sendNpcChat1("Speak up human before I lose my patience.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 508;
			break;
			
		case 508:
			sendPlayerChat2("Okay okay, what are you doing here in Edgeville?", "You weren't here a few days ago.");
			c.nextChat = 509;
			break;
			
		case 509:
			sendNpcChat1("No...I wasn't. I'm seeking a few lost posessions.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 510;
			break;
			
		case 510:
			sendPlayerChat1("What did you lose?");
			c.nextChat = 511;
			break;
			
		case 511:
			sendNpcChat3("I lost a large amount of @or2@pumpkins@bla@.", "I was travelling through the wilderness and they were stolen.", "It's probably that gang of ghosts again.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 512;
			break;
			
		case 512:
			sendNpcChat2("If you help me find those pumpkins, I'll let you live.", "Do we have a deal?", c.talkingNpc, "Grim Reaper");
			c.nextChat = 513;
			break;
			
		case 513:
			sendOption3("Yes", "Do I really have a choice?", "*whimpering* N...o");
			break;
			
		case 514:
			sendNpcChat4("Every Halloween I travel through the wilderness", 
					"carrying a very important bag of pumpkins.",
					"I need to take these pumpkins to the other world",
					"by 12:00, otherwise there will be no halloween.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 518;
			break;
			
		case 515:
			sendNpcChat2("Haha, you're clever human.", "You're lucky you know your place.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 514;
			break;
			
		case 516:
			sendNpcChat1("Do I smell fear on you human?.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 517;
			break;
			
		case 517:
			sendPlayerChat1("Please don't hurt me.", 613);
			c.nextChat = -1;
			break;
			
		case 518:
			sendPlayerChat1("I don't understand, why do you need them so badly?");
			c.nextChat = 519;
			break;
			
		case 519:
			sendNpcChat3("You fool, these are no ordinary pumpkins.", 
					"They are the root of my power, without them I cannot",
					"venture to this world. Thus, no more halloween.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 520;
			break;
			
		case 520:
			sendPlayerChat3("No more halloween, that would be no more candy.",
					"That would also mean no more grim reaper.",
					"The world would be better off without you, I can't help.");
			c.nextChat = 521;
			break;
			
		case 521:
			sendNpcChat2("YOU WILL HELP ME HUMAN", "I WILL DESTROY THIS WORLD IF YOU DON'T!", c.talkingNpc, "Grim Reaper");
			c.nextChat = 522;
			break;
			
		case 522:
			sendStatement("You whisper:", "Whoah that power...hes not bluffing.", "I will have to help him.");
			c.nextChat = 523;
			break;
			
		case 523:
			c.getHolidayStages().setStage("Halloween", 2);
			sendPlayerChat2("Alright, alright, thats enough.", "Tell me what I need to do.");
			c.nextChat = 524;
			break;
			
		case 524:
			sendNpcChat3("You will need to recover @red@50@bla@ pumpkins..",
					"You can find these pumpkins in a chest, in the wilderness.",
					"The chest is guarded by four ghosts.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 525;
			break;
			
		case 525:
			c.getHolidayStages().setStage("Halloween", 3);
			sendNpcChat4("Although they are not tough, they move quickly.",
					"They move from location to location every 10 minutes.",
					"You will need to be quick, I have somethign to help.",
					"Here, take this locating crystal to find them.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 526;
			break;
			
		case 526:
			if (c.getItems().playerHasItem(611)
					|| c.getItems().bankContains(611)) {
				sendDialogues(527, c.talkingNpc);
				return;
			}
			if (c.getItems().freeSlots() < 1) {
				sendNpcChat1("You need an open space for this locator.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			if (c.getHolidayStages().getStage("Halloween") == 3
					&& !c.getItems().playerHasItem(611)) {
				c.getItems().addItem(611, 1);
				sendDialogues(527, c.talkingNpc);
				return;
			}
			break;
			
		case 527:
			c.getHolidayStages().setStage("Halloween", 4);
			sendNpcChat3("Activating this crystal will let you know", "how far you are from the chest and in which", "direction it lies in. Move along, be quick."
					, c.talkingNpc, "Grim Reaper");
			c.nextChat = -1;
			break;
			
		case 528:
			if (!c.getItems().playerHasItem(611) && !c.getItems().bankContains(611)) {
				if (c.getItems().freeSlots() < 1) {
					sendNpcChat2("You lost the locator, but need an open space in your", "inventory, come back when you have an open space.", c.talkingNpc, "Grim Reaper");
					c.nextChat = -1;
					return;
				}
				sendNpcChat3("You are fortunate, I found the crystal.", "Do not lose this crystal again, it's important.", "Come talk to me when you have made some progress.", c.talkingNpc, "Grim Reaper");
				c.getItems().addItem(611, 1);
				c.nextChat = -1;
			} else {
				sendNpcChat2("You must bring me back the pumpkins noted.", "Lets see how much progress you have made...", c.talkingNpc, "Grim Reaper");
				c.nextChat = 529;
			}
			break;
			
		case 529:
			int total = c.getItems().getItemAmount(1960);
			if (total < 50) {
				if (total == 0) {
					sendNpcChat3("You have not found any pumpkins.", "You need to look harder and search using the locator.", "Hurry, there isn't much time.", c.talkingNpc, "Grim Reaper");
				} else if (total > 0 && total < 10) {
					sendNpcChat3("You have found "+total+", thats a good start.", "However, you need to search harder.", "Hurry, there isn't much time.", c.talkingNpc, "Grim Reaper");
				} else {
					sendNpcChat2("You're nearly finished, find the rest and", "talk to me as soon as you can.", c.talkingNpc, "Grim Reaper");
				}
				c.nextChat = -1;
			} else {
				sendNpcChat2("Great job you found the 50 pumpkins.", "Let me take them off your hands...", c.talkingNpc, "Grim Reaper");
				c.nextChat = 530;
			}
			break;
			
		case 530:
			sendPlayerChat2("Do you know what I had to go through to get these?", "What do I get in return?!");
			c.nextChat = 531;
			break;
			
		case 531:
			sendNpcChat2("Greedy human, isn't your life enough?", "Well, if you insist peasant...", c.talkingNpc, "Grim Reaper");
			c.nextChat = 532;
			break;
			
		case 532:
			if (c.getHolidayStages().getStage("Halloween") == 4) {
				if (c.getItems().playerHasItem(1960, 50)) {
					c.getHolidayStages().setStage("Halloween", 5);
					c.getItems().deleteItem2(1960, 50);
					Server.getHolidayController().giveReward(c,
							HolidayController.HALLOWEEN);
				} else {
					sendNpcChat2("Do I look like a fool to you human?!", "Try my patience, I dare you...", c.talkingNpc, "Grim Reaper");
					c.nextChat = -1;
				}
			} else {
				c.getPA().removeAllWindows();
			}
			break;
			
		case 533:
			sendNpcChat3("You came through for me human, halloween will", "continue to exist because of you.", "I hope you enjoy the reward I gave you.", c.talkingNpc, "Grim Reaper");
			c.nextChat = 534;
			break;
			
		case 534:
			sendNpcChat1("Is there anything else I can do for you?", c.talkingNpc, "Grim Reaper");
			c.nextChat = 535;
			break;
			
		case 535:
			sendOption3("What else can I do?", "I lost my locator, can I get another?", "Nothing");
			break;
			
		case 536:
			if (c.getHolidayStages().getStage("Halloween") >= 6) {
				sendNpcChat3("There is nothing left to do, you already found",
						"the hood I was keeping from you.", "Enjoy your holiday "+c.playerName+".", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
			} else {
    			sendNpcChat4("Since you helped me retrieve my pumpkins, i'll let",
    					"you in on a secret I've been keeping from you.", 
    					"Search the crate and find the @or2@Grim Reaper Hood@bla@.",
    					"There is only one in the crate.", c.talkingNpc, "Grim Reaper");
    			c.nextChat = -1;
			}
			break;
			
		case 537:
			if (c.getItems().bankContains(611)) {
				sendNpcChat3("Hmm...It seems your bank has the locator in it.", "You may have helped me but I will still punish you.", "I will provide you with one if you don't have one.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			if (c.getItems().playerHasItem(611)) {
				sendNpcChat3("Hmm...It seems your inventory has the locator in it.", "You may have helped me but I will still punish you.", "I will provide you with one if you don't have one.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			if (c.getItems().freeSlots() < 1) {
				sendNpcChat2("I can give you a new locator but you need a free", "inventory slot.", c.talkingNpc, "Grim Reaper");
				c.nextChat = -1;
				return;
			}
			c.getItems().addItem(611, 1);
			sendNpcChat2("I have given you a new locator.", "Click it to get a hint where the chest is.", c.talkingNpc, "Grim Reaper");
			c.nextChat = -1;
			break;
			
		case 538:
			sendNpcChat2("Welcome player, you can retrieve lost items in my shop.", "Unfortunately, these are mostly holiday items.", c.talkingNpc, "Diango");
			c.nextChat = -1;
			break;
			
		case 539:
			sendPlayerChat1("Hey..?");
			c.nextChat = 540;
			break;
			
		case 540:
			sendNpcChat1("Hello, wanderer.", c.talkingNpc, "Emblem Trader");
			for (BountyHunterEmblem e : BountyHunterEmblem.EMBLEMS) {
				if (c.getItems().playerHasItem(e.getItemId())) {
					c.nextChat = 553;
					break;
				}
			}
			if (c.nextChat != 553)
				c.nextChat = 541;
			sendNpcChat1("Hello, wanderer.", c.talkingNpc, "Emblem Trader");
			break;
			
		case 541:
			sendNpcChat2("Don't suppose you've come across any strange....", "emblems along your journey?", c.talkingNpc, "Emblem Trader");
			c.nextChat = 542;
			break;
			
		case 542:
			sendPlayerChat1("Not that I've seen.");
			c.nextChat = 543;
			break;
			
		case 543:
			sendNpcChat2("If you do, please do let me know. I'll reward you", "handsomely.", c.talkingNpc, "Emblem Trader");
			c.nextChat = 544;
			break;
			
		case 544:
			sendOption3("What rewards have you got?", "Can I have a PK skull please.", "That's nice.");
			c.dialogueAction = 100;
			break;
			
		case 545:
			if (!c.isSkulled) {
				sendOption2("@red@Obtain a Skull?", "Give me a PK skull.", "Cancel");
				c.dialogueAction = 101;
			} else if (c.skullTimer > 0 && c.skullTimer < Config.SKULL_TIMER) {
				sendOption2("@red@Extend duration?", "Yes", "No");
				c.dialogueAction = 102;
			} else {
				sendNpcChat1("You are already skulled, and the duration is extended.", c.talkingNpc, "Emblem Trader");
				c.nextChat = -1;
			}
			break;
			
		case 546:
			sendStatement("You are now skulled.");
			c.isSkulled = true;
			c.skullTimer = Config.SKULL_TIMER;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
			c.nextChat = -1;
			break;
			
		case 547:
			sendStatement("Your PK skull will now last for the full 20 minutes.");
			c.isSkulled = true;
			c.skullTimer = Config.EXTENDED_SKULL_TIMER;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
			c.nextChat = -1;
			break;
			
		case 548:
			if (c.getBH().isStatisticsVisible()) {
				sendOption2("Disable Streaks?", "Yes", "No");
			} else {
				sendOption2("Enable Streaks?", "Yes", "No");
			}
			c.dialogueAction = 104;
			break;
			
		case 549:
			if (c.getBH().isStatisticsVisible()) {
				c.getBH().setStatisticsVisible(false);
				sendNpcChat1("The statistics interface has been disabled.", c.talkingNpc, "Emblem Trader");
			} else {
				c.getBH().setStatisticsVisible(true);
				sendNpcChat1("The statistics interface has been enabled.", c.talkingNpc, "Emblem Trader");
			}
			c.getBH().updateTargetUI();
			c.nextChat = -1;
			break;
			
		case 553:
			sendNpcChat2("I see you have something valuable on your person.",
					"Certain.... ancient emblems, you see.", c.talkingNpc, "Emblem Trader");
			c.nextChat = 554;
			break;
			
		case 554:
			sendNpcChat2("I'll happily take those off of your hands for a handsome", "fee.", c.talkingNpc, "Emblem Trader");
			c.nextChat = 555;
			break;
			
		case 555:
			sendStatement("All of your emblems are worth a total of "+Misc.insertCommas(Integer.toString(
					c.getBH().getNetworthForEmblems())), "Bounty points.");
			c.nextChat = 556;
			break;
			
		case 556:
			sendOption2("Sell all Emblems?", "Yes", "No");
			c.dialogueAction = 106;
			break;
			
		case 557:
			sendNpcChat2("Thank you. My master in the north will be very", "pleased.", c.talkingNpc, "Emblem Trader");
			c.nextChat = -1;
			break;
			
			
		case 578:
			if (c.getBH().isSpellAccessible()) {
				sendStatement("This spell looks very familiar, I must already know", "the spell.");
			} else {
				sendOption2("Learn teleport?", "Yes", "No");
				c.dialogueAction = 114;
			}
			c.nextChat = -1;
			break;
			
		case 579:
			if (!c.getItems().playerHasItem(12846)) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBH().isSpellAccessible()) {
				sendDialogues(578, -1);
				return;
			}
			sendStatement("You start to read the scroll...", "You now have access to the @blu@Teleport to Bounty Target spell.",
					"This spell requires @blu@3 law runes@bla@ and @blu@85 magic.");
			c.getBH().setSpellAccessible(true);
			c.getItems().deleteItem2(12846, 1);
			break;
			
		case 580:
			sendNpcChat("Ho Ho Ho, Merry christmas!");
			c.nextChat = 581;
			break;
			
		case 581:
			sendPlayerChat3("Merry christmas to you too santa.", "If you don't mind me asking santa, what are", "you doing here in edgeville?");
			c.nextChat = 582;
			break;
			
		case 582:
			sendNpcChat("Strange, another player asked the same thing.", "The reason I am here in edgeville is is because",
					"I am looking for people with good hearts to help me.", "Do you have a good heart?");
			c.nextChat = 583;
			break;
			
		case 583:
			sendOption2("Yes, I do.", "No, I don't.");
			c.dialogueAction = 115;
			break;
			
		case 584:
			sendNpcChat("Ho Ho Ho, aren't you a little rascal.", "I hope you like smithing because you will",
					"be gettings tons of coal this year.");
			c.nextChat = -1;
			break;
			
		case 585:
			sendNpcChat("Ho Ho Ho, I see your name on the nice list.", "Would you like to help me?");
			c.nextChat = 586;
			break;
			
		case 586:
			sendOption2("Yes", "No");
			c.dialogueAction = 116;
			break;
			
		case 587:
			sendNpcChat("Alright then, come back if you do.", "Happy holidays.");
			c.nextChat = -1;
			break;
			
		case 588:
			sendNpcChat("Thank you, let me explain my dilema...");
			c.nextChat = 589;
			break;
			
		case 589:
			sendNpcChat("Each and every year presents are made by the elves", "and then distributed to all the children on christmas."
					,"Presents are made at @blu@2 @bla@workstations. These workstations", "are in the edgeville wilderness.");
			c.nextChat = 590;
			break;
			
		case 590:
			sendNpcChat("Normally we travel through @blu@the mines@bla@ under the wilderness.",
					"However due to a @blu@cave-in @bla@that experts believe to be",
					"caused by the mass amount of players stomping in edgeville",
					"wilderness, we can no longer travel through them.");
			c.nextChat = 591;
			break;
			
		case 591:
			sendNpcChat("The only choice we have is to go above the mines",
					"and go from station-to-station which is very dangerous.");
			c.nextChat = 592;
			break;
			
		case 592:
			sendNpcChat("I need you to go from station-to-station to further",
					"the development of each toy. Let me explain the process.",
					"Each toy has 2 stages of development. The first is",
					"the development stage, this happens at @blu@Station #1@bla@.");
			c.nextChat = 593;
			break;
			
		case 593:
			sendNpcChat("The second is the finalization stage.",
					"This happens at @blu@Station #2@bla@.");
			c.nextChat= 594;
			break;
			
		case 594:
			sendNpcChat("You must bring each toy to station 1, and station 2.",
					"You cannot take a toy to station 2, then station 1.",
					"If you do, the toy may break and you will have to restart.");
			c.nextChat = 595;
			break;
			
		case 595:
			sendNpcChat("Each station will have a unique engineer.",
					"You must give the toy to the engineer at that station.",
					"Once the engineer at station 2 has finished the",
					"development, speak to me.");
			c.nextChat = 596;
			break;
			
		case 596:
			int stage = c.getHolidayStages().getStage("Christmas");
			if (stage >= 2) {
				sendDialogues(600, 3115);
				return;
			}
			sendNpcChat("Do you understand everything or should I repeat",
					"myself? It's no trouble.");
			c.nextChat = 597;
			break;
			
		case 597:
			sendOption2("Yes, please repeat that.", "No, I understand what I have to do.");
			c.dialogueAction = 117;
			break;
			
		case 598:
			stage = c.getHolidayStages().getStage("Christmas");
			if (stage == 2) {
				sendNpcChat("The first toy is a @blu@"+ItemAssistant.getItemName(ChristmasToy.STAR.getItems()[0]) + "@bla@.");
			} else if (stage > 2) {
				sendNpcChat("The next toy is a @blu@"+ItemAssistant.getItemName(HolidayController.CHRISTMAS.forStage(stage).getItems()[0]) + "@bla@.");
			} else {
				c.nextChat = -1;
				return;
			}
			c.nextChat = 599;
			break;
			
		case 599:
			stage = c.getHolidayStages().getStage("Christmas");
			ChristmasToy toy = HolidayController.CHRISTMAS.forStage(stage);
			if (HolidayController.CHRISTMAS.hasToy(c)) {
				sendDialogues(600, 3115);
				return;
			}
			if (c.getItems().freeSlots() == 0) {
				sendNpcChat("I need to give you the toy to continue.", "You must get one free slot, come back when you do.");
				c.nextChat = -1;
				return;
			}
			c.getItems().addItem(toy.getItems()[0], 1);
			sendNpcChat("You must take this item to each station.", "If you need directions, talk to me again.",
					"I will give you the next toy when you return with", "the fully developed toy.");
			c.nextChat = -1;
			c.dialogueAction = -1;
			break;
			
		case 600:
			sendOption4("I have a fully developed toy.", "I don't know what to do.", "Where are the stations?", "Nevermind");
			c.dialogueAction = 118;
			break;
			
		case 601:
			sendNpcChat("Let me reiterate for you.");
			c.nextChat = 589;
			break;
			
		case 602:
			sendNpcChat("The first station is in @red@38 @bla@wilderness. It is",
					"in a @red@prayer temple @bla@and cannot be teleported to.",
					"The station is in multi combat and is on the western",
					"most side of the wilderness.");
			c.nextChat = 603;
			break;
			
		case 603:
			sendNpcChat("The second station is in 24 wilderness. It is located",
					"directly south of the first station. The area is",
					"single combat but is very open and dangerous.");
			c.nextChat = 600;
			break;
			
		case 604:
			stage = c.getHolidayStages().getStage("Christmas");
			if (stage == HolidayController.CHRISTMAS.getMaximumStage()) {
				sendNpcChat("Thank you for helping us, and santa.", "Woah, did you hear that?", "I thought I heard something nearby.");
				c.nextChat = -1;
				return;
			}
			int toyId = HolidayController.CHRISTMAS.forStage(stage).getItems()[0];
			if (stage < 2) {
				sendNpcChat("I'm not drunk...", "Don't tell the boss, alright?");
				c.nextChat = -1;
				return;
			}
			if (!c.getItems().playerHasItem(toyId)) {
				sendNpcChat("Hello there, you should get a toy", "from santa and come back.",
						"Or talk to the other engineer in station 2 if", "you already have the developed toy.");
				c.nextChat = -1;
				return;
			}
			if (CycleEventHandler.getSingleton().isAlive(c, CycleEventHandler.Event.CHRISTMAS_ENGINEER)) {
				sendNpcChat("This should only take a few more seconds.", "Please wait.");
				c.nextChat = -1;
				return;
			}
			sendNpcChat("I will upgrade this for you, this should only take", "5 seconds or so.");
			c.nextChat = -1;
			CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.CHRISTMAS_ENGINEER, c, 
					new ChristmasToyUpgrade(c, toyId, c.talkingNpc), 10);
			break;
			
		case 605:
			sendPlayerChat1("Err...alright.");
			c.nextChat = -1;
			break;
			
		case 606:
			sendNpcChat("The toy has been further developed and is now", "ready to be completed. Go to the second",
					"station and talk to the other engineer.");
			c.dialogueAction = -1;
			c.nextChat = -1;
			break;
			
		case 607:
			sendNpcChat("The toy has finished being developed.", "Talk to santa in edgeville.");
			c.dialogueAction = -1;
			c.nextChat = -1;
			break;
			
		case 608:
			stage = c.getHolidayStages().getStage("Christmas");
			if (stage == HolidayController.CHRISTMAS.getMaximumStage()) {
				sendNpcChat("Thank you for helping us, and santa.", "Woah, did you hear that?", "I thought I heard something nearby.");
				c.nextChat = -1;
				return;
			}
			toyId = HolidayController.CHRISTMAS.forStage(stage).getItems()[1];
			if (stage == HolidayController.CHRISTMAS.getMaximumStage()) {
				sendNpcChat("Thank you for helping us, and santa.", "Woah, did you hear that?", "I thought I heard something nearby.");
				c.nextChat = -1;
				return;
			}
			if (stage < 2) {
				sendNpcChat("Talk to santa.");
				c.nextChat = -1;
				return;
			}
			if (!c.getItems().playerHasItem(toyId)) {
				sendNpcChat("You do not have the toy I need.", "Come back when you do.");
				c.nextChat = -1;
				return;
			}
			if (CycleEventHandler.getSingleton().isAlive(c, CycleEventHandler.Event.CHRISTMAS_ENGINEER)) {
				sendNpcChat("This should only take a few more seconds.", "Please wait.");
				c.nextChat = -1;
				return;
			}
			sendNpcChat("I will upgrade this for you, this should only take", "5 seconds or so.");
			c.nextChat = -1;
			CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.CHRISTMAS_ENGINEER, c, 
					new ChristmasToyUpgrade(c, toyId, c.talkingNpc), 10);
			break;
			
		case 609:
			stage = c.getHolidayStages().getStage("Christmas");
			toyId = HolidayController.CHRISTMAS.forStage(stage).getItems()[2];
			if (!c.getItems().playerHasItem(toyId)) {
				sendNpcChat("You don't have the toy upgraded yet.", "Talk to me when you do.");
				c.nextChat = -1;
				return;
			}
			c.getItems().deleteItem(toyId, 1);
			Optional<ChristmasToy> nextToy = HolidayController.CHRISTMAS.forStage(stage).getNextToy();
			if (!nextToy.isPresent()) {
				if (stage >= HolidayController.CHRISTMAS.getMaximumStage()) {
					c.getPA().closeAllWindows();
					return;
				}
				c.getHolidayStages().setStage("Christmas", HolidayController.CHRISTMAS.getMaximumStage());
				Server.getHolidayController().giveReward(c, HolidayController.CHRISTMAS);
				sendNpcChat("Thank you for helping me.", "I have given you a @blu@Reindeer Hat@bla@",
						"for your time, it's the least I can do.");
				c.nextChat = -1;
				return;
			}
			stage++;
			c.getHolidayStages().setStage("Christmas", stage);
			sendDialogues(598, 3115);
			break;
			
		case 610:
			sendNpcChat("I cannot thank you enough for the help "+Misc.capitalize(c.playerName)+".",
					"Would you like to fight the anti-santa? You must",
					"wear the reindeer hat to successfully attack him.",
					"He is in deep wilderness, it is dangerous.");
			c.nextChat = 611;
			break;
			
		case 611:
			sendOption2("Yes, I'm ready.", "No.");
			c.dialogueAction = 119;
			break;
			
		case 612:
			sendOption2("Use christmas cracker, I may get a partyhat.", "No, I want to keep my partyhat.");
			c.dialogueAction = 120;
			break;
			
		case 613:
			sendOption5(
					"How much have I contributed this week?", 
					"Who are the top 5 contributors this week?",
					"Who won last week?",
					"When will a winner be selected?",
					"Close."
			);
			c.dialogueAction = 121;
			break;
			
		case 614:
			int amount = 0;
			try {
				amount = Server.getServerData().getWellOfGoodWill().getEntries().get(c.playerName.toLowerCase());
				if (amount <= 0) {
					sendDialogues(615, -1);
					return;
				}
				String message = "The gods are not very pleased with your contributions.";
				if (amount > 100) {
					message = "The gods notice your contributions.";
				}
				if (amount > 250) {
					message = "The gods are pleased with your contributions.";
				}
				if (amount > 500) {
					message = "The gods are astounded by your generousity.";
				}
				sendStatement("You have contributed a total worth of "+amount+" points.", message);
			} catch (NullPointerException npe) {
				sendDialogues(615, -1);
			}
			break;
			
		case 615:
			sendStatement("You have not contributed anything so far this week.", "The gods are not very pleased.");
			c.nextChat = 613;
			break;
			
		case 616:
			Map<String, Integer> entries = Server.getServerData().getWellOfGoodWill().getEntries();
			List<Entry<String, Integer>> list = new ArrayList<>(entries.entrySet());
			list.sort(Entry.comparingByValue());
			Collections.reverse(list);
			List<Entry<String, Integer>> top5 = list.subList(0, list.size() < 5 ? list.size() : 5);
			if (top5.size() == 0) {
				sendStatement("There are currently no contributors.");
				c.nextChat = 613;
				return;
			}
			StringBuilder sb = new StringBuilder();
			top5.forEach(entry -> sb.append(StringUtils.capitalize(entry.getKey()) + " contributed @blu@"+Misc.insertCommas(
					Integer.toString(entry.getValue())) + "-"));
			sendStatement(sb.toString().split("-"));
			c.nextChat = 613;
			break;
			
		case 617:
			if (Server.getServerData().getWellOfGoodWill().getWinners() == null) {
				c.sendMessage("Winners are null!");
				return;
			}
			List<Entry<String, Integer>> winners = new ArrayList<>(Server.getServerData().getWellOfGoodWill().getWinners().entrySet());
			if (winners.size() == 0) {
				sendStatement("There are no winners from last week.");
				c.nextChat = -1;
				return;
			}
			sb = new StringBuilder("The top 3 contributors from last week");
			sb.append("-");
			int spot = 1;
			for (Entry<String, Integer> entry : winners) {
				sb.append((spot++) + ".) " + StringUtils.capitalize(entry.getKey()) + " (@blu@"+Misc.insertCommas(
						Integer.toString(entry.getValue())) + "@bla@)-");
			}
			sendStatement(sb.toString().split("-"));
			c.nextChat = 613;
			break;
			
		case 618:
			sb = new StringBuilder("");
			Date date = Server.getServerData().getWellOfGoodWill().getDate();
			if (date == null) {
				sendStatement("The next date has yet to be determined.");
				c.nextChat = 613;
				return;
			}
			Calendar calendar = DateUtils.toCalendar(date);
			SimpleDateFormat formattedDate = new SimpleDateFormat("MMM dd, HH:mm");
			long difference = date.getTime() - Server.getCalendar().getInstance().getTimeInMillis();
			long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
			long hours = TimeUnit.HOURS.convert(difference - (days * (60000 * 60 * 24)), TimeUnit.MILLISECONDS);
			long minutes = TimeUnit.MINUTES.convert(difference - (days * (60000 * 60 * 24)) - (60000 * 60 * hours), TimeUnit.MILLISECONDS);
			sendStatement("The next winner will be chosen in "+days+" days, "+hours+" hours, "+minutes+" minutes.",
					"This will be on "+formattedDate.format(calendar.getTime())+".");
			c.nextChat = 613;
			break;
			
		case 619:
			sendNpcChat2("Hello player, is there anything I can help", "you with?", c.talkingNpc, "Weird Old Man");
			c.nextChat = 620;
			break;
			
		case 620:
			sendOptions(
					"I want to repair an item.",
					"I want to claim back an item that has degraded.",
					"I don't see the item I want to claim on the list.",
					"Nothing.");
			c.dialogueAction = 122;
			break;
			
		case 621:
			sendNpcChat(
					"You can repair a damaged item by using it on me.",
					"Each item has a repair cost. Most repairs range from",
					"250,000k to 500,000k");
			c.nextChat = 622;
			break;
			
		case 622:
			sendNpcChat(
					"If an item degrades and dissapears, you can claim it.",
					"It costs 2x as much to claim it then it does to repair.");
			c.nextChat = 620;
			break;
			
		case 623:
			DegradableItem[] degradeList = Degrade.getClaimedItems(c);
			if (degradeList.length == 0) {
				sendNpcChat("You don't have any fully degraded items to claim.");
				c.nextChat = 620;
				return;
			}
			String item1 = ItemAssistant.getItemName(degradeList[0].getItemId())
					+ " @red@" + (DegradableItem.forId(degradeList[0].getItemId()).get().getCost() * 2) + " PKP";
			String item2 = "";
			String item3 = "";
			String item4 = "";
			if (degradeList.length > 1) {
				item2 = ItemAssistant.getItemName(degradeList[1].getItemId())
						+ " @red@" + (DegradableItem.forId(degradeList[1].getItemId()).get().getCost() * 2) + " PKP";
			}
			if (degradeList.length > 2) {
				item3 = ItemAssistant.getItemName(degradeList[2].getItemId())
						+ " @red@" + (DegradableItem.forId(degradeList[2].getItemId()).get().getCost() * 2) + " PKP";
			}
			if (degradeList.length > 3) {
				item4 = ItemAssistant.getItemName(degradeList[3].getItemId())
						+ " @red@" + (DegradableItem.forId(degradeList[3].getItemId()).get().getCost() * 2) + " PKP";
			}
			sendOptions(item1, item2, item3, item4, "Close");
			c.dialogueAction = 123;
			break;
			
		case 624:
			sendNpcChat(
					"The list only shows up to 4 claimable items at a time.",
					"You can free up space by claiming items.",
					"It is good practice to never have more than three",
					"claimable items at any given time to avoid this.");
			break;
			
		case 625:
			sendStatement("Would you like to return to Zulrah's shrine?");
			c.nextChat = 626;
			break;
			
		case 626:
			sendOptions("Yes, return.", "No.");
			c.dialogueAction = 124;
			break;
			
		case 627:
			sendOption2("Yes", "No");
			c.dialogueAction = 125;
			break;
			
		case 628:
			sendNpcChat("Hello, my name is Sigmund.", "I am looking for items I can merchant to other players.",
					"If you have any please trade me.");
			c.nextChat = -1;
			break;
			
		case 629:
			sendNpcChat("Hello, would you like to go to the abyss?");
			c.nextChat = 630;
			break;
			
		case 630:
			sendOptions("Yes", "No");
			c.dialogueAction = 126;
			break;
			
		case 631:
			sendStatement("Would you like to enter the resource area for 50,000?");
			c.nextChat = 632;
			break;
			
		case 632:
			sendOptions("Yes", "No");
			c.dialogueAction = 127;
			break;
			
		case 633:
			sendOption5("10 Waves - Fire Cape",
					"20 Waves - Fire Cape",
					"63 Waves - Fire Cape",
					"How does it work?",
					"What are my rankings?");
			c.dialogueAction = 128;
			break;
			
		case 634:
			this.sendStatement("The minigame is fairly straightforward.", "Choose how many waves of npcs you want to fight against.",
					"After you defeat all of the npcs in the current wave", "you will move onto the next. Once you reach the final",
					"wave you will fight Tz-tok jad, a level 702 beast.");
			c.nextChat = 635;
			break;
			
		case 635:
			this.sendStatement("The more waves you defeat in total, the better your reward.",
					"You may logout at anytime within the minigame.", "You are unable to teleport from within the minigame.");
			c.nextChat = 0;
			break;
			
		case 636:
			this.sendStatement("You have completed...", "Level 1 (10 Waves) "+c.waveInfo[0]+" times",
					"Level 2 (20 Waves) "+c.waveInfo[1]+" times", "Level 3 (63 Waves) "+c.waveInfo[2]+" times");
			c.nextChat = 0;
			break;
			
		case 638:
			sendOptions("Take me to Zulrah!", "Check Zulrah time records", "Reclaim lost items");
			c.dialogueAction = 129;
			break;
			
		case 639:
			sendNpcChat("You don't have any items that are lost.");
			c.nextChat = -1;
			break;
			
		case 640:
			if (c.getLostItems().size() > 0) {
				c.getLostItems().retain();
			}
			c.nextChat = -1;
			break;
			
		case 641:
			sendOptions("Yes", "No");
			c.dialogueAction = 130;
			break;
			
		case 642:
			sendNpcChat("Error: You must claim your items back before fighting.");
			c.nextChat = -1;
			break;
			
		case 643:
			sendNpcChat("The current record is set by: @blu@Nobody@bla@.");
			c.nextChat = -1;
			break;
			
		case 644:
			SerializablePair<String, Long> pair = Server.getServerData().getZulrahTime();
			sendNpcChat("The current record is set by: @blu@" + pair.getFirst() + "@bla@.", "With a time of:@blu@ " + Misc.toFormattedMS(pair.getSecond()) + "@bla@.");
			c.nextChat = 638;
			break;
		}
		
	}

	public void sendStatement(String line1, String line2) {
		c.getPA().sendString(line1, 360);
		c.getPA().sendString(line2, 361);
		c.getPA().sendFrame164(359);
	}

	public void sendStatement(String line1, String line2, String line3) {
		c.getPA().sendString(line1, 364);
		c.getPA().sendString(line2, 365);
		c.getPA().sendString(line3, 366);
		c.getPA().sendFrame164(363);
	}

	public void sendStatement(String line1, String line2, String line3,
			String line4) {
		c.getPA().sendString(line1, 369);
		c.getPA().sendString(line2, 370);
		c.getPA().sendString(line3, 371);
		c.getPA().sendString(line4, 372);
		c.getPA().sendFrame164(368);
	}

	public void sendStatement(String line1, String line2, String line3,
			String line4, String line5) {
		c.getPA().sendString(line1, 375);
		c.getPA().sendString(line2, 376);
		c.getPA().sendString(line3, 377);
		c.getPA().sendString(line4, 378);
		c.getPA().sendString(line5, 379);
		c.getPA().sendFrame164(374);
	}

	/*
	 * Information Box
	 */

	public void sendStartInfo(String text, String text1, String text2,
			String text3, String title) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().sendFrame164(6179);
	}

	/*
	 * Options
	 */

	public void sendOption(String s) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126("Click here to continue", 2473);
		c.getPA().sendFrame164(13758);
	}

	public void sendOption2(String s, String s1) {
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendFrame164(2459);
	}
	
	public void sendOption2(String title, String s, String s1) {
		c.getPA().sendFrame126(title, 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendFrame164(2459);
	}

	public void sendOption3(String s, String s1, String s2) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126(s1, 2472);
		c.getPA().sendFrame126(s2, 2473);
		c.getPA().sendFrame164(2469);
	}

	public void sendOption4(String s, String s1, String s2, String s3) {
		c.getPA().sendFrame126("Select an Option", 2481);
		c.getPA().sendFrame126(s, 2482);
		c.getPA().sendFrame126(s1, 2483);
		c.getPA().sendFrame126(s2, 2484);
		c.getPA().sendFrame126(s3, 2485);
		c.getPA().sendFrame164(2480);
	}

	public void sendOption5(String s, String s1, String s2, String s3, String s4) {
		c.getPA().sendFrame126("Select an Option", 2493);
		c.getPA().sendFrame126(s, 2494);
		c.getPA().sendFrame126(s1, 2495);
		c.getPA().sendFrame126(s2, 2496);
		c.getPA().sendFrame126(s3, 2497);
		c.getPA().sendFrame126(s4, 2498);
		c.getPA().sendFrame164(2492);
	}

	public void sendStartInfo(String text, String text1, String text2,
			String text3, String title, boolean send) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().sendFrame164(6179);
	}

	/*
	 * Statements
	 */

	public void sendStatement(String s) { // 1 line click here to continue chat
										  // box interface
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126("Click here to continue", 358);
		c.getPA().sendFrame164(356);
	}

	/*
	 * Npc Chatting
	 */
	public void sendPlayerChat1(String s, int emoteid) {
		c.getPA().sendFrame200(969, emoteid);
		c.getPA().sendFrame126(Misc.capitalize(c.playerName), 970);
		c.getPA().sendFrame126(s, 971);
		c.getPA().sendFrame185(969);
		c.getPA().sendFrame164(968);
	}

	public void sendNpcChat1(String s, int ChatNpc, String name) {
		c.getPA().sendFrame200(4883, 591);
		c.getPA().sendFrame126(name, 4884);
		c.getPA().sendFrame126(s, 4885);
		c.getPA().sendFrame75(ChatNpc, 4883);
		c.getPA().sendFrame164(4882);
	}
	
	public void sendOptions(String... options) {
		if (options.length < 2) {
			return;
		}
		if (Arrays.asList(options).stream().anyMatch(Objects::isNull)) {
			return;
		}
		switch (options.length) {
			case 2:
				sendOption2(options[0], options[1]);
				break;
			case 3:
				sendOption3(options[0], options[1], options[2]);
				break;
			case 4:
				sendOption4(options[0], options[1], options[2], options[3]);
				break;
				
			case 5:
				sendOption5(options[0], options[1], options[2], options[3], options[4]);
				break;
		}
	}
	
	public void sendNpcChat(String... messages) {
		switch (messages.length) {
			case 1:
				sendNpcChat1(messages[0], c.talkingNpc, Server.npcHandler.getNpcName(c.talkingNpc));
				break;
			
			case 2:
				sendNpcChat2(messages[0], messages[1], c.talkingNpc, Server.npcHandler.getNpcName(c.talkingNpc));
				break;
			
			case 3:
				sendNpcChat3(messages[0], messages[1], messages[2], c.talkingNpc,
						Server.npcHandler.getNpcName(c.talkingNpc));
				break;
			
			case 4:
				sendNpcChat4(messages[0], messages[1], messages[2], messages[3], c.talkingNpc,
						Server.npcHandler.getNpcName(c.talkingNpc));
				break;
		}
	}
	
	public void sendStatement(String... messages) {
		switch (messages.length) {
			case 1:
				sendStatement(messages[0]);
				break;
				
			case 2:
				sendStatement(messages[0], messages[1]);
				break;
				
			case 3:
				sendStatement(messages[0], messages[1], messages[2]);
				break;
				
			case 4:
				sendStatement(messages[0], messages[1], messages[2], messages[3]);
				break;
				
			case 5:
				sendStatement(messages[0], messages[1], messages[2], messages[3], messages[4]);
				break;
		}
	}

	public void sendNpcChat2(String s, String s1, int ChatNpc, String name) {
		c.getPA().sendFrame200(4888, 591);
		c.getPA().sendFrame126(name, 4889);
		c.getPA().sendFrame126(s, 4890);
		c.getPA().sendFrame126(s1, 4891);
		c.getPA().sendFrame75(ChatNpc, 4888);
		c.getPA().sendFrame164(4887);
	}

	public void sendNpcChat3(String s, String s1, String s2, int ChatNpc,
			String name) {
		c.getPA().sendFrame200(4894, 591);
		c.getPA().sendFrame126(name, 4895);
		c.getPA().sendFrame126(s, 4896);
		c.getPA().sendFrame126(s1, 4897);
		c.getPA().sendFrame126(s2, 4898);
		c.getPA().sendFrame75(ChatNpc, 4894);
		c.getPA().sendFrame164(4893);
	}

	public void sendNpcChat4(String s, String s1, String s2, String s3,
			int ChatNpc, String name) {
		c.getPA().sendFrame200(4901, 591);
		c.getPA().sendFrame126(name, 4902);
		c.getPA().sendFrame126(s, 4903);
		c.getPA().sendFrame126(s1, 4904);
		c.getPA().sendFrame126(s2, 4905);
		c.getPA().sendFrame126(s3, 4906);
		c.getPA().sendFrame75(ChatNpc, 4901);
		c.getPA().sendFrame164(4900);
	}

	/*
	 * Player Chating Back
	 */

	public void sendPlayerChat1(String s) {
		c.getPA().sendFrame200(969, 591);
		c.getPA().sendFrame126(c.playerName, 970);
		c.getPA().sendFrame126(s, 971);
		c.getPA().sendFrame185(969);
		c.getPA().sendFrame164(968);
	}

	public void sendPlayerChat2(String s, String s1) {
		c.getPA().sendFrame200(974, 591);
		c.getPA().sendFrame126(c.playerName, 975);
		c.getPA().sendFrame126(s, 976);
		c.getPA().sendFrame126(s1, 977);
		c.getPA().sendFrame185(974);
		c.getPA().sendFrame164(973);
	}

	public void sendPlayerChat3(String s, String s1, String s2) {
		c.getPA().sendFrame200(980, 591);
		c.getPA().sendFrame126(c.playerName, 981);
		c.getPA().sendFrame126(s, 982);
		c.getPA().sendFrame126(s1, 983);
		c.getPA().sendFrame126(s2, 984);
		c.getPA().sendFrame185(980);
		c.getPA().sendFrame164(979);
	}

	public void sendPlayerChat4(String s, String s1, String s2, String s3) {
		c.getPA().sendFrame200(987, 591);
		c.getPA().sendFrame126(c.playerName, 988);
		c.getPA().sendFrame126(s, 989);
		c.getPA().sendFrame126(s1, 990);
		c.getPA().sendFrame126(s2, 991);
		c.getPA().sendFrame126(s3, 992);
		c.getPA().sendFrame185(987);
		c.getPA().sendFrame164(986);
	}
}
