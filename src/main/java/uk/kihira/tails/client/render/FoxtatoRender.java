package uk.kihira.tails.client.render;

import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import vazkii.botania.api.item.TinyPotatoRenderEvent;

public class FoxtatoRender {

    private FoxtatoFakeEntity fakeEntity;
    private PartInfo tailPartInfo = new PartInfo(true, 0, 0, 0, new int[]{-5480951, -6594259, -5197647}, PartsData.PartType.TAIL, 1.f, null);
    private PartInfo earPartInfo = new PartInfo(true, 0, 0, 0, new int[]{-5480951, 0xFF000000, -5197647}, PartsData.PartType.EARS, 1.f, null);

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        if (fakeEntity != null) {
            fakeEntity.setDead();
            fakeEntity = null;
        }
    }

/*    @SubscribeEvent
    public void onPotatoRender(TinyPotatoRenderEvent e) {
        if (e.name.equalsIgnoreCase("foxtato")) {
            if (fakeEntity == null) {
                fakeEntity = new FoxtatoFakeEntity(Minecraft.getMinecraft().world);
            }
            LegacyPartRenderer fox_tailRender = PartRegistry.getRenderer(PartsData.PartType.TAIL, 0);
            LegacyPartRenderer foxEarRender = PartRegistry.getRenderer(PartsData.PartType.EARS, 0);

            fox_tailRender.render(fakeEntity, tailPartInfo, e.x, e.y, e.z, e.partTicks);
            foxEarRender.render(fakeEntity, earPartInfo, e.x, e.y, e.z, e.partTicks);
            GlStateManager.glColor3f(1f, 0f, 1f);
        }
    }*/

    public static class FoxtatoFakeEntity extends FakeEntity {
        public FoxtatoFakeEntity(World world) {
            super(world);
        }
    }
}
