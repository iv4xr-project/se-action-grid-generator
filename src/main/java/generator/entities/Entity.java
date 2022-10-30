package generator.entities;

public class Entity {

    Integer xPosition;
    Integer yPosition;
    Integer surroundedBy;

    public Entity(Entity entity){
        this.xPosition = entity.xPosition;
        this.yPosition = entity.yPosition;
    }

    public Entity() {
        this.xPosition = null;
        this.yPosition = null;
    }

    public Integer getxPosition() {
        return xPosition;
    }

    public void setxPosition(Integer xPosition) {
        this.xPosition = xPosition;
    }

    public Integer getyPosition() {
        return yPosition;
    }

    public void setyPosition(Integer yPosition) {
        this.yPosition = yPosition;
    }

    public String toString(){
        return "position:" + xPosition + "," + yPosition;
    }

    public Integer getSurroundedBy() {
        return surroundedBy;
    }

    public void setSurroundedBy(Integer surroundedBy) {
        this.surroundedBy = surroundedBy;
    }

    public boolean isFullySurrounded() {return surroundedBy >= 4;}
}
