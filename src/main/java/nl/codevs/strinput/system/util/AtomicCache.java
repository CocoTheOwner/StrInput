/*
 * This file is part of the StrInput distribution.
 * (https://github.com/CocoTheOwner/StrInput)
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    /**
     * The stored value.
     */
    private transient volatile T value;

    /**
     * Stores the time when stored.
     */
    private transient volatile long expiryTime;

    /**
     * Amount of validation checks.
     */
    private transient volatile int validations;

    /**
     * The amount of validations required.
     */
    private static final int REQUIRED_VALIDATIONS = 1000;

    /**
     * Thread safety lock.
     */
    private final ReentrantLock check;

    /**
     * Thread safety lock.
     */
    private final ReentrantLock time;

    /**
     * Thread safety lock.
     */
    private final ReentrantLock write;

    /**
     * Whether this supports null values or not.
     */
    private final boolean nullSupport;

    /**
     * Create a new cache.
     */
    public AtomicCache() {
        this(false);
    }

    /**
     * Create a new cache.
     * @param supportsNull can store null if set to {@code true}
     */
    public AtomicCache(final boolean supportsNull) {
        this.nullSupport = supportsNull;
        check = new ReentrantLock();
        write = new ReentrantLock();
        time = new ReentrantLock();
        validations = 0;
        expiryTime = -1;
        value = null;
    }

    /**
     * Acquire the cache by means of a function.<br>
     * This function is ran once, after which the value is cached and returned.
     * @param supplier the supplier of the value
     * @return the value
     */
    @Nullable public T acquire(@NotNull final Supplier<T> supplier) {
        if (nullSupport) {
            return acquireNull(supplier);
        }

        if (this.value != null && validations > REQUIRED_VALIDATIONS) {
            return this.value;
        }

        if (this.value != null
                && System.currentTimeMillis() - expiryTime
                > REQUIRED_VALIDATIONS) {
            if (this.value != null) {
                //noinspection NonAtomicOperationOnVolatileField
                validations++;
            }

            return this.value;
        }

        check.lock();

        if (this.value == null) {
            write.lock();
            this.value = supplier.get();

            time.lock();

            if (expiryTime == -1) {
                expiryTime = System.currentTimeMillis();
            }

            time.unlock();
            write.unlock();
        }

        check.unlock();
        return this.value;
    }

    /**
     * Acquire the cache by means of a function.<br>
     * This function is ran once,
     * after which the value is cached and returned.<br>
     * This can return null.
     * @param supplier the supplier of the value
     * @return the value of the supplier
     */
    public @Nullable T acquireNull(@NotNull final Supplier<T> supplier) {
        if (validations > REQUIRED_VALIDATIONS) {
            return this.value;
        }

        if (System.currentTimeMillis() - expiryTime > REQUIRED_VALIDATIONS) {
            //noinspection NonAtomicOperationOnVolatileField
            validations++;
            return this.value;
        }

        check.lock();
        write.lock();
        this.value = supplier.get();

        time.lock();

        if (expiryTime == -1) {
            expiryTime = System.currentTimeMillis();
        }

        time.unlock();
        write.unlock();
        check.unlock();
        return this.value;
    }
}
