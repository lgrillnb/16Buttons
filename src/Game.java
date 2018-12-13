import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

public class Game extends JFrame {

    private JPanel buttonPanel;
    private JPanel timePanel;
    private JLabel timeLabel;
    private JMenuBar menuBar;
    private JMenu menu_modi;
    private JMenu menu_showCurrent;
    private JMenuItem menuItemSingle;
    private JMenuItem menuItemMulti;
    private JButton readyButton;
    protected JList<String> playerList;
    protected List<JButton> buttonList = new ArrayList<>();
    protected List<Integer> actualButtonList = new ArrayList<>();
    protected Timestamp timestamp;
    private int myExit;
    protected Chat chathistory;
    private boolean startFirstClick = true;
    protected boolean singleplayerIsRunning = false;
    protected boolean multiplayerIsRunning = false;
    private SingleRandomizerThread SingleRandomizerThread;
    private Client client;
    private String myName;
    private String winner;
    private int winnerTime;
    private ActionListener winnerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            winner = ae.getActionCommand().split(":")[0];
            winnerTime = Integer.parseInt(ae.getActionCommand().split(":")[1]);
            JOptionPane pane;
            JDialog dialog;
            if(winner.contentEquals(myName)){
                pane = new JOptionPane("You are the winner!!!  time["+ winnerTime +"]", JOptionPane.INFORMATION_MESSAGE);
                dialog = pane.createDialog(null, "Game Info");
                dialog.setModal(false);
                dialog.setVisible(true);
            } else{
                pane = new JOptionPane(winner.split("\\(")[0] +" is the winner!!!!  time["+ winnerTime +"]", JOptionPane.INFORMATION_MESSAGE);
                dialog = pane.createDialog(null, "Game Info");
                dialog.setModal(false);
                dialog.setVisible(true);
            }
            new Timer(2000, e -> dialog.setVisible(false)).start();
        }
    };

    public Game() {
        this.setTitle("16 Buttons Game");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        addCompToButtonPanel();

        menuBar = new JMenuBar();
        menu_modi = new JMenu("Modus");
        menu_showCurrent = new JMenu("Current Modi: Single-player");
        menu_showCurrent.setEnabled(false);
        menuItemSingle = new JMenuItem("Single-player");
        menuItemMulti = new JMenuItem("Multi-player");
        menu_modi.add(menuItemSingle);
        menu_modi.add(menuItemMulti);
        menuBar.add(menu_modi);
        menuBar.add(menu_showCurrent);

        timeLabel = new JLabel("To start select a game-modi");
        readyButton = new JButton("NOT READY");
        readyButton.setVisible(false);
        timePanel = new JPanel(new FlowLayout());
        timePanel.add(readyButton);
        timePanel.add(timeLabel);
        playerList = new JList<>(new DefaultListModel<>());
        playerList.setFixedCellWidth(100);
        playerList.setVisible(false);

        this.add(menuBar, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(timePanel, BorderLayout.SOUTH);
        this.add(playerList, BorderLayout.EAST);

        addAllActionListeners();
        this.setVisible(true);
    }

    private void addAllActionListeners() {
        for (JButton button : buttonList) {
            button.addActionListener(ae -> {
                button.setEnabled(false);
                button.setBackground(Color.WHITE);
                myExit++;
                if (myExit == actualButtonList.size()) {
                    myExit = 0;
                    timeLabel.setText(((new Timestamp(System.currentTimeMillis())).getTime() - timestamp.getTime()) + " ms");
                    if(menu_showCurrent.getText() == "Current Modi: Single-player") {
                        startSingleplayer();
                    }else {
                        client.sendMessage("finished clicking:"+timeLabel.getText().split(" ")[0]);
                    }
                }
            });
        }
        menuItemSingle.addActionListener(ae -> {
            if (startFirstClick) {
                startFirstClick = false;
                startSingleplayer();
                timeLabel.setText("Single-player starts now");
            } else {
                if(multiplayerIsRunning){
                    stopMultiplayer();
                    startSingleplayer();
                    timeLabel.setText("Single-player starts now");
                } else if(!singleplayerIsRunning){
                    startSingleplayer();
                    timeLabel.setText("Single-player starts now");
                }
            }
        });
        menuItemMulti.addActionListener(ae -> {
            if (startFirstClick) {
                startFirstClick = false;
                startMultiplayer();
            } else {
                if(singleplayerIsRunning) {
                    stopSingleplayer();
                    startMultiplayer();
                }
            }
        });
        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if(multiplayerIsRunning && readyButton.getText() == "NOT READY"){
                    readyButton.setText("READY");
                    client.sendMessage("ready");
                    ((DefaultListModel<String>)playerList.getModel()).setElementAt(myName.split("\\(")[0] + " [ready]", 0);
                }else if (multiplayerIsRunning && readyButton.getText() == "READY") {
                    readyButton.setText("NOT READY");
                    stopMultiplayer();
                }
            }
        });
        this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    stopMultiplayer();
                }
                @Override
                public void windowDeiconified(WindowEvent windowEvent) {
                    if (chathistory != null) {
                        chathistory.setLocation(Game.this.getLocation().x + Game.this.getWidth() - 10, Game.this.getLocation().y);
                        chathistory.setState(JFrame.NORMAL);
                    }
                }
                @Override
                public void windowIconified(WindowEvent windowEvent) {
                    if (chathistory != null) {
                        chathistory.setState(Frame.ICONIFIED);
                    }
                }
            });
        this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent componentEvent) {
                    if (chathistory != null) {
                        chathistory.setLocation(Game.this.getLocation().x + Game.this.getWidth() - 10, Game.this.getLocation().y);
                    }
                }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == ' '){
                    chathistory.setFocusOnInput();
                }
            }
        });
    }

    private void addCompToButtonPanel() {
        buttonPanel = new JPanel(new GridLayout(4, 4));

        for (int i = 0; i < 16; i++) {
            buttonList.add(new JButton());
        }
        for (JButton button : buttonList) {
            buttonPanel.add(button);
            button.setEnabled(false);
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
        }
    }

    private void startSingleplayer() {
        SingleRandomizerThread = new SingleRandomizerThread();
        SingleRandomizerThread.setCallBack(this);
        SingleRandomizerThread.start();
        singleplayerIsRunning = true;
        menu_showCurrent.setText("Current Modi: Single-player");
    }

    private void stopSingleplayer() {
        myExit = 0;
        for (JButton button : buttonList) {
            button.setEnabled(false);
            button.setBackground(Color.WHITE);
        }
        timeLabel.setText("Single-player stopped");
        singleplayerIsRunning = false;
    }

    private void startMultiplayer() {
        InputWindow iw = new InputWindow();
        iw.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    InputWindow tmp = (InputWindow) ae.getSource();
                    myName = tmp.name.getText() + "(" + Math.round(Math.random() * 1000) + ")";
                    client = new Client(tmp.host.getText(), Integer.parseInt(tmp.port.getText()));
                    client.setWinnerListener(winnerListener);
                    ((DefaultListModel<String>) playerList.getModel()).addElement(myName.split("\\(")[0]);
                    client.start(Game.this);
                    client.sendMessage("my name:" + myName);
                    menu_showCurrent.setText("Current Modi: Multi-player");
                    timeLabel.setText("Multi-player starts now");
                    readyButton.setVisible(true);
                    playerList.setVisible(true);
                    multiplayerIsRunning = true;
                    chathistory = new Chat(Game.this);
                    chathistory.setSendListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            client.sendMessage(actionEvent.getActionCommand());
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                    timeLabel.setText("-- Failed to connect --");
                }
            }
        });
    }

    public void stopMultiplayer(){
        client.sendMessage("exit connection");
        if (client != null) client.stop();
        multiplayerIsRunning = false;
        myExit = 0;
        for (JButton button : buttonList) {
            button.setEnabled(false);
            button.setBackground(Color.WHITE);
        }
        timeLabel.setText("Multi-player stopped");
        readyButton.setText("NOT READY");
        readyButton.setVisible(false);
        this.playerList.setVisible(false);
        ((DefaultListModel<String>)this.playerList.getModel()).removeAllElements();

        chathistory.setVisible(false);
    }

    public static void main(String[] args) {
        new Game();
    }

}