package emanondev.itemtag.compability;

import emanondev.itemtag.ItemTag;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import static emanondev.itemedit.utility.VersionUtils.hasFoliaAPI;

public abstract class SchedulerUtils {

    /**
     * Schedules a task to run later on the main server thread.
     * @param loc The location where the task should run, or null for the main thread.
     * @param task   The task to run.
     * @param delay  The delay in ticks before the task runs.
     */
    public static void runTaskLater(@Nullable Location loc, @NotNull Runnable task, long delay) {
        JavaPlugin plugin = ItemTag.get();
        if (hasFoliaAPI()) {
            try {
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    regionScheduler.runDelayed(
                            plugin,
                            loc,
                            (ScheduledTask scheduledTask) -> task.run(),
                            delay
                    );
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    globalScheduler.runDelayed(
                            plugin,
                            (ScheduledTask scheduledTask) -> task.run(),
                            delay
                    );
                }
                return;
            } catch (Exception ignored) {
            }
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * Schedules a task to run repeatedly on the main server thread.
     * @param loc The location where the task should run, or null for the main thread.
     * @param runnable   The BukkitRunnable to run.
     * @param delay  The delay in ticks before the task runs.
     * @param period The period in ticks between subsequent runs of the task.
     */
    public static void runTaskTimer(@Nullable Location loc, @NotNull FoliaRunnable runnable, long delay, long period) {
        JavaPlugin plugin = ItemTag.get();
        if (hasFoliaAPI()) {
            try {
                ScheduledTask task;
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    task = regionScheduler.runAtFixedRate(
                            plugin,
                            loc,
                            (ScheduledTask t) -> runnable.run(),
                            delay,
                            period
                    );
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    task = globalScheduler.runAtFixedRate(
                            plugin,
                            (ScheduledTask t) -> runnable.run(),
                            delay,
                            period
                    );
                }
                runnable.setScheduledTask(task);
            } catch (Exception e) {
            }
            return;
        }
        runnable.runTaskTimer(plugin, delay, period);
    }

    public static void runTaskTimerAsynchronously(@NotNull FoliaRunnable runnable, long delay, long period) {
        JavaPlugin plugin = ItemTag.get();
        if (hasFoliaAPI()) {
            try {
                Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                class AsyncRepeatingTask {
                    private ScheduledTask task;
                    void start(long initialDelay) {
                        task = globalScheduler.runDelayed(plugin, (ScheduledTask t) -> {
                            runnable.run();
                            start(period);
                        }, initialDelay);
                        runnable.setScheduledTask(task);
                    }
                }
                new AsyncRepeatingTask().start(delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        runnable.runTaskTimerAsynchronously(plugin, delay, period);
    }

    public static void runTaskAsynchronously(@NotNull Runnable task) {
        JavaPlugin plugin = ItemTag.get();
        if (hasFoliaAPI()) {
            try {
                Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                globalScheduler.execute(plugin, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static void runTask(@Nullable Location loc, @NotNull Runnable task) {
        JavaPlugin plugin = ItemTag.get();
        if (hasFoliaAPI()) {
            try {
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());

                    regionScheduler.execute(plugin, loc, task);
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());

                    globalScheduler.execute(plugin, task);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTask(plugin, task);
    }
}
