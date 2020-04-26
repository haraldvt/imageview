package nl.slideview.screensaver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

class TimedAction implements ActionListener {

    private final JFrame basisFrame;
    private final ImageComponent photo;
    private final Properties properties;
    private int entryPoint;
    private int i;
    private List<File> photos = new ArrayList<>();

    public TimedAction(JFrame basisFrame, ImageComponent foto, Properties properties) {
        this.basisFrame = basisFrame;
        this.photo = foto;
        this.properties = properties;

        leesFotoVerzameling();
    }

    private void leesFotoVerzameling() {
        photos = new ArrayList<>();
        leesFotoDirRecursief(properties.getPhotoDirectory(), photos);
        if (!photos.isEmpty()) {
            entryPoint = new Random().nextInt(photos.size());
            Logger.log("Total number of photo's: " + photos.size() + ", entryPoint: " + entryPoint);
            i = entryPoint;
        } else {
            Logger.log("No photos found, dir: " + properties.getPhotoDirectory());
            i = -1;
        }
    }

    public void leesFotoDirRecursief(String directoryName, List<File> files) {
        // check if this dir is on the excludes list:
        if (!isDirOnExcludesList(directoryName)) {
            File directory = new File(directoryName);
            if (directory.exists()) {
                // get all the files from a directory
                File[] fList = directory.listFiles();
                if (fList != null) {
                    Arrays.sort(fList);
                    for (File file : fList) {
                        if (file.isFile()) {
                            if (fileExtensionOke(file.getName())) {
                                files.add(file);
                            }
                        } else if (file.isDirectory()) {
                            leesFotoDirRecursief(file.getAbsolutePath(), files);
                        }
                    }
                }
            }
        }
    }

    private boolean isDirOnExcludesList(String dirName) {
        return properties.getExcludes().stream().anyMatch(dirName::contains);
    }
    
    private boolean fileExtensionOke(String fileName) {
        return fileName.toLowerCase().endsWith("jpg");
    }

    private void stop() {
        stop(0);
    }
    private void stop(int status) {
        Logger.log("exit " + status);
        System.exit(status);
    }

    private File geefVolgendeFoto() {
        if (photos.isEmpty()) {
            stop();
        } else {
        if (i == photos.size() - 1) {
            i = 0;
        } else
            i++;
        }
        if (i == entryPoint) {
            stop();
        }
        return photos.get(i);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (insideTimeFrame(Logger.getTime())) {
                
                File fotoFile = geefVolgendeFoto();
                if (fotoFile != null) {
                    photo.setFoto(fotoFile.getAbsolutePath());
                    addDate(photo);
                    addFotoText(photo, fotoFile.getParent());
                    photo.setVisible(true);
                }
            } else {
                // laat niets zien
                photo.setVisible(false);
            }
            basisFrame.repaint();

        } catch(Exception ex) {
            Logger.log(ex.getMessage());
        }
    }

    private boolean insideTimeFrame(String tijd) {
        int start = Integer.parseInt(properties.getDailyTimeStart());
        int stop = Integer.parseInt(properties.getDailyTimeStop());
        int huidig = Integer.parseInt(tijd);
        return (huidig > start && huidig < stop) ||
                (start > stop && (huidig > start || huidig < stop));
    }


    private final int fotoTextFontSize = 50;
    private final int fotoTextLineSize = fotoTextFontSize + 8;
    private final int fotoTextYStart = 830;
    private final int fotoTextXStart = 1140;
    private final JLabel[] fotoTextLabel = {new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    
    private final int dateFontSize = 64;
    private final int dateLineSize = dateFontSize + 8;
    private final int dateYStart = 10;
    private final int dateXStart = 10;
    private final JLabel[] dateLabel = {new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    
    private final int labelOpacity = 163;

    private void addFotoText(ImageComponent foto, String path) {
        refreshLabels(foto, fotoTextLabel);

        String[] splittedDir = path.replace("\\", "/").split("/");
        int l = splittedDir.length;
        //jaartal: splittedDir[l - 2] , omschrijving splittedDir[l - 1])

        double maxLabelWidth = addFotoTextLabel(0, "deze foto is uit:", foto);
        String maand = bepaalMaand(splittedDir[l - 1]);
        maxLabelWidth = Math.max(maxLabelWidth, addFotoTextLabel(1, maand + splittedDir[l - 2], foto));
        maxLabelWidth = Math.max(maxLabelWidth, addFotoTextLabel(2, "\"" + splittedDir[l - 1] + "\"", foto));
        
        for (int j = 0; j < 3 /*fotoTextLabel.length*/; j++) {
            JLabel jLabel = fotoTextLabel[j];
            jLabel.setBounds(fotoTextXStart, fotoTextYStart + (j * fotoTextLineSize), (int)maxLabelWidth, fotoTextLineSize);
        }
                
    }
    
    // hm de library kan dit 
    private String bepaalMaand(String beschrijving) {
        try {
            int i = beschrijving.indexOf("-");
            if (i>0) {
                switch (beschrijving.substring(i+1, i+3)){
                    case "01": return "januari ";
                    case "02": return "februari ";
                    case "03": return "maart ";
                    case "04": return "april ";
                    case "05": return "mei ";
                    case "06": return "juni ";
                    case "07": return "juli ";
                    case "08": return "augustus ";
                    case "09": return "september ";
                    case "10": return "oktober ";
                    case "11": return "november ";
                    case "12": return "december ";
                    default: return "";
                }
            }
        } catch (Exception e) {
            // slik...
            Logger.log(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    
    private double addFotoTextLabel(int regelnr, String text, ImageComponent foto) {
        JLabel l = fotoTextLabel[regelnr];
        l.setText(text);
        l.setFont(new Font("Arial", Font.PLAIN, fotoTextFontSize));
        l.setForeground(Color.WHITE);
        l.setBackground(new Color(0, 0, 0, labelOpacity));
        l.setOpaque(true);
        foto.add(l);
        return l.getPreferredSize().getWidth();
    }
    

    
    private void addDate(ImageComponent foto) {
        refreshLabels(foto, dateLabel);
        
        double maxLabelWidth = addDateLabel(0, "Vandaag is:", foto, Font.PLAIN);
        
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(1, format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        format = new SimpleDateFormat("d LLL yyyy", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(2, format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(3, "", foto, Font.BOLD));

        format = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(4, "Tijd: " + format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        for (int j = 0; j < 5 /*dateLabel.length*/; j++) {
            JLabel jLabel = dateLabel[j];
            jLabel.setBounds(dateXStart, dateYStart + (j * dateLineSize), (int)maxLabelWidth, dateLineSize);
        }
    }
    
    private double addDateLabel(int regelnr, String text, ImageComponent foto, int fontAppearance) {
        JLabel l = dateLabel[regelnr];
        l.setText(text);
        l.setFont(new Font("Arial", fontAppearance, dateFontSize));
        l.setForeground(Color.WHITE);
        l.setBackground(new Color(0, 0, 0, labelOpacity));
        l.setOpaque(true);
        foto.add(l);
        return l.getPreferredSize().getWidth();
    }

    
    private void refreshLabels(ImageComponent foto, JLabel[] labels) {
        for (JLabel label : labels) {
            if (label != null) {
                label.setText("");
                label.removeAll();
                foto.remove(label);
                //labels[j] = null;
            }
            //labels[j] = new JLabel();
        }
    }
}
