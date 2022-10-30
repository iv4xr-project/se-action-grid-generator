package generator.bugs;

import generator.entities.BlockEntity;

public class SingularBlockBug extends GameBug {

    BlockEntity block;

    public SingularBlockBug(BlockEntity blockEntity){
        block = new BlockEntity(blockEntity);
        randomizeInternalStates(block);
    }

    public String toString(){
        return block.toString();
    }

    public BlockEntity getBlock() {
        return block;
    }
}
