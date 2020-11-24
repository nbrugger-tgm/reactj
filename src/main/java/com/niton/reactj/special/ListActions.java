package com.niton.reactj.special;

public enum ListActions {
	ADD("add"),
	ADD_INDEX("add_index"),
	SET_INDEX("set_index"),
	REMOVE_INDEX("remove_index"),
	REMOVE_OBJECT("remove_object"),
	CLEAR("clear"),
	INIT("init"),
	REPLACE("replace_index");

	private final String id;

	ListActions(String identifier) {
		this.id = identifier;
	}

	public String id() {
		return id;
	}
}
