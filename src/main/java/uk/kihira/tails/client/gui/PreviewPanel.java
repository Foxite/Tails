package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;

class PreviewPanel extends Panel<GuiEditor> {
    private static final int RESET_BUTTON = 0;
    private static final int HELP_BUTTON = 1;

    private float yaw = 0F;
    private float pitch = 10F;
    private int prevMouseX = -1;
    private ScaledResolution scaledRes;
    private boolean doRender;

    PreviewPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void initGui() {
        doRender = Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (!doRender)
            return;
        scaledRes = new ScaledResolution(this.mc);
        // Reset Camera
        buttonList.add(new GuiIconButton(RESET_BUTTON, width - 18, 22, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset.camera")));
        // Help
        buttonList.add(new GuiIconButton(HELP_BUTTON, width - 18, 4, GuiIconButton.Icons.QUESTION, I18n.format("gui.button.help.camera.0"), I18n.format("gui.button.help.camera.1")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!doRender)
            return;
        zLevel = -1000;
        // Background
        drawGradientRect(0, 0, width, height, 0xEE000000, 0xEE000000);

        // Player
        drawEntity((width / 2), (height / 2) + (scaledRes.getScaledHeight() / 4), scaledRes.getScaledHeight() / 4, yaw, pitch, mc.player);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Reset Camera
        if (button.id == RESET_BUTTON) {
            yaw = 0;
            pitch = 10F;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (lastButtonClicked == 0) {
            //Yaw
            if (prevMouseX == -1) prevMouseX = mouseX;
            else {
                yaw += (mouseX - prevMouseX) * 1.5F;
                prevMouseX = mouseX;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        prevMouseX = -1;
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private static void drawEntity(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        float prevHeadYaw = entity.rotationYawHead;
        float prevRotYaw = entity.rotationYaw;
        float prevRotPitch = entity.rotationPitch;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 100F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, (float) entity.getYOffset(), 0.0F);
        GlStateManager.rotate(pitch, 1F, 0.0F, 0.0F);
        GlStateManager.rotate(yaw, 0F, 1F, 0.0F);

        entity.rotationYawHead = 0F;
        entity.rotationYaw = 0F;
        entity.rotationPitch = 0F;
        entity.renderYawOffset = 0F;
        entity.setSneaking(false);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0D, 0D, 0D, 0F, 1F, false);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        entity.rotationYawHead = prevHeadYaw;
        entity.rotationYaw = prevRotYaw;
        entity.rotationPitch = prevRotPitch;

        GlStateManager.popMatrix();
    }
}
