package dyskal;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static dyskal.TomlManager.cleaner;
import static java.awt.Component.CENTER_ALIGNMENT;
import static javax.swing.Box.createHorizontalBox;
import static javax.swing.Box.createVerticalBox;

public class TwitchPlayerOpener {
    public TwitchPlayerOpener() {
        JFrame frame = new JFrame();
        frame.setTitle("Twitch Player Opener");
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/icon.png"))).getImage());

        TomlManager tomlManager = new TomlManager();
        TwitchManager twitchManager = new TwitchManager();

        Box parameters = createHorizontalBox();
        final String[] parametersUsed = {"&enableExtensions=true", "&muted=false", "&volume=0.5"};

        JCheckBox extensions = new JCheckBox("Enable Extensions ?", true);
        extensions.addItemListener(event -> {
            int state = event.getStateChange();
            if (state == ItemEvent.SELECTED) {
                parametersUsed[0] = "&enableExtensions=true";
            } else {
                parametersUsed[0] = "&enableExtensions=false";
            }
        });

        JCheckBox muted = new JCheckBox("Mute Stream ?", false);
        muted.addItemListener(event -> {
            int state = event.getStateChange();
            if (state == ItemEvent.SELECTED) {
                parametersUsed[1] = "&muted=true";
            } else {
                parametersUsed[1] = "&muted=false";
            }
        });

        parameters.add(extensions);
        parameters.add(muted);

        Box volumeParameters = createVerticalBox();
        JLabel volumeLabel = new JLabel();
        JLabel spacer = new JLabel();
        JSlider volumeSlider = new JSlider(0, 100, 50);
        spacer.setText("                      ");
        volumeLabel.setText("Volume is at 50%");
        volumeLabel.setFont(volumeLabel.getFont().deriveFont(15f));
        volumeLabel.setAlignmentX(CENTER_ALIGNMENT);
        volumeSlider.setMaximumSize(new Dimension(500, 20));
        volumeSlider.addChangeListener(event -> {
            float volume = (float) volumeSlider.getValue() / 100;
            parametersUsed[2] = "&volume=" + volume;
            volumeLabel.setText("Volume is at " + volumeSlider.getValue() + "%");
        });
        volumeParameters.add(volumeSlider);
        volumeParameters.add(spacer);
        volumeParameters.add(volumeLabel);

        Box base = createHorizontalBox();
        JComboBox<String> streamerList = new JComboBox<>(twitchManager.getStreamers().toArray(new String[0]));
        streamerList.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        streamerList.setEditable(true);
        streamerList.setMaximumSize(new Dimension(200, 100));

        JButton buttonOpen = new JButton("Open");
        buttonOpen.addActionListener(event -> {
            String selectedStreamer = cleaner(((String) Objects.requireNonNull(streamerList.getSelectedItem())));
            boolean value = tomlManager.getStreamers().contains(selectedStreamer);
            if (!value) {
                streamerList.addItem(selectedStreamer);
                tomlManager.addStreamers(selectedStreamer);
            }
            String parametersSelected = parametersUsed[0] + parametersUsed[1] + "&player=popout" + parametersUsed[2] + "&parent=dyskal";
            String finalUrl = "https://player.twitch.tv/?channel=" + selectedStreamer + parametersSelected;
            File chrome64 = new File(System.getenv("ProgramFiles(x86)") + "\\Google\\Chrome\\Application");
            File chrome32 = new File(System.getenv("ProgramFiles") + "\\Google\\Chrome\\Application");
            if (chrome64.exists()) {
                try {
                    Runtime.getRuntime().exec(chrome64 + "\\chrome.exe" + " " + "--app=" + finalUrl + " " + "--disable-extensions");
                } catch (IOException e) {
                    try {
                        Desktop.getDesktop().browse(new URI(finalUrl));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (chrome32.exists()) {
                try {
                    Runtime.getRuntime().exec(chrome32 + "\\chrome.exe" + " " + "--app=" + finalUrl + " " + "--disable-extensions");
                } catch (IOException e) {
                    try {
                        Desktop.getDesktop().browse(new URI(finalUrl));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                try {
                    Desktop.getDesktop().browse(new URI(finalUrl));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton remove = new JButton("Remove");
        remove.addActionListener(event -> {
            String selectedStreamer = (String) streamerList.getSelectedItem();
            streamerList.removeItem(selectedStreamer);
            tomlManager.removeStreamers(Objects.requireNonNull(selectedStreamer));
        });
        base.add(streamerList);
        base.add(spacer);
        base.add(buttonOpen);
        base.add(remove);

        Box body = createVerticalBox();
        Dimension minSize = new Dimension(10, 20);
        Dimension prefSize = new Dimension(20, 20);
        Dimension maxSize = new Dimension(50, 70);
        body.add(new Box.Filler(minSize, prefSize, maxSize));
        body.add(base);
        body.add(new Box.Filler(minSize, prefSize, maxSize));
        body.add(parameters);
        body.add(new Box.Filler(minSize, prefSize, maxSize));
        body.add(volumeParameters);
        body.add(new Box.Filler(minSize, prefSize, maxSize));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                tomlManager.fileCleaner();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                tomlManager.fileCleaner();
            }
        });

        frame.add(body);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(TwitchPlayerOpener::new);
    }
}