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
import java.util.ArrayList;

public class Controller2D implements Controller {
    private cutShape cutShapeMode = cutShape.CUTRECTANGLE;
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
    private Polygon polygonToCut;
    private Rectangle rectangle1;
    private Triangle triangle1;
    private LineClip lineClip;
    private ScanLine scanLine;
    private Polygon clippedPolygon;
    private ScanLine scanLineCut;


    private enum Mode {
        POLYGON, LINE, ELLIPSE, RECTANGLE, TRIANGLE, SCANLINE, SEEDFILL, CUT
    }

    private enum cutShape {
        CUTRECTANGLE, CUTTRIANGLE
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
        polygonToCut = new Polygon();
        clippedPolygon = new Polygon();
        lineClip = new LineClip();
    }

    @Override
    public void initListeners(Panel panel) {

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    switch (currentMode) {
                        case POLYGON -> {
                            previewLine(e.getX(), e.getY(), polygon);
                            if (polygon.size() == 0) {
                                polygon.addPoint(new Point(e.getX(), e.getY()));
                            }
                            polygonRasterizer.rasterize(polygon, Color.CYAN);
                            if (polygon.size() == 2)
                                rasterizer.drawLine(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(),
                                        polygon.getPoint(1).getX(), polygon.getPoint(1).getY(), Color.CYAN);
                            panel.repaint();
                        }
                        case CUT -> {
                            previewLine(e.getX(), e.getY(), polygonToCut);
                            if (polygonToCut.size() == 0) {
                                polygonToCut.addPoint(new Point(e.getX(), e.getY()));
                            }
                            polygonRasterizer.rasterize(polygonToCut, Color.CYAN);
                            if (scanLineCut != null)
                                scanLineCut.fill();
                            panel.repaint();
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (Objects.requireNonNull(currentModeFill) == Mode.SEEDFILL) {
                        SeedFillBorder seedFill = new SeedFillBorder(panel.getRaster(), Color.BLACK.getRGB(),
                                Color.CYAN.getRGB(), Color.YELLOW, e.getX(), e.getY());
                        seedFill.fill();
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
                                Point center = new Point((lineStartPoint.getX() + e.getX()) / 2,
                                        (lineStartPoint.getY() + e.getY()) / 2);
                                int radiusX, radiusY;
                                int diameter = Math.min(Math.abs(lineStartPoint.getX() - e.getX()),
                                        Math.abs(lineStartPoint.getY() - e.getY()));
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
                            update();
                            previewLine(e.getX(), e.getY(), polygon);
                            if (polygon.size() == 2)
                                rasterizer.drawLine(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(),
                                        polygon.getPoint(1).getX(), polygon.getPoint(1).getY(), Color.CYAN);
                            polygonRasterizer.rasterize(polygon, Color.CYAN);
                        }
                        case CUT -> {
                            update();
                            previewLine(e.getX(), e.getY(), polygonToCut);
                            if (polygonToCut.size() == 2)
                                rasterizer.drawLine(polygonToCut.getPoint(0).getX(), polygonToCut.getPoint(0).getY(),
                                        polygonToCut.getPoint(1).getX(), polygonToCut.getPoint(1).getY(), Color.CYAN);
                            polygonRasterizer.rasterize(polygonToCut, Color.CYAN);
                            if (scanLineCut != null)
                                scanLineCut.fill();
                            panel.repaint();
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
                                Point center = new Point((lineStartPoint.getX() + e.getX()) / 2,
                                        (lineStartPoint.getY() + e.getY()) / 2);
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
                }
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lineStartPoint = null;
                    switch (currentMode) {
                        case POLYGON -> {
                            Point p1 = new Point(e.getX(), e.getY());
                            polygon.addPoint(p1);
                            previewLine(e.getX(), e.getY(), polygon);
                            polygonRasterizer.rasterize(polygon, Color.CYAN);
                        }
                        case CUT -> {
                            update();
                            Point p1 = new Point(e.getX(), e.getY());
                            polygonToCut.addPoint(p1);
                            previewLine(e.getX(), e.getY(), polygonToCut);
                            polygonRasterizer.rasterize(polygonToCut, Color.CYAN);
                            switch (cutShapeMode) {
                                case CUTRECTANGLE -> {
                                    ArrayList<Point> clippedPolygonPoints =
                                            lineClip.clipPoints(polygonToCut.getPoints(), rectangle1.getPoints());
                                    clippedPolygon = new Polygon(clippedPolygonPoints);
                                }
                                case CUTTRIANGLE -> {
                                    ArrayList<Point> clippedPolygonPoints =
                                            lineClip.clipPoints(polygonToCut.getPoints(), triangle1.getPoints());
                                    clippedPolygon = new Polygon(clippedPolygonPoints);
                                }
                            }
                            scanLineCut = new ScanLine(panel.getRaster(), clippedPolygon, Color.CYAN);
                            scanLineCut.fill();
                            panel.repaint();
                        }
                    }
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear();
                    panel.repaint();
                    polygon = new Polygon();
                    polygonToCut = new Polygon();
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
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    currentMode = Mode.CUT;
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    switch (cutShapeMode) {
                        case CUTRECTANGLE -> cutShapeMode = cutShape.CUTTRIANGLE;
                        case CUTTRIANGLE -> cutShapeMode = cutShape.CUTRECTANGLE;
                    }
                    polygonToCut = new Polygon();
                    clippedPolygon = new Polygon();
                    scanLineCut = new ScanLine(panel.getRaster(), clippedPolygon, Color.CYAN);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    currentModeFill = Mode.SCANLINE;
                    scanLine = null;
                    switch (currentMode) {
                        case POLYGON -> scanLine = new ScanLine(panel.getRaster(), polygon, Color.decode("#7E77AB"));
                        case ELLIPSE -> scanLine = new ScanLine(panel.getRaster(), ellipse, Color.decode("#7E77AB"));
                        case TRIANGLE -> scanLine = new ScanLine(panel.getRaster(), triangle, Color.decode("#6DA800"));
                        case RECTANGLE ->
                                scanLine = new ScanLine(panel.getRaster(), rectangle, Color.decode("#005C4E"));
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

    public void drawStaticShapes() {
        if (Objects.requireNonNull(currentMode) == Mode.CUT) {
            switch (cutShapeMode) {
                case CUTRECTANGLE -> {
                    rectangle1 = new Rectangle(new Point(50, 50), new Point(400, 400));
                    polygonRasterizer.rasterize(rectangle1, Color.BLUE);
                }
                case CUTTRIANGLE -> {
                    triangle1 = new Triangle(new Point(200, 100), new Point(200, 400));
                    polygonRasterizer.rasterize(triangle1, Color.BLUE);
                }
            }
        }
    }

    private void previewLine(int x, int y, Polygon polygon) {
        update();
        int size;
        if (polygon.size() > 0) {
            size = polygon.size();
            previewLineRasterizer.rasterize(polygon.getPoint(size - 1).getX(), polygon.getPoint(size - 1).getY(), x,
                    y, Color.PINK);
            previewLineRasterizer.rasterize(polygon.getPoint(0).getX(), polygon.getPoint(0).getY(), x, y, Color.PINK);
        }
    }

    private void update() {
        if (Objects.requireNonNull(currentMode) == Mode.CUT) {
            panel.clear();
            drawStaticShapes();
            return;
        }
        panel.clear();
    }
}
