package my.texteditor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;
import javax.swing.JLabel;
import java.util.Timer;

public class Clock {
    
    private final DateFormat dateFormat;
    private final JLabel label;
    private Timer timer;
    
    public Clock(JLabel label) {
        this.label = label;
        this.dateFormat = new SimpleDateFormat("hh:mm:ss a");
    }
    
    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                String time = dateFormat.format(calendar.getTime());
                label.setText(time);
            }
        }, 0, 1000); // update every second
    } 
   
}

 


    

