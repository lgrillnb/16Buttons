import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Chat extends JFrame {

    private JPanel mainpanel;
    private JPanel subpanel;
    private JTextField input;
    private JButton butsend;
    private JTextArea chathistory;
    private ActionListener sendListener;

    public Chat(Game cb){
        this.setTitle("Chat");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocation(cb.getLocation().x + cb.getWidth() - 10, cb.getLocation().y);

        input = new JTextField();
        chathistory = new JTextArea();
        chathistory.setEditable(false);
        butsend = new JButton("SEND");
        butsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Message einfuegen
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + input.getText()));
                input.setText("");
            }
        });
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n'){
                    butsend.doClick();
                }
            }
        });
        butsend.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n'){
                    butsend.doClick();
                }
            }
        });

        mainpanel = new JPanel(new BorderLayout());
        subpanel = new JPanel(new BorderLayout());
        subpanel.add(input, BorderLayout.CENTER);
        subpanel.add(butsend, BorderLayout.EAST);
        mainpanel.add(subpanel, BorderLayout.SOUTH);
        mainpanel.add(chathistory, BorderLayout.CENTER);
        this.add(mainpanel);
        this.setVisible(true);

    }

    public void setChathistory(String message){
        chathistory.append(message + "\n");
    }

    public void setSendListener(ActionListener sendListener){
        this.sendListener = sendListener;
    }

    public void setFocusOnInput() {
        this.input.requestFocus();
    }
}
