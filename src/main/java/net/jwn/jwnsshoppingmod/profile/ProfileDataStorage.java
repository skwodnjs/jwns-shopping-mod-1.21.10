package net.jwn.jwnsshoppingmod.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileDataStorage {
    private static final String FOLDER_NAME = "jwnsshoppingmod/profiles";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // 월드(서버) 기준 프로필 폴더 경로
    private static Path getProfileDir(MinecraftServer server) {
        // world root 디렉토리 + /jwnsshoppingmod/profiles
        Path worldRoot = server.getWorldPath(LevelResource.ROOT);
        return worldRoot.resolve(FOLDER_NAME);
    }

    // 플레이어 파일 경로
    private static Path getProfileFile(MinecraftServer server, String name) {
        return getProfileDir(server).resolve(name + ".json");
    }

    // ===== 저장 =====
    public static void saveByPlayerName(MinecraftServer server, String playerName, ProfileData data) {
        Path dir = getProfileDir(server);
        Path file = getProfileFile(server, playerName);

        try {
            Files.createDirectories(dir);

            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            // 실제 코드에서는 로그로 남기는 게 좋음
            e.printStackTrace();
        }
    }

    public static ProfileData loadByPlayerName(MinecraftServer server, String playerName) {
        Path baseDir = server.getWorldPath(LevelResource.ROOT)
                .resolve("jwnsshoppingmod")
                .resolve("profiles");

        String fileName = playerName + ".json"; // ⭐ 파일 이름이 이름 기반
        Path file = baseDir.resolve(fileName);

        if (!Files.exists(file)) {
            return null; // 저장된 데이터 없음
        }

        try {
            String json = Files.readString(file);
            return GSON.fromJson(json, ProfileData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
