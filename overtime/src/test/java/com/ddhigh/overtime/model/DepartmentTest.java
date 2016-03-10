package com.ddhigh.overtime.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
public class DepartmentTest {

    @Test
    public void testEquals() throws Exception {
        Department department = new Department();
        department.setDepartment_id(1);
        department.setName("技术部");

        Department department2 = new Department();
        department2.setDepartment_id(1);
        department2.setName("技术部");

        assertEquals(department,department2);
    }
}