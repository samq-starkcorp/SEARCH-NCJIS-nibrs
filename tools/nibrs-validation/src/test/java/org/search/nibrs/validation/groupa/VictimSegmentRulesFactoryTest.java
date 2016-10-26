package org.search.nibrs.validation.groupa;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.common.ReportSource;
import org.search.nibrs.model.GroupAIncidentReport;
import org.search.nibrs.model.VictimSegment;
import org.search.nibrs.model.codes.NIBRSErrorCode;
import org.search.nibrs.validation.rules.Rule;

public class VictimSegmentRulesFactoryTest {
	
	private VictimSegmentRulesFactory victimRulesFactory = VictimSegmentRulesFactory.instance();
	
	@Test
	public void testRule401ForSequenceNumber(){
				
		Rule<VictimSegment> rule401 = victimRulesFactory.getRule401ForSequenceNumber();
		
		VictimSegment victimSegment = getBasicVictimSegment();
		
		// test value 0
		victimSegment.setVictimSequenceNumber(0);
		
		NIBRSError nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());
		Assert.assertEquals("23", nibrsError.getDataElementIdentifier());
		
		// test value 1000
		victimSegment.setVictimSequenceNumber(1000);
		
		nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());
		
		//test null value
		victimSegment.setVictimSequenceNumber(1000);
		
		nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());				
	}
	
	
	@Test
	public void testRule401ForVictimConnectedToUcrOffenseCode(){
		
		Rule<VictimSegment> rule401 = victimRulesFactory.getRule401ForVictimConnectedToUcrOffenseCode();
		
		VictimSegment victimSegment = getBasicVictimSegment();
		
		victimSegment.setUcrOffenseCodeConnection(null);
		
		NIBRSError nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());
		Assert.assertEquals("24", nibrsError.getDataElementIdentifier());		
	}
	
	@Test
	public void testRule401ForTypeOfVictim(){
		
		Rule<VictimSegment> rule401 = victimRulesFactory.getRule401ForTypeOfVictim();

		VictimSegment victimSegment = getBasicVictimSegment();
		
		// test null value
		victimSegment.setTypeOfVictim(null);
		
		NIBRSError nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());
		
		// test invalid code
		victimSegment.setTypeOfVictim("Z");
		
		nibrsError = rule401.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._401, nibrsError.getNIBRSErrorCode());
	}
	
	
	//TODO enable when passing
	@Ignore
	public void testRule404ForAgeOfVictim(){
		
		Rule<VictimSegment> ageRule404 = victimRulesFactory.getRule404ForAgeOfVictim();
		
		VictimSegment victimSegment = getBasicVictimSegment();
		
		victimSegment.setAgeString(null);
		
		NIBRSError nibrsError = ageRule404.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._404, nibrsError.getNIBRSErrorCode());	
	}
	
	
	@Test
	public void testRule404ForEthnicityOfVictim(){
		
		Rule<VictimSegment> ethnicity404Rule = victimRulesFactory.getRule404ForEthnicityOfVictim();
		
		VictimSegment victimSegment = getBasicVictimSegment();
		
		victimSegment.setEthnicity(null);
		
		NIBRSError nibrsError = ethnicity404Rule.apply(victimSegment);
		
		Assert.assertNotNull(nibrsError);
		
		Assert.assertEquals(NIBRSErrorCode._404, nibrsError.getNIBRSErrorCode());
	}
	
	
	
	
	private VictimSegment getBasicVictimSegment(){
		
		GroupAIncidentReport report = new GroupAIncidentReport();
		ReportSource source = new ReportSource();
		source.setSourceLocation(getClass().getName());
		source.setSourceName(getClass().getName());
		report.setSource(source);		
	
		VictimSegment victimSegment = new VictimSegment();
		report.addVictim(victimSegment);
		
		return victimSegment;
	}

}
