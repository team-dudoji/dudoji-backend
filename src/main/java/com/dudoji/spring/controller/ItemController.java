package com.dudoji.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dudoji.spring.dto.Item.ItemRequestDto;
import com.dudoji.spring.dto.Item.ItemSimpleDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ItemController {

	@Value("${file.upload-dir}") String uploadDir;

	@Autowired
	private final ItemService itemService;

	@GetMapping("/api/user/items")
	public ResponseEntity<List<ItemSimpleDto>> getItems(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		List<ItemSimpleDto> result = itemService.getAllItem();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// TODO: 필요한가? 그냥 하나로 받는 게 나아 보이기도 한다.
	@GetMapping("/api/user/items/inventory")
	public ResponseEntity<List<ItemSimpleDto>> getItemInventory(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		List<ItemSimpleDto> result = itemService.getInventoryItems(
			principalDetails.getUid()
		);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/api/user/items/buy")
	public ResponseEntity<Boolean> buyItems(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestBody ItemRequestDto item
	) {
		boolean result = itemService.buyItems(principalDetails.getUid(), item.itemId(), item.quantity());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/api/user/items/use")
	public ResponseEntity<Boolean> useItems(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestBody ItemRequestDto item
	) {
		boolean result = itemService.useItems(principalDetails.getUid(), item.itemId(), item.quantity());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	@PostMapping("/api/admin/items")
	public ResponseEntity<String> createItem(
		@RequestBody ItemSimpleDto item
	) {
		Long result = itemService.createItem(item);
		if (result != null) {
			return ResponseEntity.ok("Item created: " + result);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	@DeleteMapping("/api/admin/items/{itemId}")
	public ResponseEntity<String> deleteItem(
		@PathVariable long itemId
	) {
		boolean reuslt = itemService.deleteItem(itemId);
		if (reuslt) {
			return ResponseEntity.ok("Item deleted: " + itemId);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
