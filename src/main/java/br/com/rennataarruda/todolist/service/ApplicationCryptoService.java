package br.com.rennataarruda.todolist.service;

import br.com.rennataarruda.todolist.config.ApplicationCryptoProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ApplicationCryptoService {

    private static final String ENCRYPTED_PREFIX = "{app-crypto-v1}";
    private static final String ENV_SECRET_NAME = "APP_CRYPTO_SECRET";
    private static final String AES_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ApplicationCryptoProperties properties;

    public ApplicationCryptoService(ApplicationCryptoProperties properties) {
        this.properties = properties;
    }

    public String encryptIfNeeded(String value) {
        if (!StringUtils.hasText(value) || isEncrypted(value)) {
            return value;
        }

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] payload = ByteBuffer.allocate(iv.length + cipherText.length)
                    .put(iv)
                    .put(cipherText)
                    .array();

            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível criptografar o valor informado", exception);
        }
    }

    public String decryptIfNeeded(String value) {
        if (!StringUtils.hasText(value) || !isEncrypted(value)) {
            return value;
        }

        try {
            byte[] payload = Base64.getDecoder().decode(value.substring(ENCRYPTED_PREFIX.length()));
            ByteBuffer buffer = ByteBuffer.wrap(payload);

            byte[] iv = new byte[IV_LENGTH_BYTES];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível descriptografar o valor informado", exception);
        }
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENCRYPTED_PREFIX);
    }

    private SecretKeySpec key() {
        try {
            byte[] key = MessageDigest.getInstance("SHA-256")
                    .digest(resolveSecret().getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, AES_ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", exception);
        }
    }

    private String resolveSecret() {
        if (StringUtils.hasText(properties.getSecret())) {
            return properties.getSecret();
        }

        String environmentSecret = System.getenv(ENV_SECRET_NAME);
        if (StringUtils.hasText(environmentSecret)) {
            return environmentSecret;
        }

        throw new IllegalStateException("Chave de criptografia da aplicação não configurada: informe app.crypto.secret ou " + ENV_SECRET_NAME);
    }
}
