package control;

import fill.ScanLine;
import fill.SeedFillBorder;
import model.*;
import model.Point;
import model.Polygon;
import model.Rectangle;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class Controller2D implements Controller {
    private Mode clipMode = Mode.RECTANGLE;
    private Mode currentMode = Mode.POLYGON;
    private Mode currentModeFill = Mode.SEEDFILL;
    private Point lineStartPoint;
    private final Panel panel;
    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;
    private LineRasterizerGraphics rasterizer;
    private Ellipse ellipse;
    private Rectangle rectangle;
    private Triangle triangle;
    private LineRasterizerGraphics previewLineRasterizer;
    private Polygon temporaryPolygon;

    private enum Mode {
        POLYGON, LINE, ELLIPSE, RECTANGLE, TRIANGLE, SCANLINE, SEEDFILL
    }

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
        polygonRasterizer = new PolygonRasterizer(rasterizer);
        previewLineRasterizer = new LineRasterizerGraphics(raster);
        polygon = new Polygon();
        temporaryPolygon = new Polygon();
    }

    @Override
    public void initListeners(Panel panel) {

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    switch (currentMode) {
                        case POLYGON -> {
                            previewLine(e.getX(), e.getY());
                            if (polygon.size() == 0){
                                polygon.addPoint(new Point(e.getX(), e.getY()));
                            }
                            polygonRasterizer.rasterize(polygon, Color.CYAN);
                            if (polygon.size() == 2)
                                rasterizer.drawLine(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(), polygon.getPoint(1).getX(), polygon.getPoint(1).getY(), Color.CYAN);
                            panel.repaint();
                        }
                    }
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    switch (currentModeFill) {
                        case SEEDFILL -> {
                            SeedFillBorder seedFill = new SeedFillBorder(panel.getRaster(), Color.BLACK.getRGB(), Color.CYAN.getRGB(), Color.YELLOW, e.getX(), e.getY());
                            seedFill.fill();
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        //TODO
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        //TODO
                    }
                }

            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    switch (currentMode) {
                        case LINE -> {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                update();
                                if (lineStartPoint == null) {
                                    lineStartPoint = new Point(e.getX(), e.getY());
                                }
                                // Počáteční bod
                                Point p1 = lineStartPoint;
                                // Koncový bod určený polohou myši
                                Point p2 = new Point(e.getX(), e.getY());
                                int dx = Math.abs(p2.x - p1.x);
                                int dy = Math.abs(p2.y - p1.y);
                                if (dx == dy) {
                                    //Úhlopříčná úsečka
                                    if ((p2.x > p1.x) && (p2.y > p1.y)) {
                                        // První kvadrant
                                        p2.x = p1.x + dx;
                                        p2.y = p1.y + dx;
                                    } else if ((p1.x > p2.x) && (p1.y > p2.y)) {
                                        // Třetí kvadrant
                                        p2.x = p1.x - dx;
                                        p2.y = p1.y - dx;
                                    } else if ((p1.x > p2.x) && (p2.y > p1.y)) {
                                        // Čtvrtý kvadrant
                                        p2.x = p1.x - dx;
                                        p2.y = p1.y + dx;
                                    } else {
                                        // Druhý kvadrant
                                        p2.x = p1.x + dx;
                                        p2.y = p1.y - dx;
                                    }
                                } else if (dx > dy) {
                                    // Vodorovná úsečka
                                    p2.y = p1.y;
                                } else {
                                    // Svislá úsečka
                                    p2.x = p1.x;
                                }
                                rasterizer.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), Color.yellow);
                            }
                        }
                        case RECTANGLE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            }
                            Point p1 = lineStartPoint;
                            Point p3 = new Point(e.getX(), e.getY());

                            int dx = Math.abs(p3.x - p1.x);
                            int dy = Math.abs(p3.y - p1.y);
                            int sideLength = Math.min(dx, dy);
                            p3.x = p1.x + (p3.x > p1.x ? sideLength : -sideLength);
                            p3.y = p1.y + (p3.y > p1.y ? sideLength : -sideLength);

                            Rectangle rectangle = new Rectangle(p1, p3);

                            polygonRasterizer.rasterize(rectangle, Color.green);
                            panel.repaint();
                        }
                        case ELLIPSE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            } else {
                                Point center = new Point((lineStartPoint.getX() + e.getX()) / 2, (lineStartPoint.getY() + e.getY()) / 2);
                                int radiusX, radiusY;
                                int diameter = Math.min(Math.abs(lineStartPoint.getX() - e.getX()), Math.abs(lineStartPoint.getY() - e.getY()));
                                radiusX = diameter / 2;
                                radiusY = diameter / 2;
                                int segments = 360;

                                Ellipse ellipse = new Ellipse(center, radiusX, radiusY, segments);
                                polygonRasterizer.rasterize(ellipse, Color.ORANGE);
                                panel.repaint();
                            }
                        }
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    switch (currentMode) {
                        case LINE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            }
                            // Počáteční bod
                            Point p1 = lineStartPoint;
                            // Koncový bod určený polohou myši
                            Point p2 = new Point(e.getX(), e.getY());
                            rasterizer.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), Color.yellow);
                        }
                        case POLYGON -> {
                            previewLine(e.getX(), e.getY());
                            if (polygon.size() == 2)
                                rasterizer.drawLine(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(), polygon.getPoint(1).getX(), polygon.getPoint(1).getY(), Color.CYAN);
                            polygonRasterizer.rasterize(polygon, Color.CYAN);
                        }
                        case RECTANGLE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            }
                            Point p1 = lineStartPoint;
                            Point p3 = new Point(e.getX(), e.getY());

                            rectangle = new Rectangle(p1, p3);

                            polygonRasterizer.rasterize(rectangle, Color.green);
                            panel.repaint();
                        }
                        case ELLIPSE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            } else {
                                Point center = new Point((lineStartPoint.getX() + e.getX()) / 2, (lineStartPoint.getY() + e.getY()) / 2);
                                int radiusX = Math.abs(lineStartPoint.getX() - e.getX()) / 2;
                                int radiusY = Math.abs(lineStartPoint.getY() - e.getY()) / 2;
                                int segments = 360;

                                ellipse = new Ellipse(center, radiusX, radiusY, segments);
                                polygonRasterizer.rasterize(ellipse, Color.ORANGE);
                                panel.repaint();
                            }
                        }
                        case TRIANGLE -> {
                            update();
                            if (lineStartPoint == null) {
                                lineStartPoint = new Point(e.getX(), e.getY());
                            } else {
                                Point p1 = lineStartPoint;
                                Point p2 = new Point(e.getX(), e.getY());
                                triangle = new Triangle(p1, p2);
                                polygonRasterizer.rasterize(triangle, Color.BLUE);
                                panel.repaint();
                            }
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                }
                //update();
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lineStartPoint = null;
                    if (Objects.requireNonNull(currentMode) == Mode.POLYGON) {
                        Point p1 = new Point(e.getX(), e.getY());
                        polygon.addPoint(p1);
                        previewLine(e.getX(), e.getY());
                        polygonRasterizer.rasterize(polygon, Color.CYAN);
                    }
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear();
                    panel.repaint();
                    polygon = new Polygon();
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    currentMode = Mode.LINE;
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    currentMode = Mode.POLYGON;
                } else if (e.getKeyCode() == KeyEvent.VK_T) {
                    currentMode = Mode.TRIANGLE;
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    currentMode = Mode.RECTANGLE;
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    currentMode = Mode.ELLIPSE;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    currentModeFill = Mode.SCANLINE;
                    ScanLine scanLine = null;
                    switch (currentMode) {
                        case POLYGON -> scanLine = new ScanLine(panel.getRaster(), polygon);
                        case ELLIPSE -> scanLine = new ScanLine(panel.getRaster(), ellipse);
                        case TRIANGLE -> scanLine = new ScanLine(panel.getRaster(), triangle);
                        case RECTANGLE -> scanLine = new ScanLine(panel.getRaster(), rectangle);
                    }
                    if (scanLine != null)
                        scanLine.fill();
                } else if (e.getKeyCode() == KeyEvent.VK_F) {
                    currentModeFill = Mode.SEEDFILL;
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    //    private void drawClipShape()
//    {
//        clipPolygon.clearPoints();
//
//        switch (clipMode)
//        {
//            // rectangle
//            case RECTANGLE:
//                clipPolygon.addPoint(new Point(40, 170));
//                clipPolygon.addPoint(new Point(40, 690));
//                clipPolygon.addPoint(new Point(560, 690));
//                clipPolygon.addPoint(new Point(560, 170));
//                break;
//            // triangle
//            case TRIANGLE:
//                clipPolygon.addPoint(new Point(40, 280));
//                clipPolygon.addPoint(new Point(40, 560));
//                clipPolygon.addPoint(new Point(760, 560));
//                clipPolygon.addPoint(new Point(760, 280));
//                break;
//            //ellipse
//            case ELLIPSE:
//                clipPolygon.addPoint(new Point(40, 690));
//                clipPolygon.addPoint(new Point(560, 690));
//                clipPolygon.addPoint(new Point(300, 170));
//                break;
//        }
//    }
    private void previewLine(int x, int y) {
        update();
        int size;
        if (polygon.size() > 0) {
            size = polygon.size();
            previewLineRasterizer.rasterize(polygon.getPoint(size - 1).getX(), polygon.getPoint(size - 1).getY(), x, y, Color.PINK);
            previewLineRasterizer.rasterize(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(), x, y, Color.PINK);
        }
    }
    private void update() {
        //TODO
        panel.clear();
    }

    private void hardClear() {
        panel.clear();
    }
}
