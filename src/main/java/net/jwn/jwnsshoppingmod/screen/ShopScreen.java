package net.jwn.jwnsshoppingmod.screen;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.NumberFormat;
import java.util.Locale;

public class ShopScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "textures/gui/shop_gui.png");
    private static final ResourceLocation ITEM = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "shop_item");
    private static final ResourceLocation ITEM_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "shop_item_highlighted");
    private static final ResourceLocation BUY_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_button");
    private static final ResourceLocation BUY_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_button_highlighted");
    private static final ResourceLocation PLUS_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "plus_button");
    private static final ResourceLocation PLUS_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "plus_button_highlighted");
    private static final ResourceLocation MINUS_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "minus_button");
    private static final ResourceLocation MINUS_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "minus_button_highlighted");

    private static final ResourceLocation COIN = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "coin");
    private int coin;

    public ShopScreen(int coin) {
        super(Component.translatable("gui.jwnsshoppingmod.shop.title"));
        this.coin = coin;
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

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        for (int i = 0; i < 4; i ++) {
            addRenderableWidget(new ImageButton(
                    x + 8, y + 20 + i * 27, ITEM_WIDTH, ITEM_HEIGHT, new WidgetSprites(ITEM, ITEM_HIGHLIGHTED),
                    button -> {}));
        }

        ImageButton buyButton = new ImageButton(
                x + 108, y + 142, BUY_WIDTH, BUY_HEIGHT, new WidgetSprites(BUY_BUTTON, BUY_BUTTON_HIGHLIGHTED),
                button -> {});
        addRenderableWidget(buyButton);

        ImageButton minusButton = new ImageButton(
                x + 13, y + 151, 7, 7, new WidgetSprites(MINUS_BUTTON, MINUS_BUTTON_HIGHLIGHTED),
                button -> count--);
        addRenderableWidget(minusButton);

        ImageButton plusButton = new ImageButton(
                x + 34, y + 151, 7, 7, new WidgetSprites(PLUS_BUTTON, PLUS_BUTTON_HIGHLIGHTED),
                button -> count++);
        addRenderableWidget(plusButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        setFocused(null);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COIN, x + 58, y + 131, 7, 7);

        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(this.font, this.title, x + 8, y + 8, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal("항목이름"), x + 36, y + 23, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal("3 / 10"), x + 36, y + 33, 0xFF000000, false);

        String text = NumberFormat.getInstance(Locale.US).format(coin);
        graphics.drawString(this.font, Component.literal(text), x + 68, y + 130, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal(String.valueOf(count)), x + 27 - this.font.width(String.valueOf(count)) / 2, y + 150, 0xFF000000, false);
    }
}
