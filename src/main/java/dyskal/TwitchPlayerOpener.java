package dyskal;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static javax.swing.Box.createHorizontalBox;
import static javax.swing.Box.createVerticalBox;

    public class TwitchPlayerOpener extends JFrame {
        public TwitchPlayerOpener() {
            super("Twitch Player Opener");
            this.setPreferredSize(new Dimension(600, 400));
            this.setIconImage(new ImageIcon((Objects.requireNonNull(getClass().getClassLoader().getResource("assets/icon.png")))).getImage());



            JsonManager jsonManager = new JsonManager();

            //<editor-fold desc="Parameters">
            Box parameters = createHorizontalBox();
            final String[] parametersUsed = {"&enableExtensions=true","&muted=false","&volume=0.5"};
            //<editor-fold desc="Extensions">
            JCheckBox extensions = new JCheckBox("Enable Extensions ?", true);
            extensions.addItemListener(event -> {
                int state = event.getStateChange();
                if (state== ItemEvent.SELECTED){
                    parametersUsed[0] = "&enableExtensions=true";
                } else {
                    parametersUsed[0] =  "&enableExtensions=false";
                }
            });
            //</editor-fold>
            //<editor-fold desc="Muted">
            JCheckBox muted = new JCheckBox("Mute Stream ?", false);
            muted.addItemListener(event -> {
                int state = event.getStateChange();
                if (state==ItemEvent.SELECTED){
                    parametersUsed[1] = "&muted=true";
                } else {
                    parametersUsed[1] = "&muted=false";
                }
            });
            //</editor-fold>
            parameters.add(extensions);
            parameters.add(muted);
            //</editor-fold>

            //<editor-fold desc="Volume Parameters">
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
                float volume = (float)volumeSlider.getValue() / 100;
                parametersUsed[2]= "&volume="+volume;
                volumeLabel.setText("Volume is at "+volumeSlider.getValue()+"%");
            });
            volumeParameters.add(volumeSlider);
            volumeParameters.add(spacer);
            volumeParameters.add(volumeLabel);
            //</editor-fold>

            //<editor-fold desc="Streamers List">
            Box base = createHorizontalBox();
            JComboBox<String> streamerList = new JComboBox<>(jsonManager.getStreamers().toArray(new String[0]));
            streamerList.setEditable(true);
            streamerList.setMaximumSize(new Dimension(200, 100));
            //</editor-fold>

            //<editor-fold desc="Button Open">
            JButton buttonOpen = new JButton("Open");
            buttonOpen.addActionListener(event -> {
                String selectedStreamer = ((String) streamerList.getSelectedItem());
                boolean value = jsonManager.getStreamers().contains(selectedStreamer);
                if (!value) {
                    streamerList.addItem(selectedStreamer);
                    jsonManager.addStreamers(selectedStreamer);
                }
                String parametersSelected = parametersUsed[0]+parametersUsed[1]+"&player=popout"+parametersUsed[2];
                String finalUrl = "https://player.twitch.tv/?channel="+selectedStreamer+parametersSelected;
                File chrome64 = new File(System.getenv("ProgramFiles(x86)")+"\\Google\\Chrome\\Application");
                File chrome32 = new File(System.getenv("ProgramFiles")+"\\Google\\Chrome\\Application");
                if(chrome64.exists()){
                    try {
                        Runtime.getRuntime().exec(chrome64+"\\chrome.exe"+" "+"--app="+finalUrl+" "+"--disable-extensions");
                    } catch (IOException e) {
                        try {
                            Desktop.getDesktop().browse(new URL((finalUrl)).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (chrome32.exists()){
                    try {
                        Runtime.getRuntime().exec(chrome32+"\\chrome.exe"+" "+"--app="+finalUrl+" "+"--disable-extensions");
                    } catch (IOException e) {
                        try {
                            Desktop.getDesktop().browse(new URL((finalUrl)).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Desktop.getDesktop().browse(new URL((finalUrl)).toURI());
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            JButton remove = new JButton("Remove");
            remove.addActionListener(event -> {
                String selectedStreamer = (String) streamerList.getSelectedItem();
                streamerList.removeItem(selectedStreamer);
                jsonManager.removeStreamers(selectedStreamer);
            });
            base.add(streamerList);
            base.add(spacer);
            base.add(buttonOpen);
            base.add(remove);
            //</editor-fold>

            //<editor-fold desc="Body">
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
            //</editor-fold>

            this.add(body);
            this.pack();
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
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