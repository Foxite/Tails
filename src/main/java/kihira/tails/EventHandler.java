/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.tails;

import java.util.UUID;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.render.RenderDragonTail;
import kihira.tails.render.RenderFoxTail;
import kihira.tails.render.RenderRacoonTail;
import kihira.tails.render.RenderTail;
import kihira.tails.texture.TextureHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class EventHandler {

	public static final RenderTail[] tailTypes = { new RenderFoxTail(), new RenderDragonTail(), new RenderRacoonTail() };

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRenderTick(RenderPlayerEvent.Specials.Pre e) {
    	if(TextureHelper.needsBuild(e.entityPlayer)) {
			TextureHelper.buildPlayerInfo(e.entityPlayer);
		}    	
    	
    	UUID id = e.entityPlayer.getGameProfile().getId();
        if (TextureHelper.TailMap.containsKey(id) && TextureHelper.TailMap.get(id).hastail && !e.entityPlayer.isInvisible()) {
        	TailInfo info = TextureHelper.TailMap.get(id);
        	
        	int type = info.typeid;
        	type = type > tailTypes.length ? 0 : type;
        	
            tailTypes[type].render(e.entityPlayer, info);
        }
    }
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
    	TextureHelper.clearTailInfo(e.player);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.side.isClient()) {
			if(TextureHelper.needsBuild(e.player)) {
				TextureHelper.buildPlayerInfo(e.player);
			}
		}
    }
}