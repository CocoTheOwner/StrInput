package nl.codevs.strinput.system;

import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.virtual.StrVirtualCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Command roots mapping. Functions just as a normal {@link ConcurrentHashMap} but setup is built-in.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrRoots extends ConcurrentHashMap<String, StrVirtualCategory> {

    /**
     * Create command roots
     * @param enableSettingsCommands if set to true, enables commands for the system's settings
     * @param categories array of categories
     * @param center the controlling command center
     */
    public StrRoots(final boolean enableSettingsCommands, final StrCategory[] categories, final StrCenter center) {

        // Debug
        List<StrCategory> rootInstancesFailed = new ArrayList<>();
        List<StrCategory> rootInstancesSuccess = new ArrayList<>();
        List<String> registeredRootNames = new ArrayList<>();

        // Roots
        List<StrCategory> roots = new ArrayList<>(List.of(categories));
        if (enableSettingsCommands) {
            roots.add(new StrSettings());
        }

        // Setup each root
        roots.forEach(r -> {
            if (r.getClass().isAnnotationPresent(StrInput.class)) {
                rootInstancesSuccess.add(r);
            } else {
                rootInstancesFailed.add(r);
                return;
            }

            // Get input annotation of the root instance
            StrInput input = r.getClass().getDeclaredAnnotation(StrInput.class);

            // Instance names
            List<String> names = new ArrayList<>();
            names.add(input.name());
            names.addAll(Arrays.asList(input.aliases()));

            // Actual virtual category (root)
            StrVirtualCategory root = new StrVirtualCategory(null, r, center);

            // Add names to root map
            names.forEach(n -> {
                registeredRootNames.add(n);
                put(n, root);
            });
        });

        // Debug startup
        if (StrCenter.settings.debugStartup) {
            if (rootInstancesSuccess.isEmpty()) {
                center.debug(new Str(C.R).a("No successful root instances registered. Did you register all commands in the creator? Are they all annotated?"));
            } else {
                Str r = new Str(C.G).a("Loaded root category classes: ");
                rootInstancesSuccess.forEach(c -> r.a(C.Y).a(c.getClass().getSimpleName()).a(C.G).a(", "));
                center.debug(r);
            }

            if (rootInstancesFailed.size() > 0) {
                Str r = new Str(C.R);
                rootInstancesFailed.forEach(c -> r.a(C.R).a(", ").a(C.Y).a(c.getClass().getSimpleName()));
                center.debug(r.a("Failed root instances: ").a(C.Y));
            }

            if (registeredRootNames.isEmpty()) {
                center.debug(new Str(C.R).a("No root commands registered! Did you register all commands in the creator? Are they @StrInput annotated?"));
            } else {
                Str r = new Str(C.G).a("Loaded root commands: ");
                registeredRootNames.forEach(c -> r.a(C.Y).a(c).a(C.G).a(", "));
                center.debug(r);
            }
        }
    }
}
