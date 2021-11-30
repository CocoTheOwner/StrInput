package environment;

import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.List;

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
