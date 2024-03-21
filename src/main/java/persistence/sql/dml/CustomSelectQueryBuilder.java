package persistence.sql.dml;

import pojo.EntityJoinMetaData;
import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.FieldName;
import pojo.IdField;

import java.lang.reflect.Field;

import static constants.CommonConstants.COMMA;

public class CustomSelectQueryBuilder {

    private static final String FIND_BY_ID_JOIN_QUERY = "SELECT %s FROM %s LEFT JOIN %s ON %s = %s WHERE %s = %s;";

    private final EntityMetaData entityMetaData;
    private final EntityJoinMetaData entityJoinMetaData;

    public CustomSelectQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
        this.entityJoinMetaData = entityMetaData.getEntityJoinMetaData();
    }

    public String findByIdJoinQuery(Object entity, Class<?> clazz, Object condition) {
        Field field = new FieldInfos(clazz.getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);

        return String.format(FIND_BY_ID_JOIN_QUERY, getSelectData(),
                entityMetaData.getEntityName(), entityJoinMetaData.getEntityName(),
                entityMetaData.getEntityName() + "." + idField.getFieldNameData(),
                entityJoinMetaData.getEntityName() + "." + entityJoinMetaData.getJoinColumnName(),
                entityMetaData.getEntityName() + "." + idField.getFieldNameData(), condition);
    }

    private String getSelectData() {
        String entityData = entityMetaData.getEntityColumns()
                .stream()
                .map(entityColumn -> entityColumn.getFieldName().getName())
                .map(name -> entityMetaData.getEntityName() + "." + name)
                .reduce((o1, o2) -> String.join(COMMA, o1, o2))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));

        String ownerEntityData = entityJoinMetaData.getFieldNames()
                .stream()
                .map(FieldName::getName)
                .map(name -> entityJoinMetaData.getEntityName() + "." + name)
                .reduce((o1, o2) -> String.join(COMMA, o1, o2))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));

        return entityData + COMMA + ownerEntityData;
    }

}
