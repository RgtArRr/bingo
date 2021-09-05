package me.rgtarrr;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("bingo")) {
            sender.sendMessage(ChatColor.RED + "=================================");
            int count = 0;
            ArrayList<String> items = new ArrayList<>();
            for (String objetive : Main.getInstance().getManager().objetives_str) {
                items.add(String.join(" ", objetive.split("_")));
                if (items.size() == 5) {
                    sender.sendMessage(ChatColor.DARK_GREEN + String.join(" | ", items));
                    sender.sendMessage("--------------------------");
                    items.clear();
                }
            }
            sender.sendMessage(ChatColor.RED + "=================================");
            return true;
        }
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Sorry, solo OP :p");
            return false;
        }
        if (command.getName().equalsIgnoreCase("randombingo")) {
            Main.getInstance().getManager().randomBingo();
            sender.sendMessage(ChatColor.GREEN + "Picking an another map");
        }
        if (command.getName().equalsIgnoreCase("setup")) {
            World w = Main.getInstance().getServer().getWorlds().get(0);
            w.setPVP(false);
            w.setDifficulty(Difficulty.PEACEFUL);
            w.getWorldBorder().setCenter(0, 0);
            w.getWorldBorder().setSize(3000);
            Main.getInstance().pasteLobby(w);
            ScoreMap.generate(w);
            sender.sendMessage(ChatColor.GREEN + "Setup done");
        }
        if (command.getName().equalsIgnoreCase("gametype")) {
            if (strings.length != 1) {
                return false;
            }
            if (strings[0].equals("single")) {
                Main.getInstance().getManager().gametype = GameType.SINGLE;
                sender.sendMessage(ChatColor.GREEN + "Cambiado a single");
                return true;
            }
            if (strings[0].equals("bingo")) {
                Main.getInstance().getManager().gametype = GameType.BINGO;
                sender.sendMessage(ChatColor.GREEN + "Cambiado a bingo");
                return true;
            }
            return false;
        }
        if (command.getName().equalsIgnoreCase("loadworld")) {
            SeedList seeds = new SeedList();
            seeds.load();
            long seed = seeds.getRandomSeed();
            System.out.println(seed);
            WorldCreator wc = new WorldCreator("world" + String.valueOf(seed));
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.NORMAL);
            wc.seed(seed);
            wc.createWorld();
            System.out.println("world" + seed + " successful");
        }
        if (command.getName().equalsIgnoreCase("start")) {
            sender.sendMessage(ChatColor.GREEN + "Starting the game");
            Main.getInstance().getManager().starGame();
        }
        return true;
    }
}
