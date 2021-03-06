package com.zdr.geekmusic.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zdr on 16-9-18.
 */
public class MusicLrc {
    /**
     * code : 0
     * status : success
     * msg : 数据请求成功
     * data : {"content":"[00:06.57]十年\r\n[00:07.99]如果那两个字没有颤抖\r\n[00:11.14]我不会发现 我难受\r\n[00:14.64]怎么说出口 也不过是分手\r\n[00:22.01]如果对于明天没有要求\r\n[00:26.78]牵牵手就像旅游\r\n[00:29.73]成千上万个门口\r\n[00:33.18]总有一个人要先走\r\n[00:39.58]怀抱既然不能逗留\r\n[00:42.73]何不在离开的时候\r\n[00:45.57]一边享受一边泪流\r\n[00:52.78]十年之前\r\n[00:53.54]我不认识你 你不属于我\r\n[00:59.28]我们还是一样\r\n[01:00.91]陪在一个陌生人左右\r\n[01:03.19]走过渐渐熟悉的街头\r\n[01:08.22]十年之后\r\n[01:10.05]我们是朋友 还可以问候\r\n[01:14.01]只是那种温柔\r\n[01:16.45]再也找不到拥抱的理由\r\n[01:20.31]情人最后难免沦为朋友\r\n[01:49.16]怀抱既然不能逗留\r\n[01:52.36]何不在离开的时候\r\n[01:55.20]一边享受 一边泪流\r\n[02:02.51]十年之前\r\n[02:04.29]我不认识你 你不属于我\r\n[02:08.20]我们还是一样\r\n[02:10.59]陪在一个陌生人左右\r\n[02:14.50]走过渐渐熟悉的街头\r\n[02:18.11]十年之后\r\n[02:19.68]我们是朋友 还可以问候\r\n[02:24.20]只是那种温柔\r\n[02:26.08]再也找不到拥抱的理由\r\n[02:30.09]情人最后难免沦为朋友\r\n[02:41.21]直到和你做了多年朋友\r\n[02:44.15]才明白我的眼泪\r\n[02:46.95]不是为你而流 也为别人而流\r\n[02:56.63]本歌词由网友\u201c我为歌狂1986\u201d提供 我爱\u201c駿(初\u201d 真的很喜欢，Forever！！！\r\n"}
     */

    private int code;
    private String status;
    private String msg;
    /**
     * content : [00:06.57]十年
     [00:07.99]如果那两个字没有颤抖
     [00:11.14]我不会发现 我难受
     [00:14.64]怎么说出口 也不过是分手
     [00:22.01]如果对于明天没有要求
     [00:26.78]牵牵手就像旅游
     [00:29.73]成千上万个门口
     [00:33.18]总有一个人要先走
     [00:39.58]怀抱既然不能逗留
     [00:42.73]何不在离开的时候
     [00:45.57]一边享受一边泪流
     [00:52.78]十年之前
     [00:53.54]我不认识你 你不属于我
     [00:59.28]我们还是一样
     [01:00.91]陪在一个陌生人左右
     [01:03.19]走过渐渐熟悉的街头
     [01:08.22]十年之后
     [01:10.05]我们是朋友 还可以问候
     [01:14.01]只是那种温柔
     [01:16.45]再也找不到拥抱的理由
     [01:20.31]情人最后难免沦为朋友
     [01:49.16]怀抱既然不能逗留
     [01:52.36]何不在离开的时候
     [01:55.20]一边享受 一边泪流
     [02:02.51]十年之前
     [02:04.29]我不认识你 你不属于我
     [02:08.20]我们还是一样
     [02:10.59]陪在一个陌生人左右
     [02:14.50]走过渐渐熟悉的街头
     [02:18.11]十年之后
     [02:19.68]我们是朋友 还可以问候
     [02:24.20]只是那种温柔
     [02:26.08]再也找不到拥抱的理由
     [02:30.09]情人最后难免沦为朋友
     [02:41.21]直到和你做了多年朋友
     [02:44.15]才明白我的眼泪
     [02:46.95]不是为你而流 也为别人而流
     [02:56.63]本歌词由网友“我为歌狂1986”提供 我爱“駿(初” 真的很喜欢，Forever！！！

     */

    @SerializedName("data")
    private Lrc lrc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Lrc getLrc() {
        return lrc;
    }

    public void setLrc(Lrc lrc) {
        this.lrc = lrc;
    }

    public static class Lrc {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
