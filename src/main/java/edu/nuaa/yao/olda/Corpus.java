package edu.nuaa.yao.olda;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

// public static Corpus loadCorpus(String filePath)   从指定路径加载语料库 //词典大小设为本地词典大小
// public static Corpus loadCorpus(String filename, Vocabulary voc) 从指定路径加载语料库 自带全局词典
// public static Corpus loadCorpus(String[] strs, Vocabulary vocabulary) 从指定字符串数组加载语料库 自带全局词典
public class Corpus {
	//本地局部词典
	public Vocabulary localVoc;
	//文档集
	public List<Document> docs;
	//大小
	public int M;
	public int V;
	
	// 局部id到全局id的映射   map from local coordinates (id) to global ones 
	// 全局如果没有设置 置空    null if the global dictionary is not set
	public Map<Integer, Integer> lid2gid; 
	
	//链接到全局  训练数据中是null 测试数据中不空  link to a global dictionary (optional), null for train data, not null for test data
	//全局词典
	public Vocabulary globalVoc;	 	
	
	public Corpus() {
	//构造语料 全为空	
		localVoc = new Vocabulary();
		M = 0;
		V = 0;
		docs = new ArrayList<Document>();
		lid2gid = null;
		globalVoc = null;
	}
	
	public Corpus(int M) {
	//参数为M  有M个文档的语料
		localVoc = new Vocabulary();
		this.M = M;
		V = 0;
		docs = new ArrayList<Document>(M);
		lid2gid = null;
		globalVoc = null;
	}
	
	public Corpus(int M, Vocabulary globalVoc) {
	//参数：文档集大小，全局词典	
		localVoc = new Vocabulary();	
		this.M = M;
		this.V = 0;
		docs = new ArrayList<Document>(M);	
		
		this.globalVoc = globalVoc;
		lid2gid = new HashMap<Integer, Integer>();
	}
	
	//-------------------------------------------------------------
	//Public Instance Methods
	//-------------------------------------------------------------
	/**
	 * 文档doc加入文档集，设置索引号为idx  参数：文档doc，索引号idx
	 * 在索引idx中设置文档  如果idx大于0小于M
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param doc document to be set
	 * @param idx index in the document array
	 */	
	public void setDoc(Document doc, int idx){
		if (0 <= idx && idx < M){
			docs.add(idx, doc);;
		}
	}
	
	/**
	 * 对文本line分词，并把词语添加到本地词典，如果有全局词典，也添加。 然后完成本地id到全局id的映射 put(_id, id)
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param str string contains doc
	 * @param idx index in the document array
	 */
	public void setDoc(String line) {
		String domain = null;
		//分割line  第一个参数是要分隔的字符串line,第二个是分隔字符集合" \t\r\n"
		StringTokenizer st = new StringTokenizer(line, " \t\r\n");
		domain = st.nextToken(); //返回每行第一个词语  //返回从当前位置到下一个分隔符的字符串
		//st.nextToken();
		//st.nextToken();
		//st.nextToken();
		
		int nid = st.countTokens();  //返回分隔符数量
		//System.out.println("本行文本的分隔符数量nid:"+nid);
		int[] ids = new int[nid]; //int数组ids大小为文本词语数 存放文本中词语在词典中对应的索引号
		for (int i = 0; i < nid; i++) {
			String word = st.nextToken(); //通过下一分隔 得到一个词语
			int _id = localVoc.id2word.size(); //最大编号_id=本地字典.索引to词语映射表.大小
			if (localVoc.contains(word)) {   //如果本地词典包含词语w  _id置为词语w的索引号
				_id = localVoc.getId(word);
			}
			if (globalVoc != null){          //如果全局词典不为null  
				//get the global id	 //取词语w在全局词典中的id				
				Integer id = globalVoc.word2id.get(word);
				//System.out.println(id); 
				if (id != null){         //如果全局id存在 表明全局词典中有词语w 
					localVoc.addWord(word);	//***************本地词典把词语w添加  
					                        //<可能已经包含这个词语了（如果_id已经不是最大值）>**********				
				}
				else {                  //如果id为空 表明全局词典中没有这个词语           
					//not in global dictionary
					//do nothing currently   本地词典和全局词典 都添加词语w
					localVoc.addWord(word);	//本地词典可能已经包含这个词语了				
					globalVoc.addWord(word);
				}
				lid2gid.put(_id, id); //本地id到全局id的映射中 put(_id, id) **********************************
				ids[i] = _id;  //
			}
			else {    //如果全局词典为null  不管他了  
				localVoc.addWord(word); //本地词典把词语w添加
				ids[i] = _id;
			}			
		}
		//根据文档词语索引数组ids，原始字符串line，域名domain 构造doc  加入文档集docs
		Document doc = new Document(ids, line, domain);
		docs.add(doc);
	}
	
	/**
	 *  从指定路径加载语料库
	 *  从数据流中 读取一个数据集 创建一个新字典 read a dataset from a stream, create new dictionary
	 *  @return dataset if success and null otherwise
	 */
	public static Corpus loadCorpus(String filePath) {
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "UTF-8"));
			Corpus corpus = new Corpus();
			String line = null;
			int ndoc = 0;
			//逐行读取文本  把文本添加到corpus 文本计数器ndoc加1
			while ((line = br.readLine()) != null) {
				corpus.setDoc(line);//语料库 设置文本line 
				//对文本line分词，并把词语添加到本地词典，如果有全局词典，也添加。 
				//然后完成本地id到全局id的映射 put(_id, id)    构造doc  加入文档集docs
				ndoc++;
			}
			corpus.M = ndoc;
			corpus.V = corpus.localVoc.id2word.size();//词典大小设为本地词典大小
			return corpus;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 从指定路径加载语料库 带着一个预知的词典voc作为全局词典
	 * read a dataset from a file with a preknown vocabulary
	 * @param filename file from which we read dataset
	 * @param dict the dictionary
	 * @return dataset if success and null otherwise
	 */
	public static Corpus loadCorpus(String filename, Vocabulary voc){
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));
			//read number of document   //参数：文档集大小，全局词典  文档集大小为64  *********************
			Corpus corpus = new Corpus(64 , voc);
			
			String line = null;
			int ndoc = 0;
			while ((line = br.readLine()) != null) {
				corpus.setDoc(line);   //语料库 设置文本line 
				//对文本line分词，并把词语添加到本地词典，如果有全局词典，也添加。 
				//然后完成本地id到全局id的映射 put(_id, id)    构造doc  加入文档集docs
				ndoc++;               //文档数+1
			}
			corpus.M = ndoc;
			corpus.V = corpus.localVoc.id2word.size();  //词典大小设为本地词典大小
			return corpus;
		}
		catch (Exception e){
			System.out.println("Read Dataset Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * read a dataset from a string with respect to a specified dictionary
	 * @param str String from which we get the dataset, documents are seperated by newline character	
	 * @param dict the dictionary
	 * @return dataset if success and null otherwise
	 */
	public static Corpus loadCorpus(String[] strs, Vocabulary vocabulary){
		//System.out.println("readDataset...");
		Corpus data = new Corpus(strs.length, vocabulary);  ////参数：文档集大小，全局词典	
		
		for (int i = 0 ; i < strs.length; ++i){
			//System.out.println("set doc " + i);
			data.setDoc(strs[i]); //语料库 设置文本strs[i] 
				//对文本strs[i]分词，并把词语添加到本地词典，如果有全局词典，也添加。 
				//然后完成本地id到全局id的映射 put(_id, id)    构造doc  加入文档集docs
		}
		return data;
	}
}
