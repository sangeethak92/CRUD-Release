package works.buddy.test;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.Assert.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class SeleniumTest {

    private WebDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        driver = new RemoteWebDriver(new URL("http://10.62.125.4:4444/wd/hub"), capability);
	//driver = new RemoteWebDriver(new URL("http://35.244.62.220:4444/wd/hub"), capability);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
    }

    @Test
    public void test1() throws Exception {
	driver.get("http://10.62.125.4:8083/curd1/");
    	//driver.get("http://34.93.238.186:8081/curd/");
        assertEquals("Employee Management Screen", driver.getTitle());
    }

	//Error
	@Ignore
    @Test
    public void test2() throws Exception {
	driver.get("http://10.62.125.4:8083/curd1/");
    	//driver.get("http://34.93.238.186:8081/curd/");
        assertEquals("Employee Management Screen", driver.getTitle());
    }

    @After
    public void tearDown() {
    	driver.quit();
    }

}
