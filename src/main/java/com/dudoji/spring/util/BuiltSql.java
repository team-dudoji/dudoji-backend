package com.dudoji.spring.util;

import java.util.Map;

public record BuiltSql(String whereSql, String orderBySql, Map<String, Object> params) {}
