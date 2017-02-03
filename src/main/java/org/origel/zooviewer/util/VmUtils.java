package org.origel.zooviewer.util;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

public class VmUtils {

	public static String field(String str) {
		return field(str,":", 0);
	}
	public static String field(String str,String split, int index) {
		if(StringUtils.isBlank(str)){
			return StringUtils.EMPTY;
		}
		return escape(StringUtils.split(str,split)[index]);
	}
	public static String e(String str) {
		return escape(str);
	}
	public static String escape(String str) {
		return HtmlUtils.htmlEscape(str);
	}
	public static String limit(String str, int maxLength){
		if(StringUtils.isBlank(str)){
			return StringUtils.EMPTY;
		}
		
		if(StringUtils.length(str)<maxLength){
			return escape(str);
		}else{
			return escape(StringUtils.substring(str, 0, maxLength)) + "...";
		}
		
	}
	
	public static String formatx(long time){
		return format(time/1000, "yyyy-MM-dd HH:mm:ss");
	}
	public static String format(long time){
		return format(time, "yyyy-MM-dd HH:mm:ss");
	}
	public static String format(String time){
		return format(time, "yyyy-MM-dd HH:mm:ss");
	}
	public static String format(String time, String format){
		return DateFormatUtils.format(new Date(NumberUtils.toLong(time)*1000), format);
	}
	public static String format(long time, String format){
		return DateFormatUtils.format(new Date(time*1000), format);
	}

	public static long getId(long id, int level){
		long[] MASK = new long[]{
				0xFFFF000000000000l,
				0xFFFFFFFF00000000l,
				0xFFFFFFFFFFFF0000l,
				0xFFFFFFFFFFFFFFFFl,
				};
		return id&MASK[level-1];
	}

    public static int getIdLevel(long id) {
        int level = 0;

        while (id != 0) {
            id <<= 16;
            level++;
        }
        return level;
    }
	public static void main(String[] args) {
//		System.out.println(limit("dd", 3));
//		System.out.println(limit("dddddddddddddddddd", 3));
//		System.out.println(limit("dddddddddddddddddddddddddddd", 3));
//		System.out.println(limit("dddddddddddddddddddddddddddddddddddddd", 3));
//		
//		System.out.println(format(1368561604,"yyyy-MM-dd HH:mm:ss"));
//		System.out.println(format(1368561604,"yyyy-MM-dd HH:mm:ss"));
//		System.out.println(format(1368561604,"yyyy-MM-dd HH:mm:ss"));
//		System.out.println(format(1368561604,"yyyy-MM-dd HH:mm:ss"));
		System.out.println(getId(2251808403619840l,1));
	}
}
