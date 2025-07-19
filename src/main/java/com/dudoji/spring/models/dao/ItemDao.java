package com.dudoji.spring.models.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.dudoji.spring.models.domain.Item;

import lombok.RequiredArgsConstructor;

@Repository("ItemDao")
@RequiredArgsConstructor
public class ItemDao {
	private final JdbcClient jdbcClient;

	private static final String GET_ITEMS = "SELECT * FROM Item";

	private static final String UPSERT_ITEM = """
    INSERT INTO Item (name, content, price, imageUrl)
    VALUES (:name, :content, :price, :imageUrl)
    ON CONFLICT (name) DO UPDATE
      SET content  = EXCLUDED.content,
          price    = EXCLUDED.price,
          imageUrl = EXCLUDED.imageUrl
    RETURNING itemId;
    """;

	private static final String DELETE_ITEM = "DELETE FROM Item WHERE ItemId = :itemId";

	/**
	 * 모든 아이템의 정보를 가져옵니다.
	 * @return List of Items
	 */
	public List<Item> getAllItem() {
		return jdbcClient.sql(GET_ITEMS)
			.query((rs, rowNum) -> Item.builder()
				.itemId(rs.getLong("itemId"))
				.name(rs.getString("name"))
				.content(rs.getString("content"))
				.price(rs.getInt("price"))
				.imageUrl(rs.getString("imageUrl"))
				.build())
			.list();
	}

	// TODO: 굳이 필요할까요? getAllItem 에서 가져온 이후에 대충 찾으면 될 듯 합니다.
	public Item getItemById(Long itemId) {
		return null;
	}

	/**
	 * 아이템을 생성하고 아이템의 id를 반환합니다.
	 * @param item - which contains item info
	 * @return item id
	 */
	public Long upsertItem(Item item) {
		return jdbcClient.sql(UPSERT_ITEM)
			.param("name", item.getName())
			.param("content", item.getContent())
			.param("price", item.getPrice())
			.param("imageUrl", item.getImageUrl())
			.query(Long.class)
			.single();
	}

	public boolean deleteItem(Long itemId) {
		return jdbcClient.sql(DELETE_ITEM)
			.param("itemId", itemId)
			.update() > 0;
	}
}
