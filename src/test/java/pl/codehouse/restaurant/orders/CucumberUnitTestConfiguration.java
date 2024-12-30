package pl.codehouse.restaurant.orders;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Cucumber Unit Test Suit")
@IncludeEngines("cucumber")
@CucumberContextConfiguration
@SelectClasspathResource("features")
public class CucumberUnitTestConfiguration {
}
