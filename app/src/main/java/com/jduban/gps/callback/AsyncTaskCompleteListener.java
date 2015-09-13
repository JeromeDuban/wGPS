package com.jduban.gps.callback;

/**
 * Callback interface for asyncTasks
 */
public interface AsyncTaskCompleteListener {
    void onDataTaskComplete(String result, int number);

    void onFrameEvent(int frame, int number);

    void deleteTask(int id);
}