package me.rgtarrr;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class ScoreMap extends MapRenderer {
    private static MapView mv;
    private int cont = 0;
    private String[] last = {};

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (cont == 0) {
            int width = 26;
            int pivot = 0;
            int x = 0;
            int y = 0;
            for (BufferedImage image : Main.getInstance().getManager().objetives_resource) {
                mapCanvas.drawImage(x + 2, y + 2, image);
                for (Team t : Main.getInstance().getManager().teams) {
                    if (t.points[pivot]) {
                        drawScore(mapCanvas, x, y, t.position);
                    }
                }
                pivot++;
                x += width;
                if (x == (width * 5)) {
                    x = 0;
                    y += width;
                }
            }
        }
        cont++;
        if (cont > 20) {
            cont = 0;
        }

    }

    public void drawScore(MapCanvas mapCanvas, int x, int y, String position) {
        int size = 20;
        if (position.equals("TL")) {
            drawRectangle(mapCanvas, x, x + 8, y, y + 2, MapPalette.RED);
            drawRectangle(mapCanvas, x, x + 2, y, y + 8, MapPalette.RED);
        }
        if (position.equals("TR")) {
            drawRectangle(mapCanvas, x + size - 4, x + size + 2, y, y + 2, MapPalette.BLUE);
            drawRectangle(mapCanvas, x + size + 2, x + size + 4, y, y + 8, MapPalette.BLUE);
        }
        if (position.equals("BL")) {
            drawRectangle(mapCanvas, x, x + 2, y + size - 5, y + size + 3, MapPalette.LIGHT_GREEN);
            drawRectangle(mapCanvas, x, x + 8, y + size + 1, y + size + 3, MapPalette.LIGHT_GREEN);
        }
        if (position.equals("BR")) {
            drawRectangle(mapCanvas, x + size - 4, x + size + 2, y + size + 1, y + size + 3, MapPalette.matchColor(255, 255, 0));
            drawRectangle(mapCanvas, x + size + 2, x + size + 4, y + size - 5, y + size + 3, MapPalette.matchColor(255, 255, 0));
        }
    }

    private void drawRectangle(MapCanvas mapCanvas, int p1, int p2, int p3, int p4, byte color) {
        for (int l = p1; l < p2; l++) {
            for (int h = p3; h < p4; h++) {
                mapCanvas.setPixel(l, h, color);
            }
        }
    }

    public static void generate(World w) {
        mv = Main.getInstance().getServer().createMap(w);
        for (MapRenderer mr : mv.getRenderers()) {
            mv.removeRenderer(mr);
        }
        mv.addRenderer(new ScoreMap());
    }

    public static MapView getMap() {
        return mv;
    }
}
