package wallethub.review;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;


public class WalletHub_Review {

	private WebDriver browser;
	private String testURL = "https://wallethub.com/profile/test-insurance-company-13732055i";
	private String starHighlight = "#4ae0e1";
	private String reviewText = "krock is in the house. review is in the process. no review is left behind. all reviews are duly noted " +
			" some reviews are constructive. some reviews are destructive.";
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String userReviewURL = "https://wallethub.com/profile/<username>/reviews/";


	/**
	 * @return the testURL
	 */
	public String getTestURL() {
		return this.testURL;
	}

	/**
	 * @param testURL the testURL to set
	 */
	public void setTestURL(String testURL) {
		this.testURL = testURL;
	}

	/**
	 * @return the reviewText
	 */
	public String getReviewText() {
		return reviewText;
	}

	/**
	 * @param reviewText the reviewText to set
	 */
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	/*
	 * Initialize the class with Chrome configuration
	 */
	public WalletHub_Review(String driver, String driverPath) {
		Map<String, Object> prefs = new HashMap<String, Object>();

		//Get rid of notification pop-ups on Chrome
		prefs.put("profile.default_content_setting_values.notifications", 2);

		ChromeOptions options = new ChromeOptions();

		options.setExperimentalOption("prefs", prefs);

		System.setProperty(driver, driverPath);
		this.browser = new ChromeDriver(options);

		this.browser.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		this.browser.manage().window().maximize();

	}

	/*
	 * Retrieve the WebDriver instance
	 */
	public WebDriver getBrowser() {
		return this.browser;
	}

	/*
	 * Login to the WalletHub account
	 * @param email User's email address
	 * @param pass	User's password 
	 */
	public boolean login(String email, String pass) {
		WebElement loginElement = this.browser.findElement(By.xpath("//span[contains(text(),'Login')]"));
		loginElement.click();

		WebElement emailElement = this.browser.findElement(By.name("em"));
		emailElement.sendKeys(email);

		WebElement passElement = this.browser.findElement(By.name("pw"));
		passElement.sendKeys(pass);

		WebElement loginButton = this.browser.findElement(By.xpath("//button/span[contains(text(),'Login')]"));
		loginButton.click();

		return this.confirmLogin();
	}

	private boolean confirmLogin() {
		boolean loginSuccess = false;

		this.browser.findElement(By.linkText("My Wallet"));
		loginSuccess = true;

		return loginSuccess;
	}
	
	public boolean confirmHighlight(String color) {
		boolean rightColor = false;
		
		if (color.contentEquals(this.starHighlight)) {
			rightColor = false;
		}
		
		return rightColor; 
	}
	
	/*
	 * Check to see whether the review has been received
	 */
	public boolean isReviewSuccessful() {
		boolean reviewStatus = false;
		
		try {
			WebElement awesomeElement = this.getBrowser().findElement(By.xpath("//*[contains(text(),'Your review has been posted.')]"));
			reviewStatus = true;
		} catch (NoSuchElementException noe) {
			//status already set to false
		}
		
		return reviewStatus;
	}
	
	/*
	 * Read user credentials from a file
	 */
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
	
	/*
	 * Generate a random string of 200 chars
	 */
	public String randomString() {	  
        int length = 200;
        StringBuilder sb = new StringBuilder(length);
        Random random = new SecureRandom();

        if (length <= 0) {
            throw new IllegalArgumentException("String length must be a positive integer");
        }

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        this.setReviewText(sb.toString());
        return sb.toString();
		 
	}

	/**
	 * @return the userReviewURL
	 */
	public String getUserReviewURL() {
		return userReviewURL;
	}

	/**
	 * @param userReviewURL the userReviewURL to set
	 */
	public void setUserReviewURL(String userReviewURL) {
		this.userReviewURL = userReviewURL;
	}

	public static void main(String[] args) {

		//Get user credentials
		// read user credentials from a file
		//Get the user credentials from the file
		System.out.print("Enter full path for credentials file: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
		
		String creds = WalletHub_Review.readUserCredentials(filePath);
		String[] credsArray = creds.split("\n");
		
		//Get the WebDriver type and driver executable locations
		System.out.print("Enter full path for webdriver config file: ");
        String webdriverPath = scanner.nextLine();
		
		String webDriver = WalletHub_Review.readUserCredentials(webdriverPath);
		String[] webDriverArray = webDriver.split("\n");
		
		scanner.close();
		
		WalletHub_Review myWallet = new WalletHub_Review(webDriverArray[0], webDriverArray[1]);
		//Create object 'action' of an Actions class
		Actions action = new Actions(myWallet.getBrowser()); //to be used for hovering
		WebElement elm; //to be used in the iteration body
		List<WebElement> elms; //child elements
		int numOfPathsBeforeHover = 0;
		int numOfPathsAfterHover = 0;
		
		//1. GoTo test url
		myWallet.getBrowser().get(myWallet.getTestURL());

		if (myWallet.login(credsArray[0], credsArray[1])) {
			System.out.println("Login success");
		} else {
			System.out.println("Login failed");
			myWallet.getBrowser().close();
			System.exit(1);
		}

		//2. Hover over all stars and click the fifth one
		WebElement reviewElement = myWallet.getBrowser().findElement(By.className("rvs-svg"));
		((JavascriptExecutor) myWallet.getBrowser()).executeScript("arguments[0].scrollIntoView();", reviewElement);

		myWallet.getBrowser().findElement(By.xpath("//div[@class[contains(.,'ng-enter-element')]]"));

		List<WebElement> reviewStarz = myWallet.getBrowser().findElements(By.xpath("//div[@class[contains(.,'ng-enter-element')]]//*[@class='rvs-star-svg']"));

		Iterator<WebElement> listIterator = reviewStarz.iterator();
		//Hover over starz and click on the last one
		while(listIterator.hasNext()) {
			elm = listIterator.next();

			elms = elm.findElements(By.tagName("path"));
			numOfPathsBeforeHover = elms.size();
			//Mouseover a star
			action.moveToElement(elm).perform();
			elms = elm.findElements(By.tagName("path"));
			numOfPathsAfterHover = elms.size();
			
			if (numOfPathsAfterHover - numOfPathsBeforeHover != 1) {
				System.out.println("Mouse over Has NOT been successful!!");
			} else {
				if (myWallet.confirmHighlight(elms.get(1).getAttribute("stroke"))) {
					System.out.println("Highlight color does NOT match");
				} 
			}
			
			// this is an unnecessary wait statement for allowing the test user to observe the action
			// must be removed from production code
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!listIterator.hasNext()) {
        		//This is the last star
        		//Click on the start
            	action.moveToElement(elm).click().build().perform();
        	}

		} //while
		
		//3. Select the Health Insurance
		//select the dropdown list
		WebElement listElement = myWallet.getBrowser().findElement(By.xpath("//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/div/ng-dropdown/div/span")); 
		listElement.click();
		
		// this is an unnecessary wait statement for allowing the test user to observe the action
		// must be removed from production code
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<WebElement> listPolicies = myWallet.getBrowser().findElements(By.tagName("li"));
		for (WebElement policy : listPolicies) {
			if (policy.getText().contentEquals("Health Insurance")) {
				policy.click();
				break;
			}
		}

		//4. Write some random text of 200 characters
		WebElement textElement = myWallet.getBrowser().findElement(By.xpath("//*[@id=\"reviews-section\"]/modal-dialog/div/div/write-review/div/div[1]/textarea"));
		textElement.sendKeys(myWallet.randomString());
		
		//5. Press Submit
		WebElement submitElement = myWallet.getBrowser().findElement(By.xpath("//div[contains(text(),'Submit')]"));
		submitElement.click();
		
		//6. make sure the review has been really received
		if (myWallet.isReviewSuccessful()) {
			System.out.println("Your review has been received");
		} else {
			System.out.println("Your review cannot be processed");
			
		}
		
		//7. confirm the review within the user profile
		//get the profile link 
		WebElement elmUser = myWallet.getBrowser().findElement(By.xpath("//div[@class[contains(.,'brgm-user')]]/span"));
		((JavascriptExecutor) myWallet.getBrowser()).executeScript("arguments[0].scrollIntoView();", elmUser);
		
//		WebElement userElement = myWallet.getBrowser().findElement(By.linkText("Profile"));
//		System.out.println(userElement.getAttribute("href"));
//				
//	//*[@id="web-app"]/header/div/nav[1]/div[5]/div/a[1]			
//		//go to profile page
//		WebElement profileElement = myWallet.getBrowser().findElement(By.linkText("Profile"));
//		profileElement.click();
//		
//		//find user name
//		WebElement usernameElement = myWallet.getBrowser().findElement(By.className("username"));
//		String userName = usernameElement.getText();
//		userName = userName.substring(1);
//		
//		myWallet.setUserReviewURL(myWallet.getUserReviewURL().replace("<username>", userName));
//		System.out.println(myWallet.getUserReviewURL());
//		
		//myWallet.getBrowser().close();
	}
}
