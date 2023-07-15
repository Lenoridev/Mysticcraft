package net.kapitencraft.mysticcraft.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.misc.utils.TextUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DamageIndicatorParticle extends Particle {
    protected DamageIndicatorParticle(ClientLevel p_107234_, double p_107235_, double p_107236_, double p_107237_, double amout, String damageType) {
        super(p_107234_, p_107235_, p_107236_, p_107237_);

        this.text = MysticcraftMod.doubleFormat(amout);
        this.color = TextUtils.damageIndicatorColorGenerator(damageType).getColor();
        this.setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        this.darkColor = FastColor.ARGB32.color(255, (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25));
    }

    private float fadeout = -1;
    private float prevFadeout = -1;

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final String text;
    private final int color;
    private final int darkColor;


    private float visualDY = 0;
    private float prevVisualDY = 0;
    private float visualDX = 0;
    private float prevVisualDX = 0;


    @Override
    public void render(@NotNull VertexConsumer p_107261_, @NotNull Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);

        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);


        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevVisualDY, this.visualDY), 0);

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevVisualDX, this.visualDX), 0, 0);

        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        MultiBufferSource.BufferSource buffer =  Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;

        int light = LightTexture.FULL_BRIGHT;
        fontRenderer.drawInBatch(text, x1,
                0, color, false,
                poseStack.last().pose(), buffer, false, 0, light);
        poseStack.translate(1, 1, +0.03);
        fontRenderer.drawInBatch(text, x1,
                0, darkColor, false,
                poseStack.last().pose(), buffer, false, 0, light);

        buffer.endBatch();

        poseStack.popPose();

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (lifetime - length) ? ((float) lifetime - this.age) / length : 1;

            this.prevVisualDY = this.visualDY;
            this.visualDY += this.yd;
            this.prevVisualDX = this.visualDX;
            this.visualDX += this.xd;

            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Math.pow(this.visualDX * 1.5, 2) + Math.pow(this.visualDY - 1, 2)) < 1.9 - 1) {

                this.yd = this.yd / 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }
}
