package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.Listenable;

public interface MultiListenerDsl {
    MultiListenerDsl andOn(Listenable event);
}
