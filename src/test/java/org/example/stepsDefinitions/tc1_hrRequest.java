package org.example.stepsDefinitions;


import dev.failsafe.internal.util.Assert;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class tc1_hrRequest {
    public static List<WebDriver> webDriverList = new ArrayList<WebDriver>();

    public static  String url = "https://192.168.54.200:2010/";
    public  String orgEmail;
    public WebDriver webDriver;

    public WebDriverWait webDriverWait;
    public NetworkLogger networkLogger;
    String requestId;

    public  WebDriver tc1(String orgEmail) throws InterruptedException {
        this.orgEmail = orgEmail;
        Initialize();
        tc1_hrRequest.webDriverList.add(webDriver);
        return webDriver;
    }




    private void Initialize() throws InterruptedException {


        System.setProperty("webdriver.chrome.driver","C:\\Users\\mariam.ghobrial\\Documents\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");

        webDriver = new ChromeDriver(chromeOptions);

        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10), Duration.ofSeconds(1));

        networkLogger = new NetworkLogger(webDriver);

        webDriver.navigate().to(url);
        //Ignore Certificate
        WebElement details = webDriver.findElement(By.id("details-button"));
        details.click();
        WebElement cont = webDriver.findElement(By.id("proceed-link"));
        cont.click();
        //Login
    WebElement email = webDriver.findElement(By.id("loginEmail"));
    email.sendKeys(orgEmail);
    WebElement password = webDriver.findElement(By.id("loginPassword"));
    password.sendKeys("123");
        WebElement login = webDriver.findElement(By.className("btn-danger"));
        login.click();

        Thread.sleep(9000);
    }


    @Given("^user can request hr (.*),(.*),(.*)$")
    public String openHRLetterRequest(String whomItMayConcern, String arabic, Boolean salaryIncluded) throws InterruptedException {
        tc1("kirolous.nashaat@flairstech.com").navigate().to("https://192.168.54.200:2010/my-requests");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Requests"));
        Thread.sleep(20000);
        webDriver.findElement(By.xpath("/html/body/ssa-root/app-authorized-layout/app-side-nav/mat-sidenav-container/mat-sidenav-content/section/ssa-my-requests/app-inner-page-layout/section/ssa-manage-create-requests/div/mat-tab-group/div/mat-tab-body/div/ssa-general-requests-wrapper/div/div[1]")).click();
        Thread.sleep(1000);
        var languageDropdown = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-hr-request-form/app-form-modal/div/mat-dialog-content/form/section/mat-form-field[3]/div/div[1]/div/mat-select/div/div[1]/span"));
        languageDropdown.click();

            WebElement arabicOption = webDriver.findElement(By.xpath("//span[@class='mat-option-text'][contains(.,'"+arabic+"')]"));
            arabicOption.click();


        var whomItMayConcernField = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-hr-request-form/app-form-modal/div/mat-dialog-content/form/section/mat-form-field[4]/div/div[1]/div/input"));
        whomItMayConcernField.sendKeys(whomItMayConcern);

        if (salaryIncluded) {
            var Salarybutton = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-hr-request-form/app-form-modal/div/mat-dialog-content/form/section/mat-checkbox/label/span[1]"));
            Salarybutton.click();
        }
        else {
            System.out.println("no");
        }
        var submitButton = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-hr-request-form/app-form-modal/div/mat-dialog-actions/div/button[2]/span[1]"));
        submitButton.click();
        Thread.sleep(1000);
        var createRequest = networkLogger.WaitForSpecificRequest((responseReceived -> {
            return responseReceived.getResponse().getUrl().endsWith("CreateNewHRLetterRequest") && responseReceived.getResponse().getStatus() != 204;
        }));
        Assert.notNull(createRequest, "create hr letter response");
        Assert.isTrue(createRequest.getResponse().getStatus() == 200, "Not successful");

        var response = networkLogger.getResponseBodyAsJson(createRequest.getRequestId());
         requestId = response.getAsJsonObject().get("result").getAsJsonObject().get("id").getAsString();
        Assert.notNull(requestId, "Request Id");
        return requestId;
    }
    @Then("^hr approve(.*)$")
    public void takeAction(String action) throws InterruptedException {

        tc1("george.zaki@flairstech.com").navigate().to(url+ "my-tasks(side-panel:request-details/" + requestId + ")?type=2");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Tasks"));
        var approve = webDriver.findElement(By.xpath("/html/body/ssa-root/ng-component/ssa-request-details/app-form-modal/div/section/ssa-request-details-summary/section/div[1]/button[1]"));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(approve));
        Thread.sleep(5000);
        approve.click();
        var takeAction = networkLogger.WaitForSpecificRequest((responseReceived -> {
            return responseReceived.getResponse().getUrl().contains("TakeAction") && responseReceived.getResponse().getStatus() != 204;
        }));
        Assert.notNull(takeAction, "take action response");
    }
    @AfterAll
    public  void close() throws InterruptedException {
        webDriver.quit();

    }
}
