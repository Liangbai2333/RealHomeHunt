package site.liangbai.realhomehunt.locale;

import org.jetbrains.annotations.NotNull;

public interface ILocale {
    @NotNull String asString(String node, Object... args);
}
