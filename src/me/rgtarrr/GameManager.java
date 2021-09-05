package me.rgtarrr;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitScheduler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameManager {
    private GameState state = GameState.LOBBY;
    public GameType gametype = GameType.BINGO;
    public int runnable;
    public ArrayList<Team> teams = new ArrayList<>();

    public ArrayList<Material> objetives = new ArrayList<>();
    public String[] objetives_str = new String[25];
    public BufferedImage[] objetives_resource = new BufferedImage[25];

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void addPlayer(Player p, Team t) {
        Team lastT = getTeamByPlayer(p);
        if (lastT != null) {
            lastT.players.remove(p.getUniqueId());
        }
        t.players.add(p.getUniqueId());
        p.setPlayerListName(t.color + p.getName());
        p.sendMessage(ChatColor.AQUA + "Te has unido al Team " + t.color + t.name);
    }

    public void toLobby(Player p) {
        p.getInventory().clear();
        ItemStack it = new ItemStack(Material.FILLED_MAP);
        MapMeta mm = (MapMeta) it.getItemMeta();
        mm.setMapView(ScoreMap.getMap());
        it.setItemMeta(mm);

        p.getInventory().addItem(it);
        p.setGameMode(GameMode.ADVENTURE);
        Location loc = p.getLocation().set(0, 221, 0);
        loc.setPitch(0);
        loc.setYaw(180);
        p.teleport(loc);
    }

    public Team getTeamByChatColor(ChatColor color) {
        for (Team t : teams) {
            if (t.color.equals(color)) {
                return t;
            }
        }
        return null;
    }

    public Team getTeamByPlayer(Player p) {
        for (Team t : teams) {
            if (t.players.contains(p.getUniqueId())) {
                return t;
            }
        }
        return null;
    }

    public void randomBingo() {
        Random r = new Random();
        String[] items = Main.getInstance().items;
        int length = items.length;
        objetives.clear();
        int i = 0;
        while (objetives.size() < 25) {
            int index = r.nextInt(length);
            Material m = Material.matchMaterial(items[index]);
            if (m != null && !objetives.contains(m)) {
                objetives.add(m);
                objetives_str[i] = items[index];
                try {
                    objetives_resource[i] = resize(ImageIO.read(Main.getInstance().getResource("assets/" + items[index] + ".png")), 20);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }

    private BufferedImage resize(BufferedImage src, int targetSize) {
        if (targetSize <= 0) {
            return src; //this can't be resized
        }
        int targetWidth = targetSize;
        int targetHeight = targetSize;
        float ratio = ((float) src.getHeight() / (float) src.getWidth());
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = (int) Math.ceil((float) targetWidth * ratio);
        } else { //portrait image
            targetWidth = Math.round((float) targetHeight / ratio);
        }
        BufferedImage bi = new BufferedImage(targetWidth, targetHeight, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return bi;
    }

    public void starGame() {
        BukkitScheduler scheduler = Main.getInstance().getServer().getScheduler();
        runnable = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            int cont = 10;

            @Override
            public void run() {
                Main.getInstance().getServer().broadcastMessage(ChatColor.AQUA + "La partida empieza en " + ChatColor.RED + cont);
                cont--;
                if (cont == 0) {
                    int mapSize = 1500;
                    World w = Main.getInstance().getServer().getWorlds().get(0);
                    w.setDifficulty(Difficulty.HARD);
                    w.setTime(0);
                    w.setClearWeatherDuration(99999);
                    Random r = new Random();
                    for (Team t : teams) {
                        int x = r.nextInt(mapSize) - mapSize;
                        int z = r.nextInt(mapSize) - mapSize;
                        Location random = new Location(w, x, w.getHighestBlockYAt(x, z), z);
                        t.spawn = random;
                        for (UUID uid : t.players) {
                            Player p = Main.getInstance().getServer().getPlayer(uid);
                            if (p != null) {
                                p.teleport(random);
                                p.setGameMode(GameMode.SURVIVAL);
                                p.setFoodLevel(20);
                                p.setSaturation(20);
                                p.setLevel(0);
                                p.getInventory().setHelmet(new ItemStack(Material.AIR));
                            }
                        }
                    }
                    for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                        if (Main.getInstance().getManager().getTeamByPlayer(p) == null) {
                            p.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    Main.getInstance().getManager().setState(GameState.STARTED);
                    scheduler.cancelTask(Main.getInstance().getManager().runnable);
                }
            }
        }, 0L, 20L);
        scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            Main instance = Main.getInstance();
            if (instance.getManager().getState().equals(GameState.STARTED)) {
                for (Team t : instance.getManager().teams) {
                    for (UUID uid : t.players) {
                        Player p = instance.getServer().getPlayer(uid);
                        if (p != null) {
                            for (ItemStack it : p.getInventory().getContents()) {
                                if (it != null) {
                                    int pivot = 0;
                                    for (Material mat : objetives) {
                                        if (it.getType().equals(mat)) {
                                            if (!t.points[pivot]) {
                                                t.points[pivot] = true;
                                                instance.getServer().broadcastMessage(ChatColor.AQUA + "El Team " + t.color + t.name + ChatColor.AQUA + " ha conseguido un punto.");
                                            }
                                        }
                                        pivot++;
                                    }
                                }
                            }
                        }
                    }
                    //0  1  2  3  4
                    //5  6  7  8  9
                    //10 11 12 13 14
                    //15 16 17 18 19
                    //20 21 22 23 24
                    if ((gametype.equals(GameType.BINGO) && t.isBingo()) ||
                            (gametype.equals(GameType.SINGLE) && t.points[0] && t.points[1] && t.points[2] && t.points[3] && t.points[4]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[5] && t.points[6] && t.points[7] && t.points[8] && t.points[9]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[10] && t.points[11] && t.points[12] && t.points[13] && t.points[14]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[15] && t.points[16] && t.points[17] && t.points[18] && t.points[19]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[20] && t.points[21] && t.points[22] && t.points[23] && t.points[24]) ||

                            (gametype.equals(GameType.SINGLE) && t.points[0] && t.points[5] && t.points[10] && t.points[15] && t.points[20]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[1] && t.points[6] && t.points[11] && t.points[16] && t.points[21]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[2] && t.points[7] && t.points[12] && t.points[17] && t.points[22]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[3] && t.points[8] && t.points[13] && t.points[18] && t.points[23]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[4] && t.points[9] && t.points[14] && t.points[19] && t.points[24]) ||

                            (gametype.equals(GameType.SINGLE) && t.points[0] && t.points[6] && t.points[12] && t.points[18] && t.points[24]) ||
                            (gametype.equals(GameType.SINGLE) && t.points[4] && t.points[8] && t.points[12] && t.points[16] && t.points[20])
                    ) {
                        instance.getServer().broadcastMessage(ChatColor.DARK_AQUA + "El team " + t.color + t.name + ChatColor.DARK_AQUA + " ha ganado");
                        instance.getManager().setState(GameState.ENDED);
                        for (Player p : instance.getServer().getOnlinePlayers()) {
                            p.setGameMode(GameMode.CREATIVE);
                            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
                        }
                    }
                }
            }
        }, 20L * 10, 40L);
    }
}
