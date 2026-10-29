package com.virus5600.defensive_measures.configs.records.block;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public record RawBlockGroup(List<String> blockRefs, AttributeValues attributes) {
	static RawBlockGroup fromJson(JsonObject obj) {
		List<String> refs = new ArrayList<>();
		if (obj.has("block") && obj.get("block").isJsonArray()) {
			obj.getAsJsonArray("block").forEach(el -> refs.add(el.getAsString()));
		}
		return new RawBlockGroup(refs, AttributeValues.fromJson(obj));
	}
}
