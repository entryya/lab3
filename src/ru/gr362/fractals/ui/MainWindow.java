package ru.gr362.fractals.ui;

import ru.gr362.Dialogs;
import ru.gr362.converting.Converter;
import ru.gr362.imgfiles.FileData;
import ru.gr362.imgfiles.NonImageFormatException;
import ru.gr362.math.Complex;
import ru.gr362.math.fractals.Fractal;
import ru.gr362.math.fractals.Mandelbrot;
import ru.gr362.math.fractals.Zhulia;
import ru.gr362.painting.CartesianPainter;
import ru.gr362.painting.FractalPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MainWindow extends JFrame {

    private static final int MIN_SZ = GroupLayout.PREFERRED_SIZE;
    private static final int MAX_SZ = GroupLayout.DEFAULT_SIZE;

    private PaintPanel mainPanel;
    private final Fractal f;

    private MainWindow juliaWindow;

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu fileMenu = new JMenu();
    private final JMenuItem save = new JMenuItem();
    private final JMenuItem open = new JMenuItem();

    private final Converter conv = new Converter(-2.0, 1.0, -1.0, 1.0, 0, 0);
    private final FractalPainter fp;
    private final CartesianPainter cp = new CartesianPainter(conv);

    public MainWindow(Fractal f){
        this.f = f;
        fp = new FractalPainter(conv, this.f);
        Dimension minSz = new Dimension(800, 600);
        setMinimumSize(minSz);
        setTitle(f.getName());
        if (f.getClass() == Mandelbrot.class)
            setDefaultCloseOperation(EXIT_ON_CLOSE);

        initializeComponents();

        save.setText("Сохранить...");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        save.addActionListener(e -> {
            saveFile();
        });

        open.setText("Открыть");
        fileMenu.setText("Файл");
        fileMenu.add(save);
        fileMenu.add(open);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        GroupLayout gl = new GroupLayout(getContentPane());
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, MAX_SZ, MAX_SZ, MAX_SZ)
                .addGap(8)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(mainPanel, MAX_SZ, MAX_SZ, MAX_SZ)
                )
                .addGap(8)
        );
        setLayout(gl);

        pack();
    }

    private void saveFile() {
        var file = Dialogs.showFileDialog(true);
        //JOptionPane.showMessageDialog(this, (file != null) ? file.getAbsolutePath() : "Файл не выбран");
        if (file != null) {
            try {
                FileData.saveAsImage(fp.getImg(), file);
            } catch (NonImageFormatException e) {
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Не удалось сохранить данные в файл");
            }
        }
    }

    private void initializeComponents() {
        mainPanel = new PaintPanel();

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && f.getClass() == Mandelbrot.class){
                    var re = conv.xScr2Crt(e.getX());
                    var im = conv.yScr2Crt(e.getY());
                    var zh = new Zhulia(new Complex(re, im));

                    if (juliaWindow != null) {
                        juliaWindow.dispose();
                        juliaWindow = null;
                    }

                    juliaWindow = new MainWindow(zh);
                    juliaWindow.setVisible(true);
                }
            }
        });
        mainPanel.addSelectListener(r -> {
            try {
                var xMin = conv.xScr2Crt(r.getX());
                var xMax = conv.xScr2Crt(r.getX() + r.getWidth());
                var yMin = conv.yScr2Crt(r.getY() + r.getHeight());
                var yMax = conv.yScr2Crt(r.getY());

                double viewportAspect = (double) mainPanel.getWidth() / mainPanel.getHeight();
                double selectionAspect = (xMax - xMin) / (yMax - yMin);

                // Корректируем границы для сохранения пропорций
                if (selectionAspect > viewportAspect) {
                    // Выделенная область шире - расширяем по Y
                    double centerY = (yMin + yMax) / 2;
                    double requiredHeight = (xMax - xMin) / viewportAspect;
                    yMin = centerY - requiredHeight / 2;
                    yMax = centerY + requiredHeight / 2;
                } else {
                    // Выделенная область уже - расширяем по X
                    double centerX = (xMin + xMax) / 2;
                    double requiredWidth = (yMax - yMin) * viewportAspect;
                    xMin = centerX - requiredWidth / 2;
                    xMax = centerX + requiredWidth / 2;
                }

                conv.setxMin(xMin);
                conv.setxMax(xMax);
                conv.setyMin(yMin);
                conv.setyMax(yMax);

                mainPanel.repaint();
            } catch (InvalidRectException _) { }
        });

        mainPanel.addPanListener(delta -> {
            double dx = conv.xScr2Crt(delta.x) - conv.xScr2Crt(0);
            double dy = conv.yScr2Crt(delta.y) - conv.yScr2Crt(0);

            conv.setxMin(conv.getxMin() - dx);
            conv.setxMax(conv.getxMax() - dx);
            conv.setyMin(conv.getyMin() - dy);
            conv.setyMax(conv.getyMax() - dy);

            mainPanel.repaint();
        });

        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPaintAction(g -> {
            //cp.paint(g);
            fp.paint(g);
        });
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                conv.setWidth(mainPanel.getWidth());
                conv.setHeight(mainPanel.getHeight());
            }
        });
    }
}