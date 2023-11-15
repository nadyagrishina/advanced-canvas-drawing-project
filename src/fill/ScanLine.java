package fill;

import model.Edge;
import model.Point;
import model.Polygon;
import rasterize.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ScanLine implements Filler {
    private final Raster raster;
    private final Polygon polygon;
    private final Color color;

    public ScanLine(Raster raster, Polygon polygon, Color color) {
        this.raster = raster;
        this.polygon = polygon;
        this.color = color;
    }

    @Override
    public void fill() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < polygon.size(); i++) {
            Point p1 = polygon.getPoint(i);
            int indexB = i + 1;
            if (indexB == polygon.size()) {
                indexB = 0;
            }
            Point p2 = polygon.getPoint(indexB);
            Edge edge = new Edge(p1.x, p1.y, p2.x, p2.y);
            if (edge.isHorizontal())
                continue;
            edges.add(edge);
        }

        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;
        for (int i = 0; i < polygon.size(); i++) {
            Point p = polygon.getPoint(i);
            if (p.y < yMin)
                yMin = p.y;
            if (p.y > yMax)
                yMax = p.y;
        }

        for (int i = yMin; i <= yMax; i++) {
            ArrayList<Integer> intersections = new ArrayList<>();
            for (Edge edge : edges) {
                int x1 = edge.getX1();
                int y1 = edge.getY1();
                int x2 = edge.getX2();
                int y2 = edge.getY2();

                if ((y1 <= i && i < y2) || (y2 <= i && i < y1)) {
                    int xIntersection = (int) (x1 + (double) (x2 - x1) / (y2 - y1) * (i - y1));
                    intersections.add(xIntersection);
                }
            }

            Collections.sort(intersections);

            for (int j = 0; j < intersections.size(); j += 2) {
                int start = intersections.get(j);
                int end = intersections.get(j + 1);

                for (int k = start; k <= end; k++) {
                    raster.setPixel(k, i, color.getRGB());
                }
            }
        }
    }
}
