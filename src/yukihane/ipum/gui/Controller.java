/** $Id$ */
package yukihane.ipum.gui;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import yukihane.ipum.Config;
import yukihane.ipum.Converter;
import yukihane.ipum.Event;

public abstract class Controller implements Runnable {

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
                    break;
                }
                notifyEvent(this.queue.take());
            } catch (InterruptedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected abstract void notifyEvent(Event take);
}
