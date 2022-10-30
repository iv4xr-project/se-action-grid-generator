package generator.entities;

import generator.enums.PlayerNumber;

public class PlayerEntity extends Entity {

    PlayerNumber playerNumber;

    public PlayerEntity(PlayerEntity entity) {
        super(entity);
        this.playerNumber = entity.playerNumber;
    }

    public PlayerEntity(PlayerNumber playerNumber){
        super();
        this.playerNumber = playerNumber;
    }

    public PlayerEntity(int number){
        super();
        this.playerNumber = getPlayerNumber(number);
    }

    public PlayerNumber getPlayerNumber(int number){
        switch (number){
            case 0:
                return PlayerNumber.P1;
            case 1:
                return PlayerNumber.P2;
            default:
                return null;
        }
    }

    public PlayerNumber getPlayerNumber() {
        return playerNumber;
    }


    @Override
    public String toString(){
        return playerNumber + " " + super.toString();
    }
}
