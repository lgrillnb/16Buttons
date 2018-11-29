import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private int port;
    private String host;
    protected static List<Integer> actualButtonList = new ArrayList<>();
    protected static int delay = 0;
    private myConnection conn;
    private ActionListener winnerListener;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            conn = new myConnection(new Socket(host, port));
            conn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getMessage(ae.getActionCommand());
                }
            });
            conn.startConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMessage(String msg) {
        System.out.println(msg);
        if (msg.startsWith("delay")) {
            delay = Integer.parseInt(msg.split(":")[1]);
            if(Game.readyPartnerLabel.getForeground() != Color.GREEN){
                Game.readyPartnerLabel.setForeground(Color.GREEN);
            }
        } else if (msg.startsWith("data")) {
            actualButtonList.clear();
            String[] split = msg.split(":");
            while (split[1].contains(";")) {
                actualButtonList.add(Integer.parseInt(split[1].split(";", 2)[0]));
                split[1] = split[1].split(";", 2)[1];
            }
            actualButtonList.add(Integer.parseInt(split[1]));
        } else if (msg.startsWith("start")) {
            MultiRandomizerThread myThread = new MultiRandomizerThread();
            myThread.start();
        } else if(msg.startsWith("partner is ready")){
            Game.readyPartnerLabel.setForeground(Color.GREEN);
            //int index = Game.actualPlayerList.indexOf(msg.split(":")[1].split("\\(")[0]);
            //Game.actualPlayerList.get(index).setBackground(Color.GREEN);
        } else if(msg.startsWith("new partner")){
            Game.readyPartnerLabel.setForeground(Color.ORANGE);
            Game.actualPlayerList.add(msg.split(":")[1].split("\\(")[0]);
            //int index = Game.actualPlayerList.indexOf(msg.split(":")[1].split("\\(")[0]);
            //Game.actualPlayerList.get(index).setBackground(Color.ORANGE);
        } else if(msg.startsWith("partner stopped")){
            Game.readyPartnerLabel.setForeground(Color.RED);
            int index = Game.actualPlayerList.indexOf(msg.split(":")[1].split("\\(")[0]);
            Game.actualPlayerList.remove(index);
        } else if(msg.startsWith("winner")){
            String winner = msg.split(":")[1].split(";")[0];
            int winnerTime = Integer.parseInt(msg.split(":")[1].split(";")[1]);
            winnerListener.actionPerformed(new ActionEvent(this, 0, winner + ":" + winnerTime));
        } else if(msg.startsWith("score")){
            String str = msg.split(":")[1];
            while(str.contains(";")){
                String tmpStr = str.split(";")[0];
                int index = Game.actualPlayerList.indexOf(tmpStr.split("-")[0]); //Name
                String tmp = Game.actualPlayerList.get(index);
                tmp = (tmpStr.split("-")[0].split("\\(")[0]+" ("+tmpStr.split("-")[1]+")"); //sets new Score to name
                str = str.split(";")[1];
            }
        }
    }

    public void sendMessage(String msg) {
        conn.sendMessage(msg);
    }

    public void stop() {
        conn.close();
    }

    public void setWinnerListener(ActionListener listener){
        winnerListener = listener;
    }
}
