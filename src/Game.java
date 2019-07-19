import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.lang.annotation.Target;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.min;


public class Game extends Frame {
    private final int STEP = 10;
    private final int RADIUS = 10;

    private int WIDTH, HEIGHT, SPEED;

    Random rand;
    TargetCanvas target;
    AimCanvas aim;

    Game(int w, int h) {
        rand = new Random();
        int MAX_WIDTH = 1000;
        WIDTH = min(MAX_WIDTH, w);
        int MAX_HEIGHT = 500;
        HEIGHT = min(MAX_HEIGHT, h);
//        for (int i = 0; i < 1000; i++) {
//            System.out.println(rand.nextInt(2));
//        }

        setTitle("Кабанчик");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(145, 188, 58));
        setLayout(null);
        addKeyListener(new KeysControl());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
                super.windowClosed(e);
            }
        });

        aim = new AimCanvas(WIDTH, HEIGHT);
        target = new TargetCanvas(WIDTH, HEIGHT);

        add(aim);
        add(target);

        setVisible(true);
    }

    private class KeysControl extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
//            System.out.println(e);
            int key = e.getKeyCode();
            if ((key == KeyEvent.VK_A) || (key == KeyEvent.VK_LEFT)) aim.move_left();
            if ((key == KeyEvent.VK_D) || (key == KeyEvent.VK_RIGHT)) aim.move_right();
            if ((key == KeyEvent.VK_SPACE)) shoot();
            super.keyPressed(e);
        }
    }

    private void shoot() {
        int a  = aim.getX();
        int t = target.getX()+25;
        System.out.println(a + " | " + t);
        if (Math.abs(a - t) < 100) {
            if (SPEED > 50) {
                System.out.println("Game over");
                System.exit(0);
            }
            SPEED += 5;
            target.reset();
            System.out.println("shot");
            System.out.println("speed is now " + SPEED);
        }
    }

    class TargetCanvas extends Component {
        private int W, H, tx = 0, ty, dy;
        private boolean dir;

        TargetCanvas(int w, int h) {
            W = w;
            H = h;
            setSize(W, H);
            ty = H/2 - 25;
            tx = 100;
            repaint();
            Thread animationThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        repaint();
                        try {
                            Thread.sleep(100);
                        } catch (Exception ignored) {}
                    }
                }
            });
            animationThread.start();
        }

        @Override
        public void paint(Graphics g) {
//            Graphics2D gg = (Graphics2D) g;
            if ((ty < 0) || (ty > H) || (tx > W) || (tx < 0)) {
                tx = rand.nextInt(2) == 0 ? 0: W;
//                System.out.println("tx"+tx);
                dir = (tx == 0);
                ty = rand.nextInt(H + 1);
                dy = rand.nextInt(10) -5;
            }
            tx += (dir?1:-1)*SPEED;
            ty += dy;

            g.setColor(new Color(200, 128, 32));
            g.fillRect(tx, ty, 50, 50);
            g.setColor(Color.black);
        }

        public int getX() {
            return tx;
        }
        public void reset() {
            ty = 0;
            tx = 0;
            repaint();
        }
    }

    class AimCanvas extends Component {
        private int W, H, ax, MID;

        AimCanvas(int w, int h) {
            W = w;
            H = h;
            ax = W/2;
            MID = H / 2;
            setSize(new Dimension(W, H));
            Thread animationThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        repaint();
                        try {
                            Thread.sleep(100);
                        } catch (Exception ignored) {}
                    }
                }
            });
            animationThread.start();
        }

        public int getX() {
            return ax;
        }

        public void move_left() {
            ax = ax - STEP < 0 ? W : ax - STEP;
        }

        public void move_right() {
            ax = ax + STEP > W ? 0 : ax + STEP;
        }

        @Override
        public void paint(Graphics gg) {
            Graphics2D g = (Graphics2D) gg;
            g.setColor(Color.RED);
            Ellipse2D.Double ellipse1 = new Ellipse2D.Double(ax - (RADIUS * 1.5), MID - (RADIUS * 1.5), RADIUS * 3, RADIUS * 3);
            Ellipse2D.Double ellipse2 = new Ellipse2D.Double(ax - RADIUS, MID - RADIUS, RADIUS * 2, RADIUS * 2);
            Ellipse2D.Double ellipse3 = new Ellipse2D.Double(ax - (RADIUS / 2), MID - (RADIUS / 2), RADIUS, RADIUS);
            Area circle = new Area(ellipse1);
            circle.subtract(new Area(ellipse2));
            circle.add(new Area(ellipse3));
            g.draw(circle);
            g.setColor(Color.BLACK);
        }
    }
}
