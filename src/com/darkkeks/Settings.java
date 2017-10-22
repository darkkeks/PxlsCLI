package com.darkkeks;

import java.io.*;
import com.google.gson.*;

public class Settings {

    private static JsonObject sets;

    public Settings(final String path) {
        try {
            readSettings(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSettings(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();

        sets = PxlsCLI.gson.parse(fileAsString).getAsJsonObject();
    }

    public String getTokensFilePath() {
        return sets.get("tokensFilePath").getAsString();
    }
    public String getTemplateURI() {
        return sets.get("templateURI").getAsString();
    }
    public int getTemplateOffsetX() {
        return sets.get("templateOffsetX").getAsInt();
    }
    public int getTemplateOffsetY() {
        return sets.get("templateOffsetY").getAsInt();
    }
    public int getTemplateOpacity() {
        return sets.get("templateOpacity").getAsInt();
    }

    public JsonObject getControls() {
        return sets.get("controls").getAsJsonObject();
    }
    public int getControlsUp() {
        return sets.get("controls").getAsJsonObject().get("up").getAsInt();
    }
    public int getControlsRight() {
        return sets.get("controls").getAsJsonObject().get("right").getAsInt();
    }
    public int getControlsLeft() {
        return sets.get("controls").getAsJsonObject().get("left").getAsInt();
    }
    public int getControlsDown() {
        return sets.get("controls").getAsJsonObject().get("down").getAsInt();
    }
    public int getControlsZoomIn() {
        return sets.get("controls").getAsJsonObject().get("zoomIn").getAsInt();
    }
    public int getControlsZoomOut() {
        return sets.get("controls").getAsJsonObject().get("zoomOut").getAsInt();
    }
    public int getControlsShift() {
        return sets.get("controls").getAsJsonObject().get("shift").getAsInt();
    }
    public int getControlsCtrl() {
        return  sets.get("controls").getAsJsonObject().get("ctrl").getAsInt();
    }
}