package com.ddhigh.overtime.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
@Table(name = "ot_role")
public class Role extends Model {
    /**
     * 角色
     */
    public final static int ROLE_DIRECTOR = 1;//主管
    public final static int ROLE_EMPLOTER = 2;//员工

    @Column(name = "role_id", isId = true)
    private int role_id;
    @Column(name = "name")
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }
}
