package ru.vsu.cs.bolotskikh;

import java.util.UUID;

public class Passwords {
    public UUID id;
    public String password;
    public UUID key_id;

    public Passwords(UUID id, String password, UUID key_id) {
        this.id = id;
        this.password = password;
        this.key_id = key_id;
    }

    @Override
    public String toString() {
        return "ID=" + id + ", Пароль='" + password + '\'' + ", ID ключа=" + key_id + '}';
    }
}
