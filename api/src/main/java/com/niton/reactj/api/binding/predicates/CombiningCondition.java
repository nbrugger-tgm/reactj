package com.niton.reactj.api.binding.predicates;

/**
 * A Condition that consists of two sub-conditions.
 * <p>
 * They can be combined to a single result using {@link #combine(boolean, boolean)}
 */
public abstract class CombiningCondition implements Condition {

    private final Condition condition1;
    private final Condition condition2;

    /**
     * Create a new CombiningCondition that combines the given conditions. How they are combined is
     * defined by the implementing class.
     *
     * @param condition1 The first condition
     * @param condition2 The second condition
     */
    protected CombiningCondition(Condition condition1, Condition condition2) {
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

    /**
     * Combine check and check2 in some logical way
     */
    protected abstract boolean combine(boolean check, boolean check1);

    /**
     * returns true if condition1 AND condition2 is true
     */
    public static class And extends CombiningCondition {

        public And(Condition condition1, Condition condition2) {
            super(condition1, condition2);
        }

        @Override
        protected boolean combine(boolean c1, boolean c2) {
            return c1 && c2;
        }
    }

    /**
     * returns true if either condition1 OR condition2 is true
     */
    public static class Or extends CombiningCondition {

        public Or(Condition condition1, Condition condition2) {
            super(condition1, condition2);
        }

        @Override
        protected boolean combine(boolean c1, boolean c2) {
            return c1 || c2;
        }
    }
}
