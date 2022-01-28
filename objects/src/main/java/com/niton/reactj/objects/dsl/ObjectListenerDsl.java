package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.ListenerDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;

public interface ObjectListenerDsl extends ListenerDsl {
    /**
     * Execute the previously defined action when the model changes
     */
    MultiListenerDsl onModelChange();
}
