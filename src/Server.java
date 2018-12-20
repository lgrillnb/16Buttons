import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int port;
    private List<myConnection> connList = new ArrayList<>();
    private List<Integer> actualButtonList = new ArrayList<>();
    private boolean running = false;
    private int readyCounter = 0;
    private int finishedClickCounter = 0;
    private ActionListener serverListner = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            System.out.println("Nachricht empfangen: " + ae.getActionCommand());
            myConnection conn = (myConnection) ae.getSource();
            if(ae.getActionCommand().startsWith("ready")){
                readyCounter++;
                conn.setIsReady(true);
                int index = connList.indexOf(conn);
                boolean ready = false;
                for(int i = 0; i < connList.size(); i++){
                    if(i != index){
                        connList.get(i).sendMessage("partner is ready:" + conn.getName());
                    }
                    ready = true;
                    if(!connList.get(i).getIsReady()) ready = false;
                }
                if(ready && readyCounter > 1){
                    calcDelayAndButons();
                }
            } else if(ae.getActionCommand().startsWith("my name")){
                conn.setName(ae.getActionCommand().split(":")[1]);
                int index = connList.indexOf(conn);
                for(int i = 0; i < connList.size(); i++){
                    if(i != index){
                        connList.get(i).sendMessage("new partner:" + conn.getName());   //alle anderen erfahren es gibt einen neuen
                    }
                }
            } else if(ae.getActionCommand().startsWith("finished clicking")){
                finishedClickCounter++;
                conn.setClickDuration(Integer.parseInt(ae.getActionCommand().split(":")[1]));
                if(finishedClickCounter == connList.size() && finishedClickCounter > 1) {
                    try {
                        //get the current winner
                        finishedClickCounter = 0;
                        int value = 1000000;
                        String winner = "";
                        for(myConnection tmp_conn : connList){
                            if(value > tmp_conn.getClickDuration()) {
                                value = tmp_conn.getClickDuration();
                                winner = tmp_conn.getName();
                            }
                        }
                        String score = "";
                        //sets the actual score
                        for (myConnection connec: connList) {
                            if(connec.getName() == winner){
                                connec.setScore(connec.getScore()+1);
                            }
                            //build score-string
                            score += connec.getName() + "-" + connec.getScore() + ";";
                        }
                        //current winner of this round
                        for(myConnection Conn : connList){
                            Conn.sendMessage("winner:" + winner + ";" + value);
                            Conn.sendMessage("score:" + score);
                        }
                        System.out.println("SERVER: sent winner:" + winner + ";" + value);
                        System.out.println("SERVER: sent score:" + score);
                        Thread.sleep(2000);
                        calcDelayAndButons();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if(finishedClickCounter == connList.size()){
                    finishedClickCounter = 0;
                }
            } else if(ae.getActionCommand().startsWith("exit connection")){
                connList.remove(conn);
                conn.close();
                if(readyCounter > 0) readyCounter--;
                if(finishedClickCounter > 0) finishedClickCounter--;
                System.out.println("SERVER: Client exits connection");
                for(myConnection Conn : connList){
                    Conn.sendMessage("partner stopped:" + conn.getName());
                }
            } else if(ae.getActionCommand().startsWith("chat")){
                for (myConnection Conn: connList) {
                    Conn.sendMessage("chat;" + conn.getName().split("\\(")[0]+ ": " + '\t' + ae.getActionCommand().split(";")[1]);
                }

            }
        }
    };

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        running = true;
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("SERVER: Server started");
                while (running) {
                    myConnection conn = new myConnection(server.accept());
                    conn.addActionListener(serverListner);
                    for(myConnection Conn : connList){
                        conn.sendMessage("new partner:" + Conn.getName());   //informiere mich welche partner es schon gibt
                        if(Conn.getIsReady()) conn.sendMessage("partner is ready:" + Conn.getName());
                    }
                    connList.add(conn);
                    System.out.println("SERVER: new connection");
                    conn.startConn();
                }
                for (myConnection conn : connList) {
                    conn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public static void main(String[] args) {
        Server s = new Server(1234);
        s.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (!input.equalsIgnoreCase("stop")){
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.stop();
        System.out.println("SERVER: Server stopped");
    }

    public void calcDelayAndButons() {
        actualButtonList.clear();
        int delay = (3 + (int) (Math.random() * ((6 - 3) + 1))) * 1000;
        int rand = 1 + (int) (Math.random() * 4);
        while (actualButtonList.size() < rand) {
            int randButton = 1 + (int) (Math.random() * 16);
            if (!actualButtonList.contains(randButton)) actualButtonList.add(randButton);
        }
        //werte an alle conn's schreiben
        String data = "";
        for (int i = 0; i < actualButtonList.size(); i++) {
            if (i < actualButtonList.size() - 1) data += actualButtonList.get(i).toString() + ";";
            else data += actualButtonList.get(i).toString();
        }
        for (myConnection conn : connList) {
            conn.sendMessage("delay:" + delay);
            conn.sendMessage("data:" + data);
            conn.sendMessage("start");
        }
        System.out.println("SERVER: sent delay, data and start");
    }

    public void stop(){
        running = false;
    }

}