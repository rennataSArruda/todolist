package br.com.rennataarruda.todolist;

import br.com.rennataarruda.todolist.repository.BlacklistedTokenRepository;
import br.com.rennataarruda.todolist.repository.PerfilRepository;
import br.com.rennataarruda.todolist.repository.RefreshTokenRepository;
import br.com.rennataarruda.todolist.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
		"spring.config.import=",
		"spring.autoconfigure.exclude="
				+ "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
		"spring.main.lazy-initialization=true",
		"app.security.jwt.secret=12345678901234567890123456789012"
})
class TodolistApplicationTests {

	@MockBean
	private UsuarioRepository usuarioRepository;

	@MockBean
	private RefreshTokenRepository refreshTokenRepository;

	@MockBean
	private BlacklistedTokenRepository blacklistedTokenRepository;

	@MockBean
	private PerfilRepository perfilRepository;

	@Test
	void contextLoads() {
	}

}
