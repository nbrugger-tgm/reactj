package com.niton.reactj.special;

/**
 * For internal use only
 */
public enum ListAction {
	ADD("add"),
	ADD_INDEX("add_index"),
	SET_INDEX("set_index"),
	REMOVE_INDEX("remove_index"),
	REMOVE_OBJECT("remove_object"),
	CLEAR("clear"),
	INIT("init"),
	REPLACE("replace_index"),
	MODIFY("other");

	private final String id;

	ListAction(String identifier) {
		id = identifier;
	}

	public String id() {
		return id;
	}
}
