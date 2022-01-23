package com.niton.reactj.testing.mvc;

import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.testing.observer.MockObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test template for custom {@link com.niton.reactj.api.mvc.ReactiveComponent}
 * implementations.
 */
public abstract class ReactiveComponentImplTest<C extends ReactiveComponent<M, O, ?>, M, O> {
	protected final MockObserver observer = new MockObserver();
	protected       C            component;
	private         boolean      changed;

	@BeforeEach
	void prepare() {
		component = getComponent();
		changed   = false;
	}

	/**
	 * Initialize an unmodified component impl (and return it)
	 */
	protected abstract C getComponent();

	@Test
	void testInitialModel() {
		assertEquals(getExpectedInitialModel(), component.getModel(),
		             "The initial model has to be constant"
		);
	}

	/**
	 * @return the model your component should be initialized with
	 */
	protected abstract M getExpectedInitialModel();

	@Test
	void testUiUpdateFires() {
		component.getView();//ensures that initBindings is called
		M model = generateObservable();
		component.setModel(model);
		component.onUiUpdate.listen(() -> changed = true);
		assertFalse(changed);
		modify(model);
		assertTrue(
				changed,
				"If the model changes 'uiUpdate' should be fired," +
						"even if the update didn't cause any changes"
		);
	}

	/**
	 * @return a newly generated observable object
	 */
	protected abstract M generateObservable();

	/**
	 * Modifies the object so that the internal observer fires an observation
	 *
	 * @param m the model to modify
	 */
	protected abstract void modify(M m);

	@Test
	void modelReassignment() {
		M   m1     = generateObservable();
		int m1Hash = m1.hashCode();
		M   m2     = generateObservable();
		modify(m2);
		int m2Hash = m2.hashCode();
		component.setModel(m1);
		assertEquals(m1, component.getModel());
		component.setModel(m2);
		assertEquals(m2, component.getModel());
		assertEquals(
				m1Hash, m1.hashCode(),
				"The model should not change just from assigning it to a component"
		);

		assertEquals(
				m2Hash, m2.hashCode(),
				"The model should not change just from assigning it to a component"
		);
	}

	@Test
	void validView() {
		assertNotNull(component.getView(), "getView() is not allowed to return null");
	}
}
