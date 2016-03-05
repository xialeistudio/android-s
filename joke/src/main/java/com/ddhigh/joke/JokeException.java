package com.ddhigh.joke;

/**
 * @project Study
 * @package com.ddhigh.joke
 * @user xialeistudio
 * @date 2016/3/5 0005
 */
public class JokeException extends Exception {
    public int code;

    public int getCode() {
        return code;
    }

    public JokeException(String detailMessage, int code) {
        super(detailMessage);
        this.code = code;
    }
}
