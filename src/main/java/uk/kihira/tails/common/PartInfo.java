/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class PartInfo implements Cloneable {
    @Expose public final boolean hasPart;
    @Expose public final int typeid;
    @Expose public final int subid;
    @Expose public final int[] tints;
    @Expose public final int textureID;
    @Expose public PartsData.PartType partType; //Not final to preserve compat
    @Expose public final float scale;
	private ResourceLocation texture;
    public boolean needsTextureCompile = true;
	
	public PartInfo(boolean hasPart, int type, int subtype, int textureID, int[] tints, PartsData.PartType partType, float scale, ResourceLocation texture) {
		this.hasPart = hasPart;
		this.typeid = type;
		this.subid = subtype;
        this.textureID = textureID;
        this.tints = tints;
        this.partType = partType;
        this.scale = scale;
        this.texture = texture;
	}

    public PartInfo(boolean hasPart, int type, int subtype, int textureID, int tint1, int tint2, int tint3, float scale, ResourceLocation texture, PartsData.PartType partType) {
        this(hasPart, type, subtype, textureID, new int[] {tint1, tint2, tint3}, partType, scale, texture);
    }

    public static PartInfo none(PartsData.PartType partType) {
        return new PartInfo(false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 1.f, null, partType);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setTexture(ResourceLocation texture) {
        if (texture == null || (this.texture != null && !this.texture.equals(texture))) {
            try {
                Minecraft.getMinecraft().renderEngine.deleteTexture(this.texture);
            } catch (Exception ignored) {}

            this.needsTextureCompile = true;
        }
        else {
            this.needsTextureCompile = false;
        }
        this.texture = texture;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartInfo partInfo = (PartInfo) o;

        if (hasPart != partInfo.hasPart) return false;
        if (subid != partInfo.subid) return false;
        if (textureID != partInfo.textureID) return false;
        if (typeid != partInfo.typeid) return false;
        if (partType != partInfo.partType) return false;
        if (!Arrays.equals(tints, partInfo.tints)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (hasPart ? 1 : 0);
        result = 31 * result + typeid;
        result = 31 * result + subid;
        result = 31 * result + Arrays.hashCode(tints);
        result = 31 * result + textureID;
        result = 31 * result + partType.hashCode();
        return result;
    }

    public PartInfo deepCopy() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PartInfo.class);
    }
}
