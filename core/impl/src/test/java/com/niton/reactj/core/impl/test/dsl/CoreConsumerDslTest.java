package com.niton.reactj.core.impl.test.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.core.impl.dsl.CoreDsl;
import com.niton.reactj.testing.binding.ConsumerBindingTest;

class CoreConsumerDslTest extends ConsumerBindingTest {
    @Override
    protected BinderDsl createBinder() {
        return new CoreDsl();
    }
}
