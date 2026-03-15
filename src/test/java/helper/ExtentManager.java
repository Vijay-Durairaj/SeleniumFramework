package helper;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

// This class manages the ExtentReports instance for logging test results.
public class ExtentManager {
    private static ExtentReports extent;

    /**
     * Returns a singleton instance of ExtentReports.
     * If the instance is null, it initializes a new ExtentReports object with a Spark reporter.
     *
     * @return ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }
}
