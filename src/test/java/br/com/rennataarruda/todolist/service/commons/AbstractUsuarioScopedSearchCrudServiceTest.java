package br.com.rennataarruda.todolist.service.commons;

import br.com.rennataarruda.todolist.entity.commons.UsuarioScopedEntity;
import br.com.rennataarruda.todolist.repository.commons.BaseRepository;
import br.com.rennataarruda.todolist.security.AuthenticatedUserProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractUsuarioScopedSearchCrudServiceTest {

    @Mock
    private BaseRepository<TestEntity, Long> repository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldSetAuthenticatedUserIdOnCreate() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(77L);
        when(repository.save(any(TestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestDto dto = service.create(new TestDto(null, "Nova tarefa", 999L));

        assertThat(dto.usuarioId()).isEqualTo(77L);
    }

    @Test
    void shouldKeepAuthenticatedUserIdOnUpdate() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        TestEntity existing = new TestEntity(10L, "Antiga", 88L);

        when(authenticatedUserProvider.currentUserId()).thenReturn(77L);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<TestEntity>>any()))
                .thenReturn(Optional.of(existing));
        when(repository.save(any(TestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestDto dto = service.update(10L, new TestDto(10L, "Atualizada", 999L));

        assertThat(dto.nome()).isEqualTo("Atualizada");
        assertThat(dto.usuarioId()).isEqualTo(77L);
    }

    @Test
    void shouldUseScopedSpecificationOnGet() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(77L);
        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<TestEntity>>any()))
                .thenReturn(List.of(new TestEntity(1L, "Tarefa", 77L)));

        List<TestDto> result = service.get();

        assertThat(result).hasSize(1);
        assertSpecificationFiltersByAuthenticatedUser(captureFindAllSpecification());
    }

    @Test
    void shouldUseScopedSpecificationOnSearch() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(77L);
        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<TestEntity>>any()))
                .thenReturn(List.of(new TestEntity(1L, "Tarefa", 77L)));

        service.search("Tar");

        assertSpecificationFiltersByAuthenticatedUser(captureFindAllSpecification());
    }

    @Test
    void shouldUseScopedSpecificationOnSearchPagination() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        when(authenticatedUserProvider.currentUserId()).thenReturn(77L);
        when(repository.findAll(
                org.mockito.ArgumentMatchers.<Specification<TestEntity>>any(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(new TestEntity(1L, "Tarefa", 77L))));

        service.searchPagination("Tar", 2, 20);

        assertSpecificationFiltersByAuthenticatedUser(captureFindAllPageSpecification());
    }

    @Test
    void shouldReturnNotFoundWhenScopedIdIsNotFound() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<TestEntity>>any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Teste nao encontrado");
    }

    @Test
    void shouldDeleteUsingScopedIdLookup() {
        TestService service = new TestService(repository, authenticatedUserProvider);
        TestEntity entity = new TestEntity(10L, "Tarefa", 77L);
        when(repository.findOne(org.mockito.ArgumentMatchers.<Specification<TestEntity>>any()))
                .thenReturn(Optional.of(entity));

        service.delete(10L);

        verify(repository).delete(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void assertSpecificationFiltersByAuthenticatedUser(Specification<TestEntity> specification) {
        Root<TestEntity> root = mock(Root.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<Object> usuarioIdPath = mock(Path.class);
        Predicate usuarioPredicate = mock(Predicate.class);

        when(root.get("usuarioId")).thenReturn(usuarioIdPath);
        when(criteriaBuilder.equal(usuarioIdPath, 77L)).thenReturn(usuarioPredicate);

        specification.toPredicate(root, null, criteriaBuilder);

        verify(root).get("usuarioId");
        verify(criteriaBuilder).equal(usuarioIdPath, 77L);
    }

    private Specification<TestEntity> captureFindAllSpecification() {
        ArgumentCaptor<Specification<TestEntity>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(captor.capture());
        return captor.getValue();
    }

    private Specification<TestEntity> captureFindAllPageSpecification() {
        ArgumentCaptor<Specification<TestEntity>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(captor.capture(), any(Pageable.class));
        return captor.getValue();
    }

    private static class TestService extends AbstractUsuarioScopedSearchCrudService<TestEntity, Long, TestDto, String> {

        protected TestService(
                BaseRepository<TestEntity, Long> repository,
                AuthenticatedUserProvider authenticatedUserProvider
        ) {
            super(repository, authenticatedUserProvider);
        }

        @Override
        protected TestDto toDto(TestEntity entity) {
            return new TestDto(entity.id, entity.nome, entity.usuarioId);
        }

        @Override
        protected TestEntity toNewEntity(TestDto dto) {
            return new TestEntity(dto.id(), dto.nome(), dto.usuarioId());
        }

        @Override
        protected void updateEntity(TestEntity entity, TestDto dto) {
            entity.nome = dto.nome();
        }

        @Override
        protected String notFoundMessage() {
            return "Teste nao encontrado";
        }

        @Override
        protected Predicate idEquals(Root<TestEntity> root, CriteriaBuilder criteriaBuilder, Long id) {
            return criteriaBuilder.equal(root.get("id"), id);
        }

        @Override
        protected void addSearchPredicates(
                List<Predicate> predicates,
                Root<TestEntity> root,
                CriteriaBuilder criteriaBuilder,
                String filter
        ) {
            if (filter != null) {
                predicates.add(criteriaBuilder.equal(root.get("nome"), filter));
            }
        }
    }

    private static class TestEntity implements UsuarioScopedEntity {

        private Long id;
        private String nome;
        private Long usuarioId;

        private TestEntity(Long id, String nome, Long usuarioId) {
            this.id = id;
            this.nome = nome;
            this.usuarioId = usuarioId;
        }

        @Override
        public Long getUsuarioId() {
            return usuarioId;
        }

        @Override
        public void definirUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }
    }

    private record TestDto(Long id, String nome, Long usuarioId) {
    }
}
