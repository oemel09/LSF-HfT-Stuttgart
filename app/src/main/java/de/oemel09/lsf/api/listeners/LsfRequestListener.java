package de.oemel09.lsf.api.listeners;

public interface LsfRequestListener {
    void onRequestStart();
    void onRequestFailed();
    void onLoginFailed();
}

