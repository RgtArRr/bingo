package me.rgtarrr;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main extends JavaPlugin {
    private static Main instance;
    public GameManager manager = new GameManager();
    public String[] items = {};

    @Override
    public void onEnable() {
        instance = this;
        instance.getServer().getPluginManager().registerEvents(new ListenerHandler(), this);
        instance.getCommand("bingo").setExecutor(new Command());
        instance.getCommand("gametype").setExecutor(new Command());
        instance.getCommand("loadworld").setExecutor(new Command());
        instance.getCommand("randombingo").setExecutor(new Command());
        instance.getCommand("setup").setExecutor(new Command());
        instance.getCommand("start").setExecutor(new Command());

        manager.teams.add(new Team(ChatColor.RED, "TL", "Rojo"));
        manager.teams.add(new Team(ChatColor.BLUE, "TR", "Azul"));
        manager.teams.add(new Team(ChatColor.GREEN, "BL", "Verde"));
        manager.teams.add(new Team(ChatColor.YELLOW, "BR", "Amarillo"));

        readItems();
        instance.getManager().randomBingo();
    }

    @Override
    public void onDisable() {
    }

    public static Main getInstance() {
        return instance;
    }

    public GameManager getManager() {
        return manager;
    }

    public void pasteLobby(World w) {
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(w);
        try {
            File schematic = new File(Main.getInstance().getDataFolder().getAbsolutePath() + File.separator + "lobbybingo.schem");
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            if (format == null) return;
            ClipboardReader reader = format.getReader(new FileInputStream(schematic));
            Clipboard clipboard = reader.read();
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

            Method setTickingWatchdog = editSession.getClass().getMethod("setTickingWatchdog", boolean.class);
            setTickingWatchdog.invoke(editSession, true);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(0, 220, 0))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
            editSession.flushSession();
        } catch (WorldEditException | IOException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void readItems() {
        try {
            CodeSource src = instance.getClass().getProtectionDomain().getCodeSource();
            List<String> list = new ArrayList<>();

            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze = null;

                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if (entryName.endsWith(".png")) {
                        list.add(entryName.substring(7).replaceFirst("[.][^.]+$", ""));
                    }
                }
            }
            items = list.toArray(new String[list.size()]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
