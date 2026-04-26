package br.com.rennataarruda.todolist.entity;


import br.com.rennataarruda.todolist.entity.convert.BooleanToSNConverter;
import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.NumericBooleanConverter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "USUARIO")
public class Usuario extends WithUpdatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 100, updatable = false)
    private String username;

    @Column(name = "NAME", length = 150)
    private String name;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Convert(converter = BooleanToSNConverter.class)
    @Column(name = "ROOT", nullable = false, length = 1, columnDefinition = "CHAR(1 CHAR)")
    private Boolean root;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFIL_ID", nullable = false)
    private Perfil perfil;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    public Usuario(String username, String name, String password, Perfil perfil) {
        this(username, name, password, Boolean.FALSE, perfil);
    }

    public Usuario(String username, String name, String password, Boolean root, Perfil perfil) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.root = root;
        this.perfil = perfil;
    }

    public void atualizar(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public void alterarSenha(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void alternarAtivo() {
        this.ativo = !Boolean.TRUE.equals(this.ativo);
    }

    public boolean isRoot() {
        return Boolean.TRUE.equals(root);
    }
}
