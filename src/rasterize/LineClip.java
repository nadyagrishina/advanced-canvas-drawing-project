package rasterize;

import model.Edge;
import model.Point;

import java.util.ArrayList;
import java.util.Iterator;

public class LineClip {
    public static ArrayList<Point> clipEdge(ArrayList<Point> points, Edge clipEdge) {
        if (points.size() < 2) {
            return points;
        } else {
            ArrayList<Point> result = new ArrayList<>();
            Point current = points.get(points.size() - 1);
            Point next;
            for (Iterator<Point> i = points.iterator(); i.hasNext(); current = next) {
                next = i.next();
                if (clipEdge.isInside(next)) {
                    if (!clipEdge.isInside(current)) {
                        result.add(clipEdge.intersection(current, next));
                    }
                    result.add(next);
                } else if (clipEdge.isInside(current)) {
                    result.add(clipEdge.intersection(current, next));
                }
            }
            return result;
        }
    }
    public ArrayList<Point> clipPoints(ArrayList<Point> points, ArrayList<Point> clipPoints) {
        if (clipPoints.size() < 2) {
            return points;
        } else {
            ArrayList<Point> result = points;
            Point current = clipPoints.get(clipPoints.size() - 1);

            Point next;
            for(Iterator<Point> i = clipPoints.iterator(); i.hasNext(); current = next) {
                next = i.next();
                result = clipEdge(points, new Edge(current.getX(), current.getY(), next.getX(), next.getY()));
                points = result;
            }
            return result;
        }
    }
}

