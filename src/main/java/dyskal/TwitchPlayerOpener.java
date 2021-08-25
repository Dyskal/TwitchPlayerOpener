package dyskal;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.Box.Filler;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static dyskal.TomlManager.cleaner;
import static java.awt.Desktop.getDesktop;
import static java.awt.event.ItemEvent.SELECTED;
import static java.lang.Runtime.getRuntime;
import static java.util.Objects.requireNonNull;
import static javax.swing.Box.createHorizontalBox;
import static javax.swing.Box.createVerticalBox;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.setLookAndFeel;

class TwitchPlayerOpener extends JFrame {
    private TwitchPlayerOpener() {
        super("Twitch Player Opener");
        setPreferredSize(new Dimension(600, 400));
        ArrayList<Image> icons = new ArrayList<>();
        for (String size : new String[]{"","16x16", "20x20", "24x24", "30x30", "31x31", "32x32", "40x40", "48x48", "60x60", "64x64", "96x96", "120x120", "256x256"}) {
            icons.add(new ImageIcon(requireNonNull(getClass().getClassLoader().getResource("assets/icon"+size+".png"))).getImage());
        }
        setIconImages(icons);

        TomlManager tomlManager = new TomlManager();

        Box parameters = createHorizontalBox();
        final String[] parametersUsed = {"&enableExtensions=true", "&muted=false", "&volume=0.5"};

        JCheckBox extensions = new JCheckBox("Enable Extensions ?", true);
        extensions.addItemListener(event -> parametersUsed[0] = event.getStateChange() == SELECTED ? "&enableExtensions=true" : "&enableExtensions=false");

        JCheckBox muted = new JCheckBox("Mute Stream ?", false);
        muted.addItemListener(event -> parametersUsed[1] = event.getStateChange() == SELECTED ? "&muted=true" : "&muted=false");

        parameters.add(extensions);
        parameters.add(muted);

        Box volumeParameters = createVerticalBox();
        JLabel volumeLabel = new JLabel("Volume is at 50%");
        JLabel spacer = new JLabel("                      ");
        JSlider volumeSlider = new JSlider(0, 100, 50);

        volumeLabel.setFont(volumeLabel.getFont().deriveFont(15f));
        volumeLabel.setAlignmentX(CENTER_ALIGNMENT);

        volumeSlider.setForeground(Color.decode("#9146FF"));
        volumeSlider.setMaximumSize(new Dimension(500, 20));
        volumeSlider.addChangeListener(event -> {
            parametersUsed[2] = "&volume=" + (float) volumeSlider.getValue() / 100;
            volumeLabel.setText("Volume is at " + volumeSlider.getValue() + "%");
        });

        volumeParameters.add(volumeSlider);
        volumeParameters.add(spacer);
        volumeParameters.add(volumeLabel);

        Box base = createHorizontalBox(); //TODO change size arrow
        JComboBox<String> streamerList = new JComboBox<>(new TwitchManager().getStreamers().toArray(new String[0]));
        streamerList.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12)); //TODO multiplatform font
        streamerList.setEditable(true);
        streamerList.setMaximumSize(new Dimension(200, 100));

        JButton buttonOpen = new JButton("Open");
        buttonOpen.addActionListener(event -> {
            String selectedStreamer = cleaner(((String) requireNonNull(streamerList.getSelectedItem())));
            boolean value = tomlManager.getStreamers().contains(selectedStreamer);
            if (!value) {
                streamerList.addItem(selectedStreamer);
                tomlManager.addStreamers(selectedStreamer);
            }

            //TODO multi/platform browser
            String parametersSelected = parametersUsed[0] + parametersUsed[1] + "&player=popout" + parametersUsed[2] + "&parent=dyskal";
            String finalUrl = "https://player.twitch.tv/?channel=" + selectedStreamer + parametersSelected;
            File chrome86 = new File(System.getenv("ProgramFiles(x86)") + "\\Google\\Chrome\\Application");
            File chrome = new File(System.getenv("ProgramFiles") + "\\Google\\Chrome\\Application");
            System.out.println("célong");
            if (chrome.exists()) {
                try {
                    getRuntime().exec(chrome + "\\chrome.exe" + " " + "--app=" + finalUrl + " " + "--disable-extensions");
                } catch (IOException e) {
                    try {
                        getDesktop().browse(new URI(finalUrl));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (chrome86.exists()) {
                try {
                    getRuntime().exec(chrome86 + "\\chrome.exe" + " " + "--app=" + finalUrl + " " + "--disable-extensions");
                } catch (IOException e) {
                    try {
                        getDesktop().browse(new URI(finalUrl));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                try {
                    getDesktop().browse(new URI(finalUrl));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton remove = new JButton("Remove");
        remove.addActionListener(event -> {
            String selectedStreamer = (String) streamerList.getSelectedItem();
            streamerList.removeItem(selectedStreamer);
            tomlManager.removeStreamers(requireNonNull(selectedStreamer));
        });

        base.add(streamerList);
        base.add(spacer);
        base.add(buttonOpen);
        base.add(remove);

        Box body = createVerticalBox();
        Dimension minSize = new Dimension(10, 20);
        Dimension prefSize = new Dimension(20, 20);
        Dimension maxSize = new Dimension(50, 70);

        body.add(new Filler(minSize, prefSize, maxSize));
        body.add(base);
        body.add(new Filler(minSize, prefSize, maxSize));
        body.add(parameters);
        body.add(new Filler(minSize, prefSize, maxSize));
        body.add(volumeParameters);
        body.add(new Filler(minSize, prefSize, maxSize));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                tomlManager.fileCleaner();
            }

            //TODO clean typos
            @Override
            public void windowClosing(WindowEvent e) {
                tomlManager.fileCleaner();
            }
        });

        add(body);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String... args) {
        try {
            setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        invokeLater(TwitchPlayerOpener::new);
    }
}