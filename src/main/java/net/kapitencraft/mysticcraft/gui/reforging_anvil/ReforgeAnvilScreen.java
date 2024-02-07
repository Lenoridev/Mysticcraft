package net.kapitencraft.mysticcraft.gui.reforging_anvil;

import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.block.entity.ReforgeAnvilBlockEntity;
import net.kapitencraft.mysticcraft.gui.screen.ModScreen;
import net.kapitencraft.mysticcraft.gui.screen.tooltip.ReforgeItemTooltip;
import net.kapitencraft.mysticcraft.gui.screen.tooltip.UpgradeItemTooltip;
import net.kapitencraft.mysticcraft.networking.ModMessages;
import net.kapitencraft.mysticcraft.networking.packets.C2S.ReforgingPacket;
import net.kapitencraft.mysticcraft.networking.packets.C2S.UpgradeItemPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ReforgeAnvilScreen extends ModScreen<ReforgeAnvilBlockEntity, ReforgeAnvilMenu> {
    public static final int BUTTON_Y_OFFSET = 53;

    private final UpgradeItemTooltip upgradeItemTooltip = new UpgradeItemTooltip(this.menu);
    private final ReforgeItemTooltip reforgeItemTooltip = new ReforgeItemTooltip(this.menu);


    private static final ResourceLocation BUTTON_LOCATION = MysticcraftMod.res("textures/gui/reforge_icon.png");
    public ReforgeAnvilScreen(ReforgeAnvilMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Override
    protected @NotNull String getTextureName() {
        return "reforging_anvil";
    }

    @Override
    protected void init() {
        super.init();
        this.addHoverTooltipAndImgButton(reforgeItemTooltip, BUTTON_LOCATION, this::reforgeUse);
        this.addHoverTooltipAndImgButton(upgradeItemTooltip, BUTTON_LOCATION, this::upgradeUse);
    }

    private void reforgeUse(Button ignored) {
        String exeRet = this.menu.handleButtonPress();
        if (exeRet == null) return;
        ModMessages.sendToServer(new ReforgingPacket(this.menu.getCapabilityProvider().getBlockPos(), exeRet));
    }
    private void upgradeUse(Button ignored) {
        this.menu.upgrade();
        ModMessages.sendToServer(new UpgradeItemPacket());
    }
}