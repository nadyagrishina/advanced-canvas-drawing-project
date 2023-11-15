package model;

public class Edge {
    private int x1, y1, x2, y2;
    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    public boolean isHorizontal(){
        return y1 == y2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public void orientate() {
        if (y1 > y2 || (y1 == y2 && x1 > x2)) {
            int tempX = x1;
            int tempY = y1;
            x1 = x2;
            y1 = y2;
            x2 = tempX;
            y2 = tempY;
        }
    }
    public Point intersection(Point p0, Point p1) {
        double denominator = (p0.getX() - p1.getX()) * (this.y1 - this.y2) - (p0.getY() - p1.getY()) * (this.x1 - this.x2);
        double x = ((p0.getX() * p1.getY() - p0.getY() * p1.getX()) * (this.x1 - this.x2) - (this.x1 * this.y2 - this.y1 * this.x2) * (p0.getX() - p1.getX())) / denominator;
        double y = ((p0.getX() * p1.getY() - p0.getY() * p1.getX()) * (this.y1 - this.y2) - (this.x1 * this.y2 - this.y1 * this.x2) * (p0.getY() - p1.getY())) / denominator;
        return new Point(x, y);
    }
    public boolean isInside(Point point) {
        return (this.y2 - this.y1) * point.getX() - (this.x2 - this.x1) * point.getY() + this.x2 * this.y1 - this.y2 * this.x1 > 0.0;
    }
}

