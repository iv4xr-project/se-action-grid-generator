package generator;

import generator.entities.BlockEntity;
import generator.entities.Entity;
import generator.entities.PlayerEntity;
import generator.enums.BlockType;
import generator.enums.CubeBlock;
import generator.enums.FunctionalBlock;
import generator.enums.PlayerNumber;

import java.util.*;

public class GameMapGenerator {

    Integer maxX;
    Integer maxY;

    Integer numberOfPlayers;
    Integer nonFuncBlocks;
    Integer funcBlocks;
    Integer totalNumberOfBlocks;

    boolean[] occupiedPositions;
    List<PlayerEntity> playerEntityList;

    HashMap< Integer, Entity> responseEntities;
    HashMap< Integer, BlockEntity> reachableEntities;

    public GameMapGenerator(Integer maxX, Integer maxY, Integer numberOfPlayers){
        this.maxX = maxX;
        this.maxY = maxY;
        this.numberOfPlayers = numberOfPlayers;
        this.nonFuncBlocks = CubeBlock.values().length;
        this.funcBlocks = FunctionalBlock.values().length;
        this.totalNumberOfBlocks = nonFuncBlocks + funcBlocks;
        this.occupiedPositions = new boolean[this.maxX*this.maxY];
        this.responseEntities = new HashMap<>();
        this.reachableEntities = new HashMap<>();
    }

    public GameMapGenerator(GameMapGenerator gameMapGenerator){
        this.maxX = gameMapGenerator.maxX;
        this.maxY = gameMapGenerator.maxY;
        this.numberOfPlayers = gameMapGenerator.numberOfPlayers;
        this.nonFuncBlocks = gameMapGenerator.nonFuncBlocks;
        this.funcBlocks = gameMapGenerator.funcBlocks;
        this.totalNumberOfBlocks = gameMapGenerator.totalNumberOfBlocks;
        this.occupiedPositions = gameMapGenerator.occupiedPositions.clone();
        this.playerEntityList = new ArrayList<>();
        for (PlayerEntity player : gameMapGenerator.playerEntityList){
            this.playerEntityList.add(player);
        }
        this.responseEntities = new HashMap<>();
        this.responseEntities.putAll(gameMapGenerator.responseEntities);
        this.reachableEntities = copyBlockMap(gameMapGenerator.reachableEntities);
    }

    public void generate(Integer numberOfBlocks){
        //create players
        Random rand = new Random();
        PlayerEntity player1 = new PlayerEntity(PlayerNumber.P1);
        PlayerEntity player2 = new PlayerEntity(PlayerNumber.P2);
        int numberOfEntities;

        if (numberOfBlocks == null){
            int upperboundNumberOfBlocks = (this.maxX * this.maxY / 2 ) - numberOfPlayers ;
            numberOfEntities = rand.nextInt(upperboundNumberOfBlocks) + 1;
        } else {
            numberOfEntities = numberOfBlocks;
        }

        playerEntityList = new ArrayList<>(numberOfEntities);
        playerEntityList.add(player1);
        playerEntityList.add(player2);

        responseEntities = new HashMap<>();
        reachableEntities = new HashMap<>();

        for(int i = 0; i < numberOfEntities; i++) {

            //decide block type
            int randomBlockType = rand.nextInt(totalNumberOfBlocks);
            BlockType type = getBlockTypeFromId(randomBlockType);
            BlockEntity newBlock = new BlockEntity();
            newBlock.setType(type);
            newBlock.setTypeCode(randomBlockType);

            if (responseEntities.isEmpty()){
                //randomize the coordinates of the first block
                int randomXPosition = rand.nextInt(maxX);
                int randomYPosition = rand.nextInt(maxY);

                addToGrid(newBlock, randomXPosition, randomYPosition);

                int position = getArrayPosition(randomXPosition, randomYPosition);
                responseEntities.put(position, newBlock);
                reachableEntities.put(position, newBlock);
            } else {
                List<Integer> keys = new ArrayList<>(reachableEntities.keySet());
                int randomKey = keys.get(rand.nextInt(keys.size()));
                BlockEntity target = reachableEntities.get(randomKey);

                newBlock = (BlockEntity) setEntityNextToTarget(newBlock, target);
                int position = getArrayPosition(newBlock.getxPosition(), newBlock.getyPosition());

                responseEntities.put(position, newBlock);
                reachableEntities.put(position, newBlock);
                removeUnreachableEntities();
            }
        }

    }

    public void removeUnreachableEntities(){
        Set<Integer> keys = reachableEntities.keySet();
        List<Integer> toRemove = new ArrayList<>();

        for (Integer key : keys){
            BlockEntity blockEntity = reachableEntities.get(key);
            blockEntity = (BlockEntity) checkSurrounded(blockEntity);
            if (blockEntity.isFullySurrounded()) toRemove.add(key);
        }

        toRemove.forEach(reachableEntities.keySet()::remove);
    }

    public Entity addToGrid(Entity entity, int x, int y){
        entity.setxPosition(x);
        entity.setyPosition(y);
        setOccupied(x,y);
        return entity;
    }

    public Entity checkSurrounded(Entity entity){
        int x = entity.getxPosition();
        int y = entity.getyPosition();

        int surroundedBy = 0;
        if (checkOccupied(x+1, y)) surroundedBy++;
        if (checkOccupied(x, y+1)) surroundedBy++;
        if (checkOccupied(x-1, y)) surroundedBy++;
        if (checkOccupied(x, y-1)) surroundedBy++;

        entity.setSurroundedBy(surroundedBy);
        return entity;
    }

    public BlockEntity getReachableEntity(int x, int y){
        int position = getArrayPosition(x, y);
        return reachableEntities.get(position);
    }

    public BlockType getBlockTypeFromId(int id){
        BlockType type;

        if (id >= this.nonFuncBlocks){
            type = FunctionalBlock.values()[id - this.nonFuncBlocks];
        } else {
            type = CubeBlock.values()[id];
        }

        return type;
    }

    public void setOccupied(int x, int y){
        int arrayPosition = getArrayPosition(x, y);
        occupiedPositions[arrayPosition] = true;
    }

    public void setUnoccupied(int x, int y){
        int arrayPosition = getArrayPosition(x, y);
        occupiedPositions[arrayPosition] = false;
    }

    public boolean checkOccupied(int x, int y){
        if (x < 0 || x > maxX-1 || y < 0 || y > maxY-1 ){
            return true;
        }
        int arrayPosition = getArrayPosition(x, y);
        return occupiedPositions[arrayPosition];
    }

    public Entity setEntityNextToTarget(Entity entity, BlockEntity target){
        int x = target.getxPosition();
        int y = target.getyPosition();
        Random rand = new Random();
        boolean xOrY;
        int offset;
        int newX;
        int newY;
        do {
            offset = -1;
            xOrY = rand.nextBoolean();
            offset += rand.nextInt(2) * 2;
            newX = x;
            newY = y;
            if (xOrY)   newX += offset;
            else newY += offset;
        } while (checkOccupied(newX, newY));

        return addToGrid(entity, newX, newY);
    }

    public int getArrayPosition(int x, int y){
        return x * maxX+ y;
    }

    public int[] oneAxisToCoordinates(int position) {
        int[] response = new int[2];
        response[0] = position % maxX;
        response[1] = position / maxY;
        return response;
    }

    public HashMap<Integer, BlockEntity> copyBlockMap(HashMap<Integer, BlockEntity> entities){
        int length = entities.size();
        Integer[] keySet = entities.keySet().toArray(new Integer[length]);
        HashMap<Integer, BlockEntity> copy = new HashMap<>();
        for (int i = 0; i < length; i++) {
            Integer newKey = keySet[i];
            BlockEntity newEntity = new BlockEntity(entities.get(newKey));
            copy.put(newKey, newEntity);
        }
        return copy;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public Integer getMaxY() {
        return maxY;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Integer getNonFuncBlocks() {
        return nonFuncBlocks;
    }

    public Integer getFuncBlocks() {
        return funcBlocks;
    }

    public Integer getTotalNumberOfBlocks() {
        return totalNumberOfBlocks;
    }

    public boolean[] getOccupiedPositions() {
        return occupiedPositions;
    }

    public Map<Integer, Entity> getResponseEntities() {
        return responseEntities;
    }

    public HashMap<Integer, BlockEntity> getReachableEntities() {
        return reachableEntities;
    }
}
