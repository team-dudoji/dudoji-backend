package com.dudoji.spring.models.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.dudoji.spring.dto.pin.HashtagDto;

import lombok.RequiredArgsConstructor;

@Repository("HashtagDao")
@RequiredArgsConstructor
public class HashtagDao {
	private final JdbcClient jdbcClient;

	private final static String INSERT_HASHTAG = """
		INSERT INTO Hashtag (content)
		VALUES (:content)
		RETURNING tagId
		""";

	private final static String INSERT_PIN_HASHTAG = """
		INSERT INTO PinHashTag (pinId, tagId)
		VALUES (:pinId, :tagId)
		""";

	private final static String GET_HASHTAG_ID_BY_CONTENT = """
		SELECT tagId FROM Hashtag WHERE content = :content
		""";

	private final static String GET_CONTENT_BY_ID = """
		SELECT content FROM Hashtag WHERE tagId = :tagId
		""";

	private final static String GET_HASHTAG_STRING = """
		SELECT h.content
		FROM pinHashTag ph
		JOIN Hashtag h ON ph.tagId = h.tagId
		WHERE ph.pinId = :pinId;
		""";

	private final static String GET_HASHTAG_BY_PIN_IDS = """
		SELECT pinId, content FROM PinHashTag ph
		JOIN Hashtag h ON ph.tagId = h.tagId
		WHERE pinId IN (:pinIds)
		""";

	public long insertOrGetHashtag(String content, Long pinId) {
		Optional<Long> existingHashtagId = jdbcClient.sql(GET_HASHTAG_ID_BY_CONTENT)
			.param("content", content)
			.query(Long.class)
			.optional();

		long tagId = existingHashtagId.orElseGet(() -> {
			return jdbcClient.sql(INSERT_HASHTAG)
				.param("content", content)
				.query(Long.class)
				.single();
		});

		jdbcClient.sql(INSERT_PIN_HASHTAG)
			.param("pinId", pinId)
			.param("tagId", tagId)
			.update();

		return tagId;
	}

	public long getHashtagIdByContent(String content) {
		return jdbcClient.sql(GET_HASHTAG_ID_BY_CONTENT)
			.param("content", content)
			.query(Long.class)
			.optional()
			.orElse(0L);
	}

	public String getContentByHashtagId(long tagId) {
		return jdbcClient.sql(GET_CONTENT_BY_ID)
			.param("tagId", tagId)
			.query(String.class)
			.optional()
			.orElse("");
	}

	public List<String> getHashtags(long pinId) {
		return jdbcClient.sql(GET_HASHTAG_STRING)
			.param("pinId", pinId)
			.query(String.class)
			.list();
	}

	public List<HashtagDto> getHashtagByPinIds(List<Long> pinIds) {
		return jdbcClient.sql(GET_HASHTAG_BY_PIN_IDS)
			.param("pinIds", pinIds)
			.query((rs, ronNum) ->
				new HashtagDto(
					rs.getLong("pinId"),
					rs.getString("content")
				)
			)
			.list();
	}
}
