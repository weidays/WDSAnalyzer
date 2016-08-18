package com.weidays.core.lucene.myanalyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.util.CharacterUtils.CharacterBuffer;

/**
 * 自定义分词 因为自己要索引的部分内容是没有特殊意义的.然后要实现和数据库like一样的查询(输入任意部分关键字都能比中) 所以.做了一个 排列 取子集的
 * 分词方法. <br>
 * 示例:这个世界会好吗 <br>
 * 分词结果:这个 这个世 这个世界 这个世界会 这个世界会好 这个世界会好吗 个世 个世界 个世界会 个世界会好 个世界会好吗 世界 世界会 世界会好
 * 世界会好吗 界会 界会好 界会好吗 会好 会好吗 好吗
 * 
 * @author weidays 2016年8月16日
 * 
 *
 */
public class WDSTokenizer extends Tokenizer {

	private int offset = 0, dataLen = 0;
	private static final int MIN_WORD_LEN = 2;
	// private static final int MAX_WORD_LEN = 255;
	private static final int IO_BUFFER_SIZE = 4096;

	private final CharacterUtils charUtils;
	private final CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(IO_BUFFER_SIZE);
	// 词元文本属性
	private final CharTermAttribute termAtt;
	// 词元位移属性
	private final OffsetAttribute offsetAtt;
	// 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
	// 记录最后一个词元的结束位置
	private int endPosition;

	public WDSTokenizer(Reader in) {
		this(in, false);
	}

	public WDSTokenizer(Reader in, boolean useSmart) {
		offsetAtt = addAttribute(OffsetAttribute.class);
		termAtt = addAttribute(CharTermAttribute.class);
		charUtils = CharacterUtils.getInstance();
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		if (dataLen == 0) {
			dataLen = MIN_WORD_LEN;
			charUtils.fill(ioBuffer, input); // read supplementary char aware
												// with CharacterUtils
		}
		if (offset >= ioBuffer.getLength()) {
			return false;
		}
		termAtt.copyBuffer(ioBuffer.getBuffer(), offset, dataLen);
		if (offset + dataLen >= ioBuffer.getLength()) {
			offset++;
			dataLen = MIN_WORD_LEN;
		} else {
			dataLen++;
		}
		return true;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		offset = 0;
		dataLen = 0;
		ioBuffer.reset(); // make sure to reset the IO buffer!!
	}

	@Override
	public final void end() {
		// set final offset
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

}