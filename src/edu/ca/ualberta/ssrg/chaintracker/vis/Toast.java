package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Label;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * This code was adapted from code at the following link:
 * http://stackoverflow.com/questions/10161149/android-like-toast-in-swing
 */

public class Toast {

    private final JComponent component;
    private Point   location;
    private final String  message;
    private long duration; //in millisecond

    public Toast(JComponent comp, Point toastLocation, String msg, long forDuration) {
        this.component = comp;
        this.location = toastLocation;
        this.message = msg;
        this.duration = forDuration;

        if(this.component != null)
        {
            Point loc = component.getLocationOnScreen();
            location.x = location.x + loc.x;
            location.y = location.y + loc.y;

            new Thread(new Runnable()
            {
                @Override
                public void run() 
                {
                    Popup view = null;
                    try 
                    {
                        Label tip = new Label(message);
                        tip.setForeground(Color.BLACK);
                        tip.setBackground(Colors.LIGHT_RED);
                        view = PopupFactory.getSharedInstance().getPopup(component, tip , location.x, location.y);
                        view.show();
                        Thread.sleep(duration);
                    } catch (InterruptedException ex) 
                    {
                        Logger.getLogger(Toast.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    finally
                    {
                        view.hide();
                    }
                }
            }).start();
        }
    }

    public static void showToast(JComponent component, String message, Point location, long forDuration)
    {
        new Toast(component, location, message, forDuration);
    }
}
