package io.github.eon08.plotMan.command;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.github.eon08.plotMan.db.DB;
import io.github.eon08.plotMan.listener.PlayerInteractListener;
import io.github.eon08.plotMan.plot.Plot;
import io.github.eon08.plotMan.util.ParticleUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.eon08.plotMan.listener.PlayerInteractListener.pos1;
import static io.github.eon08.plotMan.listener.PlayerInteractListener.pos2;

public class PlotCommand implements CommandExecutor, TabCompleter {

    private final Map<String, CommandAction> commandActions = new HashMap<>();

    public PlotCommand() {
        commandActions.put("create", this::create);
        commandActions.put("remove", this::remove);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage("명령어를 입력하세요. 사용법: /plot <create|remove>");
            return false;
        }

        CommandAction action = commandActions.get(args[0].toLowerCase());
        if (action != null) {
            action.execute(sender);
            return true;
        } else {
            sender.sendMessage("알 수 없는 명령어입니다. 사용법: /plot <create|remove>");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("plot.command.create")) {
                completions.add("create");
            }
            if (sender.hasPermission("plot.command.remove")) {
                completions.add("remove");
            }
        }
        return completions;
    }

    private void create(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UUID playerUUID = p.getUniqueId();
            if (pos1.containsKey(playerUUID) && pos2.containsKey(playerUUID)) {
                BlockVector3 position1 = pos1.get(playerUUID);
                BlockVector3 position2 = pos2.get(playerUUID);
                if (position1 == null || position2 == null) {
                    p.sendMessage("위치가 올바르게 설정되지 않았습니다.");
                    return;
                }
                CuboidRegion region = new CuboidRegion(position1, position2);
                Plot plot = new Plot(playerUUID, region);
                DB db = new DB("plots", plot.getPlotUUID().toString());
                db.set("plot", plot);
                p.sendMessage("새 플롯이 생성되었습니다: " + plot.getPlotUUID());
                p.sendMessage(region.getMinimumPoint() + ", " + region.getMaximumPoint());
                ParticleUtil.showPlotBorder(p.getWorld(), region);
            } else {
                p.sendMessage("pos1과 pos2를 먼저 설정해야 합니다.");
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
        }
    }

    private void remove(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UUID playerUUID = p.getUniqueId();
            // 플롯 제거 로직 (추가 필요)
            // 예시: DB에서 해당 플레이어의 플롯을 삭제하는 코드 작성
            p.sendMessage("플롯이 제거되었습니다."); // 실제 로직에 따라 메시지 수정
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("콘솔에서는 이 명령어를 실행할 수 없습니다.");
        }
    }

    @FunctionalInterface
    private interface CommandAction {
        void execute(CommandSender sender);
    }
}
