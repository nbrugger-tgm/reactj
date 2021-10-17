package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.ConditionalReactiveBinding;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public class ConditionBindingBuilder<T> extends ConditionRunnableBuilder {
	private static class AndPredicate<T> implements Predicate<T> {
		private final Predicate<T> predicate1;
		private final Predicate<T> predicate2;

		public AndPredicate(Predicate<T> predicate1, Predicate<T> predicate2) {
			this.predicate1 = predicate1;
			this.predicate2 = predicate2;
		}

		@Override
		public boolean test(T t) {
			return predicate1.test(t) && predicate2.test(t);
		}
	}

	private static class OrPredicate<T> implements Predicate<T> {
		private final Predicate<T> predicate1;
		private final Predicate<T> predicate2;

		public OrPredicate(Predicate<T> predicate1, Predicate<T> predicate2) {
			this.predicate1 = predicate1;
			this.predicate2 = predicate2;
		}

		@Override
		public boolean test(T t) {
			return predicate1.test(t) || predicate2.test(t);
		}
	}


	public ConditionBindingBuilder(ConditionalReactiveBinding<T> r, BindingBuilder rootBuilder) {
		super(r, rootBuilder);
	}

	//needs to be suppressed as this is always of "this" type
	@Override
	@SuppressWarnings("unchecked")
	public ConditionBindingBuilder<T> or(Condition condition) {
		return (ConditionBindingBuilder<T>) super.or(condition);
	}

	//needs to be suppressed as this is always of "this" type
	@Override
	@SuppressWarnings("unchecked")
	public ConditionBindingBuilder<T> and(Condition condition) {
		return (ConditionBindingBuilder<T>) super.and(condition);
	}

	public ConditionBindingBuilder<T> or(Predicate<T> condition) {
		ConditionalReactiveBinding<T> binding = (ConditionalReactiveBinding<T>) runnable;
		binding.setPredicate(new OrPredicate<>(binding.getPredicate(), condition));
		return this;
	}

	public ConditionBindingBuilder<T> and(Predicate<T> condition) {
		ConditionalReactiveBinding<T> binding = (ConditionalReactiveBinding<T>) runnable;
		binding.setPredicate(new AndPredicate<>(binding.getPredicate(), condition));
		return this;
	}
}
