package net.darkhax.resourcehogs.registry;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.resourcehogs.ResourceHogs;

public class ResourceRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File DIR_TYPES = new File("config/resourcehogs/types");
    public static final Map<String, ResourceEntry> RESOURCE_ENTRIES = new HashMap<>();
    public static final Map<String, IResourceType> RESOURCE_TYPES = new HashMap<>();
    public static final IResourceType MISSING = new ResourceTypeMissing();

    public static void constructEntries () {

        for (final ResourceEntry entry : RESOURCE_ENTRIES.values()) {

            try {

                final PigResourceType type = new PigResourceType(entry);
                type.validate();

                if (!PigResourceType.errors.containsKey(entry.getId())) {

                    RESOURCE_TYPES.put(type.getId(), type);
                }
            }

            catch (final Exception e) {

                ResourceHogs.LOG.error("Failed to load pig type {}.", entry.getId());
                ResourceHogs.LOG.catching(e);
            }
        }

        ResourceHogs.LOG.info("Successfully loaded {} resource types.", RESOURCE_TYPES.size());
    }

    public static void loadResourceEntries () {

        if (!DIR_TYPES.exists()) {

            DIR_TYPES.mkdirs();
            return;
        }

        for (final File file : DIR_TYPES.listFiles()) {

            final String fileName = file.getName();

            if (fileName.endsWith(".json")) {

                try (FileReader json = new FileReader(file)) {

                    final ResourceEntry entry = GSON.fromJson(json, ResourceEntry.class);

                    if (entry != null) {

                        RESOURCE_ENTRIES.put(entry.getId(), entry);
                    }

                    else {

                        ResourceHogs.LOG.error("Could not load pig from {}.", fileName);
                    }
                }

                catch (final Exception e) {

                    ResourceHogs.LOG.error("Unable to load file {}. Please make sure it's a valid json.", fileName);
                    ResourceHogs.LOG.catching(e);
                }
            }

            else {

                ResourceHogs.LOG.error("Found invalid file {} in the types folder. It must be a .json file!", fileName);
            }
        }

        ResourceHogs.LOG.info("Loaded {} pig types.", RESOURCE_ENTRIES.size());
    }

    public static IResourceType getRandomType () {

        final List<IResourceType> valuesList = new ArrayList<>(RESOURCE_TYPES.values());
        return valuesList.get(Constants.RANDOM.nextInt(valuesList.size()));
    }

    public static IResourceType getType (String name) {

        return RESOURCE_TYPES.getOrDefault(name, MISSING);
    }
}