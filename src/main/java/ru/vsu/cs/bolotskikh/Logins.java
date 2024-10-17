package ru.vsu.cs.bolotskikh;

import java.util.UUID;

public class Logins {
    public int id;
    public String login;
    public UUID pass_id;

    public Logins(int id, String login, UUID pass_id) {
        this.id = id;
        this.login = login;
        this.pass_id = pass_id;
    }

    @Override
    public String toString() {
        return "ID=" + id + ", Логин='" + login + '\'' + ", ID пароля=" + pass_id;
    }
}
