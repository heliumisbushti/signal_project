package com.alerts;
import java.util.List;

public class PriorityAlertDecorator extends AlertDecorator{
    private static final List<String> PRIORITY_LEVELS = List.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
    private int priority;
    public PriorityAlertDecorator(AlertInterface alert,int priority) {
        super(alert);
        this.priority = Math.min(Math.max(priority, 0), PRIORITY_LEVELS.size() - 1);
    }

public void setPriority(int priority) {
        this.priority = Math.min(Math.max(priority, 0), PRIORITY_LEVELS.size() - 1);
}
    @Override
    public String getMessage() {
        return super.getMessage() + " Priority: " + getPriority();
    }
    public int getPriority() {
        return PRIORITY_LEVELS.indexOf(getCondition());
    }

}
