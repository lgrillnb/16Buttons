import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat extends JFrame {

    private JPanel mainpanel;
    private JPanel subpanel;
    private JTextField input;
    private JButton butsend;
    private JTextPane chathistory;

    public Chat(Game cb){
        this.setTitle("Chat");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocation(cb.getLocation().x + cb.getWidth() - 10, cb.getLocation().y);

        input = new JTextField();
        chathistory = new JTextPane();
        chathistory.setEditable(false);
        butsend = new JButton("SEND");
        butsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Message einfuegen
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

    /**
     * Refresh chathistory
     * @param message
     */
    public void setChathistory(String message){

    }
}
