package edu.nuaa.yao.olda;

public class Document {
//词语索引数组，长度，原始字符串，domain
	public int[] words;
	public int length;
	public String rawStr;
	public String domain;

	public Document() {
		words = null;
		rawStr = "";
		length = 0;
	}

	public Document(int length, int[] words) {
		this.length = length;
		rawStr = "";
		this.words = words;
	}

	public Document(int[] words, String rawStr, String domain) {
		this.length = words.length;
		this.words = words;
		this.rawStr = rawStr;
		this.domain = domain;
	}

}
