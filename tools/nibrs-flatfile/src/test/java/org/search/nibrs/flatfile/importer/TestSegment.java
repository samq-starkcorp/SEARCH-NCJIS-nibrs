package org.search.nibrs.flatfile.importer;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.flatfile.importer.Segment;
import org.search.nibrs.model.GroupAIncidentReport;
import org.search.nibrs.model.OffenderSegment;
import org.search.nibrs.model.OffenseSegment;
import org.search.nibrs.model.PropertySegment;
import org.search.nibrs.model.VictimSegment;

public class TestSegment
{
	
	@Test
	public void testSegmentTooShort() {
        String segment = "00871";
        Segment s = new Segment();
        List<NIBRSError> errorList = s.setData(1, segment);
        assertEquals(1, errorList.size());
        NIBRSError error = errorList.get(0);
        assertEquals(5, error.getValue());
        assertEquals(1, error.getContext());
        assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, error.getSegmentType());
	}

    @Test
	public void testIncidentSegment()
    {
        String segment = "00871I022003    TN0390500111502      20021115 19N                                      ";
        Segment s = new Segment();
        assertEquals(0, s.setData(1, segment).size());
        assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, s.getSegmentType());
        assertEquals('I', s.getActionType());
        assertEquals("111502", s.getSegmentUniqueIdentifier());
        assertEquals("TN0390500", s.getOri());
        assertEquals(1, s.getLineNumber());
        assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, s.getSegmentLevel());
    }
    
    @Test
    public void testOffenseSegment()
    {
        String segment = "00632I022003    TN0390500111502      240CN  05               88";
        Segment s = new Segment();
        assertEquals(0, s.setData(1, segment).size());
        assertEquals(OffenseSegment.OFFENSE_SEGMENT_TYPE_IDENTIFIER, s.getSegmentType());
        assertEquals('I', s.getActionType());
        assertEquals("111502", s.getSegmentUniqueIdentifier());
        assertEquals("TN0390500", s.getOri());
        assertEquals('2', s.getSegmentLevel());
    }

    @Test
    public void testPropertySegment()
    {
        String segment = "03073I022003    TN0390500111502      703000000001                                                                                                                                                                                   01                                                                             ";
        Segment s = new Segment();
        assertEquals(0, s.setData(1, segment).size());
        assertEquals(PropertySegment.PROPERTY_SEGMENT_TYPE_IDENTIFIER, s.getSegmentType());
        assertEquals('I', s.getActionType());
        assertEquals("111502", s.getSegmentUniqueIdentifier());
        assertEquals("TN0390500", s.getOri());
    }

    @Test
    public void testVictimSegment()
    {
        String segment = "01294I022003    TN0390500111502      001240                           B                                                          ";
        Segment s = new Segment();
        assertEquals(0, s.setData(1, segment).size());
        assertEquals(VictimSegment.VICTIM_SEGMENT_TYPE_IDENTIFIER, s.getSegmentType());
        assertEquals('I', s.getActionType());
        assertEquals("111502", s.getSegmentUniqueIdentifier());
        assertEquals("TN0390500", s.getOri());
    }

    @Test
    public void testOffenderSegment()
    {
        String segment = "00455I022003    TN0390500111502      0100  UU";
        Segment s = new Segment();
        assertEquals(0, s.setData(1, segment).size());
        assertEquals(OffenderSegment.OFFENDER_SEGMENT_TYPE_IDENTIFIER, s.getSegmentType());
        assertEquals('I', s.getActionType());
        assertEquals("111502", s.getSegmentUniqueIdentifier());
        assertEquals("TN0390500", s.getOri());
    }

}
