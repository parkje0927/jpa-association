package pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utils.StringUtils.isBlankOrEmpty;

//Order
public class EntityMetaData {

    private final Class<?> clazz;
    private final String entityName;
    private final List<EntityColumn> entityColumns;
    private final EntityJoinMetaData entityJoinMetaData; //OrderItem

    public EntityMetaData(Class<?> clazz, Object entity) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entityName = getEntityNameInfo();
        this.entityColumns = null;
        this.entityJoinMetaData = null;
    }

    public String getEntityName() {
        return entityName;
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public EntityJoinMetaData getEntityJoinMetaData() {
        return entityJoinMetaData;
    }

    private String getEntityNameInfo() {
        if (clazz.isAnnotationPresent(Table.class) && !isBlankOrEmpty(clazz.getAnnotation(Table.class).name())) {
            return clazz.getAnnotation(Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    public List<EntityColumn> getEntityColumnsInfo(Object entity) {
        return new FieldInfos(clazz.getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .collect(Collectors.toList());
    }

    public EntityJoinMetaData getEntityJoinMetaDataInfo(Object entity) {
        FieldInfos fieldInfos = new FieldInfos(clazz.getDeclaredFields());

        Optional<Field> joinColumnField = fieldInfos.getJoinColumnField();
        if (joinColumnField.isEmpty()) {
            return null;
        }

        Field field = fieldInfos.getIdField();
        IdField idField = new IdField(field, entity);

        Class<?> joinClass = (Class<?>) ((ParameterizedType) joinColumnField.get().getGenericType()).getActualTypeArguments()[0];
        return new EntityJoinMetaData(joinClass, null, joinColumnField.get(), idField);
    }
}
