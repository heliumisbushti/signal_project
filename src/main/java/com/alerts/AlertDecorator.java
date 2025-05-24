package com.alerts;

public  abstract class AlertDecorator implements AlertInterface{

    protected AlertInterface alert;

    public AlertDecorator(AlertInterface alert) {
        this.alert = alert;
    }
    @Override
    public String getPatientId() {
        return "";
    }

    @Override
    public String getCondition() {
        return "";
    }

    @Override
    public long getTimestamp() {
        return 0;
    }

    @Override
    public String getMessage() {
        return "";
    }

}
