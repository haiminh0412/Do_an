package com.example.car_management.pagination.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Data
@Getter
@Setter
public class Page implements Pageable {
    protected Integer limit;
    protected Integer offset;
    protected final Sort sort;
    protected Integer totalPages;
    protected Integer totalElements;


    public Page(Integer limit, Integer offset, Sort sort) {
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public Page(Integer limit, Integer offset) {
         this(limit, offset, Sort.unsorted());
    }

    public Integer getTotalPages() {
        return (int) Math.ceil(totalElements * 1.0 / limit);
    }

    @JsonIgnore
    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @JsonIgnore
    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        return (offset / limit) + 1;
    }

    @JsonIgnore
    @Override
    public int getPageSize() {
        return limit;
    }

    @JsonIgnore
    @Override
    public long getOffset() {
        return offset;
    }

    @JsonIgnore
    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return Pageable.super.getSortOr(sort);
    }

    @Override
    public Pageable next() {
        return new Page(getPageSize(), (int) (getOffset() + getPageSize()));
    }

    public Pageable previous() {
        return hasPrevious() ? new Page(getPageSize(), (int) (getOffset() - getPageSize())) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new Page(getPageSize(), 0);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new Page(getPageSize(), pageNumber * getPageSize());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }

    @Override
    public Limit toLimit() {
        return Pageable.super.toLimit();
    }

    @Override
    public OffsetScrollPosition toScrollPosition() {
        return Pageable.super.toScrollPosition();
    }
}
