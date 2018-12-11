import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputWindow extends JFrame {

    protected JTextField name;
    protected JTextField host;
    protected JTextField port;
    private JLabel nameLabel;
    private JLabel hostLabel;
    private JLabel portLabel;
    private JButton okButton;
    private JPanel panel;
    private ActionListener saveListener;

    public InputWindow() {
        this.setTitle("Multiplayer Configuration");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        nameLabel = new JLabel("Enter your name");
        hostLabel = new JLabel("Enter your host-adress");
        portLabel = new JLabel("Enter your host-port");
        name = new JTextField();
        host = new JTextField();
        port = new JTextField();
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String tempname = name.getText();
                if(tempname.contains("(") || tempname.contains(")") || tempname.contains("[") || tempname.contains("]")
                        || tempname.contains("{") || tempname.contains("}")){
                    name.setText("");
                }else {
                    InputWindow.this.setVisible(false);
                    saveListener.actionPerformed(new ActionEvent(InputWindow.this, 0, ""));
                }
            }
        });
        okButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n'){
                    okButton.doClick();
                }
            }
        });

        panel = new JPanel(new GridLayout(4,2));
        panel.add(nameLabel);
        panel.add(name);
        panel.add(hostLabel);
        panel.add(host);
        panel.add(portLabel);
        panel.add(port);
        panel.add(new JPanel());
        panel.add(okButton);
        this.add(panel);

        this.setVisible(true);
    }

    public void setListener(ActionListener listener){
        saveListener = listener;
    }
}
