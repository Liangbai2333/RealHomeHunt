package site.liangbai.realhomehunt.storage;

import org.jetbrains.annotations.NotNull;

public enum StorageType {
    SQLITE,
    YAML,
    MYSQL;

    public static StorageType getDefault() {
        return YAML;
    }

    @NotNull
    public static StorageType matchStorageType(String storageType) {
        if (storageType == null) return getDefault();

        for (StorageType type : values()) {
            if (type.name().equalsIgnoreCase(storageType)) return type;
        }

        return getDefault();
    }
}
