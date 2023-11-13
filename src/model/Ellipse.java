package model;

public class Ellipse extends Polygon {
    public Ellipse(Point center, int radiusX, int radiusY, int segments) {
        double angleIncrement = 2 * Math.PI / segments;

        for (int i = 0; i < segments; i++) {
            double angle = i * angleIncrement;
            int x = (int) (center.getX() + radiusX * Math.cos(angle));
            int y = (int) (center.getY() + radiusY * Math.sin(angle));
            addPoint(new Point(x, y));
        }
    }
}