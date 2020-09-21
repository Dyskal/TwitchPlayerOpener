package dyskal;

import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TomlManager {
    private final File dir = new File(System.getenv("APPDATA")+"\\Dyskal\\TwitchPlayerOpener");
    private final File file = new File(dir+"\\streamers.toml");
    private final FileConfig config = FileConfig.of(file);
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void makeFile() throws IOException {
        if (!dir.exists() || !file.exists() || recreate) {
            List<String> placeholder = new ArrayList<>();
            placeholder.add("placeholder");
            dir.mkdirs();
            file.createNewFile();
            config.set("streamers", placeholder);
            config.save();
            recreate = false;
        }
    }

    public void fileCleaner(){
        streamers.remove(" ");
        streamers.forEach(string -> {
            if (!string.matches("^[a-zA-Z_0-9]+$")){
                streamers.set(streamers.indexOf(string), string.replaceAll("\\W+", ""));
            }
        });
    }

    public static String cleaner(String string) {
        String last;
        if (string.matches("^[a-zA-Z_0-9]+$")) {
            last = string;
        } else {
            last = string.replaceAll("\\W+", "");
        }
        return last;
    }

    public void writer(){
        config.remove("streamers");
        fileCleaner();
        config.add("streamers", streamers);
        config.save();
    }

    public ArrayList<String> getStreamers() {
        return streamers;
    }

    public void addStreamers(String newStreamer){
        streamers.add(newStreamer);
        writer();
        new TwitchManager();
    }

    public void removeStreamers(String newStreamer){
        streamers.remove(newStreamer.replaceAll("\\W+", ""));
        writer();
        new TwitchManager();
    }
}