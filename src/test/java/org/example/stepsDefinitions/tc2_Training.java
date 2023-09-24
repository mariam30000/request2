package org.example.stepsDefinitions;

import dev.failsafe.internal.util.Assert;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class tc2_Training {
    public static List<WebDriver> webDriverList = new ArrayList<WebDriver>();

    public static  String url = "https://192.168.54.200:2010/";
    public  String orgEmail;
    public WebDriver webDriver;

    public WebDriverWait webDriverWait;
    public NetworkLogger networkLogger;
     String requestId;

    public WebDriver tc1(String orgEmail) throws InterruptedException {
        this.orgEmail = orgEmail;
        Initialize();
        tc2_Training.webDriverList.add(webDriver);
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

        Thread.sleep(3000);
    }
    @When("^user request training(.*),(.*)$")
    public String RequestTraining(String NameT,String ImpactT) throws InterruptedException {
        tc1("kirolous.nashaat@flairstech.com").navigate().to("https://192.168.54.200:2010/my-requests");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Requests"));
        Thread.sleep(10000);
        WebElement TRAINING=webDriver.findElement(By.xpath("/html/body/ssa-root/app-authorized-layout/app-side-nav/mat-sidenav-container/mat-sidenav-content/section/ssa-my-requests/app-inner-page-layout/section/ssa-manage-create-requests/div/mat-tab-group/div/mat-tab-body/div/ssa-general-requests-wrapper/div/div[2]/mat-icon"));
        TRAINING.click();
        WebElement Name = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-training-request-form/app-form-modal/div/mat-dialog-content/form/section/mat-form-field[1]/div/div[1]/div/input"));
        Name.sendKeys(NameT);
        WebElement Impact = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-training-request-form/app-form-modal/div/mat-dialog-content/form/section/mat-form-field[6]/div/div[1]/div/textarea"));
        Impact.sendKeys(ImpactT);
        WebElement Submit = webDriver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container/ssa-training-request-form/app-form-modal/div/mat-dialog-actions/div/button[2]/span[1]"));
        Submit.click();
        var createRequest = networkLogger.WaitForSpecificRequest((responseReceived -> {
            return responseReceived.getResponse().getUrl().endsWith("CreateNewTrainingRequest") && responseReceived.getResponse().getStatus() != 204;
        }));
        Assert.notNull(createRequest, "create training request");
        Assert.isTrue(createRequest.getResponse().getStatus() == 200, "Not successful");

        var response = networkLogger.getResponseBodyAsJson(createRequest.getRequestId());
        requestId = response.getAsJsonObject().get("result").getAsJsonObject().get("id").getAsString();
        Assert.notNull(requestId, "Request Id");
        return requestId;

    }
    @And("manager approve or reject")
    public void ManagertakeAction() throws InterruptedException {

        tc1("george.zaki@flairstech.com").navigate().to(url+ "my-tasks(side-panel:request-details/" + requestId + ")?type=7");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Tasks"));
        WebElement approve = webDriver.findElement(By.xpath("/html/body/ssa-root/ng-component/ssa-request-details/app-form-modal/div/section/ssa-request-details-summary/section/div[1]/button[1]/span[1]"));
        approve.click();
    }
    @And("director approve or reject")
    public void DirectortakeAction() throws InterruptedException {

        tc1("shady.magdy@flairstech.com").navigate().to(url+ "my-tasks(side-panel:request-details/" + requestId + ")?type=7");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Tasks"));

        WebElement approve = webDriver.findElement(By.xpath("/html/body/ssa-root/ng-component/ssa-request-details/app-form-modal/div/section/ssa-request-details-summary/section/div[1]/button[1]"));
        approve.click();
    }

    @Then("l&d approve or reject")
    public void LDtakeAction() throws InterruptedException {

        tc1("raghda.othman@flairstech.com").navigate().to(url+ "my-tasks(side-panel:request-details/" + requestId + ")?type=7");
        webDriverWait.until(driver -> driver.getTitle().contentEquals("My Tasks"));
        WebElement approve = webDriver.findElement(By.xpath("/html/body/ssa-root/ng-component/ssa-request-details/app-form-modal/div/section/ssa-request-details-summary/section/div[1]/button[1]/span[1]"));
        approve.click();
    }
    public  void close() throws InterruptedException {
        tc1("george.zaki@flairstech.com").quit();
        tc1("kirolous.nashaat@flairstech.com").quit();
        tc1("shady.magdy@flairstech.com").quit();
        tc1("raghda.othman@flairstech.com").quit();

    }
}
