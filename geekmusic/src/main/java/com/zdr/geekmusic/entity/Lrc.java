package com.zdr.geekmusic.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单句歌词解析
 * Created by zdr on 16-9-18.
 */
public class Lrc {
    private static final String EMAIL_PATTERN = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,3})\\](.)*";
    private Pattern mPattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    public class LrcItem {
        private String content;
        private int start;

        public LrcItem() {
        }

        public LrcItem(String content, int start) {
            this.content = content;
            this.start = start;
        }

        public String getContext() {
            return content;
        }

        public void setContext(String content) {
            this.content = content;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }
    }

    private List<LrcItem> mLrcItems;

    public Lrc(String lrc) {
        mLrcItems = new LinkedList<>();
        readLrcFromNet(lrc);
    }

    public Lrc(File file) {
        mLrcItems = new LinkedList<>();
        readLrcFromLocal(file);
    }

    public List<LrcItem> getLrcItems() {
        return mLrcItems;
    }

    private void readLrcFromLocal(File file) {
        mLrcItems.clear();
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(fin));
            String line = null;
            while ((line = bfr.readLine()) != null) {
                matcher = mPattern.matcher(line);
                if (matcher.matches()) {
                    line = line.replace("[", "");
                    line = line.replace("]", "#");
                    String[] content = line.split("#");
                    if (content.length!=2)
                        continue;
                    mLrcItems.add(new LrcItem(content[1], getTime(content[0])));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLrcFromNet(String respone) {
        respone = respone.replace("\r", "");
        String[] data = respone.split("\n");

        for (String str : data) {
            matcher = mPattern.matcher(str);
            if (matcher.matches()) {
                str = str.replace("[", "");
                str = str.replace("]", "#");
                String[] content = str.split("#");
                if (content.length!=2)
                    continue;
                mLrcItems.add(new LrcItem(content[1], getTime(content[0])));
            }
        }
    }

    private int getTime(String time) {

        String[] times = time.split(":|\\.");
        int start = 0;
        start += Integer.parseInt(times[0]) * 60 * 1000;
        start += Integer.parseInt(times[1]) * 1000;
        start += Integer.parseInt(times[2]) * 10;
        return start;
    }

}
