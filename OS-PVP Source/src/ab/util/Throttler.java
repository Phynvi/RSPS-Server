package ab.util;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Albin
 */
public class Throttler {
 
    private long time = System.currentTimeMillis();
    
    public Throttler reset() {
        time = System.currentTimeMillis();
        return this;
    }
    public long elapsed() {
        return System.currentTimeMillis() - time;
    }
    
    public long elapsed(TimeUnit unit) {
        return unit.convert(elapsed(), TimeUnit.MILLISECONDS);
    }
}
