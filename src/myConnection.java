import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class myConnection {

    private DataOutputStream out;
    private DataInputStream in;
    private boolean running = false;
    private List<ActionListener> alList = new ArrayList<>();
    private int clickDuration;
    private String name;
    private int score;
    private boolean isReady = false;

    public myConnection(Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startConn() {
        new Thread(() -> {
            running = true;
            while (running) {
                try {
                    String input = in.readUTF();
                    notifyClientListener(input);
                } catch (IOException e) {
                   myConnection.this.close();
                }
            }
        }).start();
    }

    public void addActionListener(ActionListener al) {
        alList.add(al);
    }

    public void notifyClientListener(String msg) {
        for (ActionListener al : alList) {
            al.actionPerformed(new ActionEvent(this, 1, msg));
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            myConnection.this.close();
        }
    }

    public void close() {
        running = false;
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClickDuration(int duration){
        clickDuration = duration;
    }
    public int getClickDuration(){
        return clickDuration;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setScore(int score){ this.score = score;}
    public int getScore(){ return this.score;}
    public void setIsReady(boolean isReady){ this.isReady = isReady;}
    public boolean getIsReady(){ return this.isReady;}
}
