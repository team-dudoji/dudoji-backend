package com.dudoji.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dudoji.spring.dto.mission.QuestRequestDto;
import com.dudoji.spring.dto.npc.NpcDto;
import com.dudoji.spring.dto.npc.NpcMetaDto;
import com.dudoji.spring.dto.npc.NpcQuestDto;
import com.dudoji.spring.dto.npc.NpcQuestSimpleDto;
import com.dudoji.spring.dto.npc.NpcRequestDto;
import com.dudoji.spring.dto.npc.NpcResponseDto;
import com.dudoji.spring.dto.npc.NpcSkinDto;
import com.dudoji.spring.models.domain.PrincipalDetails;
import com.dudoji.spring.models.domain.mission.Quest;
import com.dudoji.spring.models.domain.skin.NpcSkin;
import com.dudoji.spring.service.NpcService;

@RestController
@PreAuthorize("isAuthenticated()")
public class NpcController {

	@Autowired
	private NpcService npcService;

	@GetMapping("/api/user/npcs")
	public ResponseEntity<List<NpcDto>> getNpcs(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestParam("radius") int radius,
		@RequestParam("lat") double lat,
		@RequestParam("lng") double lng
	) {
		return ResponseEntity.ok(npcService.getNpcsByRadius(lat, lng, radius, principalDetails.getUid()));
	}

	@GetMapping("/api/user/npcs/{npcId}/quests")
	public ResponseEntity<NpcQuestDto> getQuests(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable("npcId") Long npcId
	) {
		return ResponseEntity.ok(npcService.getAllNpcQuests(npcId, principalDetails.getUid()));
	}

	@GetMapping("/api/admin/npcs/{npcId}/quests")
	public ResponseEntity<List<NpcQuestSimpleDto>> getAllNpcQuests(
		@PathVariable("npcId") Long npcId
	) {
		return ResponseEntity.ok(npcService.getAllNpcQuests(npcId));
	}

	/*
	 About Quest
	 */
	@GetMapping("/api/admin/quests")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<List<NpcQuestSimpleDto>> getAllQuests() {
		return ResponseEntity.ok(npcService.getAllQuests());
	}

	@PostMapping("/api/admin/quests")
	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	public ResponseEntity<Boolean> createQuest(@RequestBody QuestRequestDto dto) {
		return ResponseEntity.ok(npcService.createQuest(dto));
	}

	@PutMapping("/api/admin/quests")
	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	public ResponseEntity<Boolean> updateQuest(@RequestBody QuestRequestDto dto) {
		return ResponseEntity.ok(npcService.updateQuest(dto));
	}

	@DeleteMapping("/api/admin/quests/{questId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> deleteQuest(@PathVariable long questId) {
		return ResponseEntity.ok(npcService.deleteQuest(questId));
	}

	/*
	 About Npc
	 */

	@GetMapping("/api/admin/npcs")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<List<NpcResponseDto>> getAllNpcs() {
		return ResponseEntity.ok(npcService.getAllNpcs());
	}

	@PostMapping("/api/admin/npcs")
	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	public ResponseEntity<Boolean> createNpc(@RequestBody NpcRequestDto dto) {

		return ResponseEntity.ok(npcService.createNpc(dto));
	}

	@PutMapping("/api/admin/npcs")
	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	public ResponseEntity<Boolean> updateNpc(@RequestBody NpcRequestDto dto) {
		return ResponseEntity.ok(npcService.updateNpc(dto));
	}

	@DeleteMapping("/api/admin/npcs/{npcId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> deleteNpc(@PathVariable long npcId) {
		return ResponseEntity.ok(npcService.deleteNpc(npcId));
	}

	/*
	 About Quest Dependency
	 */

	@PostMapping("/api/admin/npc-quest-dependency/{parentQuestId}/{childQuestId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> createQuestDependencies(@PathVariable long parentQuestId, @PathVariable long childQuestId) {
		return ResponseEntity.ok(npcService.createQuestDependency(parentQuestId, childQuestId));
	}

	@DeleteMapping("/api/admin/npc-quest-dependency/{parentQuestId}/{childQuestId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> deleteQuestDependencies(@PathVariable long parentQuestId, @PathVariable long childQuestId) {
		return ResponseEntity.ok(npcService.deleteQuestDependency(parentQuestId, childQuestId));
	}

	/*
	 About Npc Quest
	 */

	@PostMapping("/api/admin/npc-quest/{npcId}/{questId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> createNpcQuest(@PathVariable long npcId, @PathVariable long questId) {
		return ResponseEntity.ok(npcService.createNpcQuest(npcId, questId));
	}

	@DeleteMapping("/api/admin/npc-quest/{npcId}/{questId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> deleteNpcQuest(@PathVariable long npcId, @PathVariable long questId) {
		return ResponseEntity.ok(npcService.deleteNpcQuest(npcId, questId));
	}

	/*
	 About Npc Skin
	 */
	@PostMapping("/api/admin/npc-skin")
	@PreAuthorize("hasRole('admin')")
	@ResponseBody
	public ResponseEntity<Boolean> createNpcSkin(@RequestBody NpcSkinDto dto) {
		return ResponseEntity.ok(npcService.createNpcSkin(dto) > 0);
	}

	// @PutMapping("/api/admin/npc-skin")
	// @PreAuthorize("hasRole('admin')")
	// @ResponseBody
	// public ResponseEntity<Boolean> updateNpcSkin(@RequestBody NpcSkinDto dto) {
	// 	return ResponseEntity.ok(npcService.updateNpcSkin(dto));
	// }

	@DeleteMapping("/api/admin/npc-skin/{npcSkinId}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Boolean> deleteNpcSkin(@PathVariable long npcSkinId) {
		return ResponseEntity.ok(npcService.deleteNpcSkin(npcSkinId));
	}

	@GetMapping("/api/admin/npc-skin")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<List<NpcSkin>> getAllNpcSkins() {
		return ResponseEntity.ok(npcService.getAllNpcSkins());
	}


	@GetMapping("/api/user/npcs/meta")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<List<NpcMetaDto>> getNpcMetaData(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		return ResponseEntity.ok(npcService.getAllNpcMetaData(principalDetails.getUid()));
	}
}
