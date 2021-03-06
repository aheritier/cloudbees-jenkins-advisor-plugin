package com.cloudbees.jenkins.plugins.advisor;

import hudson.Extension;
import hudson.model.AdministrativeMonitor;
import hudson.util.VersionNumber;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * Displays the reminder that the user needs to register.
 */
@Extension
public class Reminder extends AdministrativeMonitor {

  @Override
  public boolean isActivated() {
    AdvisorGlobalConfiguration config = AdvisorGlobalConfiguration.getInstance();
    /*
    no nag when registered
    no nag when disabled
    */
    return !config.isValid() && config.isPluginEnabled() && !config.isNagDisabled();
  }

  @Override
  public String getDisplayName() {
    return Messages.Reminder_DisplayName();
  }

  @Restricted(NoExternalUse.class)
  @RequirePOST
  @SuppressWarnings("unused")
  public HttpResponse doAct(@QueryParameter(fixEmpty = true) String yes, @QueryParameter(fixEmpty = true) String no) {
    AdvisorGlobalConfiguration config = AdvisorGlobalConfiguration.getInstance();
    if (yes != null) {
      return HttpResponses.redirectViaContextPath(config.getUrlName());
    } else if (no != null) {
      // should never return null if we get here
      return HttpResponses.redirectViaContextPath(Jenkins.getInstance().getPluginManager().getSearchUrl() + "/installed");
    } else { //remind later
      return HttpResponses.forwardToPreviousPage();
    }
  }

  /**
   * This method can be removed when the baseline is updated to 2.103
   *
   * @return If this version of the plugin is running on a Jenkins version where JENKINS-43786 is included.
   */
  @Restricted(DoNotUse.class)
  @SuppressWarnings("unused")
  public boolean isTheNewDesignAvailable() {
    VersionNumber version = Jenkins.getVersion();
    return version != null && version.isNewerThan(new VersionNumber("2.103"));
  }
}
