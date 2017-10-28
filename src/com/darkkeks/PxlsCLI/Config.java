package com.darkkeks.PxlsCLI;

import java.io.*;
import java.util.ArrayList;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;


public class Config {

    private String p;
    private Wini config;
    private Wini configDefaults;
    private boolean autoUpdateConfigFile = false;

    public Config (final String path, final String defaultPath) {
        p = path;

        try {
            configDefaults = readIniFile(defaultPath);
            config = readIniFile(path);
        } catch (InvalidFileFormatException e) {
            System.out.println("Invalid config file format. Using default config.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error accessing config file. Using default config.");
//            e.printStackTrace();
        } finally {
            config = checkWiniConfig(config, configDefaults);
            updateConfigFile();
        }
    }

    private Wini readIniFile (String path) throws InvalidFileFormatException, IOException {
        return new Wini(new File(path));
    }

    private void writeIniFile (String path, Wini ini) throws InvalidFileFormatException, IOException {
        File f = new File(path);
        if ( !f.exists() || f.isDirectory() ) return;

        String s = WiniToIniString(ini);
        if ( s.isEmpty() ) return;

        BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write(s);

        w.close();
    }

    private String WiniToIniString (Wini w) {
        if ( w == null || w.size() < 1) return "";

        StringBuilder sb = new StringBuilder();

        for ( String sectionName : w.keySet() ) {

            sb.append("[").append(sectionName).append("]\n");

            for ( String key : w.get(sectionName).keySet() ) {
                sb.append(key).append("=").append(w.get(sectionName).get(key)).append("\n");
            }
        }

        sb.deleteCharAt(sb.lastIndexOf("\n"));

        if ( sb.length() - 1 == sb.lastIndexOf("\n")) sb.deleteCharAt(sb.length() - 1);

        return sb.toString();

    }

    private Wini checkWiniConfig(Wini c, Wini d) {
        if ( c == null || c.size() < 1 ) {
            try {
                writeIniFile(p, d);
            } catch (IOException e) {
                e.printStackTrace();
            } return d;
        }

        ArrayList<String> rs = new ArrayList<>();
        ArrayList<String[]> rk = new ArrayList<>();

        for ( String sectionName : d.keySet() ) {
            if ( c.get(sectionName) == null || c.get(sectionName).size() < 1 ) {
                c.put(sectionName, d.get(sectionName));
                continue;
            }

            for ( String key : d.get(sectionName).keySet() ) {
                Section s = c.get(sectionName);

                if ( !s.containsKey(key) || s.get(key).isEmpty())
                    c.get(sectionName).put(key, d.get(sectionName).get(key));
            }
        }

        for ( String sectionName : c.keySet() ) {
            if ( d.get(sectionName) == null || d.get(sectionName).size() < 1 ) {
                rs.add(sectionName);
                continue;
            }

            for ( String key : c.get(sectionName).keySet() ) {
                Section s = d.get(sectionName);

                if ( !s.containsKey(key) || s.get(key).isEmpty())
                    rk.add(new String[] {sectionName, key});
            }
        }

        rs.forEach(c::remove);
        rk.forEach(s -> c.get(s[0]).remove(s[1]));

        return c;
    }

    public void updateConfigFile () {
        try {
            writeIniFile(p, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAutoUpdateConfigFile (boolean state) {
        autoUpdateConfigFile = state;
    }

    public boolean isAutoUpdateConfigFile() {
        return autoUpdateConfigFile;
    }

    public String get (String section, String key) {
        return config.get(section).get(key);
    }

    public int getInt (String section, String key) {
        return Integer.parseInt(get(section, key));
    }

    public float getFloat (String section, String key) {
        return Float.parseFloat(get(section, key));
    }

    public boolean getBool (String section, String key) {
        return Boolean.parseBoolean(get(section, key));
    }

    public String[] getArray (String section, String key, String separator) {
        if ( separator.isEmpty() ) separator = " ";
        return config.get(section).get(key).split(separator);
    }

    public void put (String section, String key, String value) {
        if (config.get(section) == null) {
            config.add(section);
        }

        config.get(section).put(key, value);
        if (autoUpdateConfigFile) updateConfigFile();
    }

    public void put (String section, String key, int value) {
        put(section, key, String.valueOf(value));
    }

    public void put (String section, String key, float value) {
        put(section, key, String.valueOf(value));
    }

    public void put (String section, String key, boolean value) {
        put(section, key, String.valueOf(value));
    }

    public void put (String section, String key, String[] values, String separator) {
        if ( separator.isEmpty() ) separator = " ";
        StringBuilder sb = new StringBuilder();

        for ( String v : values ) {
            if (sb.length() == 0) sb.append(v);
            else sb.append(separator).append(v);
        }

        put(section, key, sb.toString());
    }

}
