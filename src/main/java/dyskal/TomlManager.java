package dyskal;

import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TomlManager {
    File dir = new File(System.getenv("APPDATA")+"\\Dyskal\\Twitch Player Opener");
    File file = new File(dir+"\\streamers.toml");
    FileConfig config = FileConfig.of(file);
    private ArrayList<String> streamers = new ArrayList<>();
    private boolean recreate = false;

    public TomlManager(){
        try {
            makeFile();
            config.load();
            streamers = config.get("streamers");
        } catch (Exception e) {
            e.printStackTrace();
            recreate = true;
            try {
                makeFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void TomlCleanup(){
        streamers.remove(" ");
        streamers.forEach(string -> {
            if (!string.matches("^[a-zA-Z_0-9]+$")){
                streamers.set(streamers.indexOf(string), string.replaceAll("\\W+", ""));
            }
        });
    }

    public static String cleanup(String string) {
        String last;
        if (string.matches("^[a-zA-Z_0-9]+$")) {
            last = string;
        } else {
            last = string.replaceAll("\\W+", "");
        }
        return last;
    }

    public void TomlWriter(){
        config.remove("streamers");
        TomlCleanup();
        config.add("streamers", streamers);
        config.save();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void makeFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        if (!file.exists() || recreate || br.readLine() == null) {
            List<String> placeholder = new ArrayList<>();
            placeholder.add("placeholder");
            dir.mkdir();
            file.createNewFile();
            config.set("streamers", placeholder);
            config.save();
            recreate = false;
        }
    }

    public ArrayList<String> getStreamers() {
        return streamers;
    }

    public void addStreamers(String newStreamer){
        streamers.add(newStreamer);
        TomlWriter();
        new TwitchManager();
    }

    public void removeStreamers(String newStreamer){
        streamers.remove(newStreamer);
        TomlWriter();
        new TwitchManager();
    }
}