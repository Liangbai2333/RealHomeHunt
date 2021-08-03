/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.util;

import com.dieselpoint.norm.sqlmakers.Property;
import com.dieselpoint.norm.sqlmakers.StandardPojoInfo;
import com.dieselpoint.norm.sqlmakers.StandardSqlMaker;

import javax.persistence.Column;
import java.math.BigDecimal;

public class SqlMaker {
    private final StandardSqlMaker sqlMaker;

    public SqlMaker(com.dieselpoint.norm.sqlmakers.StandardSqlMaker original) {
        this.sqlMaker = original;
    }

    public String getCreateTableIfNotExistsSql(Class<?> clazz) {
        StringBuilder buf = new StringBuilder();

        StandardPojoInfo pojoInfo = sqlMaker.getPojoInfo(clazz);
        buf.append("create table if not exists ");
        buf.append(pojoInfo.table);
        buf.append(" (");

        boolean needsComma = false;
        for (Property prop : pojoInfo.propertyMap.values()) {

            if (needsComma) {
                buf.append(',');
            }
            needsComma = true;

            Column columnAnnot = prop.columnAnnotation;
            if (columnAnnot == null) {

                buf.append(prop.name);
                buf.append(" ");
                buf.append(getColType(prop.dataType, 255, 10, 2));
                if (prop.isGenerated) {
                    buf.append(" auto_increment");
                }

            } else {
                if (columnAnnot.columnDefinition() == null) {

                    // let the column def override everything
                    buf.append(columnAnnot.columnDefinition());

                } else {

                    buf.append(prop.name);
                    buf.append(" ");
                    buf.append(getColType(prop.dataType, columnAnnot.length(), columnAnnot.precision(),
                            columnAnnot.scale()));
                    if (prop.isGenerated) {
                        buf.append(" auto_increment");
                    }

                    if (columnAnnot.unique()) {
                        buf.append(" unique");
                    }

                    if (!columnAnnot.nullable()) {
                        buf.append(" not null");
                    }
                }
            }
        }

        if (pojoInfo.primaryKeyName != null) {
            buf.append(", primary key (");
            buf.append(pojoInfo.primaryKeyName);
            buf.append(")");
        }

        buf.append(")");

        return buf.toString();
    }

    protected String getColType(Class<?> dataType, int length, int precision, int scale) {
        String colType;

        if (dataType.equals(Integer.class) || dataType.equals(int.class)) {
            colType = "integer";

        } else if (dataType.equals(Long.class) || dataType.equals(long.class)) {
            colType = "bigint";

        } else if (dataType.equals(Double.class) || dataType.equals(double.class)) {
            colType = "double";

        } else if (dataType.equals(Float.class) || dataType.equals(float.class)) {
            colType = "float";

        } else if (dataType.equals(BigDecimal.class)) {
            colType = "decimal(" + precision + "," + scale + ")";

        } else if (dataType.equals(java.util.Date.class)) {
            colType = "datetime";

        } else {
            colType = "varchar(" + length + ")";
        }
        return colType;
    }
}
