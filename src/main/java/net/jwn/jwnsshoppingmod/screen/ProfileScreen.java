package net.jwn.jwnsshoppingmod.screen;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ProfileScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "textures/gui/profile_gui.png");
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "button3");
    private static final ResourceLocation BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "button3_highlighted");
    private final ProfileData profileData;

    public ProfileScreen(ProfileData profileData) {
        // We use name as the title of the screen
        super(Component.literal(profileData.getName()));
        this.profileData = profileData;
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 110;
    private static final int DRAW_HEIGHT = 166;

    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 15;

    final int PROFILE_GAP = 57;

    int x;
    int y;
    int button_x;
    int button_y;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;
        button_x = (this.width - BUTTON_WIDTH) / 2;
        button_y = y + 105;

        ImageButton imageButton1 = new ImageButton(
                button_x, button_y, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> this.onClose());
        addRenderableWidget(imageButton1);

        ImageButton imageButton2 = new ImageButton(
                button_x, button_y + 18, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> this.onClose());
        addRenderableWidget(imageButton2);

        ImageButton imageButton3 = new ImageButton(
                button_x, button_y + 36, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> this.onClose());
        addRenderableWidget(imageButton3);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(this.font, this.title, x + PROFILE_GAP, y + 12, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal("LV. " + profileData.getLevel()), x + PROFILE_GAP, y + 23, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal(profileData.getAlias()), x + PROFILE_GAP, y + 34, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal(profileData.getCoins() + " COIN"), x + PROFILE_GAP, y + 45, 0xFF000000, false);

        String timeSuffix = Component.translatable("gui.jwnsshoppingmod.profile." + (profileData.getIsMinute() ? "minute" : "hour")).getString();
        graphics.drawString(this.font, Component.literal(profileData.getTime() + timeSuffix + " 전 접속 종료"), x + 10, y + 57, 0xFF000000, false);

        Component text = Component.literal(profileData.getComment());
        int maxWidth = DRAW_WIDTH - 26;
        int startX = x + 13;
        int startY = y + 73;

        List<FormattedCharSequence> lines = wrapByCharacter(text, maxWidth, this.font);

        int lineHeight = this.font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(
                    this.font,
                    lines.get(i),
                    startX,
                    startY + (i * lineHeight) + 1,
                    0xFF000000,
                    false
            );
        }

        Component text1 = Component.literal("방명록 보기");
        Component text2 = Component.literal("교환 신청");
        Component text3 = Component.literal("귓속말");

        graphics.drawString(this.font, text1, (this.width - font.width(text1)) / 2, button_y + 3, 0xFF000000, false);
        graphics.drawString(this.font, text2, (this.width - font.width(text2)) / 2, button_y + 21, 0xFF000000, false);
        graphics.drawString(this.font, text3, (this.width - font.width(text3)) / 2, button_y + 39, 0xFF000000, false);
    }

    public List<FormattedCharSequence> wrapByCharacter(Component text, int maxWidth, Font font) {
        String raw = text.getString();
        List<FormattedCharSequence> result = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            currentLine.append(c);

            int width = font.width(currentLine.toString());
            if (width > maxWidth) {
                currentLine.deleteCharAt(currentLine.length() - 1);
                result.add(font.split(Component.literal(currentLine.toString()), maxWidth).get(0));
                currentLine.setLength(0);
                currentLine.append(c);
            }
        }
        if (!currentLine.isEmpty()) {
            result.add(font.split(Component.literal(currentLine.toString()), maxWidth).get(0));
        }
        return result;
    }
}
