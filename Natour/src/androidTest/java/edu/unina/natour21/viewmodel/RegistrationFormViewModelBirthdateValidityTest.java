package edu.unina.natour21.viewmodel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;

import java.util.Calendar;

@RunWith(JUnit4.class)
public class RegistrationFormViewModelBirthdateValidityTest extends TestCase {

    RegistrationFormViewModel viewModel = new RegistrationFormViewModel();
    Calendar calendar = mock(Calendar.class);

    /**
     * Check birthdate validity having an invalid year.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3
     * </p>
     *
     * <i>Result:</i> Birthdate will NOT be valid.
     */
    @Test
    public void checkBirthdateValidity_Path1a() {
        int year = 1900;
        int month = 4;

        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertFalse(viewModel.checkBirthdateValidity(year, month, calendar));
    }


    /**
     * Check birthdate validity having an invalid month.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3
     * </p>
     *
     * <i>Result:</i> Birthdate will NOT be valid.
     */
    @Test
    public void checkBirthdateValidity_Path1b() {
        int year = 2000;
        int month = 14;

        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertFalse(viewModel.checkBirthdateValidity(year, month, calendar));
    }

    /**
     * Check birthdate validity having an user of less than 14 years old.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3 -> 4
     * </p>
     *
     * <i>Result:</i> Birthdate will NOT be valid.
     */
    @Test
    public void checkBirthdateValidity_Path2() {
        int year = 2020;
        int month = 10;
        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertFalse(viewModel.checkBirthdateValidity(year, month, calendar));
    }

    /**
     * Check birthdate validity having an user of less than 14 years old born 14 years ago.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3 -> 4 -> 5 -> 6
     * </p>
     *
     * <i>Result:</i> Birthdate will NOT be valid.
     */
    @Test
    // 1-2-3-4-5-6
    public void checkBirthdateValidity_Path3() {
        int year = 2008;
        int month = 10;

        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertFalse(viewModel.checkBirthdateValidity(year, month, calendar));
    }

    /**
     * Check birthdate validity having an user of more than 14 years old.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3 -> 4 -> 5 -> 7
     * </p>
     *
     * <i>Result:</i> Birthdate will be valid.
     */
    @Test
    public void checkBirthdateValidity_Path4() {
        int year = 2000;
        int month = 10;

        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertTrue(viewModel.checkBirthdateValidity(year, month, calendar));
    }

    /**
     * Check birthdate validity having an user of exactly 14 years old.
     *
     * <p>
     *     <i>Whitebox path:</i> 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7
     * </p>
     *
     * <i>Result:</i> Birthdate will be valid.
     */
    @Test
    public void checkBirthdateValidity_Path5() {
        int year = 2008;
        int month = 9;

        when(calendar.get(Calendar.YEAR)).thenReturn(2022);
        when(calendar.get(Calendar.MONTH)).thenReturn(8);

        assertTrue(viewModel.checkBirthdateValidity(year, month, calendar));
    }

}