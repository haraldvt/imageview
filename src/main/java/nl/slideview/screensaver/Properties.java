package nl.slideview.screensaver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Properties {
    public static final String SLIDEVIEW_PROPERTIES = "c:\\slideview.properties";

    private final int timeIntervalInSeconds;
    private final String photoDirectory;
    private final String photoDirExcludes;
    private final String dailyTimeStart;
    private final String dailyTimeStop;
    private final List<String> excludes;

    public int getTimeIntervalInSeconds() {
        return timeIntervalInSeconds;
    }

    public String getPhotoDirectory() {
        return photoDirectory;
    }

    public String getPhotoDirExcludes() {
        return photoDirExcludes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public String getDailyTimeStart() {
        return dailyTimeStart;
    }

    public String getDailyTimeStop() {
        return dailyTimeStop;
    }

    public Properties() {
        java.util.Properties properties = loadPropertiesFile();

        timeIntervalInSeconds = (properties.getProperty("timeIntervalInSeconds") == null ? 10: Integer.parseInt(properties.getProperty("timeIntervalInSeconds")));
        photoDirectory = properties.getProperty("photoDirectory");

        photoDirExcludes = (properties.getProperty("photoDirExcludes") == null ? "" : properties.getProperty("photoDirExcludes"));
        excludes = Arrays.asList(photoDirExcludes.split(","));

        dailyTimeStart = (properties.getProperty("dailyTimeStart") == null ? "" : properties.getProperty("dailyTimeStart"));
        dailyTimeStop = (properties.getProperty("dailyTimeStop") == null ? "" : properties.getProperty("dailyTimeStop"));
    }

    private static java.util.Properties loadPropertiesFile() {
        java.util.Properties prop = new java.util.Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(SLIDEVIEW_PROPERTIES);

            // load a properties file
            prop.load(input);

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
