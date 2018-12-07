import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class SingleRandomizerThread extends Thread {
    private Game game;
    
    @Override
    public synchronized void run() {
        game.actualButtonList.clear();
        int rand = (3 + (int) (Math.random() * ((6 - 3) + 1))) * 1000;
        try {
            sleep(rand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rand = 1 + (int) (Math.random() * 4);
        while (game.actualButtonList.size() < rand) {
            int randButton = 1 + (int) (Math.random() * 16);
            if (!game.actualButtonList.contains(randButton)) game.actualButtonList.add(randButton);
        }
        if(game.singleplayerIsRunning) {
            for (Integer i : game.actualButtonList) {
                JButton tmpButton = game.buttonList.get(i - 1);
                tmpButton.setEnabled(true);
                tmpButton.setBackground(Color.GREEN);
            }
        }
        game.timestamp = new Timestamp(System.currentTimeMillis());
    }
    
    public void setCallBack(Game game){
        this.game = game;
    }
}
