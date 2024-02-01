package me.xginko.serverrestart.common;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LanguageCache {

    @NotNull Component getTranslation(String path, String defaultTranslation);
    @NotNull Component getTranslation(String path, String defaultTranslation, String comment);
    @NotNull List<Component> getListTranslation(String path, List<String> defaultTranslation);
    @NotNull List<Component> getListTranslation(String path, List<String> defaultTranslation, String comment);

}
