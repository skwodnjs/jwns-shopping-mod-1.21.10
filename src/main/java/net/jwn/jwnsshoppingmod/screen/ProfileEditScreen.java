package net.jwn.jwnsshoppingmod.screen;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.networking.packet.EditCommentC2SPacket;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ProfileEditScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "textures/gui/profile_edit_gui.png");
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "button3");
    private static final ResourceLocation BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "button3_highlighted");
    private final ProfileData profileData;
    private static String comment = "";

    public ProfileEditScreen(ProfileData profileData) {
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

        ImageButton imageButton2 = new ImageButton(
                button_x, button_y + 18, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> this.onClose());
        addRenderableWidget(imageButton2);

        ImageButton imageButton3 = new ImageButton(
                button_x, button_y + 36, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> {
                    assert Minecraft.getInstance().player != null;
                    EditCommentC2SPacket packet = new EditCommentC2SPacket(comment);
                    ClientPacketDistributor.sendToServer(packet);
                    this.onClose();
                });
        addRenderableWidget(imageButton3);

        int startX = x + 10;
        int startY = y + 53;
        MultiLineEditBox editBox = MultiLineEditBox.builder().build(
                this.font, 90, 66, Component.empty()
        );
        editBox.setX(startX);
        editBox.setY(startY);
        editBox.setValueListener(text -> comment = text);
        addRenderableWidget(editBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, TEXTURE, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        super.render(graphics, mouseX, mouseY, partialTicks);

        Component text = Component.literal(profileData.getComment());
        int maxWidth = DRAW_WIDTH - 26;
        int startX = x + 13;
        int startY = y + 24;

        graphics.drawString(this.font, this.title, startX, y + 8, 0xFF000000, false);

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

        Component text2 = Component.literal("나가기");
        Component text3 = Component.literal("저장하기");

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
