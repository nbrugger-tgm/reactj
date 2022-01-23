package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.ListenerDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;

public interface ObjectListenerDsl extends ListenerDsl {
    MultiListenerDsl onModelChange();
}
