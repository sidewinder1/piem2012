package money.Tracker.unittests;

import money.Tracker.unittests.schedule.EditFunctionTestCase;
import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

public class PFMInstrumentationTestRuner extends InstrumentationTestRunner {

	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
		suite.addTestSuite(EditFunctionTestCase.class);
		
		return suite;
	}
}
