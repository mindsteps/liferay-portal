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

package com.liferay.portal.tools.samplesqlbuilder;

import com.liferay.counter.model.Counter;
import com.liferay.counter.model.impl.CounterImpl;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Account;
import com.liferay.portal.model.ClassName;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ContactConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.model.LayoutTypePortletConstants;
import com.liferay.portal.model.ModelHintsUtil;
import com.liferay.portal.model.PortletPreferences;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.VirtualHost;
import com.liferay.portal.model.impl.AccountImpl;
import com.liferay.portal.model.impl.ClassNameImpl;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.ContactImpl;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.model.impl.PortletPreferencesImpl;
import com.liferay.portal.model.impl.ResourcePermissionImpl;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.model.impl.VirtualHostImpl;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.impl.AssetEntryImpl;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.model.BlogsStatsUser;
import com.liferay.portlet.blogs.model.impl.BlogsEntryImpl;
import com.liferay.portlet.blogs.model.impl.BlogsStatsUserImpl;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryConstants;
import com.liferay.portlet.documentlibrary.model.DLFileEntryMetadata;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.model.DLFileEntryTypeConstants;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLSync;
import com.liferay.portlet.documentlibrary.model.DLSyncConstants;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLSyncImpl;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordVersion;
import com.liferay.portlet.dynamicdatalists.model.impl.DDLRecordImpl;
import com.liferay.portlet.dynamicdatalists.model.impl.DDLRecordSetImpl;
import com.liferay.portlet.dynamicdatalists.model.impl.DDLRecordVersionImpl;
import com.liferay.portlet.dynamicdatamapping.model.DDMContent;
import com.liferay.portlet.dynamicdatamapping.model.DDMStorageLink;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructureLink;
import com.liferay.portlet.dynamicdatamapping.model.impl.DDMContentImpl;
import com.liferay.portlet.dynamicdatamapping.model.impl.DDMStorageLinkImpl;
import com.liferay.portlet.dynamicdatamapping.model.impl.DDMStructureImpl;
import com.liferay.portlet.dynamicdatamapping.model.impl.DDMStructureLinkImpl;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.model.impl.JournalArticleImpl;
import com.liferay.portlet.journal.model.impl.JournalArticleResourceImpl;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBCategoryConstants;
import com.liferay.portlet.messageboards.model.MBDiscussion;
import com.liferay.portlet.messageboards.model.MBMailingList;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBMessageConstants;
import com.liferay.portlet.messageboards.model.MBStatsUser;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.model.impl.MBCategoryImpl;
import com.liferay.portlet.messageboards.model.impl.MBDiscussionImpl;
import com.liferay.portlet.messageboards.model.impl.MBMailingListImpl;
import com.liferay.portlet.messageboards.model.impl.MBMessageImpl;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadImpl;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.portlet.social.model.impl.SocialActivityImpl;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.model.WikiPageConstants;
import com.liferay.portlet.wiki.model.WikiPageResource;
import com.liferay.portlet.wiki.model.impl.WikiNodeImpl;
import com.liferay.portlet.wiki.model.impl.WikiPageImpl;
import com.liferay.portlet.wiki.model.impl.WikiPageResourceImpl;
import com.liferay.util.SimpleCounter;

import java.io.File;
import java.io.IOException;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory(
			String baseDir, int maxBlogsEntryCount, int maxGroupsCount,
			int maxJournalArticleSize, int maxMBCategoryCount,
			int maxMBThreadCount, int maxMBMessageCount,
			int maxUserToGroupCount)
		throws Exception {

		_baseDir = baseDir;
		_maxBlogsEntryCount = maxBlogsEntryCount;
		_maxGroupsCount = maxGroupsCount;
		_maxMBCategoryCount = maxMBCategoryCount;
		_maxMBThreadCount = maxMBThreadCount;
		_maxMBMessageCount = maxMBMessageCount;
		_maxUserToGroupCount = maxUserToGroupCount;

		_counter = new SimpleCounter(_maxGroupsCount + 1);
		_futureDateCounter = new SimpleCounter();
		_resourcePermissionCounter = new SimpleCounter();
		_socialActivityCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();

		_classNames = new ArrayList<ClassName>();

		List<String> models = ModelHintsUtil.getModels();

		for (String model : models) {
			ClassName className = new ClassNameImpl();

			long classNameId = _counter.get();

			className.setClassNameId(classNameId);

			className.setValue(model);

			_classNames.add(className);

			_classNamesMap.put(model, classNameId);
		}

		_accountId = _counter.get();
		_companyId = _counter.get();
		_sampleUserId = _counter.get();

		initCompany();
		initDLFileEntryType();
		initGroups();
		initJournalArticle(maxJournalArticleSize);
		initRoles();
		initUserNames();
		initUsers();
		initVirtualHost();
	}

	public Account getAccount() {
		return _account;
	}

	public Role getAdministratorRole() {
		return _administratorRole;
	}

	public long getBlogsEntryClassNameId() {
		return _classNamesMap.get(BlogsEntry.class.getName());
	}

	public List<ClassName> getClassNames() {
		return _classNames;
	}

	public Company getCompany() {
		return _company;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public SimpleCounter getCounter() {
		return _counter;
	}

	public String getDateLong(Date date) {
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return _simpleDateFormat.format(date);
	}

	public long getDDLRecordSetClassNameId() {
		return _classNamesMap.get(DDLRecordSet.class.getName());
	}

	public long getDDMContentClassNameId() {
		return _classNamesMap.get(DDMContent.class.getName());
	}

	public DLFileEntryType getDefaultDLFileEntryType() {
		return _defaultDLFileEntryType;
	}

	public User getDefaultUser() {
		return _defaultUser;
	}

	public long getDLFileEntryClassNameId() {
		return _classNamesMap.get(DLFileEntry.class.getName());
	}

	public long getGroupClassNameId() {
		return _classNamesMap.get(Group.class.getName());
	}

	public List<Group> getGroups() {
		return _groups;
	}

	public Group getGuestGroup() {
		return _guestGroup;
	}

	public User getGuestUser() {
		return _guestUser;
	}

	public long getJournalArticleClassNameId() {
		return _classNamesMap.get(JournalArticle.class.getName());
	}

	public List<Long> getNewUserGroupIds(long groupId) {
		List<Long> groupIds = new ArrayList<Long>(_maxUserToGroupCount + 1);

		groupIds.add(_guestGroup.getGroupId());

		if ((groupId + _maxUserToGroupCount) > _maxGroupsCount) {
			groupId = groupId - _maxUserToGroupCount + 1;
		}

		for (int i = 0; i < _maxUserToGroupCount; i++) {
			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public Role getPowerUserRole() {
		return _powerUserRole;
	}

	public List<Role> getRoles() {
		return _roles;
	}

	public User getSampleUser() {
		return _sampleUser;
	}

	public Role getUserRole() {
		return _userRole;
	}

	public VirtualHost getVirtualHost() {
		return _virtualHost;
	}

	public long getWikiPageClassNameId() {
		return _classNamesMap.get(WikiPage.class.getName());
	}

	public void initCompany() {
		_company = new CompanyImpl();

		_company.setCompanyId(_companyId);
		_company.setAccountId(_accountId);
		_company.setWebId("liferay.com");
		_company.setMx("liferay.com");
		_company.setActive(true);

		_account = new AccountImpl();

		_account.setAccountId(_accountId);
		_account.setCompanyId(_companyId);
		_account.setCreateDate(new Date());
		_account.setModifiedDate(new Date());
		_account.setName("Liferay");
		_account.setLegalName("Liferay, Inc.");
	}

	public void initDLFileEntryType() {
		_defaultDLFileEntryType = new DLFileEntryTypeImpl();

		_defaultDLFileEntryType.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryType.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryType.setCreateDate(nextFutureDate());
		_defaultDLFileEntryType.setModifiedDate(nextFutureDate());
		_defaultDLFileEntryType.setName(
			DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
	}

	public void initGroups() throws Exception {
		long groupClassNameId = getGroupClassNameId();

		long guestGroupId = _counter.get();

		_guestGroup = newGroup(
			guestGroupId, groupClassNameId, guestGroupId, GroupConstants.GUEST,
			true);

		_groups = new ArrayList<Group>(_maxGroupsCount);

		for (int i = 1; i <= _maxGroupsCount; i++) {
			Group group = newGroup(i, groupClassNameId, i, "Site " + i, true);

			_groups.add(group);
		}
	}

	public void initJournalArticle(int maxJournalArticleSize) {
		StringBundler sb = new StringBundler();

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><static-content language-id=");
		sb.append("\"en_US\"><![CDATA[<p>");

		if (maxJournalArticleSize <= 0) {
			maxJournalArticleSize = 1;
		}

		char[] chars = new char[maxJournalArticleSize];

		for (int i = 0; i < maxJournalArticleSize; i++) {
			chars[i] = (char)(CharPool.LOWER_CASE_A + (i % 26));
		}

		sb.append(new String(chars));

		sb.append("</p>]]></static-content></root>");

		_journalArticleContent = sb.toString();
	}

	public void initRoles() {
		_roles = new ArrayList<Role>();

		// Administrator

		_administratorRole = newRole(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR);

		_roles.add(_administratorRole);

		// Guest

		_guestRole = newRole(RoleConstants.GUEST, RoleConstants.TYPE_REGULAR);

		_roles.add(_guestRole);

		// Organization Administrator

		Role organizationAdministratorRole = newRole(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			RoleConstants.TYPE_ORGANIZATION);

		_roles.add(organizationAdministratorRole);

		// Organization Owner

		Role organizationOwnerRole = newRole(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION);

		_roles.add(organizationOwnerRole);

		// Organization User

		Role organizationUserRole = newRole(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION);

		_roles.add(organizationUserRole);

		// Owner

		_ownerRole = newRole(RoleConstants.OWNER, RoleConstants.TYPE_REGULAR);

		_roles.add(_ownerRole);

		// Power User

		_powerUserRole = newRole(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR);

		_roles.add(_powerUserRole);

		// Site Administrator

		Role siteAdministratorRole = newRole(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE);

		_roles.add(siteAdministratorRole);

		// Site Member

		Role siteMemberRole = newRole(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE);

		_roles.add(siteMemberRole);

		// Site Owner

		Role siteOwnerRole = newRole(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE);

		_roles.add(siteOwnerRole);

		// User

		_userRole = newRole(RoleConstants.USER, RoleConstants.TYPE_REGULAR);

		_roles.add(_userRole);
	}

	public void initUserNames() throws IOException {
		String dependenciesDir =
			"../portal-impl/src/com/liferay/portal/tools/samplesqlbuilder/" +
				"dependencies/";

		_firstNames = ListUtil.fromFile(
			new File(_baseDir, dependenciesDir + "first_names.txt"));
		_lastNames = ListUtil.fromFile(
			new File(_baseDir, dependenciesDir + "last_names.txt"));
	}

	public void initUsers() {
		_defaultUser = newUser(
			_counter.get(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true);
		_guestUser = newUser(_counter.get(), "Test", "Test", "Test", false);
		_sampleUser = newUser(
			_sampleUserId, "Sample", "Sample", "Sample", false);
	}

	public void initVirtualHost() {
		_virtualHost = new VirtualHostImpl();

		_virtualHost.setVirtualHostId(_counter.get());
		_virtualHost.setCompanyId(_companyId);
		_virtualHost.setHostname("localhost");
	}

	public AssetEntry newAssetEntry(BlogsEntry blogsEntry) {
		return newAssetEntry(
			blogsEntry.getGroupId(), blogsEntry.getCreateDate(),
			blogsEntry.getModifiedDate(), getBlogsEntryClassNameId(),
			blogsEntry.getEntryId(), blogsEntry.getUuid(), 0, true,
			ContentTypes.TEXT_HTML, blogsEntry.getTitle());
	}

	public AssetEntry newAssetEntry(DLFileEntry dlFileEntry) {
		return newAssetEntry(
			dlFileEntry.getGroupId(), dlFileEntry.getCreateDate(),
			dlFileEntry.getModifiedDate(), getDLFileEntryClassNameId(),
			dlFileEntry.getFileEntryId(), dlFileEntry.getUuid(),
			dlFileEntry.getFileEntryTypeId(), true, dlFileEntry.getMimeType(),
			dlFileEntry.getTitle());
	}

	public AssetEntry newAssetEntry(JournalArticle journalArticle) {
		return newAssetEntry(
			journalArticle.getGroupId(), journalArticle.getCreateDate(),
			journalArticle.getModifiedDate(), getJournalArticleClassNameId(),
			journalArticle.getResourcePrimKey(), journalArticle.getUuid(), 0,
			true, ContentTypes.TEXT_HTML, journalArticle.getTitle());
	}

	public AssetEntry newAssetEntry(MBMessage mbMessage) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessage.isDiscussion()) {
			classNameId = _classNamesMap.get(MBDiscussion.class.getName());
		}
		else {
			classNameId = _classNamesMap.get(MBMessage.class.getName());
			visible = true;
		}

		return newAssetEntry(
			mbMessage.getGroupId(), mbMessage.getCreateDate(),
			mbMessage.getModifiedDate(), classNameId, mbMessage.getMessageId(),
			mbMessage.getUuid(), 0, visible, ContentTypes.TEXT_HTML,
			mbMessage.getSubject());
	}

	public AssetEntry newAssetEntry(WikiPage wikiPage) {
		return newAssetEntry(
			wikiPage.getGroupId(), wikiPage.getCreateDate(),
			wikiPage.getModifiedDate(), getWikiPageClassNameId(),
			wikiPage.getResourcePrimKey(), wikiPage.getUuid(), 0, true,
			ContentTypes.TEXT_HTML, wikiPage.getTitle());
	}

	public BlogsEntry newBlogsEntry(long groupId, int index) {
		BlogsEntry blogsEntry = new BlogsEntryImpl();

		blogsEntry.setUuid(SequentialUUID.generate());
		blogsEntry.setEntryId(_counter.get());
		blogsEntry.setGroupId(groupId);
		blogsEntry.setCompanyId(_companyId);
		blogsEntry.setUserId(_sampleUserId);
		blogsEntry.setUserName(_sampleUser.getFullName());
		blogsEntry.setCreateDate(new Date());
		blogsEntry.setModifiedDate(new Date());
		blogsEntry.setTitle("Test Blog " + index);
		blogsEntry.setUrlTitle("testblog" + index);
		blogsEntry.setContent("This is test blog " + index + ".");
		blogsEntry.setDisplayDate(new Date());
		blogsEntry.setStatusDate(new Date());

		return blogsEntry;
	}

	public BlogsStatsUser newBlogsStatsUser(long groupId) {
		BlogsStatsUser blogsStatsUser = new BlogsStatsUserImpl();

		blogsStatsUser.setStatsUserId(_counter.get());
		blogsStatsUser.setGroupId(groupId);
		blogsStatsUser.setCompanyId(_companyId);
		blogsStatsUser.setUserId(_sampleUserId);
		blogsStatsUser.setEntryCount(_maxBlogsEntryCount);
		blogsStatsUser.setLastPostDate(new Date());

		return blogsStatsUser;
	}

	public Contact newContact(User user) {
		Contact contact = new ContactImpl();

		contact.setContactId(user.getContactId());
		contact.setCompanyId(user.getCompanyId());
		contact.setUserId(user.getUserId());
		contact.setUserName(user.getFullName());
		contact.setCreateDate(new Date());
		contact.setModifiedDate(new Date());
		contact.setClassNameId(_classNamesMap.get(User.class.getName()));
		contact.setClassPK(user.getUserId());
		contact.setAccountId(_accountId);
		contact.setParentContactId(ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(user.getFirstName());
		contact.setLastName(user.getLastName());
		contact.setMale(true);
		contact.setBirthday(new Date());

		return contact;
	}

	public List<Counter> newCounters() {
		List<Counter> counters = new ArrayList<Counter>();

		// Counter

		Counter counter = new CounterImpl();

		counter.setName(Counter.class.getName());
		counter.setCurrentId(_counter.get());

		counters.add(counter);

		// ResourcePermission

		counter = new CounterImpl();

		counter.setName(ResourcePermission.class.getName());
		counter.setCurrentId(_resourcePermissionCounter.get());

		counters.add(counter);

		// SocialActivity

		counter = new CounterImpl();

		counter.setName(SocialActivity.class.getName());
		counter.setCurrentId(_socialActivityCounter.get());

		counters.add(counter);

		return counters;
	}

	public DDLRecord newDDLRecord(
		long groupId, long companyId, long userId, long ddlRecordSetId) {

		DDLRecord ddlRecord = new DDLRecordImpl();

		ddlRecord.setRecordId(_counter.get());
		ddlRecord.setGroupId(groupId);
		ddlRecord.setCompanyId(companyId);
		ddlRecord.setUserId(userId);
		ddlRecord.setCreateDate(nextFutureDate());
		ddlRecord.setRecordSetId(ddlRecordSetId);

		return ddlRecord;
	}

	public DDLRecordSet newDDLRecordSet(
		long groupId, long companyId, long userId, long ddmStructureId) {

		DDLRecordSet ddlRecordSet = new DDLRecordSetImpl();

		ddlRecordSet.setRecordSetId(_counter.get());
		ddlRecordSet.setGroupId(groupId);
		ddlRecordSet.setCompanyId(companyId);
		ddlRecordSet.setUserId(userId);
		ddlRecordSet.setDDMStructureId(ddmStructureId);

		return ddlRecordSet;
	}

	public DDLRecordVersion newDDLRecordVersion(DDLRecord ddlRecord) {
		DDLRecordVersion ddlRecordVersion = new DDLRecordVersionImpl();

		ddlRecordVersion.setRecordVersionId(_counter.get());
		ddlRecordVersion.setGroupId(ddlRecord.getGroupId());
		ddlRecordVersion.setCompanyId(ddlRecord.getCompanyId());
		ddlRecordVersion.setUserId(ddlRecord.getUserId());
		ddlRecordVersion.setRecordSetId(ddlRecord.getRecordSetId());
		ddlRecordVersion.setRecordId(ddlRecord.getRecordId());

		return ddlRecordVersion;
	}

	public DDMContent newDDMContent(long groupId, long companyId, long userId) {
		DDMContent ddmContent = new DDMContentImpl();

		ddmContent.setContentId(_counter.get());
		ddmContent.setGroupId(groupId);
		ddmContent.setCompanyId(companyId);
		ddmContent.setUserId(userId);

		return ddmContent;
	}

	public DDMStorageLink newDDMStorageLink(
		long classNameId, long classPK, long structureId) {

		DDMStorageLink ddmStorageLink = new DDMStorageLinkImpl();

		ddmStorageLink.setStorageLinkId(_counter.get());
		ddmStorageLink.setClassNameId(classNameId);
		ddmStorageLink.setClassPK(classPK);
		ddmStorageLink.setStructureId(structureId);

		return ddmStorageLink;
	}

	public DDMStructure newDDMStructure(
		long groupId, long companyId, long userId, long classNameId) {

		DDMStructure ddmStructure = new DDMStructureImpl();

		ddmStructure.setStructureId(_counter.get());
		ddmStructure.setGroupId(groupId);
		ddmStructure.setCompanyId(companyId);
		ddmStructure.setUserId(userId);
		ddmStructure.setCreateDate(nextFutureDate());
		ddmStructure.setClassNameId(classNameId);

		return ddmStructure;
	}

	public DDMStructureLink newDDMStructureLink(
		long classPK, long structureId) {

		DDMStructureLink ddmStructureLink = new DDMStructureLinkImpl();

		ddmStructureLink.setStructureLinkId(_counter.get());
		ddmStructureLink.setClassNameId(getDLFileEntryClassNameId());
		ddmStructureLink.setClassPK(classPK);
		ddmStructureLink.setStructureId(structureId);

		return ddmStructureLink;
	}

	public DLFileEntry newDlFileEntry(DLFolder dlFoler, int index) {
		DLFileEntry dlFileEntry = new DLFileEntryImpl();

		dlFileEntry.setUuid(SequentialUUID.generate());
		dlFileEntry.setFileEntryId(_counter.get());
		dlFileEntry.setGroupId(dlFoler.getGroupId());
		dlFileEntry.setCompanyId(_companyId);
		dlFileEntry.setUserId(_sampleUserId);
		dlFileEntry.setUserName(_sampleUser.getFullName());
		dlFileEntry.setVersionUserId(_sampleUserId);
		dlFileEntry.setVersionUserName(_sampleUser.getFullName());
		dlFileEntry.setCreateDate(nextFutureDate());
		dlFileEntry.setModifiedDate(nextFutureDate());
		dlFileEntry.setRepositoryId(dlFoler.getRepositoryId());
		dlFileEntry.setFolderId(dlFoler.getFolderId());
		dlFileEntry.setName("TestFile" + index);
		dlFileEntry.setExtension("txt");
		dlFileEntry.setMimeType(ContentTypes.TEXT_PLAIN);
		dlFileEntry.setTitle("TestFile" + index + ".txt");
		dlFileEntry.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntry.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntry.setSize(_maxDLFileEntrySize);

		return dlFileEntry;
	}

	public DLFileEntryMetadata newDLFileEntryMetadata(
		long ddmStorageId, long ddmStructureId, long fileEntryId,
		long fileVersionId) {

		DLFileEntryMetadata dlFileEntryMetadata = new DLFileEntryMetadataImpl();

		dlFileEntryMetadata.setFileEntryMetadataId(_counter.get());
		dlFileEntryMetadata.setDDMStorageId(ddmStorageId);
		dlFileEntryMetadata.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadata.setFileEntryId(fileEntryId);
		dlFileEntryMetadata.setFileVersionId(fileVersionId);

		return dlFileEntryMetadata;
	}

	public DLFileVersion newDLFileVersion(DLFileEntry dlFileEntry) {
		DLFileVersion dlFileVersion = new DLFileVersionImpl();

		dlFileVersion.setFileVersionId(_counter.get());
		dlFileVersion.setGroupId(dlFileEntry.getGroupId());
		dlFileVersion.setCompanyId(dlFileEntry.getCompanyId());
		dlFileVersion.setUserId(dlFileEntry.getUserId());
		dlFileVersion.setRepositoryId(dlFileEntry.getRepositoryId());
		dlFileVersion.setFileEntryId(dlFileEntry.getFileEntryId());
		dlFileVersion.setExtension(dlFileEntry.getExtension());
		dlFileVersion.setMimeType(dlFileEntry.getMimeType());
		dlFileVersion.setTitle(dlFileEntry.getTitle());
		dlFileVersion.setDescription(dlFileEntry.getDescription());
		dlFileVersion.setSize(dlFileEntry.getSize());

		return dlFileVersion;
	}

	public DLFolder newDLFolder(long groupId, long parentFolderId, int index) {
		DLFolder dlFolder = new DLFolderImpl();

		dlFolder.setUuid(SequentialUUID.generate());
		dlFolder.setFolderId(_counter.get());
		dlFolder.setGroupId(groupId);
		dlFolder.setCompanyId(_companyId);
		dlFolder.setUserId(_sampleUserId);
		dlFolder.setUserName(_sampleUser.getFullName());
		dlFolder.setCreateDate(nextFutureDate());
		dlFolder.setModifiedDate(nextFutureDate());
		dlFolder.setRepositoryId(groupId);
		dlFolder.setParentFolderId(parentFolderId);
		dlFolder.setName("Test Folder " + index);
		dlFolder.setLastPostDate(nextFutureDate());
		dlFolder.setDefaultFileEntryTypeId(
			_defaultDLFileEntryType.getFileEntryTypeId());
		dlFolder.setStatusDate(nextFutureDate());

		return dlFolder;
	}

	public DLSync newDLSync(
		long companyId, long fileId, long repositoryId, long parentFolderId,
		boolean typeFolder) {

		DLSync dlSync = new DLSyncImpl();

		dlSync.setSyncId(_counter.get());
		dlSync.setCompanyId(companyId);
		dlSync.setFileId(fileId);
		dlSync.setRepositoryId(repositoryId);
		dlSync.setParentFolderId(parentFolderId);
		dlSync.setEvent(DLSyncConstants.EVENT_ADD);

		if (typeFolder) {
			dlSync.setType(DLSyncConstants.TYPE_FOLDER);
		}
		else {
			dlSync.setType(DLSyncConstants.TYPE_FILE);
		}

		return dlSync;
	}

	public Group newGroup(User user) throws Exception {
		return newGroup(
			_counter.get(), _classNamesMap.get(User.class.getName()),
			user.getUserId(), user.getScreenName(), false);
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	public JournalArticle newJournalArticle(
		JournalArticleResource journalArticleResource) {

		JournalArticle journalArticle = new JournalArticleImpl();

		journalArticle.setUuid(SequentialUUID.generate());
		journalArticle.setId(_counter.get());
		journalArticle.setResourcePrimKey(
			journalArticleResource.getResourcePrimKey());
		journalArticle.setGroupId(journalArticleResource.getGroupId());
		journalArticle.setCompanyId(_companyId);
		journalArticle.setUserId(_sampleUserId);
		journalArticle.setUserName(_sampleUser.getFullName());
		journalArticle.setCreateDate(new Date());
		journalArticle.setModifiedDate(new Date());
		journalArticle.setClassNameId(
			JournalArticleConstants.CLASSNAME_ID_DEFAULT);
		journalArticle.setArticleId(journalArticleResource.getArticleId());
		journalArticle.setVersion(JournalArticleConstants.VERSION_DEFAULT);
		journalArticle.setTitle(_JOURNAL_ARTICLE_TITLE);
		journalArticle.setUrlTitle("Test Journal Article");
		journalArticle.setContent(_journalArticleContent);
		journalArticle.setType("general");
		journalArticle.setDisplayDate(new Date());
		journalArticle.setExpirationDate(nextFutureDate());
		journalArticle.setReviewDate(new Date());
		journalArticle.setIndexable(true);
		journalArticle.setStatusDate(new Date());

		return journalArticle;
	}

	public JournalArticleResource newJournalArticleResource(long groupId) {
		JournalArticleResource journalArticleResource =
			new JournalArticleResourceImpl();

		journalArticleResource.setUuid(SequentialUUID.generate());
		journalArticleResource.setResourcePrimKey(_counter.get());
		journalArticleResource.setGroupId(groupId);
		journalArticleResource.setArticleId(String.valueOf(_counter.get()));

		return journalArticleResource;
	}

	public Layout newLayout(
		long groupId, String name, String column1, String column2) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		Layout layout = new LayoutImpl();

		layout.setUuid(SequentialUUID.generate());
		layout.setPlid(_counter.get());
		layout.setGroupId(groupId);
		layout.setCompanyId(_companyId);
		layout.setCreateDate(new Date());
		layout.setModifiedDate(new Date());
		layout.setLayoutId(simpleCounter.get());
		layout.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layout.setType(LayoutConstants.TYPE_PORTLET);
		layout.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsProperties = new UnicodeProperties(true);

		typeSettingsProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsProperties.setProperty("column-1", column1);
		typeSettingsProperties.setProperty("column-2", column2);

		String typeSettings = StringUtil.replace(
			typeSettingsProperties.toString(), "\n", "\\n");

		layout.setTypeSettings(typeSettings);

		return layout;
	}

	public List<LayoutSet> newLayoutSets(
		long groupId, int publicLayoutSetPageCount) {

		List<LayoutSet> layoutSets = new ArrayList<LayoutSet>(2);

		layoutSets.add(newLayoutSet(groupId, true, 0));
		layoutSets.add(newLayoutSet(groupId, false, publicLayoutSetPageCount));

		return layoutSets;
	}

	public MBCategory newMBCategory(long groupId, int index) {
		MBCategory mbCategory = new MBCategoryImpl();

		mbCategory.setUuid(SequentialUUID.generate());
		mbCategory.setCategoryId(_counter.get());
		mbCategory.setGroupId(groupId);
		mbCategory.setCompanyId(_companyId);
		mbCategory.setUserId(_sampleUserId);
		mbCategory.setUserName(_sampleUser.getFullName());
		mbCategory.setCreateDate(new Date());
		mbCategory.setModifiedDate(new Date());
		mbCategory.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategory.setName("Test Category " + index);
		mbCategory.setDisplayStyle(MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategory.setThreadCount(_maxMBThreadCount);
		mbCategory.setMessageCount(_maxMBThreadCount * _maxMBMessageCount);
		mbCategory.setLastPostDate(new Date());
		mbCategory.setStatusDate(new Date());

		return mbCategory;
	}

	public MBDiscussion newMBDiscussion(
		long classNameId, long classPK, long threadId) {

		MBDiscussion mbDiscussion = new MBDiscussionImpl();

		mbDiscussion.setDiscussionId(_counter.get());
		mbDiscussion.setClassNameId(classNameId);
		mbDiscussion.setClassPK(classPK);
		mbDiscussion.setThreadId(threadId);

		return mbDiscussion;
	}

	public MBMailingList newMBMailingList(MBCategory mbCategory) {
		MBMailingList mbMailingList = new MBMailingListImpl();

		mbMailingList.setUuid(SequentialUUID.generate());
		mbMailingList.setMailingListId(_counter.get());
		mbMailingList.setGroupId(mbCategory.getGroupId());
		mbMailingList.setCompanyId(_companyId);
		mbMailingList.setUserId(_sampleUserId);
		mbMailingList.setUserName(_sampleUser.getFullName());
		mbMailingList.setCreateDate(new Date());
		mbMailingList.setModifiedDate(new Date());
		mbMailingList.setCategoryId(mbCategory.getCategoryId());
		mbMailingList.setInProtocol("pop3");
		mbMailingList.setInServerPort(110);
		mbMailingList.setInUserName(_sampleUser.getEmailAddress());
		mbMailingList.setInPassword(_sampleUser.getPassword());
		mbMailingList.setInReadInterval(5);
		mbMailingList.setOutServerPort(25);

		return mbMailingList;
	}

	public MBMessage newMBMessage(MBThread mbThread, int index) {
		long messageId = 0;
		long parentMessageId = 0;

		if (index == 1) {
			messageId = mbThread.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
		}
		else {
			messageId = _counter.get();
			parentMessageId = mbThread.getRootMessageId();
		}

		return newMBMessage(
			mbThread.getGroupId(), 0, 0, mbThread.getCategoryId(),
			mbThread.getThreadId(), messageId, mbThread.getRootMessageId(),
			parentMessageId, "Test Message " + index,
			"This is test message " + index + ".");
	}

	public MBMessage newMBMessage(
		MBThread mbThread, long classNameId, long classPK, int index) {

		long messageId = 0;
		long parentMessageId = 0;
		String subject = null;
		String body = null;

		if (index == 0) {
			messageId = mbThread.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = StringUtil.valueOf(classPK);
			body = StringUtil.valueOf(classPK);
		}
		else {
			messageId = _counter.get();
			parentMessageId = mbThread.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
		}

		return newMBMessage(
			mbThread.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID, mbThread.getThreadId(),
			messageId, mbThread.getRootMessageId(), parentMessageId, subject,
			body);
	}

	public MBStatsUser newMBStatsUser(long groupId) {
		MBStatsUser mbStatsUser = new MBStatsUserImpl();

		mbStatsUser.setStatsUserId(_counter.get());
		mbStatsUser.setGroupId(groupId);
		mbStatsUser.setUserId(_sampleUserId);
		mbStatsUser.setMessageCount(
			_maxMBCategoryCount * _maxMBThreadCount * _maxMBMessageCount);
		mbStatsUser.setLastPostDate(new Date());

		return mbStatsUser;
	}

	public MBThread newMBThread(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		if (messageCount == 0) {
			messageCount = 1;
		}

		return newMBThread(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId, messageCount);
	}

	public MBThread newMBThread(MBCategory mbCategory) {
		return newMBThread(
			_counter.get(), mbCategory.getGroupId(), mbCategory.getCategoryId(),
			_counter.get(), _maxMBMessageCount);
	}

	public PortletPreferences newPortletPreferences(
		long ownerId, long plid, String portletId, String preferences) {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setPortletPreferencesId(_counter.get());
		portletPreferences.setOwnerId(ownerId);
		portletPreferences.setOwnerType(PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferences.setPlid(plid);
		portletPreferences.setPortletId(portletId);
		portletPreferences.setPreferences(preferences);

		return portletPreferences;
	}

	public List<Layout> newPublicLayouts(long groupId) {
		List<Layout> layouts = new ArrayList<Layout>();

		layouts.add(newLayout(groupId, "welcome", "58,", "47,"));
		layouts.add(newLayout(groupId, "blogs", "", "33,"));
		layouts.add(newLayout(groupId, "document_library", "", "20,"));
		layouts.add(newLayout(groupId, "forums", "", "19,"));
		layouts.add(newLayout(groupId, "wiki", "", "36,"));

		return layouts;
	}

	public List<ResourcePermission> newResourcePermission(
		long companyId, String name, String primKey) {

		List<ResourcePermission> resourcePermissions =
			new ArrayList<ResourcePermission>(2);

		ResourcePermission resourcePermission = new ResourcePermissionImpl();

		resourcePermission.setResourcePermissionId(
			_resourcePermissionCounter.get());
		resourcePermission.setCompanyId(companyId);
		resourcePermission.setName(name);
		resourcePermission.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermission.setPrimKey(primKey);
		resourcePermission.setRoleId(_ownerRole.getRoleId());
		resourcePermission.setOwnerId(_defaultUser.getUserId());
		resourcePermission.setActionIds(1);

		resourcePermissions.add(resourcePermission);

		resourcePermission = new ResourcePermissionImpl();

		resourcePermission.setResourcePermissionId(
			_resourcePermissionCounter.get());
		resourcePermission.setCompanyId(companyId);
		resourcePermission.setName(name);
		resourcePermission.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermission.setPrimKey(primKey);
		resourcePermission.setRoleId(_guestRole.getRoleId());
		resourcePermission.setOwnerId(0);
		resourcePermission.setActionIds(1);

		resourcePermissions.add(resourcePermission);

		return resourcePermissions;
	}

	public SocialActivity newSocialActivity(
		long groupId, long companyId, long userId, long classNameId,
		long classPK) {

		SocialActivity socialActivity = new SocialActivityImpl();

		socialActivity.setActivityId(_socialActivityCounter.get());
		socialActivity.setGroupId(groupId);
		socialActivity.setCompanyId(companyId);
		socialActivity.setUserId(userId);
		socialActivity.setClassNameId(classNameId);
		socialActivity.setClassPK(classPK);

		return socialActivity;
	}

	public User newUser(int index) {
		String[] userName = nextUserName(index - 1);

		return newUser(
			_counter.get(), userName[0], userName[1],
			"test" + _userScreenNameCounter.get(), false);
	}

	public WikiNode newWikiNode(long groupId, int index) {
		WikiNode wikiNode = new WikiNodeImpl();

		wikiNode.setUuid(SequentialUUID.generate());
		wikiNode.setNodeId(_counter.get());
		wikiNode.setGroupId(groupId);
		wikiNode.setCompanyId(_companyId);
		wikiNode.setUserId(_sampleUserId);
		wikiNode.setUserName(_sampleUser.getFullName());
		wikiNode.setCreateDate(new Date());
		wikiNode.setModifiedDate(new Date());
		wikiNode.setName("Test Node " + index);
		wikiNode.setLastPostDate(new Date());
		wikiNode.setStatusDate(new Date());

		return wikiNode;
	}

	public WikiPage newWikiPage(WikiNode wikiNode, int index) {
		WikiPage wikiPage = new WikiPageImpl();

		wikiPage.setUuid(SequentialUUID.generate());
		wikiPage.setPageId(_counter.get());
		wikiPage.setResourcePrimKey(_counter.get());
		wikiPage.setGroupId(wikiNode.getGroupId());
		wikiPage.setCompanyId(_companyId);
		wikiPage.setUserId(_sampleUserId);
		wikiPage.setUserName(_sampleUser.getFullName());
		wikiPage.setCreateDate(new Date());
		wikiPage.setModifiedDate(new Date());
		wikiPage.setNodeId(wikiNode.getNodeId());
		wikiPage.setTitle("Test Page " + index);
		wikiPage.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPage.setContent("This is test page " + index + ".");
		wikiPage.setFormat(WikiPageConstants.DEFAULT_FORMAT);
		wikiPage.setHead(true);

		return wikiPage;
	}

	public WikiPageResource newWikiPageResource(WikiPage wikiPage) {
		WikiPageResource wikiPageResource = new WikiPageResourceImpl();

		wikiPageResource.setUuid(SequentialUUID.generate());
		wikiPageResource.setResourcePrimKey(wikiPage.getResourcePrimKey());
		wikiPageResource.setNodeId(wikiPage.getNodeId());
		wikiPageResource.setTitle(wikiPage.getTitle());

		return wikiPageResource;
	}

	public String[] nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	protected AssetEntry newAssetEntry(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean visible,
		String mimeType, String title) {

		AssetEntry assetEntry = new AssetEntryImpl();

		assetEntry.setEntryId(_counter.get());
		assetEntry.setGroupId(groupId);
		assetEntry.setCompanyId(_companyId);
		assetEntry.setUserId(_sampleUserId);
		assetEntry.setUserName(_sampleUser.getFullName());
		assetEntry.setCreateDate(createDate);
		assetEntry.setModifiedDate(modifiedDate);
		assetEntry.setClassNameId(classNameId);
		assetEntry.setClassPK(classPK);
		assetEntry.setClassUuid(uuid);
		assetEntry.setClassTypeId(classTypeId);
		assetEntry.setVisible(visible);
		assetEntry.setStartDate(createDate);
		assetEntry.setEndDate(nextFutureDate());
		assetEntry.setPublishDate(createDate);
		assetEntry.setExpirationDate(nextFutureDate());
		assetEntry.setMimeType(mimeType);
		assetEntry.setTitle(title);

		return assetEntry;
	}

	protected Group newGroup(
			long groupId, long classNameId, long classPK, String name,
			boolean site)
		throws Exception {

		Group group = new GroupImpl();

		group.setGroupId(groupId);
		group.setCompanyId(_companyId);
		group.setCreatorUserId(_sampleUserId);
		group.setClassNameId(classNameId);
		group.setClassPK(classPK);
		group.setTreePath(group.buildTreePath());
		group.setName(name);
		group.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		group.setSite(site);
		group.setActive(true);

		return group;
	}

	protected LayoutSet newLayoutSet(
		long groupId, boolean privateLayout, int pageCount) {

		LayoutSet layoutSet = new LayoutSetImpl();

		layoutSet.setLayoutSetId(_counter.get());
		layoutSet.setGroupId(groupId);
		layoutSet.setCompanyId(_companyId);
		layoutSet.setCreateDate(new Date());
		layoutSet.setModifiedDate(new Date());
		layoutSet.setPrivateLayout(privateLayout);
		layoutSet.setThemeId("classic");
		layoutSet.setColorSchemeId("01");
		layoutSet.setWapThemeId("mobile");
		layoutSet.setWapColorSchemeId("01");
		layoutSet.setPageCount(pageCount);

		return layoutSet;
	}

	protected MBMessage newMBMessage(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String body) {

		MBMessage mbMessage = new MBMessageImpl();

		mbMessage.setUuid(SequentialUUID.generate());
		mbMessage.setMessageId(messageId);
		mbMessage.setGroupId(groupId);
		mbMessage.setCompanyId(_companyId);
		mbMessage.setUserId(_sampleUserId);
		mbMessage.setUserName(_sampleUser.getFullName());
		mbMessage.setCreateDate(new Date());
		mbMessage.setModifiedDate(new Date());
		mbMessage.setClassNameId(classNameId);
		mbMessage.setClassPK(classPK);
		mbMessage.setCategoryId(categoryId);
		mbMessage.setThreadId(threadId);
		mbMessage.setRootMessageId(rootMessageId);
		mbMessage.setParentMessageId(parentMessageId);
		mbMessage.setSubject(subject);
		mbMessage.setBody(body);
		mbMessage.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mbMessage.setStatusDate(new Date());

		return mbMessage;
	}

	protected MBThread newMBThread(
		long threadId, long groupId, long categoryId, long rootMessageId,
		int messageCount) {

		MBThread mbThread = new MBThreadImpl();

		mbThread.setUuid(SequentialUUID.generate());
		mbThread.setThreadId(threadId);
		mbThread.setGroupId(groupId);
		mbThread.setCompanyId(_companyId);
		mbThread.setUserId(_sampleUserId);
		mbThread.setUserName(_sampleUser.getFullName());
		mbThread.setCreateDate(new Date());
		mbThread.setModifiedDate(new Date());
		mbThread.setCategoryId(categoryId);
		mbThread.setRootMessageId(rootMessageId);
		mbThread.setRootMessageUserId(_sampleUserId);
		mbThread.setMessageCount(messageCount);
		mbThread.setLastPostByUserId(_sampleUserId);
		mbThread.setLastPostDate(new Date());
		mbThread.setStatusDate(new Date());

		return mbThread;
	}

	protected Role newRole(String name, int type) {
		Role role = new RoleImpl();

		role.setRoleId(_counter.get());
		role.setCompanyId(_companyId);
		role.setClassNameId(_classNamesMap.get(Role.class.getName()));
		role.setClassPK(role.getRoleId());
		role.setName(name);
		role.setType(type);

		return role;
	}

	protected User newUser(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		User user = new UserImpl();

		user.setUuid(SequentialUUID.generate());
		user.setUserId(userId);
		user.setCompanyId(_companyId);
		user.setCreateDate(new Date());
		user.setModifiedDate(new Date());
		user.setDefaultUser(defaultUser);
		user.setContactId(_counter.get());
		user.setPassword("test");
		user.setPasswordModifiedDate(new Date());
		user.setReminderQueryQuestion("What is your screen name?");
		user.setReminderQueryAnswer(screenName);
		user.setEmailAddress(screenName + "@liferay.com");
		user.setScreenName(screenName);
		user.setLanguageId("en_US");
		user.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setLoginDate(new Date());
		user.setLastLoginDate(new Date());
		user.setLastFailedLoginDate(new Date());
		user.setLockoutDate(new Date());
		user.setAgreedToTermsOfUse(true);
		user.setEmailAddressVerified(true);

		return user;
	}

	protected Date nextFutureDate() {
		return new Date(
			_FUTURE_TIME + (_futureDateCounter.get() * Time.SECOND));
	}

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final String _JOURNAL_ARTICLE_TITLE =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales=" +
			"\"en_US\" default-locale=\"en_US\"><Title language-id=\"en_US\"" +
				">Test Journal Article</Title></root>";

	private Account _account;
	private long _accountId;
	private Role _administratorRole;
	private String _baseDir;
	private List<ClassName> _classNames;
	private Map<String, Long> _classNamesMap = new HashMap<String, Long>();
	private Company _company;
	private long _companyId;
	private SimpleCounter _counter;
	private DLFileEntryType _defaultDLFileEntryType;
	private User _defaultUser;
	private List<String> _firstNames;
	private SimpleCounter _futureDateCounter;
	private List<Group> _groups;
	private Group _guestGroup;
	private Role _guestRole;
	private User _guestUser;
	private String _journalArticleContent;
	private List<String> _lastNames;
	private Map<Long, SimpleCounter> _layoutCounters =
		new HashMap<Long, SimpleCounter>();
	private int _maxBlogsEntryCount;
	private int _maxDLFileEntrySize;
	private int _maxGroupsCount;
	private int _maxMBCategoryCount;
	private int _maxMBMessageCount;
	private int _maxMBThreadCount;
	private int _maxUserToGroupCount;
	private Role _ownerRole;
	private Role _powerUserRole;
	private SimpleCounter _resourcePermissionCounter;
	private List<Role> _roles;
	private User _sampleUser;
	private long _sampleUserId;
	private Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleCounter _socialActivityCounter;
	private Role _userRole;
	private SimpleCounter _userScreenNameCounter;
	private VirtualHost _virtualHost;

}