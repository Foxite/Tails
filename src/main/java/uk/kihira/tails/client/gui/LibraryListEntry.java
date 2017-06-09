package uk.kihira.tails.client.gui;

import com.mojang.authlib.GameProfile;
import uk.kihira.tails.common.PartRegistry;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;


public class LibraryListEntry implements GuiListExtended.IGuiListEntry {

    public final LibraryEntryData data;

    public LibraryListEntry(LibraryEntryData libraryEntryData) {
        this.data = libraryEntryData;
    }

    @Override
    public void drawEntry(int slowIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (data.remoteEntry) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.SERVER;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + listWidth - 16, y + slotHeight - 12, 0F);
            GlStateManager.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v, 16, 16, 10);
            GlStateManager.popMatrix();
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawString((data.partsData.equals(Tails.localPartsData) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName, 5, y + 3, 0xFFFFFF);

        fontRenderer.setUnicodeFlag(true);
        for (PartsData.PartType type : PartsData.PartType.values()) {
            if (data.partsData.hasPartInfo(type)) {
                PartInfo partInfo = data.partsData.getPartInfo(type);
                fontRenderer.drawString(I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)), x + 5, y + 12 + (8 * type.ordinal()), 0xFFFFFF);
                for (int i = 1; i < 4; i++) {
                    Gui.drawRect(listWidth - (8 * i), y + 13 + (type.ordinal() * 8), listWidth + 7 - (8 * i), y + 20 + (type.ordinal() * 8), partInfo.tints[i - 1]);
                }
            }
        }
        fontRenderer.setUnicodeFlag(false);

        if (data.favourite) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.STAR;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + listWidth - 16, y, 0F);
            GlStateManager.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v + 32, 16, 16, 10);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}

    @Override
    public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
        return false;
    }

    @Override
    public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {}

    public static class NewLibraryListEntry extends LibraryListEntry {

        private final LibraryPanel panel;

        public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
            super(libraryEntryData);
            this.panel = panel;
        }

        @Override
        public void drawEntry(int slowIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            Minecraft.getMinecraft().fontRenderer.drawString(I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
            //Create entry and add to library
            GameProfile profile = Minecraft.getMinecraft().player.getGameProfile();
            LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Tails.localPartsData);
            Tails.proxy.getLibraryManager().addEntry(data);
            panel.addSelectedEntry(new LibraryListEntry(data));
            return false;
        }
    }
}
