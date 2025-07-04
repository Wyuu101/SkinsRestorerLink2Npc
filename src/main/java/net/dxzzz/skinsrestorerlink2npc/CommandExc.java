package net.dxzzz.skinsrestorerlink2npc;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.connections.MineSkinAPI;
import net.skinsrestorer.api.connections.model.MineSkinResponse;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinIdentifier;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class CommandExc implements CommandExecutor {
    private final JavaPlugin plugin;

    public CommandExc(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (!sender.isOp()) {
                return true;
            }
        }
        if (args.length < 1) {
            return false;
        }
        switch (args[0]) {
            case "update":
                if (args.length < 2) {
                    sender.sendMessage("§c缺乏参数");
                    return false;
                } else {
                    String targetName = args[1];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§c目标玩家不在线");
                        return true;
                    }
                    UpdateGenuinePlayerSkin(target,targetName);
                    return true;
                }
            case "clear":
                if (args.length < 2) {
                    sender.sendMessage("§c缺乏参数");
                    return false;
                } else{
                    String targetName = args[1];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§c目标玩家不在线");
                        return true;
                    }
                    ClearPlayerSkin(target);
                    return true;
                }
            case "set":
                if (args.length < 3) {
                    sender.sendMessage("§c缺乏参数");
                    return false;
                } else {
                    String targetName = args[1];
                    String skinName = args[2];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§c目标玩家不在线");
                        return true;
                    }
                    UpdatePlayerSkin(target, skinName);
                    return true;
                }
            case "url":
                if (args.length < 3) {
                    sender.sendMessage("§c缺乏参数");
                    return false;
                } else {
                    String targetName = args[1];
                    String skinURL = args[2];
                    Player target = Bukkit.getPlayer(targetName);
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§c目标玩家不在线");
                        return true;
                    }
                    UpdatePlayerSkinURL(target, skinURL);
                    return true;
                }
        }
        return true;
    }


    // 更新正版玩家皮肤
    private void UpdateGenuinePlayerSkin(Player target ,String skinName){
        if (!target.hasPermission("zbverify.skin")) {
            target.sendMessage("§b皮肤系统 >> §c你还没有进行正版验证，快输入§e /zb §c进行验证吧~");
            return;
        }
        UpdatePlayerSkin(target,skinName);
    }


    private void UpdatePlayerSkin(Player target ,String skinName){
        target.sendMessage("§b皮肤系统 >> §a正在更新皮肤，请稍后...");
        //异步，防止卡顿
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            SkinsRestorer skinsRestorerAPI = SkinsRestorerProvider.get();
            SkinStorage skinStorage = skinsRestorerAPI.getSkinStorage();
            PlayerStorage playerStorage = skinsRestorerAPI.getPlayerStorage();

            try {
                Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(skinName);

                if (result.isPresent()) {
                    SkinIdentifier skinIdentifier = result.get().getIdentifier();
                    playerStorage.setSkinIdOfPlayer(target.getUniqueId(), skinIdentifier);

                    target.sendMessage("§b皮肤系统 >> §a皮肤更新成功，重新加入服务器以生效~");
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        target.sendMessage("§b皮肤系统 >> §c皮肤更新失败,请稍后重试...");
                    });
                }
            } catch (DataRequestException | MineSkinException e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    target.sendMessage("§b皮肤系统 >> §c皮肤更新失败,请稍后重试...");
                });
            }
        });
    }


    private void UpdatePlayerSkinURL(Player target, String skinUrl) {
        target.sendMessage("§b皮肤系统 >> §a正在更新皮肤，请稍后...");
        // 异步任务，避免主线程卡顿
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                SkinsRestorer skinsRestorerAPI = SkinsRestorerProvider.get();
                SkinStorage skinStorage = skinsRestorerAPI.getSkinStorage();
                PlayerStorage playerStorage = skinsRestorerAPI.getPlayerStorage();
                MineSkinAPI mineSkinAPI = skinsRestorerAPI.getMineSkinAPI();

                // 调用 MineSkin 生成皮肤
                MineSkinResponse response = mineSkinAPI.genSkin(skinUrl, null);
                SkinProperty property = response.getProperty();

                // 简化名称避免非法字符
                String uuidShort = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                String generatedName = "skin_" + uuidShort;


                // 存储皮肤属性
                skinStorage.setCustomSkinData(generatedName, property);
                playerStorage.setSkinIdOfPlayer(target.getUniqueId(), SkinIdentifier.ofCustom(generatedName));

                target.sendMessage("§b皮肤系统 >> §a皮肤更新成功，重新加入服务器以生效~");

            } catch (DataRequestException | MineSkinException e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    target.sendMessage("§b皮肤系统 >> §c皮肤更新失败,请稍后重试...");
                });
            }
        });
    }




    private void ClearPlayerSkin(Player target) {
        target.sendMessage("§b皮肤系统 >> §a正在重置皮肤，请稍后...");
        SkinsRestorer skinsRestorerAPI = SkinsRestorerProvider.get();
        PlayerStorage playerStorage = skinsRestorerAPI.getPlayerStorage();
        // 为离线玩家清空皮肤
        playerStorage.setSkinIdOfPlayer(target.getUniqueId(), null);
        target.sendMessage("§b皮肤系统 >> §a已重置皮肤，重新加入服务器以生效~");
    }
}
