import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class SingleRandomizerThread extends Thread {
    @Override
    public synchronized void run() {
        Game.actualButtonList.clear();
        int rand = (3 + (int) (Math.random() * ((6 - 3) + 1))) * 1000;
        try {
            sleep(rand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rand = 1 + (int) (Math.random() * 4);
        while (Game.actualButtonList.size() < rand) {
            int randButton = 1 + (int) (Math.random() * 16);
            if (!Game.actualButtonList.contains(randButton)) Game.actualButtonList.add(randButton);
        }
        if(Game.singleplayerIsRunning) {
            for (Integer i : Game.actualButtonList) {
                JButton tmpButton = Game.buttonList.get(i - 1);
                tmpButton.setEnabled(true);
                tmpButton.setBackground(Color.GREEN);
            }
        }
        Game.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
