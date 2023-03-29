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
package nl.codevs.strinput.system.virtual;

import environment.TestCenter;
import environment.TestRoot;
import nl.codevs.strinput.system.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link StrVirtualCategory}.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
class StrVirtualCategoryTest {

    @BeforeAll
    static void setup() {
        if (Context.center() == null) {
            Context.touch(new TestCenter());
        }
        Context.touch(Context.center().getConsole());
    }

    @Test
    void getDefaultName() {
        assertEquals("TestRoot", new StrVirtualCategory(null, new TestRoot()).getDefaultName());
    }

    @Test
    void runOne() {
        new StrVirtualCategory(null, new TestRoot()).run(new ArrayList<>(List.of("mult", "2", "3")));
        assertEquals(6, TestRoot.multResult);
    }
}