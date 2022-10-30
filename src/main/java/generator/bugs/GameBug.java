package generator.bugs;

import generator.entities.BlockEntity;

import java.util.Random;

public class GameBug {

    public void randomizeInternalStates(BlockEntity block){
        if(block.getType().isFunctional()){

        } else {

        }

        Random rand = new Random();
        int randIntegrity = rand.nextInt(5);
        boolean randInUse;
        int randUses;
        if(block.getType().isFunctional()){
            randInUse = rand.nextBoolean();
            randUses = rand.nextInt(6);
        } else {
            randInUse = false;
            randUses = 0;
        }

        block.setIntegrity(100 - 25*randIntegrity);
        block.setInUse(randInUse);
        block.setNumberOfUses(randUses);
    }
}
