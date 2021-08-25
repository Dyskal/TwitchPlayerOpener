package dyskal;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import com.github.twitch4j.helix.domain.User;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.getenv;
import static java.util.stream.Collectors.toMap;

class TwitchManager {
    private final ArrayList<String> streamers = new ArrayList<>();

    TwitchManager() {
        TwitchHelix twitchClient = TwitchHelixBuilder.builder().withClientId(getenv("CLIENT_ID")).withClientSecret(getenv("CLIENT_SECRET")).build();
        TomlManager tomlManager = new TomlManager();

        tomlManager.fileCleaner();
        HashMap<String, String> nameIdDict = twitchClient.getUsers("", null, tomlManager.getStreamers()).execute()
                .getUsers().stream().collect(toMap(User::getId, User::getDisplayName, (a, b) -> a, HashMap::new));

        twitchClient.getStreams("", "", "", null, null, null, new ArrayList<>(nameIdDict.keySet()), null).execute()
                .getStreams().forEach(stream -> streamers.add(stream.getUserName()));

        nameIdDict.values().forEach(item -> {
            if (streamers.contains(item)) {
                streamers.remove(item);
                streamers.add(addEmoji(item, "\u2714"));
            } else {
                streamers.add(addEmoji(item, "\u274C"));
            }
        });
    }

    private static String addEmoji(String str, String add) {
        return new StringBuilder(str).insert(str.length(), " " + add).toString();
    }

    ArrayList<String> getStreamers() {
        return streamers;
    }
}
