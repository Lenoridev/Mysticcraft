package net.kapitencraft.mysticcraft.networking.packets.C2S;

import net.kapitencraft.mysticcraft.item.combat.weapon.ranged.bow.ShortBowItem;
import net.kapitencraft.mysticcraft.networking.packets.ModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseShortBowPacket implements ModPacket {

    public UseShortBowPacket() {
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(()-> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (player.getMainHandItem().getItem() instanceof ShortBowItem shortBowItem) {
                shortBowItem.releaseUsing(player.getMainHandItem(), player.level, player, -1);
            }
        });
        return true;
    }
}
