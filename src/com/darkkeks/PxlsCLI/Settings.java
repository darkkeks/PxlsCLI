package com.darkkeks.PxlsCLI;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.google.gson.JsonObject;

public class Settings {

    private static JsonObject sets;
    private static JsonObject defaults = new JsonObject();

    private static final Object[][] defaultsRaw = new Object[][] {
            {"tokensFilePath", "tokens.in"},
            {"templateURI", "https://i.imgur.com/dJixaNt.png"},
            {"templateOffsetX", 568},
            {"templateOffsetY", 762},
            {"templateOpacity", 0.5},
            {"templateReplacePixels", true},
            {"controls", new Object[][] {
                    {"up", "up"},
                    {"left", "left"},
                    {"right", "right"},
                    {"down", "down"},
                    {"zoomIn", "+"},
                    {"zoomOut", "-"},
                    {"shift", "shift"},
                    {"ctrl", "ctrl"}
            }}
    };

    Settings(final String path) {
        createDefaults();

        try {
            sets = readSettings(path);
            checkSettings(path);
        } catch (FileNotFoundException e) {
            System.out.println("Settings file not found");
            createSettingsFile(defaults, path);
            sets = defaults.deepCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject convertObjectToJsonObject(Object[][] obj) {
        JsonObject jo = new JsonObject();
        for (Object[] o : obj) {
            String key = o[0].toString();

            if (o[1] instanceof Object[][]) {
                jo.add(key, convertObjectToJsonObject((Object[][]) o[1]));
                continue;
            }

            String value = o[1].toString();

            jo.addProperty(key, value);
        }
        return jo;
    }

    private void createDefaults() {
        defaults = convertObjectToJsonObject(defaultsRaw);
    }

    private void checkSettings(String path) {
        if (sets == null || sets.size() < 1) {
            sets = defaults.deepCopy();
            createSettingsFile(defaults, path);
            return;
        }

        for (Object[] o : defaultsRaw) {
            String k = o[0].toString();

            if (o[1] instanceof Object[][]) {
                if ( !sets.has(k) || sets.get(k).isJsonNull() ) {
                    sets.add(k, defaults.get(k).getAsJsonObject());
                    continue;
                }

                for (Object[] oo: (Object[][]) o[1]) {
                    String kk = oo[0].toString();

                    if (oo[1] instanceof Object[][]) continue;

                    String vv = oo[1].toString();

                    if ( !sets.get(k).getAsJsonObject().has(kk) || sets.get(k).getAsJsonObject().get(kk).toString().isEmpty() )
                        sets.get(k).getAsJsonObject().addProperty(kk, vv);
                }
            }

            String v = o[1].toString();

            if ( !sets.has(k) || sets.get(k).toString().isEmpty() ) sets.addProperty(k, v);
        }
    }

    private void createSettingsFile(JsonObject o, String path) {
        String content = o.toString();

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);

            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private JsonObject readSettings(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();

        if (fileAsString.isEmpty()) {
            fileAsString = defaults.toString();
            createSettingsFile(defaults, path);
        }

        return PxlsCLI.gson.parse(fileAsString).getAsJsonObject();
    }

    String getTokensFilePath() {
        return sets.get("tokensFilePath").getAsString();
    }
    String getTemplateURI() {
        return sets.get("templateURI").getAsString();
    }
    int getTemplateOffsetX() {
        return sets.get("templateOffsetX").getAsInt();
    }
    int getTemplateOffsetY() {
        return sets.get("templateOffsetY").getAsInt();
    }
    float getTemplateOpacity() {
        return sets.get("templateOpacity").getAsFloat();
    }
    boolean getTemplateReplacePixels() {
        return sets.get("templateReplacePixels").getAsBoolean();
    }

    private JsonObject getControls() {
        return sets.get("controls").getAsJsonObject();
    }
    public int getControlsUp() {
        return parseKeyCode(getControls().get("up").getAsString());
    }
    public int getControlsRight() {
        return parseKeyCode(getControls().get("right").getAsString());
    }
    public int getControlsLeft() {
        return parseKeyCode(getControls().get("left").getAsString());
    }
    public int getControlsDown() {
        return parseKeyCode(getControls().get("down").getAsString());
    }
    public int getControlsZoomIn() {
        return parseKeyCode(getControls().get("zoomIn").getAsString());
    }
    public int getControlsZoomOut() {
        return parseKeyCode(getControls().get("zoomOut").getAsString());
    }
    public int getControlsShift() {
        return parseKeyCode(getControls().get("shift").getAsString());
    }
    public int getControlsCtrl() {
        return  parseKeyCode(getControls().get("ctrl").getAsString());
    }

    private int parseKeyCode(String key) {
        try {
            if (key.isEmpty()) return 0;
            int c = Integer.parseInt(key);

            if (c < 0) return 0;
            if (c > 9) return c;
        } catch (NumberFormatException e) { }

        key = key.toLowerCase();

        switch (key) {
            case "break":           return 3;
            case "backspace":       return 8;
            case "tab":             return 9;
            case "clear":           return 12;
            case "enter":           return 13;
            case "shift":           return 16;
            case "ctrl":            return 17;
            case "alt":             return 18;
            case "pause/break":     return 19;
            case "pausebreak":      return 19;
            case "pause":           return 19;
            case "capslock":        return 20;
            case "escape":          return 27;
            case "spacebar":        return 32;
            case "space":           return 32;
            case " ":               return 32;
            case "pageup":          return 33;
            case "pagedown":        return 34;
            case "end":             return 35;
            case "home":            return 36;
            case "leftarrow":       return 37;
            case "left":            return 37;
            case "uparrow":         return 38;
            case "up":              return 38;
            case "rightarrow":      return 39;
            case "right":           return 39;
            case "downarrow":       return 40;
            case "down":            return 40;
            case "insert":          return 45;
            case "delete":          return 46;
            case "0":               return 48;
            case "1":               return 49;
            case "2":               return 50;
            case "3":               return 51;
            case "4":               return 52;
            case "5":               return 53;
            case "6":               return 54;
            case "7":               return 55;
            case "8":               return 56;
            case "9":               return 57;
            case "a":               return 65;
            case "b":               return 66;
            case "c":               return 67;
            case "d":               return 68;
            case "e":               return 69;
            case "f":               return 70;
            case "g":               return 71;
            case "h":               return 72;
            case "i":               return 73;
            case "j":               return 74;
            case "k":               return 75;
            case "l":               return 76;
            case "m":               return 77;
            case "n":               return 78;
            case "o":               return 79;
            case "p":               return 80;
            case "q":               return 81;
            case "r":               return 82;
            case "s":               return 83;
            case "t":               return 84;
            case "u":               return 85;
            case "v":               return 86;
            case "w":               return 87;
            case "x":               return 88;
            case "y":               return 89;
            case "z":               return 90;
            case "windowskey":      return 91;
            case "leftwindowskey":  return 91;
            case "rightwindowskey": return 92;
            case "selectkey":       return 93;
            case "select":          return 93;
            case "numpad0":         return 96;
            case "numpad1":         return 97;
            case "numpad2":         return 98;
            case "numpad3":         return 99;
            case "numpad4":         return 100;
            case "numpad5":         return 101;
            case "numpad6":         return 102;
            case "numpad7":         return 103;
            case "numpad8":         return 104;
            case "numpad9":         return 105;
            case "num0":            return 96;
            case "num1":            return 97;
            case "num2":            return 98;
            case "num3":            return 99;
            case "num4":            return 100;
            case "num5":            return 101;
            case "num6":            return 102;
            case "num7":            return 103;
            case "num8":            return 104;
            case "num9":            return 105;
            case "multiply":        return 106;
            case "add":             return 107;
            case "subtract":        return 109;
            case "decimalpoint":    return 110;
            case "divide":          return 111;
            case "*":               return 106;
            case "+":               return 107;
            case "-":               return 109;
            case "num.":            return 110;
            case "numdot":          return 110;
            case "/":               return 111;
            case "f1":              return 112;
            case "f2":              return 113;
            case "f3":              return 114;
            case "f4":              return 115;
            case "f5":              return 116;
            case "f6":              return 117;
            case "f7":              return 118;
            case "f8":              return 119;
            case "f9":              return 120;
            case "f10":             return 121;
            case "f11":             return 122;
            case "f12":             return 123;
            case "numlock":         return 144;
            case "scrolllock":      return 145;
            case "semi-colon":      return 186;
            case "semicolon":       return 186;
            case ":":               return 186;
            case "equalsign":       return 187;
            case "equal":           return 187;
            case "=":               return 187;
            case "comma":           return 188;
            case ",":               return 188;
            case "dash":            return 189;
            case ".":               return 190;
            case "forwardslash":    return 191;
            case "graveaccent":     return 192;
            case "`":               return 192;
            case "openbracket":     return 219;
            case "[":               return 219;
            case "backslash":       return 220;
            case "\\":              return 220;
            case "closebraket":     return 221;
            case "]":               return 221;
            case "singlequote":     return 222;
            case "'":               return 222;
            default:                return 0;
        }
    }
}