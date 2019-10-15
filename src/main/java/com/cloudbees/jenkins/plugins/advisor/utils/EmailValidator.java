package com.cloudbees.jenkins.plugins.advisor.utils;

import com.cloudbees.jenkins.plugins.advisor.client.AdvisorClient;
import com.cloudbees.jenkins.plugins.advisor.client.model.Recipient;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hudson.util.FormValidation.Kind.OK;

public final class EmailValidator {

  private static final String EMAIL_REGEX = "^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
  private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

  private EmailValidator() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  public static boolean isValidEmail(String value) {
    String emailAddress = EmailUtil.fixEmptyAndTrimAllSpaces(value);
    return StringUtils.isNotBlank(emailAddress) && isValid(emailAddress);
  }
  
  public static FormValidation validateEmail(String value) {
    String emailAddress = EmailUtil.fixEmptyAndTrimAllSpaces(value);

    if (StringUtils.isBlank(emailAddress)) {
      return FormValidation.error("Email cannot be blank");
    }

    if (emailAddress.contains(";") || emailAddress.contains(",")) {
      return FormValidation.error(
        "Email cannot contain illegal character ';' or ','. Consider using the CC fields if multiple recipients are required");
    }

    if (!isValid(emailAddress)) {
      return FormValidation.error("Invalid email");
    }

    return FormValidation.ok();
  }

  public static FormValidation validateTos(boolean value) {
    if (!value) {
      return FormValidation.error("Accepting our Terms and Conditions is mandatory to use this service.");
    }
    return FormValidation.ok();
  }

  public static FormValidation testSendEmail(String value, boolean acceptToS) {
    try {
      if (!EmailValidator.validateTos(acceptToS).kind.equals(OK)) {
        return FormValidation.warning(
          "It is impossible to launch a test without accepting our Terms and Conditions");
      }
      if (!EmailValidator.validateEmail(value).kind.equals(OK)) {
        return FormValidation.warning(
          "It is impossible to launch a test without providing a valid email");
      }
      AdvisorClient advisorClient = new AdvisorClient(new Recipient(value.trim()));
      advisorClient.doTestEmail();
      return FormValidation
        .ok("A request to send a test email from the server was done. Please check your inbox and filters.");
    } catch (Exception e) {
      return FormValidation.error("The test failed: " + e.getMessage());
    }
  }

  static boolean isValid(@NonNull String email) {
    Matcher matcher = PATTERN.matcher(email);
    return matcher.matches();
  }

}
