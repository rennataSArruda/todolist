package br.com.rennataarruda.todolist.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Classe utilitaria nao deve ser instanciada");
    }

    public static String hashSHA256(String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo SHA-256 nao disponivel", exception);
        }
    }
}
