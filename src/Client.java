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

    public void start(Game cb) {
        try {
            conn = new myConnection(new Socket(host, port));
            conn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    getMessage(ae.getActionCommand(), cb);
                }
            });
            conn.startConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMessage(String msg, Game cb) {
        System.out.println(msg);
        if (msg.startsWith("delay")) {
            delay = Integer.parseInt(msg.split(":")[1]);
        }
        else if (msg.startsWith("data")) {
            actualButtonList.clear();
            String[] split = msg.split(":");
            while (split[1].contains(";")) {
                actualButtonList.add(Integer.parseInt(split[1].split(";", 2)[0]));
                split[1] = split[1].split(";", 2)[1];
            }
            actualButtonList.add(Integer.parseInt(split[1]));
        }
        else if (msg.startsWith("start")) {
            MultiRandomizerThread myThread = new MultiRandomizerThread();
            myThread.start();
        }
        else if(msg.startsWith("partner is ready")){
            /*String partner = msg.split(":")[1].split("\\(")[0];
            int index = 0;
            for(int i=0; i<cb.playerList.getModel().getSize(); i++){
                if(cb.playerList.getModel().getElementAt(i).startsWith(partner)){
                    index = i;
                }
            }
            ((DefaultListModel<String>)cb.playerList.getModel()).get(index); */  //todo: Hintergrund- oder Fontfarbe soll sich ändern
        }
        else if(msg.startsWith("new partner")){
            ((DefaultListModel<String>)cb.playerList.getModel()).addElement(msg.split(":")[1].split("\\(")[0]);
        }
        else if(msg.startsWith("partner stopped")){
            int index = 0;
            for(int i=0; i<cb.playerList.getModel().getSize(); i++){
                if(cb.playerList.getModel().getElementAt(i).startsWith(msg.split(":")[1].split("\\(")[0])){
                    index = i;
                }
            }
            ((DefaultListModel<String>)cb.playerList.getModel()).remove(index);
        }
        else if(msg.startsWith("winner")){
            String winner = msg.split(":")[1].split(";")[0];
            int winnerTime = Integer.parseInt(msg.split(":")[1].split(";")[1]);
            winnerListener.actionPerformed(new ActionEvent(this, 0, winner + ":" + winnerTime));
        }
        else if(msg.startsWith("score")){
            String str = msg.split(":")[1];
            while(str.contains(";")){
                String tmpStr = str.split(";")[0];
                int index = 0;
                for(int i=0; i<cb.playerList.getModel().getSize(); i++){
                    if(cb.playerList.getModel().getElementAt(i).startsWith(tmpStr.split("\\(")[0])){
                        index = i;
                    }
                }
                String tmp = (tmpStr.split("-")[0].split("\\(")[0]+" ("+tmpStr.split("-")[1]+")"); //sets new Score to name
                ((DefaultListModel<String>)cb.playerList.getModel()).setElementAt(tmp, index);
                str = str.split(";",2)[1];
            }
        }

        /**
         * else if fuer den Chat
         * über callback zurückgreifen
         * cb.blabla.FUNKTION (z.B sendMessage, addMessage
         * callback zurueckgeben an GAME, anschließend and Chat
         * weitergeben und chathistory weiter auffuellen
         */
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
