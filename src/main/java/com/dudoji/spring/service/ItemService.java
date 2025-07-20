package com.dudoji.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dudoji.spring.dto.Item.ItemSimpleDto;
import com.dudoji.spring.models.dao.InventoryDao;
import com.dudoji.spring.models.dao.ItemDao;
import com.dudoji.spring.models.domain.Item;

@Service
public class ItemService {

	@Autowired
	private ItemDao itemDao;

	@Autowired
	private InventoryDao inventoryDao;


	public List<ItemSimpleDto> getAllItem() {
		return itemDao.getAllItem()
			.stream()
			.map(ItemSimpleDto::new)
			.collect(Collectors.toList());
	}

	/**
	 * Item 테이블에서 아이템 정보를, Inventory 테이블에서 유저의 인벤토리 정보를 가져와 합쳐서 반환합니다.
	 * @param userId Id of User
	 * @return Item Information contains stock which user has that item.
	 */
	public List<ItemSimpleDto> getInventoryItems(long userId) {
		List<Item> items = itemDao.getAllItem();
		Map<Long, Long> itemCount = inventoryDao.getInventory(userId);
		List<ItemSimpleDto> itemDtos = new ArrayList<>();


		// 0일 때는 고려 x
		itemDtos = items.stream()
				.filter(item -> itemCount.containsKey(item.getItemId()))
					.map(item -> new ItemSimpleDto(item, userId, itemCount.get(item.getItemId())))
						.toList();

		return itemDtos;
	}

	public boolean buyItems(long userId, long itemId, int quantity) {
		if (quantity <= 0) return false; // 에러
		return inventoryDao.buyItems(userId, itemId, quantity);
	}

	public boolean useItems(long userId, long itemId, int quantity) {
		if (quantity <= 0) return false; // 에러
		return inventoryDao.useItems(userId, itemId, quantity);
	}

	/**
	 * 아이템을 만들고 그 아이템의 아이디를 반환합니다.
	 * @param itemDto 컨트롤러에서 받은 아이템 정보
	 * @return Long Item Id
	 */
	public Long createItem(ItemSimpleDto itemDto) {
		return itemDao.upsertItem(
			itemDto.toDomain()
		);
	}

	/**
	 * 아이템 아이디를 가지고 삭제를 진행합니다.
	 * @param itemId 컨트롤러에서 받은 아이템 아이디
	 * @return 삭제 성공 여부
	 */
	public boolean deleteItem(Long itemId) {
		return itemDao.deleteItem(itemId);
	}
}
