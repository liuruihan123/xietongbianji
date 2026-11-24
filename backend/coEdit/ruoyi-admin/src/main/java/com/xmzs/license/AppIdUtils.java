package com.xmzs.license;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

@Component
public class AppIdUtils {
    // 默认路径（兼容旧版本）
    private static final String DEFAULT_APP_ID_FILE = "config/app-id.properties";
    private static final String APP_ID_KEY = "app.id";

    // 从配置文件注入的路径
    @Value("${app-id.file:config/app-id.properties}")
    private String appIdFileFromConfig;

    // 静态变量，用于存储实际使用的路径（支持静态方法调用）
    private static String APP_ID_FILE;

    /**
     * Spring容器初始化时设置路径
     */
    @PostConstruct
    public void init() {
        APP_ID_FILE = appIdFileFromConfig;
        System.out.println("APP_ID文件路径已配置为: " + APP_ID_FILE);
    }

    /**
     * 获取应用ID（静态方法，兼容独立工具类使用）
     *
     * @return 应用ID
     */
    public static String getAppId() {
        return getAppId(APP_ID_FILE != null ? APP_ID_FILE : DEFAULT_APP_ID_FILE);
    }

    /**
     * 获取应用ID（指定路径，用于独立工具类）
     *
     * @param filePath app-id.properties 文件路径
     * @return 应用ID
     */
    public static String getAppId(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            filePath = DEFAULT_APP_ID_FILE;
        }

        Path appIdPath = Paths.get(filePath);

        // 如果文件存在，读取APP_ID
        if (Files.exists(appIdPath)) {
            try (InputStream in = Files.newInputStream(appIdPath)) {
                Properties prop = new Properties();
                prop.load(in);
                String appId = prop.getProperty(APP_ID_KEY);
                if (appId != null && !appId.isEmpty()) {
                    return appId;
                }
            } catch (IOException e) {
                throw new RuntimeException("读取应用ID失败: " + filePath, e);
            }
        }

        // 文件不存在或读取失败，生成新的APP_ID
        try {
            Path parentDir = appIdPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建config目录失败: " + filePath, e);
        }

        String appId = UUID.randomUUID().toString();
        try (OutputStream out = Files.newOutputStream(appIdPath)) {
            Properties prop = new Properties();
            prop.setProperty(APP_ID_KEY, appId);
            prop.store(out, "Auto-generated app ID, do not modify");
            System.out.println("已生成新的APP_ID: " + appId + "，保存位置: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("生成应用ID失败: " + filePath, e);
        }
        return appId;
    }

    /**
     * 获取当前配置的文件路径
     *
     * @return 文件路径
     */
    public String getConfiguredFilePath() {
        return appIdFileFromConfig;
    }
}
