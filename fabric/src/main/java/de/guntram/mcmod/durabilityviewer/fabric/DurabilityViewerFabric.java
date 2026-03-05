package de.guntram.mcmod.durabilityviewer.fabric;

import de.guntram.mcmod.durabilityviewer.DurabilityViewerConstants;
import net.fabricmc.api.ClientModInitializer;

public class DurabilityViewerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DurabilityViewerConstants.setModApiBride(new FabricModApiBridge());
        DurabilityViewerConstants.getDurabilityViewerInstance().init();
        DurabilityViewerConstants.setEvents(new FabricEvents());
    }





}