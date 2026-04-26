package br.com.rennataarruda.todolist.security.authorization;

public enum PapelCodigo {
    USUARIO,
    TAREFA_CATEGORIA,
    TAREFA;

    public String comporAuthority(PermissaoCodigo permissao) {
        return name() + "_" + permissao.name();
    }
}
