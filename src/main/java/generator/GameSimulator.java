package generator;


import generator.actions.ActionSequence;
import generator.actions.MAPEActions;
import generator.bugs.ConcurrentBlockBug;
import generator.bugs.ConnectedBlocksBug;
import generator.bugs.SingularBlockBug;
import generator.entities.BlockEntity;
import generator.actions.Action;

import java.util.*;

public class GameSimulator {

    GameMapGenerator originalWM;
    GameMapGenerator simulatedWM;
    Integer[] keySet;
    List<SingularBlockBug> singularBlockBugList;
    List<ConcurrentBlockBug> concurrentBlockBugList;
    List<ConnectedBlocksBug> connectedBlocksBugList;
    HashMap<Integer, Integer> singularBlockBugsDetected;
    HashMap<Integer, Integer> concurrentBlockBugsDetected;
    HashMap<Integer, Integer> connectedBlocksBugsDetected;

    public GameSimulator(GameMapGenerator gameMapGenerator, int numberOfBugs){
        originalWM = gameMapGenerator;
        simulatedWM = null;
        HashMap<Integer, BlockEntity> reachableEntities = originalWM.reachableEntities;
        int length = reachableEntities.size();
        keySet = reachableEntities.keySet().toArray(new Integer[length]);
        singularBlockBugsDetected = new HashMap<>();
        concurrentBlockBugsDetected = new HashMap<>();
        connectedBlocksBugsDetected = new HashMap<>();
        this.generateBugs(numberOfBugs);
    }

    private void generateBugs(int numberOfBugs) {
        HashMap<Integer, BlockEntity> reachableEntities = originalWM.reachableEntities;
        int bugTypes = 3;
        int modulus = numberOfBugs % bugTypes;
        int division = numberOfBugs / bugTypes;
        Random rand = new Random();
        int length = keySet.length;

        singularBlockBugList = new ArrayList<>();
        int carryOver = 0;
        if (modulus > 0){
            carryOver = 1;
            modulus--;
        }
        for (int i = 0; i < division + carryOver; i++){
            int blockPos = rand.nextInt(length);
            SingularBlockBug newBug = new SingularBlockBug(reachableEntities.get(keySet[blockPos]));
            singularBlockBugList.add(newBug);
        }

        carryOver = 0;
        if (modulus > 0){
            carryOver = 1;
            modulus--;
        }
        concurrentBlockBugList = new ArrayList<>();
        for (int i = 0; i < division + carryOver; i++){
            int blockPos = rand.nextInt(length);
            ConcurrentBlockBug newBug = new ConcurrentBlockBug(reachableEntities.get(keySet[blockPos]));
            concurrentBlockBugList.add(newBug);
        }

        connectedBlocksBugList = new ArrayList<>();
        for (int i = 0; i < division; i++){
            int blockPos1 = rand.nextInt(length);
            int blockPos2;
            do {
                blockPos2 = rand.nextInt(length);
            } while (blockPos2 == blockPos1);

            ConnectedBlocksBug newBug = new ConnectedBlocksBug(reachableEntities.get(keySet[blockPos1])
                    , reachableEntities.get(keySet[blockPos2]));
            connectedBlocksBugList.add(newBug);
        }
    }

    public void simulate(MAPEActions mapeActions, boolean resetBugsDetected){

        if (resetBugsDetected){
            singularBlockBugsDetected = new HashMap<>();
            concurrentBlockBugsDetected = new HashMap<>();
            connectedBlocksBugsDetected = new HashMap<>();
        }
        int dimension = mapeActions.getDimension();

        for (int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){

                simulatedWM = new GameMapGenerator(originalWM);
                ActionSequence actionSequence = mapeActions.get(i,j);
                int length = actionSequence.getLength();
                int numberOfPlayers = actionSequence.getNumberOfPlayers();

                for (int k = 0; k < length; k++) {
                    HashMap<Integer, BlockEntity> blocksInteracted = new HashMap<>();

                    for (int p = 0; p < numberOfPlayers; p++) {
                        Action action = actionSequence.get(p,k);
                        int x = action.getX();
                        int y = action.getY();

                        BlockEntity block = simulatedWM.getReachableEntity(x, y);
                        block.applyAction(action);
                        blocksInteracted.put(simulatedWM.getArrayPosition(x,y), block);
                    }

                    checkSingularBlockBugs(blocksInteracted);
                    checkConcurrentBlockBugs(blocksInteracted);
                    checkConnectedBlocksBugs();

                    //reset inUse states
                    for(BlockEntity blockEntity : blocksInteracted.values()){
                        blockEntity.setInUse(false);
                    }
                }
            }
        }

        simulatedWM = new GameMapGenerator(originalWM);
    }

    public void checkSingularBlockBugs(HashMap<Integer, BlockEntity> blocks){
        int size = blocks.size();
        Integer[] keyset = blocks.keySet().toArray(new Integer[size]);

        for (Integer key : keyset){
            BlockEntity block = blocks.get(key);

            for(int i = 0; i < singularBlockBugList.size(); i++){
                SingularBlockBug bug = singularBlockBugList.get(i);
                BlockEntity bugBlock = bug.getBlock();

                if(block.getxPosition().equals(bugBlock.getxPosition()) &&
                block.getyPosition().equals(bugBlock.getyPosition())){

                    if (compareBlocks(bugBlock, block)){

                        if(singularBlockBugsDetected.containsKey(i)){
                            int value = singularBlockBugsDetected.get(i);
                            singularBlockBugsDetected.put(i,++value);
                        } else {
                            singularBlockBugsDetected.put(i,1);
                        }
                    }
                }
            }
        }
    }

    public void checkConcurrentBlockBugs(HashMap<Integer, BlockEntity> blocks) {
        int size = blocks.size();
        Integer[] keyset = blocks.keySet().toArray(new Integer[size]);

        if (blocks.size() == 1){
            BlockEntity block = blocks.get(keyset[0]);

            for(int i = 0; i < concurrentBlockBugList.size(); i++){
                ConcurrentBlockBug bug = concurrentBlockBugList.get(i);
                BlockEntity bugBlock = bug.getBlock();

                if (bugBlock.getxPosition().equals(block.getxPosition()) &&
                        bugBlock.getyPosition().equals(block.getyPosition())){

                    if (compareBlocks(bugBlock, block)){

                        if(concurrentBlockBugsDetected.containsKey(i)){
                            int value = concurrentBlockBugsDetected.get(i);
                            concurrentBlockBugsDetected.put(i,++value);
                        } else {
                            concurrentBlockBugsDetected.put(i,1);
                        }
                    }
                }
            }
        }
    }

    public void checkConnectedBlocksBugs(){
        HashMap<Integer, BlockEntity> reachableEntities = simulatedWM.reachableEntities;

        for (int i = 0; i < connectedBlocksBugList.size(); i++) {
            ConnectedBlocksBug bug = connectedBlocksBugList.get(i);
            BlockEntity bugBlock1 = bug.getBlock1();
            BlockEntity bugBlock2 = bug.getBlock2();

            int position1 = simulatedWM.getArrayPosition(bugBlock1.getxPosition(), bugBlock1.getyPosition());
            int position2 = simulatedWM.getArrayPosition(bugBlock2.getxPosition(), bugBlock2.getyPosition());

            BlockEntity block1 = reachableEntities.get(position1);
            BlockEntity block2 = reachableEntities.get(position2);

            if(compareBlocks(block1, bugBlock1) && compareBlocks(block2, bugBlock2)){
                if(connectedBlocksBugsDetected.containsKey(i)){
                    int value = connectedBlocksBugsDetected.get(i);
                    connectedBlocksBugsDetected.put(i,++value);
                } else {
                    connectedBlocksBugsDetected.put(i,1);
                }
            }
        }
    }

    public boolean compareBlocks(BlockEntity block1, BlockEntity block2){
        return block1.getIntegrity() == block2.getIntegrity() &&
                block1.getNumberOfUses() == block2.getNumberOfUses() &&
                block1.isInUse() == block2.isInUse();
    }
}
