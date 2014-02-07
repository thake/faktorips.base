/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.GregorianCalendar;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

public class JodaUtilTest {
    @Test
    public void testToLocalDate() {
        assertNull(JodaUtil.toLocalDate(null));
        assertNull(JodaUtil.toLocalDate(""));

        assertEquals(new LocalDate(2010, 4, 10), JodaUtil.toLocalDate("2010-04-10"));
    }

    @Test
    public void testToStringLocalDate() {
        assertEquals("", JodaUtil.toString((LocalDate)null));
        assertEquals("2010-04-10", JodaUtil.toString(new LocalDate(2010, 4, 10)));
    }

    @Test
    public void testToGregorianCalendarLocalDate() {
        assertEquals(null, JodaUtil.toGregorianCalendar((LocalDate)null));
        assertEquals(new GregorianCalendar(2010, 3, 10), JodaUtil.toGregorianCalendar(new LocalDate(2010, 4, 10)));
    }

    @Test
    public void testToLocalTime() {
        assertNull(JodaUtil.toLocalTime(null));
        assertNull(JodaUtil.toLocalTime(""));

        assertEquals(new LocalTime(8, 4, 10), JodaUtil.toLocalTime("08:04:10"));
    }

    @Test
    public void testToStringLocalTime() {
        assertEquals("", JodaUtil.toString((LocalTime)null));
        assertEquals("08:04:10", JodaUtil.toString(new LocalTime(8, 4, 10)));
    }

    @Test
    public void testToLocalDateTime() {
        assertNull(JodaUtil.toLocalDateTime(null));
        assertNull(JodaUtil.toLocalDateTime(""));

        assertEquals(new LocalDateTime(2010, 3, 5, 8, 4, 10), JodaUtil.toLocalDateTime("2010-03-05 08:04:10"));
    }

    @Test
    public void testToStringLocalDateTime() {
        assertEquals("", JodaUtil.toString((LocalDateTime)null));
        assertEquals("2010-03-05 08:04:10", JodaUtil.toString(new LocalDateTime(2010, 3, 5, 8, 4, 10)));
    }

    @Test
    public void testToGregorianCalendarLocalDateTime() {
        assertEquals(null, JodaUtil.toGregorianCalendar((LocalDateTime)null));
        assertEquals(new GregorianCalendar(2010, 2, 5, 8, 4, 10),
                JodaUtil.toGregorianCalendar(new LocalDateTime(2010, 3, 5, 8, 4, 10)));
    }

}