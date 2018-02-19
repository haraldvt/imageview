package nl.slideview.screensaver;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLabel;

class TimedAction implements ActionListener {

    private final JFrame basisFrame;

    private final ImageComponent photo;

    private final String photoDirectory;

    private final String[] excludes;

    private int entryPoint;

    private int i;

    private List<File> photos = new ArrayList<>();

    public TimedAction(JFrame basisFrame, ImageComponent foto, String photoDirectory, String photoDirExcludes) {
        this.basisFrame = basisFrame;
        this.photo = foto;
        this.photoDirectory = photoDirectory;
        excludes = (photoDirExcludes != null ? photoDirExcludes.split(",") : new String[0]);

        leesFotoVerzameling();
    }

    private void leesFotoVerzameling() {
        photos = new ArrayList<>();
        leesFotoDirRecursief(photoDirectory, photos);
        if (!photos.isEmpty()) {
            entryPoint = new Random().nextInt(photos.size());
            System.out.println("Total number of photo's: " + photos.size() + ", entryPoint: " + entryPoint);
            i = entryPoint;
            i=0;
        } else {
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

    private boolean isDirOnExcludesList(String dirName) {
        for (int j = 0; j < excludes.length; j++) {
            if (dirName.contains(excludes[j])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean fileExtensionOke(String fileName) {
        return fileName.toLowerCase().endsWith("jpg");
    }

    private File geefVolgendeFoto() {
        if (photos.isEmpty()) {
            System.exit(0);
        } else {
        if (i == photos.size() - 1) {
            i = 0;
        } else
            i++;
        }
        if (i == entryPoint) {
            System.out.println("exit");
            System.exit(0);
        }
        return photos.get(i);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HHmm", Locale.forLanguageTag("NL"));
            String tijd = format.format(Calendar.getInstance().getTime());
            if (tijd.compareTo("0700") > 0) {  // tussen 0 en 7 niets tonen
                
                File fotoFile = geefVolgendeFoto();
                if (fotoFile != null) {
                    System.out.println(fotoFile.getAbsolutePath());
                    photo.setFoto(fotoFile.getAbsolutePath());
                    addDate(photo);
                    addFotoText(photo, fotoFile.getParent());
                    basisFrame.repaint();
                }
            } else {
                System.exit(0);
            }
            
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private final int fotoTextFontSize = 50;
    private final int fotoTextLineSize = fotoTextFontSize + 8;
    private final int fotoTextYStart = 830;
    private final int fotoTextXStart = 1140;
    private final JLabel[] fotoTextLabel = {new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    
    private final int dateFontSize = 72;
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            return "";
        }
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

        format = new SimpleDateFormat("d LLLL YYYY", Locale.forLanguageTag("NL"));
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
        for (int j = 0; j < labels.length; j++) {
            if (labels[j] != null) {
                labels[j].setText("");
                labels[j].removeAll();
                foto.remove(labels[j]);
                //labels[j] = null;
            }
            //labels[j] = new JLabel();
        }
    }
}
