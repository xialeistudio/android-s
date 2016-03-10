package com.ddhigh.overtime.model;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
public class Role extends Model {
    /**
     * 角色
     */
    public final static int ROLE_DIRECTOR = 1;//主管
    public final static int ROLE_EMPLOTER = 2;//员工
    private int role_id;
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
