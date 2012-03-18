/** $Id$ */
package yukihane.ipum.gui;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yukihane.ipum.Config;
import yukihane.ipum.Converter;
import yukihane.ipum.Event;

public abstract class Controller implements Runnable {

    private static Logger log = LoggerFactory.getLogger(Controller.class);
    private final ExecutorService executors;
    private final LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<Event>();
    private volatile boolean stop;

    public Controller(int nThreads) {
        executors = Executors.newFixedThreadPool(nThreads);
    }

    public Future<File> addTask(Config config, File file) {
        return executors.submit(new Converter(this.queue, config, file));
    }

    public void stop() {
        this.stop = true;
    }

    public void run() {
        while (true) {
            try {
                if (stop) {
                    executors.shutdownNow();
                    break;
                }
                notifyEvent(this.queue.take());
            } catch (InterruptedException ex) {
                log.info("終了要求");
            }
        }
    }

    protected abstract void notifyEvent(Event take);
}
