package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.List;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;
    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }
    public void rasterize(Polygon polygon) {
        if (polygon.size() < 3)
            return;

        for (int i = 0; i < polygon.size(); i++) {
            int indexA = i;
            int indexB = (i + 1) % polygon.size();

            Point pA = polygon.getPoint(indexA);
            Point pB = polygon.getPoint(indexB);

            lineRasterizer.rasterize(new Line(pA, pB, 0xf658b8));
        }
    }
}
