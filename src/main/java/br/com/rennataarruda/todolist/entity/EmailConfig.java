package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@Getter
@Entity
@Table(name = "EMAIL_CONFIG")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailConfig extends WithUpdatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "HOST", nullable = false, length = 150)
    private String host;

    @Column(name = "PORT", nullable = false)
    private Integer port;

    @Column(name = "USERNAME", length = 150)
    private String username;

    @Column(name = "PASSWORD", length = 512)
    private String password;

    @Column(name = "FROM_ADDRESS", nullable = false, length = 150)
    private String fromAddress;

    @Column(name = "FROM_NAME", length = 150)
    private String fromName;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "AUTH", nullable = false)
    private Boolean auth = false;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "START_TLS", nullable = false)
    private Boolean startTls = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "SSL", nullable = false)
    private Boolean ssl = false;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = false;

    public EmailConfig(
            String host,
            Integer port,
            String username,
            String password,
            String fromAddress,
            String fromName,
            Boolean auth,
            Boolean startTls,
            Boolean ssl,
            Boolean ativo
    ) {
        atualizar(host, port, username, password, fromAddress, fromName, auth, startTls, ssl, ativo);
    }

    public void atualizar(
            String host,
            Integer port,
            String username,
            String password,
            String fromAddress,
            String fromName,
            Boolean auth,
            Boolean startTls,
            Boolean ssl,
            Boolean ativo
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.auth = auth == null ? false : auth;
        this.startTls = startTls == null ? true : startTls;
        this.ssl = ssl == null ? false : ssl;
        this.ativo = ativo == null ? false : ativo;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void bloquear() {
        this.ativo = false;
    }
}
