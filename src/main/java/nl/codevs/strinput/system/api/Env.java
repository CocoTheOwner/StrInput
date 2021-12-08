package nl.codevs.strinput.system.api;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Env {

    /**
     * Get the current user.
     * @return the current user
     */
    public static StrUser user() {
        return UserContext.get();
    }

    /**
     * Get the current center.
     * @return the current center
     */
    public static StrCenter center() {
        return CenterContext.get();
    }

    /**
     * Get the current settings.
     * @return the current settings
     */
    public static StrSettings settings() {
        return center().getSettings();
    }

    /**
     * Add the {@link StrCenter} to the context map & removes dead threads.
     *
     * @param center the center
     */
    public static void touch(StrCenter center) {
        CenterContext.touch(center);
    }

    /**
     * Add the {@link StrUser} to the context map & removes dead threads.
     *
     * @param user the user
     */
    public static void touch(StrUser user) {
        UserContext.touch(user);
    }

    /**
     * Get whether this thread is registered.
     * @return true if this thread is registered
     */
    public static boolean registered() {
        return center() != null;
    }

    /**
     * Context handling (user handling).
     *
     * This system REQUIRES:
     * <ul>
     *     <li>each command to be be handled in a new thread</li>
     *     <li>a call to {@link #touch(StrUser)} asap after a command call</li>
     * </ul>
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class UserContext {

        private static final ConcurrentHashMap<Thread, StrUser> context = new ConcurrentHashMap<>();

        /**
         * Get the current user from the current thread's context
         *
         * @return the {@link StrUser} for this thread
         */
        public static StrUser get() {
            return context.get(Thread.currentThread());
        }

        /**
         * Add the {@link StrUser} to the context map & removes dead threads
         *
         * @param user the user
         */
        public static void touch(StrUser user) {
            synchronized (context) {
                context.put(Thread.currentThread(), user);

                Enumeration<Thread> contextKeys = context.keys();

                while (contextKeys.hasMoreElements()) {
                    Thread thread = contextKeys.nextElement();
                    if (!thread.isAlive()) {
                        context.remove(thread);
                    }
                }
            }
        }
    }

    /**
     * Context handling for command centers (command center handling).
     *
     * This system REQUIRES:
     * <ul>
     *     <li>each command to be be handled in a new thread</li>
     *     <li>a call to {@link #touch(StrCenter)} asap after a command call</li>
     * </ul>
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class CenterContext {

        private static final ConcurrentHashMap<Thread, StrCenter> context = new ConcurrentHashMap<>();

        /**
         * Get the current center from the current thread's context
         *
         * @return the {@link StrCenter} for this thread
         */
        public static StrCenter get() {
            return context.get(Thread.currentThread());
        }

        /**
         * Add the {@link StrCenter} to the context map & removes dead threads
         *
         * @param center the center
         */
        public static void touch(StrCenter center) {
            synchronized (context) {

                Enumeration<Thread> contextKeys = context.keys();

                while (contextKeys.hasMoreElements()) {
                    Thread thread = contextKeys.nextElement();
                    if (!thread.isAlive()) {
                        context.remove(thread);
                    }
                }

                context.put(Thread.currentThread(), center);
            }
        }
    }
}
