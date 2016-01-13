package ab;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import com.rspserver.motivote.Motivote;

import ab.clip.ObjectDef;
import ab.clip.Region;
import ab.event.CycleEventHandler;
import ab.model.holiday.HolidayController;
import ab.model.minigames.FightPits;
import ab.model.multiplayer_session.MultiplayerSessionListener;
import ab.model.npcs.NPCHandler;
import ab.model.players.PlayerHandler;
import ab.model.players.PlayerSave;
import ab.net.PipelineFactory;
import ab.util.SimpleTimer;
import ab.util.date.Calendar;
import ab.util.log.Logger;
import ab.world.ClanManager;
import ab.world.ItemHandler;
import ab.world.PlayerManager;
import ab.world.ShopHandler;
import ab.world.StillGraphicsManager;
import ab.world.WalkingCheck;

/**
 * The main class needed to start the server.
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30 Revised by Shawn Notes by Shawn
 */
public class ServerEco {

	/**
	 * Represents our calendar with a given delay using the TimeUnit class
	 */
	private static Calendar calendar = new ab.util.date.Calendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));

	private static HolidayController holidayController = new HolidayController();
	
	private static MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();

	/** A general purpose executor. */
	public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	/**
	 * Timers
	 **/
	private static SimpleTimer engineTimer, debugTimer;

	/**
	 * ClanChat Added by Valiant
	 */
	public static ClanManager clanManager = new ClanManager();
	//public static MainLoader vote = new MainLoader("70.42.74.5", "root", "bLuB321", "vote");

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
	public static int serverlistenerPort = 5555;

	/**
	 * Contains data which is saved between sessions
	 */
//	public static ServerData serverData = new ServerData();
	
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
	 * Handles the fightpits minigame.
	 */
	public static FightPits fightPits = new FightPits();

	/**
	 * Handles the main game processing.
	 */
	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


	static {
		if (!Config.SERVER_DEBUG) {
			serverlistenerPort = 5555;
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

	/**
	 * Starts the server.
	 */
	public static void main(java.lang.String args[]) throws NullPointerException, IOException {
		long startTime = System.currentTimeMillis();
		System.setOut(new Logger(System.out));
		System.setErr(new Logger(System.err));
		Config.PLACEHOLDER_ECONOMY = true;
		ObjectDef.loadConfig();
		Region.load();
		WalkingCheck.load();
		bind();
		
		//new Motivote(new RewardHandler(), "http://os-pvp.net/vote/", "e3be0ab1af").start();
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
		System.out.println("Server started up in " + elapsed + " ms.");
		System.out.println("OS PvP: ONLINE");

		/**
		 * Main server tick.
		 */
		executorService.scheduleAtFixedRate(() -> {
			try {
				itemHandler.process();
				playerHandler.process();
				npcHandler.process();
				shopHandler.process();
			//	fightPits.process();
				CycleEventHandler.getSingleton().process();
			} catch (Throwable t) {
				t.printStackTrace();
				PlayerHandler.stream().filter(Objects::nonNull).forEach(PlayerSave::save);
			}
		}, 0, 600, TimeUnit.MILLISECONDS);
	}

	/**
	 * Logging execution.
	 */
	public static boolean playerExecuted = false;

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
	/**
	 * Gets the ServerData, which contains data which is saved between sessions.
	 * @return The ServerData Object.
	 */
//	public static ServerData getServerData() {
//		return serverData;
//	}

}
