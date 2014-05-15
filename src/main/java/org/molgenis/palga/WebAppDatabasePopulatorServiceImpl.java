package org.molgenis.palga;

import org.molgenis.data.DataService;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.dataexplorer.controller.DataExplorerController;
import org.molgenis.framework.db.WebAppDatabasePopulatorService;
import org.molgenis.omx.auth.GroupAuthority;
import org.molgenis.omx.auth.MolgenisGroup;
import org.molgenis.omx.auth.MolgenisUser;
import org.molgenis.omx.auth.UserAuthority;
import org.molgenis.omx.core.RuntimeProperty;
import org.molgenis.palga.controller.HomeController;
import org.molgenis.security.MolgenisSecurityWebAppDatabasePopulatorService;
import org.molgenis.security.account.AccountService;
import org.molgenis.security.core.utils.SecurityUtils;
import org.molgenis.security.runas.RunAsSystem;
import org.molgenis.security.user.UserAccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class WebAppDatabasePopulatorServiceImpl implements WebAppDatabasePopulatorService
{
	static final String KEY_APP_HREF_CSS = "app.href.css";
	static final String KEY_APP_NAME = "app.name";
	static final String KEY_APP_HREF_LOGO = "app.href.logo";

	private final DataService dataService;
	private final MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService;

	@Autowired
	public WebAppDatabasePopulatorServiceImpl(DataService dataService,
			MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService)
	{
		if (dataService == null) throw new IllegalArgumentException("DataService is null");
		this.dataService = dataService;

		if (molgenisSecurityWebAppDatabasePopulatorService == null) throw new IllegalArgumentException(
				"MolgenisSecurityWebAppDatabasePopulator is null");
		this.molgenisSecurityWebAppDatabasePopulatorService = molgenisSecurityWebAppDatabasePopulatorService;

	}

	@Override
	@Transactional
	@RunAsSystem
	public void populateDatabase()
	{
		molgenisSecurityWebAppDatabasePopulatorService.populateDatabase(this.dataService, HomeController.ID);
		MolgenisUser anonymousUser = molgenisSecurityWebAppDatabasePopulatorService.getAnonymousUser();

		UserAuthority anonymousAuthority = new UserAuthority();
		anonymousAuthority.setMolgenisUser(anonymousUser);
		anonymousAuthority.setRole(SecurityUtils.AUTHORITY_ANONYMOUS);
		dataService.add(UserAuthority.ENTITY_NAME, anonymousAuthority);

		MolgenisGroup usersGroup = new MolgenisGroup();
		usersGroup.setName(AccountService.ALL_USER_GROUP);
		dataService.add(MolgenisGroup.ENTITY_NAME, usersGroup);
		usersGroup.setName(AccountService.ALL_USER_GROUP);

		GroupAuthority usersGroupHomeAuthority = new GroupAuthority();
		usersGroupHomeAuthority.setMolgenisGroup(usersGroup);
		usersGroupHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_READ_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(GroupAuthority.ENTITY_NAME, usersGroupHomeAuthority);

		GroupAuthority usersGroupUserAccountAuthority = new GroupAuthority();
		usersGroupUserAccountAuthority.setMolgenisGroup(usersGroup);
		usersGroupUserAccountAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX
				+ UserAccountController.ID.toUpperCase());
		dataService.add(GroupAuthority.ENTITY_NAME, usersGroupUserAccountAuthority);

		UserAuthority anonymousDataExplorerAuthority = new UserAuthority();
		anonymousDataExplorerAuthority.setMolgenisUser(anonymousUser);
		anonymousDataExplorerAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX
				+ DataExplorerController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousDataExplorerAuthority);

		UserAuthority anonymousHomeAuthority = new UserAuthority();
		anonymousHomeAuthority.setMolgenisUser(anonymousUser);
		anonymousHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousHomeAuthority);

		UserAuthority entityPaglasampleAuthority = new UserAuthority();
		entityPaglasampleAuthority.setMolgenisUser(anonymousUser);
		entityPaglasampleAuthority.setRole("ROLE_ENTITY_COUNT_" + PalgaSample.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityPaglasampleAuthority);

		UserAuthority entityMaterialAuthority = new UserAuthority();
		entityMaterialAuthority.setMolgenisUser(anonymousUser);
		entityMaterialAuthority
				.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX + Material.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityMaterialAuthority);

		UserAuthority entityDiagnosisAuthority = new UserAuthority();
		entityDiagnosisAuthority.setMolgenisUser(anonymousUser);
		entityDiagnosisAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ Diagnosis.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityDiagnosisAuthority);

		UserAuthority entityAgegroupAuthority = new UserAuthority();
		entityAgegroupAuthority.setMolgenisUser(anonymousUser);
		entityAgegroupAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ Agegroup.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityAgegroupAuthority);

		UserAuthority entityGenderAuthority = new UserAuthority();
		entityGenderAuthority.setMolgenisUser(anonymousUser);
		entityGenderAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX + Gender.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityGenderAuthority);

		UserAuthority entityRTPAuthority = new UserAuthority();
		entityRTPAuthority.setMolgenisUser(anonymousUser);
		entityRTPAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ RuntimeProperty.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityRTPAuthority);

		Map<String, String> runtimePropertyMap = new HashMap<String, String>();
		runtimePropertyMap.put(KEY_APP_HREF_CSS, "palga.css");
		runtimePropertyMap.put(KEY_APP_HREF_LOGO, "/img/logo_palga.png");
		runtimePropertyMap.put(KEY_APP_NAME, "PALGA");

		// Charts include/exclude charts
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_AGGREGATES, String.valueOf(true));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_CHARTS, String.valueOf(false));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_DATA, String.valueOf(true));

		// Annotators include files/tools
		String molgenisHomeDir = System.getProperty("molgenis.home");

		if (molgenisHomeDir == null)
		{
			throw new IllegalArgumentException("missing required java system property 'molgenis.home'");
		}

		if (!molgenisHomeDir.endsWith("/")) molgenisHomeDir = molgenisHomeDir + '/';

		for (Entry<String, String> entry : runtimePropertyMap.entrySet())
		{
			RuntimeProperty runtimeProperty = new RuntimeProperty();
			String propertyKey = entry.getKey();
			runtimeProperty.setIdentifier(RuntimeProperty.class.getSimpleName() + '_' + propertyKey);
			runtimeProperty.setName(propertyKey);
			runtimeProperty.setValue(entry.getValue());
			dataService.add(RuntimeProperty.ENTITY_NAME, runtimeProperty);
		}
	}

	@Override
	@Transactional
	@RunAsSystem
	public boolean isDatabasePopulated()
	{
		return dataService.count(MolgenisUser.ENTITY_NAME, new QueryImpl()) > 0;
	}
}