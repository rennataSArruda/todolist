package br.com.rennataarruda.todolist.security;

import br.com.rennataarruda.todolist.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

@Service
public class UserAuthorityService {

    private static final String ROOT_AUTHORITY = "ROOT";

    public Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        LinkedHashSet<String> authorities = new LinkedHashSet<>();

        if (usuario.isRoot()) {
            authorities.add(ROOT_AUTHORITY);
        } else if (usuario.getPerfil() != null) {
            usuario.getPerfil().getAutorizacoes().stream()
                    .map(autorizacao -> autorizacao.getAuthority())
                    .forEach(authorities::add);
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
