package de.guntram.mcmod.durabilityviewer;

import java.nio.file.Path;

/**
 * The few things the common code needs from the mod loader it runs on.
 * Each loader module passes an implementation to {@link DurabilityViewer#init}.
 */
public interface Platform {

    /** The game's config directory, e.g. {@code .minecraft/config}. */
    Path getConfigDir();
}
