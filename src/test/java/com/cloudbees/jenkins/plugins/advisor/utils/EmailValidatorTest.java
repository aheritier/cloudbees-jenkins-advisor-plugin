package com.cloudbees.jenkins.plugins.advisor.utils;

import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest {

  @Test
  public void validateEmail() {
    FormValidation formValidation;

    formValidation = EmailValidator.validateEmail(null);
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    formValidation = EmailValidator.validateEmail("");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    formValidation = EmailValidator.validateEmail(";");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    formValidation = EmailValidator.validateEmail(",");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    String email = "test@cloudbees.com";
    formValidation = EmailValidator.validateEmail(email + ";");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    formValidation = EmailValidator.validateEmail(email + ",");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);

    formValidation = EmailValidator.validateEmail("<Mr Foo> foo@acme.com");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("Foo<foo@acme.com>");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("Foo <foo@acme.com>");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("\"foo\"@acme.com");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("\"foo bar\"@acme.com");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("\"foo\\ \"@acme.com");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
    formValidation = EmailValidator.validateEmail("\"foo\\ /\"@acme.com");
    assertEquals(FormValidation.Kind.ERROR, formValidation.kind);


    formValidation = EmailValidator.validateEmail(StringUtils.capitalize(email));
    assertEquals(FormValidation.Kind.OK, formValidation.kind);
    formValidation = EmailValidator.validateEmail(email);
    assertEquals(FormValidation.Kind.OK, formValidation.kind);

    formValidation = EmailValidator.validateEmail("test.foo@cloudbees.com");
    assertEquals(FormValidation.Kind.OK, formValidation.kind);
    formValidation = EmailValidator.validateEmail("test-foo@cloudbees.com");
    assertEquals(FormValidation.Kind.OK, formValidation.kind);
    formValidation = EmailValidator.validateEmail("test_foo@cloudbees.com");
    assertEquals(FormValidation.Kind.OK, formValidation.kind);
    formValidation = EmailValidator.validateEmail("test+foo@cloudbees.com");
    assertEquals(FormValidation.Kind.OK, formValidation.kind);
  }

  @Test
  public void validEmails() {
    assertTrue("simple email", EmailValidator.isValidEmail("jdoe@acme.com"));
    assertTrue("email with tag", EmailValidator.isValidEmail("jdoe+advisor@acme.com"));
    // Not supported
    //assertTrue("email with name", emailValidator.isValid("John Doe <jdoe@acme.com>"));
  }

  @Test
  public void invalidEmails() {
    assertFalse("email is empty", EmailValidator.isValidEmail(""));
    assertFalse("not an email", EmailValidator.isValidEmail("Not valid"));
    assertFalse("email with not root DN", EmailValidator.isValidEmail("jdoe@acme"));
  }

}
