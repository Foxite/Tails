package uk.kihira.tails.client.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.proxy.ClientProxy;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

// Most of this class is copied from PartsPanel. It has been modified to work with OutfitParts rather than Parts.
// It also does not have a mountpoint button, and its entries have a remove button instead of an add button.
public class OutfitPartsPanel extends Panel<GuiEditor> implements IListCallback<OutfitPartsPanel.OutfitPartEntry>
{
    private static final float Z_POSITION = 100f;
    private static final float PART_SCALE = 40f;

    private GuiList<OutfitPartEntry> outfitParts;
    private OutfitPartEntry selectedPart;
    private float rotation;

    private final int listTop = 35; // TODO copied from PartsPanel

    public OutfitPartsPanel(GuiEditor parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        createOutfitList();
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, OutfitPartEntry entry) {
        selectedPart = entry;

        // Open part in transform, tint panel
        parent.setActiveOutfitPart(entry.outfitPart);

        // TODO highlight part
        return true;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        rotation += partialTicks;

        zLevel = -100;
        drawGradientRect(0, 0, width, listTop, GuiEditor.SOFT_BLACK, GuiEditor.SOFT_BLACK);
        drawGradientRect(0, listTop, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        zLevel = 0;
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawCenteredString(fontRenderer, I18n.format("tails.gui.outfitparts"), width / 2, 5, GuiEditor.TEXT_COLOUR);
        outfitParts.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        outfitParts.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        outfitParts.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput()
    {
        outfitParts.handleMouseInput();
    }

    private void createOutfitList() {
        outfitParts = new GuiList<>(
                this,
                width,
                height - listTop,
                listTop,
                height,
                55,
                parent.getOutfit().parts.stream().map(OutfitPartEntry::new).collect(Collectors.toList())
        );
        outfitParts.setCurrentIndex(0);
    }

    private void renderPart(int x, int y, OutfitPart part) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, Z_POSITION);

        Part basePart = part.getPart();
        if (basePart == null) return;

        Model model = basePart.getModel();
        if (model != null)
        {
            GlStateManager.rotate(rotation, 0f, 1f, 0f);
            GlStateManager.scale(PART_SCALE, PART_SCALE, PART_SCALE);
            ((ClientProxy) Tails.proxy).partRenderer.render(part);
        }
        else
        {
            drawTexturedModalRect(x - 16, y - 16, 0, 0, 32, 32);
            // todo render loading circle
        }

        GlStateManager.popMatrix();
    }

    class OutfitPartEntry implements GuiListExtended.IGuiListEntry {
        private static final int REMOVE_X = 1;
        private static final int REMOVE_Y = 40;
        private static final int REMOVE_WIDTH = 10;
        private static final int REMOVE_HEIGHT = 10;
        private static final int REMOVE_COLOUR = 0xFF666666;

        private OutfitPart outfitPart;

        public OutfitPartEntry(OutfitPart outfitPart) {
            this.outfitPart = outfitPart;
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) { }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            final boolean isCurrentSelectedPart = outfitParts.getCurrentIndex() == slotIndex;

            renderPart(right - 40, y + (slotHeight / 2), outfitPart);
            ClientUtils.drawStringMultiLine(fontRenderer, Objects.requireNonNull(outfitPart.getPart()).name, 5, y + 17, GuiEditor.TEXT_COLOUR);

            if (isCurrentSelectedPart)
            {
                //Yeah its not nice but eh, works
                GlStateManager.pushMatrix();
                GlStateManager.translate(5, y + 27, 0);
                GlStateManager.scale(.6f, .6f, 1f);

                fontRenderer.drawString(I18n.format("gui.author"), 0, 0, GuiEditor.TEXT_COLOUR);
                GlStateManager.translate(0, 10, 0);
                fontRenderer.drawString(TextFormatting.AQUA + outfitPart.getPart().author, 0, 0, GuiEditor.TEXT_COLOUR);
                GlStateManager.popMatrix();

                // Draw "remove" button
                GuiUtils.drawGradientRect(0, x + REMOVE_X, y + REMOVE_Y, x + REMOVE_X + REMOVE_WIDTH, y + REMOVE_Y + REMOVE_HEIGHT, REMOVE_COLOUR, REMOVE_COLOUR);
                fontRenderer.drawString("-", x + REMOVE_X + (REMOVE_WIDTH / 4), y + REMOVE_Y + (REMOVE_HEIGHT / 4), GuiEditor.TEXT_COLOUR);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (GuiBaseScreen.isMouseOver(relativeX, relativeY, REMOVE_X, REMOVE_Y, REMOVE_WIDTH, REMOVE_HEIGHT))
            {
                parent.removeOutfitPart(outfitPart);
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) { }
    }
}
