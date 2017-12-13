package me.Yukun.DelayCommand;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class DelayCommand implements Listener {
	Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("DelayCommand");
	HashMap<Player, Boolean> performing2 = Main.getActive();
	HashMap<Player, String> perfcmd = new HashMap<Player, String>();
	HashMap<Player, String> maincmd = new HashMap<Player, String>();
	HashMap<Player, Integer> delay = new HashMap<Player, Integer>();
	HashMap<Player, Integer> time = new HashMap<Player, Integer>();
	HashMap<Player, Integer> CountDown = new HashMap<Player, Integer>();
	HashMap<Player, Integer> TagChecker = new HashMap<Player, Integer>();
	HashMap<Player, Boolean> Tagged = new HashMap<Player, Boolean>();
	String prefix = Api.getMessagesString("Messages.Prefix");
	String sending = Api.getMessagesString("Messages.Sending");
	String cmdsent = Api.getMessagesString("Messages.CommandSent");
	String moved = Api.getMessagesString("Messages.Moved");
	String waiting = Api.getMessagesString("Messages.Waiting");
	String dead = Api.getMessagesString("Messages.Dead");
	String tagged = Api.getMessagesString("Messages.Tagged");

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		performing2.put(e.getPlayer(), false);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (performing2.get(player) == true) {
			player.sendMessage(Api.color(prefix + dead));
			performing2.remove(player);
			perfcmd.remove(player);
			maincmd.remove(player);
			delay.remove(player);
			time.remove(player);
			Tagged.remove(player);
			Bukkit.getServer().getScheduler().cancelTask(CountDown.get(player));
			CountDown.remove(player);
			Bukkit.getServer().getScheduler().cancelTask(TagChecker.get(player));
			TagChecker.remove(player);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = (Player) e.getEntity();
		if (performing2.get(player) == true) {
			if (Api.getConfigString("Options.EnableDeadMessage").equalsIgnoreCase("true")) {
				player.sendMessage(Api.color(prefix + dead));
			}
			performing2.put(player, false);
			perfcmd.remove(player);
			maincmd.remove(player);
			delay.remove(player);
			time.remove(player);
			Tagged.remove(player);
			Bukkit.getServer().getScheduler().cancelTask(CountDown.get(player));
			CountDown.remove(player);
			Bukkit.getServer().getScheduler().cancelTask(TagChecker.get(player));
			TagChecker.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if (Api.containsString(e.getMessage(), "Commands")) {
			maincmd.put(player, Api.getCommand(e.getMessage(), "Commands"));
			if (!player.hasPermission("DelayCommand.Bypass") || !player.isOp()) {
				if (Api.getConfigString("Commands." + maincmd.get(player) + ".Fuzzy").equalsIgnoreCase("false")) {
					if (Api.equalsString(player, maincmd.get(player), e.getMessage())) {
						if (performing2.get(player) == false) {
							if (!Api.isTagged(player)) {
								if (Api.getConfigString("Options.EnableSendingMessage").equalsIgnoreCase("true")) {
									player.sendMessage(Api
											.color(prefix + sending.replace("%command%", "'" + e.getMessage() + "'")));
								}
								performing2.put(player, true);
								perfcmd.put(player, e.getMessage().replace("/", ""));
								Tagged.put(player, false);
								if (Api.getConfigString("Options.DelayFormat").equalsIgnoreCase("ticks")) {
									delay.put(player, 20);
									time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
								} else if (Api.getConfigString("Options.DelayFormat").equalsIgnoreCase("seconds")) {
									delay.put(player, 1);
									time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
								} else {
									delay.put(player, 20);
									time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
								}
								e.setCancelled(true);
								TagChecker.put(player, Bukkit.getServer().getScheduler()
										.scheduleSyncRepeatingTask(plugin, new Runnable() {
											Player fplayer = e.getPlayer();

											public void run() {
												if (Api.isTagged(fplayer)) {
													performing2.put(fplayer, false);
													perfcmd.remove(fplayer);
													maincmd.remove(fplayer);
													delay.remove(fplayer);
													time.remove(fplayer);
													Tagged.remove(fplayer);
													Bukkit.getServer().getScheduler()
															.cancelTask(CountDown.get(fplayer));
													CountDown.remove(fplayer);
													fplayer.sendMessage(Api.color(prefix
															+ tagged.replace("%command%", "'" + e.getMessage() + "'")));
													Bukkit.getServer().getScheduler()
															.cancelTask(TagChecker.get(fplayer));
													TagChecker.remove(fplayer);
												}
											}
										}, 0, 1));
								CountDown.put(player, Bukkit.getServer().getScheduler()
										.scheduleSyncRepeatingTask(plugin, new Runnable() {
											Player fplayer = e.getPlayer();

											public void run() {
												if (time.get(fplayer) != null && time.get(fplayer) >= 1) {
													if (performing2.get(fplayer) != null
															&& performing2.get(fplayer) == true) {
														time.put(fplayer, time.get(fplayer) - delay.get(fplayer));
													}
												}
												if (time.get(fplayer) != null && time.get(fplayer) == 0) {
													if (performing2.get(fplayer) != null
															&& performing2.get(fplayer) == true) {
														fplayer.performCommand(perfcmd.get(fplayer));
														fplayer.sendMessage(Api.color(prefix + cmdsent
																.replace("%command%", "'" + e.getMessage() + "'")));
														performing2.put(fplayer, false);
														perfcmd.remove(fplayer);
														maincmd.remove(fplayer);
														delay.remove(fplayer);
														time.remove(fplayer);
														Tagged.remove(fplayer);
														Bukkit.getServer().getScheduler()
																.cancelTask(CountDown.get(fplayer));
														CountDown.remove(fplayer);
														Bukkit.getServer().getScheduler()
																.cancelTask(TagChecker.get(fplayer));
														TagChecker.remove(fplayer);
													}
												}
											}
										}, 0, delay.get(player)));
							} else {
								e.setCancelled(true);
								player.sendMessage(Api.color(prefix + tagged));
							}
						} else {
							e.setCancelled(true);
							if (Api.getConfigString("Options.EnableWaitingMessage").equalsIgnoreCase("true")) {
								if (delay.get(player) == 20) {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) / 20 + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								} else if (delay.get(player) == 1) {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								} else {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) / 20 + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								}
							}
						}
					} else {
						maincmd.remove(player);
					}
				} else {
					if (Api.containsString(e.getMessage(), "Commands")) {
						if (performing2.get(player) == false) {
							if (Api.getConfigString("Options.EnableSendingMessage").equalsIgnoreCase("true")) {
								player.sendMessage(
										Api.color(prefix + sending).replace("%command%", "'" + e.getMessage() + "'"));
							}
							performing2.put(player, true);
							perfcmd.put(player, e.getMessage().replace("/", ""));
							if (Api.getConfigString("Options.DelayFormat").equalsIgnoreCase("ticks")) {
								delay.put(player, 20);
								time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
							} else if (Api.getConfigString("Options.DelayFormat").equalsIgnoreCase("seconds")) {
								delay.put(player, 1);
								time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
							} else {
								delay.put(player, 20);
								time.put(player, Api.getConfigInt("Commands." + maincmd.get(player) + ".Time"));
							}
							e.setCancelled(true);
							CountDown.put(player,
									Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
										Player fplayer = e.getPlayer();

										public void run() {
											if (time.get(fplayer) != null && time.get(fplayer) >= 1) {
												if (performing2.get(fplayer) != null
														&& performing2.get(fplayer) == true) {
													time.put(fplayer, time.get(fplayer) - delay.get(fplayer));
												}
											}
											if (time.get(fplayer) != null && time.get(fplayer) == 0) {
												if (performing2.get(fplayer) != null
														&& performing2.get(fplayer) == true) {
													fplayer.performCommand(perfcmd.get(fplayer));
													fplayer.sendMessage(Api.color(prefix + cmdsent.replace("%command%",
															"'" + e.getMessage() + "'")));
													performing2.put(fplayer, false);
													perfcmd.remove(fplayer);
													maincmd.remove(fplayer);
													delay.remove(fplayer);
													time.remove(fplayer);
													Tagged.remove(fplayer);
													Bukkit.getServer().getScheduler()
															.cancelTask(CountDown.get(fplayer));
													CountDown.remove(fplayer);
													Bukkit.getServer().getScheduler()
															.cancelTask(TagChecker.get(fplayer));
													TagChecker.remove(fplayer);
												}
											}
										}
									}, 0, delay.get(player)));
						} else {
							e.setCancelled(true);
							if (Api.getConfigString("Options.EnableWaitingMessage").equalsIgnoreCase("true")) {
								if (delay.get(player) == 20) {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) / 20 + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								} else if (delay.get(player) == 1) {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								} else {
									player.sendMessage(
											Api.color(prefix + waiting.replace("%time%", time.get(player) / 20 + "")
													.replace("%command%", "'" + e.getMessage() + "'")));
								}
							}
						}
					} else {
						maincmd.remove(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (!player.hasPermission("DelayCommand.Bypass") || !player.isOp()) {
			if (performing2.get(player) != null && performing2.get(player) == true) {
				player.sendMessage(Api.color(prefix + moved));
				performing2.put(player, false);
				perfcmd.remove(player);
				maincmd.remove(player);
				Tagged.remove(player);
				Bukkit.getServer().getScheduler().cancelTask(CountDown.get(player));
				CountDown.remove(player);
				delay.remove(player);
				time.remove(player);
				Bukkit.getServer().getScheduler().cancelTask(TagChecker.get(player));
				TagChecker.remove(player);
			}
		}
	}
}
