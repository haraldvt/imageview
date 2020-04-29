package nl.slideview.screensaver;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * in windows 10 / nederlands: instellingen: zoek op schermbeveiliging, daar kan je screensaver instellen
 * 
 * build
 * start launch4j (C:\hvt\workspaces\launch4j, of C:\tmp\slideview\java\launch4j)
 *   gebruik settings uit farpoint.xml
 *   gebruikte jar: C:\hvt\workspaces\imageview\dist\slideview.jar
 *   .exe wordt neergezet in C:\hvt\syncPhotoFarpoint\screensaver
 * cloudstation kopieert de exe naar de doelcomputer
 * op die computer, voer uit:
 *   copyTowow64.cmd  (renamed naar .scr, en kopieert naar c:\windows\syswow64)
 *
 * compile and package : mvn -D skipTests clean package -e
 * run from commanline : target\slideview.exe --l4j-debug
 *
 * @author Harald
 */

public final class ScreenSaver {
    public static void main(final String[] args) {
        String[] arguments;
        try {
            Logger.log("Start SlideView");
            new ScreenSaver(new Properties());
        } catch (Exception e) {
            Logger.log(e.getMessage());
        }
    }

    public ScreenSaver(Properties properties) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final JFrame screenSaverFrame = new JFrame();
        screenSaverFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        screenSaverFrame.setUndecorated(true);
        screenSaverFrame.setResizable(false);

        ImageComponent foto = new ImageComponent();
        screenSaverFrame.add(foto, BorderLayout.CENTER);
        screenSaverFrame.getContentPane().setBackground(Color.BLACK);
        settingsForScreenSaverBehaviour(screenSaverFrame);
        screenSaverFrame.validate();

        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(
                screenSaverFrame);

        foto.setTotalSpace(screenSaverFrame.getSize().getWidth(), screenSaverFrame.getSize().getHeight());

        Timer timer = new Timer(
                properties.getTimeIntervalInSeconds() * 1000,
                new TimedAction(screenSaverFrame, foto, properties));
        Logger.log("start FotoTimer, aantal seconden: " + properties.getTimeIntervalInSeconds());
        timer.start();

    }

    private void settingsForScreenSaverBehaviour(JFrame screenSaverFrame) {
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        screenSaverFrame.getContentPane().setCursor(blankCursor);

        screenSaverFrame.addMouseMotionListener(new MouseMotionListener() {
            private boolean l = false;

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (l) {
                    stop("mouseMoved");
                } else {
                    Logger.log("mouseMoved");
                }
                l = true;
            }
        });
        screenSaverFrame.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                stop("mousePressed");
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                stop("mouseReleased");
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                stop("mouseExited");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                stop("mouseClicked");
            }
        });
//        screenSaverFrame.addWindowStateListener(windowEvent -> {
//            stop("windowStateChanged");
//        });
//        screenSaverFrame.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent keyEvent) {
//                stop("keyTyped");
//            }
//
//            @Override
//            public void keyPressed(KeyEvent keyEvent) {
//                stop("keyPressed");
//            }
//
//            @Override
//            public void keyReleased(KeyEvent keyEvent) {
//                stop("keyReleased");
//            }
//        });
//        screenSaverFrame.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent focusEvent) {
//
//            }
//
//            @Override
//            public void focusLost(FocusEvent focusEvent) {
//                stop("focusLost");
//            }
//        });
//        screenSaverFrame.addWindowFocusListener(new WindowFocusListener() {
//            @Override
//            public void windowGainedFocus(WindowEvent windowEvent) {
//
//            }
//
//            @Override
//            public void windowLostFocus(WindowEvent windowEvent) {
//                stop("windowLostFocus");
//            }
//        });
    }

    private void stop(String message) {
        stop(0, message);
    }
    private void stop(int status, String message) {
        Logger.log(message);
        System.exit(status);
    }
}
