package uk.kihira.tails.client.gui.controls;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import uk.kihira.tails.client.gui.ITooltip;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;


import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class GuiHSBSlider extends GuiSlider implements ITooltip {

    private static final ResourceLocation sliderTexture = new ResourceLocation(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");
    
    private final HSBSliderType type;
    private final IHSBSliderCallback callback;
    private float hueValue;
    private float briValue;
    private List<String> tooltips;
    
    public GuiHSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
        super(id, xPos, yPos, width, height, "", "", 0, 256 * 6 - 5, 0, false, false);
        this.type = type;
        this.hueValue = 0;
        this.briValue = 0;
        this.callback = callback;
    }

    public GuiHSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, String ... tooltips) {
        this(id, xPos, yPos, width, height, callback, type);
        this.tooltips = Arrays.asList(tooltips);
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            GuiUtils.drawContinuousTexturedBox(GuiButton.BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            mc.renderEngine.bindTexture(sliderTexture);
            
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(hueValue, 1F, 1F);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GlStateManager.color(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, 176, 256, 20, this.width - 2, this.height - 2);
            }
            
            int srcY = 236;
            
            if (type == HSBSliderType.BRIGHTNESS) {
                srcY -= 20;
            }
            if (type == HSBSliderType.SATURATION) {
                srcY -= 40;
            }
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(0F, 0F, briValue);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GlStateManager.color(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 231, 20, this.width - 2, this.height - 2);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 256, 20, this.width - 2, this.height - 2);
            }
            
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
    
    @Override
    protected void mouseDragged(Minecraft mc, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
                if (callback != null) {
                    callback.onValueChangeHSBSlider(this, this.sliderValue);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderHelper.startGlScissor(x, y, width, height);
            mc.renderEngine.bindTexture(sliderTexture);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.y, 0, 0, 7, 4);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.y + this.height - 4, 7, 0, 7, 4);
            //RenderHelper.endGlScissor();
        }
    }
    
    public HSBSliderType getType() {
        return type;
    }
    
    @Override
    public double getValue() {
        return sliderValue;
    }

    /**
     * Sets the current slider value between 0-1F
     * @param value New value
     */
    @Override
    public void setValue(double value) {
        this.sliderValue = value;
        updateSlider();
    }

    /**
     * Sets the current slider value between 0-1F and calls the callback
     * @param value New value
     */
    public void setValueWithCallback(double value) {
        this.sliderValue = value;
        updateSlider();
        if (callback != null) {
            callback.onValueChangeHSBSlider(this, this.sliderValue);
        }
    }

    /**
     * Sets the current hue value between 0-1F
     * @param value New value
     */
    public void setHue(float value) {
        this.hueValue = value;
    }

    /**
     * Sets the current brightness value between 0-1F
     * @param value New value
     */
    public void setBrightness(float value) {
        this.briValue = value;
    }
    
    private void drawTexturedModalRectScaled(int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        BufferBuilder renderer = Tessellator.getInstance().getBuffer();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos((double) (x + 0), (double) (y + tarHeight), (double) this.zLevel).tex((double) ((float) (u + 0) * f), (double) ((float) (v + srcHeight) * f1)).endVertex();
        renderer.pos((double) (x + tarWidth), (double) (y + tarHeight), (double) this.zLevel).tex((double) ((float) (u + srcWidth) * f), (double) ((float) (v + srcHeight) * f1)).endVertex();
        renderer.pos((double) (x + tarWidth), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (u + srcWidth) * f), (double) ((float) (v + 0) * f1)).endVertex();
        renderer.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (u + 0) * f), (double) ((float) (v + 0) * f1)).endVertex();
        Tessellator.getInstance().draw();
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
        return tooltips;
    }

    public enum HSBSliderType {
        HUE, SATURATION, BRIGHTNESS
    }

    public interface IHSBSliderCallback {
        void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue);
    }
}
