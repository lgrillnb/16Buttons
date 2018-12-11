import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class MultiRandomizerThread extends Thread {
    private Game game;
    private Client client;
    
    @Override
    public synchronized void run() {
        game.actualButtonList = client.actualButtonList;
        try {
            sleep(client.delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(game.multiplayerIsRunning) {
            for (Integer i : game.actualButtonList) {
                JButton tmpButton = game.buttonList.get(i - 1);
                tmpButton.setEnabled(true);
                tmpButton.setBackground(Color.GREEN);
            }
        }
        game.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public void setCallBack(Game game, Client client){
        this.game = game;
        this.client = client;
    }
}
