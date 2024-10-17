package ru.vsu.cs.bolotskikh;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void clearConsole(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) throws SQLException {
        Scanner scan = new Scanner(System.in);
        Boolean run = true;
        try {
            DataBase.createDbLogins();
            DataBase.createDbPass();
            DataBase.createDbKeys();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (run) {
            System.out.println("1. Ввести логин \n2. Добаить новый профиль\n3. Посмотреть все логины\n4. Завершить программу");
            try {
                switch (scan.nextInt()) {
                    case 1 -> {
                        clearConsole();
                        System.out.print("Ввести логин: ");
                        String login = scan.next();
                        if (!DataBase.allLogins().containsKey(login)) {
                            System.out.println("Такого логина не существует");
                            break;
                        }
                        System.out.println("Описание: " +  DataBase.getDescription(login));

                        System.out.println("1. Вывести все пароли\n2. Добавить новый пароль\n3. Удалить все записи\n4. Назад");
                        switch (scan.nextInt()) {
                            case 1 -> {
                                List<String> list = DataBase.allLoginsPass(login);
                                if (list != null) {
                                    for (String st : list) {
                                        System.out.println(st);
                                    }
                                }
                            }
                            case 2 -> {
                                System.out.print("Введите пароль (введите 0, чтобы отменить действие): ");
                                String password = scan.next();
                                if (!password.equals("0")) {
                                    DataBase.addNewPass(login, password);
                                }
                            }
                            case 3 -> {
                                System.out.print("Вы уверены? Если да то напишите (yes): ");
                                if (scan.next().equals("yes")) {
                                    DataBase.delLogin(login);
                                }
                            }
                        }


                    }
                    case 2 -> {
                        System.out.print("Введите логин: ");
                        String login = scan.next();
                        System.out.print("Введите описание: ");
                        DataBase.addNewLogin(login, scan.next());
                    }
                    case 3 -> {
                        Map<String, String> list = DataBase.allLogins();
                        for (String el : list.keySet()) {
                            System.out.println(el + ": " + ((list.get(el).isEmpty()) ? "Описание отсутствует" : list.get(el)));
                        }
                    }
                    case 4 -> {
                        run = false;
                    }
                }
            }catch (Exception e){
                System.out.println("Ведены неверные данные.");
                scan.next();
            }
        }

    }
}