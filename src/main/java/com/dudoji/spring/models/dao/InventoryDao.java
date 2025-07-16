package com.dudoji.spring.models.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository("InventoryDao")
@RequiredArgsConstructor
public class InventoryDao {
	private final JdbcClient jdbcClient;

	private final static String BUY_ITEM = """
		INSERT INTO Inventory (userId, itemId, quantity)
		VALUES (:userId, :itemId, :quantity)
		ON CONFLICT (userId, itemId) 
		DO UPDATE
		 SET quantity = Inventory.quantity + EXCLUDED.quantity
		""";

	private final static String USE_ITEM = """
		UPDATE Inventory
		SET quantity = Inventory.quantity - :quantity
		WHERE userId = :userId
		AND itemId = :itemId
		AND quantity >= :quantity
		""";

	private static final String GET_INVENTORY = """
		SELECT itemId, quantity
		FROM Inventory
		WHERE userId = :userId
		""";

	public boolean buyItems(long userId, long itemId, long quantity) {
		if (quantity <= 0) return false;

		return jdbcClient.sql(BUY_ITEM)
			.param("userId", userId)
			.param("itemId", itemId)
			.param("quantity", quantity)
			.update() > 0;
	}

	// TODO: 확장성을 위해 quantity 변수 추가. 기본은 1로 한 번에 하나만 사용하게 합니다.
	public boolean useItems(long userId, long itemId, long quantity) {
		if (quantity <= 0) return false;

		return jdbcClient.sql(USE_ITEM)
			.param("userId", userId)
			.param("itemId", itemId)
			.param("quantity", quantity)
			.update() > 0;
	}

	public Map<Long, Long> getInventory(long userId) {
		return jdbcClient.sql(GET_INVENTORY)
			.param("userId", userId)
			.query(rs -> {
				Map<Long, Long> map = new LinkedHashMap<>(); // Maintain Sorted
				while (rs.next()) {
					map.put(rs.getLong("itemId"), rs.getLong("quantity"));
				}
				return map;
			});
	}
}
