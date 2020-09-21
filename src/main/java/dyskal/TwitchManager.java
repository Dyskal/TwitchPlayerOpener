package dyskal;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.UserList;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import java.util.ArrayList;

public class TwitchManager {
    private final ArrayList<String> streamers = new ArrayList<>();

    public TwitchManager() {
        TwitchClient twitchClient = TwitchClientBuilder.builder().withEnableHelix(true).withClientId("dvzvtw5bg1mgiehkx773oqbc4eudmi").build();
        TomlManager tomlManager = new TomlManager();
        ArrayList<String> listName = tomlManager.getStreamers();
        BidiMap<String,String> nameIdDict = new DualHashBidiMap<>();

        tomlManager.fileCleaner();

        String token = "3jleck9uxq0v3l94rmcg08x616mxmr";
        UserList idByUser = twitchClient.getHelix().getUsers(token,null, listName).execute();
        idByUser.getUsers().forEach(users -> nameIdDict.put(users.getDisplayName(), users.getId()));

        StreamList isOnline = twitchClient.getHelix().getStreams(token, "", "", null, null, null, null, null, listName).execute();
        isOnline.getStreams().forEach(stream -> streamers.add(nameIdDict.getKey(stream.getUserId())));

        listName.forEach(item -> {
            if (streamers.contains(item)){
                streamers.remove(item);
                streamers.add(addEmoji(item, "\u2714"));
            } else {
                streamers.add(addEmoji(item, "\u274C"));
            }
        });
    }

    public String addEmoji(String str, String add) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(str.length(), " "+add);
        return sb.toString();
    }

    public ArrayList<String> getStreamers() { return streamers; }
}
