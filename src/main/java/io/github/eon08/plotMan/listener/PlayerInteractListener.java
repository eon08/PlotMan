package io.github.eon08.plotMan.listener;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    public static Map<UUID, BlockVector3> pos1 = new HashMap<>();
    public static Map<UUID, BlockVector3> pos2 = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Material itemType = player.getInventory().getItemInMainHand().getType();
        if (itemType == Material.STICK) {
            e.setCancelled(true);
            UUID playerId = player.getUniqueId();
            BlockVector3 clickedPos = BlockVector3.at(
                    e.getClickedBlock().getX(),
                    e.getClickedBlock().getY(),
                    e.getClickedBlock().getZ()
            );
            if (e.getClickedBlock() == null) {
                player.sendMessage("유효한 블록을 클릭하세요.");
                return;
            }

            switch (e.getAction()) {
                case LEFT_CLICK_BLOCK -> {
                    if (!pos1.getOrDefault(playerId, BlockVector3.ZERO).equals(clickedPos)) {
                        pos1.put(playerId, clickedPos);
                        player.sendMessage("pos1 설정됨: " + formatVector(clickedPos));
                    }
                }
                case RIGHT_CLICK_BLOCK -> {
                    if (!pos2.getOrDefault(playerId, BlockVector3.ZERO).equals(clickedPos)) {
                        pos2.put(playerId, clickedPos);
                        player.sendMessage("pos2 설정됨: " + formatVector(clickedPos));
                    }
                }
            }
        }
    }

    private @NotNull String formatVector(@NotNull BlockVector3 vec) {
        return String.format("X: %d, Y: %d, Z: %d", vec.x(), vec.y(), vec.z());
    }
}