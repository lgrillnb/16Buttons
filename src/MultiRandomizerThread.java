import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class MultiRandomizerThread extends Thread {
    @Override
    public synchronized void run() {
        Game.actualButtonList = Client.actualButtonList;
        try {
            sleep(Client.delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(Game.multiplayerIsRunning) {
            for (Integer i : Game.actualButtonList) {
                JButton tmpButton = Game.buttonList.get(i - 1);
                tmpButton.setEnabled(true);
                tmpButton.setBackground(Color.GREEN);
            }
        }
        Game.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
