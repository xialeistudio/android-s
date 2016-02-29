package com.ddhigh.dodo.orm;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 用户
 * @project Study
 * @package com.ddhigh.dodo.orm
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class User {
    public final static int FEMALE = 1;//男
    public final static int MALE = 2;//女
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private int sex;
    private Date createdAt;

    public User(String username, String password, String nickname, String phone, String email, int sex, Date createdAt) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.sex = sex;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String dateStr = simpleDateFormat.format(createdAt);
        return String.format("username:　%s\npassword: %s\nnickname: %s\nphone: %s\nemail: %s\nsex: %s\ncreatedAt: %s", username, password, nickname, phone, email, (sex == FEMALE ? "男" : "女"), dateStr);
    }
}
