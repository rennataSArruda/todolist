package br.com.rennataarruda.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Embeddable
public class PerfilPapelPermissaoId implements Serializable {

    @Column(name = "PERFIL_ID")
    private Long perfilId;

    @Column(name = "PAPEL_ID")
    private Long papelId;

    @Column(name = "PERMISSAO_ID")
    private Long permissaoId;
}
