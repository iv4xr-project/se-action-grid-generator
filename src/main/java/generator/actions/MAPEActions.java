package generator.actions;

import generator.DiversityCalculator;
import generator.enums.ActionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MAPEActions {

    private int dimension;
    private int gridDimension;
    private int interactableEntitiesSize;
    private int actionSequenceLength;
    List<Integer> unusedPositions;
    private int numberOfPlayers;
    private final int numberOfActionTypes = ActionType.values().length;
    private HashMap<Integer, ActionSequence> map;
    private DiversityCalculator fitnessCalculator = new DiversityCalculator();

    public MAPEActions(int dimension, int gridDimension, int interactableEntitiesSize) {
        this.dimension = dimension;
        this.gridDimension = gridDimension;
        map = new HashMap<>();
        unusedPositions = new ArrayList<>();
        for (int i = 0; i < dimension*dimension; i++) {
            unusedPositions.add(i);
        }
        this.interactableEntitiesSize = interactableEntitiesSize;
    }

    public int[] behavioralDescriptor(ActionSequence actionSequence) {
        int[] coordinates = new int[2];
        int x;
        int y;
        numberOfPlayers = actionSequence.getNumberOfPlayers();
        int length = actionSequence.getLength();
        actionSequenceLength = length;
        int totalActions = numberOfPlayers * length;
        HashMap<Integer, Integer> numberOfCoordinatedActions = new HashMap<>();
        int[] numberOfInteractiveActions = new int[numberOfActionTypes];

        for (int i = 0; i < length; i++) {
            for (int f = 0; f < numberOfPlayers; f++) {

                Action action = actionSequence.getActionsListForPlayers().get(f)[i];

                int position = coordinatesToOneAxisGrid(action.getX(), action.getY());
                if (numberOfCoordinatedActions.containsKey(position)){
                    int value = numberOfCoordinatedActions.get(position);
                    numberOfCoordinatedActions.put(position, ++value);
                } else {
                    numberOfCoordinatedActions.put(position, 1);
                }

                numberOfInteractiveActions[action.getActionType().ordinal()]++;

            }
        }

        x = getPositionForCoordinationAxis(numberOfCoordinatedActions, totalActions);
        y = getPositionForRepetitionAxis(numberOfInteractiveActions, totalActions);

        coordinates[0] = x;
        coordinates[1] = y;

        return coordinates;
    }

    public int getPositionForCoordinationAxis(HashMap<Integer, Integer> numberOfCoordinatedActions, int totalActions) {
        List<Integer> keys = new ArrayList<>(numberOfCoordinatedActions.keySet());

        int maxCoordinatedAction = numberOfCoordinatedActions.get(keys.get(0));
        for (int i = 1; i < keys.size(); i++) {
            int compare = numberOfCoordinatedActions.get(keys.get(i));
            if (maxCoordinatedAction < compare) {
                maxCoordinatedAction = compare;
            }
        }
        return checkPositionByFractionX(maxCoordinatedAction, totalActions);
    }

    public int getPositionForRepetitionAxis(int[] numberOfInteractiveActions, int totalActions) {
        int maxInteractiveAction = numberOfInteractiveActions[0];
        for (int i = 1; i < ActionType.values().length; i++) {
            if (maxInteractiveAction < numberOfInteractiveActions[i]) {
                maxInteractiveAction = numberOfInteractiveActions[i];
            }
        }
        return checkPositionByFractionY(maxInteractiveAction, totalActions);
    }

    public int coordinatesToOneAxis(int x, int y) {
        return x + y * dimension;
    }

    public int coordinatesToOneAxisGrid(int x, int y) {
        return x + y * gridDimension;
    }

    public int[] oneAxisToCoordinates(int position) {
        int[] response = new int[2];
        response[0] = position % dimension;
        response[1] = position / dimension;
        return response;
    }

    private int checkPositionByFractionX(float dividend, float divisor) {
        int position;
        float separator;
        float fraction = dividend / divisor;

        int totalActions = (actionSequenceLength*numberOfPlayers);
        double minFraction;

        if(totalActions > interactableEntitiesSize){
            int modulus = totalActions % interactableEntitiesSize;
            int correction = modulus > 0 ? 1 : 0;
            int minNumberOfBlocks = totalActions / interactableEntitiesSize + correction;

            minFraction =  (double) minNumberOfBlocks / totalActions;
        } else {
            minFraction = 0d;
        }

        double diff = 1 - minFraction;
        double offset = diff / dimension;
        position = 0;
        do {
            position++;
            separator = (float) (minFraction + offset * position);

        } while(fraction > separator);
        position--;

        return position;
    }

    private int checkPositionByFractionY(float dividend, float divisor) {
        int position;
        float separator;

        float fraction = dividend / divisor;

        int totalActions = (actionSequenceLength*numberOfPlayers);
        int modulus = totalActions % numberOfActionTypes;
        int correction = modulus > 0 ? 1 : 0;
        int minNumberOfReps = totalActions / numberOfActionTypes + correction;

        double minFraction =  (double) minNumberOfReps / totalActions;

        double diff = 1d - minFraction;
        double offset = diff / dimension;
        position = 0;
        do {
            position++;
            separator = (float) (minFraction + offset * position);

        } while(fraction > separator);
        position--;

        return position;
    }

    public static boolean isBetween(float x, float lower, float upper) {
        return lower <= x && x < upper;
    }

    public void checkAndReplaceElite(int x, int y, ActionSequence actionSequence) {
        int position = coordinatesToOneAxis(x, y);
        ActionSequence oldActionSequence = null;
        if (map.containsKey(position)){
            oldActionSequence = map.get(position);
        }

        if (oldActionSequence == null) {
            unusedPositions.remove(Integer.valueOf(position));
            map.put(position, actionSequence);

        } else {
            float fitnessNew = fitnessCalculator.calculateFitness(this, position, actionSequence);
            float fitnessOld = fitnessCalculator.calculateFitness(this, position, null);

            if (fitnessOld <= fitnessNew) {
                map.put(position, actionSequence);
            }
        }
    }

    public ActionSequence get(int x, int y) {
        int position = coordinatesToOneAxis(x, y);
        return map.getOrDefault(position, null);
    }

    public void set(int key, ActionSequence actionSequence) {
        map.put(key, actionSequence);
    }

    public int getDimension() {
        return dimension;
    }

    public HashMap<Integer, ActionSequence> getMap() {
        return map;
    }

    public List<Integer> getUnusedPositions() {
        return unusedPositions;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            string.append(i).append("  | ");
            for (int j = 0; j < dimension; j++) {
                int position = coordinatesToOneAxis(j, i);
                if ( map.containsKey(position))
                string.append(map.get(position).toString()).append(" | ");
            }
            string.append("\n");
        }
        return string.toString();
    }
}
