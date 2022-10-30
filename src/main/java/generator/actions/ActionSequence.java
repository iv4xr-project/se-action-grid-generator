package generator.actions;

import java.util.ArrayList;
import java.util.List;

public class ActionSequence {

    private List<Action[]> actionsListForPlayers;
    private final int numberOfPlayers;
    private int length;

    public ActionSequence(int numberOfPlayers){
        actionsListForPlayers = new ArrayList<>(numberOfPlayers);
        this.numberOfPlayers = numberOfPlayers;
    }

    public ActionSequence(ActionSequence another) {
        this.numberOfPlayers = another.getNumberOfPlayers();
        this.length = another.getLength();
        this.actionsListForPlayers = copyActionsList( this.numberOfPlayers, this.length,
                another.getActionsListForPlayers());
    }

    public List<Action[]> copyActionsList(int numberOfPlayers, int length, List<Action[]> actionsListForPlayers) {
        List<Action[]> newActionsListForPlayers = new ArrayList<>(numberOfPlayers);
        for (Action[] actions : actionsListForPlayers){
            Action[] newActions = new Action[length];
            for (int i = 0; i < length; i++){

                Action newAction = new Action(actions[i]);
                newActions[i] = newAction;
            }
            newActionsListForPlayers.add(newActions);
        }
        return newActionsListForPlayers;
    }

    public void resetValues(int length){
        actionsListForPlayers = new ArrayList<>(numberOfPlayers);

        for(int p = 0; p < numberOfPlayers; p++){
            actionsListForPlayers.add(new Action[length]);
        }
        this.length = length;
    }

    public void put(int playerNumber, int actionPosition, Action action){
        actionsListForPlayers.get(playerNumber)[actionPosition] = action;
    }

    public Action get(int playerNumber, int actionPosition){
        return actionsListForPlayers.get(playerNumber)[actionPosition];
    }

    public List<Action[]> getActionsListForPlayers() {
        return actionsListForPlayers;
    }

    public void setActionsListForPlayers(List<Action[]> actionsListForPlayers) {
        this.actionsListForPlayers = actionsListForPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getLength() {
        return length;
    }

    public String toString(){
        String string = "";
        for (int i = 0; i < numberOfPlayers; i++) {
            string += "P" + (i+1) + "[";
            for (int j = 0; j < length; j++) {
                string += " " + actionsListForPlayers.get(i)[j].toString() + " ";
            }
            string = string.substring(0,string.length()-1);
            string += " ] ";
        }
        return string;
    }


}
