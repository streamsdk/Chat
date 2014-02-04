package com.streamsdk.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.streamsdk.chat.emoji.EmojiParser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

public class ParseMsgUtil {

	public static String convertEditTextToParsableFormat(String content, Context context){
		
		StringBuilder sb = new StringBuilder();
		boolean inTag = false;
		for (int i=0; i < content.length(); i++){
			char c = content.charAt(i);
			if (c!='[' && c!=']'&&!inTag)
				sb.append(c);
			if (c == '['){
				inTag = true;
				int currentEndIndex = content.indexOf("]", i);
				String hexStr = content.substring(i+1, currentEndIndex);
				if (isEmoji(hexStr, context)){
					String str = new String(Character.toChars(Integer.parseInt(hexStr, 16)));
					sb.append(str);
				}else{
					
				}
				
				Log.i("", hexStr);
			}
			if (c == ']')
				inTag = false;
		}
		
		return sb.toString();
	}
	
	private static boolean isEmoji(String hexStr, Context context){
		
		int id = context.getResources().getIdentifier(
				"emoji_" + hexStr,
				"drawable", "com.streamsdk.chat");
		if (id != 0)
			return true;
		return false;
		
	}
	
	public static String convertToMsg(CharSequence cs, Context mContext) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(cs);
		ImageSpan[] spans = ssb.getSpans(0, cs.length(), ImageSpan.class);
		for (int i = 0; i < spans.length; i++) {
			ImageSpan span = spans[i];
			String c = span.getSource();
			int a = ssb.getSpanStart(span);
			int b = ssb.getSpanEnd(span);
			if (c.contains("emoji")) {
				ssb.replace(a, b, convertUnicode(c));
			}
		}
		ssb.clearSpans();
		return ssb.toString();
	}

	private static String convertUnicode(String emo) {
		emo = emo.substring(emo.indexOf("_") + 1);
		if (emo.length() < 6) {
			return new String(Character.toChars(Integer.parseInt(emo, 16)));
		}
		String[] emos = emo.split("_");
		char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
		char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
		char[] emoji = new char[char0.length + char1.length];
		for (int i = 0; i < char0.length; i++) {
			emoji[i]  = char0[i];
		}
		for (int i = char0.length; i < emoji.length; i++) {
			emoji[i]  = char1[i - char0.length];
		}
		return new String(emoji);
	}

	public static SpannableStringBuilder convetToHtml(String content, Context mContext) {
		String regex = "\\[e\\](.*?)\\[/e\\]";
		 Pattern pattern = Pattern.compile(regex);
		String emo = "";
		Resources resources = mContext.getResources();
		String unicode = EmojiParser.getInstance(mContext).parseEmoji(content);
		Matcher matcher = pattern.matcher(unicode);
		SpannableStringBuilder sBuilder = new SpannableStringBuilder(unicode);
		Drawable drawable = null;
		ImageSpan span = null;
		while (matcher.find()) {
			emo = matcher.group();
			try {
				int id = resources.getIdentifier(
						"emoji_" + emo.substring(emo.indexOf("]") + 1, emo.lastIndexOf("[")),
						"drawable", "com.streamsdk.chat");
				
				if (id != 0) {
					drawable = resources.getDrawable(id);
					drawable.setBounds(0, 0, 48, 48);
					span = new ImageSpan(drawable);
					sBuilder.setSpan(span, matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (Exception e) {
				break;
			}
		}
		return sBuilder;
	}

	public static String convetToNotice(String content, Context mContext) {
		return EmojiParser.getInstance(mContext).convertEmoji(content);
	}

	public static SpannableStringBuilder convetToHeart(SpannableStringBuilder content,final Context mContext) {
		ImageGetter imageGetter = new ImageGetter() {
			public Drawable getDrawable(String source) {
				Drawable d = mContext.getResources().getDrawable(R.drawable.emoji_2764);
				d.setBounds(0, 0, 24, 24);
				return d;
			}
		};
		CharSequence cs1 = Html.fromHtml("<img src='heart'/>", imageGetter, null);
		content.append(cs1);
		return content;
	}

	/*public static CharSequence convetToEmo(String content, Context mContext) {
		if (content == null ) {
			return "表情" ;
		}
		try {
			int index = Integer.parseInt(content.substring(content.indexOf("/")+1, content.indexOf(".")));
			String[] emos = mContext.getResources().getStringArray(R.array.nn_emo);
			return emos[index-1];
		} catch (Exception e) {
			return "表情" ;
		}
	}*/
}
