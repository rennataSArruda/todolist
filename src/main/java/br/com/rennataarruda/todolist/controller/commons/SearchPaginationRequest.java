package br.com.rennataarruda.todolist.controller.commons;

public record SearchPaginationRequest<Filter>(
        Filter filter,
        Integer page,
        Integer size
) {

    public int pageOrDefault() {
        return page == null ? 0 : page;
    }

    public int sizeOrDefault() {
        return size == null ? 10 : size;
    }
}
