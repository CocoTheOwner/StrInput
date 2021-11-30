/*
 * This file is part of the Strinput distribution (https://github.com/CocoTheOwner/Strinput).
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
package environment;

import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.List;

/**
 * StrCenter test implementation.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class TestCenter extends StrCenter {

    public static TestCenter SUT = null;

    static {
        try {
            SUT = new TestCenter();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     *
     * @throws InstanceAlreadyExistsException when this command system is already running
     */
    public TestCenter() throws InstanceAlreadyExistsException {
        super(
                new File("testSettings"),
                new TestUser(),
                new StrParameterHandler[0],
                new StrContextHandler[0],
                true,
                new TestRoot()
        );
    }
}
