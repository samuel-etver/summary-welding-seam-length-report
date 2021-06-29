package weldingseamlength;

import java.io.*;
import java.util.*; 

public class CfgFile {
    private final HashMap<String, String> m_Dict = new HashMap<>();
  

    public void clear() {
        m_Dict.clear();
    }
  

    public String read(String key, String def) {
        final String value = m_Dict.get(key);
        return value == null ? def : value;
    }


    public boolean read(String key, boolean def) {
        final String value = read(key, "");

        switch(value.toLowerCase()) {
          case "true":
            return true;
          case "false":
            return false;
        }

        return def;
    }


    public int read(String key, int def) {
        final String value = read(key, "");

        try {
          return Integer.parseInt(value);
        }
        catch(Exception exception) {
        }

        return def;
    }


    public float read(String key, float def) {
        final String value = read(key, "");

        try {
            return Float.parseFloat(value);
        }
        catch(Exception exception) {
        }

        return def;
    }


    public boolean load(String fileName) {
        String line;
        String key;
        String value;

        boolean done = false;

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while((line = reader.readLine()) != null) {
                final int pos = line.indexOf('=');        

                if (pos < 0) {
                    key   = line.trim();
                    value = "";
                }
                else {
                    key   = line.substring(0, pos).trim();
                    value = line.substring(pos + 1).trim();
                }

                m_Dict.put(key, value);
            }

            done = true;
        }
        catch(Exception exception) {
        }

        return done;
    }
}
