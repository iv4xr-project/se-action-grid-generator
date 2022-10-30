package generator.bugs;

import generator.entities.BlockEntity;

public class ConnectedBlocksBug extends GameBug{

    BlockEntity block1;
    BlockEntity block2;

    public ConnectedBlocksBug(BlockEntity blockEntity1, BlockEntity blockEntity2){
        block1 = new BlockEntity(blockEntity1);
        block2 = new BlockEntity(blockEntity2);
        randomizeInternalStates(block1);
        randomizeInternalStates(block2);
    }

    public String toString(){
        return block1.toString() + " " + block2.toString();
    }

    public BlockEntity getBlock1() {
        return block1;
    }

    public BlockEntity getBlock2() {
        return block2;
    }
}
