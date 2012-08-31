package net.straininfo2.grs.idloader.db;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static net.straininfo2.grs.idloader.db.ProjectInfoLoader.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectInfoLoaderTest {

    @Test
    public void testEmptyFields() {
        assertTrue(containsNoData(""));
        assertTrue(containsNoData("-"));
    }

    @Test
    public void testEmptyDateParsesToNull() {
        assertTrue(parseDate("") == null);
    }

    @Test
    public void testInvalidDateParsesToNull() {
        assertTrue(parseDate("notadata") == null);
    }

    @Test
    public void testEmptyDateTimeParsesToNull() {
        assertTrue(parseDateTime("") == null);
    }

    @Test
    public void testInvalidDateTimeParsesToNull() {
        assertTrue(parseDateTime("notadatetime") == null);
    }

    @Test
    public void testValidDateParse() {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        cal.set(2011, 2, 17, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), parseDate("03/17/2011"));
    }

    @Test
    public void testValidDateTimeParse() {
        GregorianCalendar cal = new GregorianCalendar(Locale.US);
        cal.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        cal.set(2012, 0, 25, 14, 22, 15);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.getTime(), parseDateTime("01/25/2012 14:22:15"));
    }
}
