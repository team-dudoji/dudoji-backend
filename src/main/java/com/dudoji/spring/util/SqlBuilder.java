package com.dudoji.spring.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;

public class SqlBuilder {
	// Sql 을 새롭게 생성해주는 유틸
	private final StringBuilder where = new StringBuilder();
	private final Map<String, Object> params = new HashMap<>();
	private final StringBuilder orderBy = new StringBuilder();

	/**
	 * fragment 로 조건을 추가하여 계속 Sql 을 만들 게 합니ㅏㄷ.
	 * @param fragment 추가 할 Sql 문
	 * @param paramKey 파라미터 이름
	 * @param value 파라미터 값
	 * @return 자기 자신을 반환합니다.
	 */
	public SqlBuilder and(String fragment, String paramKey, Object value) {
		if (value == null || (value instanceof String s && s.isBlank())) return this;
		where.append(where.isEmpty() ? " WHERE " : " AND "); // WHERE 이 이미 있는 지 확인
		where.append(fragment);
		params.put(paramKey, value); // 파라미터 저장해 두기
		return this;
	}

	/**
	 * ORDER BY 구문을 추가 합니다.
	 * @param sort Sort 클래스인 변수
	 * @param columnMap 해당 Sql 에 존재하는 ColumnMap
	 * @return ORDER BY 가 추가된 자기 자신 반환
	 */
	public SqlBuilder orderBy(Sort sort, Map<String, String> columnMap) {
		if (sort == null || sort.isUnsorted()) return this;
		var clauses = new ArrayList<String>();
		for (Sort.Order order : sort) {
			String column = columnMap.get(order.getProperty());
			if (column != null) clauses.add(column + " " + (order.isAscending() ? "ASC" : "DESC") + " NULLS LAST");
		}

		if (!clauses.isEmpty()) where.append(" ORDER BY " + String.join(", ", clauses));
		return this;
	}

	public String whereSql() { return where.toString(); }
	public String orderBySql() { return orderBy.toString(); }
	public Map<String, Object> params() { return params; }

	public BuiltSql build() {
		return new BuiltSql(where.toString(), orderBy.toString(), params);
	}
}
