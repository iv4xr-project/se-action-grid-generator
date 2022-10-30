package generator;

import generator.actions.ActionSequence;
import generator.actions.MAPEActions;
import generator.actions.Action;
import generator.enums.ActionType;

import java.util.List;
import java.util.Map;

public class DiversityCalculator {

    float maxTotalSimularity = 0;

    public float calculateGridDiversity(MAPEActions mape){
        int dimension = mape.getDimension();
        int x;
        int y;
        float similarity = 0;
        ActionSequence actionSequence;
        ActionSequence compare;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                x = j+1;
                y = i;
                actionSequence = mape.get(j,i);

                while (y < dimension){
                    if (x >= dimension){
                        y++;
                        x = 0;
                        continue;
                    }
                    compare = mape.get(x,y);
                    float value = calculateSimilarity(actionSequence, compare);
                    if (maxTotalSimularity < value) maxTotalSimularity = value;
                    similarity += value;
                    x++;
                }
            }
        }

        return 1f - (similarity / (float) combinations(dimension*dimension, 2));
    }

    public int combinations(int objects, int sample) {
        int diff = objects - sample;
        int dividend = 1;
        for (int i = objects; i > diff; i--) {
            dividend = dividend * i;
        }
        int divisor = 1;
        for (int j = sample; j > 1; j--) {
            divisor = divisor * j;
        }
        return dividend / divisor;
    }

    public float calculateFitness(MAPEActions mape, int position, ActionSequence calcActionSequence){
        float count = 0;
        float similarity = 0;
        ActionSequence actionSequence;
        Map<Integer, ActionSequence> actionSequenceMap = mape.getMap();

        ActionSequence compare;
        if (calcActionSequence == null) compare = actionSequenceMap.get(position);
        else compare = calcActionSequence;

        for (Integer i : actionSequenceMap.keySet()) {

            if (i != position) {
                actionSequence = actionSequenceMap.get(i);
                float value = calculateSimilarity(actionSequence, compare);
                similarity += value;
                count++;
            }

        }

        return 1f - (similarity / count);
    }

    public float calculateSimilarity(ActionSequence aS1, ActionSequence aS2) {
        float similarity1 = 0;
        float similarity2 = 0;
        float similarity;
        int length = aS1.getLength();
        int numberOfPlayers = aS1.getNumberOfPlayers();
        List<Action[]> sequenceList1 = aS1.getActionsListForPlayers();
        List<Action[]> sequenceList2 = aS2.getActionsListForPlayers();
        Action[] actions;
        Action[] actionsCompare;
        for (int i = 0; i < numberOfPlayers; i++) {

            actions = sequenceList1.get(i);

            for (int j = 0; j < numberOfPlayers; j++) {
                actionsCompare = sequenceList2.get(j);
                similarity = 0;
                for (int k = 0; k < length; k++) {
                    similarity += compareActions(actions[k], actionsCompare[k]);
                }

                if(i == j) similarity1 += similarity;
                else similarity2 += similarity;
            }
        }

        float meanSimilarity = (similarity1 + similarity2) / 2;

        return meanSimilarity / (length*numberOfPlayers);
    }

    public float compareActions(Action action1, Action action2){
        float similarity = 0;

        ActionType type1 = action1.getActionType();
        ActionType type2 = action2.getActionType();

        if (type1.equals(type2)) similarity += 0.5;

        int x1 = action1.getX();
        int y1 = action1.getY();
        int x2 = action2.getX();
        int y2 = action2.getY();

        if (x1 == x2 && y1 == y2) similarity += 0.5;

        return similarity;
    }
}
