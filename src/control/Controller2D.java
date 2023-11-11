package control;

import model.Point;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;

public class Controller2D implements Controller {
    private Point lineStartPoint;
    private final Panel panel;

    private int x, y;
    private LineRasterizerGraphics rasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
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
                    //TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
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
                        rasterizer.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    update();
                    if (lineStartPoint == null) {
                        lineStartPoint = new Point(e.getX(), e.getY());
                    }
                    // Počáteční bod
                    Point p1 = lineStartPoint;
                    // Koncový bod určený polohou myši
                    Point p2 = new Point(e.getX(), e.getY());

                    rasterizer.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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
                lineStartPoint = null;
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear();
                    panel.repaint();
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

    private void update() {
        panel.clear();
    }

    private void hardClear() {
        panel.clear();
    }
}
