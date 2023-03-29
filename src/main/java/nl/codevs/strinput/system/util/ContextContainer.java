package nl.codevs.strinput.system.util;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context container.
 * <p>
 * This system requires:
 * <ul>
 *     <li>a new thread for each call</li>
 *     <li>a call to {@link #touch(T)} before context is accessed</li>
 * </ul>
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class ContextContainer<T> {

    /**
     * Map containing contextual data.
     */
    private final ConcurrentHashMap<Thread, T> MAP = new ConcurrentHashMap<>();

    /**
     * Get the current data from the current thread's context.
     *
     * @return the data for this thread
     */
    public T get() {
        return MAP.get(Thread.currentThread());
    }

    /**
     * Adds the data to the context map and removes dead threads.
     *
     * @param data the data
     */
    public void touch(final T data) {
        synchronized (MAP) {
            MAP.put(Thread.currentThread(), data);

            Enumeration<Thread> contextKeys = MAP.keys();

            while (contextKeys.hasMoreElements()) {
                Thread thread = contextKeys.nextElement();
                if (!thread.isAlive()) {
                    MAP.remove(thread);
                }
            }
        }
    }
}
