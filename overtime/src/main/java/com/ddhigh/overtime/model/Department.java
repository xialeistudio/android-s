package com.ddhigh.overtime.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 部门
 *
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_department")
public class Department extends Model {
    @Column(name = "department_id", isId = true)
    private int department_id;
    @Column(name = "name")
    private String name;

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
