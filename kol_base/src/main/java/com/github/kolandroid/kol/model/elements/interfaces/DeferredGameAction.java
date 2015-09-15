package com.github.kolandroid.kol.model.elements.interfaces;

import com.github.kolandroid.kol.gamehandler.ViewContext;

import java.io.Serializable;

public interface DeferredGameAction extends Serializable {
    void submit(ViewContext context);
}
