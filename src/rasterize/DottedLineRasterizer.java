package rasterize;
public class DottedLineRasterizer extends LineRasterizer {
    private final int dotSize;

    public DottedLineRasterizer(Raster raster, int dotSize) {
        super(raster);
        this.dotSize = dotSize;
    }

    // Třída pro rasterizaci tečkovaných úseček pomocí Bresenhamova algoritmu

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        // Výpočet délky úsečky v osách x a y
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Směrový koeficient pro osu x
        int sx = (x1 < x2) ? 1 : -1;
        // Směrový koeficient pro osu y
        int sy = (y1 < y2) ? 1 : -1;

        // Počáteční hodnota chyby
        int err = dx - dy;
        // Sčitaní pixelů
        int pixelCounter = 0;

        while (x1 != x2 || y1 != y2) {
            if (pixelCounter % dotSize == 0) {
                // Nastavení pixelu na pozici (x1, y1) na zelenou barvu
                raster.setPixel(x1, y1, 0xf658b8);
            }

            // Dvojnásobná hodnota chyby
            int err2 = 2 * err;

            if (err2 > -dy) {
                err -= dy;
                x1 += sx; // Přesun na další pixel v ose x
            }

            if (err2 < dx) {
                err += dx;
                y1 += sy; // Přesun na další pixel v ose y
            }

            pixelCounter++;
        }

        // Nastavení posledního pixelu (x2, y2) na zelenou barvu
        raster.setPixel(x2, y2, 0xf658b8);
    }
}
