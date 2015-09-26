package com.github.kolandroid.kol.android.login;

import com.github.kolandroid.kol.android.controller.Controller;

public interface ExpiringController extends Controller {
    boolean hasExpired();
}
