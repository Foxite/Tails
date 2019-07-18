package uk.kihira.tails.client.gui;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiUtils;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.OutfitManager;
import uk.kihira.tails.common.Tails;

@OnlyIn(Dist.CLIENT)
public class LibraryListEntry extends GuiListExtended.IGuiListEntry implements IGuiEventListener {

    public final LibraryEntryData data;

    public LibraryListEntry(LibraryEntryData libraryEntryData) {
        this.data = libraryEntryData;
    }

    @Override
    public void drawEntry(int listWidth, int slotHeight, int x, int y, boolean isSelected, float partialTicks) {
        if (data.remoteEntry) {
            Minecraft.getInstance().textureManager.bindTexture(GuiIconButton.ICONS_TEXTURES);
            GuiIconButton.Icons icon = GuiIconButton.Icons.SERVER;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(x + listWidth - 16, y + slotHeight - 12, 0F);
            GlStateManager.scalef(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v, 16, 16, 10);
            GlStateManager.popMatrix();
        }

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString((data.outfit.equals(Tails.localOutfit) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName, 5, y + 3, 0xFFFFFF);

        int offset = 0;
        for (OutfitPart outfitPart : data.outfit.parts) {
            Part part = PartRegistry.getPart(outfitPart.basePart);
            if (part != null) {
                fontRenderer.drawString(I18n.format(part.name), x + 5, y + 12 + (8 * offset), 0xFFFFFF);
                //for (int i = 1; i < 4; i++) {
                //    todo Gui.drawRect(listWidth - (8 * i), y + 13 + (offset * 8), listWidth + 7 - (8 * i), y + 20 + (offset * 8), part.tint[i - 1]);
                //}
                offset++;
            } // else continue;
        }

        if (data.favourite) {
            Minecraft.getInstance().textureManager.bindTexture(GuiIconButton.ICONS_TEXTURES);
            GuiIconButton.Icons icon = GuiIconButton.Icons.STAR;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(x + listWidth - 16, y, 0F);
            GlStateManager.scalef(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(0, 0, icon.u, icon.v + 32, 16, 16, 10);
            GlStateManager.popMatrix();
        }
    }

    public static class NewLibraryListEntry extends LibraryListEntry {

        private final LibraryPanel panel;

        public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
            super(libraryEntryData);
            this.panel = panel;
        }

        @Override
        public void drawEntry(int listWidth, int slotHeight, int x, int y, boolean isSelected, float partialTicks) {
            Minecraft.getInstance().fontRenderer.drawString(I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double x, double y, int modifiers) {
        //public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int mouseSlotX, int mouseSlotY) {
            //Create entry and add to library
            GameProfile profile = Minecraft.getInstance().player.getGameProfile();
            LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Tails.localOutfit);
            OutfitManager.INSTANCE.getLibraryManager().addEntry(data);
            panel.addSelectedEntry(new LibraryListEntry(data));
            return false;
        }
    }
}
