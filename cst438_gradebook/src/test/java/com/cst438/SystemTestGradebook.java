package com.cst438;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 
import java.util.List;


import org.openqa.selenium.By; 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SystemTestGradebook {
	public static final String CHROME_DRIVER_FILE_LOCATION =
            "/Users/benjaminmeier/Downloads/chromedriver-mac-arm64/chromedriver";
	public static final String URL = "http://localhost:3000";
	public static final String ALIAS_NAME = "test";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	WebDriver driver;
	
	@BeforeEach
	public void testSetup() throws Exception {
		// if you are not using Chrome, 
		// the following lines will be different. 
		System.setProperty(
		    "webdriver.chrome.driver", 
		    CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(ops);
		
		
		driver.get(URL);
		// must have a short wait to allow time for the page to download 
		Thread.sleep(SLEEP_DURATION);
	}
	
	@Test
	public void testLoadAssignments() throws Exception {
		List<WebElement> assignments = driver.findElements(By.className("assignment"));
		for (WebElement assignment : assignments) {
			assertEquals("31045", assignment.findElement(By.className("assignmentColumn")).getText());
		}
	}
	
	@Test
	public void createAssignent() throws Exception {
		driver.findElement(By.className("createButton")).click();
		Thread.sleep(SLEEP_DURATION);
		
		driver.findElement(By.name("course_id")).sendKeys("31045");
		driver.findElement(By.name("name")).sendKeys("Homework 1");
		driver.findElement(By.name("due_date")).sendKeys("09052023");
		Thread.sleep(SLEEP_DURATION);
		
		driver.findElement(By.className("genericButton")).click();
		Thread.sleep(SLEEP_DURATION);
		
		driver.findElement(By.className("navLink")).click();
		Thread.sleep(SLEEP_DURATION);
		
		List<WebElement> assignments = driver.findElements(By.className("assignment"));
		assertEquals(assignments.size(), 3);
		assertEquals(assignments.get(2).findElements(By.className("assignmentColumn")).get(1).getText(), "Homework 1");
	}
	
	@Test
	public void deleteAssignment() throws Exception {
		var assignments = driver.findElements(By.className("assignment"));
		assertEquals(assignments.size(), 2);
		
		assignments.get(0).findElement(By.className("deleteButton")).click();
		Thread.sleep(SLEEP_DURATION);
		assignments = driver.findElements(By.className("assignment"));
		assertEquals(assignments.size(), 2);
		
		assignments.get(0).findElement(By.name("force")).click();
		assignments.get(0).findElement(By.className("deleteButton")).click();
		Thread.sleep(SLEEP_DURATION);
		assignments = driver.findElements(By.className("assignment"));
		assertEquals(assignments.size(), 1);
	}
	
	@AfterEach
	public void cleanup() {
		if (driver!=null) {
			driver.close();
			driver.quit();
			driver=null;
		}
	}
}
