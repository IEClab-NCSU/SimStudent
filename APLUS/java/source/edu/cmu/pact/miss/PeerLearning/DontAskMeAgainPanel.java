package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DontAskMeAgainPanel extends JPanel {

    private JCheckBox dontAskMeAgain;

    public DontAskMeAgainPanel(Object message) {
        setLayout(new BorderLayout());
        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            add(new JLabel(message.toString()));
        }
        dontAskMeAgain = new JCheckBox("Don't ask me again");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(dontAskMeAgain);
        add(panel, BorderLayout.SOUTH);
    }

    public boolean dontAskMeAgain() {
        return dontAskMeAgain.isSelected();
    }

    private static Properties settings;

   



    public static int showConfirmDialog(Component parent, Object message, String key) {

      //  loadProperties();

        int result = JOptionPane.NO_OPTION;

      //  if (settings.containsKey(key + ".prompt") && !Boolean.parseBoolean(settings.getProperty(key + ".value"))) {
       //     result = Integer.parseInt(settings.getProperty(key + ".value"));
        //} else {
            DontAskMeAgainPanel panel = new DontAskMeAgainPanel(message);
            result = JOptionPane.showConfirmDialog(parent, panel);
            if (panel.dontAskMeAgain()) {
                settings.put(key + ".prompt", "false");
                settings.put(key + ".value", Integer.toString(result));

               
          //  }
        }
        return result;
    }

}
