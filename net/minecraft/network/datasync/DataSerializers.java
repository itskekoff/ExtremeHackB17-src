package net.minecraft.network.datasync;

import com.google.common.base.Optional;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
    private static final IntIdentityHashBiMap<DataSerializer<?>> REGISTRY = new IntIdentityHashBiMap(16);
    public static final DataSerializer<Byte> BYTE = new DataSerializer<Byte>(){

        @Override
        public void write(PacketBuffer buf2, Byte value) {
            buf2.writeByte(value.byteValue());
        }

        @Override
        public Byte read(PacketBuffer buf2) throws IOException {
            return buf2.readByte();
        }

        @Override
        public DataParameter<Byte> createKey(int id2) {
            return new DataParameter<Byte>(id2, this);
        }

        @Override
        public Byte func_192717_a(Byte p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Integer> VARINT = new DataSerializer<Integer>(){

        @Override
        public void write(PacketBuffer buf2, Integer value) {
            buf2.writeVarIntToBuffer(value);
        }

        @Override
        public Integer read(PacketBuffer buf2) throws IOException {
            return buf2.readVarIntFromBuffer();
        }

        @Override
        public DataParameter<Integer> createKey(int id2) {
            return new DataParameter<Integer>(id2, this);
        }

        @Override
        public Integer func_192717_a(Integer p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Float> FLOAT = new DataSerializer<Float>(){

        @Override
        public void write(PacketBuffer buf2, Float value) {
            buf2.writeFloat(value.floatValue());
        }

        @Override
        public Float read(PacketBuffer buf2) throws IOException {
            return Float.valueOf(buf2.readFloat());
        }

        @Override
        public DataParameter<Float> createKey(int id2) {
            return new DataParameter<Float>(id2, this);
        }

        @Override
        public Float func_192717_a(Float p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<String> STRING = new DataSerializer<String>(){

        @Override
        public void write(PacketBuffer buf2, String value) {
            buf2.writeString(value);
        }

        @Override
        public String read(PacketBuffer buf2) throws IOException {
            return buf2.readStringFromBuffer(32767);
        }

        @Override
        public DataParameter<String> createKey(int id2) {
            return new DataParameter<String>(id2, this);
        }

        @Override
        public String func_192717_a(String p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<ITextComponent> TEXT_COMPONENT = new DataSerializer<ITextComponent>(){

        @Override
        public void write(PacketBuffer buf2, ITextComponent value) {
            buf2.writeTextComponent(value);
        }

        @Override
        public ITextComponent read(PacketBuffer buf2) throws IOException {
            return buf2.readTextComponent();
        }

        @Override
        public DataParameter<ITextComponent> createKey(int id2) {
            return new DataParameter<ITextComponent>(id2, this);
        }

        @Override
        public ITextComponent func_192717_a(ITextComponent p_192717_1_) {
            return p_192717_1_.createCopy();
        }
    };
    public static final DataSerializer<ItemStack> OPTIONAL_ITEM_STACK = new DataSerializer<ItemStack>(){

        @Override
        public void write(PacketBuffer buf2, ItemStack value) {
            buf2.writeItemStackToBuffer(value);
        }

        @Override
        public ItemStack read(PacketBuffer buf2) throws IOException {
            return buf2.readItemStackFromBuffer();
        }

        @Override
        public DataParameter<ItemStack> createKey(int id2) {
            return new DataParameter<ItemStack>(id2, this);
        }

        @Override
        public ItemStack func_192717_a(ItemStack p_192717_1_) {
            return p_192717_1_.copy();
        }
    };
    public static final DataSerializer<Optional<IBlockState>> OPTIONAL_BLOCK_STATE = new DataSerializer<Optional<IBlockState>>(){

        @Override
        public void write(PacketBuffer buf2, Optional<IBlockState> value) {
            if (value.isPresent()) {
                buf2.writeVarIntToBuffer(Block.getStateId(value.get()));
            } else {
                buf2.writeVarIntToBuffer(0);
            }
        }

        @Override
        public Optional<IBlockState> read(PacketBuffer buf2) throws IOException {
            int i2 = buf2.readVarIntFromBuffer();
            return i2 == 0 ? Optional.absent() : Optional.of(Block.getStateById(i2));
        }

        @Override
        public DataParameter<Optional<IBlockState>> createKey(int id2) {
            return new DataParameter<Optional<IBlockState>>(id2, this);
        }

        @Override
        public Optional<IBlockState> func_192717_a(Optional<IBlockState> p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Boolean> BOOLEAN = new DataSerializer<Boolean>(){

        @Override
        public void write(PacketBuffer buf2, Boolean value) {
            buf2.writeBoolean(value);
        }

        @Override
        public Boolean read(PacketBuffer buf2) throws IOException {
            return buf2.readBoolean();
        }

        @Override
        public DataParameter<Boolean> createKey(int id2) {
            return new DataParameter<Boolean>(id2, this);
        }

        @Override
        public Boolean func_192717_a(Boolean p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Rotations> ROTATIONS = new DataSerializer<Rotations>(){

        @Override
        public void write(PacketBuffer buf2, Rotations value) {
            buf2.writeFloat(value.getX());
            buf2.writeFloat(value.getY());
            buf2.writeFloat(value.getZ());
        }

        @Override
        public Rotations read(PacketBuffer buf2) throws IOException {
            return new Rotations(buf2.readFloat(), buf2.readFloat(), buf2.readFloat());
        }

        @Override
        public DataParameter<Rotations> createKey(int id2) {
            return new DataParameter<Rotations>(id2, this);
        }

        @Override
        public Rotations func_192717_a(Rotations p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<BlockPos> BLOCK_POS = new DataSerializer<BlockPos>(){

        @Override
        public void write(PacketBuffer buf2, BlockPos value) {
            buf2.writeBlockPos(value);
        }

        @Override
        public BlockPos read(PacketBuffer buf2) throws IOException {
            return buf2.readBlockPos();
        }

        @Override
        public DataParameter<BlockPos> createKey(int id2) {
            return new DataParameter<BlockPos>(id2, this);
        }

        @Override
        public BlockPos func_192717_a(BlockPos p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new DataSerializer<Optional<BlockPos>>(){

        @Override
        public void write(PacketBuffer buf2, Optional<BlockPos> value) {
            buf2.writeBoolean(value.isPresent());
            if (value.isPresent()) {
                buf2.writeBlockPos(value.get());
            }
        }

        @Override
        public Optional<BlockPos> read(PacketBuffer buf2) throws IOException {
            return !buf2.readBoolean() ? Optional.absent() : Optional.of(buf2.readBlockPos());
        }

        @Override
        public DataParameter<Optional<BlockPos>> createKey(int id2) {
            return new DataParameter<Optional<BlockPos>>(id2, this);
        }

        @Override
        public Optional<BlockPos> func_192717_a(Optional<BlockPos> p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<EnumFacing> FACING = new DataSerializer<EnumFacing>(){

        @Override
        public void write(PacketBuffer buf2, EnumFacing value) {
            buf2.writeEnumValue(value);
        }

        @Override
        public EnumFacing read(PacketBuffer buf2) throws IOException {
            return buf2.readEnumValue(EnumFacing.class);
        }

        @Override
        public DataParameter<EnumFacing> createKey(int id2) {
            return new DataParameter<EnumFacing>(id2, this);
        }

        @Override
        public EnumFacing func_192717_a(EnumFacing p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new DataSerializer<Optional<UUID>>(){

        @Override
        public void write(PacketBuffer buf2, Optional<UUID> value) {
            buf2.writeBoolean(value.isPresent());
            if (value.isPresent()) {
                buf2.writeUuid(value.get());
            }
        }

        @Override
        public Optional<UUID> read(PacketBuffer buf2) throws IOException {
            return !buf2.readBoolean() ? Optional.absent() : Optional.of(buf2.readUuid());
        }

        @Override
        public DataParameter<Optional<UUID>> createKey(int id2) {
            return new DataParameter<Optional<UUID>>(id2, this);
        }

        @Override
        public Optional<UUID> func_192717_a(Optional<UUID> p_192717_1_) {
            return p_192717_1_;
        }
    };
    public static final DataSerializer<NBTTagCompound> field_192734_n = new DataSerializer<NBTTagCompound>(){

        @Override
        public void write(PacketBuffer buf2, NBTTagCompound value) {
            buf2.writeNBTTagCompoundToBuffer(value);
        }

        @Override
        public NBTTagCompound read(PacketBuffer buf2) throws IOException {
            return buf2.readNBTTagCompoundFromBuffer();
        }

        @Override
        public DataParameter<NBTTagCompound> createKey(int id2) {
            return new DataParameter<NBTTagCompound>(id2, this);
        }

        @Override
        public NBTTagCompound func_192717_a(NBTTagCompound p_192717_1_) {
            return p_192717_1_.copy();
        }
    };

    static {
        DataSerializers.registerSerializer(BYTE);
        DataSerializers.registerSerializer(VARINT);
        DataSerializers.registerSerializer(FLOAT);
        DataSerializers.registerSerializer(STRING);
        DataSerializers.registerSerializer(TEXT_COMPONENT);
        DataSerializers.registerSerializer(OPTIONAL_ITEM_STACK);
        DataSerializers.registerSerializer(BOOLEAN);
        DataSerializers.registerSerializer(ROTATIONS);
        DataSerializers.registerSerializer(BLOCK_POS);
        DataSerializers.registerSerializer(OPTIONAL_BLOCK_POS);
        DataSerializers.registerSerializer(FACING);
        DataSerializers.registerSerializer(OPTIONAL_UNIQUE_ID);
        DataSerializers.registerSerializer(OPTIONAL_BLOCK_STATE);
        DataSerializers.registerSerializer(field_192734_n);
    }

    public static void registerSerializer(DataSerializer<?> serializer) {
        REGISTRY.add(serializer);
    }

    @Nullable
    public static DataSerializer<?> getSerializer(int id2) {
        return REGISTRY.get(id2);
    }

    public static int getSerializerId(DataSerializer<?> serializer) {
        return REGISTRY.getId(serializer);
    }
}

