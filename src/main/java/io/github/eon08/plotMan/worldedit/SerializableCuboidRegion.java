package io.github.eon08.plotMan.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class SerializableCuboidRegion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public SerializableCuboidRegion(@NotNull CuboidRegion region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        this.minX = min.x();
        this.minY = min.y();
        this.minZ = min.z();
        this.maxX = max.x();
        this.maxY = max.y();
        this.maxZ = max.z();
    }

    public CuboidRegion toCuboidRegion() {
        BlockVector3 min = BlockVector3.at(minX, minY, minZ);
        BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);
        return new CuboidRegion(min, max);
    }

    @Override
    public String toString() {
        return "SerializableCuboidRegion{" +
                "min=(" + minX + ", " + minY + ", " + minZ + ")" +
                ", max=(" + maxX + ", " + maxY + ", " + maxZ + ")" +
                '}';
    }
}