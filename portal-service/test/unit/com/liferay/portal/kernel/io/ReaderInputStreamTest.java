/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.io;

import com.liferay.portal.kernel.test.TestCase;
import com.liferay.portal.kernel.util.StringPool;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Tina Tian
 */
public class ReaderInputStreamTest extends TestCase {

	public void testAvailable() {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		try {
			ReaderInputStream readerInputStream = new ReaderInputStream(
				reader, StringPool.UTF8);

			int available = readerInputStream.available();

			assertEquals(0, available);

			readerInputStream.read();

			available = readerInputStream.available();

			assertEquals(_TEST_STRING_ENGLISH.length() - 1, available);
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testConstructor1() {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		try {
			new ReaderInputStream(reader, StringPool.UTF8, 0, 0);

			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testConstructor2() {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		try {
			new ReaderInputStream(reader, StringPool.UTF8, 1, 1);

			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testRead1() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		int result = readerInputStream.read();

		readerInputStream.close();

		assertEquals(_expectedBytesForEnglish[0], result);
	}

	public void testRead2() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		byte[] bytes = new byte[_expectedBytesForEnglish.length];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForEnglish.length, result);
		assertEquals(_expectedBytesForEnglish, bytes);
	}

	public void testRead3() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_CHINESE);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		byte[] bytes = new byte[_expectedBytesForChinese.length];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForChinese.length, result);
		assertEquals(_expectedBytesForChinese, bytes);
	}

	public void testRead4() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_CHINESE);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8, 6, 6);

		byte[] bytes = new byte[_expectedBytesForChinese.length];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForChinese.length, result);
		assertEquals(_expectedBytesForChinese, bytes);
	}

	public void testRead5() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_CHINESE);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8, 4, 6);

		byte[] bytes = new byte[_expectedBytesForChinese.length];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForChinese.length, result);
		assertEquals(_expectedBytesForChinese, bytes);
	}

	public void testRead6() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_CHINESE);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8, 6, 4);

		byte[] bytes = new byte[_expectedBytesForChinese.length];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForChinese.length, result);
		assertEquals(_expectedBytesForChinese, bytes);
	}

	public void testRead7() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(reader);

		byte[] bytes = new byte[_expectedBytesForEnglish.length + 1];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForEnglish.length, result);

		for (int i = 0; i < _expectedBytesForEnglish.length; i++) {
			assertEquals(_expectedBytesForEnglish[i], bytes[i]);
		}

		assertEquals(0, bytes[_expectedBytesForEnglish.length]);
	}

	public void testRead8() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = null;

		try {
			readerInputStream = new ReaderInputStream(reader, StringPool.UTF8);

			readerInputStream.read(null, 0, 0);

			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
		finally {
			readerInputStream.close();
		}
	}

	public void testRead9() throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = null;

		try {
			readerInputStream = new ReaderInputStream(reader, StringPool.UTF8);

			readerInputStream.read(new byte[1], 0, 2);

			fail();
		}
		catch (Exception e) {
			assertTrue(e instanceof IndexOutOfBoundsException);
		}
		finally {
			readerInputStream.close();
		}
	}

	public void testSkip1()  throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		int skipLength = 3;

		readerInputStream.skip(skipLength);

		byte[] bytes = new byte[_expectedBytesForEnglish.length - skipLength];

		int result = readerInputStream.read(bytes);

		readerInputStream.close();

		assertEquals(_expectedBytesForEnglish.length - skipLength, result);
		assertEquals(
			_expectedBytesForEnglish.length - skipLength, bytes.length);

		for (int i = skipLength; i < _expectedBytesForEnglish.length; i++) {
			assertEquals(_expectedBytesForEnglish[i], bytes[i - skipLength]);
		}
	}

	public void testSkip2()  throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		int skipLength = _expectedBytesForEnglish.length;

		int result  = (int)readerInputStream.skip(skipLength);

		assertEquals(skipLength, result);

		result = readerInputStream.read();

		readerInputStream.close();

		assertEquals(-1, result);
	}

	public void testSkip3()  throws IOException {
		Reader reader = new StringReader(_TEST_STRING_ENGLISH);

		ReaderInputStream readerInputStream = new ReaderInputStream(
			reader, StringPool.UTF8);

		int skipLength = _expectedBytesForEnglish.length + 1;

		int result  = (int)readerInputStream.skip(skipLength);

		readerInputStream.close();

		assertEquals(_expectedBytesForEnglish.length, result);
	}

	protected void assertEquals(byte[] expectBytes, byte[] actualBytes) {
		assertEquals(expectBytes.length, actualBytes.length);

		for (int i = 0; i < expectBytes.length; i++) {
			assertEquals(expectBytes[i], actualBytes[i]);
		}
	}

	private static final String _TEST_STRING_CHINESE = "这是一个测试字符串";

	private static final String _TEST_STRING_ENGLISH = "This is a test string";

	private static byte[] _expectedBytesForChinese;
	private static byte[] _expectedBytesForEnglish;

	static {
		try {
			_expectedBytesForChinese = _TEST_STRING_CHINESE.getBytes(
				StringPool.UTF8);
			_expectedBytesForEnglish = _TEST_STRING_ENGLISH.getBytes(
				StringPool.UTF8);
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

}