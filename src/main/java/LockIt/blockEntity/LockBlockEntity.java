package LockIt.blockEntity;

import LockIt.LockIt;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 8.6.2017.
 */
public class LockBlockEntity extends BlockEntity {

    public LockBlockEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return LockIt.getProtectableBlocks().contains(this.level.getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()));
    }
}
