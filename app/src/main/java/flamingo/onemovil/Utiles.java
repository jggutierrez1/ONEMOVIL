package flamingo.onemovil;

import android.provider.Settings;

import org.ini4j.*;
import java.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import android.provider.Settings.Secure;
import android.content.Context;

public class Utiles {

    /**
     * Created by Adley on 15/04/2016.
     * Read a ini config file
     */
    public class ReadIniConfigFile {
        private String configurationFile;
        private final String defaultConfigPath = "config.ini";
        private HashMap<String, String> iniFile;

        public HashMap<String, String> getIniFile() {
            return iniFile;
        }

        public void setIniFile(HashMap<String, String> iniFile) {
            this.iniFile = iniFile;
        }

        public ReadIniConfigFile(String iniConfFilePath, String... keys) {
            configurationFile = iniConfFilePath == null ? defaultConfigPath : iniConfFilePath;
            Ini ini = loadIni();
            if (ini != null) {
                if (keys.length > 0) {
                    for (String key : keys) {
                        iniFile.put(key, ini.get(key).toString());
                    }
                }
            }
        }

        public ReadIniConfigFile(String iniConfFilePath, String key) {
            configurationFile = iniConfFilePath == null ? defaultConfigPath : iniConfFilePath;
            Ini ini = loadIni();
            if (ini != null) {
                iniFile.put(key, ini.get(key).toString());
            }
        }

        public ReadIniConfigFile(String... keys) {
            configurationFile = defaultConfigPath;
            Ini ini = loadIni();
            if (ini != null) {
                if (keys.length > 0) {
                    for (String key : keys) {
                        iniFile.put(key, ini.get(key).toString());
                    }
                }
            }
        }

        public ReadIniConfigFile(String key) {
            configurationFile = defaultConfigPath;
            Ini ini = loadIni();
            if (ini != null) {
                iniFile.put(key, ini.get(key).toString());
            }
        }

        public Ini loadIni() {
            try {
                Ini ini = new Ini(new File(configurationFile));
                return ini;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public HashMap<String, String> getAllIni() {
            return iniFile;
        }

        public String getSpecificValueIni(String key) {
            return iniFile.get(key);
        }

        public Collection<String> getAllValues() {
            return iniFile.values();
        }

        public String getSpecificKeyValueIniToString(String key) {
            return key + " = " + iniFile.get(key);
        }

    }
}
