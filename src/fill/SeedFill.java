package fill;

import rasterize.Raster;

import java.awt.*;
import java.util.Stack;

public class SeedFill implements Filler {
    private final Raster raster;
    private final int x;
    private final int y;
    private final int backgroundColor;
    private final Color fillColor;

    public SeedFill(Raster raster, int backgroundColor, Color fillColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.x = x;
        this.y = y;
    }

    @Override
    public void fill() {
        seedFill(x, y, fillColor);
    }

    private void seedFill(int startX, int startY, Color fillColor) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(startX, startY));

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            int x = current.x;
            int y = current.y;
            int pixelColor = raster.getPixel(x, y);

            if (pixelColor == backgroundColor) {
                raster.setPixel(x, y, fillColor.getRGB());

                stack.push(new Point(x + 1, y));
                stack.push(new Point(x - 1, y));
                stack.push(new Point(x, y + 1));
                stack.push(new Point(x, y - 1));
                stack.push(new Point(x + 1, y + 1));
                stack.push(new Point(x - 1, y - 1));
                stack.push(new Point(x + 1, y - 1));
                stack.push(new Point(x - 1, y + 1));
            }
        }
    }
}


