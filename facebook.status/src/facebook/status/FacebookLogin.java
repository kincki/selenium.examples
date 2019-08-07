package facebook.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class FacebookLogin {
	
	private WebDriver browser;
	
	public static String readUserCredentials(String fileName) {
		 
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
	
	public FacebookLogin(String driver, String driverPath) {
		//Setup the driver
		Map<String, Object> prefs = new HashMap<String, Object>();
		
		//Get rid of notification pop-ups on Chrome
		prefs.put("profile.default_content_setting_values.notifications", 2);
		
		ChromeOptions options = new ChromeOptions();
		
		options.setExperimentalOption("prefs", prefs);
		
		System.setProperty(driver, driverPath);
		this.browser = new ChromeDriver(options);
		
		this.browser.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		this.browser.manage().window().maximize();
	}

	/**
	 * @return the browser
	 */
	public WebDriver getBrowser() {
		return browser;
	}

	/**
	 * @param browser the browser to set
	 */
	public void setBrowser(WebDriver browser) {
		this.browser = browser;
	}

	public static void main(String[] args) {
		
		//Get the user credentials from the file
		System.out.print("Enter full path for credentials file: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
		
		String creds = FacebookLogin.readUserCredentials(filePath);
		String[] credsArray = creds.split("\n");
		
		//Get the WebDriver type and driver executable locations
		System.out.print("Enter full path for webdriver config file: ");
        String webdriverPath = scanner.nextLine();
		
		String webDriver = FacebookLogin.readUserCredentials(webdriverPath);
		String[] webDriverArray = webDriver.split("\n");
		
		scanner.close();

		//Instantiate an instance of FacebookLogin
		FacebookLogin fbl = new FacebookLogin(webDriverArray[0], webDriverArray[1]);
		
		//Login to Facebook
 
		fbl.getBrowser().get("https://www.facebook.com");

		WebElement email = fbl.getBrowser().findElement(By.id("email"));
        email.sendKeys(credsArray[0]);

        WebElement passwd = fbl.getBrowser().findElement(By.id("pass"));
        passwd.sendKeys(credsArray[1]);

        WebElement login = fbl.getBrowser().findElement(By.xpath("//input[@value='Log In' and @type='submit']"));
        login.click();
        
        //Find the status box and post a message
        WebElement status = fbl.getBrowser().findElement(By.xpath("//*[@name='xhpc_message']"));
        status.click();
        
        status.sendKeys("Hello World!");
        
        WebElement postButton = fbl.getBrowser().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
        postButton.click();
                
        fbl.getBrowser().close();
	}
}

