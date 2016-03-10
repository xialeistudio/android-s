package com.ddhigh.overtime.model;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @project android-s
 * @package com.ddhigh.overtime.model
 * @user xialeistudio
 * @date 2016/3/10 0010
 */
public class DepartmentTest {
    private Department department;

    @Before
    public void setUp() throws Exception {
        department = new Department();
        department.setDepartment_id(1);
        department.setName("技术部");
    }

    @Test
    public void testDecode() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("department_id", 1);
        jsonObject.put("name", "技术部");
        Department department1 = new Department();
        department1.decode(jsonObject);
        assertEquals(department, department1);
    }

    @Test
    public void testEncode() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("department_id", 1);
        jsonObject.put("name", "技术部");
        assertEquals(jsonObject.toString(), department.encode().toString());
    }
}