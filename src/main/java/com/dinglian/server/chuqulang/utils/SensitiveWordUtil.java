package com.dinglian.server.chuqulang.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.dinglian.server.chuqulang.model.SensitiveWord;

public class SensitiveWordUtil {
	
	private static final SensitiveWordUtil instance = new SensitiveWordUtil();
	
	public static SensitiveWordUtil getInstance() {
		return instance;
	}
	
	/**
	 * 敏感词匹配规则
	 */
	public static final int MinMatchTYpe = 1; // 最小匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国]人
	public static final int MaxMatchType = 2; // 最大匹配规则，如：敏感词库["中国","中国人"]，语句："我是中国人"，匹配结果：我是[中国人]

	/**
	 * 敏感词集合
	 */
	private static HashMap firstSensitiveWordMap = new HashMap<>();
	private static HashMap secondSensitiveWordMap = new HashMap<>();
	private static HashMap thirdSensitiveWordMap = new HashMap<>();

	public HashMap getFirstSensitiveWordMap() {
		return firstSensitiveWordMap;
	}
	
	public HashMap getSecondSensitiveWordMap() {
		return secondSensitiveWordMap;
	}
	
	public HashMap getThirdSensitiveWordMap() {
		return thirdSensitiveWordMap;
	}
	
	/**
	 * 初始化敏感词库，构建DFA算法模型
	 *
	 * @param sensitiveWordSet
	 *            敏感词库
	 */
	public synchronized void init(int level, Set<String> sensitiveWordSet) {
		if (level == SensitiveWord.FIRST_LEVEL) {
			initSensitiveWordMap(firstSensitiveWordMap, sensitiveWordSet);
		} else if (level == SensitiveWord.SECOND_LEVEL) {
			initSensitiveWordMap(secondSensitiveWordMap, sensitiveWordSet);
		} else {
			initSensitiveWordMap(thirdSensitiveWordMap, sensitiveWordSet);
		}
	}
	
	/**
	 * 初始化敏感词库，构建DFA算法模型
	 *
	 * @param sensitiveWordSet
	 *            敏感词库
	 */
	private void initSensitiveWordMap(HashMap sensitiveWordMap, Set<String> sensitiveWordSet) {
		// 初始化敏感词容器，减少扩容操作
		String key;
		Map nowMap;
		Map<String, String> newWorMap;
		// 迭代sensitiveWordSet
		Iterator<String> iterator = sensitiveWordSet.iterator();
		while (iterator.hasNext()) {
			// 关键字
			key = iterator.next();
			nowMap = sensitiveWordMap;
			for (int i = 0; i < key.length(); i++) {
				// 转换成char型
				char keyChar = key.charAt(i);
				// 库中获取关键字
				Object wordMap = nowMap.get(keyChar);
				// 如果存在该key，直接赋值，用于下一个循环获取
				if (wordMap != null) {
					nowMap = (Map) wordMap;
				} else {
					// 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
					newWorMap = new HashMap<>();
					// 不是最后一个
					newWorMap.put("isEnd", "0");
					nowMap.put(keyChar, newWorMap);
					nowMap = newWorMap;
				}

				if (i == key.length() - 1) {
					// 最后一个
					nowMap.put("isEnd", "1");
				}
			}
		}
		System.out.println(sensitiveWordMap);
	}

	public String firstLevelFilter(String txt) {
		return convertSet(getSensitiveWord(firstSensitiveWordMap, txt));
	}
	
	public String secondLevelFilter(String txt) {
		return convertSet(getSensitiveWord(secondSensitiveWordMap, txt));
	}
	
	public String thirdLevelFilter(String txt) {
		return convertSet(getSensitiveWord(thirdSensitiveWordMap, txt));
	}
	
	private String convertSet(Set<String> set) {
		String result = "";
		if (!set.isEmpty()) {
			for (String word : set) {
				result += word + ",";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	/**
	 * 判断文字是否包含敏感字符
	 *
	 * @param txt
	 *            文字
	 * @param matchType
	 *            匹配规则 1：最小匹配规则，2：最大匹配规则
	 * @return 若包含返回true，否则返回false
	 */
	public boolean contains(HashMap sensitiveWordMap, String txt, int matchType) {
		boolean flag = false;
		for (int i = 0; i < txt.length(); i++) {
			int matchFlag = checkSensitiveWord(sensitiveWordMap, txt, i, matchType); // 判断是否包含敏感字符
			if (matchFlag > 0) { // 大于0存在，返回true
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 判断文字是否包含敏感字符
	 *
	 * @param txt
	 *            文字
	 * @return 若包含返回true，否则返回false
	 */
	public boolean contains(HashMap sensitiveWordMap, String txt) {
		return contains(sensitiveWordMap, txt, MaxMatchType);
	}

	/**
	 * 获取文字中的敏感词
	 *
	 * @param txt
	 *            文字
	 * @param matchType
	 *            匹配规则 1：最小匹配规则，2：最大匹配规则
	 * @return
	 */
	public Set<String> getSensitiveWord(HashMap sensitiveWordMap, String txt, int matchType) {
		Set<String> sensitiveWordList = new HashSet<>();

		for (int i = 0; i < txt.length(); i++) {
			// 判断是否包含敏感字符
			int length = checkSensitiveWord(sensitiveWordMap, txt, i, matchType);
			if (length > 0) {// 存在,加入list中
				sensitiveWordList.add(txt.substring(i, i + length));
				i = i + length - 1;// 减1的原因，是因为for会自增
			}
		}

		return sensitiveWordList;
	}

	/**
	 * 获取文字中的敏感词
	 *
	 * @param txt
	 *            文字
	 * @return
	 */
	public Set<String> getSensitiveWord(HashMap sensitiveWordMap, String txt) {
		return getSensitiveWord(sensitiveWordMap, txt, MaxMatchType);
	}

	/**
	 * 替换敏感字字符
	 *
	 * @param txt
	 *            文本
	 * @param replaceChar
	 *            替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
	 * @param matchType
	 *            敏感词匹配规则
	 * @return
	 */
	public String replaceSensitiveWord(HashMap sensitiveWordMap, String txt, char replaceChar, int matchType) {
		String resultTxt = txt;
		// 获取所有的敏感词
		Set<String> set = getSensitiveWord(sensitiveWordMap, txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word;
		String replaceString;
		while (iterator.hasNext()) {
			word = iterator.next();
			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);
		}

		return resultTxt;
	}

	/**
	 * 替换敏感字字符
	 *
	 * @param txt
	 *            文本
	 * @param replaceChar
	 *            替换的字符，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符：*， 替换结果：我爱***
	 * @return
	 */
	public String replaceSensitiveWord(HashMap sensitiveWordMap, String txt, char replaceChar) {
		return replaceSensitiveWord(sensitiveWordMap, txt, replaceChar, MaxMatchType);
	}

	/**
	 * 替换敏感字字符
	 *
	 * @param txt
	 *            文本
	 * @param replaceStr
	 *            替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
	 * @param matchType
	 *            敏感词匹配规则
	 * @return
	 */
	public String replaceSensitiveWord(HashMap sensitiveWordMap, String txt, String replaceStr, int matchType) {
		String resultTxt = txt;
		// 获取所有的敏感词
		Set<String> set = getSensitiveWord(sensitiveWordMap, txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word;
		while (iterator.hasNext()) {
			word = iterator.next();
			resultTxt = resultTxt.replaceAll(word, replaceStr);
		}

		return resultTxt;
	}

	/**
	 * 替换敏感字字符
	 *
	 * @param txt
	 *            文本
	 * @param replaceStr
	 *            替换的字符串，匹配的敏感词以字符逐个替换，如 语句：我爱中国人 敏感词：中国人，替换字符串：[屏蔽]，替换结果：我爱[屏蔽]
	 * @return
	 */
	public String replaceSensitiveWord(HashMap sensitiveWordMap, String txt, String replaceStr) {
		return replaceSensitiveWord(sensitiveWordMap, txt, replaceStr, MaxMatchType);
	}

	/**
	 * 获取替换字符串
	 *
	 * @param replaceChar
	 * @param length
	 * @return
	 */
	private String getReplaceChars(char replaceChar, int length) {
		String resultReplace = String.valueOf(replaceChar);
		for (int i = 1; i < length; i++) {
			resultReplace += replaceChar;
		}

		return resultReplace;
	}

	/**
	 * 检查文字中是否包含敏感字符，检查规则如下：<br>
	 *
	 * @param txt
	 * @param beginIndex
	 * @param matchType
	 * @return 如果存在，则返回敏感词字符的长度，不存在返回0
	 */
	private int checkSensitiveWord(HashMap sensitiveWordMap, String txt, int beginIndex, int matchType) {
		// 敏感词结束标识位：用于敏感词只有1位的情况
		boolean flag = false;
		// 匹配标识数默认为0
		int matchFlag = 0;
		char word;
		Map nowMap = sensitiveWordMap;
		for (int i = beginIndex; i < txt.length(); i++) {
			word = txt.charAt(i);
			// 获取指定key
			nowMap = (Map) nowMap.get(word);
			if (nowMap != null) {// 存在，则判断是否为最后一个
				// 找到相应key，匹配标识+1
				matchFlag++;
				// 如果为最后一个匹配规则,结束循环，返回匹配标识数
				if ("1".equals(nowMap.get("isEnd"))) {
					// 结束标志位为true
					flag = true;
					// 最小规则，直接返回,最大规则还需继续查找
					if (MinMatchTYpe == matchType) {
						break;
					}
				}
			} else {// 不存在，直接返回
				break;
			}
		}
		if (matchFlag < 2 || !flag) {// 长度必须大于等于1，为词
			matchFlag = 0;
		}
		return matchFlag;
	}

	public static void main(String[] args) {

//		Set<String> sensitiveWordSet = new HashSet<>();
//		sensitiveWordSet.add("鸡吧");
//		sensitiveWordSet.add("吃鸡吧");
//		sensitiveWordSet.add("爱恋");
//		sensitiveWordSet.add("静静");
//		sensitiveWordSet.add("哈哈");
//		sensitiveWordSet.add("啦啦");
//		sensitiveWordSet.add("感动");
//		sensitiveWordSet.add("发呆");
		// 初始化敏感词库
//		SensitiveWordUtil.init(sensitiveWordSet);
//
//		System.out.println("敏感词的数量：" + SensitiveWordUtil.sensitiveWordMap.size());
//		String string = "我们去吃鸡吧~";
//		System.out.println("待检测语句字数：" + string.length());

		// 是否含有关键字
//		boolean result = SensitiveWordUtil.contains(string);
//		System.out.println(result);
//		result = SensitiveWordUtil.contains(string, SensitiveWordUtil.MinMatchTYpe);
//		System.out.println(result);

		// 获取语句中的敏感词
//		Set<String> set = SensitiveWordUtil.getSensitiveWord(string);
//		System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
//		set = SensitiveWordUtil.getSensitiveWord(string, SensitiveWordUtil.MinMatchTYpe);
//		System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
//
//		// 替换语句中的敏感词
//		String filterStr = SensitiveWordUtil.replaceSensitiveWord(string, '*');
//		System.out.println(filterStr);
//		filterStr = SensitiveWordUtil.replaceSensitiveWord(string, '*', SensitiveWordUtil.MinMatchTYpe);
//		System.out.println(filterStr);

//		String filterStr2 = SensitiveWordUtil.replaceSensitiveWord(string, "[*敏感词*]");
//		System.out.println(filterStr2);
//		filterStr2 = SensitiveWordUtil.replaceSensitiveWord(string, "[*敏感词*]", SensitiveWordUtil.MinMatchTYpe);
//		System.out.println(filterStr2);
	}

}
