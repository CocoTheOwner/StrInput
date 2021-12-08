package nl.codevs.strinput.system.virtual;

import environment.TestCenter;
import environment.TestRoot;
import nl.codevs.strinput.system.api.Env;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrVirtualCategoryTest {

    @BeforeAll
    static void setup() {
        if (Env.center() == null) {
            Env.touch(new TestCenter());
        }
        Env.touch(Env.center().getConsole());
    }

    @Test
    void getDefaultName() {
        assertEquals("TestRoot", new StrVirtualCategory(null, new TestRoot()).getDefaultName());
    }

    @Test
    void run() {
    }
}