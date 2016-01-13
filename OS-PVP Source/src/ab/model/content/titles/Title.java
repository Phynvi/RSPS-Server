package ab.model.content.titles;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

import ab.model.content.achievement.AchievementTier;
import ab.model.content.achievement.Achievements.Achievement;
import ab.model.players.Player;

/**
 * Each element of the enum represents a singular title with an array of qualities. 
 * <p><b>
 * Please note that by default the description is wrapped to fit into the open area
 * so there is no need to use escape characters in the description.
 * </b></p>
 * @author Jason MacKeigan
 * @date Jan 22, 2015, 3:44:52 PM
 */
public enum Title implements Comparator<Title> {
	NONE("None", "", 0, TitleCurrency.NONE, Titles.NO_REQUIREMENT, "No title will be displayed."),
	
	CUSTOM("Custom", "A67711", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isDonator() || player.getRights().isStaff();
		}
	}, "Have the option of choosing your own 16-character title. You must be a donator of any rank to display this title."),
	
	CONTRIBUTOR("Contributor", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isContributor();
		}
	}, "This title is for contributors. You must be a contributor to purchase and display this title."),
	
	SPONSOR("Sponsor", "0000ff", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isSponsor();
		}
	}, "This title is for sponsors. You must be a sponsor to purchase and display this title."),
	
	SUPPORTER("Supporter", "148200", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isSupporter();
		}
	}, "This title is for supporters. You must be a supporter to purchase and display this title."),
	
	V_I_P("V.I.P", "9a2d8a", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isVIP();
		}
	}, "This title is for V.I.P's. You must be a V.I.P to purchase and display this title."),
	
	SUPER_V_I_P("Super V.I.P", "ffff00", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isSuperVIP();
		}
	}, "This title is for Super V.I.P's. You must be a super V.I.P to purchase and display this title."),
	
	RESPECTED_MEMBER("Respected Member", "9a2d8a", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isRespectedMember();
		}
	}, "This title is for respected members. You must be a respected member to purchase and display this title."),
	
	HELPER("Helper", "0000ff", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isHelper();
		}
	}, "This title represents the helper rank. You must be a helper to purchase or display this title."),
	
	MODERATOR("Moderator", "148200", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isModerator();
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has."
			+ "Only moderators on the staff team may display this."),
	
	ADMINISTRATOR("Administrator", "ffff00", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isAdministrator();
		}
	}, "A unique and powerful title that when displayed represents the high level of power the owner has."
			+ "Only administrators on the staff team may display this."),
			
	DEVELOPER("Developer", "9a2d8a", 0, TitleCurrency.NONE, new TitleRequirement() {
				@Override
				public boolean meetsStandard(Player player) {
				return player.getRights().isAdministrator();
			}
		}, "A unique title to represent the persons role on the staff team."
					+ "Only developers on the staff team may display this."),
					
	CMO("Marketing Officer", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
				@Override
				public boolean meetsStandard(Player player) {
				return player.getRights().isAdministrator();
			}
		}, "A unique title to represent the persons role on the staff team."
					+ "Only the marketing manager on the staff team may display this."),
	
	EXECUTIVE_OFFICER("Executive Officer", "df9b16", 0, TitleCurrency.NONE, new TitleRequirement() {
		@Override
		public boolean meetsStandard(Player player) {
			return player.getRights().isOwner() || player.playerName.equalsIgnoreCase("alex");
		}
	}, "The highest ranking title any person may have. This title can only be worn by those who truly deserve it."),
	
	JUNIOR_CADET("Junior Cadet", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 15;
		}
	}, "A Junior Cadet is a player that has achieved at least fifteen player kills."),
	
	SENIOR_CADET("Senior Cadet", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 25;
		}
	}, "A Senior Cadet is a player that has achieved at least twenty five kills."),
	
	SEGEART("Sergeant", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 50;
		}
	}, "A Sergeant is a player that has achieved at least fifty player kills."),
	
	COMMANDER("Commander", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 100;
		}
	}, "A Commander is a player that has achieved at least one hundred player kills."),
	
	MAJOR("Major", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 150;
		}
	}, "A Major is a player that has achieved at least one hundred and fifty player kills."),
	
	CORPORAL("Corporal", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 250;
		}
	}, "A Corporal is a player that has achieved at least two hundred and fifty player kills."),
	
	SPECIALIST("Specialist", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 350;
		}
	}, "A Specialist is a player that has achieved at least three hundred and fifty player kills."),
	
	WARCHIEF("War-chief", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.KC >= 500;
		}
	}, "A War-chief is a player that has achieved at least five hundred player kills."),
	
	GIANT_SLAYER("Giant Slayer", "ff0000", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.getNpcDeathTracker().getTotal() >= 1000;
		}
	}, "A Boss Expert is a player that has killed a total of 1,000 non playable characters. This can be tracked using your boss tracker info page."),
	
	FISHERMAN("Fisherman", "306CC5", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.MASTERFISHER.getId());
		}
	}, "A Fisherman is a player that has fished at least 2,000 fish."),
	
	LUMBERJACK("Lumberjack", "306CC5", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.LUMBERJACK.getId());
		}
	}, "A Lumberjack is a player that has cut down trees and has accumulated at least 2,000 logs."),
	
	MASTER_SLAYER("Master Slayer", "306CC5", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.SLAYER_MASTER.getId());
		}
	}, "To receive access to this title, a player must complete at least 150 slayer tasks."),
	
	GRAVE_DIGGER("Grave Digger", "306CC5", 0, TitleCurrency.NONE, new TitleRequirement() {
		
		@Override
		public boolean meetsStandard(Player player) {
			return player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.DHAROKS_COUSIN.getId());
		}
	}, "To receive access to this title, a player must complete the barrows minigame at least 300 times.");
	
	/**
	 * The name of the title when displayed
	 */
	private final String name;
	
	/**
	 * The color of each character in a title
	 */
	private final String color;
	
	/**
	 * The requirement to display or purchase this title
	 */
	private final TitleRequirement requirement;
	
	/**
	 * The currency used to purcahse the title
	 */
	private final TitleCurrency currency;
	
	/**
	 * The cost to purchase the title
	 */
	private final int cost;
	
	/**
	 * The description displayed on the interface
	 */
	private final String description;
	
	/**
	 * Represents a single title. 
	 * @param name			the name of the title
	 * @param color			the color of each character
	 * @param currency		the currency used to purchase
	 * @param cost			the cost, or amount, of currency required to purchase
	 * @param requirement	the requirement to purcahse or display
	 */
	private Title(String name, String color, int cost, TitleCurrency currency, TitleRequirement requirement, String description) {
		this.name = name;
		this.color = color;
		this.cost = cost;
		this.currency = currency;
		this.requirement = requirement;
		this.description = description;
	}
	
	/**
	 * The name of the title
	 * @return	the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The color of each character in the name
	 * @return	the color 
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * The currency used to purchse the title
	 * @return	the currency
	 */
	public TitleCurrency getCurrency() {
		return currency;
	}
	
	/**
	 * The cost to purchase the title
	 * @return	the cost
	 */
	public int getCost() {
		return cost;
	}
	
	/**
	 * The requirement that must be met before purchasing the title
	 * @return	the requirement
	 */
	public TitleRequirement getRequirement() {
		return requirement;
	}
	
	/**
	 * The sequence of words that define the title
	 * @return	the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Compares the cost value of both titles and returns -1, 0, or +1. 
	 * This follows the comparable contract.
	 */
	@Override
	public int compare(Title o1, Title o2) {
		if (o1.cost > o2.cost) {
			return 1;
		} else if (o1.cost < o2.cost){
			return -1;
		}
		return 0;
	}
	
	/**
	 * A set of elements from the {@link Title} enum. This set is unmodifiable in any regard.
	 * This set serves as a convenience.
	 */
	public static final Set<Title> TITLES = Collections.unmodifiableSet(EnumSet.allOf(Title.class));
}

