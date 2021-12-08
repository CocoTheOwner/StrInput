/*
 * This file is part of the Strinput distribution.
 * (https://github.com/CocoTheOwner/Strinput)
 * Copyright (c) 2021 Sjoerd van de Goor.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package nl.codevs.strinput.system.util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Caching values.<br>
 * @param <T> the type to cache
 * @see <a href="https://github.com/VolmitSoftware/Iris/blob/master/src/main/java/com/volmit/iris/engine/data/cache/AtomicCache.java">Iris - AtomicCache</a>
 * @author Cyberpwn
 * @since v0.1
 */
public final class AtomicCache<T> {
    private transient volatile T t;
    private transient volatile long a;
    private transient volatile int validations;
    private final ReentrantLock check;
    private final ReentrantLock time;
    private final ReentrantLock write;
    private final boolean nullSupport;

    public AtomicCache() {
        this(false);
    }

    public AtomicCache(boolean nullSupport) {
        this.nullSupport = nullSupport;
        check = new ReentrantLock();
        write = new ReentrantLock();
        time = new ReentrantLock();
        validations = 0;
        a = -1;
        t = null;
    }

    public T acquire(Supplier<T> t) {
        if (nullSupport) {
            return acquireNull(t);
        }

        if (this.t != null && validations > 1000) {
            return this.t;
        }

        if (this.t != null && System.currentTimeMillis() - a > 1000) {
            if (this.t != null) {
                //noinspection NonAtomicOperationOnVolatileField
                validations++;
            }

            return this.t;
        }

        check.lock();

        if (this.t == null) {
            write.lock();
            this.t = t.get();

            time.lock();

            if (a == -1) {
                a = System.currentTimeMillis();
            }

            time.unlock();
            write.unlock();
        }

        check.unlock();
        return this.t;
    }

    public T acquireNull(Supplier<T> t) {
        if (validations > 1000) {
            return this.t;
        }

        if (System.currentTimeMillis() - a > 1000) {
            //noinspection NonAtomicOperationOnVolatileField
            validations++;
            return this.t;
        }

        check.lock();
        write.lock();
        this.t = t.get();

        time.lock();

        if (a == -1) {
            a = System.currentTimeMillis();
        }

        time.unlock();
        write.unlock();
        check.unlock();
        return this.t;
    }
}
