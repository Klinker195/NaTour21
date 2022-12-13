package edu.unina.natour21.viewmodel;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * <pre>
 * +-------+-----------+-----------------------------------------------+
 * | CE Id | Parameter | Value                                         |
 * +-------+-----------+-----------------------------------------------+
 * | CE1   | email     | <i>VALID</i> - Pattern "fitgirl@repacks.com"         |
 * | CE2   | email     | <i>INVALID</i> - More than a @                       |
 * | CE3   | email     | <i>VALID</i> - Prefix with special characters -+_.%  |
 * | CE4   | email     | <i>INVALID</i> - @ followed by a dot                 |
 * | CE5   | email     | <i>INVALID</i> - Domain with two consecutive dots    |
 * | CE6   | email     | <i>INVALID</i> - @ preceded by a dot                 |
 * | CE7   | password  | <i>VALID</i> - Not empty password                    |
 * | CE8   | password  | <i>INVALID</i> - Empty password                      |
 * +-------+-----------+-----------------------------------------------+
 * </pre>
 */
@RunWith(JUnit4.class)
public class ViewModelBaseEmailAndPasswordFieldsValidityTest extends TestCase {

    ViewModelBase viewModel = new ViewModelBase();

    /**
     * Check fields validity having a valid email and password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE1 | CE7
     * </p>
     *
     * <i>Result:</i> Fields will be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE1_CE7() {
        String email = "fitgirl@repacks.com";
        String password = "Notsosafepassword123";

        assertTrue(viewModel.checkEmailAndPasswordValidity(email, password));
    }

    /**
     * Check fields validity having an invalid email and a valid password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE2 | CE7
     * </p>
     *
     * <i>Result:</i> Fields will NOT be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE2_CE7() {
        String email = "skidrow@@play.com";
        String password = "Cryptography101";

        assertFalse(viewModel.checkEmailAndPasswordValidity(email, password));
    }

    /**
     * Check fields validity having a valid email and password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE3 | CE7
     * </p>
     *
     * <i>Result:</i> Fields will be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE3_CE7() {
        String email = "cons%pir.4-c_y@gmail.com";
        String password = "TemporaryPassword.233";

        assertTrue(viewModel.checkEmailAndPasswordValidity(email, password));
    }

    /**
     * Check fields validity having an invalid email and password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE4 | CE8
     * </p>
     *
     * <i>Result:</i> Fields will NOT be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE4_CE8() {
        String email = "re_loaded@.warez.co.uk";
        String password = "";

        assertFalse(viewModel.checkEmailAndPasswordValidity(email, password));
    }

    /**
     * Check fields validity having an invalid email and a valid password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE5 | CE7
     * </p>
     *
     * <i>Result:</i> Fields will NOT be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE5_CE7() {
        String email = "hood.lum@wa..rez";
        String password = "Games4All_123";

        assertFalse(viewModel.checkEmailAndPasswordValidity(email, password));
    }

    /**
     * Check fields validity having an invalid email and password.
     *
     * <p>
     *     <i>Blackbox equivalence classes:</i> CE6 | CE8
     * </p>
     *
     * <i>Result:</i> Fields will NOT be valid.
     */
    @Test
    public void checkEmailAndPasswordValidity_CE6_CE8() {
        String email = "three-DM.@gmail.com";
        String password = "";

        assertFalse(viewModel.checkEmailAndPasswordValidity(email, password));
    }

}