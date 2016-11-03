package me.Yukun.DelayCommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Api {
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String removeColor(String msg) {
		msg = ChatColor.stripColor(msg);
		return msg;
	}

	public static String getConfigString(String path) {
		String msg = Main.settings.getConfig().getString(path);
		return msg;
	}
	
	public static String getMessagesString(String path) {
		String msg = Main.settings.getMessages().getString(path);
		return msg;
	}

	public static boolean equalsString(Player player, String maincmd, String perfcmd) {
		for (String line : Main.settings.getConfig().getStringList("Commands." + maincmd + ".Args")) {
			if (((maincmd + " " + line).replace("%player%", player.getName())).equalsIgnoreCase(perfcmd.replace("/", ""))) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}
	
	public static boolean containsString(String msg, String path) {
		for (String line : Main.settings.getConfig().getConfigurationSection(path).getKeys(false)) {
			if (msg.toLowerCase().contains(line.toLowerCase())) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public static String getCommand(String msg, String path) {
		for (String line : Main.settings.getConfig().getConfigurationSection(path).getKeys(false)) {
			if (msg.toLowerCase().contains(line.toLowerCase())) {
				return line;
			} else {
				continue;
			}
		}
		return null;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Integer getConfigInt(String path) {
		String msg = Main.settings.getConfig().getString(path);
		if (Api.isInt(msg)) {
			return Integer.parseInt(msg);
		} else {
			return null;
		}
	}
}
