package model;

public class Triangle extends Polygon {
    public Triangle(Point point1, Point point2) {
        addPoint(point1);
        addPoint(point2);
        addPoint(new Point((point1.x + point2.x) , point2.y));
    }
}

