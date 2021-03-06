import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class Chat extends JFrame {

    private JPanel mainpanel;
    private JPanel subpanel;
    private JPanel suggespanel;
    private JTextField input;
    private JButton butsend;
    private JButton butbusted;
    private JButton butfail;
    private JButton butlooser;
    private JButton butdestroyed;
    private JButton butknockout;
    private JTextArea chathistory;
    private JScrollPane scpane;
    private ActionListener sendListener;

    public Chat(Game cb){
        this.setTitle("Chat");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocation(cb.getLocation().x + cb.getWidth() - 10, cb.getLocation().y);

        input = new JTextField();
        chathistory = new JTextArea();
        chathistory.setEditable(false);
        scpane = new JScrollPane(chathistory,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        DefaultCaret caret = (DefaultCaret)chathistory.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        butsend = new JButton("SEND");
        butsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //BadwordFilter
                try {
                    String tmp = input.getText();
                    FileReader fr = new FileReader(new File("badwords.txt"));
                    BufferedReader br = new BufferedReader(fr);
                    String stg;
                    while ((stg = br.readLine()) !=  null){
                        tmp = tmp.toUpperCase();
                        if(tmp.contains(stg)){
                            input.setText("no bad words");
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + input.getText()));
                input.setText("");
            }
        });
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n'){
                    butsend.doClick();
                    input.requestFocus();
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
        mainpanel.add(scpane, BorderLayout.CENTER);

        suggespanel = new JPanel( new FlowLayout());
        butbusted = new JButton("BUSTED");
        butfail = new JButton("FAIL");
        butlooser = new JButton("LOOSER");
        butdestroyed = new JButton("DESTROYED");
        butknockout = new JButton("KNOCK OUT");
        suggespanel.add(butbusted);
        suggespanel.add(butfail);
        suggespanel.add(butlooser);
        suggespanel.add(butdestroyed);
        suggespanel.add(butknockout);
        subpanel.add(suggespanel, BorderLayout.NORTH);

        butbusted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + "BUSTED"));
            }
        });
        butfail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + "FAIL"));
            }
        });
        butlooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + "LOOSER"));
            }
        });
        butdestroyed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + "I HAVE DESTROYED YOU"));
            }
        });
        butknockout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendListener.actionPerformed(new ActionEvent(this, 0, "chat;" + "YOU GOT KNOCKED OUT"));
            }
        });

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
