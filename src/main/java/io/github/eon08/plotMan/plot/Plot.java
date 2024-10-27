package io.github.eon08.plotMan.plot;

import com.sk89q.worldedit.regions.CuboidRegion;
import io.github.eon08.plotMan.worldedit.SerializableCuboidRegion;

import java.io.Serializable;
import java.util.UUID;

public class Plot implements Serializable {

    private final UUID plotUUID = UUID.randomUUID();
    private UUID ownerUUID;
    private SerializableCuboidRegion serializableCuboidRegion;

    public Plot(UUID ownerUUID, CuboidRegion region) {
        this.ownerUUID = ownerUUID;
        this.serializableCuboidRegion = new SerializableCuboidRegion(region);
    }

    public UUID getPlotUUID() {
        return plotUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setCuboidRegion(CuboidRegion region) {
        this.serializableCuboidRegion = new SerializableCuboidRegion(region);
    }

    public CuboidRegion getCuboidRegion() {
        return serializableCuboidRegion.toCuboidRegion();
    }
}
