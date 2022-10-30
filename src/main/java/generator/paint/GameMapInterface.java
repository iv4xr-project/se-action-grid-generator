package generator.paint;

import generator.entities.BlockEntity;
import generator.entities.Entity;
import generator.entities.PlayerEntity;
import generator.enums.PlayerNumber;

import java.util.List;
import java.awt.*;
import javax.swing.*;
public class GameMapInterface extends JFrame {

    List<Entity> entities;
    List<BlockEntity> interactableBlocks;

    public GameMapInterface(List<Entity> entities, List<BlockEntity> interactableBlocks) {
        setSize(400, 400);
        setVisible(true);
        this.entities = entities;
        this.interactableBlocks = interactableBlocks;
    }

    public void paint(Graphics g) {
        for (int x = 50; x <= 320; x += 30)
            for (int y = 50; y <= 320; y += 30){
                Color color = null;
                String text = "";
                int realX = (x-50) / 30;
                int realY = (y-50) / 30;
                for (Entity entity : entities){
                    if (entity.getxPosition() == realX && entity.getyPosition() == realY){
                        if (entity instanceof PlayerEntity) {
                            if (((PlayerEntity) entity).getPlayerNumber().equals(PlayerNumber.P1)) {
                                text = "P1";
                                color = Color.blue;
                            } else {
                                text = "P2";
                                color = Color.red;
                            }
                        } else {

                            if (interactableBlocks.contains((BlockEntity) entity))color = Color.green;
                            else color = Color.orange;

                            text = ((BlockEntity) entity).getTypeCode().toString();

                        }
                    }
                }
                int xOffset = 0;
                if (text.length() > 2) xOffset += 3.5;

                if (color != null){
                    g.setColor(color);
                    g.fillRect(x, y, 30, 30);
                } else {
                    g.drawRect(x, y, 30, 30);

                }
                g.setColor(Color.BLACK);
                g.drawString(text, x + 10 - xOffset, y + 20);
            }

    }
}