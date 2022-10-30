package generator.enums;

public enum ActionType {

    //Wait("-"),
    Weld("W"),
    Grind("G"),
    Use("U");

    private String actionName;
    ActionType(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName(){
        return this.actionName;
    }

}
