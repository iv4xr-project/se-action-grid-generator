package generator;

import generator.actions.ActionSequence;
import generator.actions.MAPEActions;
import generator.entities.BlockEntity;
import generator.actions.Action;
import generator.enums.ActionType;

import java.util.*;

public class ActionGridGenerator {

    private MAPEActions mapElite;
    private int numberOfPlayers;
    private final int randomNumber = 2;
    private int counter;
    private final Random rand = new Random();
    private GameMapGenerator grid;
    private int mapeDimension;
    private int chanceOfMutation = 5;
    private int metricOfMutation = 10;

    public ActionGridGenerator(int numberOfPlayers, int mapeDimension, GameMapGenerator grid){
        this.numberOfPlayers = numberOfPlayers;
        counter = 0;
        mapElite = new MAPEActions(mapeDimension, grid.maxX, grid.reachableEntities.size());
        this.mapeDimension = mapeDimension;
        this.grid = grid;
    }

    public void fillActionGrid(int sequenceSize){
        while (mapElite.getMap().size() < mapeDimension * mapeDimension) {
            generateActionSequence(sequenceSize);
        }
    }
    public void generateActionSequence(int length){
        ActionSequence[] offspring = null;
        ActionSequence newActionSeq = null;
        int[] coordinates;
        int x;
        int y;

        if(mapElite.getMap().size() < randomNumber){
            newActionSeq = generateRandom(length, grid);
        } else {
            offspring = generateSelect(length, grid);
        }

        counter++;

        if (offspring != null){
            coordinates = mapElite.behavioralDescriptor(offspring[0]);
            x = coordinates[0];
            y = coordinates[1];

            mapElite.checkAndReplaceElite(x, y, offspring[0]);
            newActionSeq = offspring[1];
        }

        coordinates = mapElite.behavioralDescriptor(newActionSeq);
        x = coordinates[0];
        y = coordinates[1];

        mapElite.checkAndReplaceElite(x, y, newActionSeq);

    }

    public ActionSequence[] generateSelect(int length, GameMapGenerator grid){
        Integer randomPos1 = null;
        Integer randomPos2 = null;

        int mapSize = mapElite.getMap().size();

        Integer[] keyset = mapElite.getMap().keySet().toArray(new Integer[mapSize]);

        randomPos1 = rand.nextInt(mapSize);
        do {
            randomPos2 = rand.nextInt(mapSize);
        } while (randomPos2.equals(randomPos1));

        int chanceOfMutation1 = rand.nextInt(metricOfMutation);
        int chanceOfMutation2 = rand.nextInt(metricOfMutation);


        Integer firstKey = keyset[randomPos1];
        ActionSequence parent1 = new ActionSequence(mapElite.getMap().get(firstKey));

        Integer secondKey = keyset[randomPos2];
        ActionSequence parent2 = new ActionSequence(mapElite.getMap().get(secondKey));

        ActionSequence[] offspring = generateOffspring(parent1, parent2, length);

        if (chanceOfMutation1 < chanceOfMutation)
            offspring[0] = randomMutation(offspring[0], rand.nextInt(length*2), length, grid);
        if (chanceOfMutation2 < chanceOfMutation)
            offspring[1] = randomMutation(offspring[1], rand.nextInt(length*2), length, grid);

        return offspring;
    }

    public ActionSequence[] generateOffspring(ActionSequence parent1, ActionSequence parent2, int length){
        ActionSequence[] offspring = new ActionSequence[2];
        offspring[0] = new ActionSequence(parent1);
        offspring[1] = new ActionSequence(parent2);

        for (int i = 0; i < numberOfPlayers; i++) {
            int crossoverPoint = rand.nextInt(length);
            for (int j = 0; j <= crossoverPoint; j++) {
                offspring[0].getActionsListForPlayers().get(i)[j].copy(parent2.getActionsListForPlayers().get(i)[j]);
                offspring[1].getActionsListForPlayers().get(i)[j].copy(parent1.getActionsListForPlayers().get(i)[j]);
            }
        }

        return offspring;
    }

    public ActionSequence randomMutation(ActionSequence actionSequence, int toMutate, int length, GameMapGenerator grid){
        ActionType[] actionTypes = ActionType.values();
        int upperboundActionOrder = actionTypes.length;
        int randomActionType;
        int randomReachableBlock;
        int offset;
        HashMap<Integer, BlockEntity> reachableEntities = grid.reachableEntities;
        List<Integer> keys = new ArrayList<>(reachableEntities.keySet());

        for (int i = 0; i < toMutate;) {

            int playerNumber = rand.nextInt(numberOfPlayers);
            int position = rand.nextInt(length);

            randomReachableBlock = keys.get(rand.nextInt(keys.size()));
            BlockEntity targetBlock = reachableEntities.get(randomReachableBlock);

            if(targetBlock.getType().isFunctional()){
                offset = 0;
            } else {
                offset = 1;
            }
            randomActionType = rand.nextInt(upperboundActionOrder - offset);

            /*
            - possible improvement for lowering computing time or improving diversity for larger dimensions:
            have a chance of changing every instance of one block in both sequences to the same new block
            ------------------------------------------------------------------------------------------------------------
            boolean everyBlock = rand.nextBoolean();
            if (everyBlock) {
                Action actionPrev = actionSequence.get(playerNumber,position);
                int actionX = actionPrev.getX();
                int actionY = actionPrev.getY();
                int blockX = targetBlock.getxPosition();
                int blockY = targetBlock.getyPosition();

                for(Action action : actionSequence.getActionsListForPlayers().get(playerNumber)){
                    int x = action.getX();
                    int y = action.getY();

                    if (actionX == x && actionY == y){

                        Action newAction = new Action(actionTypes[randomActionType], blockX, blockY);
                        actionSequence.put(playerNumber, position, newAction);
                        i++;
                    }
                }
            } else {
             */

            Action newAction = new Action(actionTypes[randomActionType],
                    targetBlock.getxPosition(), targetBlock.getyPosition());
            actionSequence.put(playerNumber, position, newAction);
            i++;

        }

        return actionSequence;
    }

    public ActionSequence generateRandom(int length, GameMapGenerator grid){
        ActionSequence newActionSeq = new ActionSequence(numberOfPlayers);
        newActionSeq.resetValues(length);
        ActionType[] actionTypes = ActionType.values();
        int upperboundActionOrder = actionTypes.length;
        int randomActionType;
        int randomReachableBlock;
        int offset;

        HashMap<Integer, BlockEntity> reachableEntities = grid.reachableEntities;
        List<Integer> keys = new ArrayList<>(reachableEntities.keySet());

        for (int i = 0; i < length; i++){
            for(int f = 0; f < numberOfPlayers; f++){

                randomReachableBlock = keys.get(rand.nextInt(keys.size()));
                BlockEntity targetBlock = reachableEntities.get(randomReachableBlock);

                if(targetBlock.getType().isFunctional()){
                    offset = 0;
                } else {
                    offset = 1;
                }

                randomActionType = rand.nextInt(upperboundActionOrder - offset);
                Action newAction = new Action(actionTypes[randomActionType],
                        targetBlock.getxPosition(), targetBlock.getyPosition());

                newActionSeq.put(f, i, newAction);
            }
        }

        return newActionSeq;
    }

    public MAPEActions getMapElite() {
        return mapElite;
    }
}
