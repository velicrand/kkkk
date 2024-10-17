package ru.vsu.cs.bolotskikh;

import java.util.UUID;

public class Keys {
    public UUID id;
    public String key;

    public Keys(UUID id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public String toString() {
        return "id=" + id + ", ключ='" + key + '\'';
    }
}
