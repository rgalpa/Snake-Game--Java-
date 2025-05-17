import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JFrame {
    public SnakeGame() {
        setTitle("Snake Game - Java");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final int DELAY = 100;

    private final int x[] = new int[GAME_UNITS];
    private final int y[] = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R'; // U D L R
    private boolean running = false;
    private Timer timer;
    private Random random;
    private JButton restartButton;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT + 50));
        setBackground(new Color(20, 20, 20));
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        restartButton = new JButton("Restart");
        restartButton.setBounds((WIDTH - 100) / 2, HEIGHT + 5, 100, 30);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> resetGame());
        add(restartButton);

        random = new Random();
        startGame();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * UNIT_SIZE;
            y[i] = 100;
        }

        newApple();
        timer = new Timer(DELAY, this);
        timer.start();
        restartButton.setVisible(false);
    }

    public void resetGame() {
        timer.stop();
        startGame();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        draw(g);
    }

    public void drawBackground(Graphics g) {
        // Gradient background
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(0, 30, 60);
        Color color2 = new Color(0, 90, 130);
        GradientPaint gp = new GradientPaint(0, 0, color1, WIDTH, HEIGHT, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw border
        g2d.setColor(new Color(180, 180, 255));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(2, 2, WIDTH - 4, HEIGHT - 4);
    }

    public void draw(Graphics g) {
        if (running) {
            // Apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(173, 255, 47)); // Lime Green
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 20);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Body collision
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        // Wall collision
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            restartButton.setVisible(true);
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, HEIGHT / 3);

        // Game Over
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics2.stringWidth("Game Over")) / 2, HEIGHT / 2);
    }

    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L'; break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R'; break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U'; break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D'; break;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}
