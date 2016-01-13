package ab.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class Clock implements Runnable {

	public static final AtomicLong TIME = new AtomicLong();
	private static ScheduledExecutorService clockService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
			.setPriority(Thread.MIN_PRIORITY).setNameFormat("ClockThread").build());

	static {
		clockService.scheduleAtFixedRate(new Clock(), 10, 10, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		TIME.addAndGet(10);
	}
}
