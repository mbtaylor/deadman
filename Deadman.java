
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Deadman {
    public static void main( String[] args ) {
        JFrame frm = new JFrame();
        Alarm alarm = Alarm.createAlarm();
        CountdownPanel counter = new CountdownPanel( alarm, 20, 5 );
        Container content = frm.getContentPane();
        content.setLayout( new BorderLayout() );
        content.add( counter, BorderLayout.CENTER );
        counter.setBorder( BorderFactory.createEmptyBorder( 24, 24, 24, 24 ) );
        frm.pack();
        frm.setVisible( true );
    }
}
