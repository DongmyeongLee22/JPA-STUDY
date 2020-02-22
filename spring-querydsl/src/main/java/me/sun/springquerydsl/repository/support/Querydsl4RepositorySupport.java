package me.sun.springquerydsl.repository.support;

/**
 * Created by Stranger on 2020/02/22
 */

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

/** Querydsl 지원 클래스 직접 만들기
 * - QuerydslRepositorySupport가 지닌 한계를 극복하기 위해 Querydsl 지원클래스를 직접 만든다.
 * >> 장점 <<
 * 1. 스프링 데이터가 제공하는 페이징을 편리하게 변환
 * 2. 페이징과 카운트 쿼리 분리 기능
 * 3. 스프링 데이터 Sort 지원
 * 4. select(), selectFrom()으로 시작 가능
 * 5. EntityManger, QueryFactory 제공
 */

@Getter
@Repository
public class Querydsl4RepositorySupport {

    private final Class<?> domainClass;
    private Querydsl querydsl;
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    public Querydsl4RepositorySupport(Class<?> domainClass){
        Assert.notNull(domainClass, "Domain class mush not be null!");
        this.domainClass = domainClass;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager){
        Assert.notNull(entityManager, "EntityManager must not be null!");

        // Sort를 위해서 필요한 설
        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
        EntityPath path = resolver.createPath(entityInformation.getJavaType());

        this.entityManager = entityManager;
        this.querydsl = new Querydsl(entityManager, new PathBuilder<>(path.getType(), path.getMetadata()));
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @PostConstruct
    public void validate(){
        Assert.notNull(entityManager, "EntityManager must not be null");
        Assert.notNull(querydsl, "Querydsl must not be null");
        Assert.notNull(queryFactory, "JpaQueryFactory must not be null");
    }

    protected <T> JPAQuery<T> select(Expression<T> expr){
        return getQueryFactory().select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(EntityPath<T> from){
        return getQueryFactory().selectFrom(from);
    }

    protected <T> Page<T> applyPagination(Pageable pageable, Function<JPAQueryFactory, JPAQuery<T>> contentQuery){
        JPAQuery<T> jpaQuery = contentQuery.apply(getQueryFactory());
        List<T> content = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();
        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }
    protected <T> Page<T> applyPagination(Pageable pageable,
                                          Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
                                          Function<JPAQueryFactory, JPAQuery<T>> countQuery){
        JPAQuery<T> contentJpaQuery = contentQuery.apply(getQueryFactory());
        List<T> content = getQuerydsl().applyPagination(pageable, contentJpaQuery).fetch();

        JPAQuery<T> countJpaQuery = countQuery.apply(getQueryFactory());
        return PageableExecutionUtils.getPage(content, pageable, countJpaQuery::fetchCount);
    }
}

