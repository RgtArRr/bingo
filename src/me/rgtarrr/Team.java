package me.rgtarrr;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Team {
    public String name;
    public String position;
    public ChatColor color;
    public Location spawn;
    public ArrayList<UUID> players = new ArrayList<>();
    public boolean[] points = new boolean[25];

    public Team(ChatColor color, String position, String name) {
        this.color = color;
        this.position = position;
        this.name = name;
        Arrays.fill(this.points, false);
    }

    public boolean isBingo() {
        for (boolean b : points) {
            if (!b) {
                return false;
            }
        }
        return true;
    }
}
