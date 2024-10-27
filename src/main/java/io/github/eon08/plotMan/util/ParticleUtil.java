package io.github.eon08.plotMan.util;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class ParticleUtil {

    public static void showPlotBorder(World world, @NotNull CuboidRegion region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 outerMax = region.getMaximumPoint().add(1, 1, 1);

        Location[] corners = {
                new Location(world, min.x(), min.y(), min.z()),
                new Location(world, min.x(), min.y(), outerMax.z()),
                new Location(world, outerMax.x(), min.y(), outerMax.z()),
                new Location(world, outerMax.x(), min.y(), min.z()),
                new Location(world, min.x(), outerMax.y(), min.z()),
                new Location(world, min.x(), outerMax.y(), outerMax.z()),
                new Location(world, outerMax.x(), outerMax.y(), outerMax.z()),
                new Location(world, outerMax.x(), outerMax.y(), min.z())
        };

        drawLines(world, corners);
        drawVerticalLines(world, corners, outerMax);
    }

    private static void drawLines(World world, Location[] corners) {
        for (int i = 0; i < 4; i++) {
            drawLine(world, corners[i], corners[(i + 1) % 4]); // 바닥 사각형
            drawLine(world, corners[i + 4], corners[(i + 1) % 4 + 4]); // 위쪽 사각형
        }
    }

    private static void drawVerticalLines(World world, Location[] corners, BlockVector3 outerMax) {
        for (int i = 0; i < 4; i++) {
            drawLine(world, corners[i], new Location(world, corners[i].getX(), outerMax.y(), corners[i].getZ()));
        }
    }

    private static void drawLine(World world, @NotNull Location start, Location end) {
        double distance = start.distance(end);
        int steps = (int) (distance * 20);
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = start.getX() + (end.getX() - start.getX()) * t;
            double y = start.getY() + (end.getY() - start.getY()) * t;
            double z = start.getZ() + (end.getZ() - start.getZ()) * t;
            world.spawnParticle(Particle.HAPPY_VILLAGER, new Location(world, x, y, z), 1);
        }
    }
}