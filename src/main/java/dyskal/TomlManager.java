package dyskal;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TomlManager {
    File dir = new File(System.getenv("APPDATA")+"\\Twitch Player Opener");
    File file = new File(dir+"\\streamers.toml");
    FileConfig config = FileConfig.of(file);
    private List<String> streamers = new ArrayList<>();
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

    public void TomlWriter(){
        config.remove("streamers");
        config.add("streamers", streamers);
        config.save();
    }

    public void makeFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        if (!file.exists() || recreate || br.readLine() == null) {
            List<String> placeholder = new ArrayList<>();
            placeholder.add("placeholder");
            IGNORE_RESULT(dir.mkdir());
            IGNORE_RESULT(file.createNewFile());
            config.set("streamers", placeholder);
            config.save();
            recreate = false;
        }
    }

    @SuppressWarnings("unused")
    private static void IGNORE_RESULT(boolean b){}

    public ArrayList<String> getStreamers() {
        return (ArrayList<String>) streamers;
    }

    public void addStreamers(String newStreamer){
        streamers.add(newStreamer);
        TomlWriter();
    }

    public void removeStreamers(String newStreamer){
        streamers.remove(newStreamer);
        TomlWriter();
    }
}