export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface SearchRequest<TFilter = Record<string, unknown>> extends PageRequest {
  filter?: TFilter;
}
