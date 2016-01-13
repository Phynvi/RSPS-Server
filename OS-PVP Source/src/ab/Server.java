package ab;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.Vote.MainLoader;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import com.rspserver.motivote.Motivote;

import ab.clip.ObjectDef;
import ab.clip.Region;
import ab.database.ChatLogHandler;
import ab.database.KillLogHandler;
import ab.database.LoginLogHandler;
import ab.event.CycleEventHandler;
import ab.model.holiday.HolidayController;
import ab.model.items.ItemDefinition;
import ab.model.minigames.FightPits;
import ab.model.multiplayer_session.MultiplayerSessionListener;
import ab.model.npcs.NPCDefinitions;
import ab.model.npcs.NPCHandler;
import ab.model.players.PlayerHandler;
import ab.model.players.PlayerSave;
import ab.model.wogw.WellOfGoodWillEvent;
import ab.net.PipelineFactory;
import ab.server.data.ServerData;
import ab.social_media.hitbox.Hitbox;
import ab.util.MadTurnipConnection;
import ab.util.SimpleTimer;
import ab.util.date.Calendar;
import ab.util.log.Logger;
import ab.world.ClanManager;
import ab.world.ItemHandler;
import ab.world.PlayerManager;
import ab.world.ShopHandler;
import ab.world.StillGraphicsManager;
import ab.world.WalkingCheck;
import ab.world.objects.GlobalObjects;

/**
 * The main class needed to start the server.
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30 Revised by Shawn Notes by Shawn
 */
public class Server {
	
	private static final Hitbox hitbox = new Hitbox();

	/**
	 * Represents our calendar with a given delay using the TimeUnit class
	 */
	private static Calendar calendar = new ab.util.date.Calendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));

	private static HolidayController holidayController = new HolidayController();

	private static MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();

	private static GlobalObjects globalObjects = new GlobalObjects();
	
	/**
	 * Timers
	 **/
	private static SimpleTimer engineTimer, debugTimer;

	/**
	 * ClanChat Added by Valiant
	 */
	public static ClanManager clanManager = new ClanManager();
	/* public static MainLoader vote = new MainLoader("70.42.74.5", "root",
	 "bLuB321", "vote");*/

	/**
	 * Calls to manage the players on the server.
	 */
	public static PlayerManager playerManager = null;
	private static StillGraphicsManager stillGraphicsManager = null;

	/**
	 * Sleep mode of the server.
	 */
	public static boolean sleeping;
	/**
	 * The test thingy
	 */
	public static boolean canGiveReward;

	public static long lastReward = 0;
	/**
	 * Calls the rate in which an event cycles.
	 */
	public static final int cycleRate;

	/**
	 * Server updating.
	 */
	public static boolean UpdateServer = false;

	/**
	 * Calls in which the server was last saved.
	 */
	public static long lastMassSave = System.currentTimeMillis();

	/**
	 * Calls the usage of CycledEvents.
	 */
	private static long cycleTime, cycles, totalCycleTime, sleepTime;

	/**
	 * Used for debugging the server.
	 */
	private static DecimalFormat debugPercentFormat;

	/**
	 * Forced shutdowns.
	 */
	public static boolean shutdownServer = false;
	public static boolean shutdownClientHandler;

	public static boolean canLoadObjects = false;

	/**
	 * Used to identify the server port.
	 */
	public static int serverlistenerPort = 43594;

	/**
	 * Contains data which is saved between sessions.
	 */
	public static ServerData serverData = new ServerData();

	/**
	 * Handles the logging of all logins.
	 */
	public static LoginLogHandler loginLogHandler = new LoginLogHandler();

	/**
	 * Handles the logging of all chat messages.
	 */
	public static ChatLogHandler chatLogHandler = new ChatLogHandler();

	/**
	 * Handles the logging of all PvP kills.
	 */
	public static KillLogHandler killLogHandler = new KillLogHandler();

	/**
	 * Calls the usage of player items.
	 */
	public static ItemHandler itemHandler = new ItemHandler();

	/**
	 * Handles logged in players.
	 */
	public static PlayerHandler playerHandler = new PlayerHandler();

	/**
	 * Handles global NPCs.
	 */
	public static NPCHandler npcHandler = new NPCHandler();

	/**
	 * Handles global shops.
	 */
	public static ShopHandler shopHandler = new ShopHandler();
	/**
	 * Donate System
	 */
	public static MadTurnipConnection md;
	/**
	 * Handles the fightpits minigame.
	 */
	public static FightPits fightPits = new FightPits();

	/**
	 * Handles the main game processing.
	 */
	private static final ScheduledExecutorService GAME_THREAD = Executors.newSingleThreadScheduledExecutor();
	
	private static final ScheduledExecutorService IO_THREAD = Executors.newSingleThreadScheduledExecutor();

	static {
		if (!Config.SERVER_DEBUG) {
			serverlistenerPort = 43594;
		} else {
			serverlistenerPort = 5555;
		}
		cycleRate = 600;
		shutdownServer = false;
		engineTimer = new SimpleTimer();
		debugTimer = new SimpleTimer();
		sleepTime = 0;
		debugPercentFormat = new DecimalFormat("0.0#%");
	}
	
	@SuppressWarnings("unchecked")
	public static void main(java.lang.String args[]) {
		try {
			long startTime = System.currentTimeMillis();
			System.setOut(new Logger(System.out));
			System.setErr(new Logger(System.err));
			CycleEventHandler.getSingleton();
			CycleEventHandler.getSingleton().addEvent(new Object(), new WellOfGoodWillEvent(), 1500);
			ItemDefinition.load();
			//NPCDefinitions.getDefini();
			//NPCDefinitions.getDefinitions();
			globalObjects.loadGlobalObjectFile();
			//wtch
			ObjectDef.loadConfig();
			Region.load();
			WalkingCheck.load();
			/*if (Config.mySql) {
				new Motivote(new RewardHandler(), "http://os-perfection.net/vote/", "09aa86ba").start();
				md = new MadTurnipConnection();
				md.start();
			}*/
			bind();
			holidayController.initialize();
			playerManager = PlayerManager.getSingleton();
			playerManager.setupRegionPlayers();
			stillGraphicsManager = new StillGraphicsManager();
			Connection.initialize();
			// HighscoresConfig.loadHighscores();

			/**
			 * Successfully loaded the server.
			 */
			long endTime = System.currentTimeMillis();
			long elapsed = endTime - startTime;
			System.out.println("OS PvP has successfully started up in " + elapsed + " milliseconds.");

			/**
			 * Main server tick.
			 */
			GAME_THREAD.scheduleAtFixedRate(() -> {
				try {
					itemHandler.process();
					playerHandler.process();
					npcHandler.process();
					shopHandler.process();
					globalObjects.pulse();
					CycleEventHandler.getSingleton().process();
					serverData.processQueue();
				} catch (Throwable t) {
					t.printStackTrace();
					PlayerHandler.stream().filter(Objects::nonNull).forEach(PlayerSave::save);
				}
			}, 0, 600, TimeUnit.MILLISECONDS);
			IO_THREAD.scheduleAtFixedRate(() -> { // note, this isn't thread safe
				try {
					hitbox.update();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}, 0, 30_000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the sleep mode timer and puts the server into sleep mode.
	 */
	public static long getSleepTimer() {
		return sleepTime;
	}

	/**
	 * Gets the Graphics manager.
	 */
	public static StillGraphicsManager getStillGraphicsManager() {
		return stillGraphicsManager;
	}

	/**
	 * Gets the Player manager.
	 */
	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static MultiplayerSessionListener getMultiplayerSessionListener() {
		return multiplayerSessionListener;
	}

	/**
	 * Java connection. Ports.
	 */
	private static void bind() {
		ServerBootstrap serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
		serverBootstrap.bind(new InetSocketAddress(serverlistenerPort));
	}

	public static Calendar getCalendar() {
		return calendar;
	}

	public static HolidayController getHolidayController() {
		return holidayController;
	}

	public static ServerData getServerData() {
		return serverData;
	}

	public static GlobalObjects getGlobalObjects() {
		return globalObjects;
	}

	public static LoginLogHandler getLoginLogHandler() {
		return loginLogHandler;
	}
	public static ChatLogHandler getChatLogHandler() {
		return chatLogHandler;
	}

	public static KillLogHandler getKillLogHandler() {
		return killLogHandler;
	}

}
