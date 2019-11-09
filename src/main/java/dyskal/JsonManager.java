package dyskal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonManager {
    private ArrayList<String> streamers = new ArrayList<>();
    Gson gson = new Gson();
    File dir = new File(System.getenv("APPDATA")+"\\Twitch Player Opener");
    File file = new File(dir+"\\streamers.json");
    private boolean recreate = false;

    public JsonManager() {
        try {
            makeDir();
            JsonReader reader = new JsonReader(new FileReader(file));
            Type streamersType = new TypeToken<ArrayList<String>>(){}.getType();
            streamers = gson.fromJson(reader, streamersType);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            recreate = true;
            try {
                makeDir();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void makeDir() throws IOException {
        if ((!file.exists() || recreate)) {
            IGNORE_RESULT(dir.mkdir());
            IGNORE_RESULT(file.createNewFile());
            Writer writerCreator = new FileWriter(file);
            Gson creator = new GsonBuilder().setPrettyPrinting().create();
            String[] base = {"placeholder"};
            creator.toJson(base, writerCreator);
            writerCreator.close();
            recreate=false;
        }
    }

    public void JsonWriter(){
        try {
            Writer writer = new FileWriter(file);
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            builder.toJson(streamers, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unused")
    private static void IGNORE_RESULT(boolean b){}

    public ArrayList<String> getStreamers(){
        return streamers;
    }

    public void addStreamers(String newStreamer) {
        streamers.add(newStreamer);
        JsonWriter();
    }

    public void removeStreamers(String newStreamer){
        streamers.remove(newStreamer);
        JsonWriter();
    }

}