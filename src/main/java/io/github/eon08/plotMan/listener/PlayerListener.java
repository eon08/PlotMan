package io.github.eon08.plotMan.listener;

import io.github.eon08.plotMan.db.DB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DB db = new DB("user", p.getUniqueId().toString());
        if (db.get("join-count", Integer.class) == null) {
            db.set("join-count", 1);
        } else {
            db.set("join-count", db.get("join-count", Integer.class) + 1);
        }
        p.sendMessage("접속 횟수: " + db.get("join-count", Integer.class));
    }
}
