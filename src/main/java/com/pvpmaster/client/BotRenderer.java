package com.pvpmaster.client;

import com.pvpmaster.entity.BotEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

/** Renders the bot as a Steve-skinned humanoid with held items (armor layer not yet rendered). */
public class BotRenderer extends BipedEntityRenderer<BotEntity, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/player/wide/steve.png");

    public BotRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override public BipedEntityRenderState createRenderState() { return new BipedEntityRenderState(); }

    @Override public Identifier getTexture(BipedEntityRenderState state) { return TEXTURE; }
}
