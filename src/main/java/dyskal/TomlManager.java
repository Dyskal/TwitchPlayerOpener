package dyskal;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static net.harawata.appdirs.AppDirsFactory.getInstance;

class TomlManager {
    private final File dir = new File(getInstance().getUserConfigDir("TwitchPlayerOpener", null, "Dyskal", true));
    private final File file = new File(dir + "\\streamers.toml");
    private final FileConfig config = FileConfig.of(file);
    private ArrayList<String> streamers = new ArrayList<>();
    private boolean recreate = false;

    TomlManager() {
        try {
            makeFile();
        } catch (IOException e) {
            e.printStackTrace();
            recreate = true;
            try {
                makeFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static String cleaner(String string) {
        return string.matches("^[a-zA-Z_0-9]+$") ? string : string.replaceAll("\\W+", "");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeFile() throws IOException {
        if (!recreate && dir.exists() && file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                if (br.readLine() != null && br.readLine().isEmpty()) {
                    config.load();
                    streamers = config.get("streamers");
                    return;
                }
            }
        }
        ArrayList<String> placeholder = new ArrayList<>();
        placeholder.add("placeholder");
        dir.mkdirs();
        file.createNewFile();
        config.set("streamers", placeholder);
        config.save();

        config.load();
        streamers = config.get("streamers");
        recreate = false;
    }

    void fileCleaner() {
        streamers.remove(" ");
        streamers.stream().filter(string -> !string.matches("^[a-zA-Z_0-9]+$"))
                .forEach(string -> streamers.set(streamers.indexOf(string), string.replaceAll("\\W+", "")));
    }

    private void writer() {
        config.remove("streamers");
        fileCleaner();
        config.add("streamers", streamers);
        config.save();
    }

    ArrayList<String> getStreamers() {
        return streamers;
    }

    void addStreamers(String newStreamer) {
        streamers.add(newStreamer);
        writer();
        new TwitchManager();
    }

    void removeStreamers(String newStreamer) {
        streamers.remove(newStreamer.replaceAll("\\W+", ""));
        writer();
        new TwitchManager();
    }
}