package nl.slideview.screensaver;

import lombok.Builder;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

class TimedAction implements ActionListener {

    private final JFrame basisFrame;
    private final ImageComponent photo;
    private final Properties properties;
    private int entryPoint;
    private int photoCounter;
    private List<File> photos = new ArrayList<>();

    private final PhotoLabel photoLabel = PhotoLabel.builder()
            .textFontSize(50)
            .textLineSize(50 + 8)
            .textYStart(830)
            .textXStart(1140).build();
    private final PhotoLabel dateLabel = PhotoLabel.builder()
            .textFontSize(64)
            .textLineSize(64 + 8)
            .textYStart(10)
            .textXStart(10).build();


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
            photoCounter = entryPoint;
        } else {
            Logger.log("No photos found, dir: " + properties.getPhotoDirectory());
            photoCounter = -1;
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
        if (photoCounter == photos.size() - 1) {
            photoCounter = 0;
        } else
            photoCounter++;
        }
        if (photoCounter == entryPoint) {
            stop();
        }
        return photos.get(photoCounter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (insideTimeFrame(Logger.getTime())) {
                
                File fotoFile = geefVolgendeFoto();
                if (fotoFile != null) {
                    photo.setFoto(fotoFile.getAbsolutePath());
                    addDate(photo, dateLabel);
                    addFotoText(photo, photoLabel, fotoFile.getParent());
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


    private void addFotoText(ImageComponent foto, PhotoLabel photoLabel, String path) {
        refreshLabels(foto, photoLabel.getTextLabel());

        String[] splittedDir = path.replace("\\", "/").split("/");
        int l = splittedDir.length;

        double maxLabelWidth = addFotoTextLabel(0, photoLabel, "deze foto is uit:", foto);
        String maand = bepaalMaand(splittedDir[l - 1]);
        maxLabelWidth = Math.max(maxLabelWidth, addFotoTextLabel(1, photoLabel, maand + splittedDir[l - 2], foto));
        maxLabelWidth = Math.max(maxLabelWidth, addFotoTextLabel(2, photoLabel, "\"" + splittedDir[l - 1] + "\"", foto));
        
        for (int j = 0; j < 3 /*fotoTextLabel.length*/; j++) {
            JLabel jLabel = photoLabel.getTextLabel()[j];
            jLabel.setBounds(photoLabel.getTextXStart(), photoLabel.getTextYStart() + (j * photoLabel.getTextLineSize()),
                    (int)maxLabelWidth, photoLabel.getTextLineSize());
        }
                
    }
    
    private String bepaalMaand(String beschrijving) {
        try {
            int pos = beschrijving.indexOf("-");
            if (pos > 0 && beschrijving.length() >= pos + 3) {
                int month = Integer.parseInt(beschrijving.substring(pos + 1, pos + 3));
                return new DateFormatSymbols().getMonths()[month-1];
            }
        } catch (Exception e) {
            Logger.log(beschrijving);
            Logger.log(e.getMessage());
        }
        return "";
    }
    
    private double addFotoTextLabel(int regelnr, PhotoLabel photoLabel, String text, ImageComponent foto) {
        JLabel l = photoLabel.getTextLabel()[regelnr];
        l.setText(text);
        l.setFont(new Font("Arial", Font.PLAIN, photoLabel.getTextFontSize()));
        l.setForeground(Color.WHITE);
        l.setBackground(new Color(0, 0, 0, photoLabel.getLabelOpacity()));
        l.setOpaque(true);
        foto.add(l);
        return l.getPreferredSize().getWidth();
    }
    

    
    private void addDate(ImageComponent foto, PhotoLabel dateLabel) {
        refreshLabels(foto, dateLabel.getTextLabel());
        
        double maxLabelWidth = addDateLabel(0, dateLabel, "Vandaag is:", foto, Font.PLAIN);
        
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(1, dateLabel, format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        format = new SimpleDateFormat("d LLL yyyy", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(2, dateLabel, format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(3, dateLabel, "", foto, Font.BOLD));

        format = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("NL"));
        maxLabelWidth = Math.max(maxLabelWidth, addDateLabel(4, dateLabel, "Tijd: " + format.format(Calendar.getInstance().getTime()), foto, Font.BOLD));

        for (int j = 0; j < 5 /*dateLabel.length*/; j++) {
            JLabel jLabel = dateLabel.getTextLabel()[j];
            jLabel.setBounds(dateLabel.getTextXStart(), dateLabel.getTextYStart() + (j * dateLabel.getTextLineSize()),
                    (int)maxLabelWidth, dateLabel.getTextLineSize());
        }
    }
    
    private double addDateLabel(int regelnr, PhotoLabel dateLabel, String text, ImageComponent foto, int fontAppearance) {
        JLabel l = dateLabel.getTextLabel()[regelnr];
        l.setText(text);
        l.setFont(new Font("Arial", fontAppearance, dateLabel.getTextFontSize()));
        l.setForeground(Color.WHITE);
        l.setBackground(new Color(0, 0, 0, dateLabel.getLabelOpacity()));
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
            }
        }
    }


    @Getter
    @Builder
    private static class PhotoLabel {
        private final int textFontSize;
        private final int textLineSize;
        private final int textYStart;
        private final int textXStart;
        private final JLabel[] textLabel = {new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel()};
        private final int labelOpacity = 163;
    }

}
