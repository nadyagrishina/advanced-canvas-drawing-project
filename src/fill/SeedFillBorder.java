package fill;

import rasterize.Raster;

import java.awt.*;
import java.util.Stack;

public class SeedFillBorder implements Filler {
    private final Raster raster;
    private final int x;
    private final int y;
    private final int backgroundColor;
    private final int borderColor;
    private final Color fillColor;

    public SeedFillBorder(Raster raster, int backgroundColor, int borderColor, Color fillColor, int x, int y) {
        this.raster = raster;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
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

            if (isWithinBounds(x, y)) {
                int pixelColor = raster.getPixel(x, y);

                if (pixelColor == backgroundColor && pixelColor != borderColor) {
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

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight();
    }
}


