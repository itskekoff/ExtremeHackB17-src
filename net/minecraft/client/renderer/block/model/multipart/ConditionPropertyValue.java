package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.multipart.ICondition;

public class ConditionPropertyValue
implements ICondition {
    private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String value;

    public ConditionPropertyValue(String keyIn, String valueIn) {
        this.key = keyIn;
        this.value = valueIn;
    }

    @Override
    public Predicate<IBlockState> getPredicate(BlockStateContainer blockState) {
        List<String> list;
        boolean flag;
        final IProperty<?> iproperty = blockState.getProperty(this.key);
        if (iproperty == null) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": Definition: " + blockState + " has no property: " + this.key);
        }
        String s2 = this.value;
        boolean bl2 = flag = !s2.isEmpty() && s2.charAt(0) == '!';
        if (flag) {
            s2 = s2.substring(1);
        }
        if ((list = SPLITTER.splitToList(s2)).isEmpty()) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": has an empty value: " + this.value);
        }
        Predicate<IBlockState> predicate = list.size() == 1 ? this.makePredicate(iproperty, s2) : Predicates.or(Iterables.transform(list, new Function<String, Predicate<IBlockState>>(){

            @Override
            @Nullable
            public Predicate<IBlockState> apply(@Nullable String p_apply_1_) {
                return ConditionPropertyValue.this.makePredicate(iproperty, p_apply_1_);
            }
        }));
        return flag ? Predicates.not(predicate) : predicate;
    }

    private Predicate<IBlockState> makePredicate(final IProperty<?> property, String valueIn) {
        final Optional<?> optional = property.parseValue(valueIn);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.valueOf(this.toString()) + ": has an unknown value: " + this.value);
        }
        return new Predicate<IBlockState>(){

            @Override
            public boolean apply(@Nullable IBlockState p_apply_1_) {
                return p_apply_1_ != null && p_apply_1_.getValue(property).equals(optional.get());
            }
        };
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
    }
}

