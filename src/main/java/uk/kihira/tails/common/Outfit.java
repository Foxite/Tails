package uk.kihira.tails.common;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.kihira.tails.client.OutfitPart;

import java.util.ArrayList;
import java.util.UUID;

public class Outfit implements IDisposable {
    public final UUID id;
    public String name;
    public String description;
    public ArrayList<OutfitPart> parts;

    public Outfit() {
        id = UUID.randomUUID();
        parts = new ArrayList<>();
    }

    // todo make a client only multimap of mountpoint <-> outfitpart?

    @SideOnly(Side.CLIENT)
    public void dispose() {
        for (OutfitPart part : parts) {
            part.dispose();
        }
    }
}
