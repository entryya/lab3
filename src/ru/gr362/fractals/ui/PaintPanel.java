package ru.gr362.fractals.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class PaintPanel extends JPanel {
    class MouseButtonAndMotionListener extends MouseAdapter {

        public Rect rect = new Rect();

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                super.mousePressed(e);
                g = getGraphics();
                g.setXORMode(Color.WHITE);
                g.fillRect(-10, -10, 1, 1);
                g.setPaintMode();

                rect.clear();
                rect.addPoint(e.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (SwingUtilities.isLeftMouseButton(e) && selectedAction != null){
                selectedAction.accept(rect);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                super.mouseDragged(e);
                if (rect.isValid()) {
                    drawRect(rect);
                }
                rect.addPoint(e.getPoint());
                if (rect.isValid()) {
                    drawRect(rect);
                }
            }
        }
    }

    class PanListener extends MouseAdapter {
        private Point startPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                startPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (startPoint != null && SwingUtilities.isRightMouseButton(e)) {
                Point current = e.getPoint();
                int dx = current.x - startPoint.x;
                int dy = current.y - startPoint.y;
                startPoint = current;
                if (panAction != null) panAction.accept(new Point(dx, dy));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                startPoint = null;
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private Consumer<Graphics> paintAction = null;
    private Consumer<Rect> selectedAction = null;
    private Consumer<Point> panAction = null;
    private MouseButtonAndMotionListener mbl = new MouseButtonAndMotionListener();
    private PanListener panListener = new PanListener();
    private Graphics g = null;
    private Color selectionColor = Color.BLACK;

    public void addSelectListener(Consumer<Rect> l) {
        selectedAction = l;
        addMouseListener(mbl);
        addMouseMotionListener(mbl);
    }

    public void removeSelectListener(Consumer<Rect> l){
        selectedAction = null;
        removeMouseListener(mbl);
        removeMouseMotionListener(mbl);
    }

    public void addPanListener(Consumer<Point> l) {
        this.panAction = l;
        addMouseListener(panListener);
        addMouseMotionListener(panListener);
    }

    public void setPaintAction(Consumer<Graphics> action){
        paintAction = action;
    }

    public void removePaintAction(){
        paintAction = null;
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        if (paintAction != null) paintAction.accept(g);
    }

    private void drawRect(Rect r){
        g.setXORMode(Color.WHITE);
        g.setColor(selectionColor);
        try {
            g.drawRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        } catch (InvalidRectException _){}
        finally {
            g.setPaintMode();
        }
    }
}