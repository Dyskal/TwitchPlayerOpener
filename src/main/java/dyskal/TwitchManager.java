package dyskal;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;

import java.util.ArrayList;
import java.util.HashMap;

import static com.github.twitch4j.TwitchClientBuilder.builder;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.toMap;

class TwitchManager {
    private final ArrayList<String> streamers = new ArrayList<>();

    TwitchManager() {
        TwitchClient twitchClient = builder().withEnableHelix(true).withClientId(getenv("CLIENT_ID")).withClientSecret(getenv("CLIENT_SECRET")).build();
        TomlManager tomlManager = new TomlManager();

        tomlManager.fileCleaner();
        HashMap<String, String> nameIdDict = twitchClient.getHelix().getUsers("", null, tomlManager.getStreamers()).execute()
                .getUsers().stream().collect(toMap(User::getId, User::getDisplayName, (a, b) -> a, HashMap::new));

        twitchClient.getHelix().getStreams("", "", "", null, null, null, new ArrayList<>(nameIdDict.keySet()), null).execute()
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
