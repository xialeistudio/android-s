package com.ddhigh.study;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @project Study
 * @package com.ddhigh.study
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class ToDoItem {
    String task;
    Date created;

    public String getTask() {
        return task;
    }

    public Date getCreated() {
        return created;
    }

    public ToDoItem(String task) {
        this(task, new Date(System.currentTimeMillis()));
    }

    public ToDoItem(String task, Date created) {
        this.task = task;
        this.created = created;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        String dateStr = simpleDateFormat.format(created);
        return "(" + dateStr + ") " + task;
    }
}
