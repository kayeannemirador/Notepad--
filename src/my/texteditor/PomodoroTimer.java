
package my.texteditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

    public class PomodoroTimer extends JFrame {

    private static final int TIMER_INTERVAL = 1000;
    private JLabel timeLabel;
    private Timer timer;
    private int pomodoroTime;
    private boolean isRunning;

    public PomodoroTimer() {
        super("Pomodoro Timer");

        // Initialize the timer.
        timer = new Timer(TIMER_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning) {
                    pomodoroTime--;
                    timeLabel.setText(String.format("%02d:%02d", pomodoroTime / 60, pomodoroTime % 60));

                    if (pomodoroTime == 0) {
                        stop();
                    }
                }
            }
        });

        // Initialize the time label.
        timeLabel = new JLabel("00:00");

        // Add the time label to the frame.
        add(timeLabel, BorderLayout.CENTER);

        // Add a start button to the frame.
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        add(startButton, BorderLayout.SOUTH);

        // Set the frame's size and visibility.
        setSize(300, 100);
        setVisible(true);
    }

    public void start() {
        if (!isRunning) {
            timer.start();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        PomodoroTimer timer = new PomodoroTimer();
        timer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
