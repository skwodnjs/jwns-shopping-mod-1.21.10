package net.jwn.jwnsshoppingmod.screen;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.networking.packet.BuyItemC2SPacket;
import net.jwn.jwnsshoppingmod.networking.packet.EditCommentC2SPacket;
import net.jwn.jwnsshoppingmod.shop.PlayerBlockTimerData;
import net.jwn.jwnsshoppingmod.shop.ShopItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "textures/gui/shop_gui.png");
    private static final ResourceLocation ITEM = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "shop_item");
    private static final ResourceLocation ITEM_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "shop_item_highlighted");
    private static final ResourceLocation BUY_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_button");
    private static final ResourceLocation BUY_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_button_highlighted");
    private static final ResourceLocation BUY_BUTTON_DISABLED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_button_disabled");
    private static final ResourceLocation PLUS_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "plus_button");
    private static final ResourceLocation PLUS_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "plus_button_highlighted");
    private static final ResourceLocation MINUS_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "minus_button");
    private static final ResourceLocation MINUS_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "minus_button_highlighted");

    private static final ResourceLocation COIN = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "coin");

    private final List<ShopItem> shopItems;
    private final List<ShopItem> displayShopItems = new ArrayList<>();
    private int startIndex = 0;
    private static final int VISIBLE = 4;

    private final int coin;
    private int tick;
    private ShopItem selected;
    private int totalCost;

    public ShopScreen(int coin, List<ShopItem> shopItems, int time) {
        super(Component.translatable("gui.jwnsshoppingmod.shop.title"));
        this.coin = coin;
        this.shopItems = shopItems;
        this.tick = PlayerBlockTimerData.RESET_TIMER - time;
        this.selected = ShopItem.empty();
        this.totalCost = 0;
        updateDisplayItems();
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 176;
    private static final int DRAW_HEIGHT = 166;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 25;
    private static final int BUY_WIDTH = 24;
    private static final int BUY_HEIGHT = 16;

    private int count = 0;

    private int x;
    private int y;

    private void updateDisplayItems() {
        displayShopItems.clear();

        int n = shopItems.size();
        if (n == 0) return;

        startIndex = Math.max(0, Math.min(startIndex, Math.max(0, n - VISIBLE)));

        int end = Math.min(n, startIndex + VISIBLE);
        displayShopItems.addAll(shopItems.subList(startIndex, end));
    }

    ImageButton buyButton;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        for (int i = 0; i < 4; i ++) {
            int finalI = i;
            addRenderableWidget(new ImageButton(
                    x + 8, y + 20 + i * 27, ITEM_WIDTH, ITEM_HEIGHT, new WidgetSprites(ITEM, ITEM_HIGHLIGHTED),
                    button -> {
                        selected = displayShopItems.get(finalI);
                        count = 0;
                    }));
        }

        assert Minecraft.getInstance().player != null;
        buyButton = new ImageButton(
                x + 108, y + 142, BUY_WIDTH, BUY_HEIGHT, new WidgetSprites(BUY_BUTTON, BUY_BUTTON_DISABLED, BUY_BUTTON_HIGHLIGHTED),
                button -> {
                    if (tick == 0) {
                        Minecraft.getInstance().player.displayClientMessage(Component.translatable("gui.jwnsshoppingmod.shop.reopen"), false);
                        onClose();
                    } else {
                        if (!selected.item().equals(Items.AIR) && !(count == 0)) {
                            BuyItemC2SPacket packet = new BuyItemC2SPacket(selected, count);
                            ClientPacketDistributor.sendToServer(packet);
                            Minecraft.getInstance().player.displayClientMessage(
                                    Component.translatable("gui.jwnsshoppingmod.shop.buy", selected.item().getName().getString()), false);
                            onClose();
                        }
                    }
                });
        addRenderableWidget(buyButton);

        ImageButton minusButton = new ImageButton(
                x + 13, y + 151, 7, 7, new WidgetSprites(MINUS_BUTTON, MINUS_BUTTON_HIGHLIGHTED),
                button -> {
                    count--;
                });
        addRenderableWidget(minusButton);

        ImageButton plusButton = new ImageButton(
                x + 34, y + 151, 7, 7, new WidgetSprites(PLUS_BUTTON, PLUS_BUTTON_HIGHLIGHTED),
                button -> {
                    count++;
                });
        addRenderableWidget(plusButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        setFocused(null);
        buyButton.active = !(count > selected.remaining()) && (coin >= totalCost);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COIN, x + 58, y + 131, 7, 7);

        super.render(graphics, mouseX, mouseY, partialTicks);

        for (int i = 0; i < 4; i++) {
            ItemStack stack = new ItemStack(displayShopItems.get(i).item(), displayShopItems.get(i).bundleSize());
            graphics.renderItem(stack, x + 14, y + 24 + i * 27);
            graphics.renderItemDecorations(this.font, stack, x + 14, y + 24 + i * 27);
//            if (isMouseOver(x, y, 16, 16, mouseX, mouseY)) {
//                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
//            }
            graphics.drawString(this.font, stack.getItemName(), x + 36, y + 23 + i * 27, 0xFF000000, false);
            graphics.drawString(this.font, Component.literal(displayShopItems.get(i).remaining() + "/" + displayShopItems.get(i).maxPurchase()), x + 36, y + 33 + i * 27, 0xFF000000, false);
            String text = displayShopItems.get(i).price() + Component.translatable("gui.jwnsshoppingmod.shop.coin").getString();
            graphics.drawString(this.font, text, x + 120, y + 29 + i * 27, 0xFF000000, false);
        }

        ItemStack stack = new ItemStack(selected.item(), selected.bundleSize());
        graphics.renderItem(stack, x + 19, y + 131);
        graphics.renderItemDecorations(this.font, stack, x + 19, y + 131);

        graphics.drawString(this.font, this.title, x + 8, y + 8, 0xFF000000, false);

        int totalSeconds = tick / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        Component text1;
        if (minutes > 0) {
            text1 = Component.translatable("gui.jwnsshoppingmod.shop.left_time_with_min", minutes, seconds);
        } else {
            text1 = Component.translatable("gui.jwnsshoppingmod.shop.left_time_without_min", seconds);
        }
        graphics.drawString(this.font, text1, x + 60, y + 8, 0xFF000000, false);

        totalCost = selected.price() * count;
        String text2 = NumberFormat.getInstance(Locale.US).format(totalCost) + Component.translatable("gui.jwnsshoppingmod.shop.coin").getString();
        graphics.drawString(this.font, Component.literal(text2), x + 61, y + 147, 0xFF000000, false);

        String text3 = NumberFormat.getInstance(Locale.US).format(coin);
        graphics.drawString(this.font, text3, x + 68, y + 130, 0xFF000000, false);

        graphics.drawString(this.font, Component.literal(String.valueOf(count)), x + 27 - this.font.width(String.valueOf(count)) / 2, y + 150, 0xFF000000, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (!isMouseOverListArea(mouseX, mouseY)) return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);

        int n = shopItems.size();
        int maxStart = Math.max(0, n - VISIBLE);
        if (maxStart == 0) return true;

        int dir = deltaY > 0 ? -1 : 1;
        startIndex = Math.max(0, Math.min(startIndex + dir, maxStart));

        updateDisplayItems();
        return true;
    }

    private boolean isMouseOverListArea(double mouseX, double mouseY) {
        int listX = x + 8;
        int listY = y + 20;
        int listW = 143;
        int listH = 106;
        return mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + listH;
    }

    @Override
    public void tick() {
        if (tick > 0) tick -= 1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
