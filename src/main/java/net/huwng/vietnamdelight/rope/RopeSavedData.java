package net.huwng.vietnamdelight.rope;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class RopeSavedData extends SavedData {
    public static final String DATA_NAME = "vietnam_delight_rope_data";

    /** raw NBT – chứa connections serialized bởi RopeConnectionManager */
    private CompoundTag data = new CompoundTag();

    /** cache tiện lợi cho RopeNodes (nếu bạn có nhu cầu) */
    private final Set<BlockPos> ropeNodes = new HashSet<>();

    public RopeSavedData() {}

    // ---------- FACTORY METHODS ----------
    public static RopeSavedData create() {
        return new RopeSavedData();
    }

    public static RopeSavedData load(CompoundTag tag) {
        RopeSavedData saved = new RopeSavedData();
        saved.data = tag.copy();
        saved.parseNodesFromRawData();
        return saved;
    }

    // ---------- SAVE ----------
    @Override
    public CompoundTag save(CompoundTag tag) {
        // luôn merge raw data
        if (this.data != null && !this.data.isEmpty()) {
            tag.merge(this.data);
        }

        // đảm bảo RopeNodes được ghi để dễ debug
        serializeNodesToRawData();
        tag.put("RopeNodes", this.data.getList("RopeNodes", 10));

        return tag;
    }

    // ---------- COMPATIBILITY API ----------
    /** Thay thế toàn bộ raw data (thường được gọi từ RopeConnectionManager.serialize...) */
    public void setData(CompoundTag t) {
        this.data = (t == null) ? new CompoundTag() : t.copy();
        parseNodesFromRawData();
        setDirty();
    }

    public CompoundTag getData() {
        return this.data;
    }

    // ---------- NODE MANAGEMENT ----------
    public void addNode(BlockPos pos) {
        if (pos != null && this.ropeNodes.add(pos)) {
            serializeNodesToRawData();
            setDirty();
        }
    }

    public void removeNode(BlockPos pos) {
        if (pos != null && this.ropeNodes.remove(pos)) {
            serializeNodesToRawData();
            setDirty();
        }
    }

    public boolean hasNode(BlockPos pos) {
        return this.ropeNodes.contains(pos);
    }

    public Set<BlockPos> getAllNodes() {
        return this.ropeNodes;
    }

    // ---------- HELPERS ----------
    private void parseNodesFromRawData() {
        this.ropeNodes.clear();
        if (this.data == null) return;
        if (this.data.contains("RopeNodes")) {
            ListTag list = this.data.getList("RopeNodes", 10); // 10 = CompoundTag
            for (int i = 0; i < list.size(); i++) {
                CompoundTag n = list.getCompound(i);
                this.ropeNodes.add(new BlockPos(n.getInt("x"), n.getInt("y"), n.getInt("z")));
            }
        }
    }

    private void serializeNodesToRawData() {
        if (this.data == null) this.data = new CompoundTag();
        ListTag list = new ListTag();
        for (BlockPos pos : ropeNodes) {
            CompoundTag n = new CompoundTag();
            n.putInt("x", pos.getX());
            n.putInt("y", pos.getY());
            n.putInt("z", pos.getZ());
            list.add(n);
        }
        this.data.put("RopeNodes", list);
    }

    // ---------- GET FROM LEVEL ----------
    public static RopeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            RopeSavedData::load,
            RopeSavedData::create,
            DATA_NAME
        );
    }
}
