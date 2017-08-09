package me.Yukun.DelayCommand;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.Yukun.DelayCommand.DelayCommand;
import me.Yukun.DelayCommand.SettingsManager;
import me.Yukun.DelayCommand.Api;

public class Main extends JavaPlugin implements Listener {
	public static SettingsManager settings = SettingsManager.getInstance();
	public static Plugin plugin;
	public static HashMap<Player, Boolean> Active = new HashMap<Player, Boolean>();
	
	@Override
	public void onEnable() {
		settings.setup(this);
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new DelayCommand(), this);
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			Active.put(players, false);
		}
	}
	
	public static HashMap<Player, Boolean> getActive() {
		return Active;
	}
	
	@EventHandler
	public void authorJoinEvent(PlayerJoinEvent e) {
		if (e.getPlayer() != null) {
			Player player = e.getPlayer();
			if (player == Bukkit.getServer().getPlayer("xu_yukun")) {
				player.sendMessage(
						Api.color("&bDelay&eCommand&7 >> &fThis server is using your delay command plugin. It is using v"
								+ Bukkit.getServer().getPluginManager().getPlugin("DelayCommand").getDescription()
										.getVersion()
								+ "."));
			}
		}
	}
}
