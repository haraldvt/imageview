package nl.slideview.screensaver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * in windows 10 / nederlands: instellingen: zoek op schermbeveiliging, daar kan je screensaver instellen
 * 
 * .exe wordt gemaakt met C:\tmp\slideview\java\launch4j, dan renamed naar .scr, en gekopieerd naar c:\windows\syswow64
 * 
 * @author Harald
 */

public final class ScreenSaver {

    private static final String SLIDEVIEW_PROPERTIES = "c:\\slideview.properties";

    public static final void main(final String[] args) {
        String[] arguments;
        try {
            System.out.println("Start SlideView");
            arguments = args;
            Properties properties = loadPropertiesFile(SLIDEVIEW_PROPERTIES);

            int timeIntervalInSeconds = (properties.getProperty("timeIntervalInSeconds") == null ? 10
                    : Integer.parseInt(properties.getProperty("timeIntervalInSeconds")));
            new ScreenSaver(timeIntervalInSeconds, properties.getProperty("photoDirectory"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

//    private void debugLogArgs(String[] args, JLabel label) {
//        if (args.length > 0) {
//            label.setText(args[0]);
//        }
//        if (args.length > 1) {
//            label.setText(label.getText() + ", " + args[1]);
//        }
//        if (args.length > 2) {
//            label.setText(label.getText() + ", " + args[2]);
//        }
//        if (args.length > 3) {
//            label.setText(label.getText() + ", " + args[3]);
//        }
//    }

    public ScreenSaver(int timeIntervalInSeconds, String photoDirectory) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final JFrame screenSaverFrame = new JFrame();
        screenSaverFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        screenSaverFrame.setUndecorated(true);
        screenSaverFrame.setResizable(false);

        ImageComponent foto = new ImageComponent();
        screenSaverFrame.add(foto, BorderLayout.CENTER);
        screenSaverFrame.getContentPane().setBackground(Color.BLACK);

        screenSaverFrame.validate();

        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(
                screenSaverFrame);

        foto.setTotalSpace(screenSaverFrame.getSize().getWidth(), screenSaverFrame.getSize().getHeight());

        Timer timer = new Timer(timeIntervalInSeconds * 1000, new TimedAction(screenSaverFrame, foto, photoDirectory));
        System.out.println("start FotoTimer, aantal seconden: " + timeIntervalInSeconds);
        timer.start();

        screenSaverFrame.addMouseMotionListener(new MuisExiter());
        screenSaverFrame.addMouseListener(new MuisExiter());

    }

    class OnMouseMovementExit implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("mouseClicked");
            System.exit(0);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println("mousePressed");
            System.exit(0);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println("mouseReleased");
            System.exit(0);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // System.out.println("mouseEntered");
            // System.exit(0);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            System.out.println("mouseExited");
            System.exit(0);
        }
    }

    class MuisExiter extends MouseAdapter {

        private boolean l = false;

        @Override
        public void mouseMoved(MouseEvent e) {
            System.out.println("mouseMoved");
            if (l) {
                System.exit(0);
            }
            l = true;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println("mousePressed");
            System.exit(0);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            System.out.println("mouseExited");
            System.exit(0);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("mouseClicked");
            System.exit(0);
        }
    }


    private static Properties loadPropertiesFile(String propertiesFileName) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(propertiesFileName);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("timeIntervalInSeconds"));
            System.out.println(prop.getProperty("photoDirectory"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

}
