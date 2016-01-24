package pl.edu.pwr.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Pawel on 2014-12-04.
 */
public class ConfigService {
    private Properties prop = new Properties();

    public ConfigService(String path) throws IOException {
        try (FileInputStream in = new FileInputStream(path)) {
            prop.load(in);
        }
    }

    public String getProp(String p) {
        return prop.getProperty(p, "");
    }
}
