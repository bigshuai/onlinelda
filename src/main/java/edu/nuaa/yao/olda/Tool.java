package edu.nuaa.yao.olda;

import java.util.ArrayList;
import java.util.List;



public class Tool {
	public static final List<String> DOMAINS = DomainService.getDomainNames();

	public static void main(String[] args) {
		Vocabulary small = new Vocabulary();
		Vocabulary big = new Vocabulary();
		small.readWordMap("");
		big.readWordMap("");
		List<Integer> map = new ArrayList<Integer>();
		List<String> list = small.id2word;
		for (int i = 0, h = list.size(); i < h; i++) {
			map.add(big.word2id.get(list.get(i)));
		}
	}
}
