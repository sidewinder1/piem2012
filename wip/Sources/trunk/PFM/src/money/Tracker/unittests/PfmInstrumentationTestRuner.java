package money.Tracker.unittests;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

public class PfmInstrumentationTestRuner extends InstrumentationTestRunner {

	@Override
	public TestSuite getAllTests() {		
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
		
		suite.addTestSuite(ScheduleViewTestCase.class);
		suite.addTestSuite(ScheduleEditTestCase.class);
		suite.addTestSuite(EntryEditTestCase.class);
		suite.addTestSuite(EntryViewTestCase.class);		
		suite.addTestSuite(ConverterTestCase.class);
		suite.addTestSuite(DateTimeHelperTestCase.class);
		suite.addTestSuite(EntryDeleteTestCase.class);
		suite.addTestSuite(ScheduleDeleteTestCase.class);
		
		return suite;
	}
	
	@Override
	public ClassLoader getLoader() {
		// TODO Auto-generated method stub
		return PfmInstrumentationTestRuner.class.getClassLoader();
	}
}
