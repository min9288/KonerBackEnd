package project.SangHyun.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QNickname is a Querydsl query type for Nickname
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QNickname extends BeanPath<Nickname> {

    private static final long serialVersionUID = 711497260L;

    public static final QNickname nickname1 = new QNickname("nickname1");

    public final StringPath nickname = createString("nickname");

    public QNickname(String variable) {
        super(Nickname.class, forVariable(variable));
    }

    public QNickname(Path<? extends Nickname> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNickname(PathMetadata metadata) {
        super(Nickname.class, metadata);
    }

}
