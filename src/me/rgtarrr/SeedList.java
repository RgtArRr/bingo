package me.rgtarrr;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SeedList {
    ArrayList<Long> seeds = new ArrayList<>();

    public void load() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Main.getInstance().getResource("me/rgtarrr/seeds.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() != 0) {
                    seeds.add(Long.valueOf(line));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loaded " + seeds.size() + " seeds");
    }

    public long getRandomSeed() {
        return seeds.get(new Random().nextInt(seeds.size()));
    }
}