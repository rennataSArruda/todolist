package br.com.rennataarruda.todolist.entity;

import br.com.rennataarruda.todolist.entity.commons.WithUpdatedAt;
import jakarta.persistence.Column;
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

@Getter
@Entity
@Table(name = "CONFIGURACOES_GERAIS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfiguracoesGerais extends WithUpdatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFIL_PADRAO_PRIMEIRO_ACESSO_ID")
    private Perfil perfilPadraoPrimeiroAcesso;

    public ConfiguracoesGerais(Perfil perfilPadraoPrimeiroAcesso) {
        this.perfilPadraoPrimeiroAcesso = perfilPadraoPrimeiroAcesso;
    }

    public void atualizarPerfilPadraoPrimeiroAcesso(Perfil perfilPadraoPrimeiroAcesso) {
        this.perfilPadraoPrimeiroAcesso = perfilPadraoPrimeiroAcesso;
    }
}
