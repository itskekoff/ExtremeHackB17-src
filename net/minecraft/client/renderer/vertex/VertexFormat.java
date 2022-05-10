package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<VertexFormatElement> elements = Lists.newArrayList();
    private final List<Integer> offsets = Lists.newArrayList();
    private int nextOffset;
    private int colorElementOffset = -1;
    private final List<Integer> uvOffsetsById = Lists.newArrayList();
    private int normalElementOffset = -1;

    public VertexFormat(VertexFormat vertexFormatIn) {
        this();
        for (int i2 = 0; i2 < vertexFormatIn.getElementCount(); ++i2) {
            this.addElement(vertexFormatIn.getElement(i2));
        }
        this.nextOffset = vertexFormatIn.getNextOffset();
    }

    public VertexFormat() {
    }

    public void clear() {
        this.elements.clear();
        this.offsets.clear();
        this.colorElementOffset = -1;
        this.uvOffsetsById.clear();
        this.normalElementOffset = -1;
        this.nextOffset = 0;
    }

    public VertexFormat addElement(VertexFormatElement element) {
        if (element.isPositionElement() && this.hasPosition()) {
            LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        }
        this.elements.add(element);
        this.offsets.add(this.nextOffset);
        switch (element.getUsage()) {
            case NORMAL: {
                this.normalElementOffset = this.nextOffset;
                break;
            }
            case COLOR: {
                this.colorElementOffset = this.nextOffset;
                break;
            }
            case UV: {
                this.uvOffsetsById.add(element.getIndex(), this.nextOffset);
            }
        }
        this.nextOffset += element.getSize();
        return this;
    }

    public boolean hasNormal() {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset() {
        return this.normalElementOffset;
    }

    public boolean hasColor() {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset() {
        return this.colorElementOffset;
    }

    public boolean hasUvOffset(int id2) {
        return this.uvOffsetsById.size() - 1 >= id2;
    }

    public int getUvOffsetById(int id2) {
        return this.uvOffsetsById.get(id2);
    }

    public String toString() {
        String s2 = "format: " + this.elements.size() + " elements: ";
        for (int i2 = 0; i2 < this.elements.size(); ++i2) {
            s2 = String.valueOf(s2) + this.elements.get(i2).toString();
            if (i2 == this.elements.size() - 1) continue;
            s2 = String.valueOf(s2) + " ";
        }
        return s2;
    }

    private boolean hasPosition() {
        int j2 = this.elements.size();
        for (int i2 = 0; i2 < j2; ++i2) {
            VertexFormatElement vertexformatelement = this.elements.get(i2);
            if (!vertexformatelement.isPositionElement()) continue;
            return true;
        }
        return false;
    }

    public int getIntegerSize() {
        return this.getNextOffset() / 4;
    }

    public int getNextOffset() {
        return this.nextOffset;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public VertexFormatElement getElement(int index) {
        return this.elements.get(index);
    }

    public int getOffset(int index) {
        return this.offsets.get(index);
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            VertexFormat vertexformat = (VertexFormat)p_equals_1_;
            if (this.nextOffset != vertexformat.nextOffset) {
                return false;
            }
            if (!this.elements.equals(vertexformat.elements)) {
                return false;
            }
            return this.offsets.equals(vertexformat.offsets);
        }
        return false;
    }

    public int hashCode() {
        int i2 = this.elements.hashCode();
        i2 = 31 * i2 + this.offsets.hashCode();
        i2 = 31 * i2 + this.nextOffset;
        return i2;
    }
}

