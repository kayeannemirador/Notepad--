/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.texteditor;
   import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;


 
public class AnalogClock extends JPanel {

    private static final int CLOCK_SIZE = 300;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color HOUR_HAND_COLOR = Color.BLACK;
    private static final Color MINUTE_HAND_COLOR = Color.BLACK;
    private static final Color SECOND_HAND_COLOR = Color.RED;

    private int centerX;
    private int centerY;
    private int hourHandLength;
    private int minuteHandLength;
    private int secondHandLength;

    public AnalogClock() {
        setPreferredSize(new Dimension(CLOCK_SIZE, CLOCK_SIZE));
        setBackground(BACKGROUND_COLOR);

        calculateParameters();
        startClock();
    }

    private void calculateParameters() {
        centerX = CLOCK_SIZE / 2;
        centerY = CLOCK_SIZE / 2;
        hourHandLength = CLOCK_SIZE / 4;
        minuteHandLength = CLOCK_SIZE / 3;
        secondHandLength = CLOCK_SIZE / 2 - 10;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawClockFace(g);
        drawClockHands(g);
    }

    private void drawClockFace(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(5, 5, CLOCK_SIZE - 10, CLOCK_SIZE - 10);

        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(30 * i - 90);
            int x = (int) (centerX + (CLOCK_SIZE / 2 - 20) * Math.cos(angle));
            int y = (int) (centerY + (CLOCK_SIZE / 2 - 20) * Math.sin(angle));
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
    }

    private void drawClockHands(Graphics g) {
        LocalTime time = LocalTime.now();
        int hour = time.getHour() % 12;
        int minute = time.getMinute();
        int second = time.getSecond();

        double hourAngle = Math.toRadians((hour + minute / 60.0) * 30 - 90);
        double minuteAngle = Math.toRadians((minute + second / 60.0) * 6 - 90);
        double secondAngle = Math.toRadians(second * 6 - 90);

        drawHand(g, centerX, centerY, hourHandLength, hourAngle, HOUR_HAND_COLOR);
        drawHand(g, centerX, centerY, minuteHandLength, minuteAngle, MINUTE_HAND_COLOR);
        drawHand(g, centerX, centerY, secondHandLength, secondAngle, SECOND_HAND_COLOR);
    }

    private void drawHand(Graphics g, int x, int y, int length, double angle, Color color) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(x, y, (int) (x + length * Math.cos(angle)), (int) (y + length * Math.sin(angle)));
    }
    
      
}

    
   

