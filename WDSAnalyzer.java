package com.weidays.core.lucene.myanalyzer;

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.IOUtils;

/**
 * 自定义分词 因为自己要索引的部分内容是没有特殊意义的.然后要实现和数据库like一样的查询(输入任意部分关键字都能比中) 所以.做了一个 排列 取子集的
 * 分词方法. <br>
 * 示例:这个世界会好吗 <br>
 * 分词结果:这个 这个世 这个世界 这个世界会 这个世界会好 这个世界会好吗 个世 个世界 个世界会 个世界会好 个世界会好吗 世界 世界会 世界会好
 * 世界会好吗 界会 界会好 界会好吗 会好 会好吗 好吗
 * 
 * @author weidays 2016年8月16日
 *
 */
public class WDSAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		Reader reader = null;
		try {
			reader = new StringReader(arg0);
			WDSTokenizer it = new WDSTokenizer(reader);
			return new Analyzer.TokenStreamComponents(it);
		} finally {
			IOUtils.closeWhileHandlingException(reader);
		}
	}

}