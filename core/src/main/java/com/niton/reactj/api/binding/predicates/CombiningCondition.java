package com.niton.reactj.api.binding.predicates;

public abstract class CombiningCondition implements Condition {

	private final Condition condition1;
	private final Condition condition2;

	public static class And extends CombiningCondition {

		public And(Condition condition1, Condition condition2) {
			super(condition1, condition2);
		}

		@Override
		public boolean combine(boolean c1, boolean c2) {
			return c1 && c2;
		}
	}

	public static class Or extends CombiningCondition {

		public Or(Condition condition1, Condition condition2) {
			super(condition1, condition2);
		}

		@Override
		public boolean combine(boolean c1, boolean c2) {
			return c1 || c2;
		}
	}

	public CombiningCondition(Condition condition1, Condition condition2) {
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	public Condition getCondition1() {
		return condition1;
	}

	public Condition getCondition2() {
		return condition2;
	}

	@Override
	public boolean check() {
		return combine(condition1.check(), condition2.check());
	}

	public abstract boolean combine(boolean check, boolean check1);
}
