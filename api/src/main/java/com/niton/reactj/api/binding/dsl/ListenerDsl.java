package com.niton.reactj.api.binding.dsl;

import com.niton.reactj.api.binding.Listenable;

public interface ListenerDsl {

    MultiListenerDsl on(Listenable event);


    Runnable build();
}
