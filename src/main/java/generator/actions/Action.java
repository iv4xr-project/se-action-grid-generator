package generator.actions;

import generator.enums.ActionType;

public class Action {

    private ActionType actionType;
    private int x;
    private int y;

    public Action(ActionType actionType, int x, int y) {
        this.actionType = actionType;
        this.x = x;
        this.y = y;
    }

    public Action(Action action){
        this.actionType = action.actionType;
        this.x = action.x;
        this.y = action.y;
    }

    public void copy(Action action){
        this.actionType = action.actionType;
        this.x = action.x;
        this.y = action.y;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return actionType.getActionName() + "-" + x + "," + y;
    }
}
