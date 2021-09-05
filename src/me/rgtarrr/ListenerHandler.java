package me.rgtarrr;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class ListenerHandler implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent e) {
        if (!Main.getInstance().getManager().getState().equals(GameState.LOBBY)) {
            if (Main.getInstance().getManager().getTeamByPlayer(e.getPlayer()) == null && !e.getPlayer().isOp()) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Partida en curso!");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        GameManager gm = Main.getInstance().getManager();
        Player p = e.getPlayer();
        if (gm.getState().equals(GameState.LOBBY)) {
            gm.toLobby(p);
        } else {
            Team t = gm.getTeamByPlayer(p);
            if (t != null) {
                p.setPlayerListName(t.color + p.getName());
            } else {
                gm.toLobby(p);
            }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (Main.getInstance().getManager().getState().equals(GameState.LOBBY)) {
            Team t = Main.getInstance().getManager().getTeamByPlayer(e.getPlayer());
            if (t != null) {
                t.players.remove(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        for (Iterator<ItemStack> iterator = e.getDrops().iterator(); iterator.hasNext(); ) {
            ItemStack drop = iterator.next();
            if (drop.getType().equals(Material.FILLED_MAP)) {
                iterator.remove();
                e.getItemsToKeep().add(drop);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String GLOBAL = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.GOLD + "" + ChatColor.BOLD + "GLOBAL" + ChatColor.RESET + ChatColor.GRAY + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
        String TEAM = ChatColor.GRAY + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.GREEN + "" + ChatColor.BOLD + "TEAM" + ChatColor.RESET + ChatColor.GRAY + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";

        Team t = Main.getInstance().getManager().getTeamByPlayer(e.getPlayer());
        ChatColor teamcolor = (t != null ? t.color : ChatColor.GRAY);
        if (e.getMessage().startsWith("!") || (Main.getInstance().getManager().getState() == GameState.LOBBY)) {
            e.setFormat(GLOBAL + "" + (t != null ? teamcolor : ChatColor.GRAY) + " %1$s" + ChatColor.WHITE + " > %2$s");
        } else {
            e.getRecipients().clear();
            if (t != null) {
                for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                    if (t.players.contains(p.getUniqueId())) {
                        p.sendMessage(TEAM + "" + teamcolor + e.getPlayer().getName() + ChatColor.WHITE + " > " + e.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        if (!e.isBedSpawn()) {
            Team t = Main.getInstance().getManager().getTeamByPlayer(e.getPlayer());
            if (t != null) {
                e.setRespawnLocation(t.spawn);
            }
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        GameManager gm = Main.getInstance().getManager();
        if (gm.getState().equals(GameState.LOBBY)) {
            if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock() != null) {
                Location loc = e.getClickedBlock().getLocation();
                if (loc.getX() == -6 && loc.getY() == 218 && loc.getZ() == -45) {
                    gm.addPlayer(e.getPlayer(), gm.getTeamByChatColor(ChatColor.YELLOW));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.GOLD_BLOCK));
                }
                if (loc.getX() == -2 && loc.getY() == 218 && loc.getZ() == -46) {
                    gm.addPlayer(e.getPlayer(), gm.getTeamByChatColor(ChatColor.RED));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
                }
                if (loc.getX() == 2 && loc.getY() == 218 && loc.getZ() == -46) {
                    gm.addPlayer(e.getPlayer(), gm.getTeamByChatColor(ChatColor.BLUE));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.LAPIS_BLOCK));
                }
                if (loc.getX() == 6 && loc.getY() == 218 && loc.getZ() == -45) {
                    gm.addPlayer(e.getPlayer(), gm.getTeamByChatColor(ChatColor.GREEN));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.EMERALD_BLOCK));
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (Main.getInstance().getManager().getState().equals(GameState.LOBBY)) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!Main.getInstance().getManager().getState().equals(GameState.STARTED)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!Main.getInstance().getManager().getState().equals(GameState.STARTED)) {
            e.setCancelled(true);
        }
    }
}
