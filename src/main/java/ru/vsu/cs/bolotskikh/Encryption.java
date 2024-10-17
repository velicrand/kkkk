package ru.vsu.cs.bolotskikh;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Encryption {
    KeyGenerator keyGenerator;


    public List<String> encrypting(String pass) throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Указываем размер ключа
        List<String> res = new ArrayList<>();
        SecretKey secretKey = keyGenerator.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        res.add(encodedKey);

        Cipher cipher = Cipher.getInstance("AES");


        cipher.init(Cipher.ENCRYPT_MODE, secretKey);;
        byte[] encryptedMessage = cipher.doFinal(pass.getBytes());

        // Преобразование зашифрованного сообщения в строку для отправки
        res.add(Base64.getEncoder().encodeToString(encryptedMessage));
        return res;
    }

    public String decoding(String pass, String secretKey) throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Указываем размер ключа
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, originalKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(pass));
        String decryptedMessage = new String(decryptedBytes);

        return decryptedMessage;
    }
}
