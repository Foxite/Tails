package uk.kihira.tails.client.gui.dialog;

import com.google.common.base.Strings;
import uk.kihira.tails.client.gui.GuiBase;
import uk.kihira.tails.client.gui.Panel;
import net.minecraft.client.gui.GuiButton;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public class Dialog<T extends GuiBase & IDialogCallback> extends Panel<T> {

    protected boolean dragging;

    protected String title;

    public Dialog(T parent, String title, int left, int top, int width, int height) {
        this(parent, left, top, width, height);
        this.title = title;
    }

    public Dialog(T parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        Validate.isInstanceOf(IDialogCallback.class, parent);
    }

    /* Removed in 1.13. Function no longer necessary?
    @Override
    protected void actionPerformed(GuiButton button) {
        parent.buttonPressed(this, button);
    }
     */

    @Override
    public void render(int x, int y, float p_73863_3_) {
        drawGradientRect(0, 0, width, height, 0xFF808080, 0xFF808080);
        drawGradientRect(1, 12, width - 1, height - 1, 0xFF000000, 0xFF000000);

        if (!Strings.isNullOrEmpty(title)) {
            drawString(fontRenderer, title, 2, 2, 0xFFFFFFFF);
        }

        super.render(x, y, p_73863_3_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        //Only if they grab the top
        if (mouseButton == 0 && mouseY < 12) {
            dragging = true;
            return true;
        }
        else {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (dragging && mouseButton == 0) {
            dragging = false;
            return true;
        }
        else {
            return super.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double mouseDX, double mouseDY) {
        if (dragging) {
            left -= mouseDX;
            right = left + width;

            top -= mouseDY;
            bottom = top + height;
            return true;
        }
        else {
            return super.mouseDragged(mouseX, mouseY, mouseButton, mouseDX, mouseDY);
        }
    }
}
