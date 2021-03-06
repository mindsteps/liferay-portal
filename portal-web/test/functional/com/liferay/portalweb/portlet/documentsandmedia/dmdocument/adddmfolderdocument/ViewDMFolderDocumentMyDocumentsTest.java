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

package com.liferay.portalweb.portlet.documentsandmedia.dmdocument.adddmfolderdocument;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class ViewDMFolderDocumentMyDocumentsTest extends BaseTestCase {
	public void testViewDMFolderDocumentMyDocuments() throws Exception {
		selenium.selectWindow("null");
		selenium.selectFrame("relative=top");
		selenium.open("/web/guest/home/");
		selenium.clickAt("link=Documents and Media Test Page",
			RuntimeVariables.replace("Documents and Media Test Page"));
		selenium.waitForPageToLoad("30000");
		selenium.waitForVisible("//button[@title='Icon View']");
		selenium.clickAt("//button[@title='Icon View']",
			RuntimeVariables.replace("Icon View"));
		assertEquals(RuntimeVariables.replace("Mine"),
			selenium.getText("//ul[@class='lfr-component']/li[3]/a"));
		selenium.clickAt("//ul[@class='lfr-component']/li[3]/a",
			RuntimeVariables.replace("Mine"));
		selenium.waitForText("//a[contains(@class,'entry-link')]/span[@class='entry-title']",
			"DM Folder Document Title");
		assertEquals(RuntimeVariables.replace("DM Folder Document Title"),
			selenium.getText(
				"//a[contains(@class,'entry-link')]/span[@class='entry-title']"));
		Thread.sleep(1000);
		selenium.waitForVisible("//button[@title='List View']");
		selenium.clickAt("//button[@title='List View']",
			RuntimeVariables.replace("List View"));
		Thread.sleep(1000);
		selenium.waitForVisible(
			"//button[contains(@class,'aui-state-active') and @title='List View']");
		assertTrue(selenium.isVisible(
				"//button[contains(@class,'aui-state-active') and @title='List View']"));
		selenium.waitForText("//tr[3]/td[2]/div/span/a/span",
			"DM Folder Document Title");
		assertEquals(RuntimeVariables.replace("DM Folder Document Title"),
			selenium.getText("//tr[3]/td[2]/div/span/a/span"));
		assertEquals(RuntimeVariables.replace("0k"),
			selenium.getText("//tr[3]/td[3]"));
		assertEquals(RuntimeVariables.replace("0"),
			selenium.getText("//tr[3]/td[4]"));
		selenium.waitForVisible("//button[@title='Icon View']");
		selenium.clickAt("//button[@title='Icon View']",
			RuntimeVariables.replace("Icon View"));
		Thread.sleep(1000);
		selenium.waitForVisible(
			"//button[contains(@class,'aui-state-active') and @title='Icon View']");
		assertTrue(selenium.isVisible(
				"//button[contains(@class,'aui-state-active') and @title='Icon View']"));
		selenium.waitForText("//a[contains(@class,'entry-link')]/span[@class='entry-title']",
			"DM Folder Document Title");
		assertEquals(RuntimeVariables.replace("DM Folder Document Title"),
			selenium.getText(
				"//a[contains(@class,'entry-link')]/span[@class='entry-title']"));
	}
}