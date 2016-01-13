package ab;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import ab.net.NetworkConstants;
import ab.net.PipelineFactory;

public class NewServer {
	
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
	private final ServerBootstrap serverBootstrap = new ServerBootstrap();
	
	private final ExecutorService networkExecutor = Executors.newCachedThreadPool();
	
	private final Timer timer = new HashedWheelTimer();
	
	public NewServer() throws Exception {
		logger.info("Starting OS-Perfection...");
	}
	
	public static void main(String[] args) {
		NewServer server = null;
		try {
			server = new NewServer();
			server.init();

			SocketAddress service = new InetSocketAddress(NetworkConstants.SERVICE_PORT);

			server.start();
			server.bind(service);
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Error whilst starting server.", t);
		}
	}
	
	public void bind(SocketAddress serviceAddress) {
		logger.fine("Binding service listener to address: " + serviceAddress + "...");
		serverBootstrap.bind(serviceAddress);

		logger.info("Ready for connections.");
	}
	
	public void init() {
		
		ChannelFactory factory = new NioServerSocketChannelFactory(networkExecutor, networkExecutor);
		serverBootstrap.setFactory(factory);

		ChannelPipelineFactory servicePipelineFactory = new PipelineFactory(timer);
		serverBootstrap.setPipelineFactory(servicePipelineFactory);
		
	}
	
	public void start() throws Exception {
		
	}
	
}
