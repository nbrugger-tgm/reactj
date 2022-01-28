package com.niton.reactj.core.impl.test.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.core.impl.dsl.CoreDsl;
import com.niton.reactj.testing.binding.DslTest;

class CoreDslTest extends DslTest {
    @Override
    protected BinderDsl createBinder() {
        return new CoreDsl();
    }
}
