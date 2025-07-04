package net.dxzzz.skinsrestorerlink2npc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkinsRestorerLink2Npc extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("dxzskin").setExecutor(new CommandExc(this));
        getLogger().info("§aSkinsRestorerLink2Npc 插件已加载");
        getLogger().info("§b作者: Wyuu101");
        getLogger().info("");
        getLogger().info("§7小声bb:都怪SkinsRestorer ( ´•︵•` )");
        getLogger().info("");
    }

    @Override
    public void onDisable() {

    }
}
