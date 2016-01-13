package com.tencao.saoui.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Tencao on 13/01/2016.
 */
public class EntityList {

    public final List<Entity> loadedEntityList = Lists.<Entity>newArrayList();

    public <T extends Entity> List<T> getEntities(Class <? extends T > entityType, Predicate<? super T > filter)
    {
        List<T> list = Lists.<T>newArrayList();
        Minecraft mc = Minecraft.getMinecraft();

        for (Object entity : getLoadedEntities(mc.theWorld))
        {
            if (entityType.isAssignableFrom(entity.getClass()) && filter.apply((T)entity))
            {
                this.loadedEntityList.add((T)entity);
            }
        }

        return list;
    }

    public List getLoadedEntities(World world){
        return world.loadedEntityList;
    }

}
