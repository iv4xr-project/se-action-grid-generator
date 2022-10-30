package generator.entities;

import generator.actions.Action;
import generator.enums.ActionType;
import generator.enums.BlockType;

public class BlockEntity extends Entity {

    BlockType type;
    Integer typeCode;
    int integrity = 100;
    boolean inUse = false;
    int numberOfUses = 0;

    public BlockEntity(BlockEntity entity) {
        super(entity);
        this.type = entity.type;
        this.typeCode = entity.typeCode;
        this.integrity = entity.integrity;
    }

    public BlockEntity() {
        super();
        this.type = null;
        this.typeCode = null;
    }

    public void applyAction(Action action){
        ActionType actionType = action.getActionType();
        if (integrity > 0 ){
            if (actionType.equals(ActionType.Grind)){
                integrity -= 25;
            } else if (actionType.equals(ActionType.Weld) && integrity < 100){
                integrity += 25;
            } else if (actionType.equals(ActionType.Use)){
                if(inUse){
                    inUse = false;
                } else {
                    numberOfUses++;
                    inUse = true;
                }
            }
        }
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public int getIntegrity() {
        return integrity;
    }

    public void setIntegrity(int integrity) {
        this.integrity = integrity;
    }

    public void setNumberOfUses(int numberOfUses) {
        this.numberOfUses = numberOfUses;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public int getNumberOfUses() {
        return numberOfUses;
    }

    @Override
    public String toString(){
        return type + " " + integrity + "% " + (inUse ? "inUse" : "!inUse")
                + " uses:" + numberOfUses + " " + super.toString();
    }
}
